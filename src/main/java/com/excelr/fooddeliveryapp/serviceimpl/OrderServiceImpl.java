package com.excelr.fooddeliveryapp.serviceimpl;

import com.excelr.fooddeliveryapp.dto.OrderRequestDTO;
import com.excelr.fooddeliveryapp.dto.OrderResponseDTO;
import com.excelr.fooddeliveryapp.dto.OrderItemDTO;
import com.excelr.fooddeliveryapp.entity.Address;
import com.excelr.fooddeliveryapp.entity.CartItem;
import com.excelr.fooddeliveryapp.entity.MenuItem;
import com.excelr.fooddeliveryapp.entity.Order;
import com.excelr.fooddeliveryapp.entity.OrderItem;
import com.excelr.fooddeliveryapp.entity.Restaurant;
import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.OrderStatus;
import com.excelr.fooddeliveryapp.enums.PaymentMethod; // Import PaymentMethod
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.AddressRepository;
import com.excelr.fooddeliveryapp.repository.CartItemRepository;
import com.excelr.fooddeliveryapp.repository.MenuItemRepoisitory;
import com.excelr.fooddeliveryapp.repository.OrdersRepository;
import com.excelr.fooddeliveryapp.repository.RestaurantRepository;
import com.excelr.fooddeliveryapp.repository.UserRepository;
import com.excelr.fooddeliveryapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Added for logging
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections; // Import Collections
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Function; // Ensure this is imported

@Service
@RequiredArgsConstructor
@Slf4j // Enable logging for this service
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepoisitory menuItemRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO) {
        log.info("OrderService: Placing order for user ID: {}", orderRequestDTO.getUserId());

        User user = userRepository.findById(orderRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + orderRequestDTO.getUserId()));
        Address deliveryAddress = addressRepository.findById(orderRequestDTO.getDeliveryAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Address not found with ID: " + orderRequestDTO.getDeliveryAddressId()));
        Restaurant restaurant = restaurantRepository.findById(orderRequestDTO.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + orderRequestDTO.getRestaurantId()));

        Order order = Order.builder()
                .customer(user)
                .restaurant(restaurant)
                .deliveryAddress(deliveryAddress)
                .orderDate(LocalDateTime.now())
                .totalAmount(BigDecimal.valueOf(orderRequestDTO.getTotalAmount()))
                .paymentMethod(orderRequestDTO.getPaymentMethod())
                .status(OrderStatus.PENDING.name()) // Set initial status as PENDING
                .noContactDelivery(orderRequestDTO.getNoContactDelivery())
                .couponCode(orderRequestDTO.getCouponCode())
                .build();

        // CRITICAL FIX: Explicitly cast the lambda for the map operation here
        // Ensure that OrderItemDTO.price is correctly used and converted if needed
        Set<OrderItem> orderItems = orderRequestDTO.getOrderItems().stream()
                .map((Function<OrderItemDTO, OrderItem>) itemDto -> {
                    MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found with ID: " + itemDto.getMenuItemId()));
                    return OrderItem.builder()
                            .order(order) // Link back to the order
                            .menuItem(menuItem)
                            .quantity(itemDto.getQuantity())
                            .price(itemDto.getPrice().doubleValue()) // Assuming OrderItem entity stores price as double
                            .build();
                }).collect(Collectors.toSet());

        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        log.info("OrderService: Order saved with ID: {}", savedOrder.getId());

        // Clear the user's cart after successful order placement
        // Assuming cartId is the same as userId for a customer's cart
        cartItemRepository.deleteByCartId(user.getId());
        log.info("OrderService: Cart cleared for user ID: {}", user.getId());

        return convertToOrderResponseDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long orderId) {
        log.info("OrderService: Fetching order by ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return convertToOrderResponseDTO(order);
    }

    // Helper method to convert Order entity to OrderResponseDTO
    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map((Function<OrderItem, OrderItemDTO>) item -> OrderItemDTO.builder()
                        .menuItemId(item.getMenuItem().getId())
                        .menuItemName(item.getMenuItem().getName())
                        .quantity(item.getQuantity())
                        .price(BigDecimal.valueOf(item.getPrice())) // CRITICAL FIX: Map actual price from entity
                        .build())
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .userId(order.getCustomer().getId())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .deliveryAddress(order.getDeliveryAddress().getFullAddress())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount().doubleValue())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus()) // Use orderStatus field from DTO
                .noContactDelivery(order.getNoContactDelivery())
                .couponCode(order.getCouponCode())
                .orderItems(orderItemDTOs)
                .build();
    }

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        // This method seems redundant with placeOrder, consider removing or re-purposing
        // For now, it's a stub to satisfy the interface.
        log.warn("OrderService: createOrder method called, but placeOrder is preferred. Returning null.");
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        log.info("OrderService: Fetching all orders.");
        return orderRepository.findAll().stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        log.info("OrderService: Fetching orders for user ID: {}", userId);
        // Ensure that the repository method exists, e.g., findByCustomerId(Long userId)
        List<Order> orders = orderRepository.findByCustomerId(userId);
        if (orders.isEmpty()) {
            log.info("OrderService: No orders found for user ID: {}", userId);
            return Collections.emptyList(); // Return empty list, not null
        }
        log.info("OrderService: Found {} orders for user ID: {}", orders.size(), userId);
        return orders.stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrderByRestaurantId(Long restaurantId) {
        log.info("OrderService: Fetching orders for restaurant ID: {}", restaurantId);
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        if (orders.isEmpty()) {
            log.info("OrderService: No orders found for restaurant ID: {}", restaurantId);
            return Collections.emptyList(); // Return empty list, not null
        }
        log.info("OrderService: Found {} orders for restaurant ID: {}", orders.size(), restaurantId);
        return orders.stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus newStatus) {
        log.info("OrderService: Updating order status for ID {} to {}", id, newStatus);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        order.setStatus(newStatus.name()); // Assuming status is stored as String in entity
        Order updatedOrder = orderRepository.save(order);
        log.info("OrderService: Order ID {} status updated to {}", id, updatedOrder.getStatus());
        return convertToOrderResponseDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO orderRequestDto) {
        log.info("OrderService: Updating order ID {}", id);
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        // Update fields from DTO to existingOrder
        existingOrder.setDeliveryAddress(addressRepository.findById(orderRequestDto.getDeliveryAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Address not found with ID: " + orderRequestDto.getDeliveryAddressId())));
        existingOrder.setTotalAmount(BigDecimal.valueOf(orderRequestDto.getTotalAmount()));
        existingOrder.setPaymentMethod(orderRequestDto.getPaymentMethod());
        existingOrder.setNoContactDelivery(orderRequestDto.getNoContactDelivery());
        existingOrder.setCouponCode(orderRequestDto.getCouponCode());

        // Update order items if provided (this can be complex depending on your logic: add, remove, update existing)
        // For simplicity, this example assumes a full replacement or only updates top-level order details.
        // A more robust solution would compare existing items with DTO items.
        Set<OrderItem> updatedOrderItems = orderRequestDto.getOrderItems().stream()
                .map((Function<OrderItemDTO, OrderItem>) itemDto -> {
                    MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found with ID: " + itemDto.getMenuItemId()));
                    return OrderItem.builder()
                            .order(existingOrder)
                            .menuItem(menuItem)
                            .quantity(itemDto.getQuantity())
                            .price(itemDto.getPrice().doubleValue())
                            .build();
                }).collect(Collectors.toSet());
        existingOrder.setOrderItems(updatedOrderItems); // This will replace existing items

        Order savedOrder = orderRepository.save(existingOrder);
        log.info("OrderService: Order ID {} updated successfully.", id);
        return convertToOrderResponseDTO(savedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        log.info("OrderService: Deleting order ID: {}", id);
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
        log.info("OrderService: Order ID {} deleted.", id);
    }

    @Override
    @Transactional
    public OrderResponseDTO assignDeliveryPerson(Long orderId, Long deliveryPersonId) {
        log.info("OrderService: Assigning delivery person {} to order {}", deliveryPersonId, orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        User deliveryPerson = userRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Person not found with ID: " + deliveryPersonId));

        // Assuming your Order entity has a setDeliveryPerson method
        order.setDeliveryPerson(deliveryPerson);
        // You might also want to update the order status here, e.g., to ASSIGNED or SHIPPED
        // order.setStatus(OrderStatus.ASSIGNED.name());

        Order updatedOrder = orderRepository.save(order);
        log.info("OrderService: Order {} assigned to delivery person {}", orderId, deliveryPersonId);
        return convertToOrderResponseDTO(updatedOrder);
    }
}
