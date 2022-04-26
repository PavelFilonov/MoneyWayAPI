package com.edu.moneywayapi.domain.exception;

import java.util.NoSuchElementException;

public class NoSuchCategoryException extends NoSuchElementException {
    public NoSuchCategoryException(String message) {
        super(message);
    }
}
