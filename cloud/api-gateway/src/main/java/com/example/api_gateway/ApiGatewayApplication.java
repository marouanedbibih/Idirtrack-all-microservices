package com.example.api_gateway;

import java.util.function.Predicate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()

				// Handle OPTIONS requests globally
				.route("options_route", r -> r.method("OPTIONS")
						.filters(f -> f.setStatus(HttpStatus.OK))
						.uri("lb://user-service"))

				/**
				 * Route for the login API, this is a public endpoint
				 */
				.route("user_service_login_route", r -> r.path("/user-api/auth/login")
						.uri("lb://user-service"))

				/**
				 * Route for the admin API, only users with the role ADMIN can access this
				 */
				.route("user_service_admin_apis", r -> r
						.path("/user-api/admin/**")
						.and().predicate(hasAuthority("ADMIN"))
						.filters(f -> f.setStatus(HttpStatus.FORBIDDEN))
						.uri("lb://user-service"))
				/**
				 * Route for the manager API, only users with the role ADMIN can access this
				 */
				.route("user_service_manager_apis", r -> r
						.path("/user-api/manager/**")
						.and().predicate(hasAuthority("ADMIN"))
						.filters(f -> f.setStatus(HttpStatus.FORBIDDEN))
						.uri("lb://user-service"))

				.route("vehicle_service_route", r -> r.path("/vehicle-api/**")
						.uri("lb://vehicle-service"))
				.route("stock_service_route", r -> r.path("/stock-api/**")
						.uri("lb://stock-service"))
				.route("user_service_route", r -> r
						.path("/user-api/**")
						.uri("lb://user-service"))
				.route("discovery_service_route", r -> r.path("/euraka/web")
						.uri("http://localhost:8761"))
				.route("discovery_service_static_route", r -> r.path("/euraka/**")
						.uri("http://localhost:8761"))
				.build();

	}

	// private Predicate<ServerWebExchange> hasAuthority(String authority) {
	// return exchange -> {
	// String userAuthority =
	// exchange.getRequest().getHeaders().getFirst("X-Authority");
	// return userAuthority != null && userAuthority.equals(authority);
	// };
	// }
	private Predicate<ServerWebExchange> hasAuthority(String authority) {
		return exchange -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !authentication.isAuthenticated()) {
				return false;
			}
			return authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
		};

	}

}
