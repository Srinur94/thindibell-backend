package com.excelr.fooddeliveryapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.excelr.fooddeliveryapp.entity.DeliveryPerson;

@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long>{
	
	Optional<DeliveryPerson> findFirstByIsAvailableTrue();

	Optional<DeliveryPerson> findTopByIsAvailableTrueOrderByIdAsc();


}
