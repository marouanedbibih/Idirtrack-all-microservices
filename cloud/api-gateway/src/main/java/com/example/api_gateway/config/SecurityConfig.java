package com.example.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.example.api_gateway.security.JwtGlobalFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        @Autowired
        private JwtGlobalFilter jwtFilter;

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
                // CORS config
                http.cors(cors -> cors.disable());
                // Disable CSRF
                http.csrf(ServerHttpSecurity.CsrfSpec::disable);
                // Authorize requests based on the URL and the role of the user
                http.authorizeExchange(exchange -> exchange
                                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .pathMatchers(HttpMethod.POST, "/user-api/auth/login", "/user-api/auth/register")
                                .permitAll() // Public routes
                                .anyExchange().authenticated() // All other routes require authentication
                );

                // Add the JWT filter
                http.addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);

                // http.httpBasic(Customizer.withDefaults());
                return http.build();
        }
}
