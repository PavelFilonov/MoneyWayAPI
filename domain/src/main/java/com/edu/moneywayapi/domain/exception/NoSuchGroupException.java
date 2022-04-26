package com.edu.moneywayapi.domain.exception;

import java.util.NoSuchElementException;

public class NoSuchGroupException extends NoSuchElementException {
    public NoSuchGroupException(String s) {
        super(s);
    }
}
