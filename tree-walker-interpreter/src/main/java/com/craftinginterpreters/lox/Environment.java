package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>(); // Stores the bindings.
    private final List<String> undefinedValues = new ArrayList<>();

    /* For a global scope's environment. */
    Environment() {
        enclosing = null;
    }

    /* For a local scope's environment. */
    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    /* Returns the variable's value. */
    Object get(Token name) {
        if (undefinedValues.contains(name.lexeme)) {
            throw new RuntimeError(name,
               "Variable '" + name.lexeme + "' might not have been initialised.");
        }

        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        // Recursively search for the variable.
        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,
            "Undefined variable '" + name.lexeme + "'.");
    }

    /* Assigns a new value to an existing variable. */
    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (undefinedValues.contains(name.lexeme)) {
            undefinedValues.remove(name.lexeme);
            values.put(name.lexeme, value);
            return;
        }

        // Recursively search for the variable.
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name,
            "Undefined variable '" + name.lexeme + "'.");
    }

    /* Binds a new variable name to a value. */
    void define(String name, Object value) {
        if (value == null) {
            undefinedValues.add(name);
        } else {
            values.put(name, value);
        }
    }
}
