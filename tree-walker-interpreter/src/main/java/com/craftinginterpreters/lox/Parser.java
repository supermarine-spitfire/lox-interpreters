package com.craftinginterpreters.lox;

import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException {} // Sentinel class used to unwind the parser.

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /* Starting point for parsing. */
    Expr parse() {
        try {
            return  expression();
        } catch (ParseError error) {
            return null;
        }
    }

    /*
     * Equivalent to the production:
     * expression -> equality ;
     */
    private Expr expression() {
        return equality();
    }

    /*
     * Equivalent to the production
     * equality -> comparison ( ( "!=" | "==" ) comparison )* ;
     */
    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /*
     * Equivalent to the production
     * comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
     */
    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /*
     * Equivalent to the production
     * term -> factor ( ( "-" | "+" ) factor )* ;
     */
    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /*
     * Equivalent to the production
     * factor -> unary ( ( "/" | "*" ) unary )* ;
     */
    private Expr factor() {
        Expr expr = unary();

        while(match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /*
     * Equivalent to the production
     * unary -> ( "!" | "-" ) unary | primary ;
     */
    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    /*
     * Equivalent to the production
     * primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
     */
    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    /* Determines if the next token is one of those specified. */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();  // Consume the token.
                return true;
            }
        }
        return false;
    }

    /* Similar to match() but throws errors if it fails to find a match. */
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    /*
     * Checks if the current token is of the given type.
     * The token is not consumed.
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    /* Consumes and returns the current token. */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    /* Checks if parser ran out of tokens. */
    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    /* Returns current token yet to be consumed. */
    private Token peek() {
        return tokens.get(current);
    }

    /* Returns the most recently consumed token. */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /* Reports an error at a given token by providing the token's location and the token itself. */
    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    /* Discards tokens until a statement boundary is identified. */
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }
}
