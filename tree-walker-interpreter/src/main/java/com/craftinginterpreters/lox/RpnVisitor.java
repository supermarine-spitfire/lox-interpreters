package com.craftinginterpreters.lox;

public class RpnVisitor implements Expr.Visitor<String> {
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        // Access order: left, right, operator.
        return traverse(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return traverse(null, expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return traverse(expr.operator.lexeme, expr.right);
    }

    private String traverse(String nodeVal, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        for (Expr expr : exprs) {
            builder.append(expr.accept(this));
            builder.append((exprs.length == 2) ? " " : ""); // Only binary expressions require spaces in them.
        }
        builder.append((nodeVal != null) ? nodeVal : "");   // Only print out the literal expressions' values.

        return builder.toString();
    }

    public static void main(String[] args) {
        // Encode the expression "(1 + 2) * (4 - 3)".
        Expr expression = new Expr.Binary(
                new Expr.Grouping(
                        new Expr.Binary(
                                new Expr.Literal("1"),
                                new Token(TokenType.PLUS, "+", null, 1),
                                new Expr.Literal("2"))),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Binary(
                                new Expr.Literal("4"),
                                new Token(TokenType.MINUS, "-", null, 1),
                                new Expr.Literal("3"))));

        System.out.println(new RpnVisitor().print(expression));
    }
}
