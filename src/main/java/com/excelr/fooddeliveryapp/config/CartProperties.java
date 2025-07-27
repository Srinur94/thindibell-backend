package com.excelr.fooddeliveryapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.cart")
public class CartProperties {
    
    private int maxItemsPerCart = 50;
    private int maxQuantityPerItem = 10;
    private boolean allowDifferentRestaurants = false;
    private int cartExpirationHours = 24;
    private boolean enableCartNotifications = true;
   
}