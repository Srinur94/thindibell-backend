package com.excelr.fooddeliveryapp.repository;

import com.excelr.fooddeliveryapp.entity.Order;
import com.excelr.fooddeliveryapp.entity.Payment;
import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);
    
    List<Payment> findByUserId(Long userId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId AND p.status = :status")
    Optional<Payment> findByOrderIdAndStatus(@Param("orderId") Long orderId, @Param("status") PaymentStatus status);
    
    Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);
    
    

    
    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.status = :status")
    List<Payment> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") PaymentStatus status);

	List<User> findByOrder(Order order);

	List<Payment> findByUser(User user);
}