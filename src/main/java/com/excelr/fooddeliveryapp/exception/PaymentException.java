package com.excelr.fooddeliveryapp.exception;

public class PaymentException extends RuntimeException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 8479435679719317722L;

	public PaymentException(String message) {
        super(message);
    }
    
    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}