package com.challenge.bandsapi.exception;

public class BandNotFoundException extends RuntimeException {

    public BandNotFoundException(String identifier) {
        super("Band not found: " + identifier);
    }
}

