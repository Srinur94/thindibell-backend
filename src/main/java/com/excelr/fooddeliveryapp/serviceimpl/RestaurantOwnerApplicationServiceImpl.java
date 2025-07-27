package com.excelr.fooddeliveryapp.serviceimpl;

import com.excelr.fooddeliveryapp.dto.RestaurantOwnerApplicationRequestDTO;
import com.excelr.fooddeliveryapp.dto.RestaurantOwnerApplicationResponseDTO;
import com.excelr.fooddeliveryapp.entity.RestaurantOwnerApplication;
import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.ApplicationStatus;
import com.excelr.fooddeliveryapp.enums.Role;
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.RestaurantOwnerApplicationRepository;
import com.excelr.fooddeliveryapp.repository.UserRepository;
import com.excelr.fooddeliveryapp.service.RestaurantOwnerApplicationService;
import com.excelr.fooddeliveryapp.service.UserService; // To update user role
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantOwnerApplicationServiceImpl implements RestaurantOwnerApplicationService {

    private final RestaurantOwnerApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final UserService userService; // Inject UserService to update user roles

    private RestaurantOwnerApplicationResponseDTO convertToDto(RestaurantOwnerApplication application) {
        // Safely get applicant and reviewer names
        String applicantName = application.getApplicant() != null ? application.getApplicant().getName() : null;
        String applicantEmail = application.getApplicant() != null ? application.getApplicant().getEmail() : null;
        String reviewedByAdminName = application.getReviewedBy() != null ? application.getReviewedBy().getName() : null;

        return RestaurantOwnerApplicationResponseDTO.builder()
                .id(application.getId())
                .applicantId(application.getApplicant().getId())
                .applicantName(applicantName)
                .applicantEmail(applicantEmail)
                .proposedRestaurantName(application.getProposedRestaurantName())
                .businessRegistrationNumber(application.getBusinessRegistrationNumber())
                .contactPhone(application.getContactPhone())
                .contactEmail(application.getContactEmail())
                .additionalNotes(application.getAdditionalNotes())
                .status(application.getStatus().name())
                .applicationDate(application.getApplicationDate())
                .reviewDate(application.getReviewDate())
                .reviewedByAdminId(application.getReviewedBy() != null ? application.getReviewedBy().getId() : null)
                .reviewedByAdminName(reviewedByAdminName)
                .adminComments(application.getAdminComments())
                .build();
    }

    @Override
    @Transactional
    public RestaurantOwnerApplicationResponseDTO submitApplication(RestaurantOwnerApplicationRequestDTO requestDTO) {
        User applicant = userRepository.findById(requestDTO.getApplicantId())
                .orElseThrow(() -> new ResourceNotFoundException("Applicant user not found with ID: " + requestDTO.getApplicantId()));

        // Check if user already has a pending application
        if (applicationRepository.existsByApplicantIdAndStatus(applicant.getId(), ApplicationStatus.PENDING)) {
            throw new IllegalArgumentException("User with ID " + applicant.getId() + " already has a pending application.");
        }

        // Check if user is already a restaurant owner
        if (applicant.getRole().equals(Role.RESTAURANT_OWNER)) {
            throw new IllegalArgumentException("User with ID " + applicant.getId() + " is already a Restaurant Owner.");
        }

        RestaurantOwnerApplication application = RestaurantOwnerApplication.builder()
                .applicant(applicant)
                .proposedRestaurantName(requestDTO.getProposedRestaurantName())
                .businessRegistrationNumber(requestDTO.getBusinessRegistrationNumber())
                .contactPhone(requestDTO.getContactPhone())
                .contactEmail(requestDTO.getContactEmail())
                .additionalNotes(requestDTO.getAdditionalNotes())
                .status(ApplicationStatus.PENDING)
                .applicationDate(LocalDateTime.now())
                .build();

        RestaurantOwnerApplication savedApplication = applicationRepository.save(application);
        return convertToDto(savedApplication);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantOwnerApplicationResponseDTO> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantOwnerApplicationResponseDTO> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantOwnerApplicationResponseDTO getApplicationById(Long id) {
        RestaurantOwnerApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));
        return convertToDto(application);
    }

    @Override
    @Transactional
    public RestaurantOwnerApplicationResponseDTO approveApplication(Long applicationId, Long adminId, String adminComments) {
        RestaurantOwnerApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        if (!application.getStatus().equals(ApplicationStatus.PENDING)) {
            throw new IllegalArgumentException("Application is not in PENDING status and cannot be approved.");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with ID: " + adminId));
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("User with ID " + adminId + " is not an ADMIN.");
        }

        application.setStatus(ApplicationStatus.APPROVED);
        application.setReviewDate(LocalDateTime.now());
        application.setReviewedBy(admin);
        application.setAdminComments(adminComments);

        // CRITICAL: Update the applicant's role to RESTAURANT_OWNER
        userService.updateUserRole(application.getApplicant().getId(), Role.RESTAURANT_OWNER);

        RestaurantOwnerApplication updatedApplication = applicationRepository.save(application);
        return convertToDto(updatedApplication);
    }

    @Override
    @Transactional
    public RestaurantOwnerApplicationResponseDTO rejectApplication(Long applicationId, Long adminId, String adminComments) {
        RestaurantOwnerApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        if (!application.getStatus().equals(ApplicationStatus.PENDING)) {
            throw new IllegalArgumentException("Application is not in PENDING status and cannot be rejected.");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with ID: " + adminId));
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("User with ID " + adminId + " is not an ADMIN.");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        application.setReviewDate(LocalDateTime.now());
        application.setReviewedBy(admin);
        application.setAdminComments(adminComments);

        // No role change needed for rejection

        RestaurantOwnerApplication updatedApplication = applicationRepository.save(application);
        return convertToDto(updatedApplication);
    }
}
