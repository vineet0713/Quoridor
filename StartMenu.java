// Vineet Joshi
// Professor Radhika Grover
// StartMenu.java
// This JPanel is the start menu of the board game 'Quoridor'.

// Start Date: November 11, 2017
// End Date: November 26, 2017
// End Date (with comments): November 27, 2017
// Due Date: December 4, 2017

import javax.swing.JPanel;

import javax.swing.Timer;

import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.image.BufferedImage;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StartMenu extends JPanel implements ActionListener {
	private static final int LEFT_BOUND = 0, RIGHT_BOUND = 755;
	
	private JLabel title;
	private JButton startGame, instructions;
	
	private Timer timer;
	
	private BufferedImage horiBlock, vertBlock;
	
	private int titleXPos, forward;
	
	public StartMenu(BufferedImage horiBlock, BufferedImage vertBlock) {
		setLayout(null);
		setBackground(Color.BLACK);
		
		this.horiBlock = horiBlock;
		this.vertBlock = vertBlock;
		
		title = new JLabel("Quoridor");
		title.setFont(new Font("Matura MT Script Capitals", Font.PLAIN, 125));
		title.setForeground(new Color(216, 161, 107));
		title.setBounds(titleXPos, 75, 525, 125);
		add(title);
		
		startGame = new JButton("START GAME");
		startGame.setFont(new Font("Herculanum", Font.PLAIN, 40));
		startGame.setForeground(new Color(86, 47, 14));
		startGame.setBounds(420, 365, 350, 90);
		startGame.addActionListener(this);
		startGame.setActionCommand("Game Board");
		add(startGame);
		
		instructions = new JButton("INSTRUCTIONS");
		instructions.setFont(new Font("Herculanum", Font.PLAIN, 40));
		instructions.setForeground(new Color(86, 47, 14));
		instructions.setBounds(420, 495, 350, 90);
		instructions.addActionListener(this);
		instructions.setActionCommand("Instructions Page");
		add(instructions);
		
		timer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (titleXPos == RIGHT_BOUND) {
					forward = -1;
				} else if (titleXPos == LEFT_BOUND) {
					forward = 1;
				}
				titleXPos += forward;
				title.setLocation(titleXPos, 75);
			}
		});
		resetTimer();
	}	// end of constructor
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// draws the vertical block decorations
		for (int x = 340; x <= 830; x += 490) {
			g.drawImage(vertBlock, x, 335, this);
			g.drawImage(vertBlock, x, 485, this);
		}
		
		// draws the horizontal block decorations
		for (int y = 335; y <= 595; y += 130) {
			g.drawImage(horiBlock, 380, y, this);
			g.drawImage(horiBlock, 530, y, this);
			g.drawImage(horiBlock, 680, y, this);
		}
	}	// end of public void paintComponent
	
	// based on which button, switches the JPanel:
	public void actionPerformed(ActionEvent e) {
		timer.stop();
		String command = e.getActionCommand();
		if (command.equals("Instructions Page")) {
			(Quoridor.ip).setSegue("Start Menu");
		} else {
			(Quoridor.gb).getFocus();
		}
		(Quoridor.cards).show(Quoridor.topPanel, command);
	}	// end of public void actionPerformed
	
	// resets the animation Timer:
	public void resetTimer() {
		titleXPos = 1;
		forward = 1;
		timer.start();
	}	// end of public void resetTimer
}	// end of class StartMenu