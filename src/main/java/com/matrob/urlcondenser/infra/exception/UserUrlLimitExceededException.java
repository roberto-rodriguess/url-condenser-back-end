package com.matrob.urlcondenser.infra.exception;

public class UserUrlLimitExceededException extends RuntimeException {

    public UserUrlLimitExceededException(String message) {
        super(message);
    }

}
