//  AI class, a main component of the CFM Project
//  This component is really the brains of the program since it is used to simplify expressions and solve equations
//  and to determine if student answers are algebraically equivalent with the correct answers.  
//  By G. Baugher

package cfm_files;

import java.util.Scanner;

public class AI {

	static String origText, origValue, expression, messages = "";
	static String[] answer = { "", "" };
	static double enteredValue;
	static Term[] overallExpr = initArrayOfTerms();
	static boolean equation = false;

	public static class Term {  //  Creates a class for representing algebraic terms, each having a coefficient, a variable, and an exponent.
		public double coeff; public char variable; public int expon;
		public Term( double coef, char var, int expo )	{
			this.coeff = coef; 
			this.variable = var;
			this.expon = expo; 
			}
		public double getCoeff() { return coeff; }
		public char getVar() { return variable; }
		public int getExpon() { return expon; }
		public void setCoeff(double coef) { coeff = coef; }
		public void setVar(char var) { variable = var; }
		public void setExpon(int expo) { expon = expo; }  
		public Term() { 
			coeff = 0.0; 
			variable = ' '; 
			expon = 0; 
			};	
	}
	
	public static Term initTerm(Term term) {
		term.setCoeff(0.0); 
		term.setVar(' '); 
		term.setExpon(0);	
		return term;
		}
	
	public static Term[] initArrayOfTerms( ) {
		Term[] arrayOfTerms = new Term[9];
		int i;
		for (i=0; i<9; i++) {
			arrayOfTerms[i] = new Term(); 
			initTerm( arrayOfTerms[i] ); 
		}
		return arrayOfTerms;
	}

	public static int numUsedTerms( Term[] terms ) {   //  Used several places to determine the number of used terms in an expression (array of terms)
		int i, num = 0;
		for (i=0; i<9; i++) { if (terms[i].coeff != 0) num += 1; };
		return num;
	}

	public static String elimOrAddSigns( String expression) {  // Eliminates initial plus signs or terminal signs in an expression and also adds 1's before variables
	 int i, len = expression.length(), count = 0;
	 char ch;
	 if (expression.compareTo("+") == 0) { 
		 expression = ""; 
		 len = 0; 
		 messages += "Error - incorrect expression: no algebra terms or numbers in expression/equation. "; 
		 };
	 while ((len > 1) && (count < len)) {
		 // System.out.println( "len:  " + len + "  count:  " + count ); 
		 len = expression.length(); 
		 count+=2;
		 if ((expression.charAt(len-1) == '+') || (expression.charAt(len-1) == '-')) expression = expression.substring(0,len-1);  
		 if (expression.charAt(0) == '+')  expression = expression.substring(1); };
		 ch = expression.charAt(0); 
		 if (Character.isLetter(ch)) expression = "1" + expression;
		 len = expression.length();
		 for ( i = 0; i < len - 2; i++ ) {
		 	ch = expression.charAt(i);
		 	if ((( ch == '+') || (ch == '-')) || ((ch == '(') || (ch == ')')) )
		 		if (Character.isLetter( expression.charAt(i+1) )) expression = expression.substring(0,i+1) + "1" + expression.substring(i+1); };
		 return expression;
	}
	
	public static String removeSpaces( String exp ) {
		
		int i;
		exp = exp.trim();
		while (exp.indexOf(' ') != -1) {
			for(i=0; i<exp.length(); i++) {
				if (exp.charAt(i)==' ') {
					exp = exp.substring(0,i) + exp.substring(i+1); } 
				} 
			};
		return exp;
	}
		
	public static String prepareExpOrEq( String origText ) {  //  Prepares the string in various ways, detects unwanted characters, determines if equation, etc.
		
		String twoChar, tempString, leftSide, rightSide; int i, j, count, location, len; char ch; boolean ok;
		
		// Remove any spaces and switch upper to lower case letters
		expression = removeSpaces( origText ); 
		expression = expression.toLowerCase();
		//  System.out.println( "After removing spaces and conversion to lower case: " + expression ); 
		
		// Detect characters other than numbers, letters, operation signs, equal signs, and parentheses or brackets.
		len = expression.length(); 
		
		i = 0;
		if (len > 0)
			while ( i < len-1 ) {
				ok = false; 
				ch = expression.charAt(i);
				if (Character.isDigit(ch)) ok = true;  
				if (Character.isLetter(ch)) ok = true; 
				if (( ch=='=') || ( ch== '+' )) ok = true;
				if (( ch=='-') || ( ch=='*' )) ok = true;
				if (( ch=='/') || ( ch=='(' )) ok = true;
				if (( ch==')') || ( ch=='[')) ok = true;
				if (( ch==']') || ( ch=='{')) ok = true;
				if (( ch=='}') || ( ch=='^')) ok = true;
				if (( ch=='.' ) || ( ch=='–')) ok = true;
				if (!ok) { 
					messages += "Error - expression has an unacceptable character: " + ch + " (character removed). "; 
					expression = expression.substring(0,i) + expression.substring(i+1); 
					len = expression.length();
					};
				i++; 
			};
					
		// Change ++ to +, +- or -+ to -, and -- to +, if any of these are in the string
		count = 0; 
		tempString = expression;
		while (count < expression.length()) {
			for ( j = 0; j < expression.length() - 1; j++ ) {
				twoChar = expression.substring(j,j+2); 
				ch = '#';
				if (twoChar.compareTo("++") == 0) 
					{ ch = '+'; messages += "Double plus signs should be changed to + only. "; };
				if ((twoChar.compareTo("+-") == 0) || (twoChar.compareTo("-+") == 0)) 
					{ ch = '-'; messages += "+- or -+ should be changed to - only. "; };
				if (twoChar.compareTo("--") == 0) 
					{ ch = '+'; messages += "Double minus signs should be changed to a + sign. "; };
				if (ch != '#') expression = expression.substring(0,j) + ch + expression.substring(j+2);
			};	
			count++; 
			if (tempString.compareTo(expression) == 0) count = 999; 
			tempString = expression; };
			
			// Eliminate initial plus signs or terminal signs in the whole expression or add 1's before variables if no coefficient
			expression = elimOrAddSigns( expression );
					
		// Determine if there are equal signs (and if more than one) and if one, divide the string into left and right side expressions to prep each side
		count = 0; 
		location = 0; 
		equation = false; 
		leftSide = ""; 
		rightSide = "";
		for (i=0; i < expression.length(); i++ )  { 
			if ( expression.charAt(i) == '=' ) {
				count++; location = i; };	
				};
		equation = (count == 1);
		if (count > 1) { messages += "Error - incorrect expression: multiple equal signs. "; }  
		if (equation) { 
			leftSide = expression.substring(0,location); 
			rightSide = expression.substring(location+1); 
			}
		if (equation) System.out.println("Left Side: " + leftSide + "  Right Side: " + rightSide );
	
		// If equation, eliminate initial plus signs or terminal signs in leftSide or rightSide expressions
		if (equation) {
			leftSide = elimOrAddSigns( leftSide );  
			rightSide = elimOrAddSigns( rightSide );
			expression = leftSide + '=' + rightSide;
			};
	
		// System.out.println("Messages: " + messages);
		return expression;
	} // end of prepareExpOrEq
	
	public static Term[] determineExpr( String origExpr, int beginPos, int endPos) {  // Analyzes the string character by character and converts to an array of Terms
		boolean nowExpon = false, prevIsDigit = false, prevIsLetter = false;          // (assuming no parentheses in the expression)
		int i, index, signMultiplier = 1, numOfTerm = 0;
		char ch; String numStr = ""; 
		double number = 0;
		int pos[] = new int[9]; 
		double coeff[] = new double[9]; 
		char variable[] = new char[9]; int expon[] = new int[9];
		for ( index = 0; index < 9; index++ ) {
			pos[index] = 0; 
			coeff[index] = 0; 
			variable[index] = ' '; 
			expon[index] = 0; };
		Term[] exprAsArrayOfTerms = initArrayOfTerms(); 
		
		i = beginPos; 
		ch = origExpr.charAt(i);  
		if ((ch == '+') || ( ch == '-')) 
			{ if (ch == '-') signMultiplier = -1; i++; };
		while ( i < endPos ) {
			ch = origExpr.charAt(i);
			if (!(Character.isDigit(ch) || (ch == '.')) && prevIsDigit) {
				number = Double.parseDouble(numStr);  
				if (nowExpon) { expon[numOfTerm] = (int) number; nowExpon = false; } 
					else { coeff[numOfTerm] = signMultiplier*number; };  
				numStr = ""; prevIsDigit = false; };
				if (Character.isDigit(ch) || (ch == '.')) {
					numStr += ch; prevIsDigit = true; };
					if (Character.isLetter(ch)) { expon[numOfTerm] = 1;
					if (prevIsLetter) messages += "Error: adjacent letters found. "; 
						else { variable[numOfTerm] = ch;  prevIsLetter = true; }; }
					else { prevIsLetter = false; };
					if (ch == '^') { if (nowExpon) messages += "Error: multiple adjacent exponent symbols (^) found. ";    
						if (!(prevIsLetter)) { messages += "Error: exponent not preceeded by a variable. "; };
						nowExpon = true; };
					if ((ch == '+') || (ch == '-')) {
						numOfTerm++; pos[numOfTerm] = i; 
						if (ch == '+') signMultiplier = 1; 
						if (ch == '-') signMultiplier = -1; };
						if ((i == endPos-1) && prevIsDigit) { number = Double.parseDouble(numStr);  
						if (nowExpon) { expon[numOfTerm] = (int) number; } 
							else { coeff[numOfTerm] = signMultiplier*number; variable[numOfTerm] = ' '; expon[numOfTerm] = 0;  }; }
						i++; }
	
		for (index = 0; index <= numOfTerm; index++ ) { exprAsArrayOfTerms[index].setCoeff(coeff[index]);
		exprAsArrayOfTerms[index].setVar(variable[index]); exprAsArrayOfTerms[index].setExpon(expon[index]); };
		
		return exprAsArrayOfTerms;
	}   // end of determineExpr
	
	public static void displayExpr( Term[] expr ) { //  Used various places where expressions are to be displayed in the console
		int index, num = numUsedTerms( expr );
		for (index = 0; index < num; index++ ) {
			System.out.print( " " + expr[index].coeff );
			if ( expr[index].expon != 0 ) { System.out.print( expr[index].variable + "^" + expr[index].expon ); 
			}
		if (index != num-1) System.out.print( " +" );  
		else System.out.println("\n"); };
		}
	
	public static String convertToStr( Term[] expr ) {  //  Used several places to convert an array of Terms into a string
		int i, numOfTerms;
		String newExpr = "";
		numOfTerms = numUsedTerms( expr );
		for (i=0; i<numOfTerms; i++) {	// Build new expression after distributing
			if ( expr[i].expon == 0 ) {	newExpr += Double.toString( expr[i].coeff ); }
				else { newExpr += Double.toString( expr[i].coeff ) + expr[i].variable + "^" + Integer.toString((int) expr[i].expon ); }
			if ( i < numOfTerms - 1) newExpr += " + "; 
			}
		return newExpr;
	}
	
	public static Term[] distributeMonomial( Term[] monomial, Term[] insideExpr) { // Distributes a single term (monomial) to an expression
		int index;
		Term[] resultExpr = initArrayOfTerms(); 
		for (index = 0; index < 9; index++ ) {
			resultExpr[index].setCoeff( monomial[0].coeff*insideExpr[index].coeff );
			char monVar = monomial[0].variable; 
			char insVar = insideExpr[index].variable;
			if ((monVar == insVar)||(insVar == 32)) {
				resultExpr[index].setVar(monomial[0].variable);
				resultExpr[index].setExpon( monomial[0].expon + insideExpr[index].expon); }
			else { 
				resultExpr[index].variable = insideExpr[index].variable; 
				resultExpr[index].expon = insideExpr[index].expon; };
			};
		return resultExpr;
	}
	
	public static Term[] deleteTerm( int index, Term[] expression )  {
		// To eliminate the "processed" highExpon term from the input expression
		int m, numOfTerms = numUsedTerms( expression );			
		if ( index == numOfTerms - 1 )  {
			initTerm( expression[ index ] );
			}
		else {
			for ( m = index; m < numOfTerms-1; m++ ) {
				expression[m].setCoeff( expression[m+1].coeff ); 
				expression[m].setVar( expression[m+1].variable ); 
				expression[m].setExpon( expression[m+1].expon ); 
				};
				initTerm( expression[numOfTerms-1] );
			};		
		return expression;
	    }
	
	public static Term[] combineLikeTerms( Term[] inputExpr) {  // Starting with the highest exponent, searches for like terms and combines
		
		int i, j, highExpon, highIndex = 0, origNumOfTerms, numOfTerms; 
		double sumOfCoeffs;
		char highVar;
		Term[] outputExpr = initArrayOfTerms(); 
		origNumOfTerms = numUsedTerms( inputExpr ); 
		numOfTerms = origNumOfTerms;
	
		i = 0;
		while ( ( i < origNumOfTerms ) && ( numOfTerms != 0 ) ) {
			
			numOfTerms = numUsedTerms( inputExpr );	
					
			// set the high exponent term to be the first term
			highExpon = inputExpr[0].expon; 
			highVar = inputExpr[0].variable; 
			highIndex = 0;
			if (numOfTerms > 0) {
				for ( j = 1; j < numOfTerms; j++ ) {
					if (highExpon < inputExpr[j].expon ) { // add a condition of the variables being the same
						 highExpon = inputExpr[j].getExpon(); 
						 highIndex = j;  
						 highVar = inputExpr[j].getVar(); 
						 };
					};
	
			// Stores highest exponent term in outputExpr, then removes it from the inputExpr array
			outputExpr[i].setVar( inputExpr[highIndex].variable ); 
			outputExpr[i].setExpon( inputExpr[highIndex].expon );
			outputExpr[i].setCoeff( inputExpr[highIndex].coeff );
			
			inputExpr = deleteTerm( highIndex, inputExpr );
			numOfTerms = numUsedTerms( inputExpr ); 
			};
	
			//  To look for a term that is "like" the outputExpr[i] term and combine it
			
			for ( j = 0; j < numOfTerms; j++ ) { 
				
				if (( outputExpr[i].variable == inputExpr[j].variable ) && ( outputExpr[i].expon == inputExpr[j].expon))
					{  
					// To store the combined term in the output expression
					sumOfCoeffs = inputExpr[j].coeff + outputExpr[i].coeff;
					outputExpr[i].setCoeff( sumOfCoeffs );
	
					// To eliminate the "processed" like term from the input expression
					inputExpr = deleteTerm( j, inputExpr ); 
					numOfTerms = numUsedTerms( inputExpr ); 
					};
					
				};  // End of for j Loop
				
			i++;
			}
						
			return outputExpr;
	}   // End of combineLikeTerms
	
	public static Term[] simpSinglePar( String expression )  {  // Handles when the expression has one set of parentheses
		
		int i, j, k, l, m = 0, n = 0, posOpenPar1, posClosePar1;
		String newExpr = "", beginStr = "", endStr = ""; 
		boolean parError = false, endMultiplier = false;  
		Term[] expressionList = initArrayOfTerms(); 
		Term[] nestExpr = initArrayOfTerms();  
		Term[] multiplierTerm = initArrayOfTerms(); 
		Term[] resultExpr = initArrayOfTerms(); 
		
		// Check for close parentheses before open parentheses
		posOpenPar1 = expression.indexOf( '(' ); posClosePar1 = expression.indexOf( ')' );
		if (posClosePar1 < posOpenPar1) { 
			System.out.println( "Error: closing parentheses before opening parentheses."); 
			parError = true; 
			};
		
		// Process parentheses except for any term after the parentheses
		k = posOpenPar1; 
		i = expression.lastIndexOf('+', k); 
		j = expression.lastIndexOf('-', k);
		l = expression.lastIndexOf('–', k);
		if ((j!=-1) || (l!=-1)) j = Math.max(j, l); 
		if ((i!=-1)||(j!=-1)) { 
			k = Math.max(i, j); 
			multiplierTerm = determineExpr( expression, k, posOpenPar1); 
			}
		else { 
			multiplierTerm = determineExpr( expression, 0, posOpenPar1);
			k = 0; 
			};
			
		if (k != 0) beginStr = expression.substring(0,k) + "+";
		nestExpr = determineExpr( expression, posOpenPar1+1, posClosePar1 );
		if ((numUsedTerms(multiplierTerm) == 1) && (multiplierTerm[0].coeff != 0)) 
			resultExpr = distributeMonomial( multiplierTerm, nestExpr);
		
		// Determine any final term to distribute and distribute it
		if (posClosePar1 < expression.length()-1) {
			i = expression.indexOf('+',posClosePar1); 
			j = expression.indexOf('-',posClosePar1); 
			l = expression.lastIndexOf('–',posClosePar1);
			m = posClosePar1;
			if (i!=-1) m = i;
			if ((j!=-1) || (l!=-1)) j = Math.max(j, l); 
			if (j!=-1) { 
				n = j; 
				if (i!=-1) m = Math.min( m, n ); 
					else m = j; };
			if (( i == -1 ) && ( j == -1 )) { 
				multiplierTerm = determineExpr( expression, posClosePar1+1, expression.length() ); 
				endMultiplier = true; 
				endStr = ""; 
				}
				else { 	multiplierTerm = determineExpr( expression, posClosePar1+1, m); 
				endMultiplier = true;
				if (m>posClosePar1) endStr = "+" + expression.substring( m ); 
					else endStr = "+" + expression.substring(m+1); };
				if (endMultiplier) if ((numUsedTerms(multiplierTerm) == 1) && (multiplierTerm[0].coeff != 0))
					{ resultExpr = distributeMonomial( multiplierTerm, resultExpr); };
			};
			
		// Combine all parts and prepare expression and combine like terms
		newExpr = convertToStr( resultExpr );
		expression = beginStr + newExpr + endStr;
		expression = prepareExpOrEq( expression );
		
		expressionList = determineExpr( expression, 0, expression.length() );
		expressionList = combineLikeTerms ( expressionList );
		
		if (parError) System.out.println( "Incorrect entry. Try again." );  
		
		return expressionList;
	}  // end of simpSinglePar
	
	public static Term[] addTwoExpressions( Term[] exp1, Term[] exp2 )  {  // Adds two expressions (arrays of terms)
		
		int i, numOfTerms; String newExpr = "";
		Term[] expression = initArrayOfTerms();
		numOfTerms = numUsedTerms( exp1 );
		
		for (i=0; i < numOfTerms; i++) {  // Build first part of new expression
			if ( exp1[i].expon == 0 ) { newExpr += Double.toString( exp1[i].coeff ); }
				else { newExpr += Double.toString( exp1[i].coeff ) + exp1[i].variable + "^" + Integer.toString((int) exp1[i].expon ); }
			if ( i < numOfTerms - 1) newExpr += "+"; }
		newExpr += "+";
		
		numOfTerms = numUsedTerms( exp2 );
		for (i=0; i < numOfTerms; i++) {  // Build second part of new expression
			if ( exp2[i].expon == 0 ) { newExpr += Double.toString( exp2[i].coeff ); }
			else { newExpr += Double.toString( exp2[i].coeff ) + exp2[i].variable + "^" + Integer.toString((int) exp2[i].expon ); }
				if ( i < numOfTerms - 1) newExpr += "+"; }
		
		newExpr = prepareExpOrEq( newExpr );
		expression = determineExpr( newExpr, 0, newExpr.length() );
		expression = combineLikeTerms( expression );
		return expression;
	}
	 
	public static Term[] simpDoublePar( String origExpression )  {  //  Handles when the expression has two sets of parentheses by use of simpSinglePar twice
		
		int i, j, k, posOpenPar1 = -1, posOpenPar2 = -1, posOpenPar3 = -1, posOpenPar4 = -1, posClosePar1 = -1, posClosePar2 = -1;
		String beginExpr = "", endExpr = ""; 
		boolean parError = false; 
		char ch;
		Term[] firstExpr = initArrayOfTerms();
		Term[] secondExpr = initArrayOfTerms(); 
		Term[] expression = initArrayOfTerms(); 
	
		for ( i = 0; i < origExpression.length(); i++ ) {
			ch = origExpression.charAt(i);
			if (ch == '(')  { 
				if (posOpenPar1 == -1) { posOpenPar1 = i; }
				else if (posOpenPar2 == -1) { posOpenPar2 = i; }
					 else if (posOpenPar3 == -1) { posOpenPar3 = i; }
					      else if (posOpenPar4 == -1) { posOpenPar4 = i; } 
				}
			};
		//System.out.println( "posOpenPar1: " + posOpenPar1 + "  posOpenPar2: " + posOpenPar2 );
			
		j = origExpression.indexOf(')'); 
		if (j != -1) {
			if (j < posOpenPar1) { 
				System.out.println( "Error: closing parentheses before opening parentheses."); 
				parError = true; } 
			else { posClosePar1 = j; }; }
		
		j = origExpression.indexOf(')',j+2); 
		if (j != -1) {
			if ((j < posOpenPar2) || (j < posOpenPar1)) { 
				System.out.println( "Error: closing parentheses before opening parentheses."); 
				parError = true; 
				}
				else posClosePar2 = j;
			}
		//System.out.println( "posClosePar1: " + posClosePar1 + "  posClosePar2: " + posClosePar2 ); 
		
		k = posClosePar1+1;
		
		//i = origExpression.indexOf( '+', posClosePar1 ); 
		//j = origExpression.indexOf( '-', posClosePar1 ); 
		//if ((i!=-1) && (i<posOpenPar2)) { k = i; }; 
		//if ((j!=-1) && (j<posOpenPar2)) { k = j; };
		
		beginExpr = origExpression.substring(0,k); 
		firstExpr = simpSinglePar( beginExpr );	
		endExpr = origExpression.substring(k); 
		secondExpr = simpSinglePar( endExpr );
		//System.out.println( "First expr: " + convertToStr(firstExpr) + "  Second expr: " + convertToStr(secondExpr) ); 
		
		expression = addTwoExpressions( firstExpr, secondExpr );
		//System.out.println( "Combined expr: " + convertToStr(expression )); 
		if (parError) System.out.println( "Incorrect entry. Try again." );  
		
		return expression;
	}  //  End of simpDoublePar
	
	public static Term[] simpNestParExpr( String exp )  {  //  Handles expressions with one set of parentheses inside another set of parentheses
		
		int i, j, posOpenPar1 = -1, posOpenPar2 = -1, posClosePar2 = -1;
		String newExpr = ""; 
		boolean parError = false; 
		char ch;
		Term[] expression = initArrayOfTerms();
		Term[] nestExpr = expression; 
		
		for ( i = 0; i < exp.length(); i++ ) {
			ch = exp.charAt(i); if ((ch == '(') && (posOpenPar1 == -1)) { posOpenPar1 = i; }
			if ((ch == '(') && ((posOpenPar1 != -1) && (posOpenPar1 != i))) posOpenPar2 = i; };
		j = exp.indexOf(')'); 
		if (j != -1) 
			if (j < posOpenPar1) {
				System.out.println( "Error: closing parentheses before opening parentheses."); 
				parError = true; };
		j = exp.indexOf(')',j+1); 
		if (j != -1) 
			if ((j < posOpenPar2) || (j < posOpenPar1)) 
				{
				System.out.println( "Error: closing parentheses before opening parentheses."); 
				parError = true; 
				}
				else { posClosePar2 = j; };
				
		// Simplify inner nested parentheses
		newExpr = exp.substring( posOpenPar1+1, posClosePar2);
		newExpr = prepareExpOrEq( newExpr );
		nestExpr = simpSinglePar( newExpr );
		
		// Build new string expression after combining strings
		newExpr = convertToStr( nestExpr );
		//System.out.println( "NewExpr: " + newExpr );
		exp = exp.substring(0,posOpenPar1) + "(" + newExpr + ")" + exp.substring( posClosePar2+1 );
		//System.out.println( "Whole Expr: " + exp );
		exp = prepareExpOrEq( exp );
		
		// Simplify complete expression string
		expression = simpSinglePar( exp );
		if (parError) System.out.println( "Incorrect entry. Try again." );
		
		return expression;
		
	}  // End of simpNestParExpr
	
	public static Term[] simpAdjacParExpr( String exp )  {  //  Handles expressions with two parentheses adjacent to each other (where they must be multiplied)
	
		int i, j, k = 0, m, len, endLen, posOpenPar1 = -1, posOpenPar2 = -1, posClosePar1 = -1, posClosePar2 = -1, numOfTerms1, numOfTerms2;
		double multiplier = 1; 
		String beginStr = "", endStr = "";
		boolean parError = false, multiplyingDone = false, backDistribute = false; char ch;
		
		Term[] expression = initArrayOfTerms();
		Term[] multiplierExpr = expression;  
		Term[] firstExpr = expression;
		Term[] tempExpr = expression;
		Term[] secondExpr = expression;
		Term[] resultExpr = expression;
		Term[] endMultiplierTerm = expression;
	
		//  Determine positions of openings and closings of parentheses
		exp = prepareExpOrEq( exp ); 
		len = exp.length();
		for ( i = 0; i < len; i++ ) {
			ch = exp.charAt(i); 
			if ((ch == '(') && (posOpenPar1 == -1)) { posOpenPar1 = i; }
			if ((ch == '(') && ((posOpenPar1 != -1) && (posOpenPar1 != i))) posOpenPar2 = i; };
		j = exp.indexOf(')'); 
		if (j != -1) 
			if (j < posOpenPar1) { 
				System.out.println( "Error: closing parentheses before opening parentheses."); 
				parError = true; } 
			else { posClosePar1 = j; };
		j = exp.indexOf(')',j+1); 
		if (j != -1)  
			if ((j < posOpenPar2) || (j < posOpenPar1)) { 
				System.out.println( "Error: closing parentheses before opening parentheses."); 
				parError = true; }
			else { posClosePar2 = j; };
	
		//  Handle any part of the expression before the first parentheses
		if (posOpenPar1 != 0) {
			beginStr = exp.substring( 0, posOpenPar1);
			
			//  If just one character before opening parentheses, set multiplier to -1 if a negative sign or to a digit if a digit
			if (beginStr.length() == 1) {
				ch = beginStr.charAt(0); 
				if (ch == '-') multiplier = -1;
				if (Character.isDigit(ch)) multiplier = Double.valueOf( ch ) - 48; 
				beginStr = "";  		
				multiplierExpr[0].setCoeff( multiplier );   
				};  
	
			//  If multiple characters before opening parentheses, determine if any are multipliers to the parentheses
			if (beginStr.length() > 1) {
				k = posOpenPar1; 
				i = exp.lastIndexOf( '+', k ); 
				j = exp.lastIndexOf( '-', k ); 
				if (i>j) k = i; 
					else if (j>i) k = j;  
				if ((i!=-1) || (j!=-1)) {
					multiplierExpr = determineExpr( beginStr, k, beginStr.length() ); 
					beginStr = beginStr.substring(0, k); }
					else { 
						multiplierExpr = determineExpr( beginStr, 0, posOpenPar1 ); 
						beginStr = ""; 
					}; 
				};
			};
	
		//  Handle any part of the expression after the parentheses
		if (posClosePar2 < len-1) {
			endStr = exp.substring( posClosePar2+1, len); 
			endLen = endStr.length();
			if (endLen == 1) {	
				ch = endStr.charAt(0);
				if (Character.isDigit(ch)) { 
					multiplier = Integer.valueOf( ch ) - 48; 
					backDistribute = true; 
					endMultiplierTerm = determineExpr( endStr, 0, 1); };
				};
			if (endLen > 1) {
				i = endStr.indexOf('+'); 
				j = endStr.indexOf('-'); 
				k = -1;  
				if ((i==-1) && (j==-1)) { 
					endMultiplierTerm = determineExpr( endStr, 0, endLen); 
					backDistribute = true; }
					else { 
						if (i!=-1) { 
							k = i; 
							if (j!=-1) k = Math.min( i, j ); 
							}
						else { 
							if (j!=-1) k = j; }; 
						};
				if (k!=-1) {
					if (k>0) { 
						endMultiplierTerm = determineExpr( endStr, 0, k); 
						backDistribute = true; 
						endStr = endStr.substring(k); }
						else if (k==0) { 
							backDistribute = false; }; 
					}
					else { 
						endMultiplierTerm = determineExpr( endStr, 0, endStr.length() );
						};
				};
		  };
		  
		//  Perform the multiplication of the two expressions in parentheses
		firstExpr = determineExpr( exp, posOpenPar1, posClosePar1 ); 
		firstExpr = combineLikeTerms( firstExpr ); 
		numOfTerms1 = numUsedTerms( firstExpr );  
		secondExpr = determineExpr( exp, posOpenPar2, posClosePar2 ); 
		secondExpr = combineLikeTerms( secondExpr ); 
		numOfTerms2 = numUsedTerms( secondExpr );
		if (numOfTerms1 == 1) {
			resultExpr = distributeMonomial( firstExpr, secondExpr ); 
			multiplyingDone = true; };
		if ((numOfTerms2 == 1) && (!multiplyingDone)) { 
			resultExpr = distributeMonomial( secondExpr, firstExpr ); 
			multiplyingDone = true; };
		if (!multiplyingDone) {
			for (i=0; i < 9; i++) initTerm( resultExpr[i] );
			for (j = 0; j < numOfTerms1; j++ ) { 
				Term[] term = initArrayOfTerms(); 
				term[0] = firstExpr[j]; 
				tempExpr = distributeMonomial( term, secondExpr ); 
				resultExpr = addTwoExpressions( resultExpr, tempExpr ); 
				};
			multiplyingDone = true; };
			
		//  Distribute any multiplier to the result of above parentheses multiplication, and add in beginStr and endExpr
		if (posOpenPar1 != 0) { resultExpr = distributeMonomial( multiplierExpr, resultExpr ); };
		if (backDistribute) { resultExpr = distributeMonomial( endMultiplierTerm, resultExpr ); };
		if (beginStr.length() > 0) { 
			tempExpr = determineExpr( beginStr, 0, beginStr.length() ); 
			resultExpr = addTwoExpressions( tempExpr, resultExpr ); };
		if (endStr.length() > 0) { 
			tempExpr = determineExpr( endStr, 0, endStr.length() ); 
			resultExpr = addTwoExpressions( tempExpr, resultExpr ); 
			};  
		
		expression = combineLikeTerms( resultExpr );
		if (parError) System.out.println( "Incorrect entry. Try again." );  
		
		return expression;
	}  // end of simpAdjParExpr
	
	public static Term[] roundCoeffs( Term[] expression )  {  //  Used to handle roundoff errors or to limit the digits displayed after the decimal point
	
		int i, numOfTerms = numUsedTerms( expression );  
		Term[] outputExpr = initArrayOfTerms();
		
		for (i=0; i < numOfTerms; i++)  {
			outputExpr[i].setCoeff( Math.rint( expression[i].getCoeff()*1000 )/1000 );  
			outputExpr[i].setVar( expression[i].getVar() );
			outputExpr[i].setExpon( expression[i].getExpon() );
		}
		return outputExpr;
	}
	
	public static Term[] simplifyExpr( String exp ) {  //  The main component to distinguish the type of expression and send it to a particular method to simplify it
		
		int i, j, k, posOpenPar1 = -1, posOpenPar2 = -1, posClosePar1 = -1, posClosePar2 = -1;
		char ch;  
		boolean noPar = false, parError = false, nestedPar = false, singlePar = false, doublePar = false, adjacentPar = false;
	
		Term[] result = initArrayOfTerms();
		
		// Determine positions of parentheses (and if incorrectly ordered), and determine if no parentheses, single, double, or nested.
		for ( i = 0; i < exp.length(); i++ ) {
			ch = exp.charAt(i);
			if (ch == '(') {
				if (posOpenPar1 == -1) posOpenPar1 = i;  
				else if (posOpenPar1 != i) posOpenPar2 = i; };
			};
		j = exp.indexOf(')');
		if (j != -1)  
			if (j < posOpenPar1) { System.out.println( "Error: closing parentheses before opening parentheses."); parError = true; }
				else { posClosePar1 = j; };
		j = exp.indexOf(')',j+1);
		if (j != -1)  
			if ((j < posOpenPar2) || (j < posOpenPar1)) { System.out.println( "Error: closing parentheses before opening parentheses."); parError = true; }
				else { posClosePar2 = j; };
	
		if ((exp.indexOf('(')==-1) && (exp.indexOf(')')==-1)) noPar = true;
			else if ((posOpenPar2 == -1) && (posClosePar2 == -1)) singlePar = true;
		if (( posOpenPar2 != -1) && (posClosePar1 != -1))
			if (posOpenPar2 < posClosePar1) nestedPar = true;
			else doublePar = true;  
		if (posClosePar1 == posOpenPar2 - 1) { adjacentPar = true; doublePar = false; };
		
		// System.out.println( "NoPar: " + noPar + "  SinglePar: " + singlePar + "  DoublePar: " + doublePar + "  Nested: " + nestedPar + "  Adjacent: " + adjacentPar );
		
		// Place a 1 in front of parentheses when parentheses are added or subtracted
		if (posOpenPar1 != -1) {
			i = exp.lastIndexOf('+',posOpenPar1); 
			j = exp.lastIndexOf('-',posOpenPar1); 
			k = posOpenPar1;
			if ((k==i) || (k==j)) { 
				exp = exp.substring(0,k+1) + "1" + exp.substring(k+1);
				posOpenPar1++;  
				posClosePar1++;  
				if (posOpenPar2 != -1) { posOpenPar2++; posClosePar2++; }
				};
		};
		if (posOpenPar2 != -1) {
			i = exp.lastIndexOf('+',posOpenPar2-1); 
			j = exp.lastIndexOf('-',posOpenPar2-1); 
			k = posOpenPar2-1;
			if ((k==i) || (k==j)) { 
				exp = exp.substring(0,k+1) + "1" + exp.substring(k+1);
				posOpenPar2++; 
				posClosePar2++; 
				if ( posClosePar1 > posOpenPar2 ) posClosePar1++;  
			};
		};
		System.out.println( "Expression: " + exp );
		System.out.print( "   Type: " );
		
		// Pass the expression to the appropriate method to simplify it
		if (noPar) {  
			System.out.println( "No Parentheses"); 
			result = determineExpr( exp, 0, exp.length() ); 
			result = combineLikeTerms( result ); 
			//System.out.print( "Expression after simplifying: " ); displayExpr( result ); 
			};
		if (singlePar) { 
			System.out.println( "Single Parentheses"); 
			result = simpSinglePar( exp ); 
			//System.out.println( "Expression after simplifying: " + convertToStr( result ) );
			};
		if (doublePar) { 
			System.out.println( "Double Parentheses"); 
			result = simpDoublePar( exp ); 
			//System.out.println( "Expression after simplifying: " + convertToStr( result ) );
			};
		if (nestedPar) { 
			System.out.println( "Nested Parentheses"); 
			result = simpNestParExpr( exp ); 
			//System.out.println( "Expression after simplifying: " + convertToStr( result ) );
			};
		if (adjacentPar) { 
			System.out.println( "Adjacent Parentheses"); 
			result = simpAdjacParExpr( exp ); 
			//System.out.println( "Expression after simplifying: " + convertToStr( result ) );
			};
		result = combineLikeTerms( result );
		result = roundCoeffs( result );
		
		System.out.print( "\nFinal Simplified Expression: "); displayExpr( result );
		if (parError) System.out.println( "Incorrect entry. Try again." );  
		return result;
	}   // End of simplifyExpr
	
	public static double evaluateExpr( Term[] expression, double value ) {  //  Given a value for the variable, this method evaluates a whole expression
		double termVal = 0, tempVal = 1, answer = 0;
		int index, i, numOfTerms = numUsedTerms( expression );
		for (index=0; index<numOfTerms; index++) {
			termVal = 0; tempVal = 1;
			for (i=0; i<expression[index].expon; i++) tempVal *= value;  
			termVal = expression[index].coeff*tempVal;
			answer = answer + termVal;
		};
		return answer;
	}  // End of evaluateExpr
	
	public static String simpEquation( String eqStr ) {  // Converts left & right sides to expression, combines on left side, converts equation back to string
		int i, location;
		String leftSide = "", rightSide = "", simplifiedEquation = "", negOneStr = "-1";
		Term[] leftExpr = initArrayOfTerms();
		Term[] rightExpr = leftExpr;
		Term[] combinedExpr = leftExpr; 
		Term[] negOne = determineExpr( negOneStr, 0, 2);
		
		eqStr = removeSpaces( eqStr ); 
		location = eqStr.indexOf('=');
		leftSide = eqStr.substring(0,location); 
		rightSide = eqStr.substring(location+1);  
		//System.out.println( "left: " + leftSide + "  right: " + rightSide);
	
		leftSide = prepareExpOrEq( leftSide );
		rightSide = prepareExpOrEq( rightSide );
		//System.out.println( "prepped left: " + leftSide + "  prepped right: " + rightSide);
			
		leftExpr = simplifyExpr( leftSide ); 
		rightExpr = simplifyExpr( rightSide );
		//System.out.println( "simplified left: " + convertToStr( leftExpr ) + "  simplified right: " + convertToStr( rightExpr ) );
		
		combinedExpr = addTwoExpressions( leftExpr, distributeMonomial( negOne, rightExpr ) );
		//System.out.print( "Combined Sides: "); displayExpr( combinedExpr );  
		
		if ( numUsedTerms( combinedExpr ) > 0 ) simplifiedEquation = convertToStr( combinedExpr );
			else simplifiedEquation = "0";
		simplifiedEquation = prepareExpOrEq( simplifiedEquation + " = 0" );
		return simplifiedEquation;
	}  // End of simpEquation
	
	public static String[] solveEq( String equation ) {  // Once equation is simplified, this method solves it and returns answers in an array of type String
		// If equation has degree < 3, equation is solved using -b/a (linear) or by quadratic formula (quadratic)
		int i, location, degree, numOfTerms;
		double[] a = new double[9];
		Term[] expression = initArrayOfTerms();
		String[] ans = new String[2]; 
		for (i=0; i<2; i++) { ans[i] = ""; };
		
		location = equation.indexOf('=');
		expression = determineExpr( equation, 0, location);
		numOfTerms = numUsedTerms( expression );
		degree = 0; 
		for (i=0; i<9; i++) { 
			if (expression[i].expon > degree) degree = expression[i].expon; };
		if (degree == 0) { 
			if (expression[0].coeff == 0) ans[0] = "All real numbers"; 
				else ans[0] = "No solutions"; };
		if (degree == 1) { ans[0] = String.valueOf( -1*expression[1].coeff / expression[0].coeff ); };
		if (degree == 2) { 
			for ( i=0; i < numOfTerms; i++) {
				if (expression[i].expon == 2) a[2] = expression[i].coeff;
				if (expression[i].expon == 1) a[1] = expression[i].coeff;
				if (expression[i].expon == 0) a[0] = expression[i].coeff; 
				};
			if (a[1]*a[1]-4*a[2]*a[0] >= 0) {
				ans[0]=String.valueOf((-1*a[1]+Math.sqrt(a[1]*a[1]-4*a[2]*a[0]))/(2*a[2])); 
				ans[1]=String.valueOf((-1*a[1]-Math.sqrt(a[1]*a[1]-4*a[2]*a[0]))/(2*a[2])); }
				else ans[0] = "No solution in the real numbers.";
				};
		if (degree > 2) ans[0] = "This program only solves equations up to degree 2.";
		return ans;
		 
	}  // End of solveEq
	
	public static void AITest() {
		
		boolean passed, allPassed = true; 
		String origExpr;
		Term[] exprTerms = initArrayOfTerms();
		String ANSI_RED = "\u001B[31m";
		String ANSI_BLACK = "\u001B[30m";
		
		System.out.println( " A I  T E S T : \n" );
		
		// Combine Like Terms - No Parentheses
		System.out.println( "TEST: Combine Like Terms - No Parentheses" );
		passed = false;
		String str0 = "5x - 2x^2 + 4 - 7x + x^2 +1 - 6x^3", ans0 = "-6.0x^3-1.0x^2-2.0x^1+5.0"; 
		origExpr = prepareExpOrEq(str0);
		exprTerms = simplifyExpr( origExpr );
		System.out.println( "Simplified expression: " + prepareExpOrEq(convertToStr( exprTerms )) + "   Answer: " + ans0 );
		if ( prepareExpOrEq(convertToStr( exprTerms )).compareTo( ans0 ) == 0) { passed = true; }
		else { allPassed = false; };
		System.out.println( "Combining Like Terms - No Parentheses Test passed?  " + ANSI_RED + passed + ANSI_BLACK + "\n");
			
		// Single Parentheses
		System.out.println( "TEST: Single Parentheses" );
		passed = false; 
		String str1 = "3x+-5(3x-6+4x^2-5x-7)2-5-", ans1 = "-40.0x^2+23.0x^1+125.0"; 
		origExpr = prepareExpOrEq(str1);
		exprTerms = simplifyExpr( origExpr );
		System.out.println( "Simplified expression: " + prepareExpOrEq(convertToStr( exprTerms )) + "   Answer: " + ans1 );
		if ( prepareExpOrEq(convertToStr( exprTerms )).compareTo( ans1 ) == 0) { passed = true; }
		else { allPassed = false; };
		System.out.println( "Single Parentheses Test passed? " + ANSI_RED + passed + ANSI_BLACK + "\n");
		
		// Double Parentheses
		System.out.println( "TEST: Double Parentheses" );
		passed = false;
		String str2 = "3x+1x(3x-6+4)+5-(2x^2-5x-7)-5--7x", ans2 = "1.0x^2+13.0x^1+7.0";
		origExpr = prepareExpOrEq(str2);
		exprTerms = simplifyExpr( origExpr );
		System.out.println( "Simplified expression: " + prepareExpOrEq(convertToStr( exprTerms )) + "   Answer: " + ans2 );
		if ( prepareExpOrEq(convertToStr( exprTerms )).compareTo( ans2 ) == 0) { passed = true; }
		else { allPassed = false; };
		System.out.println( "Double Parentheses Test passed? " + ANSI_RED + passed + ANSI_BLACK + "\n");
		
		// Nested Parentheses
		System.out.println( "TEST: Nested Parentheses" );
		passed = false;
		String str3 = "-3x+x(3x-6+4(2x^2-5x-7)--5)-7x", ans3 = "8.0x^3-17.0x^2-39.0x^1";
		origExpr = AI.prepareExpOrEq( str3 );
		exprTerms = AI.simplifyExpr( origExpr );
		System.out.println( "Simplified expression: " + prepareExpOrEq(convertToStr( exprTerms )) + "   Answer: " + ans3 );
		if ( prepareExpOrEq(convertToStr( exprTerms )).compareTo( ans3 ) == 0) { passed = true; }
		else { allPassed = false; };
		System.out.println( "Nested Parentheses Test passed? " + ANSI_RED + passed + ANSI_BLACK + "\n");	
		
		// Adjacent Parentheses
		System.out.println( "TEST: Adjacent Parentheses" );
		passed = false;
		String str4 = "--3x+1x(3x-6+4)(2x^2-5x-7)-5-7x", ans4 = "6.0x^4-19.0x^3-11.0x^2+10.0x^1-5.0";
		origExpr = AI.prepareExpOrEq( str4 );			 		
		exprTerms = AI.simplifyExpr( origExpr );
		System.out.println( "Simplified expression: " + prepareExpOrEq(convertToStr( exprTerms )) + "   Answer: " + ans4 );
		if ( prepareExpOrEq(convertToStr( exprTerms )).compareTo( ans4 ) == 0) { passed = true; }
		else { allPassed = false; };
		System.out.println( "Adjacent Parentheses Test passed? " + ANSI_RED + passed + ANSI_BLACK + "\n");
		 
	    /*
		// Double Parentheses with Adjacent
		passed = false;
		String str5 = "2 - 3x( 4x^2 - 5x ) - 5( x + 2 )( 6x - 9 )", ans5 = "-12.0x^3-15.0x^2-15.0x^1+92.0";
		origExpr = AI.prepareExpOrEq( str5 );
		exprTerms = AI.simplifyExpr( origExpr ); 
		System.out.println( "\nSimplified expression: " + prepareExpOrEq(convertToStr( exprTerms )) + "   Answer: " + ans5 );
		if ( prepareExpOrEq(convertToStr( exprTerms )).compareTo( ans5 ) == 0) { passed = true; }
		else { allPassed = false; };
		System.out.println( "Double Parentheses With Adjacent Test passed? " + passed + "\n" );
		*/
		
		// Linear Equation
		System.out.println( "TEST: Solving a Linear Equation" );passed = false;
		String str6 = "-3( 2x - 5 ) -7( x + 4 ) = 5( -3x - 2 ) + 9", ans6 = "6.0";
		origExpr = simpEquation( str6 );
		String[] calcAnswer = solveEq( origExpr ); 
		System.out.println( "Simplified equation: " + origExpr + "   Calculated answer: " + calcAnswer[0] + "   Desired Answer: " + ans6 );
		if ( calcAnswer[0].compareTo( ans6 ) == 0) { passed = true; }
		else { allPassed = false; };
		System.out.println( "Linear Equation Test passed? " + ANSI_RED + passed + ANSI_BLACK + "\n" ); 
		
		// Quadratic Equation
		System.out.println( "TEST: Solving a Quadratic Equation" );
		passed = false;
		String str7 = "3x( 2x - 5 ) - 7( x^2 + 4x - 3 ) = -5x( 3x - 2 ) + 9x^2", ans7 = "10.188,0.412";
		origExpr = simpEquation( str7 );
		String[] calcAnswerQuad = solveEq( origExpr );
		double quadAns0 = Double.parseDouble(calcAnswerQuad[0]), quadAns1 = Double.parseDouble(calcAnswerQuad[1]);
		quadAns0 = quadAns0*1000; quadAns1 = quadAns1*1000;
		quadAns0 = Math.round(quadAns0); quadAns1 = Math.round(quadAns1);
		quadAns0 = quadAns0/1000; quadAns1 = quadAns1/1000; 
		String quadAnsStr0 = Double.toString( quadAns0 ); String quadAnsStr1 = Double.toString( quadAns1 ); 
		String calcAnsQuad = quadAnsStr0 + "," + quadAnsStr1;
		System.out.println( "Simplified equation: " + origExpr + "  Calculated answers: " + calcAnsQuad + "  Desired Answers: " + ans7 );
		if ( calcAnsQuad.compareTo( ans7 ) == 0) { passed = true; }
		else { allPassed = false; };
		System.out.println( "Quadratic Equation Test passed? " + ANSI_RED + passed + ANSI_BLACK + "\n" );
		
		System.out.println( "All Tests Passed? " + ANSI_RED + allPassed + ANSI_BLACK + "\n\n");
		
		}	
	
	public static void main(String[] args) {
		 
		AITest(); 
			
		//  Input original expression
		System.out.println("Enter your expression or equation: (only one variable & up to 2 sets of parentheses allowed)");
		Scanner sc = new Scanner( System.in );
		try { origText = sc.nextLine(); }
		catch (Exception e) { e.printStackTrace(); }
	
		// The method prepareExpOrEq removes spaces, double signs, leading or trailing unnecessary signs, detects unacceptable characters, etc. and determines
		// whether or not the inputed string is an equation
		expression = prepareExpOrEq( origText ); 
		// System.out.println( "After prepareExpOrEq: " + expression ); 
		
		if (!equation) overallExpr = simplifyExpr(expression);   // Uses various methods to simplify the expression (distributing, combining like terms, etc.)
		System.out.print( "After simplifying expression: "); 
		displayExpr( overallExpr );
		
		if (!equation) {  //  After simplifying the expression, this section evaluates the expression for an inputed value of the variable.
			System.out.println( "\nExpression Evaluator: Enter a value for the variable: \n" );
			try { origValue = sc.nextLine(); }
			catch (Exception e) { e.printStackTrace(); }
			finally { sc.close(); }
			enteredValue = Double.valueOf( origValue );
			System.out.println( "The expression evaluates to the answer " + evaluateExpr( overallExpr, enteredValue )); }
		
		if (equation) {
			expression = simpEquation( expression );  // simpEquation uses simplifyExpr to simplify both sides separately then combine all terms on the left side
			System.out.println( "Simplified and combined equation: " + expression );
			answer = solveEq( expression );   // solveEq distinguishes the degree of the equation and solves it accordingly (up to degree 2)
			System.out.print( "Solution Set: { " ); 
			System.out.print( answer[0]);
			if (answer[1]!="") { System.out.print(", " + answer[1] + " }"); } 
				else { System.out.println( " }" ); };
			};
		
		}  

}
	
