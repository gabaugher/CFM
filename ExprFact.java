package cfm_files;

import java.util.Scanner;

import cfm_files.AI.PrepExpr;

public interface ExprFact {  // Abstract factory interface
	
	Expression createExpr(String preparedString);  //  Just needed so that it can be overrode
	
	public static void EFTestExprFact() {		
		int i;
		boolean passed = false, allPassed = true; 
		String ANSI_RED = "\u001B[31m";
		String ANSI_BLACK = "\u001B[30m";
		String[] testName = { "Multiple Signs/Extra Characters", "Multiple Variables", "Expression with 0 Coefficients", "Combine Like Terms - No Parentheses", "Single Parentheses", "Double Parentheses", "Nested Parentheses",
				"Adjacent Parentheses", "Linear Equation", "Quadratic Equation" };
		String[] origExpr = { "-&-2+-@-3x-+(2x++6)-", "4x - 2y + 6 - x + y^2 + y -3","8x - 5 + 0x + 2x^2 - 0 - 7x - 0x^2 +9", "5x - 2x^2 + 4 - 7x + x^2 +1 - 6x^3", "3x+-5(3x-6+4x^2-5x-7)2-5-", "3x+1x(3x-6+4)+5-(2x^2-5x-7)-5--7x", 
				"-3x+x(3x-6+4(2x^2-5x-7)--5)-7x", "--3x+1x(3x-6+4)(2x^2-5x-7)-5-7x", "-3( 2x - 5 ) -7( x + 4 ) = 5( -3x - 2 ) + 9",
				"3x( 2x - 5 ) - 7( x^2 + 4x - 3 ) = -5x( 3x - 2 ) + 9x^2" };
		String[] answer = { "1.0x^1-4.0", "1.0y^2+3.0x^1-1.0y^1+3.0", "2.0x^2+1.0x^1+4.0", "-6.0x^3-1.0x^2-2.0x^1+5.0", "-40.0x^2+23.0x^1+125.0", "1.0x^2+13.0x^1+7.0", "8.0x^3-17.0x^2-39.0x^1",
				"6.0x^4-19.0x^3-11.0x^2+10.0x^1-5.0", "6.0", "10.188,0.412"};
		
		System.out.println( "        E F  T E S T  : \n" );
		
		ExprFact exprFact = ExprFactSelector.getFactory( origExpr[0] );  
		Expression exp = exprFact.createExpr( PrepExpr.prepareExpOrEq( origExpr[0] ) ); 
		System.out.println( "exp.terms = " + convertTermsToStr( exp ) ); 
		passed = passTest( exp, testName[0], answer[0], 0);
		if (!passed) allPassed = false; 
		
		for ( i = 1; i < testName.length; i++ ) {			
			passed = false;
			exprFact = ExprFactSelector.getFactory( origExpr[i] );  
			exp = exprFact.createExpr( PrepExpr.prepareExpOrEq( origExpr[i] ) ); 	
			passed = passTest( exp, testName[i], answer[i], i);		
			if (!passed) allPassed = false;
			}		
		System.out.println( "All Tests Passed = " + ANSI_RED + allPassed + ANSI_BLACK + "\n\n");
				
	};
	
	public static boolean passTest( Expression exp, String testName, String answer, int i ) {
		boolean passed = false;
		String finalExpr; 
		String ANSI_RED = "\u001B[31m";
		String ANSI_BLACK = "\u001B[30m";	
		
		System.out.println( "TEST: " + testName );
						
		if (exp.originalType == "equation") finalExpr = exp.answer;
		else finalExpr = convertTermsToStr( exp );
		if ( finalExpr.compareTo( answer ) == 0) passed = true;
		
		System.out.println( "Original expression: " +  convertTermsToStr( exp ) + "   Simplified expression: " + finalExpr + "   Correct Answer: " + answer );								
		System.out.println( "TEST: " + testName + "  Passed = " + ANSI_RED + passed + ANSI_BLACK + "\n" );	
		return passed; 
		}
	
	public static String convertTermsToStr( Expression expression ) {
		String result = PrepExpr.prepareExpOrEq( AI.convertToStr( expression.terms )); 
		return result;
		}
	
	public static void main(String[] args) {
		
		EFTestExprFact(); 
		
		// Input original expression
		System.out.println("Enter your expression or equation: (only one variable & up to 2 sets of parentheses allowed)");
		try (Scanner sc = new Scanner( System.in )) {
			String origText = ""; 
			try { origText = sc.nextLine(); }
			catch (Exception e) { e.printStackTrace(); }
			// The method prepareExpOrEq removes spaces, double signs, leading or trailing unnecessary signs, detects unacceptable characters, etc. and determines
			// whether or not the inputed string is an equation 
	
			ExprFact exprFact = ExprFactSelector.getFactory( origText );  
			Expression exp1 = exprFact.createExpr( PrepExpr.prepareExpOrEq( origText ) ); 	
			System.out.print( "String: " + exp1.string + "  Type: " + exp1.originalType + "  Terms: "); 
			ExpressionFactory.displayTerms( exp1 );
			} 	
	}

}
