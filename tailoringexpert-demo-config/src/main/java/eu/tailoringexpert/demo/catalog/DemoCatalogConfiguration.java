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

import eu.tailoringexpert.catalog.BaseCatalogExcelDocumentCreator;
import eu.tailoringexpert.catalog.BaseCatalogPDFDocumentCreator;
import eu.tailoringexpert.catalog.BaseDRDPDFDocumentCreator;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class DemoCatalogConfiguration {


    @Bean
    DemoDocumentService demoCatalogDocumentService(
        @NonNull BaseCatalogPDFDocumentCreator baseCatalogPDFDocumentCreator,
        @NonNull BaseDRDPDFDocumentCreator baseDRDPDFDocumentCreator,
        @NonNull BaseCatalogExcelDocumentCreator baseCatalogExcelDocumentCreator) {
        return new DemoDocumentService(
            baseCatalogPDFDocumentCreator,
            baseCatalogExcelDocumentCreator,
            baseDRDPDFDocumentCreator
        );
    }


}
