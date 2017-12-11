// Vineet Joshi
// Professor Radhika Grover
// InstructionsPage.java
// This JPanel displays the instructions of the board game 'Quoridor'.

// Start Date: November 11, 2017
// End Date: November 26, 2017
// End Date (with comments): November 27, 2017
// Due Date: December 4, 2017

import javax.swing.JPanel;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InstructionsPage extends JPanel {
	private JLabel title;
	private JButton back;
	private JTextArea instructions;
	
	private String segue;
	
	public InstructionsPage(String instructionsText) {
		setLayout(null);
		setBackground(Color.WHITE);
		
		back = new JButton("← BACK");
		back.setFont(new Font("Herculanum", Font.PLAIN, 20));
		back.setBounds(20, 20, 100, 50);
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				(Quoridor.cards).show(Quoridor.topPanel, segue);
				if (segue.equals("Start Menu")) {
					(Quoridor.sm).resetTimer();
				} else {
					(Quoridor.gb).getFocus();
				}
			}
		});
		add(back);
		
		title = new JLabel("Instructions", JLabel.CENTER);
		title.setFont(new Font("Cracked", Font.PLAIN, 100));
		title.setForeground(new Color(128, 0, 128));
		title.setBounds(140, 20, 1000, 75);
		add(title);
		
		instructions = new JTextArea(instructionsText);
		instructions.setFont(new Font("Chalkduster", Font.PLAIN, 35));
		instructions.setForeground(Color.DARK_GRAY);
		instructions.setLineWrap(true);
		instructions.setWrapStyleWord(true);
		instructions.setBounds(20, 125, 1240, 545);
		add(instructions);
	}	// end of constructor
	
	// sets the segue to the JPanel that called this page:
	public void setSegue(String segue) {
		this.segue = segue;
	}	// end of public void setSegue
}	// end of class InstructionsPage