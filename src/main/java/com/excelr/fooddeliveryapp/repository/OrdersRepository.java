package com.excelr.fooddeliveryapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.excelr.fooddeliveryapp.entity.DeliveryPerson;
import com.excelr.fooddeliveryapp.entity.Order;
import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.OrderStatus;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {

	List<Order> findBycustomerId(Long customerId);
	List<Order> findByRestaurantId(Long restaurantId);
	List<Order> findByDeliveryPerson(User deliveryPerson);
	List<Order> findByOrderStatus(OrderStatus orderStatus);
	boolean existsByDeliveryPerson(DeliveryPerson deliveryPerson);
	boolean existsByDeliveryPersonAndOrderStatusIn(DeliveryPerson deliveryPerson, List<OrderStatus> statuses);
	List<Order> findByCustomer(User user);
	Optional<Order> findById(Long id);
	List<Order> findByCustomerId(Long userId);
	
	List<Order> findByDeliveryPerson(DeliveryPerson deliveryPerson);



}
