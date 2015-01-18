/*
 * Thanks to the bug testers:
 * Daniel - moved pieces below board and into each other
 * Stephan - stuck pieces into walls
 */

package Tetris;

import javax.swing.*;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.io.*;

import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import sun.audio.*;

import Tetris.Pieces.*;

public class GamePanel extends JPanel implements ActionListener {
	private static final long	serialVersionUID		= 1L;

	// // Coordinates
	// x position of game area
	int							xBoard					= 277;
	// y position of game area
	int							yBoard					= 450;
	// Block dimension
	int							blockWidth				= 18;

	// Delays
	int							delayDrop				= 1000;
	int							delayPlace				= 300;

	// Timers
	Timer						time					= new Timer(10, this);
	Timer						timeDrop				= new Timer(delayDrop, this);
	Timer						timePlace				= new Timer(delayPlace, this);
	Timer						timeTmode				= new Timer(3000, this);

	/*
	 * Guide to piece types: 
	 * 0 = Line Piece (cyan) 
	 * 1 = Square Piece (yellow) 
	 * 2 = T-Piece (purple) 
	 * 3 = L-Piece Right (orange) 
	 * 4 = L-Piece Left (blue) 
	 * 5 = Z-Piece Right (green) 
	 * 6 = Z-Piece Left (red)
	 */
	/*
	 * Guide to piece state: 
	 * 0 = active 
	 * 1 = preview 
	 * 2 = upcoming 
	 * 3 = 2nd upcoming 
	 * 4 = 3rd upcoming 
	 * 5 = 4th upcoming
	 */
	/*
	 * Guide to piece dir: 
	 * 0 = north 
	 * 1 = east 
	 * 2 = south 
	 * 3 = west
	 */
	ArrayList<Piece>			pieces					= new ArrayList<Piece>();
	ArrayList<Block>			deadBlocks				= new ArrayList<Block>();

	// // Gameplay variables
	int							runningState			= 0;
	// 0 = game not started 1 = playing, 2 = paused, 3 = game over

	ArrayList<Integer>			upcomingPieces			= new ArrayList<Integer>();
	int							pieceStored				= -1;
	boolean						canStore				= true;
	int							level					= 1;
	int							totalLinesCleared		= 0;
	int							score					= 0;
	boolean						prevDiffLine			= false;
	int							pauseState 				= 0;

	// Bonus mode variables
	boolean						tModeActive				= false;
	int							tModeProgress			= 0;
	int[]						tModeKeys				= { KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT,
			KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A };

	// Fonts
	Font						fontBlock;
	Font						fontScore				= new Font("Arial", Font.BOLD, 14);

	// Images
	Image						imageBG					= new ImageIcon(getClass().getResource("res/images/bg.png")).getImage();
	Image						imageBGover				= new ImageIcon(getClass().getResource("res/images/bg1.png")).getImage();
	Image						imageBGblank			= new ImageIcon(getClass().getResource("res/images/bg2.png")).getImage();
	Image						imageOptions			= new ImageIcon(getClass().getResource("res/images/options.png")).getImage();
	Image						imageSaveN				= new ImageIcon(getClass().getResource("res/images/saveN.png")).getImage();
	Image						imageSaveY				= new ImageIcon(getClass().getResource("res/images/saveY.png")).getImage();
	Image						imageGhost				= new ImageIcon(getClass().getResource("res/images/ghost.png")).getImage();

	Image						imageTmodeDead			= new ImageIcon(getClass().getResource("res/images/pieces/troll.png")).getImage();
	Image						imageTmodeActive		= new ImageIcon(getClass().getResource("res/images/pieces/trolla.png")).getImage();
	Image						imageTmodeBG			= new ImageIcon(getClass().getResource("res/images/bgtroll.png")).getImage();
	Image						imageTmodeBGover		= new ImageIcon(getClass().getResource("res/images/bgtroll1.png")).getImage();
	Image						imageTmodeGhost			= new ImageIcon(getClass().getResource("res/images/ghosttroll.png")).getImage();
	Image						imageTmodeSaveN			= new ImageIcon(getClass().getResource("res/images/saveNtroll.png")).getImage();
	Image						imageTmodeSaveY			= new ImageIcon(getClass().getResource("res/images/saveYtroll.png")).getImage();

	Image						imageBlockActive[]		= new Image[7];
	Image						imageBlockDead[]		= new Image[7];
	Image						imagePiecePreview[]		= new Image[7];
	Image						imagePieceUpcoming[]	= new Image[7];

	// Window variables
	Color						colorWindow				= new Color(210, 231, 244);
	BevelBorder					borderWindow			= new BevelBorder(1, Color.BLACK, Color.BLACK);

	// Game begin window
	JWindow						gameBegin				= new JWindow();
	JPanel						panGameBegin			= new JPanel(new BorderLayout());
	JLabel						labBegin				= new JLabel("Press any key to begin!", SwingConstants.CENTER);
	JLabel						labInstructions			= new JLabel("Press 'I' for instructions", SwingConstants.CENTER);

	// Game over window
	JWindow						gameOver				= new JWindow();

	JPanel						panGameOver				= new JPanel(new FlowLayout());
	JPanel						panButs					= new JPanel(new FlowLayout());
	JPanel						panLevel				= new JPanel(new FlowLayout());
	JPanel						panLines				= new JPanel(new FlowLayout());
	JPanel						panScore				= new JPanel(new FlowLayout());
	JLabel						labLevel				= new JLabel("You reached level: ");
	JLabel						labLines				= new JLabel("Total lines cleared: ");
	JLabel						labScore				= new JLabel("     Your final score: ");
	JTextField					fieldLevel				= new JTextField(6);
	JTextField					fieldLines				= new JTextField(6);
	JTextField					fieldScore				= new JTextField(6);

	JButton						butRestart				= new JButton("Restart");
	JButton						butExit					= new JButton("Exit");

	// Constructor
	public GamePanel() {
		loadImages();
		addKeyListener(new AL());
		setFocusable(true);
		getStartingPieces();
		time.start();
		timeDrop.start();

		// Loads the font
		try {
			fontBlock = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("res/AnjaEliane.ttf"));
			fontBlock = fontBlock.deriveFont(48f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		startBGMusic();

		createGameBeginWindow();

		createGameOverWindow();
	}

	public void startBGMusic() { //Plays the background music
		//make a new AudioPlayer.
		AudioPlayer myBackgroundPlayer = AudioPlayer.player;

		ContinuousAudioDataStream myLoop = null;
		//use a try block in case the file doesn’t exist.
		try {
			myLoop = new ContinuousAudioDataStream(new AudioStream(new FileInputStream("/res/audio/bg.wav")).getData());
		} catch (Exception error) {
		}

		// play background music.
		myBackgroundPlayer.start(myLoop);
	}

	// Loads all block images
	private void loadImages() {
		for (int j = 0; j < 7; j++) {
			imageBlockActive[j] = new ImageIcon(getClass().getResource("res/images/pieces/" + j + "a.png")).getImage();
			imageBlockDead[j] = new ImageIcon(getClass().getResource("res/images/pieces/" + j + "d.png")).getImage();
			imagePiecePreview[j] = new ImageIcon(getClass().getResource("res/images/pieces/" + j + "p.png")).getImage();
			imagePieceUpcoming[j] = new ImageIcon(getClass().getResource("res/images/pieces/" + j + "u.png")).getImage();
		}
	}

	// Creates the game begin window
	private void createGameBeginWindow() {
		gameBegin.setSize(240, 140);
		gameBegin.setLocationRelativeTo(null);
		gameBegin.add(panGameBegin);

		panGameBegin.setBackground(colorWindow);
		panGameBegin.setBorder(borderWindow);
		panGameBegin.add(labBegin, BorderLayout.CENTER);
		panGameBegin.add(labInstructions, BorderLayout.SOUTH);

		labBegin.setFont(fontScore);
	}

	// Creates the game over window
	private void createGameOverWindow() {
		// properties of window
		gameOver.setSize(240, 140);
		gameOver.setLocationRelativeTo(null);
		gameOver.add(panGameOver);
		gameOver.setVisible(false);

		panGameOver.setLayout(new BoxLayout(panGameOver, BoxLayout.PAGE_AXIS));
		panGameOver.setBorder(borderWindow);

		// displays level
		panGameOver.add(panLevel);
		panLevel.add(labLevel);
		labLevel.setFont(fontScore);
		panLevel.add(fieldLevel);
		fieldLevel.setEditable(false);
		fieldLevel.setBorder(null);
		fieldLevel.setBackground(colorWindow);
		fieldLevel.setFont(fontScore);
		panLevel.setBackground(colorWindow);

		// displays lines cleared
		panGameOver.add(panLines);
		panLines.add(labLines);
		labLines.setFont(fontScore);
		panLines.add(fieldLines);
		fieldLines.setEditable(false);
		fieldLines.setBorder(null);
		fieldLines.setBackground(colorWindow);
		fieldLines.setFont(fontScore);
		panLines.setBackground(colorWindow);

		// displays score
		panGameOver.add(panScore);
		panScore.add(labScore);
		labScore.setFont(fontScore);
		panScore.add(fieldScore);
		fieldScore.setEditable(false);
		fieldScore.setBorder(null);
		fieldScore.setBackground(colorWindow);
		fieldScore.setFont(fontScore);
		panScore.setBackground(colorWindow);

		// displays restart and exit buttons
		panGameOver.add(panButs);
		panButs.add(butRestart);
		butRestart.setFont(fontScore);
		panButs.add(butExit);
		butExit.setFont(fontScore);
		panButs.setBackground(colorWindow);

		butRestart.addActionListener(this);
		butExit.addActionListener(this);
	}

	// Shows the 'You Lose' window
	private void showGameOver() {
		fieldLevel.setText("" + level);
		fieldLines.setText("" + totalLinesCleared);
		fieldScore.setText("" + score);
		gameOver.setVisible(true);
	}

	// Resets everything
	private void newGame() {
		pieces = new ArrayList<Piece>();
		deadBlocks = new ArrayList<Block>();
		upcomingPieces = new ArrayList<Integer>();
		pieceStored = -1;
		canStore = true;
		level = 1;
		totalLinesCleared = 0;
		score = 0;
		getStartingPieces();

		tModeActive = false;
		delayDrop = 1000;
		timeDrop = new Timer(delayDrop, this);
		timePlace.stop();
		runningState = 0;
		pauseState = 0;
	}

	// Pauses/unpauses the game
	private void pauseGame() {
		if (runningState == 1) {
			runningState = 2;
			if (timePlace.isRunning()) {
				timePlace.stop();
				pauseState = 1;
			} else {
				timeDrop.stop();
				pauseState = 2;
			}
		} else if (runningState == 2) {
			runningState = 1;
			if (pauseState == 1)
				timePlace.start();
			else
				timeDrop.start();
		}

	}

	// Generates a random seven pieces
	private void newRandomUpcoming() {
		// Sets them all ordered 1-7
		for (int i = 0; i < 7; i++) {
			upcomingPieces.add(i);
		}

		// Randomizes the order
		for (int i = 0; i < 5; i++) {
			int i1 = (int) (Math.random() * 6);
			int i2 = (int) (Math.random() * 6);

			int temp = upcomingPieces.get(i1);
			upcomingPieces.set(i1, upcomingPieces.get(i2));
			upcomingPieces.set(i2, temp);
		}
	}

	// Gets the next piece that should be upcoming
	private int getNextUpcoming() {
		// Creates a new random order if current order is empty
		if (upcomingPieces.size() == 0)
			newRandomUpcoming();
		// Stores next piece in a temp var
		int temp = upcomingPieces.get(0);
		// Removes that upcoming piece
		upcomingPieces.remove(0);
		return temp;
	}

	// Creates the starting pieces
	private void getStartingPieces() {
		newRandomUpcoming();
		for (int i = 0; i < 6; i++) {
			newPiece(getNextUpcoming(), i);
		}
	}

	// Creates a new piece
	private void newPiece(int type, int state) {
		switch (type) {
			case 0:
				pieces.add(new p0(state));
				break;
			case 1:
				pieces.add(new p1(state));
				break;
			case 2:
				pieces.add(new p2(state));
				break;
			case 3:
				pieces.add(new p3(state));
				break;
			case 4:
				pieces.add(new p4(state));
				break;
			case 5:
				pieces.add(new p5(state));
				break;
			case 6:
				pieces.add(new p6(state));
				break;
		}
	}

	// Replaces current piece
	private void replacePiece(int type) {
		switch (type) {
			case 0:
				pieces.set(0, new p0(0));
				break;
			case 1:
				pieces.set(0, new p1(0));
				break;
			case 2:
				pieces.set(0, new p2(0));
				break;
			case 3:
				pieces.set(0, new p3(0));
				break;
			case 4:
				pieces.set(0, new p4(0));
				break;
			case 5:
				pieces.set(0, new p5(0));
				break;
			case 6:
				pieces.set(0, new p6(0));
				break;
		}
	}

	// Gets the next piece in line
	private void nextPiece() {
		pieces.remove(0);// Deletes placed block from active blocks

		// Shifts all the states of remaining blocks
		for (int i = 0; i < 5; i++)
			pieces.get(i).nextState();

		newPiece(getNextUpcoming(), 5);// Creates a new upcoming block
	}

	// Places a piece on the board
	private void placePiece() {
		// Adds placed block to array of inactive blocks
		for (int i = 0; i < 4; i++) {
			// Change the relative block location into an absolute location
			pieces.get(0).shape[pieces.get(0).dir][i].x = pieces.get(0).x + pieces.get(0).shape[pieces.get(0).dir][i].x;
			pieces.get(0).shape[pieces.get(0).dir][i].y = pieces.get(0).y + pieces.get(0).shape[pieces.get(0).dir][i].y;
			// Adds piece blocks to deadBlocks
			deadBlocks.add(pieces.get(0).shape[pieces.get(0).dir][i]);
		}
		nextPiece();
		canStore = true;
	}

	// Drops the current piece
	private void dropPiece() {
		int ini = pieces.get(0).y;
		int end = checkLowestDrop();
		score += (ini - end) * 2;
		pieces.get(0).y = end;
	}

	// Checks for the lowest location to drop to
	private int checkLowestDrop() {
		// Checks through all elevations
		for (int y = pieces.get(0).y; y >= -1; y--) {
			if (checkCollision(pieces.get(0).x, y)) {
				return y + 1;
			}
		}
		return 0;
	}

	// Checks if the active block is colliding with a specified 'x' and 'y'
	private boolean checkCollision(int x, int y) {
		for (int i = 0; i < 4; i++) {
			// block[i] of the active piece offset from reference point
			int blockXoffset = pieces.get(0).shape[pieces.get(0).dir][i].x;
			int blockYoffset = pieces.get(0).shape[pieces.get(0).dir][i].y;

			// Checks if the y value is below the board
			if (y + blockYoffset < 0)
				return true;

			// Checks if the x value is outside the board
			if (x + blockXoffset < 0 || x + blockXoffset > 9)
				return true;

			// Checks if the block is at the same location a dead block
			for (int j = 0; j < deadBlocks.size(); j++)
				if (x + blockXoffset == deadBlocks.get(j).x && y + blockYoffset == deadBlocks.get(j).y)
					return true;
		}
		return false;
	}

	// Resets the timer for block placement
	private void resetPlaceTimer() {
		if (timePlace.isRunning())
			timePlace.restart();
	}

	// Rotates the active piece clockwise or counter-clockwise as specified
	private void rotateActivePiece(boolean clockwise) {
		// Rotates the piece
		pieces.get(0).rotatePiece(clockwise);
		// Checks if it collides at current location as well as one block in each direction, if so, rotates it back
		if (!checkCollision(pieces.get(0).x, pieces.get(0).y)) {
			resetPlaceTimer();
		} else if (!checkCollision(pieces.get(0).x, pieces.get(0).y + 1)) {
			resetPlaceTimer();
			pieces.get(0).y++;
		} else if (!checkCollision(pieces.get(0).x + 1, pieces.get(0).y)) {
			resetPlaceTimer();
			pieces.get(0).x++;
		} else if (!checkCollision(pieces.get(0).x, pieces.get(0).y - 1)) {
			resetPlaceTimer();
			pieces.get(0).y--;
		} else if (!checkCollision(pieces.get(0).x - 1, pieces.get(0).y)) {
			resetPlaceTimer();
			pieces.get(0).x--;
		} else
			pieces.get(0).rotatePiece(!clockwise);
	}

	// Checks for completed lines and removes them
	private void clearCompletedLines() {
		int nCleared = 0;
		// creates new array all set to 0
		int yCount[] = new int[20];
		for (int i = 0; i < 20; i++)
			yCount[i] = 0;

		// counts blocks on each line
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < deadBlocks.size(); j++)
				if (deadBlocks.get(j).y == i)
					yCount[i]++;
		}

		// checks for completed lines
		for (int i = 19; i >= 0; i--)
			if (yCount[i] == 10) {
				for (int j = deadBlocks.size() - 1; j >= 0; j--)
					// Removes cleared lines
					if (deadBlocks.get(j).y == i)
						deadBlocks.remove(j);

				// Shifts all blocks down
				for (int j = 0; j < deadBlocks.size(); j++)
					if (deadBlocks.get(j).y > i)
						deadBlocks.get(j).y--;
				totalLinesCleared++;
				nCleared++;
			}

		// If a line has been cleared
		if (nCleared > 0) {
			switch (nCleared) {
				case 1:
					score += 100 * level;
					prevDiffLine = false;
					break;
				case 2:
					score += 300 * level;
					totalLinesCleared++;
					prevDiffLine = false;
					break;
				case 3:
					score += 500 * level;
					totalLinesCleared += 2;
					prevDiffLine = false;
					break;
				case 4:
					if (prevDiffLine) {
						score += 1200 * level;
						totalLinesCleared += 8;
					} else {
						score += 800 * level;
						totalLinesCleared += 4;
					}
					prevDiffLine = true;
					break;
			}
			// Checks for a perfect clear
			if (deadBlocks.size() == 0) {
				totalLinesCleared += 10;
				score += 2500 * level;
			}
		}

		// Increases level when goal is reached
		if (getTotalLinesLevel() - totalLinesCleared <= 0) {
			delayDrop = (int) (delayDrop * 0.75);
			timeDrop = new Timer(delayDrop, this);
			level++;
		}
	}

	// Gets the total lines cleared required for current level
	private int getTotalLinesLevel() {
		return (int) (2.5 * (level * level + level));
	}

	// Draws a string in the block letter style with an outline
	private void drawBlockString(String s, int x, int y, Graphics2D g2d) {
		// Draws the string with 'blockFont'
		if (tModeActive)
			g2d.setColor(new Color(0, 0, 255));
		else
			g2d.setColor(new Color(255, 255, 0));
		g2d.setFont(fontBlock);
		g2d.drawString(s, x, y);

		// Creates a translation for the outline
		AffineTransform at = new AffineTransform();
		at.translate(x, y);

		// Draws outline of the string
		g2d.setColor(new Color(0, 0, 0));
		g2d.draw(new TextLayout(s, fontBlock, g2d.getFontRenderContext()).getOutline(at));
	}

	// Checks if the user is entering the bonus code
	private void tModeAdvancementCheck(KeyEvent e) {
		if (tModeProgress == 10)
			tModeProgress = 0;
		if (e.getKeyCode() == tModeKeys[tModeProgress])
			tModeProgress++;
		else
			tModeProgress = 0;
	}

	// Draws all images
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// Main menu
		if (runningState == 0) {
			g2d.drawImage(imageBGblank, 0, 0, null);
			gameBegin.setVisible(true);
		}

		// Main game
		if (runningState != 0) {
			if (tModeActive)
				// Background
				g2d.drawImage(imageTmodeBG, 0, 0, null);
			else
				g2d.drawImage(imageBG, 0, 0, null);

			// Draws the ghost blocks for active piece
			{
				int lowestY = checkLowestDrop();
				for (int i = 0; i < 4; i++) {
					int tBlockX = xBoard + blockWidth * (pieces.get(0).x + pieces.get(0).shape[pieces.get(0).dir][i].x);
					int tBlockY = yBoard - blockWidth - blockWidth * (lowestY + pieces.get(0).shape[pieces.get(0).dir][i].y);
					if (tModeActive)
						g2d.drawImage(imageTmodeGhost, tBlockX + 1, tBlockY, null);
					else
						g2d.drawImage(imageGhost, tBlockX + 1, tBlockY, null);
				}
			}

			// Draw the active block
			for (int i = 0; i < 4; i++) {
				int tBlockX = xBoard + blockWidth * (pieces.get(0).x + pieces.get(0).shape[pieces.get(0).dir][i].x);
				int tBlockY = yBoard - blockWidth - blockWidth * (pieces.get(0).y + pieces.get(0).shape[pieces.get(0).dir][i].y);
				if (tModeActive)
					g2d.drawImage(imageTmodeActive, tBlockX, tBlockY, null);
				else
					g2d.drawImage(imageBlockActive[pieces.get(0).type], tBlockX, tBlockY, null);
			}

			// Draw the stored block
			if (pieceStored != -1) {
				if (pieceStored == 0)
					g2d.drawImage(imagePiecePreview[pieceStored], 184, 143, null);
				if (pieceStored == 1)
					g2d.drawImage(imagePiecePreview[pieceStored], 199, 135, null);
				if (pieceStored > 1)
					g2d.drawImage(imagePiecePreview[pieceStored], 191, 135, null);

			}

			// Draw the preview block
			g2d.drawImage(imagePiecePreview[pieces.get(1).type], pieces.get(1).x, pieces.get(1).y, null);

			// Draws the upcoming blocks
			for (int i = 2; i < 6; i++)
				g2d.drawImage(imagePieceUpcoming[pieces.get(i).type], pieces.get(i).x, pieces.get(i).y, null);

			// Draws the inactive blocks
			for (int i = 0; i < deadBlocks.size(); i++) {
				int tBlockX = xBoard + deadBlocks.get(i).x * blockWidth;
				int tBlockY = yBoard - blockWidth - deadBlocks.get(i).y * blockWidth;
				if (tModeActive)
					g2d.drawImage(imageTmodeDead, tBlockX, tBlockY, null);
				else
					g2d.drawImage(imageBlockDead[deadBlocks.get(i).type], tBlockX, tBlockY, null);
			}

			// Draws the header overlay
			if (tModeActive)
				g2d.drawImage(imageTmodeBGover, 0, 0, null);
			else
				g2d.drawImage(imageBGover, 0, 0, null);

			// Level, goal and score
			{
				String levelS = "" + level;
				drawBlockString(levelS, (levelS.length() == 1) ? 210 : 194, 280, g2d);
				String goalS = "" + (getTotalLinesLevel() - totalLinesCleared);
				drawBlockString(goalS, (goalS.length() == 1) ? 210 : 194, 400, g2d);
				String scoreS = "" + score;
				drawBlockString(scoreS, 460 - g.getFontMetrics(fontBlock).stringWidth(scoreS), 80, g2d);
			}

			if (canStore) {
				if (tModeActive)
					g2d.drawImage(imageTmodeSaveY, 169, 104, null);
				else
					g2d.drawImage(imageSaveY, 169, 104, null);
			} else {
				if (tModeActive)
					g2d.drawImage(imageTmodeSaveN, 167, 102, null);
				else
					g2d.drawImage(imageSaveN, 167, 102, null);
			}
		}

		// Game over screen
		if (runningState == 2) {
			g2d.drawImage(imageOptions, 145, 182, null);
		}
	}

	// if an action occurs
	public void actionPerformed(ActionEvent e) {
		if (runningState == 1) {
			clearCompletedLines();

			// Checks if pieces are above the board
			for (int i = 0; i < deadBlocks.size(); i++) {
				if (deadBlocks.get(i).y > 19) {
					runningState = 3;
					showGameOver();
					break;
				}
			}

			// Stops the dropping delay and begins the placing delay
			if (checkCollision(pieces.get(0).x, pieces.get(0).y - 1) && !timePlace.isRunning()) {
				timeDrop.stop();
				timePlace = new Timer(delayPlace, this);
				timePlace.start();
			} else if (!checkCollision(pieces.get(0).x, pieces.get(0).y - 1) && !timeDrop.isRunning()) {
				timePlace.stop();
				timeDrop = new Timer(delayDrop, this);
				timeDrop.start();
			}

			// If the place timer activates, place piece
			if (e.getSource() == timePlace) {
				placePiece();
			}

			// If the drop timer activates, drop piece
			if (e.getSource() == timeDrop) {
				pieces.get(0).y--;
			}

			if (tModeActive && e.getSource() == timeTmode) {
				int wid = Toolkit.getDefaultToolkit().getScreenSize().width - 760;
				int hei = Toolkit.getDefaultToolkit().getScreenSize().height - 560;
				double r1 = Math.random();
				double r2 = Math.random();
				getTopLevelAncestor().setLocation((int) (r1 * wid), (int) (r2 * hei));
			}

		}

		// Restarts game
		if (e.getSource() == butRestart) {
			gameOver.dispose();
			newGame();
		}

		// Quits game
		if (e.getSource() == butExit) {
			System.exit(0);
		}

		repaint();
	}

	private class AL extends KeyAdapter {
		public void keyTyped(KeyEvent e) {
			e.consume();
		}

		public void keyReleased(KeyEvent e) {
			int k = e.getKeyCode();

			// Game restart
			if (k == KeyEvent.VK_R)
				newGame();

			// Game exit
			if (k == KeyEvent.VK_ESCAPE && (runningState != 1) && !tModeActive)
				System.exit(0);

			// Game pause
			if ((k == KeyEvent.VK_P || k == KeyEvent.VK_ESCAPE) && runningState != 0)
				pauseGame();

			// Game tutorial
			if (k == KeyEvent.VK_I)
				if (Desktop.isDesktopSupported()) {
					try {
						File myFile = new File("Instructions.pdf");
						Desktop.getDesktop().open(myFile);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			// Bonus mode
			tModeAdvancementCheck(e);
			if (tModeProgress == 10) {
				tModeActive = !tModeActive;
				if (tModeActive) {
					timeTmode.start();
					((JFrame) getTopLevelAncestor()).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				} else {
					timeTmode.stop();
					((JFrame) getTopLevelAncestor()).setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				}
			}

			// Begins the game when any key is pressed
			if (runningState == 0 && k != KeyEvent.VK_I) {
				gameBegin.dispose();
				runningState = 1;
			}
		}

		public void keyPressed(KeyEvent e) {
			int k = e.getKeyCode();

			if (runningState == 1) {
				// Hard drop
				if (k == KeyEvent.VK_SPACE) {
					dropPiece();
					placePiece();
				}

				// Stores piece
				if ((k == KeyEvent.VK_SHIFT || k == KeyEvent.VK_C) && canStore) {
					// If no other piece has been stored
					if (pieceStored == -1) {
						pieceStored = pieces.get(0).type;
						canStore = false;
						nextPiece();
					}
					// If a piece is currently stored 
					else {
						int tempType = pieceStored;
						pieceStored = pieces.get(0).type;
						canStore = false;
						replacePiece(tempType);
					}
				}

				// Right movement
				if (k == KeyEvent.VK_RIGHT) {
					if (!checkCollision(pieces.get(0).x + 1, pieces.get(0).y))
						pieces.get(0).x++;
					resetPlaceTimer();
				}

				// Left movement
				if (k == KeyEvent.VK_LEFT) {
					if (!checkCollision(pieces.get(0).x - 1, pieces.get(0).y))
						pieces.get(0).x--;
					resetPlaceTimer();
				}

				// Clockwise rotation
				if (k == KeyEvent.VK_UP || k == KeyEvent.VK_X) {
					rotateActivePiece(true);
				}

				// Counter-clockwise rotation
				if (k == KeyEvent.VK_CONTROL || k == KeyEvent.VK_Z) {
					rotateActivePiece(false);
				}

				// Soft drop
				if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) {
					if (!checkCollision(pieces.get(0).x, pieces.get(0).y - 1)) {
						pieces.get(0).y--;
						score++;
					}
				}
			}
		}
	}

}
