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


import eu.tailoringexpert.tailoring.DocumentCreator;
import eu.tailoringexpert.tailoring.DocumentService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class DemoTailoringConfiguration {

    @Bean
    DocumentService demoDocumentService(
        @NonNull @Qualifier("tailoringCatalogPDFDocumentCreator") DocumentCreator tailoringCatalogPDFDocumentCreator,
        @NonNull @Qualifier("tailoringCatalogSpreadsheetCreator") DocumentCreator tailoringCatalogSpreadsheetCreator,
        @NonNull @Qualifier("comparisionPDFDocumentCreator") DocumentCreator comparisionPDFDocumentCreator,
        @NonNull @Qualifier("drdPDFDocumentCreator") DocumentCreator drdPDFDocumentCreator,
        @NonNull @Qualifier("cmPDFDocumentCreator") DocumentCreator cmPDFDocumentCreator,
        @NonNull @Qualifier("cmSpreadsheetDocumentCreator") DocumentCreator cmSpreadsheetDocumentCreator,
        @NonNull @Qualifier("cmRequirementsSpreadsheetDocumentCreator") DocumentCreator cmRequirementsSpreadsheetDocumentCreator) {

        return new DemoDocumentService(
            tailoringCatalogPDFDocumentCreator,
            tailoringCatalogSpreadsheetCreator,
            comparisionPDFDocumentCreator,
            drdPDFDocumentCreator,
            cmPDFDocumentCreator,
            cmSpreadsheetDocumentCreator,
            cmRequirementsSpreadsheetDocumentCreator
        );
    }
}
