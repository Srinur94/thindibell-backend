package com.excelr.fooddeliveryapp.controller;

import java.util.Collections; // Import Collections
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.excelr.fooddeliveryapp.dto.OrderItemRequestDTO;
import com.excelr.fooddeliveryapp.dto.OrderItemResponseDTO;
import com.excelr.fooddeliveryapp.service.OrderItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Added for logging

@RestController
@RequestMapping("/api/v1/order-items")
@RequiredArgsConstructor
@Slf4j // Enable logging for this controller
public class OrderItemController {

	private final OrderItemService orderItemService;

	@GetMapping("/{orderItemId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER', 'CUSTOMER')") // Allow customer to view their own order items
	public ResponseEntity<OrderItemResponseDTO> getOrderItemById(@PathVariable Long orderItemId){
		log.info("OrderItemController: Fetching order item by ID: {}", orderItemId);
		OrderItemResponseDTO orderItem = orderItemService.getOrderItemById(orderItemId);
		if (orderItem == null) {
			log.warn("OrderItemController: Order item not found for ID: {}", orderItemId);
			return ResponseEntity.notFound().build();
		}
		log.info("OrderItemController: Successfully fetched order item by ID: {}", orderItemId);
		return ResponseEntity.ok(orderItem);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')") // Only ADMIN and RESTAURANT_OWNER can get all order items
	public ResponseEntity<List<OrderItemResponseDTO>> getAllOrderItems(){
		log.info("OrderItemController: Fetching all order items.");
		List<OrderItemResponseDTO> orderItems = orderItemService.getAllOrderItems();
		if (orderItems == null) { // Should return empty list, but defensive check
			log.warn("OrderItemController: getAllOrderItems returned null. Returning empty list.");
			return ResponseEntity.ok(Collections.emptyList());
		}
		log.info("OrderItemController: Fetched {} total order items.", orderItems.size());
		return ResponseEntity.ok(orderItems);
	}

	@GetMapping("/order/{orderId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER','CUSTOMER')")
	public ResponseEntity<List<OrderItemResponseDTO>> getOrderItemByorderId(@PathVariable Long orderId){
		log.info("OrderItemController: Fetching order items for order ID: {}", orderId);
		List<OrderItemResponseDTO> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
		if (orderItems == null) { // Should return empty list, but defensive check
			log.warn("OrderItemController: getOrderItemByorderId returned null. Returning empty list.");
			return ResponseEntity.ok(Collections.emptyList());
		}
		log.info("OrderItemController: Fetched {} order items for order ID: {}", orderItems.size(), orderId);
		return ResponseEntity.ok(orderItems);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
	public ResponseEntity<OrderItemResponseDTO> updateOrderItem(@PathVariable Long id , @Valid @RequestBody OrderItemRequestDTO orderItemRequestDto){
		log.info("OrderItemController: Updating order item ID {}", id);
		OrderItemResponseDTO updatedOrderItem = orderItemService.updateOrderItem(id, orderItemRequestDto);
		log.info("OrderItemController: Order item ID {} updated successfully.", id);
		return ResponseEntity.ok(updatedOrderItem);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')") // Added pre-authorize for delete
	public ResponseEntity<String> deleteOrderItem(@PathVariable Long id) {
		log.info("OrderItemController: Deleting order item ID {}", id);
		orderItemService.deleteOrderItem(id);
		log.info("OrderItemController: Order item ID {} deleted successfully.", id);
		return ResponseEntity.ok("Order item deleted successfully");
	}
}
