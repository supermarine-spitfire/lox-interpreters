package com.craftinginterpreters.lox;

public class Continue extends RuntimeException {
    Continue() {
        // Not reporting an error, so disable error-specific JVM functions.
        super(null, null, false, false);
    }
}
