package com.edu.moneywayapi.domain.exception;

import java.util.NoSuchElementException;

public class NoSuchUserException extends NoSuchElementException {
    public NoSuchUserException(String s) {
        super(s);
    }
}
