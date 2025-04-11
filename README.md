**Project 5 - Garbage Collection**
Rita Brokhman

Files Submitted:

Main.java – Provided by Tim Carpenter. Initializes the program, sets up input reading, and launches the parser and execution.

Scanner.java – Provided by Tim Carpenter. Tokenizes the .code file input for parsing.

Core.java – Provided by Tim Carpenter. Defines the core token types used in parsing and execution.

Parser.java – Provided by Tim Carpenter. Parses the tokenized input into a tree-like structure based on Core grammar.

Procedure.java – Provided by Tim Carpenter. Parses procedures and ensures their structure is correct.

DeclSeq.java – Provided by Tim Carpenter. Handles sequences of variable and function declarations.

Decl.java – Provided by Tim Carpenter. Abstract class for variable declarations.

DeclObj.java – Provided by Tim Carpenter. Parses and stores object declarations.

DeclInteger.java – Provided by Tim Carpenter. Parses and stores integer declarations.

StmtSeq.java – Provided by Tim Carpenter. Parses sequences of executable statements.

Stmt.java – Provided by Tim Carpenter. Parses and identifies the type of statement to be executed.

Assign.java – Provided by Tim Carpenter. Handles assignment statements including object operations.

Print.java – Provided by Tim Carpenter. Handles print statements that output integer values.

Read.java – Provided by Tim Carpenter. Reads integer input during program execution.

If.java – Provided by Tim Carpenter. Handles conditional logic and branching.

Loop.java – Provided by Tim Carpenter. Handles while loops.

Cond.java – Provided by Tim Carpenter. Parses conditions for control structures.

Cmpr.java – Provided by Tim Carpenter. Parses and evaluates comparison expressions.

Expr.java – Provided by Tim Carpenter. Handles arithmetic expressions.

Term.java – Provided by Tim Carpenter. Handles multiplication and division operations.

Factor.java – Provided by Tim Carpenter. Evaluates constants, variables, and nested expressions.

Memory.java – Modified. Manages variable storage and scoping. Extended to implement reference-counting garbage collection. Tracks object references and prints "gc:n" when the number of reachable objects changes.

Executor.java – Provided by Tim Carpenter. Executes parsed program logic and delegates memory handling.

Id.java – Provided by Tim Carpenter. Represents and evaluates identifiers (variable names).

Function.java – Provided by Tim Carpenter. Parses and executes user-defined functions with parameter handling and recursive call support.

Call.java – Provided by Tim Carpenter. Handles execution of procedure calls, including argument evaluation and stack management.

Parameter.java – Provided by Tim Carpenter. Parses and manages procedure parameters.

tester.sh – Provided by Tim Carpenter. Automates batch testing using predefined .code and .data files.

Correct Folder – Provided by Tim Carpenter. Contains valid test cases and their expected output.

Special Features:

The interpreter now includes reference-counting garbage collection. Each object’s reference count is tracked, and the number of reachable objects is printed whenever this count changes.

The call stack still uses a frame-based design, allowing for recursive and nested procedure calls while maintaining proper scoping.

Reference counts are updated during assignments, scope entry/exit, and procedure calls to ensure correct memory tracking.

Testing:

Testing was performed using tester.sh, which runs all test cases from the Correct/ folder.

Known Bugs:

None