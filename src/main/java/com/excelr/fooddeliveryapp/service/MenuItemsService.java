package com.excelr.fooddeliveryapp.service;

import java.awt.Menu;
import java.util.List;

import com.excelr.fooddeliveryapp.dto.MenuItemsRequestDTO;
import com.excelr.fooddeliveryapp.dto.MenuItemsResponseDTO;

public interface MenuItemsService {

	MenuItemsResponseDTO createMenuItems(MenuItemsRequestDTO menuItemsRequestDTO);
	MenuItemsResponseDTO getMenuItemsById(Long id);
	List<MenuItemsResponseDTO> getMenuItemsByRestaurant(Long restaurantId);
	MenuItemsResponseDTO updateMenuItem(Long id, MenuItemsRequestDTO menuitemsDTO);
	void deleteMenuItem(Long id);
	List<MenuItemsResponseDTO> getAllMenuItems();
	List<MenuItemsResponseDTO> searchMenuItems(String name);
	
	List<MenuItemsResponseDTO> getMenuItemsByCategory(String category);
	List<MenuItemsResponseDTO> getPopularMenuItems();
	
	
}
