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
package eu.tailoringexpert.demo.catalog;

import eu.tailoringexpert.catalog.DocumentCreator;
import eu.tailoringexpert.catalog.DocumentServiceRepository;
import eu.tailoringexpert.domain.BaseRequirement;
import eu.tailoringexpert.domain.Catalog;
import eu.tailoringexpert.domain.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DemoDocumentServiceTest {

    DocumentCreator catalogPDFCreatorMock;
    DocumentCreator catalogExcelCreatorMock;
    DocumentCreator drdCreatorMock;
    DocumentServiceRepository serviceRepositoryMock;

    DemoDocumentService serviceSpy;

    @BeforeEach
    void beforeEach() {
        this.catalogPDFCreatorMock = mock(DocumentCreator.class);
        this.catalogExcelCreatorMock = mock(DocumentCreator.class);
        this.drdCreatorMock = mock(DocumentCreator.class);
        this.serviceRepositoryMock = mock(DocumentServiceRepository.class);

        this.serviceSpy = spy(
            new DemoDocumentService(
                catalogPDFCreatorMock,
                catalogExcelCreatorMock,
                drdCreatorMock
            )
        );
    }

    @Test
    void createAll_ValidInput_2DocumentsInZip() {
        // arrage
        Catalog<BaseRequirement> catalog = Catalog.<BaseRequirement>builder().version("8.2.1").build();

        File catalogFile = File.builder().name("catalog.pdf").build();
        given(catalogPDFCreatorMock.createDocument(anyString(), any(Catalog.class), any(Map.class)))
            .willReturn(catalogFile);

        File drdFile = File.builder().name("drd.pdf").build();
        given(drdCreatorMock.createDocument(anyString(), any(Catalog.class), any(Map.class)))
            .willReturn(drdFile);

        // act
        Collection<File> actual = serviceSpy.createAll(catalog, LocalDateTime.now());

        // assert
        assertThat(actual)
            .isNotNull()
            .hasSize(2)
            .containsOnly(catalogFile, drdFile);

        verify(catalogPDFCreatorMock, times(1)).createDocument(anyString(), any(Catalog.class), any(Map.class));
        verify(drdCreatorMock, times(1)).createDocument(anyString(), any(Catalog.class), any(Map.class));
    }

    @Test
    void createCatalog_CatalogNull_NullPointerExceptionThrown() {
        // arrange

        // act
        Throwable actual = catchThrowable(() -> serviceSpy.createCatalog(null, LocalDateTime.now()));

        // assert
        assertThat(actual).isInstanceOf(NullPointerException.class);
        verify(catalogPDFCreatorMock, times(0)).createDocument(any(), any(), any());
    }

    @Test
    void createCatalog_MockCallWithValidValues_FileReturned() {
        // arrange
        String docId = "PA,Safety & Sustainability-Katalog_8.2.1";
        LocalDateTime now = LocalDateTime.now();
        Catalog<BaseRequirement> catalog = Catalog.<BaseRequirement>builder().version("8.2.1").build();
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(catalogPDFCreatorMock.createDocument(eq(docId), eq(catalog), placeholderCaptor.capture()))
            .willReturn(File.builder().build());

        // act
        Optional<File> actual = serviceSpy.createCatalog(catalog, now);

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("DRD_DOCID");
    }


    @Test
    void createCatalog_MockCallReturnedNull_EmptyReturned() {
        // arrange
        String docId = "PA,Safety & Sustainability-Katalog_8.2.1";
        LocalDateTime now = LocalDateTime.now();
        Catalog<BaseRequirement> catalog = Catalog.<BaseRequirement>builder().version("8.2.1").build();
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(catalogPDFCreatorMock.createDocument(eq(docId), eq(catalog), placeholderCaptor.capture()))
            .willReturn(null);

        // act
        Optional<File> actual = serviceSpy.createCatalog(catalog, now);

        // assert
        assertThat(actual).isEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("DRD_DOCID");
    }

    @Test
    void createCatalogExcel_CatalogNull_NullPointerExceptionThrown() {
        // arrange

        // act
        Throwable actual = catchThrowable(() -> serviceSpy.createCatalogExcel(null, LocalDateTime.now()));

        // assert
        assertThat(actual).isInstanceOf(NullPointerException.class);
        verify(catalogExcelCreatorMock, times(0)).createDocument(any(), any(), any());
    }

    @Test
    void createCatalogExcel_MockCallWithValidValues_FileReturned() {
        // arrange
        String docId = "PA,Safety & Sustainability-Katalog_8.2.1";
        LocalDateTime now = LocalDateTime.now();
        Catalog<BaseRequirement> catalog = Catalog.<BaseRequirement>builder().version("8.2.1").build();
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(catalogExcelCreatorMock.createDocument(eq(docId), eq(catalog), placeholderCaptor.capture()))
            .willReturn(File.builder().build());

        // act
        Optional<File> actual = serviceSpy.createCatalogExcel(catalog, now);

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("DRD_DOCID");
    }


    @Test
    void createCatalogExcel_MockCallReturnedNull_EmptyReturned() {
        // arrange
        String docId = "PA,Safety & Sustainability-Katalog_8.2.1";
        LocalDateTime now = LocalDateTime.now();
        Catalog<BaseRequirement> catalog = Catalog.<BaseRequirement>builder().version("8.2.1").build();
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(catalogExcelCreatorMock.createDocument(eq(docId), eq(catalog), placeholderCaptor.capture()))
            .willReturn(null);

        // act
        Optional<File> actual = serviceSpy.createCatalogExcel(catalog, now);

        // assert
        assertThat(actual).isEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("DRD_DOCID");
    }

}
