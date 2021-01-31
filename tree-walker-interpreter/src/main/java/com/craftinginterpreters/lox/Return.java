package com.craftinginterpreters.lox;

public class Return extends RuntimeException {
    final Object value;

    Return(Object value) {
        // Not reporting an error, so disable error-specific JVM functions.
        super(null, null, false, false);
        this.value = value;
    }
}
