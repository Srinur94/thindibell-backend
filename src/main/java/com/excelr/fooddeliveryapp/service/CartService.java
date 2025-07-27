package com.excelr.fooddeliveryapp.service;

import com.excelr.fooddeliveryapp.dto.AddToCartRequest;
import com.excelr.fooddeliveryapp.dto.CartDTO;
import com.excelr.fooddeliveryapp.dto.CartResponse;
import com.excelr.fooddeliveryapp.dto.CartSummaryResponse;
import com.excelr.fooddeliveryapp.dto.UpdateCartItemRequest;
import com.excelr.fooddeliveryapp.entity.User;

public interface CartService {
    CartDTO addToCart(Long userId, AddToCartRequest request);
    CartDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request);
    CartDTO removeFromCart(Long userId, Long cartItemId);
    CartDTO getCart(Long userId);
    void clearCart(Long userId);
    int getCartItemCount(Long userId);
    boolean hasCart(Long userId);
    CartSummaryResponse getCartSummary(Long userId);
    CartResponse getCartByUserId(Long userId);
    
    CartDTO getCartForUser(User user);

}
