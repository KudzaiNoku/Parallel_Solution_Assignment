#MonteCarloMinimizationParallel

THe Java source files should be saved in a "src" directory and the compiled class files saved in a "bin" directory.

To execute the code using the provided makefile, follow these steps:

1. Type "make" to compile the java files and save the compiled class files in a bin directory
2. Type "make run ARGS=" followed by 7 arguments, seperated by spaces, to run the MonteCarloMinimizationParallel program.
For example,to run the program with specific arguments, type:
make run ARGS="100 100 -50 50 -50 50 0.5"
In the case that the Makefile fails to work, follow these steps:

1. To compile the code, type the command "javac -d bin src/MonteCarloMini/*.java"
2. To run the code, type the command "java -cp bin MonteCarloMini.MonteCarloMinimizationParallel",
followed by the 7 arguments.

For example, to run the program with specific arguments, type:
java -cp bin MonteCarloMini.MonteCarloMinimizationParallel 100 100 -50 50 -50 50 0.5
