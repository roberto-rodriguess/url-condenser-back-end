package com.matrob.urlcondenser.infra.exception;

public class DuplicateUrlException extends RuntimeException {

    public DuplicateUrlException(String message) {
        super(message);
    }

}
