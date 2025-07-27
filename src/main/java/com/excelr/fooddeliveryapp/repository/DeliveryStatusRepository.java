package com.excelr.fooddeliveryapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.excelr.fooddeliveryapp.entity.DeliveryStatus;

@Repository
public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus, Long>{

}
