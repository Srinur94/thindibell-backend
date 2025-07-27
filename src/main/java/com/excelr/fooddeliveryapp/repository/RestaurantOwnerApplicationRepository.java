package com.excelr.fooddeliveryapp.repository;

import com.excelr.fooddeliveryapp.entity.RestaurantOwnerApplication;
import com.excelr.fooddeliveryapp.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantOwnerApplicationRepository extends JpaRepository<RestaurantOwnerApplication, Long> {
    // Find applications by status
    List<RestaurantOwnerApplication> findByStatus(ApplicationStatus status);

    // Find a pending application by user ID
    Optional<RestaurantOwnerApplication> findByApplicantIdAndStatus(Long applicantId, ApplicationStatus status);

    // Check if a user already has a pending application
    boolean existsByApplicantIdAndStatus(Long applicantId, ApplicationStatus status);
}
