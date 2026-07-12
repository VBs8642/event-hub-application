package com.event_hub.event_hub.config;

import com.event_hub.event_hub.security.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
    private final SessionInterceptor sessionInterceptor;

    public MvcConfiguration(SessionInterceptor sessionInterceptor) {
        this.sessionInterceptor = sessionInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/images/**");
    }
}
