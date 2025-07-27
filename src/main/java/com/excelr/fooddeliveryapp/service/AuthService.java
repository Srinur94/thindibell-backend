package com.excelr.fooddeliveryapp.service;

import com.excelr.fooddeliveryapp.dto.UserRegisterRequestDTO;
import com.excelr.fooddeliveryapp.dto.auth.AuthRequest;
import com.excelr.fooddeliveryapp.dto.auth.AuthResponse;

public interface AuthService {

	AuthResponse register(UserRegisterRequestDTO request);

	AuthResponse authentic(AuthRequest request);

	AuthResponse googleLogin(String idTokenString);
}
