# lox-interpreters
Follows the _Crafting Interpreters_ book.

# Lox Language Specification

## Grammar

    program -> declaration* EOF ;
    declaration -> varDecl | statement ;
    statement -> exprStmt | printStmt | block ;
    block ->  "{" declaration* "}" ;
    printStmt -> "print" expression ";" ;
    varDecl -> "var" IDENTIFIER ( "=" expression )? ";" ;
    exprStmt -> expression ";"? ;
    expression -> assignment ;
    assignment -> IDENTIFIER "=" assignment | equality ;
    equality -> comparison ( ( "!=" | "==" ) comparison )* ;
    comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term -> factor ( ( "-" | "+" ) factor )* ;
    factor -> unary ( ( "/" | "*" ) unary )* ;
    unary -> ( "!" | "-" ) unary | primary ;
    primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER ;
