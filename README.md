# CFM
CFM is a foundational mathematics tutorial program to present content and to administer practice problems and assessment.  
CFM consists of several components, including an opening/login screen, main menu, scenario reader, practicer, data analyzer, etc.
The AI component, the "Algebra Interpreter," represents the main processing part of the CFM project that can determine solutions
for equations or simplified versions of fairly complicated algebra expressions.

The current repository called Factory includes files to exhibit a different way of arranging the AI code using objects called 
"Expression"s that have the original string from which the expression was derived, the type of expression, the terms stored in 
an array Terms[], and the answer (solution) if this was an equation.

ExprFact.java is an interface that runs several tests using the new Expression factory approach and serves as the super interface 
for regularExprFact.java and equatExprFact.java, which are selected by ExprFactSelector.java that is called in ExprFact.java.
