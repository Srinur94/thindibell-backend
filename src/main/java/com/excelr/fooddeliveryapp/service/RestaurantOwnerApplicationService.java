package com.excelr.fooddeliveryapp.service;

import com.excelr.fooddeliveryapp.dto.RestaurantOwnerApplicationRequestDTO;
import com.excelr.fooddeliveryapp.dto.RestaurantOwnerApplicationResponseDTO;
import com.excelr.fooddeliveryapp.enums.ApplicationStatus;

import java.util.List;

public interface RestaurantOwnerApplicationService {
    RestaurantOwnerApplicationResponseDTO submitApplication(RestaurantOwnerApplicationRequestDTO requestDTO);
    List<RestaurantOwnerApplicationResponseDTO> getAllApplications();
    List<RestaurantOwnerApplicationResponseDTO> getApplicationsByStatus(ApplicationStatus status);
    RestaurantOwnerApplicationResponseDTO getApplicationById(Long id);
    RestaurantOwnerApplicationResponseDTO approveApplication(Long applicationId, Long adminId, String adminComments);
    RestaurantOwnerApplicationResponseDTO rejectApplication(Long applicationId, Long adminId, String adminComments);
}
