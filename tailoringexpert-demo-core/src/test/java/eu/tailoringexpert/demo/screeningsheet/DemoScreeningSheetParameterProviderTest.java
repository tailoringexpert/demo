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

import eu.tailoringexpert.screeningsheet.ScreeningSheetParameterField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Paths.get;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class DemoScreeningSheetParameterProviderTest {

    private DemoScreeningSheetParameterProvider provider;

    @BeforeEach
    void beforeEach() {
        this.provider = new DemoScreeningSheetParameterProvider();
    }

    @Test
    void parse_InputStreamNull_NullPointerExceptionThrown() {
        // arrange

        // act
        Throwable actual = catchThrowable(() -> provider.parse(null));

        // assert
        assertThat(actual).isInstanceOf(NullPointerException.class);
    }

    @Test
    void parse_ValidScreeningsheet_ProjectAndPhasesReturned() throws Exception {
        // arrange
        byte[] data;
        try (InputStream is = newInputStream(get("src/test/resources/screeningsheet.pdf"))) {
            assert nonNull(is);
            data = is.readAllBytes();
        }

        // act
        Collection<ScreeningSheetParameterField> actual = provider.parse(new ByteArrayInputStream(data));

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual)
            .extracting("label")
            .contains("NORMUNG", "0", "A", "B", "C", "D", "E", "F");
    }
}
