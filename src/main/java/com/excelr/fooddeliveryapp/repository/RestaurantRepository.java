package com.excelr.fooddeliveryapp.repository;

import com.excelr.fooddeliveryapp.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Import Optional

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // Custom query to fetch restaurants along with their owners to avoid N+1 problem and lazy loading issues
    @Query("SELECT r FROM Restaurant r JOIN FETCH r.owner")
    List<Restaurant> findAllWithOwners();

    // Custom query to find a restaurant by ID and eagerly fetch its owner
    @Query("SELECT r FROM Restaurant r JOIN FETCH r.owner WHERE r.id = :id")
    Optional<Restaurant> findByIdWithOwner(Long id); // Use @Param("id") if not using method parameter names

    // Existing methods (ensure they are present if used elsewhere)
    List<Restaurant> findByOwnerId(Long ownerId);
    boolean existsByName(String name);

    // You might have other custom queries here as well, e.g., for categories, popular items
    @Query("SELECT r FROM Restaurant r JOIN r.menuItems mi WHERE mi.category = ?1 GROUP BY r")
    List<Restaurant> findRestaurantsByMenuItemCategory(String category);

    @Query("SELECT r FROM Restaurant r JOIN r.menuItems mi GROUP BY r HAVING COUNT(mi) > 0 ORDER BY COUNT(mi) DESC")
    List<Restaurant> findRestaurantsOfferPopularItems();

	List<Restaurant> findByOwnerEmail(String username);
}
