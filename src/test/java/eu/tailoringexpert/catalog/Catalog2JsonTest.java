/*-
 * #%L
 * TailoringExpert
 * %%
 * Copyright (C) 2022 Michael Bädorf and others
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
package eu.tailoringexpert.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.tailoringexpert.App;
import eu.tailoringexpert.BaseCatalogImport;
import eu.tailoringexpert.FileSaver;
import eu.tailoringexpert.TenantContext;
import eu.tailoringexpert.domain.BaseRequirement;
import eu.tailoringexpert.domain.Catalog;
import eu.tailoringexpert.project.ProjectServiceRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Log4j2
@SpringJUnitConfig(classes = {App.class})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class Catalog2JsonTest {

    @Autowired
    BaseCatalogImport baseCatalog;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectServiceRepository projectServiceRepository;

    private BiConsumer<String, byte[]> toFile = new FileSaver();

    @BeforeEach
    void setup() throws Exception {
        log.debug("setup started");

        TenantContext.setCurrentTenant("plattform");
        RequestContextHolder.setRequestAttributes(
            new ServletRequestAttributes(new MockHttpServletRequest())
        );
        baseCatalog.get();

        log.debug("setup completed");
    }

    @Test
    void catalog2Json() throws IOException {
        // arrange

        // act
        Catalog<BaseRequirement> actual = projectServiceRepository.getBaseCatalog("8.2.1");

        // assert
        assertThat(actual).isNotNull();
        toFile.accept("basecatalog.json", objectMapper.writeValueAsBytes(actual));
    }

}
