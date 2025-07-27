package com.excelr.fooddeliveryapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.excelr.fooddeliveryapp.entity.MenuItem;
import com.excelr.fooddeliveryapp.entity.Restaurant;

@Repository
public interface MenuItemRepoisitory extends JpaRepository<MenuItem, Long>{
        List<MenuItem>findByRestaurant(Restaurant restaurant);
        
        List<MenuItem> findByCategoryIgnoreCase(String category);
        
        List<MenuItem> findByIsPopular(Boolean isPopular);

		 List<MenuItem> findByNameContainingIgnoreCase(String name);

		//Optional<Restaurant> findByRestaurantId(Long restaurantId);
}
