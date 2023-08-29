package com.enigma.tokonyadia.exception;

import lombok.Getter;

public class BadRequestException extends RuntimeException{
    private Object errors;
    public BadRequestException(Object errors) {
        this.errors = errors;
    }

    public Object getErrors() {
        return errors;
    }
}
