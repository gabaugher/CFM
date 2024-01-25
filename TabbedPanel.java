//  TabbedPanel Class
//  Use of a tabbed panel was decided to give students or other users ease in 
//  navigation between program components. Eventually the MainMenuGUI will be
//  no longer used since all components will be called from this tabbed panel.

package cfm_files;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


@SuppressWarnings("serial")
public class TabbedPanel extends JPanel {

	boolean loginSuccessful = false;
	
	boolean loginTabCreated = false; 

	JTabbedPane tabbedPane;

	public TabbedPanel() {

		super();
		
		tabbedPane = new JTabbedPane();

		Dimension dim = new Dimension(1200, 900);

		tabbedPane.setPreferredSize(dim );
		
		BufferedImage image = null;
		try { image = ImageIO.read(new File("CFM Opening Image.png")); }
		catch (IOException e) { e.printStackTrace(); }
		JLabel picLabel = new JLabel(new ImageIcon(image));

		JPanel panel1 = new JPanel();
		panel1.add(picLabel); 
		
		tabbedPane.addTab("CFM (click to login)", null, panel1,	"Default");

		tabbedPane.setMnemonicAt(0, KeyEvent.VK_H);

		panel1.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) { 
				if (loginTabCreated == false) addLoginTab();
				}

			@Override
			public void mouseEntered(MouseEvent arg0) {  }

			@Override
			public void mouseExited(MouseEvent arg0) {  }

			@Override
			public void mousePressed(MouseEvent arg0) {   }

			@Override
			public void mouseReleased(MouseEvent arg0) {   }

		});				
		
		add(tabbedPane);
		
		//The following line enables the use of scrolling tabs.
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

	}

	protected void addLoginTab() {

		CISLoginDialog loginPanel = new CISLoginDialog(tabbedPane);

		tabbedPane.addTab("Fill in form, then press Login button", null, loginPanel, "");

		int count = tabbedPane.getTabCount();
		
		tabbedPane.setSelectedIndex(count-1);

		repaint();
		
		loginTabCreated = true; 
		
	}

	static void createAndShowGUI() {

		//Create and set up the frame

		JFrame frame = new JFrame("  C H A L L E N G E S   I N   F O U N D A T I O N A L   M A T H E M A T I C S");
		
		frame.setFont( new Font("System", Font.BOLD, 32));
		
		ImageIcon cornerIcon = new ImageIcon("CornerIcon.png"); 
		
		frame.setIconImage(cornerIcon.getImage()); 

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add content to the window.

		frame.add(new TabbedPanel( ) );

		//Display the window.

		frame.pack();

		frame.setVisible(true);

	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				//Turn off metal's use of bold fonts

				UIManager.put("swing.boldMetal", Boolean.FALSE);

				createAndShowGUI();

			}

		});

	}

}
