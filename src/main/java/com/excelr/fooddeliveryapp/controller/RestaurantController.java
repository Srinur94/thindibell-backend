package com.excelr.fooddeliveryapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.excelr.fooddeliveryapp.dto.RestaurantRequestDTO;
import com.excelr.fooddeliveryapp.dto.RestaurantResponseDTO;
import com.excelr.fooddeliveryapp.service.RestaurantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/restaurant")
@RequiredArgsConstructor
@Validated
public class RestaurantController {

	 private final RestaurantService restaurantService;

	 @PostMapping
	 @PreAuthorize("hasRole('RESTAURANT_OWNER')") //Only Restaurant owners create Restaurants
	 public ResponseEntity<RestaurantResponseDTO> createRestaurant(@Valid @RequestBody RestaurantRequestDTO restaurantRequestDTO){

		 RestaurantResponseDTO response = restaurantService.createRestaurant(restaurantRequestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	 }


	 //Get By Id
	 @GetMapping("/{id}")
	 @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_OWNER')")
	 public ResponseEntity<RestaurantResponseDTO> getRestaurantById(@PathVariable Long id){
		 RestaurantResponseDTO restaurantResponseDto = restaurantService.getRestaurantById(id);
		return ResponseEntity.ok(restaurantResponseDto);

	 }

	    @GetMapping()
	   // @PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants() {
	        List<RestaurantResponseDTO> restaurants = restaurantService.getAllRestaurants();
	        return ResponseEntity.ok(restaurants);
	    }

	    @GetMapping("/owner/{ownerId}")
	    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantByOwner(@PathVariable Long ownerId){
	    	List<RestaurantResponseDTO> restaurantResponseDTO= restaurantService.getRestaurantByOwner(ownerId);
			return ResponseEntity.ok(restaurantResponseDTO);

	    }

	    //update the restaurant
	    @PutMapping("/{id}")
	   @PreAuthorize("hasRole('RESTAURANT_OWNER')")
	    public ResponseEntity<RestaurantResponseDTO> updateRestaurant(@PathVariable Long id, @Valid @RequestBody RestaurantRequestDTO restaurantResponsedto){
	    	RestaurantResponseDTO updatedRestaurant = restaurantService.updateRestaurant(id, restaurantResponsedto);

			return ResponseEntity.ok(updatedRestaurant);

	    }

	    @DeleteMapping("/{id}")
	    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id){
	    	restaurantService.deleteRestaurant(id);
	    	return ResponseEntity.noContent().build();
	    }
	    
	    @GetMapping("/category/{categoryName}")
	    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByMenuItemCategory(@PathVariable String categoryName){
	    	List<RestaurantResponseDTO> restaurants = restaurantService.getRestaurantsByMenuItemCategory(categoryName);
	    	return ResponseEntity.ok(restaurants);
	    }
	    
	    @GetMapping("/popular")
	    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsOfferingPopularItems(){
	    	List<RestaurantResponseDTO> restaurants = restaurantService.getRestaurantsOfferingPopularItems();
	    	return ResponseEntity.ok(restaurants);
	    }
	    
	    


}
