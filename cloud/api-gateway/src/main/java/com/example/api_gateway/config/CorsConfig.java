package com.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:3001"); // Specify your frontend origin
        corsConfig.addAllowedMethod("*"); // Autoriser toutes les méthodes HTTP
        corsConfig.addAllowedHeader("*"); // Autoriser tous les en-têtes
        corsConfig.setAllowCredentials(true); // Autoriser les credentials
        corsConfig.setMaxAge(3600L); // Définir le temps maximum en secondes que la réponse peut être mise en cache
        corsConfig.setAllowCredentials(true); // Allow credentials (cookies, authorization headers, etc.)


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", corsConfig); // Appliquer la configuration CORS à toutes les routes

        return new CorsWebFilter(source);
    }
}