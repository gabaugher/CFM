package cfm_files;

import cfm_files.AI.PrepExpr;
import cfm_files.AI.Term;

public class equatExprFact implements ExprFact {
	
	@Override
	public Expression createExpr( String preparedString ) {
		String equationStr = "";
		Term[] terms = new AI.Term[19];
		
		equationStr = simplifyAndMoveToLeft( preparedString ); 
		terms = AI.determineExpr( equationStr, 0, equationStr.length());
		terms = AI.combineLikeTerms( terms ); 
		
		Expression expression = new Expression(); 
		expression.string = preparedString; 
		System.out.println( "PreparedStr: " + preparedString );
		expression.answer = AI.solveEquation( equationStr );
		System.out.println( "Answer: " + expression.answer ); 
		
		return expression; 
	};

	public static String simplifyAndMoveToLeft( String equationStr ) {
		int location;
		String leftSide = "", rightSide = "", simplifiedEquation = "";
		Term[] leftExpr = Term.initArrayOfTerms();
		Term[] rightExpr = leftExpr;
		Term[] combinedExpr = leftExpr; 
		Term[] negOne = AI.determineExpr( "-1", 0, 2);
		
		equationStr = PrepExpr.removeSpaces( equationStr ); 
		System.out.println( "EquationStr: " + equationStr ); 
		location = equationStr.indexOf('=');
		leftSide = equationStr.substring(0,location); 
		rightSide = equationStr.substring(location+1);  
		System.out.println( "left: " + leftSide + "  right: " + rightSide);
	
		leftSide = PrepExpr.prepareExpOrEq( leftSide );
		rightSide = PrepExpr.prepareExpOrEq( rightSide );
		System.out.println( "prepped left: " + leftSide + "  prepped right: " + rightSide);
			
		leftExpr = AI.simplifyExpr( leftSide ); 
		rightExpr = AI.simplifyExpr( rightSide );
		System.out.println( "simplified left: " + AI.convertToStr( leftExpr ) + "  simplified right: " + AI.convertToStr( rightExpr ) );
		
		combinedExpr = AI.addTwoExpressions( leftExpr, AI.distributeMonomial( negOne, rightExpr ) );
		System.out.print( "Combined Sides: "); AI.displayExpr( combinedExpr );  
		
		if ( Term.numNonEmptyTerms( combinedExpr ) > 0 ) simplifiedEquation = AI.convertToStr( combinedExpr );
			else simplifiedEquation = "0";
		simplifiedEquation = PrepExpr.prepareExpOrEq( simplifiedEquation + "=0" );
		System.out.println( "Prepped Combined Sides: "+ simplifiedEquation );  
		return simplifiedEquation;
	}

}
