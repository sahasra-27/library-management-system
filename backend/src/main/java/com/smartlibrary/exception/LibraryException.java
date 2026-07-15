package com.smartlibrary.exception;

import org.springframework.http.HttpStatus;

public class LibraryException extends RuntimeException {
    private final HttpStatus status;

    public LibraryException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
