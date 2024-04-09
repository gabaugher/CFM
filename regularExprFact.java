package cfm_files;

import cfm_files.AI.ParPos;
import cfm_files.AI.Term;

public class regularExprFact implements ExprFact {
	
	@Override
	public Expression createExpr( String preparedString ) {
		
		Expression expression = new Expression(); 
		expression.string = preparedString; 
		System.out.println( "PreparedStr: " + preparedString );
		
		expression.originalType = determineExprType( preparedString );
		expression.terms = Terminator( expression );  //  Terminator creates terms from the string expression
		return expression;
	}
	
	public final static String determineExprType ( String expressionString ) {

		String exprType = null;
		
		// Determine positions of parentheses (and if correctly ordered or has error)
		ParPos pp = new ParPos(); 
		pp.determParPos(expressionString); 
		
		// Determine the type of expression: if no parentheses, single, double, or nested.
		if ((expressionString.indexOf('(')==-1) && (expressionString.indexOf(')')==-1)) exprType = "noPar";
			else if ((pp.Open2 == -1) && (pp.Close2 == -1)) exprType = "singlePar";
		if (( pp.Open2 != -1) && (pp.Close1 != -1))
			if (pp.Open2 < pp.Close1) exprType = "nestedPar";
				else exprType = "doublePar";  
		if (pp.Close1 == pp.Open2 - 1) { exprType = "adjacentPar"; };

		return exprType; 
	}

	public static Term[] Terminator( Expression expression ) {   //  Terminator creates terms from the string expression
		Term[] terms = new AI.Term[19];
		int length = expression.string.length(); 
		
		switch( expression.originalType ) { 
			case "noPar": {
				terms = AI.determineExpr( expression.string, 0, length ); 
				terms = AI.combineLikeTerms( terms ); };
				break;
			case "singlePar": {  
				terms = AI.simpSingleParExpr( expression.string ); };
				terms = AI.combineLikeTerms( terms ); 
				break;
			case "doublePar": { 
				terms = AI.simpDoubleParExpr( expression.string ); };
				terms = AI.combineLikeTerms( terms ); 
				break;
			case "nestedPar": { 
				terms = AI.simpNestParExpr( expression.string ); };
				terms = AI.combineLikeTerms( terms ); 
				break;
			case "adjacentPar": { 
				terms = AI.simpAdjacParExpr( expression.string ); };
				terms = AI.combineLikeTerms( terms ); 
				break;
			default:
				System.out.println( "No discernable type" ); 
			};
				
		return terms;
		}
	
}
