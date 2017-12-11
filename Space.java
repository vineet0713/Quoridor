// Vineet Joshi
// Professor Radhika Grover
// Space.java
// This data structure represents a space on the game board of the board game 'Quoridor'.

// Start Date: November 24, 2017
// End Date: November 26, 2017
// End Date (with comments): November 27, 2017
// Due Date: December 4, 2017

import java.io.Serializable;

public class Space implements Serializable {
	private boolean right, down;
	
	public Space() {
		right = down = false;
	}	// end of constructor
	
	public boolean getRight() {
		return right;
	}	// end of boolean getRight
	
	public boolean getDown() {
		return down;
	}	// end of boolean getDown
	
	public void setRight(boolean right) {
		this.right = right;
	}	// end of void setRight
	
	public void setDown(boolean down) {
		this.down = down;
	}	// end of void setDown
}	// end of class Space