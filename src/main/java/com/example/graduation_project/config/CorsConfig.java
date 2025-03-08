package com.example.graduation_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Collections;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("http://localhost:5173")); // 允许前端访问
        config.setAllowedMethods(Collections.singletonList("*")); // 允许所有 HTTP 方法（GET, POST, PUT, DELETE）
        config.setAllowedHeaders(Collections.singletonList("*")); // 允许所有请求头
        config.setAllowCredentials(true); // 允许携带 Cookie 和认证信息

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

