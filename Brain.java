/* Interface: Brain.java
 * ---------------------
 * The interface for Tetris brains
 */

package tetris;

public interface Brain {
    // Move is used as a struct to store a single Move
    public static class Move {
        public int x;
        public int y;
        public Piece piece;
        public double score;    // lower scores represent better moves
    }
    
    /* Method: bestMove
     * ----------------
     * Given a piece and a board, returns a move object that represents
     * the best play for that piece, or returns null if no play is possible.
     * The board should be in the committed state when this is called.
     
     * limitHeight is the height of the lower part of the board that pieces
     * must be inside when they land for the game to keep going
     * -- typically 20 (i.e. board.getHeight() - 4)
     * If the passed in move is non-null, it is used to hold the result
     * (just to save the memory allocation).
     */
    public Brain.Move bestMove(Board board, Piece piece, int limitHeight, Brain.Move move);
}
