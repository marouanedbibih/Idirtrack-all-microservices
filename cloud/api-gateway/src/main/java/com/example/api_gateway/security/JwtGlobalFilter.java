package com.example.api_gateway.security;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.example.api_gateway.jwt.JwtUtils;
import com.example.api_gateway.user.UserDTO;
import com.example.api_gateway.user.UserRole;
import com.example.api_gateway.utils.BasicException;
import com.example.api_gateway.utils.BasicResponse;

import reactor.core.publisher.Mono;

@Component
public class JwtGlobalFilter implements WebFilter {

    @Autowired
    private JwtUtils jwtUtils;
    private static final List<String> PUBLIC_PATHS = List.of(
            "/user-api/auth/login",
            "/user-api/auth/register");

    @Autowired
    private WebClient.Builder webClientBuilder;

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(JwtGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        // Extract the method
        String method = exchange.getRequest().getMethod().name();
        logger.debug("Path: " + path);
        logger.debug("Method: " + method);
        if (method.equals("OPTIONS")) {
            return chain.filter(exchange);

        }

        // Bypass JWT validation for public paths
        if (PUBLIC_PATHS.contains(path)) {
            return chain.filter(exchange);
        }

        // Get the Authorization header from the request
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        try {
            // Check if the header is null or does not start with "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorizedResponse(exchange, "You are not authenticated, please login");
            }
            String token = authHeader.substring(7);
            logger.debug("Token: " + token);

            // Call the user-service API to validate the token
            return webClientBuilder.build()
                    .get()
                    .uri("http://user-service/user-api/token/validate")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(BasicResponse.class)
                    .flatMap(response -> {
                        if (response.getStatus() == HttpStatus.OK) {

                            // Cast the content to a Map to access the role
                            Map<String, Object> content = (Map<String, Object>) response.getContent();
                            String role = (String) content.get("role");
                            UserDTO userDTO = UserDTO.builder()
                                    .username((String) content.get("username"))
                                    .role(UserRole.valueOf(role))
                                    .build();
                            logger.debug("UserDTO: " + userDTO);
                            // Set the security context using ReactiveSecurityContextHolder
                            Authentication authentication = new UsernamePasswordAuthenticationToken(
                                    userDTO.getUsername(), null,
                                    List.of(new SimpleGrantedAuthority(userDTO.getRole().name())));

                            SecurityContext context = SecurityContextHolder.createEmptyContext();
                            context.setAuthentication(authentication);

                            return chain.filter(exchange)
                                    .contextWrite(
                                            ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

                            // exchange.getRequest().mutate()
                            // .header("X-Authority", role)
                            // .build();

                            // return chain.filter(exchange);
                        } else {
                            return unauthorizedResponse(exchange, response.getMessage());
                        }
                    })
                    .onErrorResume(e -> unauthorizedResponse(exchange, "Token validation failed"));
        } catch (Exception e) {
            return unauthorizedResponse(exchange, "Token validation failed");
        }
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

}
