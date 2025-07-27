package com.excelr.fooddeliveryapp.service;

import java.util.List;

import com.excelr.fooddeliveryapp.dto.UserDTO;
import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.Role;

				public interface UserService {
					
				UserDTO getUserById(Long Id);

				List<UserDTO >getAllUsers();

				UserDTO updateUser(Long Id, UserDTO userDTO);

				void deleteUser(Long Id);

				UserDTO getCurrentUser(); // get details of the authenticated user

				UserDTO updateUserRole(Long userId, Role newRole);
				
				List<UserDTO> findUsersByRole(Role role);

}
