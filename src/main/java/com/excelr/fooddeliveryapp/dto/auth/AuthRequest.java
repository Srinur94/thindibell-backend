package com.excelr.fooddeliveryapp.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
	//Auth request is for login

	@NotBlank(message="mail id is required")
	@Email(message="Inavalid mail format")
	private String email;

	@NotBlank(message = "Password is requireed")
	private String password;

}
