package com.excelr.fooddeliveryapp.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables @PreAuthorize for method-level security
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; // Assuming JwtAuthFilter exists
    private final AuthenticationProvider authenticationProvider;

    // CORS configuration for frontend communication

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Allow your frontend origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Custom entry point for unauthorized responses
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized access\", \"message\": \"" + authException.getMessage() + "\"}");
        };
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless API
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
            .authorizeHttpRequests(authorize -> authorize
                // Publicly accessible endpoints (no authentication required)
                .requestMatchers("/api/v1/auth/**").permitAll() // Login, Signup
                .requestMatchers("/api/v1/restaurant/public/**").permitAll() // Public restaurant listings
                .requestMatchers("/api/v1/menu-items/public/**").permitAll() // Public menu item listings

                // Customer accessible endpoints (requires authentication, role CUSTOMER or ADMIN)
                // Use .hasAnyRole() for multiple roles
                .requestMatchers("/api/v1/cart/**").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers("/api/v1/orders/customer/**").hasAnyRole("CUSTOMER", "ADMIN") // Access own orders
                .requestMatchers("/api/v1/orders/{orderId}").hasAnyRole("CUSTOMER", "ADMIN", "RESTAURANT_OWNER", "DELIVERY_PERSON")
                .requestMatchers("/api/v1/payments/customer/**").hasAnyRole("CUSTOMER", "ADMIN") // Access own payments
                .requestMatchers("/api/v1/users/profile").hasAnyRole("CUSTOMER", "ADMIN", "DELIVERY_PERSON") // User profile
                .requestMatchers("/api/v1/addresses/**").hasAnyRole("CUSTOMER", "ADMIN") // Address management

                // General resource access (e.g., fetching all restaurants/menu items for display)
                // If these are fetched after login, they should be accessible by CUSTOMER
                .requestMatchers("/api/v1/restaurant/**").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers("/api/v1/menu-items/**").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers("/api/v1/restaurant/search").hasAnyRole("CUSTOMER", "ADMIN") // For search functionality

                // Delivery Person specific endpoints (requires authentication, role DELIVERY_PERSON or ADMIN)
                .requestMatchers("/api/v1/delivery/**").hasAnyRole("DELIVERY_PERSON", "ADMIN")

                // Admin specific endpoints (requires authentication, role ADMIN)
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/users/**").hasRole("ADMIN") // Admin user management
                .requestMatchers("/api/v1/orders/all").hasRole("ADMIN") // Admin view all orders
                .requestMatchers("/api/v1/payments/all").hasRole("ADMIN") // Admin view all payments
                
                // Any other authenticated requests that don't match specific roles
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
