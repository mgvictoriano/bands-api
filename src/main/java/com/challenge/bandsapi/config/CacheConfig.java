package com.challenge.bandsapi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@EnableConfigurationProperties(BandsCacheProperties.class)
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(BandsCacheProperties properties) {
        CaffeineCacheManager manager = new CaffeineCacheManager("bandsCatalog");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(properties.ttl())
                .maximumSize(properties.maximumSize()));
        return manager;
    }
}

