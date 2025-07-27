package com.excelr.fooddeliveryapp.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excelr.fooddeliveryapp.dto.UserDTO;
import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.Role;
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.UserRepository;
import com.excelr.fooddeliveryapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        // Do not update password here directly, use a separate method for password change
        existingUser.setRole(Role.valueOf(userDto.getRole()));
 // Role updates might need admin privileges

        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getCurrentUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
        return convertToDto(user);
    }



    private UserDTO convertToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

//    private User convertToEntity(UserDTO userDto) {
//        return User.builder()
//                .id(userDto.getId())
//                .name(userDto.getName())
//                .email(userDto.getEmail())
//            .role(userDto.getRole())
//              // Password is not part of UserDto for security reasons during general retrieval/update
//             .build();
   // }

	

	 @Override
	    @Transactional
	    public UserDTO updateUserRole(Long userId, Role newRole) {
	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
	        user.setRole(newRole);
	        User updatedUser = userRepository.save(user);
	        return convertToDto(updatedUser);
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public List<UserDTO> findUsersByRole(Role role) {
	        return userRepository.findByRole(role).stream()
	                .map(this::convertToDto)
	                .collect(Collectors.toList());
	    }
		
	}	

