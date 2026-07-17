package com.matrob.urlcondenser.infra.exception;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String message) {
        super(message);
    }

}