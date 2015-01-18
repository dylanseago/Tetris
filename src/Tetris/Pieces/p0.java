package Tetris.Pieces;

public class p0 extends Piece {
	public p0(int state) {
		super(state);
		super.type = 0;
	}

	public void setShape() {
		// north
		super.shape[0][0] = new Block(0, -1, -1);
		super.shape[0][1] = new Block(0, 0, -1);
		super.shape[0][2] = new Block(0, 1, -1);
		super.shape[0][3] = new Block(0, 2, -1);

		// east
		super.shape[1][0] = new Block(0, 0, 1);
		super.shape[1][1] = new Block(0, 0, 0);
		super.shape[1][2] = new Block(0, 0, -1);
		super.shape[1][3] = new Block(0, 0, -2);

		// south
		super.shape[2][0] = new Block(0, -1, 0);
		super.shape[2][1] = new Block(0, 0, 0);
		super.shape[2][2] = new Block(0, 1, 0);
		super.shape[2][3] = new Block(0, 2, 0);

		// west
		super.shape[3][0] = new Block(0, 1, 1);
		super.shape[3][1] = new Block(0, 1, 0);
		super.shape[3][2] = new Block(0, 1, -1);
		super.shape[3][3] = new Block(0, 1, -2);
	}

	public void setPiecePosition() {
		if (this.state == 0) { // Sets position if active
			x = 4;
			y = startingY + 1;
		} else if (this.state == 1) { // Sets position if preview
			x = 488;
			y = 142;
		} else if (this.state > 1) { // Sets position if upcoming
			x = 492;
			y = 160 + (state - 1) * 56;
		}
	}
}
