    package com.excelr.fooddeliveryapp.dto;

    import lombok.*;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Positive;
    import jakarta.validation.Valid;
    import java.util.List;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class OrderRequestDTO {
        @NotNull(message = "User ID is required")
        private Long userId;

        @NotNull(message = "Restaurant ID is required")
        private Long restaurantId;

        @NotNull(message = "Delivery Address ID is required")
        private Long deliveryAddressId;

        @NotBlank(message = "Payment method is required")
        private String paymentMethod; // e.g., "COD", "ONLINE"

        @Positive(message = "Total amount must be positive")
        private Double totalAmount;

        private Boolean noContactDelivery;
        private String couponCode;

        @NotNull(message = "Order items are required")
        @Valid // Validate each item in the list
        private List<OrderItemDTO> orderItems;
    }
    