/* Class: Piece
 * ------------
 * An immutable representation of a tetris piece in a particular rotation.
 * Each piece is defined by the blocks that make up its body.
 */
package tetris;

import java.util.*;

public class Piece {
	private TPoint[] body;
	private int[] skirt;
	private int width;
	private int height;
	private Piece next; // "next" rotation

	static private Piece[] pieces;	// singleton static array of first rotations

	/* Constructor: Piece
	 * ------------------
	 * Defines a new piece given a TPoint[] array of its body.
	 * Makes its own copy of the array and the TPoints inside it.
	 * The points array must be properly formed - no duplicates.
	 */
	public Piece(TPoint[] points) {
		body = points;
		
		// set the width and height of the piece
		for(TPoint point : points) {
			if (point.x >= this.width) this.width = point.x + 1;
			if (point.y >= this.height) this.height = point.y + 1;
		}
		
		
		// set skirt values 
		skirt = new int [this.width];
		for(int i = 0; i < width; i++) {
			skirt[i] = height;
		}
		
		// iterate through x's and all points checking for matching xCoords - if y is less than skirt value, update skirt value 
		for(int currX = 0; currX < width; currX++) {
			for(TPoint skirtPoint : body) {
				// dependent on well-formed body string
				if(skirtPoint.x == currX && skirtPoint.y < skirt[currX]) skirt[currX] = skirtPoint.y;
			}
		}
		
		next = null;
	}
	

	
	
	/* Constructor: Piece
	 * ------------------
	 * Alternate constructor, takes a String with the x,y body points
	 * all separated by spaces, such as "0 0  1 0  2 0	1 1".
	 * (provided)
	 */
	public Piece(String points) {
		this(parsePoints(points));
	}

	/* Method: getWidth
	 * ----------------
	 * Returns the width of the piece measured in blocks.
	 */
	public int getWidth() {
		return width;
	}

	/* Method: getHeight
	 * -----------------
	 * Returns the height of the piece measured in blocks.
	 */
	public int getHeight() {
		return height;
	}

	/* Method: getBody
	 * ---------------
	 * Returns a pointer to the piece's body. The caller
	 * should not modify this array.
	 */
	public TPoint[] getBody() {
		return body;
	}

	/* Method: getSkirt
	 * ----------------
	 * Returns a pointer to the piece's skirt. For each x value
	 * across the piece, the skirt gives the lowest y value in the body.
	 * This is useful for computing where the piece will land.
	 * The caller should not modify this array.
	 */
	public int[] getSkirt() {
		return skirt;
	}

	
	/* Method: computeNextRotation
	 * ---------------------------
	 * Returns a new piece that is 90 degrees counter-clockwise
	 * rotated from the receiver.
	 */
	public Piece computeNextRotation() {
		// make new array of points based on old array in string format
		String nextBody = "";
		
		for(TPoint t : body) {
			int updatedX = height - t.y - 1;
			int updatedY = t.x;
			
			nextBody += updatedX;
			nextBody += " ";
			nextBody += updatedY;
			nextBody += " ";
		}
		
		// construct and return the new piece
		Piece nextPiece = new Piece(nextBody);
		return nextPiece;
	}

	/* Method: fastRotation
	 * --------------------
	 * Returns a pre-computed piece that is 90 degrees counter-clockwise
	 * rotated from the receiver. Fast because the piece is pre-computed.
	 * This only works on pieces set up by makeFastRotations(), and otherwise
	 * just returns null.
	 */	
	public Piece fastRotation() {
		return next;
	}
	


	/* Method: equals
	 * --------------
	 * Returns true if two pieces are the same --
	 * their bodies contain the same points.
	 * Interestingly, this is not the same as having exactly the
	 * same body arrays, since the points may not be
	 * in the same order in the bodies. Used internally to detect
	 * if two rotations are effectively the same.
	 */
	public boolean equals(Object obj) {
		// standard equals() technique 1
		if (obj == this) return true;
		
		// standard equals() technique 2
		// (null will be false)
		if (!(obj instanceof Piece)) return false;
		Piece other = (Piece)obj;
		
		// iterate through the points of other's body - each point must have a match in this.body at some point
		for (TPoint otherP : other.body) {
			boolean found = false;
			for(TPoint currP : body) {
				// if a match is found for the given point, set found to true
				if (otherP.equals(currP)) found = true;
			}
			// return false if the point was not found
			if (!found) return false;
		}

		return true;
	}

	// String constants for the standard 7 tetris pieces
	public static final String STICK_STR	= "0 0	0 1	 0 2  0 3";
	public static final String L1_STR		= "0 0	0 1	 0 2  1 0";
	public static final String L2_STR		= "0 0	1 0 1 1	 1 2";
	public static final String S1_STR		= "0 0	1 0	 1 1  2 1";
	public static final String S2_STR		= "0 1	1 1  1 0  2 0";
	public static final String SQUARE_STR	= "0 0  0 1  1 0  1 1";
	public static final String PYRAMID_STR	= "0 0  1 0  1 1  2 0";
	
	// Indexes for the standard 7 pieces in the pieces array
	public static final int STICK = 0;
	public static final int L1	  = 1;
	public static final int L2	  = 2;
	public static final int S1	  = 3;
	public static final int S2	  = 4;
	public static final int SQUARE	= 5;
	public static final int PYRAMID = 6;
	
	/* Method: getPieces
	 * -----------------
	 * Returns an array containing the first rotation of each of the 7 standard 
	 * tetris pieces in the order STICK, L1, L2, S1, S2, SQUARE, PYRAMID. The next 
	 * (counterclockwise) rotation can be obtained from each piece with the message. 
	 * In this way, the client can iterate through all the rotations until eventually 
	 * getting back to the first rotation.
	 */
	public static Piece[] getPieces() {
		// lazy evaluation -- create static array if needed
		if (Piece.pieces==null) {
			// use makeFastRotations() to compute all the rotations for each piece
			Piece.pieces = new Piece[] {
				makeFastRotations(new Piece(STICK_STR)),
				makeFastRotations(new Piece(L1_STR)),
				makeFastRotations(new Piece(L2_STR)),
				makeFastRotations(new Piece(S1_STR)),
				makeFastRotations(new Piece(S2_STR)),
				makeFastRotations(new Piece(SQUARE_STR)),
				makeFastRotations(new Piece(PYRAMID_STR)),
			};
		}
		return Piece.pieces;
	}
	


	/* Method: makeFastRotations
	 * -------------------------
	 * Given the "first" root rotation of a piece, computes all the other rotations 
	 * and links them all together in a circular list. The list loops back to the root as soon
	 * as possible. Returns the root piece. fastRotation() relies on the pointer structure 
	 * setup here.
	 * 
	 * Note: uses computeNextRotation() and Piece.equals() to detect when 
	 * the rotations have gotten us back to the first piece.
	 */
	private static Piece makeFastRotations(Piece root) {
		Piece currPiece = root;
		while (true) {
			Piece nextPiece = currPiece.computeNextRotation();
			if (nextPiece.equals(root)) break;
			currPiece.next = nextPiece;
			currPiece = nextPiece;
		}
		currPiece.next = root;
		
		return root;
	}
	
	

	/* Method: parsePoints
	 * -------------------
	 * Given a string of x,y pairs ("0 0	0 1 0 2 1 0"), parses
	 * the points into a TPoint[] array.
	 */
	private static TPoint[] parsePoints(String string) {
		List<TPoint> points = new ArrayList<TPoint>();
		StringTokenizer tok = new StringTokenizer(string);
		try {
			while(tok.hasMoreTokens()) {
				int x = Integer.parseInt(tok.nextToken());
				int y = Integer.parseInt(tok.nextToken());
				
				points.add(new TPoint(x, y));
			}
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse x,y string:" + string);
		}
		
		// Make an array out of the collection
		TPoint[] array = points.toArray(new TPoint[0]);
		return array;
	}

}
