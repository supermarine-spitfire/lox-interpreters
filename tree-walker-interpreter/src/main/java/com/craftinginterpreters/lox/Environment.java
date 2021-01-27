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

    /* Assigns a new value to an existing variable. */
    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        throw new RuntimeError(name,
            "Undefined variable '" + name.lexeme + "'.");
    }

    /* Binds a new variable name to a value. */
    void define(String name, Object value) {
        values.put(name, value);
    }
}
