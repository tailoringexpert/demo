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

import eu.tailoringexpert.domain.Catalog;
import eu.tailoringexpert.domain.File;
import eu.tailoringexpert.domain.ScreeningSheet;
import eu.tailoringexpert.domain.SelectionVector;
import eu.tailoringexpert.domain.Tailoring;
import eu.tailoringexpert.domain.TailoringRequirement;
import eu.tailoringexpert.tailoring.DocumentCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DemoDocumentServiceTest {

    DocumentCreator catalogPDFDocumentCreatorMock;
    DocumentCreator catalogSpreadsheetCreatorMock;
    DocumentCreator comparisonPDFDocumentCreatorMock;
    DocumentCreator cmPDFDocumentCreatorMock;
    DocumentCreator drdPDFDocumentCreatorMock;
    DocumentCreator cmSpreadsheetDocumentCreatorMock;
    DemoDocumentService serviceSpy;

    @BeforeEach
    void beforeEach() {
        this.catalogPDFDocumentCreatorMock = mock(DocumentCreator.class);
        this.catalogSpreadsheetCreatorMock = mock(DocumentCreator.class);
        this.comparisonPDFDocumentCreatorMock = mock(DocumentCreator.class);
        this.drdPDFDocumentCreatorMock = mock(DocumentCreator.class);
        this.cmPDFDocumentCreatorMock = mock(DocumentCreator.class);
        this.cmSpreadsheetDocumentCreatorMock = mock(DocumentCreator.class);

        this.serviceSpy =
            spy(
                new DemoDocumentService(
                    catalogPDFDocumentCreatorMock,
                    catalogSpreadsheetCreatorMock,
                    comparisonPDFDocumentCreatorMock,
                    drdPDFDocumentCreatorMock,
                    cmPDFDocumentCreatorMock,
                    cmSpreadsheetDocumentCreatorMock)
            );
    }

    @Test
    void createRequirementDocument_InternalVersionScreeningSheetNull_NullPointerExceptionThrown() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .build();

        // act
        Throwable actual = catchThrowable(() -> serviceSpy.createRequirementDocument(tailoring, LocalDateTime.now()));

        // assert
        assertThat(actual).isInstanceOf(NullPointerException.class);
        verify(serviceSpy, times(1)).createTailoringRequirementDocument(any(), any(), eq(true));
    }

    @Test
    void createRequirementDocument_InternalVersion_() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").parameters(emptyList()).build())
            .selectionVector(SelectionVector.builder().build())
            .build();

        // act
        serviceSpy.createRequirementDocument(tailoring, LocalDateTime.now());

        // assert
        verify(serviceSpy, times(1)).createTailoringRequirementDocument(any(), any(), eq(true));
    }

    @Test
    void createComparisonDocument_ScreeningSheetNull_NullPointerExceptionThrown() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .build();

        // act
        Throwable actual = catchThrowable(() -> serviceSpy.createComparisonDocument(tailoring, LocalDateTime.now()));

        // assert
        assertThat(actual).isInstanceOf(NullPointerException.class);
        verify(comparisonPDFDocumentCreatorMock, times(0)).createDocument(any(), any(), any());
    }

    @Test
    void createComparisonDocument_MockCallWithValidValues_FileReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").build())
            .build();
        String docId = "SAMPLE-AR-SU-DLR-1000-DV-Tailoring-Diffs";
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(comparisonPDFDocumentCreatorMock.createDocument(eq(docId), eq(tailoring), placeholderCaptor.capture()))
            .willReturn(File.builder().build());

        // act
        Optional<File> actual = serviceSpy.createComparisonDocument(tailoring, LocalDateTime.now());

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("PROJEKT", "DATUM");
    }

    @Test
    void createComparisonDocument_MockReturnedNull_EmptyReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").build())
            .build();
        String docId = "SAMPLE-AR-SU-DLR-1000-DV-Tailoring-Diffs";
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(comparisonPDFDocumentCreatorMock.createDocument(eq(docId), eq(tailoring), placeholderCaptor.capture()))
            .willReturn(null);

        // act
        Optional<File> actual = serviceSpy.createComparisonDocument(tailoring, LocalDateTime.now());

        // assert
        assertThat(actual).isEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("PROJEKT", "DATUM");
    }

    @Test
    void createDRDPDFDocument_ScreeningSheetNull_NullPointerExceptionThrown() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .build();

        // act
        Throwable actual = catchThrowable(() -> serviceSpy.createDRDPDFDocument(tailoring, LocalDateTime.now()));

        // assert
        assertThat(actual).isInstanceOf(NullPointerException.class);
        verify(cmPDFDocumentCreatorMock, times(0)).createDocument(any(), any(), any());
    }

    @Test
    void createDRDPDFDocument_MockCallWithValidValues_FileReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").build())
            .build();
        String docId = "SAMPLE-AR-SU-DLR-1000-DV-DRD";
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(drdPDFDocumentCreatorMock.createDocument(eq(docId), eq(tailoring), placeholderCaptor.capture()))
            .willReturn(File.builder().build());

        // act
        Optional<File> actual = serviceSpy.createDRDPDFDocument(tailoring, LocalDateTime.now());

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("PROJEKT", "DATUM", "DOKUMENT");
    }

    @Test
    void createDRDPDFDocument_MockCalledReturnedNull_EmptyReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").build())
            .build();
        String docId = "SAMPLE-AR-SU-DLR-1000-DV-DRD";
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(drdPDFDocumentCreatorMock.createDocument(eq(docId), eq(tailoring), placeholderCaptor.capture()))
            .willReturn(null);

        // act
        Optional<File> actual = serviceSpy.createDRDPDFDocument(tailoring, LocalDateTime.now());

        // assert
        assertThat(actual).isEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("PROJEKT", "DATUM", "DOKUMENT");
    }


    @Test
    void createCMPDFDokument_ScreeningSheetNull_NullPointerExceptionThrown() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .build();

        // act
        Throwable actual = catchThrowable(() -> serviceSpy.createCMPDFDocument(tailoring, LocalDateTime.now()));

        // assert
        assertThat(actual).isInstanceOf(NullPointerException.class);
        verify(catalogSpreadsheetCreatorMock, times(0)).createDocument(any(), any(), any());
    }

    @Test
    void createCMDPDFDocument_MockCallWithValidValues_FileReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .catalog(Catalog.<TailoringRequirement>builder().version("8.2.1").build())
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").build())
            .build();
        String docId = "SAMPLE-AR-SU-DLR-1000-DV-CM";
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(cmPDFDocumentCreatorMock.createDocument(eq(docId), eq(tailoring), placeholderCaptor.capture()))
            .willReturn(File.builder().build());

        // act
        Optional<File> actual = serviceSpy.createCMPDFDocument(tailoring, LocalDateTime.now());

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("PROJEKT", "DATUM", "DOKUMENT", "KATALOG_DOCID", "DRD_DOCID");
    }

    @Test
    void createCMPDFDocument_MockCallReturnedNull_EmptyReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .catalog(Catalog.<TailoringRequirement>builder().version("8.2.1").build())
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").build())
            .build();
        String docId = "SAMPLE-AR-SU-DLR-1000-DV-CM";
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(cmPDFDocumentCreatorMock.createDocument(eq(docId), eq(tailoring), placeholderCaptor.capture()))
            .willReturn(null);

        // act
        Optional<File> actual = serviceSpy.createCMPDFDocument(tailoring, LocalDateTime.now());

        // assert
        assertThat(actual).isEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("PROJEKT", "DATUM", "DOKUMENT", "KATALOG_DOCID", "DRD_DOCID");
    }

    @Test
    void createCMSpreadsheetDocument_ScreeningSheetNull_NullPointerExceptionThrown() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .build();

        // act
        Throwable actual = catchThrowable(() -> serviceSpy.createCMSpreadsheetDocument(tailoring, LocalDateTime.now()));

        // assert
        assertThat(actual).isInstanceOf(NullPointerException.class);
    }

    @Test
    void createCMSpreadsheetDocument_ChapterBasedMockCallReturnedNull_EmptyReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").build())
            .catalog(Catalog.<TailoringRequirement>builder().version("8.2.1").build())
            .build();
        String docId = "SAMPLE-AR-SU-DLR-1000-DV-CM";
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(cmSpreadsheetDocumentCreatorMock.createDocument(eq(docId), eq(tailoring), placeholderCaptor.capture()))
            .willReturn(null);

        // act
        Optional<File> actual = serviceSpy.createCMSpreadsheetDocument(tailoring, LocalDateTime.now());

        // assert
        assertThat(actual).isEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("PROJEKT", "DATUM");
    }

    @Test
    void createTailoringRequirementDocument_ScreeningSheetNull_NullPointerExceptionThrown() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .build();

        // act
        Throwable actual = catchThrowable(() -> serviceSpy.createTailoringRequirementDocument(tailoring, LocalDateTime.now(), true));

        // assert
        assertThat(actual).isInstanceOf(NullPointerException.class);
        verify(catalogPDFDocumentCreatorMock, times(0)).createDocument(any(), any(), any());
    }

    @Test
    void createTailoringRequirementDocument_MockCallWithValidValues_FileReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").parameters(emptyList()).build())
            .selectionVector(SelectionVector.builder().level("W", 5).build())
            .build();
        String docId = "SAMPLE-AR-SU-DLR-1000-DV-Product Assurance Safety Sustainability Requirements";
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(catalogPDFDocumentCreatorMock.createDocument(eq(docId), eq(tailoring), placeholderCaptor.capture()))
            .willReturn(File.builder().build());

        // act
        Optional<File> actual = serviceSpy.createTailoringRequirementDocument(tailoring, LocalDateTime.now(), true);

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("PROJEKT", "DATUM", "${DRD_DOCID}", "SHOW_ALL", "DOKUMENT", "SELECTIONVECTOR");
    }

    @Test
    void createTailoringRequirementDocument_MockCallReturnedNull_EmptyReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").parameters(emptyList()).build())
            .selectionVector(SelectionVector.builder().build())
            .build();
        String docId = "SAMPLE-AR-SU-DLR-1000-DV-Product Assurance Safety Sustainability Requirements";
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(catalogPDFDocumentCreatorMock.createDocument(eq(docId), eq(tailoring), placeholderCaptor.capture()))
            .willReturn(null);

        // act
        Optional<File> actual = serviceSpy.createTailoringRequirementDocument(tailoring, LocalDateTime.now(), true);

        // assert
        assertThat(actual).isEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("PROJEKT", "DATUM", "${DRD_DOCID}", "SHOW_ALL", "DOKUMENT", "SELECTIONVECTOR");
    }

    @Test
    void createTailoringRequirementSpreadsheet_ScreeningSheetNull_NullPointerExceptionThrown() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .build();

        // act
        Throwable actual = catchThrowable(() -> serviceSpy.createTailoringRequirementSpreadsheetDocument(tailoring, LocalDateTime.now()));

        // assert
        assertThat(actual).isInstanceOf(NullPointerException.class);
        verify(cmSpreadsheetDocumentCreatorMock, times(0)).createDocument(any(), any(), any());
    }

    @Test
    void createTailoringRequirementSpreadsheet_MockCallWithValidValues_FileReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").build())
            .build();
        String docId = "SAMPLE-AR-SU-DLR-1000-DV-CONFIG";
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(catalogSpreadsheetCreatorMock.createDocument(eq(docId), eq(tailoring), placeholderCaptor.capture()))
            .willReturn(File.builder().build());

        // act
        Optional<File> actual = serviceSpy.createTailoringRequirementSpreadsheetDocument(tailoring, LocalDateTime.now());

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("PROJEKT", "DATUM");
    }

    @Test
    void createTailoringRequirementSpreadsheet_MockCallReturnedNull_EmptyReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").build())
            .build();
        String docId = "SAMPLE-AR-SU-DLR-1000-DV-CONFIG";
        ArgumentCaptor<Map<String, Object>> placeholderCaptor = ArgumentCaptor.forClass(Map.class);

        given(catalogSpreadsheetCreatorMock.createDocument(eq(docId), eq(tailoring), placeholderCaptor.capture()))
            .willReturn(null);

        // act
        Optional<File> actual = serviceSpy.createTailoringRequirementSpreadsheetDocument(tailoring, LocalDateTime.now());

        // assert
        assertThat(actual).isEmpty();
        assertThat(placeholderCaptor.getValue()).containsKeys("PROJEKT", "DATUM");
    }

    @Test
    void createAll_AllReturnedFile_6FilesReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").build())
            .build();
        LocalDateTime now = LocalDateTime.now();

        doReturn(Optional.of(File.builder().build()))
            .when(serviceSpy).createTailoringRequirementDocument(tailoring, now, false);
        doReturn(Optional.of(File.builder().build()))
            .when(serviceSpy).createComparisonDocument(tailoring, now);
        doReturn(Optional.of(File.builder().build()))
            .when(serviceSpy).createDRDPDFDocument(tailoring, now);
        doReturn(Optional.of(File.builder().build()))
            .when(serviceSpy).createCMPDFDocument(tailoring, now);
        doReturn(Optional.of(File.builder().build()))
            .when(serviceSpy).createCMSpreadsheetDocument(tailoring, now);
        doReturn(Optional.of(File.builder().build()))
            .when(serviceSpy).createTailoringRequirementSpreadsheetDocument(tailoring, now);

        // act
        Collection<File> actual = serviceSpy.createAll(tailoring, now);

        // assert

        assertThat(actual).hasSize(6);
    }

    @Test
    void createAll_OnelReturnedEmptyFile_5FilesReturned() {
        // arrange
        Tailoring tailoring = Tailoring.builder()
            .identifier("1000")
            .screeningSheet(ScreeningSheet.builder().project("SAMPLE").build())
            .build();
        LocalDateTime now = LocalDateTime.now();

        doReturn(Optional.of(File.builder().build()))
            .when(serviceSpy).createTailoringRequirementDocument(tailoring, now, false);
        doReturn(Optional.of(File.builder().build()))
            .when(serviceSpy).createComparisonDocument(tailoring, now);
        doReturn(Optional.of(File.builder().build()))
            .when(serviceSpy).createDRDPDFDocument(tailoring, now);
        doReturn(Optional.of(File.builder().build()))
            .when(serviceSpy).createCMPDFDocument(tailoring, now);
        doReturn(Optional.of(File.builder().build()))
            .when(serviceSpy).createCMSpreadsheetDocument(tailoring, now);
        doReturn(Optional.empty())
            .when(serviceSpy).createTailoringRequirementSpreadsheetDocument(tailoring, now);

        // act
        Collection<File> actual = serviceSpy.createAll(tailoring, now);

        // assert
        assertThat(actual).hasSize(5);
    }
}
