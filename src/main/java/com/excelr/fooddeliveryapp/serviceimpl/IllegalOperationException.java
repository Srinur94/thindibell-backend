package com.excelr.fooddeliveryapp.serviceimpl;

public class IllegalOperationException extends RuntimeException {
    public IllegalOperationException(String message) {
        super(message);
    }
}