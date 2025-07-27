package com.excelr.fooddeliveryapp.repository;

import com.excelr.fooddeliveryapp.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId); // CRITICAL: Method to fetch addresses by user ID
    // Or if your Address entity has a User object directly: List<Address> findByUser(User user);
}
