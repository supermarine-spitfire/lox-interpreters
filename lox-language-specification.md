## Lox Language Specification
The following is a summary of Chapter 3 of *Crafting Interpreters*.

Lox is a high-level, dynamically typed programming language that takes inspiration from C. It is garbage collected.

### Example program
    // Your first Lox program!
    print "Hello, world!";

### Data Types
* Booleans.
* Numbers (double-precision floating-point only).
* Strings.
* Nil (no value).

### Expressions
#### Arithmetic
Arithmetic operators are available:

    add + me;
    subtract - me;
    multiply * me;
    divide / me;

The `-` operator is also used for negation:

    -negateMe;

#### Comparison and equality
Comparison operators only work on numbers:

    less < than;
    lessThan <= orEqual;
    greater > than;
    greaterThan >= orEqual;

Equality and inequality work on all types:

    1 == 2;         // false.
    "cat" != "dog"; // true.
    314 == "pi";    // false.
    123 == "123";   // false.

#### Logical operators
The not operator (`!`) inverts the value of a boolean value/expression:

    !true;  // false.
    !false; // true.

Lox defines logical conjunction using the `and` operator:

    true and false; // false.
    true and true;  // true.

Logical disjunction uses the `or` operator:

    false or false; // false.
    true or false;  // true.

The `and` and `or` operators short-circuit.

#### Precedence and grouping
Brackets (i.e. `()`) are used for grouping. Precedence and associativity works like C.

### Statements
Lox has three types of statements: print statements, expression statements, and block statements.

#### Print statements
Lox incorporates output functionality into the language itself. The `print` statement evaluates a single expression and displays the result to the user:

    print "Hello, world";   // Displays "Hello, world!" to the user.

#### Expression statements
Expressions followed by a semicolon (`;`) are called expression statements:

    "some expression";
    1 + 3;

#### Block statements
Multiple expressions can be grouped into a single expression by wrapping them in a block:

    {
        print "One statement.";
        print "Two statements.";
    }

### Variables
Variables are declared using `var` statements. Uninitialised variables' values default to `nil`:

    var imAVariable = "here is my value";
    var iAmNil;

Variable access, assignment, and scope works like Java and C.

### Control Flow
`if` statements implement conditional statements:

    if (condition) {
        print "yes";
    } else {
        print "no";
    }

`while` loops execute their body so long as their condition expression evaluates to true:

    var a = 1;
    while (a < 10) {
        print a;
        a = a + 1;
    }

`for` loops execute their body a certain number of times:

    for (var a = 1; a < 10; a = a + 1) {
        print a;
    }

### Functions
Function call expressions work like in C. Trailing brackets are mandatory for calling functions without arguments.

Defining functions is done with `fun`:

    fun printSum(a, b) {
        print a + b;
    }

A function body is always a block. To return values from a function, use a `return` statement:

    fun returnSum(a, b) {
        return a + b;
    }

If no return is provided, a function implicitly returns `nil`.

#### Closures
Functions are first class in Lox:

    fun addPair(a, b) {
        return a + b;
    }

    fun identity(a) {
        return a;
    }

    print identity(addPair)(1, 2);  // Prints "3".

Local functions may be declared inside another function:

    fun outerFunction() {
        fun localFunction() {
            print "I'm local!";
        }

        localFunction();
    }

Closures are possible in Lox:

    fun returnFunction() {
        var outside = "outside";

        fun inner() {
            print outside;
        }

        return inner;
    }

    var fn = returnFunction();
    fn();   // Prints "outside".

### Classes
Consider the following example class:

    class Breakfast {
        cook() {
            print "Eggs a-fryin'!;
        }

        serve(who) {
            print "Enjoy your breakfast, " + who + ".";
        }
    }

A class's body contains its methods. Methods are defined similarly to functions, just without the `fun` keyword.

Classes are first class in Lox:

    // Store it in variables.
    var someVariable = Breakfast;

    // Pass it to functions.
    someFunction(Breakfast);

#### Instantiation and initialisation
Creating instances is reminiscent of Python:

    var breakfast = Breakfast();
    print breakfast;    // "Breakfast instances".

Lox allows properties to be added onto objects:

    breakfast.meat = "sausage";
    breakfast.bread = "sourdough";

A field is created in the object if it does not already exist.

The `this` keyword allows fields and methods associated with the current object to be accessed:

    class Breakfast {
        serve(who) {
            print "Enjoy your " + this.meat + " and " +
                    this.bread + ", " + who + ".";
        }

        // ...
    }

Classes can define an initializer using the `init()` method:

    class Breakfast {
        init(meat, bread) {
            this.meat = meat;
            this.bread = bread;
        }

        // ...
    }

    var baconAndToast = Breakfast("bacon", "toast");
    baconAndToast.serve("Dear Reader");
    // "Enjoy your bacon and toast, Dear Reader."

#### Inheritance
When defining a class, the less-than operator (`<`) specifies a class from which it inherits:

    class Brunch < Breakfast {
        drink() {
            print "How about a Bloody Mary?";
        }
    }

All methods defined in a superclass/base class are available in its subclasses/derived classes:

    var benedict = Brunch("ham", "English muffin");
    benedict.serve("Noble Reader");
    // "Enjoy your ham and English muffin, Noble Reader."

The `super` keyword allows access to a superclass's fields and methods:

    class Brunch < Breakfast {
        init(meat, bread, drink) {
            super.init(meat, bread);
            this.drink = drink;
        }
    }

Like Java, primitive types are *not* objects.

### The Standard Library
Only one function is provided: `clock()`, which returns the number of seconds since the program started.


## Lox Grammar
### Syntax Grammar
    program        → declaration* EOF ;

    declaration    → classDecl
                   | funDecl
                   | varDecl
                   | statement ;
    classDecl      → "class" IDENTIFIER ( "<" IDENTIFIER )?
                     "{" function* "}" ;
    funDecl        → "fun" function ;
    varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;

    statement      → exprStmt
                   | forStmt
                   | ifStmt
                   | printStmt
                   | returnStmt
                   | whileStmt
                   | block ;

    exprStmt       → expression ";" ;
    forStmt        → "for" "(" ( varDecl | exprStmt | ";" )
                               expression? ";"
                               expression? ")" statement ;
    ifStmt         → "if" "(" expression ")" statement
                     ( "else" statement )? ;
    printStmt      → "print" expression ";" ;
    returnStmt     → "return" expression? ";" ;
    whileStmt      → "while" "(" expression ")" statement ;
    block          → "{" declaration* "}" ;
 
    expression     → assignment ;
    assignment     → ( call "." )? IDENTIFIER "=" assignment
                   | logic_or ;
    
    logic_or       → logic_and ( "or" logic_and )* ;
    logic_and      → equality ( "and" equality )* ;
    equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term           → factor ( ( "-" | "+" ) factor )* ;
    factor         → unary ( ( "/" | "*" ) unary )* ;
    
    unary          → ( "!" | "-" ) unary | call ;
    call           → primary ( "(" arguments? ")" | "." IDENTIFIER )* ;
    primary        → "true" | "false" | "nil" | "this"
                   | NUMBER | STRING | IDENTIFIER | "(" expression ")"
                   | "super" "." IDENTIFIER ;

    function       → IDENTIFIER "(" parameters? ")" block ;
    parameters     → IDENTIFIER ( "," IDENTIFIER )* ;
    arguments      → expression ( "," expression )* ;

### Lexical Grammar
    NUMBER         → DIGIT+ ( "." DIGIT+ )? ;
    STRING         → "\"" <any char except "\"">* "\"" ;
    IDENTIFIER     → ALPHA ( ALPHA | DIGIT )* ;
    ALPHA          → "a" ... "z" | "A" ... "Z" | "_" ;
    DIGIT          → "0" ... "9" ;
