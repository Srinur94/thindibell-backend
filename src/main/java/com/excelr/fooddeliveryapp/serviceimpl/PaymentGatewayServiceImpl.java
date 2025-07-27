package com.excelr.fooddeliveryapp.serviceimpl;

import com.excelr.fooddeliveryapp.enums.PaymentMethod;
import com.excelr.fooddeliveryapp.exception.PaymentException;
import com.excelr.fooddeliveryapp.service.PaymentGatewayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    @Value("${payment.gateway.stripe.secret-key}")
    private String stripeSecretKey;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String processPayment(BigDecimal amount, PaymentMethod paymentMethod, Map<String, String> paymentDetails) {
        log.info("PGW: Processing payment of amount: {} using method: {}", amount, paymentMethod);
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException("Invalid payment amount.");
        }
        if (paymentMethod == null) {
            throw new PaymentException("Payment method is required.");
        }
        
        try {
            if (paymentMethod == PaymentMethod.STRIPE) {
                if (paymentDetails == null || paymentDetails.isEmpty()) {
                    throw new PaymentException("Stripe payment details are required.");
                }
                return processStripePayment(amount, paymentDetails);
            } else if (paymentMethod == PaymentMethod.CASH_ON_DELIVERY) {
                 log.info("PGW: Cash on Delivery selected. No external gateway call needed.");
                 return "COD_TXN_" + UUID.randomUUID();
            } else {
                log.warn("PGW: Unsupported payment method for direct processing: {}", paymentMethod);
                throw new PaymentException("Payment method " + paymentMethod.name() + " is not supported by the configured gateway.");
            }
        } catch (PaymentException e) {
            log.error("PGW: Payment processing failed", e);
            throw new PaymentException("Payment processing failed: " + e.getMessage(), e);
        }
    }
        
    @Override
    public String refundPayment(String transactionId, BigDecimal amount, String reason) {
        log.info("PGW: Processing refund for transaction: {} with amount: {}", transactionId, amount);
        
        try {
            return processStripeRefund(transactionId, amount, reason);
        } catch (PaymentException e) {
            log.error("PGW: Refund processing failed for transaction: {}", transactionId, e);
            throw new PaymentException("Refund processing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifyPayment(String transactionId) {
        log.info("PGW: Verifying payment for transaction: {}", transactionId);
        
        try {
            return verifyStripePayment(transactionId);
        } catch (PaymentException e) {
            log.error("PGW: Payment verification failed for transaction: {}", transactionId, e);
            return false;
        }
    }

    // --- Stripe Integration Methods ---
    private String processStripePayment(BigDecimal amount, Map<String, String> paymentDetails) {
        log.info("PGW: Initiating Stripe PaymentIntent creation.");
        
        String stripePaymentMethodId = paymentDetails.get("stripePaymentMethodId");
        String currency = paymentDetails.get("currency");

        if (stripePaymentMethodId == null || stripePaymentMethodId.isBlank()) {
            throw new PaymentException("Stripe Payment Method ID is required.");
        }
        if (currency == null || currency.isBlank()) {
            throw new PaymentException("Currency is required for Stripe payment.");
        }
        
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
        
        // Prepare Payment Intent creation parameters
        Map<String, Object> paymentIntentParams = new HashMap<>();
        paymentIntentParams.put("amount", amountInCents);
        paymentIntentParams.put("currency", currency.toLowerCase());
        paymentIntentParams.put("payment_method", stripePaymentMethodId);
        paymentIntentParams.put("confirm", true);
        paymentIntentParams.put("off_session", false);
        paymentIntentParams.put("return_url", "http://localhost:3000/order-success"); // <--- ADDED THIS LINE
        // You might consider making this URL configurable via application.properties

        String stripeResponse = callStripeAPI("/v1/payment_intents", paymentIntentParams, HttpMethod.POST);
        
        try {
            JsonNode root = objectMapper.readTree(stripeResponse);
            String paymentIntentId = root.path("id").asText();
            String status = root.path("status").asText();
            String clientSecret = root.path("client_secret").asText();

            log.info("PGW: Stripe PaymentIntent created/confirmed: ID={}, Status={}, ClientSecret={}", paymentIntentId, status, clientSecret);
            
            if ("requires_action".equals(status) || "requires_confirmation".equals(status)) {
                return paymentIntentId + "|" + clientSecret;
            } else if ("succeeded".equals(status)) {
                return paymentIntentId;
            } else {
                throw new PaymentException("Stripe PaymentIntent failed with status: " + status);
            }
        } catch (Exception e) {
            log.error("PGW: Error parsing Stripe PaymentIntent response: {}", e.getMessage());
            throw new PaymentException("Failed to parse Stripe PaymentIntent response.", e);
        }
    }

    private String processStripeRefund(String transactionId, BigDecimal amount, String reason) {
        log.info("PGW: Processing Stripe refund for PaymentIntent: {}", transactionId);
        
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
        
        Map<String, Object> refundParams = new HashMap<>();
        refundParams.put("payment_intent", transactionId);
        refundParams.put("amount", amountInCents);
        refundParams.put("reason", "requested_by_customer");
        refundParams.put("metadata", Map.of("reason", reason));
        
        String stripeResponse = callStripeAPI("/v1/refunds", refundParams, HttpMethod.POST);
        
        try {
            JsonNode root = objectMapper.readTree(stripeResponse);
            String refundId = root.path("id").asText();
            log.info("PGW: Stripe refund processed successfully with refund ID: {}", refundId);
            return refundId;
        } catch (Exception e) {
            log.error("PGW: Error parsing Stripe refund response: {}", e.getMessage());
            throw new PaymentException("Failed to parse Stripe refund response.", e);
        }
    }

    private boolean verifyStripePayment(String transactionId) {
        log.info("PGW: Verifying Stripe PaymentIntent: {}", transactionId);
        try {
            String stripeResponse = callStripeAPI("/v1/payment_intents/" + transactionId, null, HttpMethod.GET);
            JsonNode root = objectMapper.readTree(stripeResponse);
            String status = root.path("status").asText();
            log.info("PGW: Stripe PaymentIntent {} status: {}", transactionId, status);
            return "succeeded".equals(status);
        } catch (Exception e) {
            log.error("PGW: Stripe payment verification failed for {}: {}", transactionId, e.getMessage());
            return false;
        }
    }

    // --- Utility Method for Stripe API Calls ---
    private String callStripeAPI(String endpoint, Map<String, Object> params, HttpMethod method) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(stripeSecretKey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); 
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        
        String requestBody = "";
        if (params != null) {
            requestBody = params.entrySet().stream()
                .map(entry -> {
                    try {
                        return URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()) + "=" +
                               URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8.toString());
                    } catch (Exception e) {
                        log.error("Error encoding parameter: {}", e.getMessage());
                        throw new PaymentException("Failed to encode request parameter", e);
                    }
                })
                .collect(Collectors.joining("&"));
        }
        
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        
        String url = "https://api.stripe.com" + endpoint;
        log.debug("PGW: Calling Stripe API: {} {}", method, url);
        log.debug("PGW: Stripe Request Body (Form Encoded): {}", requestBody);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
            log.debug("PGW: Stripe API Response Status: {}", response.getStatusCode());
            log.debug("PGW: Stripe API Response Body: {}", response.getBody());
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new PaymentException("Stripe API call failed with status: " + response.getStatusCode() + " Body: " + response.getBody());
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("PGW: Error during Stripe API call to {}: {}", url, e.getMessage());
            throw new PaymentException("Error calling Stripe API: " + e.getMessage(), e);
        }
    }
}