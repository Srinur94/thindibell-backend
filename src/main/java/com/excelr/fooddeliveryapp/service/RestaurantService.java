package com.excelr.fooddeliveryapp.service;

import java.util.List;

import com.excelr.fooddeliveryapp.dto.RestaurantRequestDTO;
import com.excelr.fooddeliveryapp.dto.RestaurantResponseDTO;

public interface RestaurantService {

	 RestaurantResponseDTO createRestaurant(RestaurantRequestDTO restaurantRequestDTO);
	 RestaurantResponseDTO getRestaurantById(Long Id);
	 List<RestaurantResponseDTO> getRestaurantByOwner(Long ownerId);
	 RestaurantResponseDTO updateRestaurant (Long id, RestaurantRequestDTO restaurantRequestDTO);
	 void deleteRestaurant(Long id);
	 
	 List<RestaurantResponseDTO> getRestaurantsByMenuItemCategory(String category);
	 List<RestaurantResponseDTO> getRestaurantsOfferingPopularItems();
	List<RestaurantResponseDTO> getAllRestaurants();
}
