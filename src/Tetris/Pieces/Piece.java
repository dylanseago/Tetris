package Tetris.Pieces;

public abstract class Piece {
	public int			type, state, dir, x, y;
	public int			blockWidth	= 18;
	public int			startingY	= 20;

	// shape[dir][blocks for dir]
	public Block[][]	shape		= new Block[4][4];

	/*
	 * Guide to piece types: 
	 * 0 = Line Piece (cyan) 
	 * 1 = Square Piece (yellow) 
	 * 2 = T-Piece (purple) 
	 * 3 = L-Piece Right (orange) 
	 * 4 = L-Piece Left (blue) 
	 * 5 = Z-Piece Right (green) 
	 * 6 = Z-Piece Left (pink)
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

	public Piece(int state) {
		this.state = state;
		this.dir = 0;

		setPiecePosition();

		setShape();
	}

	public int getState() {
		// System.out.println(this.state + " getState");
		return this.state;
	}

	public void nextState() {
		this.state--;
		setPiecePosition();
	}

	// Returns the width of the piece
	public int getWidth() {
		if (type == 0) {
			if (dir == 0 || dir == 2)
				return 4;
			else
				return 1;
		} else if (type == 1)
			return 2;
		else {
			if (dir == 0 || dir == 2)
				return 3;
			else
				return 2;
		}
	}

	// Sets the coordinates of the pieces blocks
	public abstract void setShape();

	// Sets the position of the piece
	public abstract void setPiecePosition();

	public void rotatePiece(boolean clockwise) {
		if (clockwise) {
			if (dir != 3)
				dir++;
			else
				dir = 0;
		} else {
			if (dir != 0)
				dir--;
			else
				dir = 3;
		}
	}
}
