package com.audition.common.exception;

import java.io.Serial;

public class ItemNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(Exception  message) {
        super(message);
    }
}
