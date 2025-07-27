package com.excelr.fooddeliveryapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
public class CartConfig {
    
    // Configuration for cart-related beans if needed
    
    @Bean
    public CartProperties cartProperties() {
        return new CartProperties();
    }
}