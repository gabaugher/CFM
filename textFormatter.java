package cfm_files;

import java.awt.font.TextAttribute;
import java.text.AttributedString;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


public class textFormatter {

	public AttributedString parseString(String inputStr) {
	
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		String newStr = null;
		
		for (int i = 0; i < inputStr.length(); i++) {
			if (inputStr.charAt(i) == '^') {
				indexList.add(i);
			} else {
				newStr = newStr + inputStr.charAt(i);
			}
		}
		
		System.out.println(newStr);

		AttributedString result = new AttributedString(newStr);
		
		for (int i : indexList) {
			result.addAttribute(TextAttribute.SIZE, 28, i, i);
			result.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, 5, 16);
		}
		
		return result;
	}

	public static void main(String[] args) {
		
	}
		textFormatter tf = new textFormatter();
		
		
		protected void paintComponent(Graphics g) {
            //super.paintComponent(g);
            JFrame frame = new JFrame("AttributedString superscript test");
            JPanel panel = new JPanel();
		
            frame.add(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
		
            Graphics2D g1 = (Graphics2D) g;
            g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		
            panel.setLayout(new BorderLayout());
		
            frame.getContentPane().add(panel);
		
            String alg1 = "a^2 + b^2";
            String alg2 = "10^2 + b^3";
		
            g1.drawString(tf.parseString(alg1).getIterator(), 50, 50);
            g1.drawString(tf.parseString(alg2).getIterator(), 50, 50);
		
	}
}
    
