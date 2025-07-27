package com.excelr.fooddeliveryapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.excelr.fooddeliveryapp.entity.Cart;
import com.excelr.fooddeliveryapp.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // Find cart item by cart and menu item
    Optional<CartItem> findByCartIdAndMenuItemId(Long cartId, Long menuItemId);
    
    // Find all cart items by cart ID
    @Query("SELECT ci FROM CartItem ci LEFT JOIN FETCH ci.menuItem WHERE ci.cart.id = :cartId")
    java.util.List<CartItem> findByCartId(@Param("cartId") Long cartId);
    
    // Delete all cart items by cart ID
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);
    
    // Count items in cart
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Long countByCartId(@Param("cartId") Long cartId);
    
    // Get total quantity in cart
    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Integer getTotalQuantityByCartId(@Param("cartId") Long cartId);
    
    List<CartItem> findByCart(Cart cart);
}