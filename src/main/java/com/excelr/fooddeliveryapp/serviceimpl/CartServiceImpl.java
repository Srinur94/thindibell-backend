// src/main/java/com/excelr/fooddeliveryapp/serviceimpl/CartServiceImpl.java
package com.excelr.fooddeliveryapp.serviceimpl;

import com.excelr.fooddeliveryapp.dto.*;
import com.excelr.fooddeliveryapp.entity.*;
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.*;
import com.excelr.fooddeliveryapp.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepoisitory menuItemRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    @Transactional
    public CartDTO addToCart(Long userId, AddToCartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(user, menuItem.getRestaurant()));

        validateRestaurantConsistency(cart, menuItem.getRestaurant());

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getMenuItem().getId().equals(menuItem.getId()))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .menuItem(menuItem)
                    .quantity(request.getQuantity())
                    .priceAtAddToCart(menuItem.getPrice())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            cart.getCartItems().add(cartItem);
        }

        cart.recalculateTotal();
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    // FIX HERE: Use cartItemId from parameter, not from request DTO
    public CartDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById(cartItemId) // <--- FIXED: Use passed cartItemId
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Cart item does not belong to user");
        }

        if (request.getQuantity() <= 0) {
            return removeFromCart(userId, cartItem.getId());
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        Cart cart = cartItem.getCart();
        cart.recalculateTotal();
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO removeFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Cart item does not belong to user");
        }

        Cart cart = cartItem.getCart();
        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        if (cart.getCartItems().isEmpty()) {
            cart.setRestaurant(null);
        }

        cart.recalculateTotal();
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.setRestaurant(null);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> cart.getCartItems().size())
                .orElse(0);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCart(Long userId) {
        return cartRepository.existsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartSummaryResponse getCartSummary(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        return CartSummaryResponse.builder()
                .cartId(cart.getId())
                .totalItems(cart.getCartItems().size())
                .totalPrice(cart.getTotalPrice().doubleValue())
                .restaurantName(cart.getRestaurant() != null ? cart.getRestaurant().getName() : null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartForUser(User user) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        List<CartItemDTO> itemDTOs = cartItems.stream()
                .map(item -> CartItemDTO.builder()
                        .cartItemId(item.getId())
                        .menuItemId(item.getMenuItem().getId())
                        .menuItemName(item.getMenuItem().getName())
                        .menuItemImageUrl(item.getMenuItem().getImageUrl())
                        .menuItemPrice(item.getPriceAtAddToCart())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .restaurantId(item.getMenuItem().getRestaurant().getId())
                        .restaurantName(item.getMenuItem().getRestaurant().getName())
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalPrice = itemDTOs.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDTO.builder()
                .cartId(cart.getId())
                .userId(user.getId())
                .restaurantId(cart.getRestaurant() != null ? cart.getRestaurant().getId() : null)
                .items(itemDTOs)
                .totalPrice(totalPrice)
                .build();
    }


    // Helper methods

    private Cart createNewCart(User user, Restaurant restaurant) {
        return cartRepository.save(Cart.builder()
                .user(user)
                .restaurant(restaurant)
                .cartItems(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO)
                .build());
    }

    private Cart createNewCartForUser(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO)
                .build();
        return cartRepository.save(cart);
    }

    
    private void validateRestaurantConsistency(Cart cart, Restaurant restaurant) {
        if (cart.getRestaurant() != null && !cart.getRestaurant().getId().equals(restaurant.getId())) {
            throw new IllegalArgumentException("Cannot add items from different restaurants. Clear cart first.");
        }
    }

    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getCartItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return CartDTO.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .restaurantId(cart.getRestaurant() != null ? cart.getRestaurant().getId() : null)
                .items(itemDTOs)
                .totalPrice(cart.getTotalPrice())
                .build();
    }

    private CartItemDTO convertToDTO(CartItem cartItem) {
        MenuItem menuItem = cartItem.getMenuItem();
        BigDecimal price = cartItem.getPriceAtAddToCart();
        int quantity = cartItem.getQuantity();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));

        return CartItemDTO.builder()
                .cartItemId(cartItem.getId())
                .menuItemId(menuItem.getId())
                .menuItemName(menuItem.getName())
                .menuItemImageUrl(menuItem.getImageUrl())
                .menuItemPrice(price)
                .quantity(quantity)
                .subtotal(subtotal)
                .restaurantId(menuItem.getRestaurant().getId())
                .restaurantName(menuItem.getRestaurant().getName())
                .build();
    }

    private CartItemResponse convertCartItemResponse(CartItem item) {
        MenuItem menuItem = item.getMenuItem();
        BigDecimal subtotal = item.getPriceAtAddToCart().multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .cartItemId(item.getId())
                .menuItemId(menuItem.getId())
                .menuItemName(menuItem.getName())
                .menuItemPrice(item.getPriceAtAddToCart())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
    
    private void updateCartTotalPrice(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(total);
        // Removed the recursive call: updateCartTotalPrice(cart);
        // This method should only update the total, not call itself.
    }


    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user with ID: " + userId));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(this::convertCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(userId)
                .restaurantId(cart.getRestaurant() != null ? cart.getRestaurant().getId() : null)
                .restaurantName(cart.getRestaurant() != null ? cart.getRestaurant().getName() : null)
                .items(itemResponses)
                .totalAmount(totalPrice)
                .build();
    }
}