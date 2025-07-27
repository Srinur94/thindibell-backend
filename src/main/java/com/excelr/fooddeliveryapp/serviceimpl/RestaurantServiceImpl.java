package com.excelr.fooddeliveryapp.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import com.excelr.fooddeliveryapp.dto.RestaurantRequestDTO;
import com.excelr.fooddeliveryapp.dto.RestaurantResponseDTO;
import com.excelr.fooddeliveryapp.entity.Restaurant;
import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.Role;
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.RestaurantRepository;
import com.excelr.fooddeliveryapp.repository.UserRepository;
import com.excelr.fooddeliveryapp.service.RestaurantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService{

	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;

	private RestaurantResponseDTO convertToDto(Restaurant restaurant) {
	    Long ownerId = null;
	    String ownerName = null;

	    // CRITICAL FIX: Get ownerId and ownerName directly from the already-fetched 'owner' object
	    // No need for an extra userRepository.findById() call here if owner is already loaded (e.g., via JOIN FETCH)
	    if (restaurant.getOwner() != null) {
	        ownerId = restaurant.getOwner().getId();
	        ownerName = restaurant.getOwner().getName();
	    }

	    return RestaurantResponseDTO.builder()
	            .id(restaurant.getId())
	            .name(restaurant.getName())
	            .description(restaurant.getDescription())
	            .ownerId(ownerId) // Use the safely retrieved ownerId
	            .ownerName(ownerName) // Use the safely retrieved ownerName
	            .address(restaurant.getAddress())
	            .phoneNumber(restaurant.getPhoneNumber())
	            .email(restaurant.getEmail())
	            .imageUrl(restaurant.getImageUrl()) // CRITICAL FIX: Changed from .image_url to .imageUrl
	            .rating(restaurant.getRating())
	            .build();
	}

    // Helper method to convert Request DTO to Entity (for creation/update)
	private Restaurant convertToEntity(RestaurantRequestDTO dto, Restaurant restaurant) {
	    if (restaurant == null) {
	        restaurant = new Restaurant();
	    }

	    restaurant.setName(dto.getName());
	    restaurant.setAddress(dto.getAddress());
	    // REMOVED: restaurant.setOwnerName(dto.getOwnerName()); // This field should not be set directly
	    restaurant.setDescription(dto.getDescription());
	    restaurant.setImageUrl(dto.getImageUrl());
	    restaurant.setRating(dto.getRating());
	    // REMOVED: Redundant restaurant.setAddress(dto.getAddress());
	    restaurant.setPhoneNumber(dto.getPhoneNumber());

	    User owner = userRepository.findById(dto.getOwnerId())
	            .orElseThrow(() -> new ResourceNotFoundException("User (owner) not found with ID: " + dto.getOwnerId()));

	    // Optional: role check
	    if (owner.getRole() != null && !owner.getRole().equals(Role.RESTAURANT_OWNER)) {
	        throw new IllegalArgumentException("User with ID " + dto.getOwnerId() + " is not a RESTAURANT_OWNER.");
	    }

	    restaurant.setOwner(owner);

	    return restaurant;
	}

     // Create Restaurant
    @Override
    @Transactional // Add @Transactional for write operations
    public RestaurantResponseDTO createRestaurant(RestaurantRequestDTO restaurantRequestDTO) {
        // Check if with ownerId already has a restaurant
        List<Restaurant> existing = restaurantRepository.findByOwnerId(restaurantRequestDTO.getOwnerId());
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("Owner with ID " + restaurantRequestDTO.getOwnerId() + " already has a restaurant.");
        }

        Restaurant restaurant = convertToEntity(restaurantRequestDTO, null);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return convertToDto(savedRestaurant);
    }

     // Find By Id
	@Override
	@Transactional(readOnly = true) // Add @Transactional(readOnly = true) for read operations
	public RestaurantResponseDTO getRestaurantById(Long Id) {
		// CRITICAL FIX: Use findByIdWithOwner to eagerly fetch the owner
		Restaurant restaurants= restaurantRepository.findByIdWithOwner(Id)
				.orElseThrow(()-> new ResourceNotFoundException("Restaurant not found with Id " + Id));
		return convertToDto(restaurants);
	}

	@Override
	@Transactional(readOnly = true) // Add @Transactional(readOnly = true)
	public List<RestaurantResponseDTO> getAllRestaurants() { // CRITICAL FIX: Corrected typo from getAllRestaurans
	    // CRITICAL FIX: Use findAllWithOwners to eagerly fetch owners
	    return restaurantRepository.findAllWithOwners().stream()
	        .map(this::convertToDto)
	        .collect(Collectors.toList());
	}


	@Override
	@Transactional(readOnly = true) // Add @Transactional(readOnly = true)
	public List<RestaurantResponseDTO> getRestaurantByOwner(Long ownerId) {
		if(!userRepository.existsById(ownerId)) {
			throw new ResourceNotFoundException("User (owner) not found with id :" + ownerId);
		}
		// Assuming findByOwnerId also implicitly fetches the owner or it's handled by convertToDto
		return restaurantRepository.findByOwnerId(ownerId).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional // Add @Transactional for write operations
	public RestaurantResponseDTO updateRestaurant(Long id, RestaurantRequestDTO restaurantRequestDTO) {
		Restaurant existingRestaurant = restaurantRepository.findById(id)
				.orElseThrow(()->new ResourceNotFoundException("Restaurant Not Found with id " + id));

		if (!existingRestaurant.getName().equals(restaurantRequestDTO.getName()) &&
	            restaurantRepository.existsByName(restaurantRequestDTO.getName())) {
	            throw new IllegalArgumentException("Restaurant with name '" + restaurantRequestDTO.getName() + "' already exists.");
	        }

	        Restaurant updatedRestaurant = convertToEntity(restaurantRequestDTO, existingRestaurant);
	        Restaurant savedRestaurant = restaurantRepository.save(updatedRestaurant);
	        return convertToDto(savedRestaurant);
	    }

	@Override
	@Transactional // Add @Transactional for write operations
	public void deleteRestaurant(Long id) {
		if(!restaurantRepository.existsById(id)) {
			throw new ResourceNotFoundException("Restaurant not found with id" + id);
		}
		restaurantRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true) // Add @Transactional(readOnly = true)
	public List<RestaurantResponseDTO> getRestaurantsByMenuItemCategory(String category) {
	List<Restaurant> restaurants  = restaurantRepository.findRestaurantsByMenuItemCategory(category);
		return restaurants.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true) // Add @Transactional(readOnly = true)
	public List<RestaurantResponseDTO> getRestaurantsOfferingPopularItems() {
		List<Restaurant> restaurants = restaurantRepository.findRestaurantsOfferPopularItems();
		return restaurants.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

}
