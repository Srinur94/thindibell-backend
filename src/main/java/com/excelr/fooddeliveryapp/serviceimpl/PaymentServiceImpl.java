package com.excelr.fooddeliveryapp.serviceimpl;

import com.excelr.fooddeliveryapp.dto.PaymentRequestDTO;
import com.excelr.fooddeliveryapp.dto.PaymentResponseDTO;
import com.excelr.fooddeliveryapp.dto.PaymentStatusDTO;
import com.excelr.fooddeliveryapp.entity.Order;
import com.excelr.fooddeliveryapp.entity.Payment;
import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.OrderStatus;
import com.excelr.fooddeliveryapp.enums.PaymentMethod;
import com.excelr.fooddeliveryapp.enums.PaymentStatus;
import com.excelr.fooddeliveryapp.exception.PaymentException;
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.OrdersRepository;
import com.excelr.fooddeliveryapp.repository.PaymentRepository;
import com.excelr.fooddeliveryapp.repository.UserRepository;
import com.excelr.fooddeliveryapp.service.PaymentGatewayService;
import com.excelr.fooddeliveryapp.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrdersRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentGatewayService paymentGatewayService;

    @Override
    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO) {
        log.info("PS: Creating payment for order ID: {}", paymentRequestDTO.getOrderId());

        Order order = orderRepository.findById(paymentRequestDTO.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + paymentRequestDTO.getOrderId()));

        User currentUser = getCurrentUser();

        if (!order.getCustomer().getId().equals(currentUser.getId())) {
            log.warn("PS: Unauthorized payment attempt: User {} tried to pay for order {} owned by user {}",
                    currentUser.getId(), order.getId(), order.getCustomer().getId());
            throw new PaymentException("User not authorized to make payment for this order");
        }

        Payment payment = Payment.builder()
                .order(order)
                .user(currentUser)
                .amount(order.getTotalAmount())
                .currency(paymentRequestDTO.getPaymentDetails().getCurrency())
                .paymentMethod(paymentRequestDTO.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("PS: Initial payment record created with ID: {}", savedPayment.getId());

        String transactionId = null;
        String clientSecret = null;
        PaymentStatus finalPaymentStatus = PaymentStatus.PENDING;
        String transactionMessage = "Payment initiated.";

        Map<String, String> gatewayPaymentDetails = new HashMap<>();
        if (paymentRequestDTO.getPaymentMethod() == PaymentMethod.STRIPE) {
            gatewayPaymentDetails.put("stripePaymentMethodId", paymentRequestDTO.getPaymentDetails().getStripePaymentMethodId());
            gatewayPaymentDetails.put("currency", paymentRequestDTO.getPaymentDetails().getCurrency());
        } else if (paymentRequestDTO.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            // No specific details needed for COD in gatewayPaymentDetails map
        }

        try {
            log.info("PS: Calling PaymentGatewayService for payment ID: {}", savedPayment.getId());
            String gatewayResponse = paymentGatewayService.processPayment(
                                        savedPayment.getAmount(),
                                        savedPayment.getPaymentMethod(),
                                        gatewayPaymentDetails);

            if (savedPayment.getPaymentMethod() == PaymentMethod.STRIPE) {
                String[] parts = gatewayResponse.split("\\|");
                transactionId = parts[0];
                if (parts.length > 1) {
                    clientSecret = parts[1];
                }

                if (clientSecret != null && !clientSecret.isEmpty()) {
                    finalPaymentStatus = PaymentStatus.PENDING;
                    transactionMessage = "Payment requires customer action (e.g., 3D Secure).";
                } else {
                    finalPaymentStatus = PaymentStatus.COMPLETED;
                    transactionMessage = "Payment succeeded via Stripe.";
                }
            } else if (savedPayment.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
                transactionId = gatewayResponse;
                finalPaymentStatus = PaymentStatus.COMPLETED;
                transactionMessage = "Cash on Delivery payment confirmed.";
            } else {
                 log.warn("PS: Unhandled payment method response from gateway: {}", savedPayment.getPaymentMethod());
                 finalPaymentStatus = PaymentStatus.FAILED;
                 transactionMessage = "Payment method not fully handled in service layer.";
            }

        } catch (PaymentException e) {
            log.error("PS: Payment Gateway Error for payment ID {}: {}", savedPayment.getId(), e.getMessage());
            finalPaymentStatus = PaymentStatus.FAILED;
            transactionMessage = "Payment gateway error: " + e.getMessage();
            throw e;
        } catch (Exception e) {
            log.error("PS: Unexpected error during payment processing for payment ID {}: {}", savedPayment.getId(), e.getMessage());
            finalPaymentStatus = PaymentStatus.FAILED;
            transactionMessage = "An unexpected error occurred during payment: " + e.getMessage();
            throw new PaymentException("An unexpected error occurred during payment: " + e.getMessage());
        }

        savedPayment.setStatus(finalPaymentStatus);
        savedPayment.setTransactionId(transactionId);
        savedPayment.setClientSecret(clientSecret);
        savedPayment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(savedPayment);
        log.info("PS: Payment ID {} updated to status: {}", savedPayment.getId(), finalPaymentStatus);

        if (finalPaymentStatus == PaymentStatus.COMPLETED) {
            order.setOrderStatus(OrderStatus.PAID);
            orderRepository.save(order);
            log.info("PS: Order ID {} status updated to PAID.", order.getId());
        } else if (finalPaymentStatus == PaymentStatus.FAILED || finalPaymentStatus == PaymentStatus.CANCELLED) {
            order.setOrderStatus(OrderStatus.PAYMENT_FAILED);
            orderRepository.save(order);
            log.info("PS: Order ID {} status updated to PAYMENT_FAILED.", order.getId());
        }

        return mapToResponseDTO(savedPayment);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(Long paymentId, PaymentRequestDTO requestDTO) {
        log.info("PS: Processing payment confirmation for payment ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setUpdatedAt(LocalDateTime.now());
        Payment updatedPayment = paymentRepository.save(payment);
        log.info("PS: Payment ID {} status updated to COMPLETED via processPayment.", paymentId);
        return mapToResponseDTO(updatedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        return mapToResponseDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatusDTO getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        return PaymentStatusDTO.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByOrder(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("No payments found for order ID: " + orderId);
        }
        return payments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        List<Payment> payments = paymentRepository.findByUser(user); 
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("No payments found for user ID: " + userId);
        }
        return payments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponseDTO refundPayment(Long paymentId, String reason) {
        log.info("PS: Initiating refund for payment ID: {} with reason: {}", paymentId, reason);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentException("Only completed payments can be refunded. Current status: " + payment.getStatus());
        }

        try {
            String refundTransactionId = paymentGatewayService.refundPayment(
                payment.getTransactionId(),
                payment.getAmount(),
                reason
            );
            
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setRefundReason(reason);
            payment.setRefundedAt(LocalDateTime.now());
            payment.setRefundTransactionId(refundTransactionId);
            payment.setUpdatedAt(LocalDateTime.now());
            Payment updatedPayment = paymentRepository.save(payment);
            log.info("PS: Payment ID {} successfully refunded. Refund Transaction ID: {}", paymentId, refundTransactionId);
            return mapToResponseDTO(updatedPayment);

        } catch (Exception e) {
            log.error("PS: Failed to process refund for payment ID {}: {}", paymentId, e.getMessage());
            throw new PaymentException("Failed to process refund: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public PaymentResponseDTO cancelPayment(Long paymentId) {
        log.info("PS: Initiating cancellation for payment ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.REFUNDED || payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new PaymentException("Payment cannot be cancelled in its current status: " + payment.getStatus());
        }
        
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setUpdatedAt(LocalDateTime.now());
        Payment updatedPayment = paymentRepository.save(payment);
        log.info("PS: Payment ID {} successfully cancelled.", paymentId);
        return mapToResponseDTO(updatedPayment);
    }

    // Helper method to convert Payment entity to PaymentResponseDTO
    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .userId(payment.getUser().getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .clientSecret(payment.getClientSecret())
                .paymentMethodType(payment.getPaymentMethod().name())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .message("Payment details retrieved successfully.")
                .build();
    }
}