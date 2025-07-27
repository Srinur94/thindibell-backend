    package com.excelr.fooddeliveryapp.entity;

    import jakarta.persistence.*;
    import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
    import java.util.HashSet;
    import java.util.Set;

import com.excelr.fooddeliveryapp.enums.OrderStatus;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Entity
    @Table(name = "orders")
    public class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User customer;
        
        @Enumerated(EnumType.STRING)
        private OrderStatus orderStatus;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "restaurant_id", nullable = false)
        private Restaurant restaurant;
        
        @ManyToOne
        @JoinColumn(name = "delivery_person_id")
        private User deliveryPerson;


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "delivery_address_id", nullable = false)
        private Address deliveryAddress;

        @Column(name = "order_time", nullable = false) 
        private LocalDateTime orderDate;
        

        @Column(name = "total_price", nullable = false) // CRITICAL FIX: Map to 'total_price' column and ensure it's not null
        private BigDecimal totalAmount; 
        
        private String paymentMethod; // e.g., COD, ONLINE
        private String status; // e.g., PENDING, CONFIRMED, DELIVERED, CANCELLED

        @Column(nullable = true) // Allow null for optional fields
        private Boolean noContactDelivery;

        @Column(nullable = true) // Allow null for optional fields
        private String couponCode;

        @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        private Set<OrderItem> orderItems = new HashSet<>();

        // Helper to add order item
        public void addOrderItem(OrderItem item) {
            orderItems.add(item);
            item.setOrder(this);
        }

        // Helper to remove order item
        public void removeOrderItem(OrderItem item) {
            orderItems.remove(item);
            item.setOrder(null);
        }
    }
    