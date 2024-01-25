//  CISLoginDialog class - this component serves to take the user's login information 
//  and verify their login credentials to allow or deny access to the program. 

package cfm_files;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class CISLoginDialog extends JPanel {

	public static final String DEFAULT_SERVER_NAME = "csdata.cd4sevot432y.us-east-1.rds.amazonaws.com"; public static final String DEFAULT_DB_NAME = "CFM";

	private JLabel lbServerName; private JTextField tfServerName; private JLabel lbDatabase; private JTextField tfDatabase; private JLabel lbUserID; 

	private JTextField tfUserID; private JLabel lbUsername; private JTextField tfUsername; private JLabel lbPassword; private JPasswordField pfPassword; 

	private JButton btnLogin; private JButton btnCancel; 

	public CISLoginDialog(JTabbedPane pane) {

		super();

		//prepare fields in a panel
		this.setFont( new Font("System", Font.BOLD, 20));
		JPanel panel = new JPanel(new GridBagLayout()); 
		GridBagConstraints cs = new GridBagConstraints(); 
		cs.fill = GridBagConstraints.HORIZONTAL; 
		
		JLabel title = new JLabel( "Challenges in Foundational Mathematics" ); 
		cs.gridx = 0; cs.gridy = 0; cs.gridwidth = 1; 
		panel.add(title, cs);
		
		JLabel blank = new JLabel( " " ); 
		cs.gridx = 0; cs.gridy = 1; cs.gridwidth = 1; 
		panel.add(blank, cs);
		
		lbServerName = new JLabel("Server Name: "); 
		cs.gridx = 0; cs.gridy = 2; cs.gridwidth = 1; 
		panel.add(lbServerName, cs);
		
		tfServerName = new JTextField(30); tfServerName.setText(DEFAULT_SERVER_NAME); 
		cs.gridx = 1; cs.gridy = 2; cs.gridwidth = 2; 
		panel.add(tfServerName, cs); 
		
		lbDatabase = new JLabel("Database: "); 
		cs.gridx = 0; cs.gridy = 3; cs.gridwidth = 1; 
		panel.add(lbDatabase, cs);
		
		tfDatabase = new JTextField(20); tfDatabase.setText(DEFAULT_DB_NAME); 
		cs.gridx = 1; cs.gridy = 3; cs.gridwidth = 2; 
		panel.add(tfDatabase, cs);
		
		lbUserID = new JLabel("Mercer ID: "); 
		cs.gridx = 0; cs.gridy = 4; cs.gridwidth = 1; 
		panel.add(lbUserID, cs);
		
		tfUserID = new JTextField(20);
		cs.gridx = 1; cs.gridy = 4; cs.gridwidth = 2; 
		panel.add(tfUserID, cs); 
		lbUsername = new JLabel("User Name: "); 
		cs.gridx = 0; cs.gridy = 5; cs.gridwidth = 1; panel.add(lbUsername, cs);
		tfUsername = new JTextField(20); cs.gridx = 1; cs.gridy = 5; cs.gridwidth = 2; 
		panel.add(tfUsername, cs);
		
		lbPassword = new JLabel("Password: "); 
		cs.gridx = 0; cs.gridy = 6; cs.gridwidth = 1; 
		panel.add(lbPassword, cs); 
		
		pfPassword = new JPasswordField(20); 
		cs.gridx = 1; cs.gridy = 6; cs.gridwidth = 2; 
		panel.add(pfPassword, cs); 
		
		panel.setBorder(new LineBorder(Color.GRAY));

		//prepare buttons
		btnLogin = new JButton("LOGIN"); 
		
		btnLogin.addActionListener(new ActionListener() { 

			public void actionPerformed(ActionEvent e) {

				ResultSet rs = null;

				int userID = 0;

				String loginName = null, loginPassword = null;
				
				try {

					// Establish the connection.

					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

					String connectionUrl = "jdbc:sqlserver://;servername=csdata.cd4sevot432y.us-east-1.rds.amazonaws.com;"

							+ "user=csc312cloud;password=c3s!c2Cld;" + "databaseName=CFM;";

					Connection con = DriverManager.getConnection(connectionUrl);

					Statement stmt = con.createStatement();

					String sql;

					//Create and execute an SQL statement that returns the value in the specified column for the specified enrollmentID (the old value)

					sql = "SELECT StudID FROM LoginData WHERE StudID = " + String.valueOf(tfUserID.getText()) + ";";

					rs = stmt.executeQuery(sql);

					ResultSetMetaData meta = rs.getMetaData();

					int columns = meta.getColumnCount();

					if ((columns == 1) && (rs.next())) userID = rs.getInt(1);

					sql = "SELECT LoginName from LoginData WHERE StudID = " + String.valueOf(tfUserID.getText()) + ";";

					rs = stmt.executeQuery(sql);

					meta = rs.getMetaData();

					columns = meta.getColumnCount();

					if ((columns == 1) && (rs.next())) loginName = rs.getString(1);

					loginName = loginName.trim(); 

					sql = "SELECT LoginPassword from LoginData WHERE StudID = " + String.valueOf(tfUserID.getText()) + ";";

					rs = stmt.executeQuery(sql);

					meta = rs.getMetaData();

					columns = meta.getColumnCount();

					if ((columns == 1) && (rs.next())) loginPassword = rs.getString(1);

					loginPassword = loginPassword.trim(); 

					rs.close();

				}

				// Handle any errors that may have occurred.

				catch (Exception e1) { e1.printStackTrace(); }

				finally { 
					
					if (rs != null) try { rs.close(); }  catch(Exception e1) {} }

				if ( ( loginName.compareTo( tfUsername.getText().trim() ) == 0 ) && ( loginPassword.compareTo( getPassword().trim() ) == 0 ) ) {

					JOptionPane.showMessageDialog(CISLoginDialog.this, "Hello " + getUsername() + 

							". You have successfully logged in.", "LOGIN", JOptionPane.INFORMATION_MESSAGE );

					userID = Integer.valueOf( getUserID() );  
					
					int count = pane.getTabCount();
					
					pane.remove(count-1);			
					
					pane.remove(0);
					
					JPanel panel3 = new MainMenuGUI( userID ); 
					pane.addTab("Main Menu", null, panel3, "Default");
					
					JPanel panel4 = new JPanel();
					JPanel panel5 = new ScenarioReader( panel4, userID );
					pane.addTab("Scenario Reader", null, panel5, "Default");
					
					pane.setSelectedIndex(0);
				} 

				else { JOptionPane.showMessageDialog(CISLoginDialog.this, "Invalid Mercer ID, Username or Password. Please try again...", "LOGIN", JOptionPane.ERROR_MESSAGE);

				// reset userID, user name, and password

				tfUserID.setText(""); tfUsername.setText(""); pfPassword.setText(""); } 

			}

		});

		btnCancel = new JButton("CANCEL"); btnCancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { } });

		JPanel bp = new JPanel(); bp.add(btnLogin); bp.add(btnCancel); this.setLayout( new BorderLayout() ); 

		this.setLayout(new BorderLayout());

		this.add(panel); 

		this.add(bp, BorderLayout.PAGE_END); 
		

	} // End of CISLoginDialog()

	public String getUserID() { return tfUserID.getText().trim(); }

	public String getUsername() { return tfUsername.getText().trim(); }

	public String getPassword() { return new String(pfPassword.getPassword()); }

	public static void main(String[] args) {  //  For testing purposes only

		JFrame f = new JFrame( "C.F.M." );

		JTabbedPane tabbedPane = new JTabbedPane();

		f.add( new CISLoginDialog(tabbedPane) );

		f.pack();

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		f.setVisible(true);

	}

}
