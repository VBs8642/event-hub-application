package com.event_hub.event_hub.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableCaching
@EnableScheduling
public class CachingAndSchedulingConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "events",
                "userEvents",
                "eventDetails",
                "publicCatalog",
                "users",
                "userRegistrations",
                "agendaItems",
                "notificationPreferences",
                "adminDashboard"
        );
    }
}
