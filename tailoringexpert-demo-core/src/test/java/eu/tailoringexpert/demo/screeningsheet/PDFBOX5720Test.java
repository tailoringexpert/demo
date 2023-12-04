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
package eu.tailoringexpert.demo.screeningsheet;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Paths.get;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PDFBOX5720Test {

    @Test
    void isChecked_EarlyAutoClose_WrongCheckedState() throws Exception {
        // arrange
        byte[] data;
        try (InputStream is = newInputStream(get("src/test/resources/screeningsheet.pdf"))) {
            assert nonNull(is);
            data = is.readAllBytes();
        }

        List<PDField> fields = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(data)) {
            fields.addAll(document.getDocumentCatalog().getAcroForm().getFields());
        }

        // act
        List<PDCheckBox> actual = filterCheckedCheckboxes(fields);

        // assert
        Optional<PDCheckBox> checkBox = actual.stream()
            .filter(checkbox -> "A".equals(checkbox.getPartialName()))
            .findFirst();

        assertTrue(checkBox.isEmpty());
    }


    @Test
    void isChecked_LateAutoClose_CorrectCheckedState() throws Exception {
        // arrange
        byte[] data;
        try (InputStream is = newInputStream(get("src/test/resources/screeningsheet.pdf"))) {
            assert nonNull(is);
            data = is.readAllBytes();
        }

        List<PDField> fields = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(data)) {
            fields.addAll(document.getDocumentCatalog().getAcroForm().getFields());

            // act
            List<PDCheckBox> actual = filterCheckedCheckboxes(fields);

            // assert
            Optional<PDCheckBox> checkBox = actual.stream()
                .filter(checkbox -> "A".equals(checkbox.getPartialName()))
                .findFirst();

            assertTrue(checkBox.isPresent());
        } catch (Exception e) {
            throw e;
        }


    }

    <T extends PDField> List<T> filterByType(List<PDField> fields, Class<T> clz) {
        return fields.stream()
            .filter(clz::isInstance)
            .map(clz::cast)
            .toList();
    }

    List<PDCheckBox> filterCheckedCheckboxes(List<PDField> fields) {
        return filterByType(fields, PDCheckBox.class)
            .stream()
            .filter(PDCheckBox::isChecked)
            .toList();
    }
}
