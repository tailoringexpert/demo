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
import eu.tailoringexpert.tailoring.JPATailoringServiceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class DemoCacheConfiguration {

    @Bean
    CacheManager demoCacheManager() {
        log.info("Creating cache manager demo");
        return new DemoCacheManager(
            BaseCatalogRepository.CACHE_BASECATALOG,
            BaseCatalogRepository.CACHE_BASECATALOGLIST,
            JPAProjectServiceRepository.CACHE_BASECATALOG,
            LogoRepository.CACHE_LOGO,
            DokumentSigneeRepository.CACHE_DOCUMENTSIGNEE,
            JPATailoringServiceRepository.CACHE_PROFILES
        );
    }

}
