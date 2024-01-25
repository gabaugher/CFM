//  ScenarioReader class, a main component of the CFM Project
//  Currently unfinished, this creates the GUI that will be used eventually to display the 
//  content, questions, graphics, etc. for each Scenario in the learning modules.
//  Eventually it will cycle the user through from where they left off to however far they
//  choose to go in the modules, stopping to administer quizzes after each module completion.
//  By G. Baugher

package cfm_files; 

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.swing.*;

@SuppressWarnings("serial")
public class ScenarioReader extends JPanel {
	
	static int studentID; 
	String question = null;
	String response = null, result = null;
	JPanel panel = new JPanel(); JPanel pan1 = new JPanel(); JPanel pan2 = new JPanel(); 
	JPanel pan3 = new JPanel(); JPanel pan4 = new JPanel();	JPanel pan5 = new JPanel(); 
	
	static Connection con = null; static Statement stmt = null; static ResultSet rs = null;
	static String connectionUrl = "jdbc:sqlserver://;servername=csdata.cd4sevot432y.us-east-1.rds.amazonaws.com;"
			+ "user=csc312cloud;password=c3s!c2Cld;" + "databaseName=CFM;"; String table = "";
	
	//  Get the most recent enrollment from the DB, then their full and first name
	int enrollID = Practicer.getEnrollID( studentID );
	int currScenario = getCurrScenNum( studentID );
	int questNum = 0; 
		 	
	String skill1 = "none", skill2 = "none"; String textField1 = "none", textField2 = "none"; 
	String imageFile = "M1S1Intro.png"; 
	
	public static int getCurrScenNum( int enrollID )  {  // Reads the current scenario number from the UserHistory table of the DB
				
		int currScenNum = 9; // Set to 9 as a default for testing
		
		try {
	    	// Establish the connection.
	    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    	con = DriverManager.getConnection(connectionUrl);
 	    	stmt = con.createStatement();
	    	String sql;
	    	
	    	// Create and execute an SQL statement that returns the value in the specified column for the specified enrollmentID (the old value)
	    	sql = "SELECT CurrScenNum FROM UserHistory WHERE EnrollmentID = " + String.valueOf( enrollID ) + ";";
	    	rs = stmt.executeQuery(sql);
	    	ResultSetMetaData meta = rs.getMetaData();
	    	int columns = meta.getColumnCount();
	    	if ((columns == 1) && (rs.next())) currScenNum = rs.getInt(1);
    		}
		// Handle any errors that may have occurred.
		catch (Exception e) { e.printStackTrace(); }
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
	    		if (stmt != null) try { stmt.close(); } catch(Exception e) {}
	    		if (con != null) try { con.close(); } catch(Exception e) {}
			}
		
		return currScenNum;
	}  // End of getCurrScenNum
	
	public String[] createTextLines ( int currScenario, boolean intro ) {  // Divides the scenario introduction (hundreds of characters) into lines of a particular length
		int i = 0, j = 0, k = 0; 
		String longText = ""; 
		String[] text = new String[20];			// Note: for some reason the last partial line of text is not displayed in the ScenarioReader
			
		try {  // Establish the connection.
	    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    	con = DriverManager.getConnection(connectionUrl);
 	    	stmt = con.createStatement();
	    		    	
	    	// Create and execute an SQL statement that returns the value in the specified column for the specified enrollmentID (the old value)
 	    	String sql;
 	    	if (intro) {
	    		sql = "SELECT ScenIntroText FROM Scenario WHERE ScenNum = " + String.valueOf(currScenario) + ";"; }
	    	else { 
	    		sql = "SELECT ScenExamples FROM Scenario WHERE ScenNum = " + String.valueOf(currScenario) + ";";
	    	}
 	    	
	    	rs = stmt.executeQuery(sql);
	    	ResultSetMetaData meta = rs.getMetaData();
	    	int columns = meta.getColumnCount();
	    	if ((columns == 1) && (rs.next())) longText = rs.getString(1);
    		}
		// Handle any errors that may have occurred.
		catch (Exception e) { e.printStackTrace(); }
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
	    		if (stmt != null) try { stmt.close(); } catch(Exception e) {}
	    		if (con != null) try { con.close(); } catch(Exception e) {}
			}	
		longText = " " + longText.trim(); 
		
		int lineLen = 72, index = 0; i = 0; 
		k = longText.length(); 
		if (k > lineLen) {
			while (k > lineLen) {
				j = longText.indexOf(' ', lineLen*index + lineLen ); 
				if ( j > 0 ) {
					text[index] = longText.substring(i, j);
					k -= (j-i); 
					i = j; index++; }
				else text[index] = longText.substring(i); 	// Note: for some reason the last partial line of text is not displayed in the ScenarioReader
				}; 
			}
		else text[0] = longText;
		
		return text;
	}  // End of createTextLines
	
	public String scenMainPanel( int studID ) {   // Creates/updates the main panel for the ScenarioReader, returns a final result in terms of whether user
												  // wants to begin the scenario, advance to further scenarios, or exit the scenario, etc. It is also the 
												  // screen that user will see most often as he/she learns from the modules/scenarios, answers practice problems, etc. 
		
		int enrollID = Practicer.getEnrollID(studID);	// To get the current enrollment ID number from the given student ID number from the DB 
		
		int currScenario = getCurrScenNum( enrollID );  // To get the current scenario the student needs to begin at for this enrollment from the DB
			
		String fullName = MainMenuGUI.getFullName( studID );  // To get the full name of the student from their student ID number from the DB
			
		int overGrade = 87, skillGrade = 93, psGrade = 77;   // These will be calculated by a method in the Grading component in the future
		
		// Retrieval of the scenario text lines
		String[] textline = new String[20];
		for (int i = 0; i < 20; i++) { textline[i] = "Line " + Integer.toString(i); }; // Default values
	 	textline = createTextLines( currScenario, true );    
		
		String skill1 = "none", skill2 = "none"; 
		String textField1 = "none", textField2 = "none"; 
		String imageFile = "Challenges.png"; 
		question = "Are you ready to begin Scenario #" + Integer.toString(currScenario) + "?";		//  question will be replaced with scenario questions later
					
		//  Creation and details of the JPanel labeled panel 
	 	this.setSize(1100,850); this.setFont( new Font("System", Font.BOLD, 22)); this.setLayout( new BorderLayout() );
		JLabel lab1 = new JLabel( " C H A L L E N G E S   I N   F O U N D A T I O N A L   M A T H E M A T I C S" ); 
			
		//  Creation of the NORTH border
		JLabel lab1a = new JLabel(); JLabel lab1b = new JLabel(); JLabel lab2 = new JLabel();
		pan1.setBounds(0,0,400,50); pan1.setBackground(Color.cyan); lab1a.setText( "Skill(s): " + skill1 ); 
		
		lab1a.setFont( new Font("System",Font.BOLD,15)); lab1a.setHorizontalTextPosition(JLabel.LEFT); lab1a.setVerticalTextPosition(JLabel.CENTER);	 
		lab1a.setVerticalAlignment(JLabel.CENTER); lab1a.setHorizontalAlignment(JLabel.LEFT); lab1a.setBounds(5,5,390,25); 
		pan1.add(lab1); pan1.add(lab1a); 
		if (!(skill2.compareTo("none")==0)) { lab1b.setText(", " + skill2); lab1b.setFont( new Font("System",Font.BOLD,15)); 
			lab1b.setHorizontalTextPosition(JLabel.LEFT); lab1b.setVerticalTextPosition(JLabel.CENTER); lab1b.setVerticalAlignment(JLabel.CENTER); 
			lab1b.setHorizontalAlignment(JLabel.LEFT); lab1b.setBounds(5,25,390,25); pan1.add(lab1b);  }
		pan1.setPreferredSize(new Dimension(400,50)); 
		add(pan1, BorderLayout.NORTH);
		
		pan2.setBackground(Color.cyan);  pan2.setBounds(400,0,340,50); lab2.setFont( new Font("System",Font.BOLD,24)); 
		lab2.setText("    Module #" + ( currScenario/10 ) + "        Scenario #" + currScenario%10 ); 
		lab2.setHorizontalTextPosition(JLabel.LEFT); lab2.setVerticalTextPosition(JLabel.CENTER); 
		lab2.setVerticalAlignment(JLabel.CENTER); lab2.setHorizontalAlignment(JLabel.CENTER); lab2.setBounds(5,5,340,50); pan2.add(lab2); 
		pan2.setPreferredSize(new Dimension(340,50)); 
		add(pan2, BorderLayout.NORTH); 	
		
		//  Creation of the EAST border
		pan3.setBackground(Color.white); 
		JLabel lab3 = new JLabel(); ImageIcon image = new ImageIcon(imageFile); lab3.setPreferredSize(new Dimension(340,500)); 
		lab3.setIcon(image); lab3.setHorizontalAlignment(JLabel.CENTER); 
		lab3.setVerticalAlignment(JLabel.TOP); lab3.setHorizontalTextPosition(JLabel.CENTER); lab3.setVerticalTextPosition(JLabel.BOTTOM); 
		lab3.setFont( new Font("System",Font.BOLD,18)); lab3.setText( question ); 
		pan3.setPreferredSize(new Dimension(360,730)); pan3.setBounds(700,0,360,750); pan3.add(lab3, BorderLayout.EAST);  

		JTextField tf1 = new JTextField(textField1); if (!(textField1.compareTo("none") == 0)) pan3.add(tf1);
		JTextField tf2 = new JTextField(textField2); if (!(textField2.compareTo("none") == 0)) pan3.add(tf2);
		
	    String button1 = "Press to Begin"; String button2 = "Press to Exit";
		JButton jb1 = new JButton(button1); if (!(button1.compareTo("none") == 0)) pan3.add(jb1); 
		JButton jb2 = new JButton(button2); if (!(button2.compareTo("none") == 0)) pan3.add(jb2);
		
		jb1.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { response = "button1"; } }); 
		jb2.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent f) { response = "button2"; } });
		
		add(pan3, BorderLayout.EAST);
		
		//  Creation of the CENTER section
		int len = textline.length; pan4.setLayout(new GridLayout( len, 1)); GridBagConstraints c = new GridBagConstraints(); 
		pan4.setBackground(Color.yellow); pan4.setBounds(0,40,700,750); pan4.setPreferredSize(new Dimension(700,750));
		
		for ( int i = 0; i < len; i++ ) {  // Lines of text displayed using the array textline[]; for some reason the last partial line of text is not displayed
										   // This section was altered on 5/2/23 and it was discovered that the last [partial] line is missing.   	
			c.fill = GridBagConstraints.HORIZONTAL; c.fill = GridBagConstraints.VERTICAL; 
			JLabel lab4 = new JLabel(); c.gridx = 1; c.gridy = i; c.weightx = 0.5; c.weighty = 0.5; 
			lab4.setFont( new Font("System",Font.BOLD,20)); lab4.setText( textline[i] ); pan4.add(lab4, c);		
		}		
		
		add(pan4, BorderLayout.CENTER);	
		
		//  Creation of the SOUTH border
		JLabel lab6 = new JLabel(); JLabel lab7 = new JLabel(); 
		lab6.setText("Informatics and Mathematics Department, College of Professional Advancement, Mercer University");
		lab6.setFont( new Font("System",Font.BOLD,16)); lab6.setBounds(5,5,1130,40); lab6.setHorizontalTextPosition(JLabel.LEFT); 
		lab6.setVerticalTextPosition(JLabel.TOP); lab6.setVerticalAlignment(JLabel.TOP); lab6.setHorizontalAlignment(JLabel.LEFT); 

		lab7.setText("  " + fullName + "  (" + studID + ")    Overall Grade: "+ overGrade + "    Skills Grade: " + skillGrade + "    Problem-Solving Grade: " + psGrade); 
		lab7.setFont( new Font("System",Font.BOLD,16)); lab7.setBounds(5,30,1130,40); lab7.setHorizontalTextPosition(JLabel.LEFT); 
		lab7.setVerticalTextPosition(JLabel.TOP); lab7.setVerticalAlignment(JLabel.TOP); lab7.setHorizontalAlignment(JLabel.LEFT);  

		pan5.setBackground(Color.cyan); pan5.setPreferredSize(new Dimension(60,60)); 
		pan5.add(lab6); pan5.add(lab7);
		add(pan5,BorderLayout.SOUTH);
		setPreferredSize(new Dimension(900,700));
		setVisible(true);
		
		//  if ( response.compareTo( "button1" ) == 0 ) System.out.println( "Button 1 was pressed.");    These cause ScenarioReader to not even add to the tabbed frame 
		//  if ( response.compareTo( "button2" ) == 0 ) System.out.println( "Button 2 was pressed.");    for some unknown reason. Just added on 5/2/23. 
		
		return result;	
	}
	
	public void displayModuleTitle ( int modNum, JPanel mainPanel ) {
		
		JPanel modTitlePanel = new JPanel();
		setBackground(Color.MAGENTA); 
		modTitlePanel.setPreferredSize(new Dimension(600,600));
		JLabel title = new JLabel( "M O D U L E   N U M B E R  " + modNum ); 
		title.setFont( new Font("System",Font.BOLD,30));
		title.setVerticalAlignment(JLabel.CENTER); 
		title.setHorizontalAlignment(JLabel.CENTER); 
		modTitlePanel.add(title);
		
		mainPanel.add(modTitlePanel);
		
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ScenarioReader( JPanel mainPanel, int studID ) {	//  This will eventually be the controlling method for ScenarioReader, that advances the user to new scenarios
															//  displays Module titles at the beginning of new modules, recommends the user takes a quiz when ready, etc. 
		int enrollID = Practicer.getEnrollID(studID);
		
		int currScenario = getCurrScenNum( enrollID );
	    
		if ( currScenario%10 == 0 ) {
			displayModuleTitle( currScenario/10, mainPanel ); }
		
		else scenMainPanel( studID );
		    
		
	}  // End of ScenarioReader

	public static void main( String[] args ) {  // For testing purposes only

	//  Creation of a JFrame labeled fr 
	JFrame fr = new JFrame(); 
	ImageIcon cornerIcon = new ImageIcon("CornerIcon.png"); fr.setIconImage(cornerIcon.getImage()); fr.setLayout(new BorderLayout()); 
	fr.setResizable(true);	fr.getContentPane().setBackground(Color.cyan); fr.setSize( 1100, 800);

	JPanel mainPanel = new JPanel();
	
	ScenarioReader sr = new ScenarioReader( mainPanel, 10180394 );
		
	fr.add(mainPanel);
	fr.setVisible(true);

	}
	
}
