// Vineet Joshi
// Professor Radhika Grover
// Quoridor.java
// This program is the main entry point into a computerized version of the board game 'Quoridor'.

// Start Date: November 11, 2017
// End Date: November 26, 2017
// End Date (with comments): November 27, 2017
// Due Date: December 4, 2017

import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.awt.CardLayout;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Quoridor {
	private static final String INSTRUCTIONS_FILE = "instructions.txt";
	private static final String HORI_BLOCK_FILE = "wooden_block_horizontal.jpg";
	private static final String VERT_BLOCK_FILE = "wooden_block_vertical.jpg";
	private static final String VICTORY_ICON_FILE = "victory.png";
	
	public static StartMenu sm;
	public static InstructionsPage ip;
	public static GameBoard gb;
	public static JPanel topPanel;
	public static CardLayout cards;
	
	public static void main(String[] args) {
		Quoridor gui = new Quoridor();
		gui.run();
	}
	
	// initializes all JPanel/JFrame instances and runs the game:
	public void run() {
		String instructions = readInstructions();
		BufferedImage horiBlock = loadImage(HORI_BLOCK_FILE);
		BufferedImage vertBlock = loadImage(VERT_BLOCK_FILE);
		BufferedImage victoryIcon = loadImage(VICTORY_ICON_FILE);
		
		sm = new StartMenu(horiBlock, vertBlock);
		ip = new InstructionsPage(instructions);
		gb = new GameBoard(horiBlock, vertBlock, victoryIcon);
		
		topPanel = new JPanel();
		cards = new CardLayout();
		topPanel.setLayout(cards);
		topPanel.add(sm, "Start Menu");
		topPanel.add(ip, "Instructions Page");
		topPanel.add(gb, "Game Board");
		
		JFrame window = new JFrame();
		window.setContentPane(topPanel);
		window.setSize(1280, 720);
		window.setTitle("Quoridor");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}	// end of public void run
	
	// reads the content from the instructions text file:
	private String readInstructions() {
		String instructions = "";
		try {
			File file = new File(INSTRUCTIONS_FILE);
			Scanner fileScanner = new Scanner(file);
			while (fileScanner.hasNextLine()) {
				instructions += (fileScanner.nextLine() + "\n");
			}
			fileScanner.close();
		} catch (FileNotFoundException e) {
			instructions = "The file '" + INSTRUCTIONS_FILE + "' was not found!";
		}
		return instructions;
	}	// end of private String readInstructions
	
	// loads the image specified by the String 'imageFile' parameter:
	private BufferedImage loadImage(String imageFile) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(imageFile));
		} catch (IOException e) {
			System.out.println("An error occurred while loading the image '" + imageFile + "'.");
			System.exit(1);
		}
		return image;
	}	// end of private BufferedImage loadImage
}	// end of class Quoridor