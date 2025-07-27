package com.excelr.fooddeliveryapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.excelr.fooddeliveryapp.entity.Reviews;

@Repository
public interface ReviewRepository extends JpaRepository<Reviews, Long> {

}
