package com.excelr.fooddeliveryapp.controller;

import java.util.Collections; // Import Collections
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.excelr.fooddeliveryapp.dto.OrderRequestDTO;
import com.excelr.fooddeliveryapp.dto.OrderResponseDTO;
import com.excelr.fooddeliveryapp.enums.OrderStatus;
import com.excelr.fooddeliveryapp.service.OrderService;
import com.excelr.fooddeliveryapp.entity.User; // Assuming your User entity is here or similar custom UserDetails

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Added for logging

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Slf4j // Enable logging for this controller
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDto){
		OrderResponseDTO created = orderService.createOrder(orderRequestDto);
		return new ResponseEntity<>(created , HttpStatus.CREATED);
	}

	@PostMapping("/place") // Maps to POST /api/v1/orders/place
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')") // Only customers and admins can place orders
	public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
		log.info("OrderController: Received request to place order for user ID: {}", orderRequestDTO.getUserId());
		OrderResponseDTO newOrder = orderService.placeOrder(orderRequestDTO);
		log.info("OrderController: Order placed successfully with ID: {}", newOrder.getOrderId());
		return ResponseEntity.status(201).body(newOrder); // 201 Created
	}

	@GetMapping("/{orderId}")
	// ADMIN can view any order, RESTAURANT_OWNER can view their restaurant's orders (might need more specific check here)
	// For customer to view their own order by ID, you'd need a separate endpoint or more complex @PreAuthorize
	@PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_OWNER')") // Keep this as is for now, assuming specific internal use
	public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long orderId){
		log.info("OrderController: Attempting to fetch order by ID: {}", orderId);
		OrderResponseDTO order = orderService.getOrderById(orderId);
		if (order == null) {
			log.warn("OrderController: Order not found for ID: {}", orderId);
			return ResponseEntity.notFound().build();
		}
		log.info("OrderController: Successfully fetched order by ID: {}", orderId);
		return ResponseEntity.ok(order);
	}

	@GetMapping("/user/{customerId}")
	// FIX: Ensure customer can only view their own orders or Admin can view any
	// Assuming authentication.principal.id is available and is a Long. If it's a User object, cast it.
	@PreAuthorize("hasRole('ADMIN') or (#customerId == authentication.principal.id)")
	public ResponseEntity<List<OrderResponseDTO>> getOrdersByCustomerId(@PathVariable Long customerId){
		log.info("OrderController: Attempting to fetch orders for customer ID: {}", customerId);
		// For debugging, log the authenticated principal's ID if available
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			// Assuming your UserDetails implementation has a method to get the actual user ID (e.g., getId())
			if (userDetails instanceof User) { // If your User entity implements UserDetails and has getId()
				User authenticatedUser = (User) userDetails;
				log.info("OrderController: Authenticated principal ID: {}", authenticatedUser.getId());
			} else {
				log.warn("OrderController: Authenticated principal is UserDetails but not your custom User entity. Cannot directly get ID from principal.");
			}
		}

		List<OrderResponseDTO> orders = orderService.getOrdersByUserId(customerId);
		// Ensure an empty list is returned, not null, for consistent frontend handling
		if (orders == null) {
			log.info("OrderController: No orders found for customer ID: {}. Returning empty list.", customerId);
			return ResponseEntity.ok(Collections.emptyList());
		}
		log.info("OrderController: Successfully fetched {} orders for customer ID: {}", orders.size(), customerId);
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/restaurant/{restaurantId}")
	@PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_OWNER')")
	public ResponseEntity<List<OrderResponseDTO>> getOrdersByRestaurantId(@PathVariable Long restaurantId) {
		log.info("OrderController: Attempting to fetch orders for restaurant ID: {}", restaurantId);
		List<OrderResponseDTO> orders = orderService.getOrderByRestaurantId(restaurantId);
		if (orders == null) {
			log.info("OrderController: No orders found for restaurant ID: {}. Returning empty list.", restaurantId);
			return ResponseEntity.ok(Collections.emptyList());
		}
		log.info("OrderController: Successfully fetched {} orders for restaurant ID: {}", orders.size(), restaurantId);
		return ResponseEntity.ok(orders);
	}

	@PatchMapping("/{orderId}/status")
	@PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_OWNER','DELIVERY_PERSON')")
	public ResponseEntity<OrderResponseDTO> updateOrderStatus(
			@PathVariable("orderId") Long id,
			@RequestParam("status") OrderStatus status) {
		log.info("OrderController: Updating status for order ID {} to {}", id, status);
		OrderResponseDTO updatedOrder = orderService.updateOrderStatus(id, status);
		log.info("OrderController: Order ID {} status updated to {}", id, updatedOrder.getStatus());
		return ResponseEntity.ok(updatedOrder);
	}


	@PutMapping("/{orderId}")
	// FIX: Add ownership check for CUSTOMER
	// This assumes getOrderById returns an OrderResponseDTO with a getUserId() method
	@PreAuthorize("hasRole('ADMIN') or @orderService.getOrderById(#orderId).getUserId() == authentication.principal.id")
	public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Long orderId, @Valid @RequestBody OrderRequestDTO orderRequestDTO){
		log.info("OrderController: Updating order ID {}", orderId);
		OrderResponseDTO updatedOrder = orderService.updateOrder(orderId, orderRequestDTO);
		log.info("OrderController: Order ID {} updated successfully.", orderId);
		return ResponseEntity.ok(updatedOrder);
	}

	@DeleteMapping("/{orderId}")
	// FIX: Add ownership check for CUSTOMER
	// This assumes getOrderById returns an OrderResponseDTO with a getUserId() method
	@PreAuthorize("hasRole('ADMIN') or @orderService.getOrderById(#orderId).getUserId() == authentication.principal.id")
	public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId){
		log.info("OrderController: Deleting order ID {}", orderId);
		orderService.deleteOrder(orderId);
		log.info("OrderController: Order ID {} deleted successfully.", orderId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{orderId}/assign-delivery-person/{deliveryPersonId}")
	@PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_OWNER')")
	public ResponseEntity<OrderResponseDTO> assignDeliveryPersonToOrder(
			@PathVariable("orderId") Long orderId,
			@PathVariable("deliveryPersonId") Long deliveryPersonId) {
		log.info("OrderController: Assigning delivery person ID {} to order ID {}", deliveryPersonId, orderId);
		OrderResponseDTO updatedOrder = orderService.assignDeliveryPerson(orderId, deliveryPersonId);
		log.info("OrderController: Delivery person ID {} assigned to order ID {}", deliveryPersonId, orderId);
		return ResponseEntity.ok(updatedOrder);
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
		log.info("OrderController: Fetching all orders (Admin access).");
		List<OrderResponseDTO> orders = orderService.getAllOrders();
		log.info("OrderController: Fetched {} total orders.", orders.size());
		return ResponseEntity.ok(orders);
	}
	
	
}
