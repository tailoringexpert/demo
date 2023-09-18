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
package eu.tailoringexpert.demo.tailoring;

import eu.tailoringexpert.Tenant;
import eu.tailoringexpert.domain.File;
import eu.tailoringexpert.domain.Tailoring;
import eu.tailoringexpert.tailoring.DocumentCreator;
import eu.tailoringexpert.tailoring.DocumentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Demo implementation of (tailoring) @see {@link DocumentService}.<p>
 * Demo implements:
 * <ul>
 *     <li>Requirement PDF Catalog</li>
 *     <li>DRD PDF Document</li>
 *     <li>CM PDF Document</li>
 *     <li>Comparison PDF Document</li>
 *     <li>Requirement Excel List</li>
 *     <li>CM Excel Document</li>
 * </ul>
 *
 * @author Michael Bädorf
 */
@Tenant("demo")
@RequiredArgsConstructor
public class DemoDocumentService implements DocumentService {
    @NonNull
    private DocumentCreator catalogPDFDocumentCreator;

    @NonNull
    private DocumentCreator catalogSpreadsheetDocumentCreator;

    @NonNull
    private DocumentCreator comparisonPDFDocumentCreator;

    @NonNull
    private DocumentCreator drdPDFDocumentCreator;

    @NonNull
    private DocumentCreator cmPDFDocumentCreator;

    @NonNull
    private DocumentCreator cmSpreadsheetDocumentCreator;

    private static final String PARAMETER_PROJEKT = "PROJEKT";

    private static final String PARAMETER_DATUM = "DATUM";

    private static final String PARAMETER_DOKUMENT = "DOKUMENT";

    private static final String PARAMETER_SHOWALL = "SHOW_ALL";

    private static final String PARAMETER_SELECTIONVECTOR = "SELECTIONVECTOR";
    private static final String PARAMETER_SCREENINGSHEET = "SCREENINGSHEET";

    private static final String PARAMETER_DRD_DOCID = "${DRD_DOCID}";

    private static final String PARAMETER_KATALOG_DOCID = "KATALOG_DOCID";

    private static final String PATTERN_DATUM = "dd.MM.yyyy";

    private static final String FORMAT_BASIS_DATEINAME = "%s-AR-SU-DLR-%s-DV-%s";

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<File> createRequirementDocument(Tailoring tailoring, LocalDateTime creationTimestamp) {
        return createTailoringRequirementDocument(tailoring, creationTimestamp, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<File> createComparisonDocument(Tailoring tailoring, LocalDateTime creationTimestamp) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put(PARAMETER_PROJEKT, tailoring.getScreeningSheet().getProject());
        placeholders.put(PARAMETER_DATUM, creationTimestamp.format(DateTimeFormatter.ofPattern(PATTERN_DATUM)));

        String docId = String.format("%s-AR-SU-DLR-%s-DV-Tailoring-Diffs",
            tailoring.getScreeningSheet().getProject(),
            tailoring.getIdentifier());

        File document = comparisonPDFDocumentCreator.createDocument(docId, tailoring, placeholders);
        return ofNullable(document);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<File> createAll(Tailoring tailoring, LocalDateTime creationTimestamp) {
        Collection<File> result = new LinkedList<>();

        createTailoringRequirementDocument(tailoring, creationTimestamp, false).ifPresent(result::add);
        createComparisonDocument(tailoring, creationTimestamp).ifPresent(result::add);
        createDRDPDFDocument(tailoring, creationTimestamp).ifPresent(result::add);
        createCMPDFDocument(tailoring, creationTimestamp).ifPresent(result::add);

        createCMSpreadsheetDocument(tailoring, creationTimestamp).ifPresent(result::add);
        createTailoringRequirementSpreadsheetDocument(tailoring, creationTimestamp).ifPresent(result::add);

        return result;
    }

    /**
     * Create DRD document containing only templates referenced by selected requirements.
     *
     * @param tailoring   tailoring to create DRD document for
     * @param currentTime creation timestamp to use
     * @return created PDF File
     */
    Optional<File> createDRDPDFDocument(Tailoring tailoring, LocalDateTime currentTime) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put(PARAMETER_PROJEKT, tailoring.getScreeningSheet().getProject());
        placeholders.put(PARAMETER_DATUM, currentTime.format(DateTimeFormatter.ofPattern(PATTERN_DATUM)));
        placeholders.put(PARAMETER_DOKUMENT, String.format("%s-AR-SU-DLR-%s-DV",
            tailoring.getScreeningSheet().getProject(),
            tailoring.getIdentifier()));

        String docId = String.format(FORMAT_BASIS_DATEINAME,
            tailoring.getScreeningSheet().getProject(),
            tailoring.getIdentifier(),
            "DRD");

        File document = drdPDFDocumentCreator.createDocument(docId, tailoring, placeholders);
        return ofNullable(document);
    }

    /**
     * Create CM PDF document.
     *
     * @param tailoring   tailoring to create CM document for
     * @param currentTime creation timestamp to use
     * @return created PDF File
     */
    Optional<File> createCMPDFDocument(Tailoring tailoring, LocalDateTime currentTime) {
        String docId = String.format(FORMAT_BASIS_DATEINAME,
            tailoring.getScreeningSheet().getProject(),
            tailoring.getIdentifier(),
            "CM");

        String catalogDocId = String.format(FORMAT_BASIS_DATEINAME,
            tailoring.getScreeningSheet().getProject(),
            tailoring.getIdentifier(),
            "Product Assurance Safety Sustainability Requirements");

        String drdDocId = String.format(FORMAT_BASIS_DATEINAME,
            tailoring.getScreeningSheet().getProject(),
            tailoring.getIdentifier(),
            "DRD");

        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put(PARAMETER_PROJEKT, tailoring.getScreeningSheet().getProject());
        placeholders.put(PARAMETER_DATUM, currentTime.format(DateTimeFormatter.ofPattern(PATTERN_DATUM)));
        placeholders.put(PARAMETER_DOKUMENT, docId);
        placeholders.put(PARAMETER_KATALOG_DOCID, catalogDocId);
        placeholders.put("DRD_DOCID", drdDocId);

        File document = cmPDFDocumentCreator.createDocument(docId, tailoring, placeholders);
        return ofNullable(document);
    }

    /**
     * Create CM Excel document.
     *
     * @param tailoring   tailoring to create CM document for
     * @param currentTime creation timestamp to use
     * @return created Excel File
     */
    Optional<File> createCMSpreadsheetDocument(Tailoring tailoring, LocalDateTime currentTime) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put(PARAMETER_PROJEKT, tailoring.getScreeningSheet().getProject());
        placeholders.put(PARAMETER_DATUM, currentTime.format(DateTimeFormatter.ofPattern(PATTERN_DATUM)));

        String docId = String.format(FORMAT_BASIS_DATEINAME,
            tailoring.getScreeningSheet().getProject(),
            tailoring.getIdentifier(),
            "CM");

        File document = cmSpreadsheetDocumentCreator.createDocument(docId, tailoring, placeholders);
        return ofNullable(document);
    }

    /**
     * Create Excel tailoring catalog document.
     *
     * @param tailoring   tailoring to create tailoring catalog for
     * @param currentTime creation timestamp to use
     * @return created Excel File
     */
    Optional<File> createTailoringRequirementSpreadsheetDocument(Tailoring tailoring, LocalDateTime currentTime) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put(PARAMETER_PROJEKT, tailoring.getScreeningSheet().getProject());
        placeholders.put(PARAMETER_DATUM, currentTime.format(DateTimeFormatter.ofPattern(PATTERN_DATUM)));

        String docId = String.format(FORMAT_BASIS_DATEINAME,
            tailoring.getScreeningSheet().getProject(),
            tailoring.getIdentifier(),
            "CONFIG");

        File document = catalogSpreadsheetDocumentCreator.createDocument(docId, tailoring, placeholders);
        return ofNullable(document);
    }

    /**
     * Create PDF tailoring catalog document.
     *
     * @param tailoring   tailoring to create tailoring catalog for
     * @param currentTime creation timestamp to use
     * @return created PDF File
     */
    Optional<File> createTailoringRequirementDocument(Tailoring tailoring, LocalDateTime currentTime, boolean internal) {
        String docId = String.format(FORMAT_BASIS_DATEINAME,
            tailoring.getScreeningSheet().getProject(),
            tailoring.getIdentifier(),
            "Product Assurance Safety Sustainability Requirements");

        String drdDocId = String.format(FORMAT_BASIS_DATEINAME,
            tailoring.getScreeningSheet().getProject(),
            tailoring.getIdentifier(),
            "DRD");

        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put(PARAMETER_PROJEKT, tailoring.getScreeningSheet().getProject());
        placeholders.put(PARAMETER_DATUM, currentTime.format(DateTimeFormatter.ofPattern(PATTERN_DATUM)));
        placeholders.put(PARAMETER_DRD_DOCID, drdDocId);
        placeholders.put(PARAMETER_SHOWALL, Boolean.toString(internal));

        placeholders.put(PARAMETER_DOKUMENT, String.format("%s-AR-SU-DLR-%s-DV",
            tailoring.getScreeningSheet().getProject(),
            tailoring.getIdentifier())
        );

        List<DocumentSelectionVector> selectionVector = tailoring.getSelectionVector().getLevels()
            .entrySet()
            .stream().map(entry -> DocumentSelectionVector.builder().type(entry.getKey()).level(entry.getValue()).build())
            .toList();
        placeholders.put(PARAMETER_SELECTIONVECTOR, selectionVector);

        File document = catalogPDFDocumentCreator.createDocument(docId, tailoring, placeholders);
        return ofNullable(document);
    }
}
