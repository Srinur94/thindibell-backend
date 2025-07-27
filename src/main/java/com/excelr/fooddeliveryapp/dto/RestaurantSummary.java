package com.excelr.fooddeliveryapp.dto;

import com.excelr.fooddeliveryapp.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantSummary {
    private Long id;
    private String name;
    private String address;
    private String phone;

    // ✅ Fix: Add this static method
    public static RestaurantSummary fromEntity(Restaurant restaurant) {
        if (restaurant == null) return null;

        return RestaurantSummary.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .phone(restaurant.getPhoneNumber()) // make sure this matches your entity
                .build();
    }
}
