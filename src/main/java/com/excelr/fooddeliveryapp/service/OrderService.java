package com.excelr.fooddeliveryapp.service;

import com.excelr.fooddeliveryapp.dto.OrderRequestDTO;
import com.excelr.fooddeliveryapp.dto.OrderResponseDTO;
import com.excelr.fooddeliveryapp.enums.OrderStatus;
import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO requestDTO);
    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getAllOrders();
    List<OrderResponseDTO> getOrdersByUserId(Long userId); // Changed from getOrdersByCustomerId
    List<OrderResponseDTO> getOrderByRestaurantId(Long restaurantId);
    OrderResponseDTO updateOrderStatus(Long id, OrderStatus newStatus);
    OrderResponseDTO updateOrder(Long id, OrderRequestDTO orderRequestDto);
    void deleteOrder(Long id);
    OrderResponseDTO assignDeliveryPerson(Long orderId, Long deliveryPersonId);
    
    OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO); 
}