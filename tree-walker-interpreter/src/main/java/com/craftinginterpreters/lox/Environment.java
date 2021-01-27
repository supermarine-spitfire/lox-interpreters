package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
    private final Map<String, Object> values = new HashMap<>(); // Stores the bindings.

    /* Returns the variable's value. */
    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        throw new RuntimeError(name,
            "Undefined variable '" + name.lexeme + "'.");
    }

    /* Binds a new name to a value. */
    void define(String name, Object value) {
        values.put(name, value);
    }
}
