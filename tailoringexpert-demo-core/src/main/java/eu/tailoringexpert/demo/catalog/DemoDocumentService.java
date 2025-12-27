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
package eu.tailoringexpert.demo.catalog;

import eu.tailoringexpert.Tenant;
import eu.tailoringexpert.catalog.DocumentCreator;
import eu.tailoringexpert.catalog.DocumentService;
import eu.tailoringexpert.catalog.ToRevisedBaseCatalogFunction;
import eu.tailoringexpert.domain.BaseRequirement;
import eu.tailoringexpert.domain.Catalog;
import eu.tailoringexpert.domain.File;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import static eu.tailoringexpert.catalog.ToRevisedBaseCatalogFunction.REPLACEMENT_ORIGINAL_VERSION;
import static eu.tailoringexpert.catalog.ToRevisedBaseCatalogFunction.REPLACEMENT_REVISED_VERSION;
import static java.util.Optional.ofNullable;

/**
 * Plattform implementation of @see {@link DocumentService} for creating a base catalog document.
 *
 * @author Michael Bädorf
 */
@RequiredArgsConstructor
@Log4j2
@Tenant("demo")
public class DemoDocumentService implements DocumentService {

    @NonNull
    private DocumentCreator catalogPDFCreator;

    @NonNull
    private DocumentCreator catalogExcelCreator;

    @NonNull
    private DocumentCreator drdCreator;

    @NonNull
    private ToRevisedBaseCatalogFunction diffMapper;

    private static final String FORMAT_BASIS_DATEINAME = "DEMO-TAILORINGEXPERT-DEMO-%s-1000_%s.pdf";
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<File> createAll(Catalog<BaseRequirement> catalog, LocalDateTime creationTimestamp) {
        Collection<File> result = new LinkedList<>();

        createCatalog(catalog, creationTimestamp).ifPresent(result::add);
        createDRDDocument(catalog, creationTimestamp).ifPresent(result::add);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<File> createCatalog(Catalog<BaseRequirement> catalog, LocalDateTime creationTimestamp) {
        log.info("STARTED | trying to create pdf of catalog version {}", catalog.getVersion());

        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("DRD_DOCID", "RD-PS-01");

        String docId = String.format("PA,Safety & Sustainability-Katalog_%s", catalog.getVersion());
        File dokument = catalogPDFCreator.createDocument(docId, catalog, placeholders);

        log.info("FINISHED | created catalog document  {}", docId);
        return ofNullable(dokument);
    }

    @Override
    public Optional<File> createCatalog(Catalog<BaseRequirement> base, Catalog<BaseRequirement> compare, LocalDateTime creationTimestamp) {
        log.info("STARTED | trying to create diff catalog of versions {}", base.getVersion(), compare.getVersion());

        Catalog<BaseRequirement> catalog = diffMapper.apply(base, compare, Map.of(
            "/assets/" + REPLACEMENT_ORIGINAL_VERSION, "/assets/" + REPLACEMENT_REVISED_VERSION
        ));

        Optional<File> document = createCatalog(catalog, creationTimestamp);
        Optional<File> result = document.map(doc -> File.builder()
            .name(String.format(FORMAT_BASIS_DATEINAME, "RS", catalog.getVersion()))
            .data(doc.getData())
            .build());

        log.info("FINISHED | created diff catalog document  {}", "RD-PS-01" + ".pdf");
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<File> createCatalogExcel(Catalog<BaseRequirement> catalog, LocalDateTime creationTimestamp) {
        log.info("STARTED | trying to create excel of catalog version {}", catalog.getVersion());

        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("DRD_DOCID", "RD-PS-01");

        String docId = String.format("PA,Safety & Sustainability-Katalog_%s", catalog.getVersion());
        File dokument = catalogExcelCreator.createDocument(docId, catalog, placeholders);

        log.info("FINISHED | created excel catalog document  {}", docId);
        return ofNullable(dokument);

    }

    /**
     * Create DRD document containing only templates referenced by selected requirements.
     *
     * @param catalog     base catalog to get defined DRDs of
     * @param currentTime creation timestamp to use
     * @return created PDF File
     */
    Optional<File> createDRDDocument(Catalog<BaseRequirement> catalog, LocalDateTime currentTime) {
        Map<String, Object> placeholders = new HashMap<>();
        String docId = String.format("PA,Safety & Sustainability-Katalog_%s_%s", catalog.getVersion(), "DRD");

        File document = drdCreator.createDocument(docId, catalog, placeholders);
        return ofNullable(document);
    }
}

