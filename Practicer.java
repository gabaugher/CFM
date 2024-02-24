//  Practicer class, a main component of the CFM Project
//  Currently, the Practicer component has a text-based console interface but will have 
//  a graphical interface installed in coming months.
//  By G. Baugher and S. Ngo

package cfm_files;

import java.util.Random;
import java.util.Scanner;
import java.lang.Math;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import cfm_files.AI.*;

public class Practicer {
	
	// Globally, declare variables, JDBC objects, and connectionURL
	static Scanner sc = new Scanner(System.in);	static int studID = 10180394; static int enrollmentID; 
	static Connection con = null; static Statement stmt = null; static ResultSet rs = null;
	static String connectionUrl = "jdbc:sqlserver://;servername=csdata.cd4sevot432y.us-east-1.rds.amazonaws.com;"
			+ "user=csc312cloud;password=c3s!c2Cld;" + "databaseName=CFM;"; String table = "PractScores";
			
public Practicer( int studID ) {
	
	double percent = 0.0;
	int stChoice = -1;
	boolean exitProgram = false;
	Scanner sc = new Scanner( System.in );

	// studentID = getStudentID( sc );   //  Input the student's ID number from the administrator
	while (!exitProgram )
		{
		//  Displays main dashboard and gives the student a chance to exit if that option is chosen.
		enrollmentID = getEnrollID( studID ); 
		displayDashboard();
		stChoice = sc.nextInt();
		switch (stChoice)  {
			case 1: { percent = (Math.round(intOperPract()*10)/10); System.out.println( "Your score with Integer Operations was " + percent + "%" ); break; }
			case 2: { percent = (Math.round(distPract()*10)/10); System.out.println( "Your score with Distributing was " + percent + "%" ); break; }
			case 3: { percent = (Math.round(simplifyPract()*10)/10); System.out.println( "Your score with Distributing and Combining Terms was " + percent + "%" ); break; } 
			case 4: { percent = (Math.round(basicEqPract()*10)/10); System.out.println( "Your score with Solving Basic Equations was " + percent + "%" ); break; }
			case 5: { percent = (Math.round(multiEqPract()*10)/10); System.out.println( "Your score with Solving Multi-step Equations was " + percent + "%" ); break; }
			case 6: { exitProgram = true; };
			};
		};
		sc.close();
		System.out.println("END OF PROGRAM");
}

	public static int getEnrollID( int studID )  //  Accesses DB, determines and reads in the most recent EnrollmentID for a given StudID 
		{
		int enrollID = 0;
		try {
	    	// Establish the connection.
	    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    	con = DriverManager.getConnection(connectionUrl);
 	    	stmt = con.createStatement();
	    	String sql;
	    	
	    	// Create and execute an SQL statement that returns the value in the specified column for the specified enrollmentID (the old value)
	    	sql = "SELECT MAX(e.EnrollmentID) FROM Student s JOIN Enrollment e on (s.StudID = e.StudID) WHERE s.StudID = " + String.valueOf(studID) + ";";
	    	rs = stmt.executeQuery(sql);
	    	ResultSetMetaData meta = rs.getMetaData();
	    	int columns = meta.getColumnCount();
	    	if ((columns == 1) && (rs.next())) enrollID = rs.getInt(1);
    	}
		// Handle any errors that may have occurred.
		catch (Exception e) { e.printStackTrace(); }
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
	    		if (stmt != null) try { stmt.close(); } catch(Exception e) {}
	    		if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return enrollID;
	}
			

	public static int getScore( int enrollID, String columnName )  //  Accesses DB to get a specific Practice Score for a particular EnrollmentID 
		{
    	int score = 0;		
		try {
	    	// Establish the connection.
	    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    	con = DriverManager.getConnection(connectionUrl);
 	    	stmt = con.createStatement();
	    	String sql;
	    	
	    	// Create and execute an SQL statement that returns the value in the specified column for the specified enrollmentID (the old value)
	    	sql = "SELECT " + columnName + " FROM PractScores WHERE EnrollmentID = " + String.valueOf(enrollID) + ";";
	    	rs = stmt.executeQuery(sql);
	    	ResultSetMetaData meta = rs.getMetaData();
	    	int columns = meta.getColumnCount();
	    	if ((columns == 1) && (rs.next())) score = rs.getInt(1);
    	}
		// Handle any errors that may have occurred.
		catch (Exception e) { e.printStackTrace(); }
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
	    		if (stmt != null) try { stmt.close(); } catch(Exception e) {}
	    		if (con != null) try { con.close(); } catch(Exception e) {}
		}	
		return score;
	}
		

	public static void storeScore( int enrollID, String columnName, int score )  //  Stores in the DB a specific new Score if it is higher than current recorded score 
		{
		try {
	    	// Establish the connection.
	    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    	con = DriverManager.getConnection(connectionUrl);
            // Create and execute an SQL statement that returns the most recent (highest) EnrollmentID of the student
	    	stmt = con.createStatement();
	    	String sql;
	    	
	    	// Create and execute an SQL statement that returns the value in the specified column for the specified enrollmentID (the old value)
	    	sql = "SELECT " + columnName + " FROM PractScores WHERE EnrollmentID = " + String.valueOf(enrollID) + ";";
	    	rs = stmt.executeQuery(sql);
	    	ResultSetMetaData meta = rs.getMetaData();
	    	int columns = meta.getColumnCount();
	    	int oldScore = 0; 
	    	if ((columns == 1) && (rs.next())) 
	    		oldScore = rs.getInt(1); 
	    	
	    	// Create and execute an SQL statement that inserts the new score if higher than the old score
	    	// System.out.println( "OldScore:  " + oldScore + "  NewScore: " + score );
	    	if (oldScore < score) { 
	    		sql = "UPDATE PractScores SET " + columnName + " = " + String.valueOf(score) + " WHERE EnrollmentID = " + String.valueOf(enrollID) + ";";	
	    		try { stmt.executeQuery(sql); } catch (Exception e) {} }
			}
			
			// Handle any errors that may have occurred.
			catch (Exception e) { e.printStackTrace(); }
			finally {
				if (rs != null) try { rs.close(); } catch(Exception e) {}
		    		if (stmt != null) try { stmt.close(); } catch(Exception e) {}
		    		if (con != null) try { con.close(); } catch(Exception e) {}
			}	
	}


	public static double intOperPract()	  //  Option #1 - This repeatedly gives the user integer operations practice, updating the scores to the DB.
		{
		int stAns = 1, score = 0, num = 0, a = 0, b = 0, n = 10, operInt = 0, currentIntScore = 0;
		double percent = 0;
		String operation = ""; Random r = new Random();
		// Display of current recorded score
		System.out.println( "\nYour current recorded score for a set of ten Integer Operations is " + getScore( enrollmentID, "IntOpMax" ));
		
		// Practice in integer operations
		while (stAns!=0) {
			n = 0; operInt = r.nextInt(4);
			while (n == 0) { b = -9 + r.nextInt(18); if (operInt == 3) { a = b*(-9 + r.nextInt(18)); } else { a = -9 + r.nextInt(18); }; n = a*b; };
			if (operInt == 0) { operation = "+"; n = a+b; }; 
			if (operInt == 1) { operation = "-"; n = a-b; };
			if (operInt == 2) { operation = "*"; n = a*b; }; 
			if (operInt == 3) { operation = "/"; n = a/b; };
			if (b<0) System.out.print( "\nWhat is " + a + operation + "(" + b + ") ?   (enter 0 to exit)" );
				else System.out.print( "\nWhat is " + a + operation + b + " ?   (enter 0 to exit)" );
	
			stAns = sc.nextInt(); num++;
			if (stAns != 0) {
				if (stAns == n) { score++; System.out.println( "     CORRECT !"); }
					else { System.out.println( "     Not Correct."); } }
			else { 
				if (n == 0) { score++; System.out.println( "     CORRECT !" ); stAns = 1; }
					else num--; };
		};
		
		//  This calculates the percentage correct and if for at least 10 problems records the score in the database
		if (num != 0) percent = (double) 100*score/num;  
		System.out.println( "\nYou correctly calculated " + score + " integer operations out of " + num + " operations."  );
		sc.nextLine();
		currentIntScore = (int) Math.round(percent);
		if (num>=10) { 
			storeScore( enrollmentID, "IntOpMax", currentIntScore );
			System.out.println( "\nYour score of " + currentIntScore + "% (out of at least 10 problems) was stored only if it was the highest to date." ); } 
		return percent;
	};

	public static double distPract()	  //  Option #2 - This repeatedly gives distributing problem practice, updating the scores to the DB.
		{
		int i, a = 1, len = 0, stAns = 1, score = 0, num = 0, n = 0, numOfTerms, expon = 0, currentIntScore = 0;
		int coef[] = new int[4]; for (i=0; i<4; i++) coef[i] = 1;  
		String monomial = "", polynomial = "", fullExpr = "", stAnsStr = "", correctAns = "", shortAns = ""; 
		char letter, ch;
		double percent = 0;	Random r = new Random();  
	
		// Display of current recorded score
		System.out.println( "\nYour current recorded score for Distributing Monomials is " + getScore( enrollmentID, "DistMax" ));
	
		// Practice in distributing
		while (stAns!=0) {
			polynomial = "";   
			System.out.println( "\nDistribute the monomial to each term in the parentheses, using ^ for exponents as in the example: -3X^2 + 24x - 21.\n" );
			numOfTerms = r.nextInt(2)+2; 
			letter = (char) (97 + r.nextInt(26)); if (letter == 'o') letter = (char) (97 + r.nextInt(26));
			n = 1; expon = r.nextInt(4);  
			for (i=1; i<numOfTerms; i++) { coef[i] = -9 + r.nextInt(18); n = n*coef[i]; }
			a = 0; 
			while (a==0) { a = -9 + r.nextInt(18); if (a==1) a = -9 + r.nextInt(18); }
			if (expon == 0) { monomial = Integer.toString( a ); } 
				else monomial = Integer.toString(a) + letter + "^" + Integer.toString(expon);  
			for (i=numOfTerms; i>0; i-- ) {
				if (i>0) { polynomial +=  Integer.toString(coef[i]) + letter + "^" +  Integer.toString(i); }
				else polynomial +=  Integer.toString(coef[i]);
				if (i>1) polynomial += " + "; };
				fullExpr = monomial + "(" + polynomial + ")";
				System.out.print( "\nDistribute:  " + fullExpr + "  Enter the answer (or type 0 to exit)" );		
				stAnsStr = sc.next();  
				
				// Effort to determine if user wants to exit out
				len = stAnsStr.length();
				if (len>0) {
					if (len == 1) { ch = stAnsStr.charAt(0);
						if ( ch == '-') stAns = -1;
						if ( ch == '+') stAns = 1; 
						if (Character.isDigit( ch )) stAns = Integer.parseInt(stAnsStr);  
						}
					else if (len>1) {
							ch = stAnsStr.charAt(0); 
							i = 0;
							while (((ch == '-') || (ch == '+')) || (Character.isDigit(ch))) { i++;
								ch = stAnsStr.charAt(i); }
							shortAns = stAnsStr.substring(0, i);
							len = shortAns.length();
							stAns = Integer.parseInt( shortAns );
						}
					}
				else stAns = 0;
				
				// Processing of student answer and determination of correct answer. 
				if (stAns != 0) { num++;
					stAnsStr = AI.prepareExpOrEq(stAnsStr);
					stAnsStr = AI.convertToStr( AI.determineExpr( stAnsStr, 0, stAnsStr.length())); 
					stAnsStr = AI.prepareExpOrEq( stAnsStr );
					fullExpr = AI.prepareExpOrEq( fullExpr );
					correctAns = AI.prepareExpOrEq( AI.convertToStr( AI.simpSingleParExpr( fullExpr ) ) );
					System.out.println( "\nYour answer: " + stAnsStr + "   Correct Distributed Expression: " + correctAns );
					if (stAnsStr.compareTo(correctAns) == 0) { score++; System.out.println( "     CORRECT !" ); }
						else { System.out.println( "     Not Correct." ); };
					};	
			};

		//  This calculates the percentage correct and if for at least 10 problems records the score in the database
		if (num>0) {percent = (double) 100*score/num;    
					System.out.println( "\nYou correctly calculated " + score + " distributing problems out of " + num + " problems."  ); };
		sc.nextLine();
		if (num>=10) { 
			storeScore( enrollmentID, "DistMax", currentIntScore );
			System.out.println( "\nYour score of " + currentIntScore + "% (out of at least 10 problems) was stored only if it was the highest to date." ); } 
		return percent;
	}; // End of distPract()

	public static double simplifyPract() {   //  Option #3 - This needs to be finished.
	
		int i, stAns = 1, score=0, num=0, currentIntScore = 0; 
		double percent = 0; 
		int type[] = { 0, 0, 0, 0 };
		int coef[] = new int[4]; for (i=0; i<4; i++) coef[i] = 1;  
		String prompt = "", fullExpr = "", stAnsStr = "", correctAns = "";
		boolean okEntry = false, exiting = false;  
	
		// Display of current recorded score
		System.out.println( "\nYour current recorded score for Simplifying Expressions is " + getScore( enrollmentID, "SimpMax" ));
	
		// Practice in distributing and combining terms
		while (!exiting) {
			okEntry = false;  
			while (!okEntry)
			{ System.out.println( "\nWhich type?  c (combining only), s (single parentheses),  d (double parentheses),  n (nested parentheses)  (type e to exit)");
			stAnsStr = sc.next();
			if (stAnsStr.compareTo("c") == 0) { stAns = 1; prompt = "Combine all like terms:"; okEntry = true; }
			if (stAnsStr.compareTo("s") == 0) { stAns = 2; prompt = "Distribute and then combine like terms:"; okEntry = true; }
			if (stAnsStr.compareTo("d") == 0) { stAns = 3; prompt = "Distribute for both sets of parentheses, then combine like terms:"; okEntry = true; }
			if (stAnsStr.compareTo("n") == 0) { stAns = 4; prompt = "Distribute to the inner parentheses, combine terms, distribute to the outer parentheses,"
					+ " then combine terms again:"; okEntry = true; }
				if (stAnsStr.compareTo("e") == 0) { okEntry = false; exiting = true; };  
				if ((okEntry) && (!exiting)) {  
					type[stAns]++; 
					if (stAns == 1) fullExpr = combineTerms();
					if (stAns == 2) fullExpr = distAndComb();
					if (stAns == 3) fullExpr = distTwoPar();
					if (stAns == 4) fullExpr = distNestPar();
					System.out.println( "Enter your answer using ^ for exponents as in the example: -3X^2+24x-21" );
					System.out.print( "\n" + prompt + fullExpr + "  Enter here: ");
				};
				if (okEntry)
					if (stAns != 0)
					{ 	num++; 
						stAnsStr = sc.next(); stAnsStr = AI.prepareExpOrEq(stAnsStr);
					stAnsStr = AI.convertToStr( AI.determineExpr( stAnsStr, 0, stAnsStr.length())); 
					stAnsStr = AI.prepareExpOrEq( stAnsStr );
					fullExpr = AI.prepareExpOrEq( fullExpr ); 
					if (stAns == 2) fullExpr = AI.convertToStr( AI.simpSingleParExpr( fullExpr ));
					if (stAns == 3) fullExpr = AI.convertToStr( AI.simpDoubleParExpr( fullExpr ));
					if (stAns == 4) fullExpr = AI.convertToStr( AI.simpNestParExpr( fullExpr )); 
					correctAns = AI.prepareExpOrEq( AI.convertToStr( AI.combineLikeTerms( AI.determineExpr( fullExpr, 0, fullExpr.length() ) ) ) );
					System.out.println( "\nYour answer: " + stAnsStr + "   Correct Distributed Expression: " + correctAns );
					if (stAnsStr.compareTo(correctAns) == 0) { score++; System.out.println( "     CORRECT !"); }
						else { System.out.println( "     Not Correct."); };
						};
				};
			};
			
			//  This calculates the percentage correct and if for at least 10 problems records the score in the database
			percent = 0;
			if (num>0) {percent = (double) 100*score/num;    
						System.out.println( "\nYou correctly calculated " + score + " distributing/combining terms problems out of " + num + " problems."  ); };
			sc.nextLine();
			if (num>=10) { 
				storeScore( enrollmentID, "SimpMax", currentIntScore );
				System.out.println( "\nYour score of " + currentIntScore + "% (out of at least 10 problems) was stored only if it was the highest to date." ); } 
			return percent;
			}

	public static String combineTerms() {  //  Returns a set of several terms to be combined (belongs to Option #3) 
		
		int i = 0, beginNum = 2, endNum = 2;
		int coef[] = new int[6]; 
		for (i=0; i<6; i++) coef[i] = 1;
		int expon[] = new int[6]; 
		for (i=0; i<6; i++) expon[i] = 1;
		char letter; String beginStr = "", endStr = "", expStr = "";
		Random r = new Random();
	
		letter = (char) (97 + r.nextInt(26)); if (letter == 'o') letter = (char) (97 + r.nextInt(26));
		beginNum = 1+ r.nextInt(4); 
		endNum = 1 + r.nextInt(4); 
		for (i=1; i<beginNum; i++)
			{ expon[i] = r.nextInt(3); coef[i] = -9+r.nextInt(18);
			if (coef[i] != 0) {
				if (expon[i]==0) beginStr += Integer.toString( coef[i] ) + "+";
					else beginStr += Integer.toString( coef[i] ) + letter + "^" + Integer.toString( expon[i] ) + "+"; }; };
		for (i=1; i<endNum; i++)
			{ expon[i] = r.nextInt(3); coef[i] = -9+r.nextInt(18);
			if (coef[i] != 0) {
				if (expon[i]==0) endStr += "+" + Integer.toString( coef[i] );
					else endStr += "+" + Integer.toString( coef[i] ) + letter + "^" + Integer.toString( expon[i] ); }; };
			expStr = AI.prepareExpOrEq( beginStr + endStr );

		return expStr;
	}

	public static String distAndComb() {	//  This method generates a distributing problem surrounded by two expressions (belongs to Option #3)
	
		int i, a, exponent, numOfTerms, beginNum = 2, endNum = 2;
		int coef[] = new int[6]; for (i=0; i<6; i++) coef[i] = 1;
		int expon[] = new int[6]; for (i=0; i<6; i++) expon[i] = 1;
		char letter; String beginStr = "", endStr = "", monomial = "", polynomial = "", expStr = "";
		Random r = new Random();
	
		numOfTerms = 2 + r.nextInt(2);
		letter = (char) (97 + r.nextInt(26)); if (letter == 'o') letter = (char) (97 + r.nextInt(26));
		beginNum = 1+ r.nextInt(4); endNum = 1 + r.nextInt(4); exponent = r.nextInt(3);  
		for (i=1; i<beginNum; i++)
			{ expon[i] = r.nextInt(3); coef[i] = -9+r.nextInt(18);
			if (coef[i] != 0) {
				if (expon[i]==0) beginStr += Integer.toString( coef[i] ) + "+";
					else beginStr += Integer.toString( coef[i] ) + letter + "^" + Integer.toString( expon[i] ) + "+"; }; };
		for (i=1; i<endNum; i++)
			{ expon[i] = r.nextInt(3); coef[i] = -9+r.nextInt(18);
			if (coef[i] != 0) {
				if (expon[i]==0) endStr += "+" + Integer.toString( coef[i] );
					else endStr += "+" + Integer.toString( coef[i] ) + letter + "^" + Integer.toString( expon[i] ); }; };
					
			for (i=1; i<numOfTerms; i++) { coef[i] = -9 + r.nextInt(18); expon[i] = r.nextInt(3); };
			a = 0; 
			while (a==0) { 
				a = -9 + r.nextInt(18); 
				if (a==1) a = -9 + r.nextInt(18); 
				};
			if (exponent == 0) { monomial = Integer.toString( a ); } 
				else monomial = Integer.toString(a) + letter + "^" + Integer.toString(exponent);  
			for (i=numOfTerms; i>0; i-- ) {
				if (i>0) { polynomial +=  Integer.toString(coef[i]) + letter + "^" +  Integer.toString(i); }
					else polynomial +=  Integer.toString(coef[i]);
				if (i>1) polynomial += " + "; 
				};
			expStr = beginStr + monomial + "(" + polynomial + ")" + endStr;
			
			return expStr;
	}

	public static String distTwoPar() {   //  This method belonging to Option #3 needs to be written.
		String expStr = "";
		return expStr;
	}
	
	public static String distNestPar() {     //  This method belonging to Option #3 needs to be written.
		String expStr = "";
		return expStr;
	}

	public static int gcd(int n1, int n2)  {  //  Finds the greatest common divisor. Used in basicEqPract(), the 4th option.
		if (n2==0) return n1; 
		return gcd( n2, n1%n2); 
		};

	public static double basicEqPract() {	//  Option #4 - This repeatedly gives the user basic equations and leads them through solving them.
	
		// Practice in solving basic equations
		int a, b, c = 1, d = 1, k, n, x=1, score = 0, num = 0, currentIntScore = 0; 
		String response = "", responseWord = "", leadCoef = ""; 
		char sign = '+';
		boolean solved = false, responseOk = false;
		double percent = 0; 
		Random r = new Random(); 
		int rand = r.nextInt(18);
		
		// Display of current recorded score
		System.out.println( "\nYour current recorded score for Solving Basic Equations  is " + getScore( enrollmentID, "BasEqMax" ));
	
		// Creation and display of an equation to be solved
		while (response.compareTo("e") != 0)  {
			rand = r.nextInt(10); a = 0; b = 0;  
			leadCoef = ""; sign = '+'; solved = false;
			if (rand<7) {
				while ( a*b == 0 ) { a = -9 + r.nextInt(18);  b = -9 + r.nextInt(18); };
					x = -9 + r.nextInt(18); n = a*x+b; leadCoef = Integer.toString(a); if (b<0) sign = '-'; }
			else {
				while ( a*b*c == 0 ) { 
					a = -9 + r.nextInt(18); 
					b = -9 + r.nextInt(18); 
					c = r.nextInt(7); 
					}
				d = gcd( Math.abs(a), c ); 
				a = a/d; 
				c = c/d; 
				x = c*(-9+r.nextInt(18)); 
				n = a*x/c + b;
				if (b<0) sign = '-';
				leadCoef = Integer.toString(a) + "/" + Integer.toString(c); 
				};
			System.out.println( "\n#" + (num+1) + "  Solve the equation: " + leadCoef + "X" + sign + Math.abs(b) + " = " + n ); 
			
			// This leads the user in solving the equation and determines when it has been solved
			while (!solved) {
				System.out.print( "\nWhich operation should you use? Enter a (add), s (subtract), m (multiply), or d (divide) ?   (e to exit)");
				response = sc.next(); 
				if (response.compareTo("e") == 0 ) break;
				if (response.compareTo("a") == 0) responseWord = "add to"; 
				if (response.compareTo("s") == 0) responseWord = "subtract from";
				if (response.compareTo("m") == 0) responseWord = "multiply by on"; 
				if (response.compareTo("d") == 0) responseWord = "divide by on";
				System.out.print( "\nWhat number do you want to " + responseWord + " both sides ?" );
				k = sc.nextInt();
				if (((response.compareTo("m")==0) || (response.compareTo("d")==0)) && (k == 0)) {
					System.out.println( "\nMultiplying by 0 does not help and dividing by 0 is not acceptable!"); 
					response = "e"; break;
					};
				if ((response.compareTo("a") == 0)||(response.compareTo("s") == 0)) { 
					responseOk = true;
					if (response.compareTo("a") == 0) { 
						b = b+k; n = n+k; } 
						else { b = b-k; n = n-k; };  
					if (Math.abs(b)>10) System.out.println( "\nTry to make the constant term disappear on the left side."); }
						else {
							if (b!=0) { System.out.println( "\nThe best approach is to eliminate the constant term on the left first."); };
							if (response.compareTo("m") == 0) { a = a*k; b = b*k; n = n*k; responseOk = true; };
							if (response.compareTo("d") == 0) {
								if (((Math.abs(k)>Math.abs(a)) || !(Math.abs(a)%Math.abs(k)==0)) || k==1)
									{ System.out.println( "\nThis does not help. Try again. "); }
									else { a = a/k; b = b/k; n = n/k; responseOk = true; }; 
								};
							if (!responseOk) System.out.println( "\nResponse not appropriate. Try again."); };
							if (rand > 7) {
								if ((a%c) == 0) {
									a = a/c; rand = 1; 
									sign = '+'; 
									if (b<0) sign = '-';  
									if (b == 0) System.out.println( "\n(" + a*c + "/" + c + ")X = " + n + " reduces to the equation " + a + "X = " + n );
										else System.out.println( "\n(" + a*c + "/" + c + ")X " + sign + Math.abs(b) + " = " + n + " reduces to the equation " + a + "X " + sign + Math.abs(b) + " = " + n );  
									c = 1; }; 
								};
							if (b<0) sign='-'; 
								else sign = '+';
							if (rand<8) { leadCoef = Integer.toString(a); }
								else { leadCoef = Integer.toString(a) + "/" + Integer.toString(c); };
							if (b==0) { System.out.println( "\nCurrent equation (after chosen operation):   " + leadCoef + "X = " + n ); }
								else { System.out.println( "\nCurrent equation (after chosen operation):  " + leadCoef + "X" + sign + Math.abs(b) + " = " + n ); 
								};
						if (rand>7) 
							if (a%c == 0) { a = a/c; c = 1; };
						if ((b == 0) && ((a == 1) && (c==1))) {
							System.out.println( "\nGood job finding that X = " + n + " ! ! !\n"); solved = true; score++; };
				}
				num++;
			}
			if (num != 0) { 
				percent = (double) 100*score/num;    
				System.out.println( "\nYou correctly solved " + score + " basic equations out of " + num + " equations.\n"); 
				}
			sc.nextLine();
	
			//  This calculates the percentage correct and if for at least 10 problems records the score in the database
			currentIntScore = (int) Math.round(percent);
			if (num>=10) { 
				storeScore( enrollmentID, "BasEqMax", currentIntScore );
				System.out.println( "\nYour score of " + currentIntScore + "% (out of at least 10 problems) was stored only if it was the highest to date." ); 
				} 
		return percent;
	}

	public static double multiEqPract() {	//  Option #5 - This needs to be written.
		
		//  This will repeatedly give the user multi-step equations and leads them through solving them.
		int num=0, currentIntScore = 0; 
		double percent = 0;
	
		// Display of current recorded score
		System.out.println( "\nYour current recorded score for Solving Multi-Step Equations  is " + getScore( enrollmentID, "MultEqMax" ));
		currentIntScore = (int) Math.round(percent);
		if (num>=10) { 
			storeScore( enrollmentID, "BasEqMax", currentIntScore );
			System.out.println( "\nYour score of " + currentIntScore + "% (out of at least 10 problems) was stored only if it was the highest to date." ); 
			} 
		return percent;
	}

	public static void displayDashboard()	{   //  This displays the Practicer menu (will be replaced with a GUI)
		System.out.println("\n\n=========================================================================================================\n");
		System.out.println( "\t\t\t\t Challenges in Foundational Math - Practice Dashboard\n");
		System.out.println("=========================================================================================================\n");
		System.out.println("\t\t1.  Practice Integer Operations\n");
		System.out.println("\t\t2.  Practice Distributing\n");
		System.out.println("\t\t3.  Practice Simplifying (Distributing and Combining Terms)\n");
		System.out.println("\t\t4.  Practive Solving Basic Equations\n");
		System.out.println("\t\t5.  Practice Solving Multi-Step Equations\n");
		System.out.println("\t\t6.  Exit the Practice Program\n");
		System.out.print("\nPlease enter your choice:  ");
	}

	public static void main( String[] args) {
    
		Practicer pr = new Practicer( 10180394 );  // For testing purposes only
	
	}

}
