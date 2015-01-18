package Tetris.Pieces;

public class p4 extends Piece {
	public p4(int state) {
		super(state);
		super.type = 4;
	}

	public void setShape() {
		// north
		super.shape[0][0] = new Block(4, -1, 0);
		super.shape[0][1] = new Block(4, 0, 0);
		super.shape[0][2] = new Block(4, 1, 0);
		super.shape[0][3] = new Block(4, -1, 1);

		// east
		super.shape[1][0] = new Block(4, 0, 1);
		super.shape[1][1] = new Block(4, 0, 0);
		super.shape[1][2] = new Block(4, 0, -1);
		super.shape[1][3] = new Block(4, 1, 1);

		// south
		super.shape[2][0] = new Block(4, -1, 0);
		super.shape[2][1] = new Block(4, 0, 0);
		super.shape[2][2] = new Block(4, 1, 0);
		super.shape[2][3] = new Block(4, 1, -1);

		// west
		super.shape[3][0] = new Block(4, 0, 1);
		super.shape[3][1] = new Block(4, 0, 0);
		super.shape[3][2] = new Block(4, 0, -1);
		super.shape[3][3] = new Block(4, -1, -1);
	}

	public void setPiecePosition() {
		if (this.state == 0) { // Sets position when active
			x = 5;
			y = startingY;
		} else if (this.state == 1) { // Sets position of preview piece
			y = 134;
			x = 496;
		} else if (this.state > 1) { // Sets position of upcoming piece
			y = 153 + (state - 1) * 56;
			x = 499;
		}
	}
}
