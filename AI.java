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
		for (i=0; i<9; i++) { if (terms[i].coeff != 0) num++; };
		return num;
	}

	public static String elimOrAddSigns( String expression) {  // Eliminates initial plus signs or terminal signs in an expression and also adds 1's before variables
		 int i, len = expression.length(), count = 0;
		 char ch;
		 
		 if ( expression.length() != 0 ) {     //  Do nothing if expression is void.
			 
			 if ( expression.length() == 1 ) {   //  Check if single character expression has no number or variable
				 ch = expression.charAt(0);
				 if ( "=+-*/()[]{}^.–".indexOf(ch) != -1 ) {
					expression = ""; 
					messages += "Error - incorrect expression: no algebra terms or numbers in the expression. "; 
				 	};
			 	}	
			 else {
				 while ((len > 1) && (count < len)) {
					 // System.out.println( "len:  " + len + "  count:  " + count ); 
					 count+=2;				 
					 
					 // Remove any initial '+' sign or any '+' or '-' sign at end of expression.
					 len = expression.length(); 
					 if ((expression.charAt(len-1) == '+') || (expression.charAt(len-1) == '-')) expression = expression.substring(0,len-1);  
					 if (expression.charAt(0) == '+')  expression = expression.substring(1); 
					 
					 // Add coefficient of 1 in front of any letter not already having a coefficient
					 if (Character.isLetter( expression.charAt(0) )) expression = "1" + expression;			 
					 for ( i = 0; i < expression.length() - 2; i++ ) {
					 	ch = expression.charAt(i);
					 	if ((( ch == '+') || (ch == '-')) || ((ch == '(') || (ch == ')')) )
					 		if (Character.isLetter( expression.charAt(i+1) )) expression = expression.substring(0,i+1) + "1" + expression.substring(i+1); 
					 	};
				 	};
				 }
		 	}
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
		
	public static String prepareEquation( String expression ) {
		int i, count = 0, location = 0; 
		equation = false; 
		String leftSide = "", rightSide = "";

		// Determine if there are equal signs (and if more than one) and if one, set equation to true.
		for (i=0; i < expression.length(); i++ )  { 
			if ( expression.charAt(i) == '=' ) {
				count++; location = i; };	
				};
		equation = (count == 1);
		if (count > 1) { messages += "Error - incorrect expression: multiple equal signs. "; }  

		// If equation, divide into two sides and eliminate initial plus signs or terminal signs in leftSide or rightSide expressions
		if (equation) { 
			leftSide = expression.substring(0,location); 
			rightSide = expression.substring(location+1); 
			System.out.println("Left Side: " + leftSide + "  Right Side: " + rightSide );
			leftSide = elimOrAddSigns( leftSide );  
			rightSide = elimOrAddSigns( rightSide );
			expression = leftSide + '=' + rightSide;
			};
		return expression;
		}

	public static String removeDoubleSigns( String expression ) {

		boolean completed = false;
		int j;
		char ch;
		String twoChar, tempString = expression;

		// Change ++ to +, +- or -+ to -, and -- to +, if any of these are in the string
		if ( expression.length() > 2 ) {
			while ( !completed) {
				for ( j = 0; j < expression.length() - 1; j++ ) {
					twoChar = expression.substring(j,j+2); 
					ch = '#';
					if (twoChar.compareTo("++") == 0) 
						{ ch = '+'; messages += "Double plus signs should be changed to + only. "; };
					if ((twoChar.compareTo("+-") == 0) || (twoChar.compareTo("-+") == 0)) 
						{ ch = '-'; messages += "+- or -+ should be changed to - only. "; };
					if (twoChar.compareTo("--") == 0) 
						{ ch = '+'; messages += "Double minus signs should be changed to a + sign. "; };
					if (ch != '#')  { 
						expression = expression.substring(0,j) + ch + expression.substring(j+2);
						//j--;
						}
					};
				if ( tempString.compareTo(expression) == 0 ) completed = true;
				tempString = expression;
				
				};
			}	
		return expression;
		}
	
	public static String removeOtherCharacters( String expression ) {
		int i = 0; 
		char ch; 		
		boolean ok;
		
		if ( expression.length() > 0 )
			while ( i < expression.length()-1 ) {
				ok = false; 
				ch = expression.charAt(i);
				if ((Character.isDigit(ch)) || (Character.isLetter(ch))) ok = true;  
				if ("=+-*/()[]{}^.–".indexOf(ch) != -1) ok = true;
				if (!ok) { 
					System.out.println( "Error - expression has an unacceptable character: " + ch + " (character removed). " ); 
					expression = expression.substring(0,i) + expression.substring(i+1); 
					};
				i++; 
			};
	
		return expression;
	}
	
	public static String removeTermsWithZeroCoefficients( String expression ) {
		
		//  This needs to be written or the code written and added into combineLikeTerms() or determineExpr() 
		
		return expression;
	}
	
	public static String prepareExpOrEq( String origText ) {  //  Prepares the string in various ways, detects unwanted characters, determines if equation, etc.
		
		// Remove any spaces and switch upper to lower case letters
		String expression = removeSpaces( origText ).toLowerCase();
		
		// Detect and remove characters other than numbers, letters, operation signs, equal signs, and parentheses or brackets.		
		expression = removeOtherCharacters( expression );
			
		// Remove any double signs such as '--' or '+-'
		expression = removeDoubleSigns( expression ); 
		
		// Eliminate initial plus signs or terminal signs in the whole expression or add 1's before variables if no coefficient
		expression = elimOrAddSigns( expression );
		
		// If equation, prepare the two sides separately
		expression = prepareEquation( expression ); 
		
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
		//char highVar;
		Term[] outputExpr = initArrayOfTerms(); 
		origNumOfTerms = numUsedTerms( inputExpr ); 
		numOfTerms = origNumOfTerms;
	
		i = 0;
		while ( ( i < origNumOfTerms ) && ( numOfTerms != 0 ) ) {
			
			numOfTerms = numUsedTerms( inputExpr );	
					
			// set the high exponent term to be the first term
			highExpon = inputExpr[0].expon; 
			//highVar = inputExpr[0].variable; 
			highIndex = 0;
			if (numOfTerms > 0) {
				for ( j = 1; j < numOfTerms; j++ ) {
					if (highExpon < inputExpr[j].expon ) { // add a condition of the variables being the same
						 highExpon = inputExpr[j].getExpon(); 
						 highIndex = j;  
						 //highVar = inputExpr[j].getVar(); 
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
	
	public static Term[] simpSingleParExpr( String expression )  {  // Handles when the expression has one set of parentheses
		
		int i, j, k, l, m = 0, n = 0;
		String newExpr = "", beginStr = "", endStr = ""; 
		boolean parError = false, endMultiplier = false;  
		Term[] expressionList = initArrayOfTerms(); 
		Term[] nestExpr = initArrayOfTerms();  
		Term[] multiplierTerm = initArrayOfTerms(); 
		Term[] resultExpr = initArrayOfTerms(); 
		
		// Determine positions of open and close parentheses
		ParPos pp = new ParPos(); 
		pp.determParPos(expression); 
		
		// Process parentheses except for any term after the parentheses
		k = pp.Open1; 
		i = expression.lastIndexOf('+', k); 
		j = expression.lastIndexOf('-', k);
		l = expression.lastIndexOf('–', k);
		if ((j!=-1) || (l!=-1)) j = Math.max(j, l); 
		if ((i!=-1)||(j!=-1)) { 
			k = Math.max(i, j); 
			multiplierTerm = determineExpr( expression, k, pp.Open1); }
		else { 
			multiplierTerm = determineExpr( expression, 0, pp.Open1);
			k = 0; };
			
		if (k != 0) beginStr = expression.substring(0,k) + "+";
		
		nestExpr = determineExpr( expression, pp.Open1+1, pp.Close1 );
		
		if ((numUsedTerms(multiplierTerm) == 1) && (multiplierTerm[0].coeff != 0)) 
			resultExpr = distributeMonomial( multiplierTerm, nestExpr);
		
		// Determine if any final term to distribute and distribute it; determine any trailing added part of expression
		if (pp.Close1 < expression.length()-1) {
			i = expression.indexOf('+',pp.Close1); 
			j = expression.indexOf('-',pp.Close1); 
			l = expression.lastIndexOf('–',pp.Close1);
			m = pp.Close1;
			if (i!=-1) m = i;
			if ((j!=-1) || (l!=-1)) j = Math.max(j, l); 
			if (j!=-1) { 
				n = j; 
				if (i!=-1) m = Math.min( m, n ); 
					else m = j; };
			if (( i == -1 ) && ( j == -1 )) { 
				multiplierTerm = determineExpr( expression, pp.Close1+1, expression.length() ); 
				endMultiplier = true; 
				endStr = ""; 
				}
				else { 	multiplierTerm = determineExpr( expression, pp.Close1+1, m); 
				endMultiplier = true;
				if (m>pp.Close1) endStr = "+" + expression.substring( m ); 
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
	
	static class ParPos {
		int Open1 = -1, Open2 = -1, Close1 = -1, Close2 = -1;
		boolean parError = false;
			
		public ParPos determParPos( String exp ) {
			int i, j;
			char ch;
			ParPos pp = new ParPos();
			
			for ( i = 0; i < exp.length(); i++ ) {
				ch = exp.charAt(i); 
				if ((ch == '(') && (Open1 == -1)) { Open1 = i; }
				if ((ch == '(') && ((Open1 != -1) && (Open1 != i))) Open2 = i; };
			j = exp.indexOf(')'); 
			if (j != -1) 
				if (j < Open1) { 
					parError = true;
					System.out.println( "Error: closing parentheses before opening parentheses."); 
					} 
				else { Close1 = j; };
			j = exp.indexOf(')',j+1); 
			if (j != -1)  
				if ((j < Open2) || (j < Open1)) { 
					parError = true;
					System.out.println( "Error: closing parentheses before opening parentheses."); 
					}
				else { Close2 = j; };
				
			return pp;
			}
			
		public void setOpen1( int position ) { Open1 = position; };
		public void setOpen2( int position ) { Open2 = position; };
		public void setClose1( int position ) { Close1 = position; };
		public void setClose2( int position ) { Close2 = position; };
				
		}
	
	public static Term[] simpDoubleParExpr( String origExpression )  {  //  Handles when the expression has two sets of parentheses by use of simpSinglePar twice
		
		int k, posClosePar1 = -1;
		String beginExpr = "", endExpr = ""; 
		Term[] firstExpr = initArrayOfTerms();
		Term[] secondExpr = firstExpr; 
		Term[] expression = firstExpr; 
	
		// Determine end of first set of parentheses
		ParPos pp = new ParPos();  pp.determParPos(origExpression); 
		posClosePar1 = pp.Close1;
		
		k = posClosePar1+1;	
		beginExpr = origExpression.substring(0,k); 
		firstExpr = simpSingleParExpr( beginExpr );	
		endExpr = origExpression.substring(k); 
		secondExpr = simpSingleParExpr( endExpr );
		expression = addTwoExpressions( firstExpr, secondExpr );
		if (pp.parError) System.out.println( "Incorrect entry. Try again." );  
		
		return expression;
	}  //  End of simpDoublePar
	
	public static Term[] simpNestParExpr( String expression )  {  //  Handles expressions with one set of parentheses inside another set of parentheses
		
		String newExpr = ""; 
		Term[] expressionTerms = initArrayOfTerms();
		Term[] nestExpr = expressionTerms; 
			
		ParPos pp = new ParPos();  
		pp.determParPos(expression); 
						
		// Simplify inner nested parentheses
		newExpr = expression.substring( pp.Open1+1, pp.Close2);
		newExpr = prepareExpOrEq( newExpr );
		nestExpr = simpSingleParExpr( newExpr );
		
		// Build new string expression after combining strings
		newExpr = convertToStr( nestExpr );
		expression = expression.substring(0,pp.Open1) + "(" + newExpr + ")" + expression.substring( pp.Close2+1 );
		expression = prepareExpOrEq( expression );
		
		// Simplify complete expression string
		expressionTerms = simpSingleParExpr( expression );
		if (pp.parError) System.out.println( "Incorrect entry. Try again." );
		
		return expressionTerms;
		
	}  // End of simpNestParExpr
	
	public static Term[] simpAdjacParExpr( String exp )  {  //  Handles expressions with two parentheses adjacent to each other (where they must be multiplied)
	
		int i, j, k = 0, len, endLen, numOfTerms1, numOfTerms2;
		double multiplier = 1; 
		String beginStr = "", endStr = "";
		boolean multiplyingDone = false, backDistribute = false; char ch;
		
		Term[] expression = initArrayOfTerms();  Term[] multiplierExpr = expression;   Term[] firstExpr = expression;   Term[] tempExpr = expression;
		Term[] secondExpr = expression;   Term[] resultExpr = expression;   Term[] endMultiplierTerm = expression;
		
		exp = prepareExpOrEq( exp ); 
		len = exp.length();
		
		// Determine positions of openings and closings of parentheses	
		ParPos pp = new ParPos(); pp.determParPos(exp); 
		
		//  Handle any part of the expression before the first parentheses
		if (pp.Open1 != 0) {
			beginStr = exp.substring( 0, pp.Open1);
			
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
				k = pp.Open1; 
				i = exp.lastIndexOf( '+', k ); 
				j = exp.lastIndexOf( '-', k ); 
				if (i>j) k = i; 
					else if (j>i) k = j;  
				if ((i!=-1) || (j!=-1)) {
					multiplierExpr = determineExpr( beginStr, k, beginStr.length() ); 
					beginStr = beginStr.substring(0, k); }
					else { 
						multiplierExpr = determineExpr( beginStr, 0, pp.Open1 ); 
						beginStr = ""; 
					}; 
				};
			};
	
		//  Handle any part of the expression after the parentheses
		if (pp.Close2 < len-1) {
			endStr = exp.substring( pp.Close2+1, len); 
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
		firstExpr = determineExpr( exp, pp.Open1, pp.Close1 ); 
		firstExpr = combineLikeTerms( firstExpr ); 
		numOfTerms1 = numUsedTerms( firstExpr );  
		secondExpr = determineExpr( exp, pp.Open2, pp.Close2 ); 
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
		if (pp.Open1 != 0) { resultExpr = distributeMonomial( multiplierExpr, resultExpr ); };
		if (backDistribute) { resultExpr = distributeMonomial( endMultiplierTerm, resultExpr ); };
		if (beginStr.length() > 0) { 
			tempExpr = determineExpr( beginStr, 0, beginStr.length() ); 
			resultExpr = addTwoExpressions( tempExpr, resultExpr ); };
		if (endStr.length() > 0) { 
			tempExpr = determineExpr( endStr, 0, endStr.length() ); 
			resultExpr = addTwoExpressions( tempExpr, resultExpr ); 
			};  
		
		expression = combineLikeTerms( resultExpr );
		if (pp.parError) System.out.println( "Incorrect entry. Try again." );  
		
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
	
	public static String add1inFrontOfParentheses( String expression, ParPos pp ) {
		int i, j, k;
		
		if (pp.Open1 != -1) {
			i = expression.lastIndexOf('+', pp.Open1); 
			j = expression.lastIndexOf('-', pp.Open1); 
			k = pp.Open1-1;
			if ((k==i) || (k==j)) { 
				expression = expression.substring(0,k+1) + "1" + expression.substring(k+1);
				pp.setOpen1( pp.Open1 + 1 ); 
				pp.setClose1( pp.Close1 + 1 ); 
				if (pp.Open2 != -1) { pp.setOpen2( pp.Open2 + 1 ); pp.setClose2( pp.Close2 + 1 ); }
				};
			};
		if (pp.Open2 != -1) {
			i = expression.lastIndexOf('+',pp.Open2); 
			j = expression.lastIndexOf('-',pp.Open2); 
			k = pp.Open2-1;
			if ((k==i) || (k==j)) { 
				expression = expression.substring(0,k+1) + "1" + expression.substring(k+1);
				pp.setOpen2( pp.Open2 + 1 ); 
				pp.setClose2( pp.Close2 + 1 ); 
				};
			};
		return expression;
		}

	public static Term[] simplifyExpr( String expression ) {  //  The main component to distinguish the type of expression and send it to a particular method to simplify it
		
		String type = ""; 
		boolean parError = false;
	
		Term[] result = initArrayOfTerms();
		expression = prepareExpOrEq( expression ); 
		
		// Determine positions of parentheses (and if correctly ordered or has error)
		ParPos pp = new ParPos(); 
		pp.determParPos(expression); 
		parError = pp.parError;
		
		// Determine the type of expression: if no parentheses, single, double, or nested.
		if ((expression.indexOf('(')==-1) && (expression.indexOf(')')==-1)) type = "noPar";
			else if ((pp.Open2 == -1) && (pp.Close2 == -1)) type = "singlePar";
		if (( pp.Open2 != -1) && (pp.Close1 != -1))
			if (pp.Open2 < pp.Close1) type = "nestedPar";
				else type = "doublePar";  
		if (pp.Close1 == pp.Open2 - 1) { type = "adjacentPar"; };
			
		// Place a 1 in front of parentheses when parentheses are added or subtracted	
		expression = add1inFrontOfParentheses( expression, pp ); 
		
		System.out.println( "Expression: " + expression );
		System.out.print( "   Type: " );
		
		// Pass the expression to the appropriate method to simplify it
		switch( type ) { 
			case "noPar": {
				System.out.println( "No Parentheses"); 
				result = determineExpr( expression, 0, expression.length() ); 
				result = combineLikeTerms( result ); };
				break;
			case "singlePar": { 
				System.out.println( "Single Parentheses"); 
				result = simpSingleParExpr( expression ); };
				break;
			case "doublePar": { 
				System.out.println( "Double Parentheses"); 
				result = simpDoubleParExpr( expression ); };
				break;
			case "nestedPar": { 
				System.out.println( "Nested Parentheses"); 
				result = simpNestParExpr( expression ); };
				break;
			case "adjacentPar": { 
				System.out.println( "Adjacent Parentheses"); 
				result = simpAdjacParExpr( expression ); };
				break;
			default:
				System.out.println( "No discernable type" ); 
			};
			
		result = combineLikeTerms( result );
		result = roundCoeffs( result );
		
		if (!parError) { System.out.print( "\nFinal Simplified Expression: "); displayExpr( result ); }
			else { System.out.println( "Incorrect entry. Try again." ); };  
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
	
	public static String simplifyEquation( String equationStr ) {  // Converts left & right sides to expression, combines on left side, converts equation back to string
		int location;
		String leftSide = "", rightSide = "", simplifiedEquation = "";
		Term[] leftExpr = initArrayOfTerms();
		Term[] rightExpr = leftExpr;
		Term[] combinedExpr = leftExpr; 
		Term[] negOne = determineExpr( "-1", 0, 2);
		
		equationStr = removeSpaces( equationStr ); 
		location = equationStr.indexOf('=');
		leftSide = equationStr.substring(0,location); 
		rightSide = equationStr.substring(location+1);  
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
		simplifiedEquation = prepareExpOrEq( simplifiedEquation + "=0" );
		return simplifiedEquation;
	}  // End of simpEquation
	
	public static String[] solveEquation( String equation ) {  // Once equation is simplified, this method solves it and returns answerwers in an array of type String
		// If equation has degree < 3, equation is solved using -b/a (linear) or by quadratic formula (quadratic)
		int i, location, degree, numOfTerms;
		double[] a = new double[9];
		Term[] expression = initArrayOfTerms();
		String[] answer = new String[2]; 
		for (i=0; i<2; i++) { answer[i] = ""; };
		
		location = equation.indexOf('=');
		expression = determineExpr( equation, 0, location);
		numOfTerms = numUsedTerms( expression );
		degree = 0; 
		for (i=0; i<9; i++) { 
			if (expression[i].expon > degree) degree = expression[i].expon; };
		if (degree == 0) { 
			if (expression[0].coeff == 0) answer[0] = "All real numbers"; 
				else answer[0] = "No solutions"; };
		if (degree == 1) { answer[0] = String.valueOf( -1*expression[1].coeff / expression[0].coeff ); };
		if (degree == 2) { 
			for ( i=0; i < numOfTerms; i++) {
				if (expression[i].expon == 2) a[2] = expression[i].coeff;
				if (expression[i].expon == 1) a[1] = expression[i].coeff;
				if (expression[i].expon == 0) a[0] = expression[i].coeff; 
				};
			if (a[1]*a[1]-4*a[2]*a[0] >= 0) {
				answer[0]=String.valueOf((-1*a[1]+Math.sqrt(a[1]*a[1]-4*a[2]*a[0]))/(2*a[2])); 
				answer[1]=String.valueOf((-1*a[1]-Math.sqrt(a[1]*a[1]-4*a[2]*a[0]))/(2*a[2])); }
				else answer[0] = "No solution in the real numbers.";
				};
		if (degree > 2) answer[0] = "This program currently only solves equations up to degree 2.";
		return answer;
		 
	}  // End of solveEq
	
	public static String round2LineAnswer ( String[] twoLineAnswer ) {

		double line1ofAnswer = Double.parseDouble(twoLineAnswer[0]), line2ofAnswer = Double.parseDouble(twoLineAnswer[1]);
		line1ofAnswer = line1ofAnswer*1000; line2ofAnswer = line2ofAnswer*1000;
		line1ofAnswer = Math.round(line1ofAnswer); line2ofAnswer = Math.round(line2ofAnswer);
		line1ofAnswer = line1ofAnswer/1000; line2ofAnswer = line2ofAnswer/1000; 
		String result = Double.toString( line1ofAnswer ) + "," + Double.toString( line2ofAnswer ); 
		return result;
	}
	
	public static void AITest() {
		
		int i;
		boolean passed, allPassed = true; 
		String origExpression, finalExpr;
		String[] equationAnswer;
		Term[] exprTerms = initArrayOfTerms();
		String ANSI_RED = "\u001B[31m";
		String ANSI_BLACK = "\u001B[30m";
		
		String[] testName = { "Multiple Signs/Extra Characters", "Multiple Variables", "Combine Like Terms - No Parentheses", "Single Parentheses", "Double Parentheses", "Nested Parentheses",
				"Adjacent Parentheses", "Linear Equation", "Quadratic Equation" };
		String[] origExpr = { "-&-2+-@-3x-+(2x++6)-", "4x - 2y + 6 - x + y^2 + y -3", "5x - 2x^2 + 4 - 7x + x^2 +1 - 6x^3", "3x+-5(3x-6+4x^2-5x-7)2-5-", "3x+1x(3x-6+4)+5-(2x^2-5x-7)-5--7x", 
				"-3x+x(3x-6+4(2x^2-5x-7)--5)-7x", "--3x+1x(3x-6+4)(2x^2-5x-7)-5-7x", "-3( 2x - 5 ) -7( x + 4 ) = 5( -3x - 2 ) + 9",
				"3x( 2x - 5 ) - 7( x^2 + 4x - 3 ) = -5x( 3x - 2 ) + 9x^2" };
		String[] answer = { "1.0x^1-4.0", "1.0y^2+3.0x^1-1.0y^1+3.0", "-6.0x^3-1.0x^2-2.0x^1+5.0", "-40.0x^2+23.0x^1+125.0", "1.0x^2+13.0x^1+7.0", "8.0x^3-17.0x^2-39.0x^1",
				"6.0x^4-19.0x^3-11.0x^2+10.0x^1-5.0", "6.0", "10.188,0.412"};
		
		System.out.println( " A I  T E S T : \n" );
		
		for ( i=0; i < testName.length; i++ ) {
			
			System.out.println( "TEST: " + testName[i] );
			passed = false;
			origExpression = prepareExpOrEq( origExpr[i] ); 
			if ( origExpression.indexOf('=') == -1 ) {
				exprTerms = simplifyExpr( prepareExpOrEq( origExpr[i] ));
				finalExpr = prepareExpOrEq(convertToStr( exprTerms ));
				System.out.println( "Original expression: " +  origExpr[i] + "   Simplified expression: " + finalExpr + "   Correct Answer: " + answer[i] );
				}
			else {
				equationAnswer = solveEquation( simplifyEquation( origExpression) );
				if ( testName[i] == "Quadratic Equation" ) {   // Handle the special case of a quadratic equation with two answers
					finalExpr = round2LineAnswer( equationAnswer );
					}
				else finalExpr = prepareExpOrEq( equationAnswer[0] );
				 
				System.out.println( "Original equation: " +  origExpr[i] + "   Calculated answer(s): " + finalExpr + "   Correct Answer: " + answer[i] );
			}
			
			if ( finalExpr.compareTo( answer[i] ) == 0) { passed = true; }
				else { allPassed = false; };
			System.out.println( "TEST: " + testName[i] + "  Passed = " + ANSI_RED + passed + ANSI_BLACK + "\n");
		
			};
			
		/*    The AI code was not designed to handle this test since can only handle two sets of parentheses
		Double Parentheses with Adjacent;  String str5 = "2 - 3x( 4x^2 - 5x ) - 5( x + 2 )( 6x - 9 )", ans5 = "-12.0x^3-15.0x^2-15.0x^1+92.0"; */
			
		System.out.println( "All Tests Passed = " + ANSI_RED + allPassed + ANSI_BLACK + "\n\n");
		
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
			expression = simplifyEquation( expression );  // simpEquation uses simplifyExpr to simplify both sides separately then combine all terms on the left side
			System.out.println( "Simplified and combined equation: " + expression );
			answer = solveEquation( expression );   // solveEq distinguishes the degree of the equation and solves it accordingly (up to degree 2)
			System.out.print( "Solution Set: { " ); 
			System.out.print( answer[0]);
			if (answer[1]!="") { System.out.print(", " + answer[1] + " }"); } 
				else { System.out.println( " }" ); };
			};	
		}  

}
