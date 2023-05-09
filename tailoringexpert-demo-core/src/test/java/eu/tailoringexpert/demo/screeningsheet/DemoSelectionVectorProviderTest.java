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

import eu.tailoringexpert.domain.Parameter;
import eu.tailoringexpert.domain.SelectionVector;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.EEE;
import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.GROUNDSEGMENT;
import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.MAINTAINABILITY;
import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.PLANETARYPROTECTION;
import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.PMMP;
import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.PRODUCTASSURANCE;
import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.QUALITYASSURANCE;
import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.RELIABILITY;
import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.SAFETY;
import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.SOFTWARE;
import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.SPACECYBERSECURITY;
import static eu.tailoringexpert.demo.screeningsheet.DemoSelectionVectorProvider.SPACEDEBRIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@Log4j2
class DemoSelectionVectorProviderTest {

    Function<Collection<Parameter>, SelectionVector> provider;

    @BeforeEach
    void setup() {
        this.provider = new DemoSelectionVectorProvider();
    }

    @Test
    void apply_NonInputNeeded_SelectionVectorReturned() {
        // arrange
        List<Parameter> parameterConfigurations = Collections.emptyList();

        // act
        SelectionVector actual = provider.apply(parameterConfigurations);

        //assert
        assertThat(actual).isNotNull();
        assertThat(actual.getLevels()).containsOnly(
            entry(PRODUCTASSURANCE, 5),
            entry(EEE, 5),
            entry(PMMP, 5),
            entry(RELIABILITY, 5),
            entry(SAFETY, 5),
            entry(SOFTWARE, 5),
            entry(SPACEDEBRIS, 5),
            entry(MAINTAINABILITY, 5),
            entry(SPACECYBERSECURITY, 5),
            entry(PLANETARYPROTECTION, 5),
            entry(GROUNDSEGMENT, 5),
            entry(QUALITYASSURANCE, 5)
        );
    }
}
