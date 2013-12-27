/* Class: JBrainTetris.java
 * ------------------------
 * The class sets up the GUI and enables users to play Tetris.
 */
package tetris;

import java.awt.*;

import javax.swing.*;

import java.util.*;
import java.awt.event.*;

import javax.swing.event.*;

import java.awt.Toolkit;

public class JBrainTetris extends JTetris {
	// GUI options
	JPanel little;
	JCheckBox brainMode;
	JSlider adversary;
	JLabel adversaryLabel;
	
	DefaultBrain brain;
	Brain.Move bestMove;
	
	private int curCount;
	
	/* Constructor: JBrainTetris
	 * -------------------------
	 * Sets up the Tetris game and Brain for play.
	 */
	JBrainTetris(int pixels) {
		super(pixels);
		curCount = 0;
		
		brain = new DefaultBrain();
	}
	
	/* Method: createControlPanel
	 * --------------------------
	 * Creates the Tetris control panel.
	 */
	@Override
	public JComponent createControlPanel() {
		JComponent brainPanel = super.createControlPanel();
		
		brainPanel.add(new JLabel("Brain: "));
		brainMode = new JCheckBox("Brain active");
		brainPanel.add(brainMode);
		
		little = new JPanel();
		little.add(new JLabel("Adversary: "));
		adversary = new JSlider(0, 100, 0);
		adversary.setPreferredSize(new Dimension(100, 15));
		adversaryLabel = new JLabel("ok");
		little.add(adversary);
		little.add(adversaryLabel);
		brainPanel.add(little);
		
		return brainPanel;
	}
	
	/* main
	 * --------------------------
	 * Creates a frame with a JBrainTetris.
	 */
	public static void main(String[] args) {
		// Set GUI Look And Feel Boilerplate.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		JTetris tetris = new JBrainTetris(16);
		JFrame frame = JBrainTetris.createFrame(tetris);
		frame.setVisible(true);
	}
	
	/* Method: tick
	 * ------------
	 * The overrided tick method supports functionality for an AI brain to play Tetris 
	 */
	@Override
	public void tick(int verb) {
		if (brainMode.isSelected() && verb == DOWN) {	// play a brain tick
			board.undo();
			if (curCount != count) {
				curCount = count;
				bestMove = brain.bestMove(board, currentPiece, HEIGHT, null);
			}
			
			if (bestMove != null) {
				if(!currentPiece.equals(bestMove.piece)) {
					super.tick(ROTATE);
				}
			
				if (bestMove.x < currentX) {
					super.tick(LEFT);
				} else if (bestMove.x > currentX){
					super.tick(RIGHT);
				}
			}
			// the piece must move down following any rotation and/or left/right move
			super.tick(DOWN);
		} else {	// play a standard tick
			super.tick(verb);
		}
	}
	
	/* Method: pickNextPiece
	 * ---------------------
	 * The overrided pickNextPiece supports functionality for an adversarial game in which
	 * the computer selects the worst pieces for play - based on the brain functionality
	 * 
	 * Note: this is an interesting sample of code re-use. Originally, bestMove was created 
	 * to select the move with the best rating. However, it can be utilized to select a
	 * horrible move.
	 */
	@Override
	public Piece pickNextPiece() {
		Piece piece = null;
		// get a random number from 1 to 99
		Random r = new Random();
		int randVal = r.nextInt(98) + 1;
		int sliderVal = adversary.getValue();
		
		if (randVal < sliderVal) {	// adversary intervenes whenever sliderVal is greater than randVal
			adversaryLabel.setText("*ok*");
			double worstScore = 0;
			
			// iterate through the pieces to find the worst piece
			for(Piece p : super.pieces) {
				Brain.Move currMove = brain.bestMove(board, p, HEIGHT, null);
				// if no bestMove is found, default to the super's pick piece
				if (currMove == null) return super.pickNextPiece();
				if (currMove.score > worstScore) {
					worstScore = currMove.score;
					piece = p;
				}
			}
		} else {	// adversary does not intervene
			adversaryLabel.setText("ok");
			piece = super.pickNextPiece();
		}
		return piece;
	}
	
}