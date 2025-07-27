package com.excelr.fooddeliveryapp.serviceimpl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections; // Import Collections
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import com.excelr.fooddeliveryapp.dto.OrderItemRequestDTO;
import com.excelr.fooddeliveryapp.dto.OrderItemResponseDTO;
import com.excelr.fooddeliveryapp.entity.MenuItem;
import com.excelr.fooddeliveryapp.entity.Order;
import com.excelr.fooddeliveryapp.entity.OrderItem;
import com.excelr.fooddeliveryapp.entity.Restaurant;
import com.excelr.fooddeliveryapp.entity.User; // Assuming User entity
import com.excelr.fooddeliveryapp.enums.OrderStatus;
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.MenuItemRepoisitory;
import com.excelr.fooddeliveryapp.repository.OrderItemRepository;
import com.excelr.fooddeliveryapp.repository.OrdersRepository;
import com.excelr.fooddeliveryapp.repository.RestaurantRepository;
import com.excelr.fooddeliveryapp.repository.UserRepository; // Added for user lookup if needed
import com.excelr.fooddeliveryapp.service.OrderItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Added for logging

@Service
@RequiredArgsConstructor
@Slf4j // Enable logging for this service
public class OrderItemServiceImpl implements OrderItemService {

	private final OrderItemRepository orderItemRepository;
	private final OrdersRepository orderRepository;
	private final MenuItemRepoisitory menuItemRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository; // Added to fetch user details if needed for owner email

	private OrderItemResponseDTO convertToDto(OrderItem orderItem) {
		return OrderItemResponseDTO.builder()
				.id(orderItem.getId())
				.quantity(orderItem.getQuantity())
				.price(orderItem.getPrice()) // Assuming price in entity is double, DTO expects double
				.orderId(orderItem.getOrder() !=null ? orderItem.getOrder().getId() : null)
				.menuItemId(orderItem.getMenuItem().getId())
				.menuItemName(orderItem.getMenuItem().getName())
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public OrderItemResponseDTO getOrderItemById(Long orderItemId) {
		log.info("OrderItemService: Fetching order item by ID: {}", orderItemId);
		OrderItem orderItem = orderItemRepository.findById(orderItemId)
				.orElseThrow(()-> new ResourceNotFoundException("Order item not found with id : " + orderItemId ));
		return convertToDto(orderItem);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderItemResponseDTO> getAllOrderItems() {
		log.info("OrderItemService: Attempting to fetch all order items with role-based access.");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		log.debug("User: {}, Authorities: {}", username, authorities);

		// ADMIN access
		if (hasAuthority(authorities, "ADMIN", "ROLE_ADMIN")) {
			log.info("OrderItemService: User is ADMIN. Fetching all order items.");
			return orderItemRepository.findAll().stream()
					.map(this::convertToDto)
					.collect(Collectors.toList());
		}

		// RESTAURANT_OWNER access
		if (hasAuthority(authorities, "RESTAURANT_OWNER", "ROLE_RESTAURANT_OWNER")) {
			log.info("OrderItemService: User is RESTAURANT_OWNER. Fetching order items for their restaurant.");
			// Assuming owner's email is their username
			List<Restaurant> restaurants = restaurantRepository.findByOwnerEmail(username);
			if (restaurants.isEmpty()) {
				log.warn("OrderItemService: No restaurant found for owner: {}", username);
				throw new UsernameNotFoundException("No restaurant found for owner: " + username);
			}
			// Assuming an owner can only have one restaurant or we pick the first one
			Long restaurantId = restaurants.get(0).getId();
			log.debug("Found restaurant ID {} for owner {}", restaurantId, username);

			return orderItemRepository.findByOrder_Restaurant_Id(restaurantId).stream()
					.map(this::convertToDto)
					.collect(Collectors.toList());
		}

		log.warn("OrderItemService: User {} is not authorized to view all order items.", username);
		throw new AccessDeniedException("You are not authorized to view order items.");
	}

	private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String... roles) {
		for (String role : roles) {
			if (authorities.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(role))) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId) {
		log.info("OrderItemService: Fetching order items for order ID: {}", orderId);
		if(!orderRepository.existsById(orderId)) {
			throw new ResourceNotFoundException("Order not found with Id " + orderId);
		}
		// Assuming findByOrderId exists in OrderItemRepository
		List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
		if (orderItems.isEmpty()) {
			log.info("OrderItemService: No order items found for order ID: {}", orderId);
			return Collections.emptyList();
		}
		log.info("OrderItemService: Found {} order items for order ID: {}", orderItems.size(), orderId);
		return orderItems.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public OrderItemResponseDTO updateOrderItem(Long id, OrderItemRequestDTO orderItemRequestDto) {
		log.info("OrderItemService: Updating order item ID {}", id);
		OrderItem existingOrderItem = orderItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order Item Not Found with Id: " + id));

		Order parentOrder = existingOrderItem.getOrder();
		if (parentOrder == null) {
			throw new IllegalStateException("Order item is not associated with an order");
		}

		if (parentOrder.getStatus().equals(OrderStatus.DELIVERED.name()) || // Use .equals() for String comparison
				parentOrder.getStatus().equals(OrderStatus.CANCELLED.name())) {
			throw new IllegalStateException("Cannot update order item for an order that is already " + parentOrder.getStatus());
		}

		MenuItem menuItem = menuItemRepository.findById(orderItemRequestDto.getMenuItemId())
				.orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id " + orderItemRequestDto.getMenuItemId()));

		// Recalculate price based on new quantity and current menu item price
		BigDecimal calculatedPrice = menuItem.getPrice().multiply(BigDecimal.valueOf(orderItemRequestDto.getQuantity()));


		existingOrderItem.setQuantity(orderItemRequestDto.getQuantity());
		existingOrderItem.setPrice(calculatedPrice.doubleValue()); // Set as double
		existingOrderItem.setMenuItem(menuItem);

		OrderItem updatedOrderItem = orderItemRepository.save(existingOrderItem);
		log.info("OrderItemService: Order item ID {} updated. Recalculating parent order total.", id);

		// Recalculate the total price of the parent order based on its current items
		// Need to fetch the latest items associated with the parent order, or ensure 'items' collection is loaded
		parentOrder.setTotalAmount(BigDecimal.valueOf(
				parentOrder.getOrderItems().stream() // Assuming getOrderItems() returns a Set/List of OrderItem
						.mapToDouble(OrderItem::getPrice) // Use getPrice() directly if it's double
						.sum()
		));
		orderRepository.save(parentOrder);
		log.info("OrderItemService: Parent order total for ID {} recalculated.", parentOrder.getId());

		return convertToDto(updatedOrderItem);
	}

	@Override
	@Transactional
	public void deleteOrderItem(Long id) {
		log.info("OrderItemService: Deleting order item ID: {}", id);
		// Fetch the order item or throw error
		OrderItem orderItem = orderItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order Item not found with Id: " + id));

		// Get parent order
		Order parentOrder = orderItem.getOrder();
		if (parentOrder == null) {
			throw new IllegalStateException("Order Item is not associated with any order.");
		}

		// Prevent deletion if order is already in a final state
		if (parentOrder.getStatus().equals(OrderStatus.DELIVERED.name()) ||
				parentOrder.getStatus().equals(OrderStatus.CANCELLED.name())) {
			throw new IllegalStateException("Cannot delete order item for an order that is already " + parentOrder.getStatus());
		}

		// Delete the order item
		orderItemRepository.delete(orderItem);
		log.info("OrderItemService: Order item ID {} deleted from database.", id);

		// Remove from order's item list (in memory) and recalculate total
		// This requires the collection to be managed by JPA or manually updated
		parentOrder.getOrderItems().removeIf(item -> item.getId().equals(orderItem.getId()));

		// Recalculate total price based on remaining items
		parentOrder.setTotalAmount(BigDecimal.valueOf(
				parentOrder.getOrderItems().stream()
						.mapToDouble(OrderItem::getPrice)
						.sum()
		));

		orderRepository.save(parentOrder);
		log.info("OrderItemService: Parent order total for ID {} recalculated after item deletion.", parentOrder.getId());
	}
}
