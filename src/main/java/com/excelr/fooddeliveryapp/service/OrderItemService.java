package com.excelr.fooddeliveryapp.service;

import java.util.List;

import com.excelr.fooddeliveryapp.dto.OrderItemRequestDTO;
import com.excelr.fooddeliveryapp.dto.OrderItemResponseDTO;

public interface OrderItemService {

	//OrderItemResponseDTO createOrderItem(OrderItemRequestDTO orderItemRequestDto);
	OrderItemResponseDTO getOrderItemById(Long id);
	List<OrderItemResponseDTO> getAllOrderItems();
	List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId);

	OrderItemResponseDTO updateOrderItem(Long id, OrderItemRequestDTO orderItemRequestDto);

	void deleteOrderItem(Long id);
}
