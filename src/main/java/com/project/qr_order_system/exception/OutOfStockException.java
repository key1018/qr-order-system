package com.project.qr_order_system.exception;

/**
 * 재고 부족 Exception
 */
public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}
