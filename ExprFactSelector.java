package cfm_files;

public class ExprFactSelector {

	public static ExprFact getFactory( String originalString ) {
		int equalSignLocation = -1; 
		ExprFact exprFact = null; 
			
		equalSignLocation = findEqualSignLocation( originalString );
		
		if ( equalSignLocation == -1 )
			exprFact = new regularExprFact( );
		else 
			exprFact = new equatExprFact( ); 
		
		return exprFact; 
	}
	
	public static int findEqualSignLocation( String expressionString ) {
		int i = 0, count = 0, location = -1, result;
		boolean equation = false; 
		
		// Determine if there are equal signs (and if more than one) and if one, set equation to true.
		for (i=0; i < expressionString.length(); i++ )  { 
			if ( expressionString.charAt(i) == '=' ) {
				count++; 
				location = i; 
				};	
			};
		equation = (count == 1);
		if (equation) result = location;
		else result = -1;
		
		return result; 
		}	
	
}
