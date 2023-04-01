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
package eu.tailoringexpert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.tailoringexpert.catalog.CatalogService;
import eu.tailoringexpert.domain.BaseRequirement;
import eu.tailoringexpert.domain.Catalog;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.InputStream;

import java.nio.file.Paths;
import java.util.function.Supplier;

import static java.nio.file.Files.newInputStream;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class BaseCatalogImport implements Supplier<Catalog<BaseRequirement>> {
    @NonNull
    private ObjectMapper objectMapper;

    @NonNull
    private CatalogService catalogService;

    @Override
    @SneakyThrows
    public Catalog<BaseRequirement> get() {
        try (InputStream is = newInputStream(Paths.get("src/test/resources/basecatalog.json"))) {
            assert nonNull(is);

            Catalog<BaseRequirement> result = objectMapper.readValue(is, new TypeReference<Catalog<BaseRequirement>>() {
            });
            catalogService.doImport(result);
            return result;
        }
    }

}
