package com.excelr.fooddeliveryapp.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetailsDTO {
 private LocalDateTime timestamp;
 private String message;
 private String details;
}