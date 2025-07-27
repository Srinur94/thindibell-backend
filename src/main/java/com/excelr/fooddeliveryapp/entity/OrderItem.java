    package com.excelr.fooddeliveryapp.entity;

    import jakarta.persistence.*;
    import lombok.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Entity
    @Table(name = "order_items")
    public class OrderItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id", nullable = false)
        private Order order;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "item_id", nullable = false) // CRITICAL FIX: Map to 'item_id' column
        private MenuItem menuItem;

        
        private Integer quantity;
        private Double price; // Price at the time of order
    }
    