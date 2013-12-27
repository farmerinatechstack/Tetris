/* Class: Board.java
 * -----------------
 * Represents a Tetris board -- essentially a 2-d grid
 * of booleans. Supports tetris pieces and row clearing.
 * Has an "undo" feature that allows clients to add and remove pieces efficiently.
 * Does not do any drawing or have any idea of pixels. Instead,
 * just represents the abstract 2-d board.
 */
package tetris;

public class Board	{
	private int maxHeight;
	private int maxHeightBackup;
	
	private int width;
	private int height;
	
	// The abstract representation of the 2-d board is stored via grid and gridBackup
	private boolean[][] grid;
	private boolean[][] gridBackup;
	
	// wArray and wArrayBackup store information regarding how filled given rows are
	private int wArray[];
	private int wArrayBackup[];
	
	// hArray and hArrayBackup store information regarding how filled given columns are
	private int hArray[];
	private int hArrayBackup[];
	
	private boolean DEBUG = true;
	boolean committed;
		
	/* Constructor: Board
	 * ------------------
	 * Creates an empty board of the given width and height
	 * measured in blocks.
	 */
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		
		grid = new boolean[width][height];
		gridBackup = new boolean[width][height];
		
		wArray = new int[height];
		wArrayBackup = new int[height];
		
		hArray = new int[width];
		hArrayBackup = new int[width];
		
		maxHeight = 0;
		maxHeightBackup = 0;
		committed = true;
	}
	
	/* Method: getWidth
	 * ----------------
	 * Returns the width of the board in blocks.
	 */
	public int getWidth() {
		return width;
	}
	
	/* Method: getHeight
	 * -----------------
	 * Returns the height of the board in blocks.
	 */
	public int getHeight() {
		return height;
	}
	
	/* Method: getHeight
	 * -----------------
	 * Returns the max column height present in the board.
	 * For an empty board this is 0.
	 */
	public int getMaxHeight() {
		return maxHeight;	
	}
	
	/* Method: printBoardState
	 * -----------------------
	 * A useful testing method to print the grid formatted as a board
	 */
	public void printBoardState() {
		System.out.println("PRINT BOARD");
		if (DEBUG) {
			System.out.println("Height " + height);
			System.out.println("Width " + width);
			
			for (int y = height-1; y >= 0; y--) {
				System.out.println("-----------------");
				for(int x = 0; x < width; x++) {
					System.out.print(" | " + grid[x][y] + " | ");
				}	
				System.out.println();
			}
		}
	}
	
	/* Method: sanityCheck
	 * -------------------
	 * Checks the board for internal consistency -- used
	 * for debugging.
	 */
	public void sanityCheck() {
		if (DEBUG) {
			int max = 0;
		
			// check if the height array matches the board state
			for (int curCol = 0; curCol < width; curCol++) {
				int colHeight = height-1;
				while(colHeight >= 0 && !grid[curCol][colHeight]) colHeight--;
				colHeight++;
				if (colHeight > max) max = colHeight;
				if (hArray[curCol] != colHeight) throw new RuntimeException("hArray[" + curCol + "] incorrect");
			}
		
			// check if the maxHeight matches the board's max height
			if (max != maxHeight) throw new RuntimeException("maxHeight incorrect");
		
			// check if the width array matches the board state
			for (int curRow = 0; curRow < height; curRow++) {
				int numBlocks = 0;
				for(int curCol = 0; curCol < width; curCol++) {
					if (grid[curCol][curRow]) numBlocks++;
				}
				if (numBlocks != wArray[curRow]) throw new RuntimeException("wArray[" + curRow + "] incorrect");
			}
		}
	}
	
	/* Method: dropHeight
	 * ------------------
	 * Given a piece and an x value for the grid, returns the y
	 * value where the piece would come to rest if it were dropped 
	 * straight down at that x.
	 *
	 * Implementation: use the skirt and the col heights
	 * to compute this fast.
	 */
	public int dropHeight(Piece piece, int x) {
		if (x < 0 || x >= width) throw new RuntimeException("Cannot drop piece out of bounds");
		
		int firstStop = 0;
		int [] skirtVals = piece.getSkirt();
		
		for(int currPieceX = 0; currPieceX < piece.getWidth(); currPieceX++) {
			int stop = getColumnHeight(x+currPieceX) - skirtVals[currPieceX];
			if (stop > firstStop) firstStop = stop;
		}
		
		return firstStop;
	}
	
	/* Method: getColumnHeight
	 * -----------------------
	 * Returns the height of the given column --
	 * i.e. the y value of the highest block + 1.
	 * The height is 0 if the column contains no blocks.
	 */
	public int getColumnHeight(int x) {
		return hArray[x];
	}
	
	/* Method: getRowWidth
	 * -------------------
	 * Returns the number of filled blocks in
	 * the given row.
	 */
	public int getRowWidth(int y) {
		 return wArray[y];
	}
	
	/* Method: getRowWidth
	 * -------------------
	 * Returns true if the given block is filled in the board.
	 * Blocks outside of the valid width/height area
	 * always return true.
	 */
	public boolean getGrid (int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) return true;
		return grid[x][y];
	}
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/* Method: inBounds
	 * ----------------
	 * Returns if a given x,y coordinate is in bounds with respect to the grid.
	 */
	private boolean inBounds(int x, int y, int pW, int pH) {
		return (x >= 0 && x+pW <= width && y >= 0 && y+pH <= height);
	}
	
	/* Method: updateBackups
	 * -----------------------
	 * Updates grid backup data to match the current grid data. The combination
	 * of backup and current data is used so that the grid can be updated (i.e.
	 * pieces can be moved, rows can be cleared, etc.) while also having the
	 * functionality to revert to a previous state if something goes wrong.
	 * 
	 * Note: More is explained in the comments for the "place" method.
	 */
	private void updateBackups() {
		maxHeightBackup = maxHeight;
		
		System.arraycopy(wArray, 0, wArrayBackup, 0, height);
		System.arraycopy(hArray, 0, hArrayBackup, 0, width);
		
		for (int curCol = 0; curCol < width; curCol++) {
			System.arraycopy(grid[curCol], 0, gridBackup[curCol], 0, height);
		}
	}
	
	/* Method: place
	 * -------------
	 * Attempts to add the body of a piece to the board.
	 * Copies the piece blocks into the board grid.
	 * Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 * for a regular placement that causes at least one row to be filled.
	 
	 * Error cases:
	 * A placement may fail in two ways. First, if part of the piece may falls out
	 * of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 * Or the placement may collide with existing blocks in the grid
	 * in which case PLACE_BAD is returned.
	 * In both error cases, the board may be left in an invalid
	 * state. The client can use undo(), to recover the valid, pre-place state.
	 */
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
		committed = false;
		updateBackups();
		
		int result = PLACE_OK;
		int pieceWidth = piece.getWidth();
		int pieceHeight = piece.getHeight();
		
		if (!inBounds(x, y, pieceWidth, pieceHeight)) return PLACE_OUT_BOUNDS;
		// if the y place is lower than the dropHeight, the placement is bad - does not check for floating placements
		if (y < dropHeight(piece, x)) return PLACE_BAD;
		
		for (TPoint pt : piece.getBody()) {
			int xCoord = x + pt.x;
			int yCoord = y + pt.y;
			if (yCoord+1 > maxHeight) maxHeight = yCoord+1;
			
			grid[xCoord][yCoord] = true;
			if (yCoord+1 > hArray[xCoord]) hArray[xCoord] = yCoord+1;
			wArray[yCoord]++;
			if (wArray[yCoord] == width) result = PLACE_ROW_FILLED;
		}
		
		sanityCheck();
		return result;
	}
	
	/* Method: resetHeightArray
	 * ------------------------
	 * Resets the values of the height arrays by starting at the top of each 
	 * column and iterating down until a true is found.
	 */
	private void resetHeightArray() {
		for (int curCol = 0; curCol < width; curCol++) {
			int colHeight = height-1;
			while(colHeight >= 0 && !grid[curCol][colHeight]) colHeight--;
			hArray[curCol] = colHeight+1;
		}
	}
	
	/* Method: shiftRow
	 * ----------------
	 * Shifts a row down based on the number of rows that have been cleared beneath.
	 */
	private void shiftRow(int rowNum, int rowsCleared) {
		for (int colNum = 0; colNum < width; colNum++) {
			grid[colNum][rowNum-rowsCleared] = grid[colNum][rowNum];
			grid[colNum][rowNum] = false;
		}
		wArray[rowNum-rowsCleared] = wArray[rowNum];
		wArray[rowNum] = 0;
	}

	/* Method: clearRows
	 * -----------------
	 * Deletes rows that are filled all the way across, moving
	 * things above down. Returns the number of rows cleared.
	 */
	public int clearRows() {
		int rowsCleared = 0;
		if (committed) updateBackups();
		committed = false;

		// iterates through the rows shifting each row down based on how many rows have been cleared
		for(int rowNum = 0; rowNum < getMaxHeight(); rowNum++) {
			if(wArray[rowNum] == width) {
				wArray[rowNum] = 0;
				shiftRow(rowNum, 0);
				rowsCleared++;
			} else if (rowsCleared > 0) {
				shiftRow(rowNum, rowsCleared);
			}
		}
		
		// the maxHeight must always be decreased by the number of cleared rows
		maxHeight -= rowsCleared;
		// reset hArray - this could have been done more efficiently above but I chose to do it outside the shift row loops to improve readability
		resetHeightArray();
		
		sanityCheck();
		return rowsCleared;
	}

	/* Method: revertToBackups
	 * -----------------------
	 * Revert the current board data structures to the backup states.
	 */ 
	private void revertToBackups() {
		maxHeight = maxHeightBackup;
		
		System.arraycopy(wArrayBackup, 0, wArray, 0, height);
		System.arraycopy(hArrayBackup, 0, hArray, 0, width);
		
		for(int curCol = 0; curCol < width; curCol++) {
			System.arraycopy(gridBackup[curCol], 0, grid[curCol], 0, height);
		}
	}

	/* Method: revertToBackups
	 * -----------------------
	 * Reverts the board to its state before up to one place
	 * and one clearRows();
	 * If the conditions for undo() are not met, such as
	 * calling undo() twice in a row, then the second undo() does nothing.
	 */
	public void undo() {
		if (!committed) {
			committed = true;
			revertToBackups();
			sanityCheck();
		}
	}
	
	/* Method: commit
	 * --------------
	 * Puts the board in the committed state.
	 */
	public void commit() {
		committed = true;	
	}

	/* Method: toString
	 * ----------------
	 * Renders the board state as a big String, suitable for printing.
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}