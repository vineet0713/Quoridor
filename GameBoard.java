// Vineet Joshi
// Professor Radhika Grover
// GameBoard.java
// This JPanel is the game board of the board game 'Quoridor'.

// Start Date: November 11, 2017
// End Date: November 26, 2017
// End Date (with comments): November 27, 2017
// Due Date: December 4, 2017

import javax.swing.JPanel;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

import java.awt.Point;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;

public class GameBoard extends JPanel implements KeyListener {
	private static final int ROWS_COLUMNS = 9;
	private static final int SPACE_HEIGHT_WIDTH = 55;
	private static final int SPACE_MARGIN = 20;
	private static final int START_X = 312;
	private static final int START_Y = 20;
	private static final int WALL_INCREMENT = 75;
	private static final int WALL_MARGIN = 15;
	
	private static final String MOVE_INSTR = "Use the arrow keys to move your game piece up, down, left, or right into a valid space.";
	private static final String WALL_INSTR = "Drag and drop a horizontal or vertical wall on a valid groove between two spaces.";
	private static final String SAVE_MSG = "Are you sure you want to save this game and go back to the main menu?";
	private static final String INVALID_MOVE_MSG = "You have made an invalid move on the gameboard.\nMake sure you move into a valid space.";
	private static final String INVALID_WALL_MSG = "You have placed an invalid wall on the gameboard.\nMake sure you make a valid wall placement.";
	
	private static final String SERIALIZED_OBJECTS_FILE = "saved_objects.dat";
	private static final String SERIALIZED_PRIMITIVES_FILE = "saved_primitives.dat";
	
	private final int[] TURN_XPOSITIONS = {15, 982};
	private final int[] WINNING_ROW = {ROWS_COLUMNS - 1, 0};
	private final Color[] PIECE_COLORS = {Color.BLUE, Color.RED};
	
	private JButton quit, instructions;
	
	private JLabel horiBlock, vertBlock;
	private int horiDraggedAtX, horiDraggedAtY, vertDraggedAtX, vertDraggedAtY;
	
	private BufferedImage horiBlockImage, vertBlockImage;
	
	private ImageIcon victoryIcon;
	
	private Point[] playerCoordinates;
	// Point translation:
	// getX() returns the player's row index
	// getY() returns the player's column index
	
	private JTextField p1NameLabel, p2NameLabel;
	private String[] playerNames;
	
	// stores the wall placements on the gameboard (to the right and bottom of each space)
	private Space[][] wallPlacements;
	
	private JLabel displayTurn;
	private JRadioButton moveGamePiece, placeWall;
	private ButtonGroup turnGroup;
	private JTextArea moveInstructions, wallInstructions;
	
	private int playerToPlay;
	// 0 when it is player 1's turn
	// 1 when it is player 2's turn
	
	private int turnChoice;
	// 0 when player has not chosen a turn yet
	// 1 when player has chosen to move
	// 2 when player has chosen to place a wall
	
	// gets the focus to the current window (so KeyListener will work)
	public void getFocus() {
		setFocusable(true);
		requestFocusInWindow();
	}	// end of public void getFocus
	
	// returns a generic JTextField for entering a player's name
	private JTextField returnNameLabel(int player, int xPos) {
		JTextField tf = new JTextField();
		tf.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 20));
		tf.setForeground(PIECE_COLORS[player]);
		tf.setHorizontalAlignment(JTextField.CENTER);
		tf.setBounds(xPos, 629, 282, 50);
		return tf;
	}	// end of private JTextField returnNameLabel
	
	// returns a generic JTextArea for displaying turn instructions
	private JTextArea returnInformationTextArea(String content) {
		JTextArea ta = new JTextArea(content);
		ta.setFont(new Font("Chalkduster", Font.PLAIN, 15));
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		return ta;
	}	// end of private JTextArea returnInformationTextArea
	
	public GameBoard(BufferedImage horiBlockImage, BufferedImage vertBlockImage, BufferedImage victoryImage) {
		setLayout(null);
		setBackground(Color.WHITE);
		addKeyListener(this);
		
		this.horiBlockImage = horiBlockImage;
		this.vertBlockImage = vertBlockImage;
		
		horiBlock = new JLabel(new ImageIcon(horiBlockImage), JLabel.CENTER);
		// adds MouseListener for pressing and releasing the horizontal block JLabel
		horiBlock.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				horiDraggedAtX = e.getXOnScreen();
				horiDraggedAtY = e.getYOnScreen();
			}
			public void mouseReleased(MouseEvent e) {
				int x = TURN_XPOSITIONS[playerToPlay] + 44 + (e.getXOnScreen() - horiDraggedAtX);
				int y = 574 + (e.getYOnScreen() - horiDraggedAtY);
				if (isValidHoriWallPlacement(x, y) == false) {
					horiBlock.setLocation(TURN_XPOSITIONS[playerToPlay] + 44, 574);
					JOptionPane.showMessageDialog(Quoridor.gb, INVALID_WALL_MSG, "Invalid Wall Placement", JOptionPane.ERROR_MESSAGE);
				} else {
					switchTurns();
				}
			}
		});
		// adds MouseListener for dragging the horizontal block JLabel
		horiBlock.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				int deltaX = e.getXOnScreen() - horiDraggedAtX;
				int deltaY = e.getYOnScreen() - horiDraggedAtY;
				horiBlock.setLocation(TURN_XPOSITIONS[playerToPlay] + 44 + deltaX, 574 + deltaY);
			}
		});
		add(horiBlock);
		
		vertBlock = new JLabel(new ImageIcon(vertBlockImage), JLabel.CENTER);
		// adds MouseListener for pressing and releasing the vertical block JLabel
		vertBlock.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				vertDraggedAtX = e.getXOnScreen();
				vertDraggedAtY = e.getYOnScreen();
			}
			public void mouseReleased(MouseEvent e) {
				int x = TURN_XPOSITIONS[playerToPlay] + 44 + 130 + 44 + (e.getXOnScreen() - vertDraggedAtX);
				int y = 574 - (130 - 20) + (e.getYOnScreen() - vertDraggedAtY);
				if (isValidVertWallPlacement(x, y) == false) {
					vertBlock.setLocation(TURN_XPOSITIONS[playerToPlay] + 44 + 130 + 44, 574 - (130 - 20));
					JOptionPane.showMessageDialog(Quoridor.gb, INVALID_WALL_MSG, "Invalid Wall Placement", JOptionPane.ERROR_MESSAGE);
				} else {
					switchTurns();
				}
			}
		});
		// adds MouseListener for dragging the vertical block JLabel
		vertBlock.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				int deltaX = e.getXOnScreen() - vertDraggedAtX;
				int deltaY = e.getYOnScreen() - vertDraggedAtY;
				vertBlock.setLocation(TURN_XPOSITIONS[playerToPlay] + 44 + 130 + 44 + deltaX, 574 - (130 - 20) + deltaY);
			}
		});
		add(vertBlock);
		
		victoryIcon = new ImageIcon(victoryImage);
		
		quit = new JButton("SAVE GAME");
		quit.setFont(new Font("Herculanum", Font.PLAIN, 20));
		quit.setBounds(56, 20, 200, 50);
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int reply = JOptionPane.showConfirmDialog(Quoridor.gb, SAVE_MSG, "Save Game", JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					saveGame();
					(Quoridor.sm).resetTimer();
					(Quoridor.cards).show(Quoridor.topPanel, "Start Menu");
				}
			}
		});
		add(quit);
		
		instructions = new JButton("INSTRUCTIONS");
		instructions.setFont(new Font("Herculanum", Font.PLAIN, 20));
		instructions.setBounds(1023, 20, 200, 50);
		instructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				(Quoridor.ip).setSegue("Game Board");
				(Quoridor.cards).show(Quoridor.topPanel, "Instructions Page");
			}
		});
		add(instructions);
		
		playerNames = new String[2];
		
		p1NameLabel = returnNameLabel(0, 15);
		p1NameLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playerNames[0] = p1NameLabel.getText();
				displayTurn.setText(playerNames[playerToPlay] + "'s turn!");
				getFocus();
			}
		});
		add(p1NameLabel);
		
		p2NameLabel = returnNameLabel(1, 982);
		p2NameLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playerNames[1] = p2NameLabel.getText();
				displayTurn.setText(playerNames[playerToPlay] + "'s turn!");
				getFocus();
			}
		});
		add(p2NameLabel);
		
		playerCoordinates = new Point[2];
		playerCoordinates[0] = new Point();
		playerCoordinates[1] = new Point();
		
		displayTurn = new JLabel();
		displayTurn.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 20));
		displayTurn.setHorizontalAlignment(JLabel.CENTER);
		add(displayTurn);
		
		moveGamePiece = new JRadioButton("Move Game Piece");
		moveGamePiece.setFont(new Font("Curlz MT", Font.PLAIN, 35));
		add(moveGamePiece);
		moveInstructions = returnInformationTextArea(MOVE_INSTR);
		add(moveInstructions);
		
		placeWall = new JRadioButton("Place A Wall");
		placeWall.setFont(new Font("Curlz MT", Font.PLAIN, 35));
		add(placeWall);
		wallInstructions = returnInformationTextArea(WALL_INSTR);
		add(wallInstructions);
		
		turnGroup = new ButtonGroup();
		turnGroup.add(moveGamePiece);
		turnGroup.add(placeWall);
		
		moveGamePiece.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				turnChoice = 1;
				setInstructionsVisible(true);
				getFocus();
			}
		});
		
		placeWall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				turnChoice = 2;
				setInstructionsVisible(false);
				getFocus();
			}
		});
		
		File objectsFile = new File(SERIALIZED_OBJECTS_FILE);
		File primitivesFile = new File(SERIALIZED_PRIMITIVES_FILE);
		if (objectsFile.exists() && primitivesFile.exists()) {
			loadGame(objectsFile, primitivesFile);
		} else {
			resetGameBoard();
		}
		
		p1NameLabel.setText(playerNames[0]);
		p2NameLabel.setText(playerNames[1]);
		
		updateForNewTurn();
	}	// end of constructor
	
	// loads the game from the serialized files:
	private void loadGame(File objectsFile, File primitivesFile) {
		try {
			FileInputStream fileInObjects = new FileInputStream(objectsFile);
			ObjectInputStream objectIn = new ObjectInputStream(fileInObjects);
			playerCoordinates = (Point[])(objectIn.readObject());
			playerNames = (String[])(objectIn.readObject());
			wallPlacements = (Space[][])(objectIn.readObject());
			objectIn.close();
			
			FileInputStream fileInPrimitives = new FileInputStream(primitivesFile);
			DataInputStream dataIn = new DataInputStream(fileInPrimitives);
			playerToPlay = dataIn.readInt();
			dataIn.close();
		} catch (FileNotFoundException e) {
			System.out.println("'FileNotFoundException' while loading game.");
		} catch (IOException e) {
			System.out.println("'IOException' while loading game.");
		} catch (ClassNotFoundException e) {
			System.out.println("'ClassNotFoundException' while loading game.");
		}
		
		// deletes the serialized files:
		objectsFile.delete();
		primitivesFile.delete();
	}	// end of private void loadGame
	
	// saves the current state of the game to serialized files:
	private void saveGame() {
		try {
			FileOutputStream fileOutObjects = new FileOutputStream(new File(SERIALIZED_OBJECTS_FILE));
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOutObjects);
			objectOut.writeObject(playerCoordinates);
			objectOut.writeObject(playerNames);
			objectOut.writeObject(wallPlacements);
			objectOut.close();
			
			FileOutputStream fileOutPrimitives = new FileOutputStream(new File(SERIALIZED_PRIMITIVES_FILE));
			DataOutputStream dataOut = new DataOutputStream(fileOutPrimitives);
			dataOut.writeInt(playerToPlay);
			dataOut.close();
		} catch (FileNotFoundException e) {
			System.out.println("'FileNotFoundException' while saving game.");
		} catch (IOException e) {
			System.out.println("'IOException' while saving game.");
		}
	}	// end of private void saveGame
	
	// resets the gameboard to start a new game:
	private void resetGameBoard() {
		playerCoordinates[0].setLocation(0, ROWS_COLUMNS / 2);
		playerCoordinates[1].setLocation(ROWS_COLUMNS - 1, ROWS_COLUMNS / 2);
		
		playerNames[0] = "[Player 1]";
		playerNames[1] = "[Player 2]";
		
		wallPlacements = new Space[ROWS_COLUMNS][ROWS_COLUMNS];
		for (int row = 0; row < wallPlacements.length; row++) {
			for (int column = 0; column < wallPlacements[row].length; column++) {
				wallPlacements[row][column] = new Space();
			}
		}
		
		playerToPlay = 0;
	}	// end of private void resetGameBoard
	
	// updates all necessary components to start a new turn:
	private void updateForNewTurn() {
		turnChoice = 0;
		
		moveInstructions.setVisible(false);
		wallInstructions.setVisible(false);
		horiBlock.setVisible(false);
		vertBlock.setVisible(false);
		
		displayTurn.setBounds(TURN_XPOSITIONS[playerToPlay], 110, 282, 50);
		displayTurn.setText(playerNames[playerToPlay] + "'s turn!");
		
		moveGamePiece.setBounds(TURN_XPOSITIONS[playerToPlay], 200, 282, 40);
		moveInstructions.setBounds(TURN_XPOSITIONS[playerToPlay], 250, 282, 70);
		placeWall.setBounds(TURN_XPOSITIONS[playerToPlay] + 20, 340, 282, 40);
		wallInstructions.setBounds(TURN_XPOSITIONS[playerToPlay], 390, 282, 70);
		
		horiBlock.setBounds(TURN_XPOSITIONS[playerToPlay] + 44, 574, 131, 19);
		vertBlock.setBounds(TURN_XPOSITIONS[playerToPlay] + 44 + 130 + 44, 574 - (130 - 20), 19, 131);
		
		displayTurn.setForeground(PIECE_COLORS[playerToPlay]);
		moveGamePiece.setForeground(PIECE_COLORS[playerToPlay]);
		moveInstructions.setForeground(PIECE_COLORS[playerToPlay]);
		placeWall.setForeground(PIECE_COLORS[playerToPlay]);
		wallInstructions.setForeground(PIECE_COLORS[playerToPlay]);
		
		turnGroup.clearSelection();
	}	// end of private void updateForNewTurn
	
	// sets components visible based on the boolean 'move' parameter:
	private void setInstructionsVisible(boolean move) {
		moveInstructions.setVisible(move);
		wallInstructions.setVisible(!move);
		horiBlock.setVisible(!move);
		vertBlock.setVisible(!move);
	}	// end of private void setInstructionsVisible
	
	// checks if the horizontal wall placement is valid:
	private boolean isValidHoriWallPlacement(int xCoordinate, int yCoordinate) {
		int row = getSpaceIndex(yCoordinate - 76);
		int col = getSpaceIndex(xCoordinate - 312);
		
		if (row != -1 && col != -1) {
			// makes sure the wall placement does not completely encompass any player
			boolean prevValue = wallPlacements[row][col].getDown();
			wallPlacements[row][col].setDown(true);
			if (canMakeMove(0) && canMakeMove(1)) {
				wallPlacements[row][col].setDown(true);
				return true;
			} else {
				wallPlacements[row][col].setDown(prevValue);
				return false;
			}
		} else {
			return false;
		}
	}	// end of private boolean isValidHoriWallPlacement
	
	// checks if the vertical wall placement is valid:
	private boolean isValidVertWallPlacement(int xCoordinate, int yCoordinate) {
		int row = getSpaceIndex(yCoordinate - 20);
		int col = getSpaceIndex(xCoordinate - 368);
		
		if (row != -1 && col != -1) {
			// makes sure the wall placement does not completely encompass any player
			boolean prevValue = wallPlacements[row][col].getRight();
			wallPlacements[row][col].setRight(true);
			if (canMakeMove(0) && canMakeMove(1)) {
				wallPlacements[row][col].setRight(true);
				return true;
			} else {
				wallPlacements[row][col].setRight(prevValue);
				return false;
			}
		} else {
			return false;
		}
	}	// end of private boolean isValidVertWallPlacement
	
	// returns the index of the wall coordinate (relative to the gameboard):
	private int getSpaceIndex(int coordinate) {
		for (int margin = 0; margin < (WALL_INCREMENT * wallPlacements.length); margin += WALL_INCREMENT) {
			if ((coordinate >= (margin - WALL_MARGIN)) && (coordinate <= (margin + WALL_MARGIN))) {
				return (margin / WALL_INCREMENT);
			}
		}
		return -1;
	}	// end of private int getSpaceIndex
	
	// checks if the player can make a move based on current wall placements:
	private boolean canMakeMove(int player) {
		int r = (int)playerCoordinates[player].getX(), c = (int)playerCoordinates[player].getY();
		return (canMoveUp(r, c) || canMoveDown(r, c) || canMoveLeft(r, c) || canMoveRight(r, c));
	}	// end of private boolean canMakeMove
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// draws the gameboard spaces:
		int xVal, yVal;
		for (int r = 0; r < ROWS_COLUMNS; r++) {
			for (int c = 0; c < ROWS_COLUMNS; c++) {
				xVal = START_X + ((SPACE_HEIGHT_WIDTH + SPACE_MARGIN) * c);
				yVal = START_Y + ((SPACE_HEIGHT_WIDTH + SPACE_MARGIN) * r);
				g.drawRect(xVal, yVal, SPACE_HEIGHT_WIDTH, SPACE_HEIGHT_WIDTH);
			}
		}
		
		drawGamePieces(g, 0);
		drawGamePieces(g, 1);
		
		drawHoriWalls(g);
		drawVertWalls(g);
	}	// end of public void paintComponent
	
	// draws the player's game pieces:
	private void drawGamePieces(Graphics g, int player) {
		g.setColor(PIECE_COLORS[player]);
		int xVal = START_X + ((SPACE_HEIGHT_WIDTH + SPACE_MARGIN) * (int)playerCoordinates[player].getY());
		int yVal = START_Y + ((SPACE_HEIGHT_WIDTH + SPACE_MARGIN) * (int)playerCoordinates[player].getX());
		g.fillOval(xVal, yVal, SPACE_HEIGHT_WIDTH, SPACE_HEIGHT_WIDTH);
	}	// end of private void drawGamePieces
	
	// draws the horizontal wall images:
	private void drawHoriWalls(Graphics g) {
		int xVal, yVal;
		for (int r = 0; r < wallPlacements.length; r++) {
			for (int c = 0; c < wallPlacements[r].length; c++) {
				if (wallPlacements[r][c].getDown() == true) {
					xVal = START_X + ((SPACE_HEIGHT_WIDTH + SPACE_MARGIN) * c);
					yVal = START_Y + (((SPACE_HEIGHT_WIDTH + SPACE_MARGIN) * (r + 1)) - SPACE_MARGIN) + 1;
					g.drawImage(horiBlockImage, xVal, yVal, this);
				}
			}
		}
	}	// end of private void drawHoriWalls
	
	// draws the vertical wall images:
	private void drawVertWalls(Graphics g) {
		int xVal, yVal;
		for (int c = 0; c < wallPlacements.length; c++) {
			for (int r = 0; r < wallPlacements[c].length; r++) {
				if (wallPlacements[r][c].getRight() == true) {
					xVal = START_X + (((SPACE_HEIGHT_WIDTH + SPACE_MARGIN) * (c + 1)) - SPACE_MARGIN) + 1;
					yVal = START_Y + ((SPACE_HEIGHT_WIDTH + SPACE_MARGIN) * r);
					g.drawImage(vertBlockImage, xVal, yVal, this);
				}
			}
		}
	}	// end of private void drawVertWalls
	
	// switches the player turns
	private void switchTurns() {
		repaint();
		if (checkForWinner() == false) {
			playerToPlay = Math.abs(playerToPlay - 1);
			updateForNewTurn();
		}
	}	// end of private void switchTurns
	
	// checks if a player has won or not:
	private boolean checkForWinner() {
		if (playerCoordinates[playerToPlay].getX() == WINNING_ROW[playerToPlay]) {
			String victoryMsg = playerNames[playerToPlay] + " has won!\nWould you like to play again?";
			int reply = JOptionPane.showConfirmDialog(Quoridor.gb, victoryMsg, "Game Over!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, victoryIcon);
			if (reply == JOptionPane.NO_OPTION) {
				(Quoridor.sm).resetTimer();
				(Quoridor.cards).show(Quoridor.topPanel, "Start Menu");
			}
			resetGameBoard();
			repaint();
			return true;
		}
		return false;
	}	// end of private boolean checkForWinner
	
	// checks if the player can move up or not:
	private boolean canMoveUp(int r, int c) {
		if (r > 0 && wallPlacements[r - 1][c].getDown() == false) {
			if (c - 1 >= 0) {
				return (wallPlacements[r - 1][c - 1].getDown() == false);
			}
			return true;
		}
		return false;
	}	// end of private boolean canMoveUp
	
	// checks if the player can move down or not:
	private boolean canMoveDown(int r, int c) {
		if (r < ROWS_COLUMNS - 1 && wallPlacements[r][c].getDown() == false) {
			if (c - 1 >= 0) {
				return (wallPlacements[r][c - 1].getDown() == false);
			}
			return true;
		}
		return false;
	}	// end of private boolean canMoveDown
	
	// checks if the player can move left or not:
	private boolean canMoveLeft(int r, int c) {
		if (c > 0 && wallPlacements[r][c - 1].getRight() == false) {
			if (r - 1 >= 0) {
				return (wallPlacements[r - 1][c - 1].getRight() == false);
			}
			return true;
		}
		return false;
	}	// end of private boolean canMoveLeft
	
	// checks if the player can move right or not:
	private boolean canMoveRight(int r, int c) {
		if (c < ROWS_COLUMNS - 1 && wallPlacements[r][c].getRight() == false) {
			if (r - 1 >= 0) {
				return (wallPlacements[r - 1][c].getRight() == false);
			}
			return true;
		}
		return false;
	}	// end of private boolean canMoveRight
	
	// checks if the player made a valid move or not:
	public void keyPressed(KeyEvent e) {
		if (turnChoice == 1) {
			int r = (int)playerCoordinates[playerToPlay].getX(), c = (int)playerCoordinates[playerToPlay].getY();
			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					if (canMoveUp(r, c)) {
						playerCoordinates[playerToPlay].setLocation(r - 1, c);
						switchTurns();
					} else {
						JOptionPane.showMessageDialog(Quoridor.gb, INVALID_MOVE_MSG, "Invalid Move", JOptionPane.ERROR_MESSAGE);
					}
					break;
				case KeyEvent.VK_DOWN:
					if (canMoveDown(r, c)) {
						playerCoordinates[playerToPlay].setLocation(r + 1, c);
						switchTurns();
					} else {
						JOptionPane.showMessageDialog(Quoridor.gb, INVALID_MOVE_MSG, "Invalid Move", JOptionPane.ERROR_MESSAGE);
					}
					break;
				case KeyEvent.VK_LEFT:
					if (canMoveLeft(r, c)) {
						playerCoordinates[playerToPlay].setLocation(r, c - 1);
						switchTurns();
					} else {
						JOptionPane.showMessageDialog(Quoridor.gb, INVALID_MOVE_MSG, "Invalid Move", JOptionPane.ERROR_MESSAGE);
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (canMoveRight(r, c)) {
						playerCoordinates[playerToPlay].setLocation(r, c + 1);
						switchTurns();
					} else {
						JOptionPane.showMessageDialog(Quoridor.gb, INVALID_MOVE_MSG, "Invalid Move", JOptionPane.ERROR_MESSAGE);
					}
					break;
			}
		}
	}	// end of public void keyPressed
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
}	// end of class GameBoard