package com.excelr.fooddeliveryapp.dto;

import com.excelr.fooddeliveryapp.entity.Payment.PaymentBuilder;
import com.excelr.fooddeliveryapp.enums.PaymentMethod;
import com.excelr.fooddeliveryapp.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentResponseDTO {
    
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String status;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime refundedAt;
    private String refundTransactionId;
    private String refundReason;
    private String failureReason;
    private String currency;
    private String statuss;
    private String clientSecret;
    private String paymentMethodType;
    private LocalDateTime updatedAt;
    private String message;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Long getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(Long orderId) {
//        this.orderId = orderId;
//    }
//
//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }
//
//    public BigDecimal getAmount() {
//        return amount;
//    }
//
//    public void setAmount(BigDecimal amount) {
//        this.amount = amount;
//    }
//
//    public PaymentMethod getPaymentMethod() {
//        return paymentMethod;
//    }
//
//    public void setPaymentMethod(PaymentMethod paymentMethod) {
//        this.paymentMethod = paymentMethod;
//    }
//
//    public PaymentStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(PaymentStatus status) {
//        this.status = status;
//    }
//
//    public String getTransactionId() {
//        return transactionId;
//    }
//
//    public void setTransactionId(String transactionId) {
//        this.transactionId = transactionId;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getCompletedAt() {
//        return completedAt;
//    }
//
//    public void setCompletedAt(LocalDateTime completedAt) {
//        this.completedAt = completedAt;
//    }
//
//    public LocalDateTime getCancelledAt() {
//        return cancelledAt;
//    }
//
//    public void setCancelledAt(LocalDateTime cancelledAt) {
//        this.cancelledAt = cancelledAt;
//    }
//
//    public LocalDateTime getRefundedAt() {
//        return refundedAt;
//    }
//
//    public void setRefundedAt(LocalDateTime refundedAt) {
//        this.refundedAt = refundedAt;
//    }
//
//    public String getRefundTransactionId() {
//        return refundTransactionId;
//    }
//
//    public void setRefundTransactionId(String refundTransactionId) {
//        this.refundTransactionId = refundTransactionId;
//    }
//
//    public String getRefundReason() {
//        return refundReason;
//    }
//
//    public void setRefundReason(String refundReason) {
//        this.refundReason = refundReason;
//    }
//
//    public String getFailureReason() {
//        return failureReason;
//    }
//
//    public void setFailureReason(String failureReason) {
//        this.failureReason = failureReason;
//    }


}