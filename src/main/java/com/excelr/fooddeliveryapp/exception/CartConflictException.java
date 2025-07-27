package com.excelr.fooddeliveryapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CartConflictException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public CartConflictException(String message) {
        super(message);
    }
    
    public CartConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}