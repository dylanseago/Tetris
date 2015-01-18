/************************************************************************
 *                          PROGRAM HEADER                              *
 ************************************************************************
 * PROGRAMMER'S NAME:    Dylan Seago                                    *
 * DATE:                 Monday, January 21, 2013                       *
 * PROGRAM NAME:         Tetris                                         *
 * CLASS:                ICS4U1                                         *
 * TEACHER:              Mrs. Barsan                                    *
 *                                                                      *
 ************************************************************************
 * WHAT THE PROGRAM DOES                                                *
 * This program is an attempt to recreate the classic game Tetris which *
 * has been around since 1984. The goal is to manipulate the pieces as  *
 * they descend the Matrix and form complete horizontal lines to score  *
 * points and rank up in level. As the level increases the Tetrimino 	*
 * fall speed increases as well.										*
 *                                                                      *
 ************************************************************************
 * CLASSES				                                                *
 *                                                                      *
 * Main - Instantiates the GamePanel class								*
 * 																		*
 * GamePanel - Handles all user interface, interation and game events   *
 * 																		*
 * Piece - Abstract class for all piece types (p0, p1 ... p6)			*
 * 																		*
 * Block - Entity class for a single Mino or block used in a Piece		*
 * 																		*
 * Please see External Documentation for a detailed explanation of all  *
 * fields and methods within this program.								*
 *                                                                      *
 ************************************************************************
 * ERROR HANDLING                                                       *
 * There are no known errors in this program as everything is done using*
 * KeyEvents.															*
 *                                                                      *
 ************************************************************************
 * PROGRAM LIMITATIONS                                                  *
 * This program is limited to 15 levels, once 15 levels are reached the *
 * game is over.
 *                                                                      *
 ************************************************************************
 * EXTENSIONS AND IMPROVEMENTS                                          *
 * This program could be improved by adding game sound FX and background*
 * music. T-spins and comboing could also be added to improve the game.	*
 *                                                                      *
 ***********************************************************************/

package Tetris;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		// Main Frame
		JFrame mFrame = new JFrame();
		// Main Panel
		GamePanel mPanel = new GamePanel();

		// Frame properties
		mFrame.setTitle("Tetris"); // Title
		mFrame.setSize(766, 586); // Size
		mFrame.setResizable(false); // Unresizable
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // End when closed
		mFrame.setLocationRelativeTo(null); // Center window
		mFrame.setIconImage(new ImageIcon(mPanel.getClass().getResource("res/images/pieces/1p.png")).getImage()); // Program icon
		mFrame.add(mPanel); // Adds panel to window
		mFrame.requestFocus();
		mFrame.setVisible(true); // Display the window
	}
}
