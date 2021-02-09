package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    /* Used to track where the code being visited is defined. */
    private enum FunctionType {
        NONE,
        FUNCTION
    }

    /* Resolves variables in a list of statements. */
    void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    /* Resolves blocks. */
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    /* Resolves expression statements. */
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    /* Resolves functions. */
    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        // Need to bind the function's name.
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    /*
     * Resolves if statements.
     * The resolver needs to go through all branches.
     */
    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    /* Resolves print statements. */
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    /* Resolves variable declarations. */
    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    /*
     * Resolves while loops.
     * The resolver only needs to access the loop's body once.
     */
    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    /* Resolves return statements. */
    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Can't return from top-level code.");
        }

        if (stmt.value != null) {
            resolve(stmt.value);
        }

        return null;
    }

    /* Resolves assignment expressions. */
    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    /* Resolves binary expressions. */
    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    /* Resolves function calls. */
    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);

        for (Expr argument : expr.arguments) {
            resolve(argument);
        }

        return null;
    }

    /* Resolves grouping expressions. */
    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    /* Literal expressions do not contain any variables or subexpressions. */
    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    /*
     * Resolves logical expressions.
     * The resolver needs to access both sides.
     */
    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    /* Evaluates unary expressions. */
    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    /* Resolves variable expressions. */
    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() &&
            scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Lox.error(expr.name,
                    "Can't read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    /* Resolves variables in a statement. */
    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    /* Resolves variables in an expression. */
    private void resolve(Expr expr) {
        expr.accept(this);
    }

    /* Resolves variables in a function's body. */
    private void resolveFunction(
            Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    /* Enters a scope. */
    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    /* Exits a scope. */
    private void endScope() {
        scopes.pop();
    }

    /* Adds a new variable declaration to the innermost scope. */
    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name,
                    "Already variable with this name in this scope.");
        }
        scope.put(name.lexeme, false);  // Variable is still undefined.
    }

    /* Declares a variable as fully initialised. */
    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);   // Variable is now defined.
    }

    /* Helper method for resolving variables. */
    private void resolveLocal(Expr expr, Token name) {
        // Starts at innermost scope and works outward.
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }
}
