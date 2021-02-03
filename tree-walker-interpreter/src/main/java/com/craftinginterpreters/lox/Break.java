package com.craftinginterpreters.lox;

public class Break extends RuntimeException {
    Break() {
        // Not reporting an error, so disable error-specific JVM functions.
        super(null, null, false, false);
    }
}
