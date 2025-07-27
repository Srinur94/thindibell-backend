package com.excelr.fooddeliveryapp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.excelr.fooddeliveryapp.enums.PaymentMethod;
import com.excelr.fooddeliveryapp.enums.PaymentStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length=50)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    
    @Column(nullable = false, length = 3) // <--- ENSURE THIS IS String AND HAS A LENGTH
    private String currency;
    
    @Column(length = 255) // <--- THIS IS THE CRITICAL FIELD
    private String clientSecret; 
    
    @Column(unique = true)
    private String transactionId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime refundedAt;
    private LocalDateTime updatedAt;

    private String refundTransactionId;
    private String refundReason;
    private String failureReason;
}
