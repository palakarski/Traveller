package com.example.travellerproject.exeptions;

public class UnauthorizedExeption extends RuntimeException{
    public UnauthorizedExeption(String message) {
        super(message);
    }
}
