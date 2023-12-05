package com.example.demo.exception;

public class WrongCommissionException extends RuntimeException{
    public WrongCommissionException(String message) {
        super(message);
    }
}
