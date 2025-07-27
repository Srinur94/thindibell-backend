package com.excelr.fooddeliveryapp.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.excelr.fooddeliveryapp.dto.MenuItemsRequestDTO;
import com.excelr.fooddeliveryapp.dto.MenuItemsResponseDTO;
import com.excelr.fooddeliveryapp.entity.MenuItem;
import com.excelr.fooddeliveryapp.entity.Restaurant;
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.MenuItemRepoisitory;
import com.excelr.fooddeliveryapp.repository.RestaurantRepository;
import com.excelr.fooddeliveryapp.service.MenuItemsService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MenuItemsServiceImpl implements MenuItemsService{

	private final MenuItemRepoisitory menuItemRepository;

	private final RestaurantRepository restaurantRepository;

	private MenuItem convertToEntity(MenuItemsRequestDTO menuItemRequestDto, Restaurant restaurant) {

		return MenuItem.builder()
				.name(menuItemRequestDto.getName())
				.description(menuItemRequestDto.getDescription())
				.price(menuItemRequestDto.getPrice())
				.imageUrl(menuItemRequestDto.getImageUrl())
				.restaurant(restaurant)
				.build();
	}

	private MenuItemsResponseDTO convertToDto(MenuItem menuItems) {
		return MenuItemsResponseDTO.builder()
				.id(menuItems.getId())
				.name(menuItems.getName())
				.description(menuItems.getDescription())
				.price(menuItems.getPrice())
				.imageUrl(menuItems.getImageUrl())
				.restaurantId(menuItems.getRestaurant().getId())
				.restaurantName(menuItems.getRestaurant().getName())
				.build();

	}

	@Override
	@Transactional
	public MenuItemsResponseDTO createMenuItems(MenuItemsRequestDTO menuItemsRequestDTO) {
		Restaurant restaurant = restaurantRepository.findById(menuItemsRequestDTO.getRestaurantId())
				.orElseThrow(()-> new ResourceNotFoundException("Restaurant not found with Id: " + menuItemsRequestDTO.getRestaurantId()));

		MenuItem menuItem = convertToEntity(menuItemsRequestDTO, restaurant);
		MenuItem savedMenuItem = menuItemRepository.save(menuItem);
		return convertToDto(savedMenuItem);
	}

	@Override
	public MenuItemsResponseDTO getMenuItemsById(Long id) {
		MenuItem menuItems =menuItemRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Menu Items  not found with this id"+id));
		return convertToDto(menuItems);
	}

	@Override
	public List<MenuItemsResponseDTO> getMenuItemsByRestaurant(Long restaurantId) {
	    Restaurant restaurant = restaurantRepository.findById(restaurantId)
	        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id :" + restaurantId));

	    return menuItemRepository.findByRestaurant(restaurant).stream()
	        .map(menuItem -> {
	            MenuItemsResponseDTO dto = convertToDto(menuItem);
	            return dto;
	        })
	        .collect(Collectors.toList());
	}

	@Override
	public MenuItemsResponseDTO updateMenuItem(Long id, MenuItemsRequestDTO menuitemsDTO) {
		MenuItem existingMenuItem= menuItemRepository.findById(id)
				.orElseThrow(()->new ResourceNotFoundException("Menu Item not found with id :" + id));

		Restaurant restaurant = restaurantRepository.findById(menuitemsDTO.getRestaurantId())
				.orElseThrow(()-> new ResourceNotFoundException("Restaurant Not Found with id :" + menuitemsDTO.getRestaurantId()));

		existingMenuItem.setName(menuitemsDTO.getName());
		existingMenuItem.setDescription(menuitemsDTO.getDescription());
		existingMenuItem.setPrice(menuitemsDTO.getPrice());
		existingMenuItem.setRestaurant(restaurant);

		MenuItem updatedMenu = menuItemRepository.save(existingMenuItem);
		return convertToDto(updatedMenu) ;
	}

	@Override
	public void deleteMenuItem(Long id) {
	   if(!menuItemRepository.existsById(id)) {
		   throw new ResourceNotFoundException("Menu Item not found with id :" + id);
	   }
		 menuItemRepository.deleteById(id);

	}

	@Override
	public List<MenuItemsResponseDTO> getAllMenuItems() {
		return menuItemRepository.findAll().stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<MenuItemsResponseDTO> searchMenuItems(String name) {
		return menuItemRepository.findByNameContainingIgnoreCase(name)
				.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<MenuItemsResponseDTO> getMenuItemsByCategory(String category) {
		return menuItemRepository.findByCategoryIgnoreCase(category).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<MenuItemsResponseDTO> getPopularMenuItems(){
		return menuItemRepository.findByIsPopular(true).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}



}
