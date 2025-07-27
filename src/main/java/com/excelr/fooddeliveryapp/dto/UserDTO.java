package com.excelr.fooddeliveryapp.dto;

import com.excelr.fooddeliveryapp.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	    private Long id;
	    private String name;
	    private String email;
	    private String role;

}
