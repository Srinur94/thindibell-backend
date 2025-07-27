package com.excelr.fooddeliveryapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryDTO {
    private long totalUsers;
    private long totalOrders;
    private long totalRestaurants;
    private long totalMenuItems;
    private String imageUrl;
}

