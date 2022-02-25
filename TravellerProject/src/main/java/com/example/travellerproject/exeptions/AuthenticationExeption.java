package com.example.travellerproject.exeptions;

public class AuthenticationExeption extends RuntimeException{
    public AuthenticationExeption(String message) {
        super(message);
    }
}
