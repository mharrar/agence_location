package com.example.location_voiture.exceptions.agence;

public class AgenceNotFoundException extends RuntimeException {
    public AgenceNotFoundException(String message) {
        super(message);
    }
}
