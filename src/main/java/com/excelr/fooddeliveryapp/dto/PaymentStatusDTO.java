package com.excelr.fooddeliveryapp.dto;

import com.excelr.fooddeliveryapp.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusDTO {
    
    private Long paymentId;
    private String status;
    private String transactionId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String failureReason;

//    public Long getPaymentId() {
//        return paymentId;
//    }
//
//    public void setPaymentId(Long paymentId) {
//        this.paymentId = paymentId;
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
//    public BigDecimal getAmount() {
//        return amount;
//    }
//
//    public void setAmount(BigDecimal amount) {
//        this.amount = amount;
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
//    public String getFailureReason() {
//        return failureReason;
//    }
//
//    public void setFailureReason(String failureReason) {
//        this.failureReason = failureReason;
//    }
//
//    
}