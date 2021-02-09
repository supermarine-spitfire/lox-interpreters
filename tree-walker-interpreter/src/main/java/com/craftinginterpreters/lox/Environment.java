package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>(); // Stores the bindings.

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
        values.put(name, value);
    }

    /* Returns the variable's value, given the distance to its scope. */
    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    /* Assigns a new value to an existing variable, given the distance to its scope. */
    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }

    /* Returns the environment `distance` number of hops above the current one. */
    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }
}
