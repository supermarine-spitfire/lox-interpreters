package com.craftinginterpreters.lox;

class RuntimeError extends RuntimeException {
    final Token token;  // Used to identify where the error came from.

    RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
