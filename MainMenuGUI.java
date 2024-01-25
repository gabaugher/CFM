//  MainMenuGUI class, a temporary component of the CFM Project
//  Since the TabbedPanel GUI was determined to be best, this component will eventually 
//  not be needed and the user will navigate to various components via the tabs.
//  Currently, the Practicer component has a text-based console interface but will have 
//  a graphical interface installed in coming months. 
//  By G. Baugher

package cfm_files;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenuGUI extends JPanel {

	public static String getFullName( int studID )  //  Accesses DB to get the full name of the user for a particular student ID 
		{
		String firstName = "", lastName = "", fullName = ""; 
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null; String sql;
		String connectionUrl = "jdbc:sqlserver://;servername=csdata.cd4sevot432y.us-east-1.rds.amazonaws.com;"
				+ "user=csc312cloud;password=c3s!c2Cld;" + "databaseName=CFM;"; 
		
		try {  // Establish the connection.
	    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    	con = DriverManager.getConnection(connectionUrl); 
	    	stmt = con.createStatement();
	    	    	
	    	// Create and execute an SQL statement that returns the value in the specified column for the specified student ID
		    sql = "SELECT StudLastName, StudFirstName FROM Student WHERE StudID = " + String.valueOf( studID ) + ";";
	    	rs = stmt.executeQuery(sql);
	    	ResultSetMetaData meta = rs.getMetaData();
	    	int columns = meta.getColumnCount();
	    	if (columns == 2) { 
	    		if (rs.next()) lastName = rs.getString(1); 
	    		firstName = rs.getString(2); } 
				}
		// Handle any errors that may have occurred.
		catch (Exception e) { e.printStackTrace(); }
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
	    		if (stmt != null) try { stmt.close(); } catch(Exception e) {}
	    		if (con != null) try { con.close(); } catch(Exception e) {} }	
	
		fullName = firstName.trim() + " " + lastName.trim();
		
	return fullName;
	}  // End of getFullName()
	
	public MainMenuGUI( int studentID ) {  // Uses user's name to make a user-friendly navigation menu for the CFM program
			
		int i = 0, len = 0; 
		Font fnt18 = new Font("System", Font.BOLD, 18); 
		Font fnt22 = new Font("System", Font.BOLD, 22);  
		Font fnt28 = new Font("System", Font.BOLD, 28); 
		
		int enrollID = Practicer.getEnrollID(studentID);
		String fullName = getFullName( studentID );
		String firstName = ""; 
		i = fullName.indexOf(' '); 
		if (i > 0)  firstName = fullName.substring(0,i);
			else firstName = fullName;
	
		String question = ""; 
		
		//Creation of a JFrame labeled fr 
	 	String[] buttonText = {"1. Continue Module or Scenario (where I left off)", "2. Practice Algebra Skills", 
	 			"3. Redo a Module or Scenario.", "4. Take a Quiz (if ready).", "5. View My Grades.", "6. Exit the Program."};
	 	String textField1 = " "; 
	    
		// this.setDefaultCloseOperation(JPanel.EXIT_ON_CLOSE); 
		this.setSize(1100,850); 
		this.setFont( new Font("System", Font.BOLD, 22));
		JLabel title = new JLabel("C H A L L E N G E S   I N  F O U N D A T I O N A L   M A T H E M A T I C S"); 

		// this.setResizable(true);	
		this.setBackground(Color.cyan); 
		this.setLayout(new BorderLayout()); 
		
		//  Creation of the NORTH border
		JLabel lab1a = new JLabel(); 
		JLabel lab1b = new JLabel(); 
		JPanel pan1 = new JPanel(); 
		pan1.setBounds(0,0,150,80); 
		pan1.setBackground(Color.cyan); 
		lab1a.setText( "Name: " + fullName ); 
		lab1a.setFont( fnt18 ); 
		lab1a.setHorizontalTextPosition(JLabel.CENTER);
		lab1a.setVerticalTextPosition(JLabel.CENTER);	 
		lab1a.setVerticalAlignment(JLabel.CENTER); 
		lab1a.setHorizontalAlignment(JLabel.LEFT); 
		lab1a.setBounds(5,15,140,35); 
		lab1b.setText(" StudID: " + studentID + " "); 
		lab1b.setFont( fnt18 ); 
		lab1b.setHorizontalTextPosition(JLabel.LEFT); 
		lab1b.setVerticalTextPosition(JLabel.CENTER); 
		lab1b.setVerticalAlignment(JLabel.CENTER); 
		lab1b.setHorizontalAlignment(JLabel.LEFT); 
		lab1b.setBounds(5,25,140,35); 
		pan1.add(lab1a); pan1.add(lab1b); 
		pan1.setPreferredSize(new Dimension(150,80)); 
		this.add(pan1, BorderLayout.NORTH);
		
		JPanel pan2 = new JPanel();  
		pan2.setBackground(Color.cyan); 
		pan2.setBounds(150,0,500,80); 
		JLabel lab2 = new JLabel(); 
		lab2.setFont( new Font("System",Font.BOLD,28)); 
		lab2.setText("Good morning, " + firstName + ".  What would you like to do?"); 
		lab2.setHorizontalTextPosition(JLabel.CENTER); 
		lab2.setVerticalTextPosition(JLabel.CENTER); 
		lab2.setVerticalAlignment(JLabel.CENTER); 
		lab2.setHorizontalAlignment(JLabel.CENTER); 
		lab2.setBounds(5,5,490,75); pan2.add(lab2); 
		pan2.setPreferredSize(new Dimension(500,80)); 
		this.add(pan2, BorderLayout.NORTH); 	
		
		//  Creation of the EAST border
		JPanel pan3 = new JPanel(); 
		pan3.setBackground(Color.white); 
		JLabel lab3 = new JLabel(); 
		ImageIcon image = new ImageIcon("M1S1Intro.png"); 
		lab3.setIcon(image); 
		lab3.setHorizontalAlignment(JLabel.CENTER); 
		lab3.setVerticalAlignment(JLabel.TOP); 
		lab3.setHorizontalTextPosition(JLabel.CENTER); 
		lab3.setVerticalTextPosition(JLabel.BOTTOM); 
		lab3.setFont( fnt22 ); 
		lab3.setText( question ); 
		pan3.setPreferredSize(new Dimension(360,730)); 
		pan3.setBounds(700, 0, 360, 750); 
		pan3.add(lab3, BorderLayout.EAST);  
		this.add(pan3, BorderLayout.EAST);
		
		//  Creation of the CENTER section
		JPanel pan4 = new JPanel(); 
		pan4.setLayout(new GridLayout( len, 1)); 
		GridBagConstraints c = new GridBagConstraints(); 
		
		pan4.setBackground(Color.yellow); 
		pan4.setBounds(0,40,700,550); 
		pan4.setPreferredSize(new Dimension(700,550));
		
		c.fill = GridBagConstraints.HORIZONTAL; c.fill = GridBagConstraints.VERTICAL; 
		JLabel lab4a = new JLabel(); 
		c.gridx = 0; c.gridy = 0; c.weightx = 0.5; c.weighty = 0.5; 
		lab4a.setFont( fnt28 ); 
		lab4a.setText( "Press the button of your selection:" ); 
		pan4.add(lab4a, c);
		
		JButton jb0 = new JButton(buttonText[0]); 
		c.gridx = 0; c.gridy = 1; c.weightx = 0.5; c.weighty = 0.5; 
		pan4.add(jb0, c); 
		jb0.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) 
			{   } });  // send to ScenarioReader sr = new ScenarioReader( enrollID );??? 
		
		JButton jb1 = new JButton(buttonText[1]); 
		c.gridx = 0; c.gridy = 2; c.weightx = 0.5; c.weighty = 0.5; 
		pan4.add(jb1, c); 
		jb1.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) 
			{ Practicer pr = new Practicer(studentID); } });
		
		JButton jb2 = new JButton(buttonText[2]); 
		c.gridx = 0; c.gridy = 3; c.weightx = 0.5; c.weighty = 0.5; 
		pan4.add(jb2, c); 
		jb2.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {  } }); 
		
		JButton jb3 = new JButton(buttonText[3]); 
		c.gridx = 0; c.gridy = 4; c.weightx = 0.5; c.weighty = 0.5; 
		pan4.add(jb3, c); 
		jb3.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {  } });
	
		JButton jb4 = new JButton(buttonText[4]); 
		c.gridx = 0; c.gridy = 5; c.weightx = 0.5; c.weighty = 0.5; 
		pan4.add(jb4, c); 
		jb4.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {  } }); 
		
		JButton jb5 = new JButton(buttonText[5]); 
		c.gridx = 0; c.gridy = 6; c.weightx = 0.5; c.weighty = 0.5; 
		pan4.add(jb5, c); 
		jb5.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {  } });
		
		this.add(pan4, BorderLayout.CENTER);	
		
		//  Creation of the SOUTH border
		JLabel lab6 = new JLabel(); JLabel lab7 = new JLabel(); lab6.setFont( fnt22);
		lab6.setText("Informatics and Mathematics Department, College of Professional Advancement, Mercer University");
		lab6.setBounds(5,5,1130,40); lab6.setHorizontalTextPosition(JLabel.LEFT); lab6.setVerticalTextPosition(JLabel.TOP); 
		lab6.setVerticalAlignment(JLabel.TOP); lab6.setHorizontalAlignment(JLabel.LEFT); 
	
		JPanel pan6 = new JPanel(); pan6.setBackground(Color.cyan); pan6.setPreferredSize(new Dimension(60,60)); pan6.add(lab6); pan6.add(lab7); 
		this.add(pan6,BorderLayout.SOUTH);
		
		this.setVisible(true);
	
	}  // End of mainMenuGUI

public static void main( String[] args ) {
	
	JFrame frame = new JFrame(); 
	MainMenuGUI mmg = new MainMenuGUI( 10180394 ); 
	frame.add(mmg);
	Dimension dim = new Dimension( 1000, 800); 
	frame.setSize( dim );
	frame.setVisible(true); 
	}

}


