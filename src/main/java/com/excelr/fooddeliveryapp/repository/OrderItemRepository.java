package com.excelr.fooddeliveryapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.excelr.fooddeliveryapp.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

	List<OrderItem> findByOrderId(Long orderId);

	//Optional<OrderItem> findByRestaurantId(Long restaurantId);
	
	List<OrderItem> findByOrder_Restaurant_Id(Long restaurantId);


	

	//List<OrderItem> findByMenuItemId(Long menuItemId);
}
