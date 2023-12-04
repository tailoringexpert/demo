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
package eu.tailoringexpert.demo;

import eu.tailoringexpert.project.JPAProjectServiceRepository;
import eu.tailoringexpert.repository.BaseCatalogRepository;
import eu.tailoringexpert.repository.DokumentSigneeRepository;
import eu.tailoringexpert.repository.LogoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class DemoCacheManagerTest {

    @Test
    void getCacheNames_EmptyConstructor_EmptyListReturned() {
        // arrange
        DemoCacheManager cacheManager = new DemoCacheManager();

        // act
        Collection<String> actual = cacheManager.getCacheNames();

        // assert
        assertThat(actual).isEmpty();
    }

    @Test
    void getCacheNames_ConstructorWithCachenames_AllCachenamesReturned() {
        // arrange
        DemoCacheManager cacheManager = new DemoCacheManager(
            BaseCatalogRepository.CACHE_BASECATALOG,
            BaseCatalogRepository.CACHE_BASECATALOGLIST,
            JPAProjectServiceRepository.CACHE_BASECATALOG,
            LogoRepository.CACHE_LOGO,
            DokumentSigneeRepository.CACHE_DOCUMENTSIGNEE
        );

        // act
        Collection<String> actual = cacheManager.getCacheNames();
        // assert
        assertThat(actual)
            .hasSize(5)
            .containsOnly(
                BaseCatalogRepository.CACHE_BASECATALOG,
                BaseCatalogRepository.CACHE_BASECATALOGLIST,
                JPAProjectServiceRepository.CACHE_BASECATALOG,
                LogoRepository.CACHE_LOGO,
                DokumentSigneeRepository.CACHE_DOCUMENTSIGNEE
            );
    }

    @Test
    void getCache_CacheNotExists_NullReturned() {
        // arrange
        DemoCacheManager cacheManager = new DemoCacheManager(
            BaseCatalogRepository.CACHE_BASECATALOG,
            BaseCatalogRepository.CACHE_BASECATALOGLIST,
            JPAProjectServiceRepository.CACHE_BASECATALOG,
            LogoRepository.CACHE_LOGO,
            DokumentSigneeRepository.CACHE_DOCUMENTSIGNEE
        );

        // act
        Cache actual = cacheManager.getCache("someNotExistingCache");

        // assert
        assertThat(actual).isNull();
    }

    @Test
    void getCache_CacheExists_CacheReturned() {
        // arrange
        DemoCacheManager cacheManager = new DemoCacheManager(
            BaseCatalogRepository.CACHE_BASECATALOG,
            BaseCatalogRepository.CACHE_BASECATALOGLIST,
            JPAProjectServiceRepository.CACHE_BASECATALOG,
            LogoRepository.CACHE_LOGO,
            DokumentSigneeRepository.CACHE_DOCUMENTSIGNEE
        );

        // act
        Cache actual = cacheManager.getCache(BaseCatalogRepository.CACHE_BASECATALOGLIST);


        // assert
        assertThat(actual).isNotNull();
    }

}
