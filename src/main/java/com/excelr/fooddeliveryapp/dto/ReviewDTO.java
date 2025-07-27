package com.excelr.fooddeliveryapp.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
	private Long id;

    private UserDTO user;
    private RestaurantDTO restaurant;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private Integer rating;

    private String comments;
    private LocalDateTime createdAt;

    @NotNull(message = "Restaurant Id is required for review")
    private Long restaurantId;
}
