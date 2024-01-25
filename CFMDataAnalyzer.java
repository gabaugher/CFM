package cfm_files;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class CFMDataAnalyzer {
	
	double[][] distance;
	
	public class UserAccount {
		
		private String userIDString;
		private int recentEnrollID;
		private String email;
		private String fullName;
		private TreeMap<Integer, String> incorrectList; //will contain question numbers and responses of incorrectly answered questions
		private TreeMap<Integer, Double> gradeList;  
		
		public UserAccount(String userIDString, String email, String fullName) {
			super();
			this.userIDString = userIDString;
			this.email = email;
			this.fullName = fullName;
			this.incorrectList = new TreeMap<Integer,String>(); 
			this.gradeList = new TreeMap<Integer,Double>(); }
		
	    public String getEmail() { return email; }
		
	    public void setEmail(String email) 
	        { this.email = email; }
		
	    public String getFullName() { return fullName; }
		
	    public void setFullName(String fullName) { this.fullName = fullName; }
		
	    public String getUserIDString() { return userIDString; }
	    
	    public int getrecentEnrollID() { return recentEnrollID; }
		
	    public TreeMap<Integer, String> getIncorrectList() { return incorrectList; }
	    
	    public TreeMap<Integer, Double> getGradeList() { return gradeList; }
		
	    public void addIncorrectQuestion(int questionNum, String response, double grade ) { 
	        incorrectList.put(questionNum, response); 
	        gradeList.put(questionNum, grade );  
	        }	    
	    
	    public double getGrade( int questNum ) {    		    	
	    	return gradeList.get(questNum);
	    }
	}
	
	public class UserData {
	    private TreeMap<String, UserAccount> accounts;
	    
	    public UserData() {
	        super();
	        accounts = new TreeMap<>(); }
	    
	    public TreeMap<String, UserAccount> getAccounts() {
	        return accounts; }
	    
	    public void addAccount(UserAccount account) {
	        this.accounts.put(account.getUserIDString(), account); }
	    
	    public UserAccount getAccount(String userIDString) {
	        return accounts.get(userIDString); }
	}
		
	public static UserAccount getSimilarUserResponse(UserData accounts, String userIDString) {
		
        UserAccount account = accounts.getAccount( userIDString );
        return getSimilarUserResponse( accounts, account);
        }

	public static UserAccount getSimilarUserResponse(UserData accounts, UserAccount account1) {
				
	    int maxSimIndexResponse = 0;
	    
        int currentSimIndexResponse = 0;
        
        UserAccount currMaxUserResponse = null;
        

        //  For each user
        for (Map.Entry<String, UserAccount> entry : accounts.getAccounts().entrySet())  {

            //  get other User
            UserAccount account2 = entry.getValue();

            // if itself or the first one, skip
            if (account2.equals(currMaxUserResponse) || account2.equals(account1))  { continue; }

            //  calculate similarity indexes for both question response and grade
            currentSimIndexResponse = findSimilarityIndexReponse( account1, account2 );
            
             
            if ( currentSimIndexResponse > maxSimIndexResponse ) {
                maxSimIndexResponse = currentSimIndexResponse;
                currMaxUserResponse = account2; 
                }
            
            System.out.println( "SimIndex for Responses for " + entry.getKey( ) + " is " + currentSimIndexResponse );
            }
        System.out.println( "MaxResponsesIndex: (count) " + maxSimIndexResponse ); 
        return currMaxUserResponse;
	    }  
	
	private static int findSimilarityIndexReponse(UserAccount account1, UserAccount account2) {
	
	    //define return
	    int indexCounter = 0;
	
	    //for each question answered by the first User
	    for (Entry<Integer, String> entry : account1.getIncorrectList().entrySet()) {
	
	        //get the user ID
	        int questNum = entry.getKey();
	       
	        //check if the second user answered the question incorrectly (so in the list)
	        if (account2.getIncorrectList().containsKey( questNum )) { indexCounter++; }
	
	    }   
	    return indexCounter;
	}
	
	public static UserAccount getSimilarUserGrade(UserData accounts, String userIDString) {
		
        UserAccount account = accounts.getAccount( userIDString );
        
        return getSimilarUserGrade( accounts, account);
        }

	public static UserAccount getSimilarUserGrade(UserData accounts, UserAccount account1) {
		
	    double minSimIndexGrade = 500;
        
        double currentSimIndexGrade = 0; 
        
        UserAccount currMinUserGrade = null;

        //  For each user
        for (Map.Entry<String, UserAccount> entry : accounts.getAccounts().entrySet())  {

            //  get other User
            UserAccount account2 = entry.getValue();

            // if itself or the first one, skip
            if (account2.equals(currMinUserGrade) || account2.equals(account1))  { continue; }

            //  calculate similarity indexes for both question response and grade
             
            currentSimIndexGrade = findSimilarityIndexGrade( account1, account2 );           
    
            if ( currentSimIndexGrade < minSimIndexGrade ) {
                minSimIndexGrade = currentSimIndexGrade;
                currMinUserGrade = account2; 
                }
            System.out.println( "SimIndex for Grades for " + entry.getKey( ) + " is " + currentSimIndexGrade );
            }
        System.out.println( "MinGradeIndex: (Euclidean Distance) " + minSimIndexGrade ); 
        
        return currMinUserGrade;
	    }  
	
	private static double findSimilarityIndexGrade(UserAccount account1, UserAccount account2) {
		
	    //define return
	    double distance = 1;
	
	    //for each question answered by the first User
	    for (Entry<Integer, Double> entry : account1.getGradeList().entrySet()) {
	        
	        // get the questionNum
	        int questNum = entry.getKey();
	       
	        //check if the second user answered the question incorrectly (so in the list)
	        if (account2.getIncorrectList().containsKey( questNum )) { 

	        	distance = findEuclidDist( account1, account2, questNum ); 
	        	}	
	    }   
	    return distance;
	}
	
	public static double findEuclidDist( UserAccount account1, UserAccount account2, int questNum ) {
				
		//define sum of squares
		double sum = 0;
		double grade1 = 0, grade2 = 0;
		
		grade1 = account1.gradeList.get( questNum );

		grade2 = account2.gradeList.get( questNum );
				
		//calculate difference		
		double difference = grade1 - grade2;  
		
		//square the difference
		double squareOfDifference = difference*difference;
		//add to the sum
		sum += squareOfDifference;
					
		//add square root of sum as similarity 
		return Math.sqrt(sum);
	}
	
	public static void main( String[] args) {
		
	    CFMDataAnalyzer cda = new CFMDataAnalyzer(); 
		
		int[] question = {123, 112, 104, 125, 114, 119, 134};
	
	    UserAccount[] newAccount = new UserAccount[4];
	    newAccount[0] = cda.new UserAccount( "10230456", "izzy@aol.com", "Isabell Frank" );
	    newAccount[0].addIncorrectQuestion(question[0], "4", 0 );
	    newAccount[0].addIncorrectQuestion(question[1], "2x-5", 0.5 );
	    newAccount[0].addIncorrectQuestion(question[5], "4x", 0.25 );
	    newAccount[0].addIncorrectQuestion(question[3], "7x+2", 0.5 );
	    newAccount[1] = cda.new UserAccount( "10120457", "fred@comcast.com", "Fred Jones" );
	    newAccount[1].addIncorrectQuestion(question[4], "5x", 0.5 );
	    newAccount[1].addIncorrectQuestion(question[5], "4x", 0 );
	    newAccount[1].addIncorrectQuestion(question[6], "x-7", 0.5 );
	    newAccount[2] = cda.new UserAccount( "10230563","stevesmith@yahoo.com", "Steve Smith" );
	    newAccount[2].addIncorrectQuestion(question[1], "2x-5", 0.25 );
	    newAccount[2].addIncorrectQuestion(question[5], "4x", 0.75 );
	    newAccount[2].addIncorrectQuestion(question[3], "7x+2", 0.5 );
	    newAccount[3] = cda.new UserAccount( "10110299", "jenniferbeck@gmail.com", "Jennifer Beck" );
	    newAccount[3].addIncorrectQuestion(question[1], "5x", 0.75 );
	    newAccount[3].addIncorrectQuestion(question[5], "4x", 0.5 );
	    newAccount[3].addIncorrectQuestion(question[6], "x-7", 0 );
	
	    UserData newAccounts = cda.new UserData();
	    newAccounts.addAccount(newAccount[0]);
	    newAccounts.addAccount(newAccount[1]);
	    newAccounts.addAccount(newAccount[2]);
	    newAccounts.addAccount(newAccount[3]);
	
	    String knownUser = "10120457";
	    UserAccount SimilarUserResponse = getSimilarUserResponse( newAccounts, knownUser );
	    System.out.println(knownUser+ ", user " + newAccounts.getAccount(knownUser).getFullName() + " has similar user " + SimilarUserResponse.getFullName() + "\n" );
	
	    knownUser = "10120457";
	    UserAccount SimilarUserGrade = getSimilarUserGrade( newAccounts, knownUser );
	    System.out.println(knownUser+ ", user " + newAccounts.getAccount(knownUser).getFullName() + " has similar user " + SimilarUserGrade.getFullName() + "\n" );
	    
	    knownUser = "10230456";
	    SimilarUserResponse = getSimilarUserResponse( newAccounts, knownUser );
	    System.out.println(knownUser + ", user " + newAccounts.getAccount(knownUser).getFullName() + " has similar user " + SimilarUserResponse.getFullName() + "\n" );
	    
	    knownUser = "10230456";
	    SimilarUserGrade = getSimilarUserGrade( newAccounts, knownUser );
	    System.out.println(knownUser + ", user " + newAccounts.getAccount(knownUser).getFullName() + " has similar user " + SimilarUserGrade.getFullName() + "\n" );

	}

}

