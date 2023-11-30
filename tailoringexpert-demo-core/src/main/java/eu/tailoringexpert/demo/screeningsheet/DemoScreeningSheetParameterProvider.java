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
package eu.tailoringexpert.demo.screeningsheet;

import eu.tailoringexpert.Tenant;
import eu.tailoringexpert.screeningsheet.ScreeningSheetParameterField;
import eu.tailoringexpert.screeningsheet.ScreeningSheetParameterProvider;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static eu.tailoringexpert.demo.screeningsheet.DemoScreeningSheetParameter.ALLGEMEIN;
import static eu.tailoringexpert.demo.screeningsheet.DemoScreeningSheetParameter.PHASE;
import static java.util.stream.Stream.of;

@Log4j2
@Tenant("demo")
public class DemoScreeningSheetParameterProvider implements ScreeningSheetParameterProvider {

    @Override
    public Collection<ScreeningSheetParameterField> parse(InputStream is) {
        Collection<ScreeningSheetParameterField> result = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(is.readAllBytes())) {
            List<PDField> fields = new ArrayList<>();
            fields.addAll(document.getDocumentCatalog().getAcroForm().getFields());

            List<PDField> textfelder = filterByType(fields, PDField.class);
            ALLGEMEIN.getParameter()
                .stream()
                .map(parameter -> mapField(textfelder, parameter))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(result::add);

            List<PDCheckBox> selectedParameters = filterCheckedCheckboxes(fields);
            of(PHASE)
                .map(parameterType -> mapField(selectedParameters, parameterType))
                .forEach(result::addAll);
        } catch (IOException e) {
            log.catching(e);
        }

        return result;
    }


    private static <T extends PDField> List<T> filterByType(List<PDField> fields, Class<T> clz) {
        return fields.stream()
            .filter(clz::isInstance)
            .map(clz::cast)
            .toList();
    }

    private List<PDCheckBox> filterCheckedCheckboxes(List<PDField> fields) {
        return filterByType(fields, PDCheckBox.class)
            .stream()
            .filter(PDCheckBox::isChecked)
            .toList();
    }

    private <T extends PDField> Collection<ScreeningSheetParameterField> mapField(
        List<T> fields,
        DemoScreeningSheetParameter parameter) {
        return fields.stream()
            .filter(field -> parameter.hasMember(field.getPartialName()))
            .map(field -> ScreeningSheetParameterField.builder()
                .category(parameter.getCategory())
                .name(field.getPartialName())
                .label(field.getValueAsString())
                .build())
            .toList();
    }

    private <T extends PDField> Optional<ScreeningSheetParameterField> mapField(
        List<T> fields,
        String name) {
        return fields.stream()
            .filter(field -> name.equals(field.getPartialName()))
            .map(field -> ScreeningSheetParameterField.builder()
                .category(name)
                .name(field.getPartialName())
                .label(field.getValueAsString())
                .build())
            .findFirst();
    }

}
