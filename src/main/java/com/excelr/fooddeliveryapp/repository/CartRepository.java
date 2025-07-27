package com.excelr.fooddeliveryapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.excelr.fooddeliveryapp.entity.Cart;
import com.excelr.fooddeliveryapp.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    // Find cart by user ID
    Optional<Cart> findByUserId(Long userId);
    
    // Find active cart by user ID (if you want to implement multiple carts per user)
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId ORDER BY c.updatedAt DESC")
    Optional<Cart> findLatestCartByUserId(@Param("userId") Long userId);
    
    // Check if cart exists for user
    boolean existsByUserId(Long userId);
    
    // Delete cart by user ID
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    // Get cart with items
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.menuItem WHERE c.id = :cartId")
    Optional<Cart> findCartWithItems(@Param("cartId") Long cartId);
    
    // Get cart by user with items
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.menuItem WHERE c.user.id = :userId")
    Optional<Cart> findCartWithItemsByUserId(@Param("userId") Long userId);
    
    Optional<Cart> findByUser(User user);


}

