/*-
 * #%L
 * TailoringExpert
 * %%
 * Copyright (C) 2022 - 2023 Michael Bädorf and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package eu.tailoringexpert.demo.tailoring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.openhtmltopdf.extend.FSDOMMutator;
import eu.tailoringexpert.domain.Catalog;
import eu.tailoringexpert.domain.Chapter;
import eu.tailoringexpert.domain.DRD;
import eu.tailoringexpert.domain.DocumentSignature;
import eu.tailoringexpert.domain.DocumentSignatureState;
import eu.tailoringexpert.domain.File;
import eu.tailoringexpert.domain.Phase;
import eu.tailoringexpert.domain.Tailoring;
import eu.tailoringexpert.domain.TailoringRequirement;
import eu.tailoringexpert.renderer.PDFEngine;
import eu.tailoringexpert.renderer.RendererRequestConfiguration;
import eu.tailoringexpert.renderer.RendererRequestConfigurationSupplier;
import eu.tailoringexpert.renderer.TailoringexpertDOMMutator;
import eu.tailoringexpert.renderer.ThymeleafTemplateEngine;
import eu.tailoringexpert.tailoring.CMPDFDocumentCreator;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Paths.get;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@Log4j2
class CMPDFDocumentCreatorTest {

    String templateHome;
    ObjectMapper objectMapper;
    FileSaver fileSaver;
    BiFunction<Chapter<TailoringRequirement>, Collection<Phase>, Map<DRD, Set<String>>> drdProviderMock;
    CMPDFDocumentCreator creator;

    @BeforeEach
    void setup() {
        Dotenv env = Dotenv.configure().ignoreIfMissing().load();
        this.templateHome = env.get("TEMPLATE_HOME", "src/test/resources/templates/");

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModules(new ParameterNamesModule(), new JavaTimeModule(), new Jdk8Module());
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        this.fileSaver = new FileSaver("target");

        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();

        FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
        fileTemplateResolver.setCacheable(false);
        fileTemplateResolver.setPrefix(this.templateHome);
        fileTemplateResolver.setSuffix(".html");
        fileTemplateResolver.setCharacterEncoding("UTF-8");
        fileTemplateResolver.setOrder(1);
        springTemplateEngine.addTemplateResolver(fileTemplateResolver);

        RendererRequestConfigurationSupplier supplier = () -> RendererRequestConfiguration.builder()
            .id("demo")
            .name("Plattform")
            .templateHome(get(this.templateHome).toAbsolutePath().toString())
            .build();

        ThymeleafTemplateEngine templateEngine = new ThymeleafTemplateEngine(springTemplateEngine, supplier);
        FSDOMMutator domMutator = new TailoringexpertDOMMutator();

        this.drdProviderMock = mock(BiFunction.class);
        this.creator = new CMPDFDocumentCreator(
            templateEngine,
            new PDFEngine(domMutator, supplier),
            drdProviderMock
        );
    }

    @Test
    void createDocument_InputValid_DocumentCreated() throws IOException {
        // arrange
        Catalog<TailoringRequirement> catalog;
        try (InputStream is = newInputStream(get("src/test/resources/tailoringcatalog.json"))) {
            assert nonNull(is);

            catalog = objectMapper.readValue(is, new TypeReference<Catalog<TailoringRequirement>>() {
            });
        }

        Tailoring tailoring = Tailoring.builder()
            .catalog(catalog)
            .signatures(List.of(
                DocumentSignature.builder()
                    .faculty("Software")
                    .applicable(Boolean.TRUE)
                    .build(),
                DocumentSignature.builder()
                    .state(DocumentSignatureState.AGREED)
                    .applicable(Boolean.TRUE)
                    .build()
            ))
            .build();

        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("PROJEKT", "HRWS");
        placeholders.put("DATUM", now.format(DateTimeFormatter.ofPattern("dd.MM.YYYY")));
        placeholders.put("DOKUMENT", "HRWS-RD-PS-1940/DV7");
        placeholders.put("KATALOG_DOCID", "HRWS-RD-PS-1940/DV7_10000");


        given(drdProviderMock.apply(any(), any()))
            .willReturn(ofEntries(
                    entry(
                        DRD.builder()
                            .title("ESA: Qualification Status List (QSL)")
                            .number("02.02")
                            .build(), Collections.emptySet())
                )
            );


        // act
        File actual = creator.createDocument("4711", tailoring, placeholders);

        // assert
        assertThat(actual).isNotNull();
        fileSaver.accept("cm.pdf", actual.getData());
    }
}
