package tetris;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

public class PieceTest {
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece so1, so2;
	private Piece st1, st2;
	private Piece lo1, lo2, lo3, lo4;
	private Piece lt1, lt2, lt3, lt4;
	private Piece stick;
	private Piece square;

	@Before
	public void setUp() throws Exception {
		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();
		
		so1 = new Piece(Piece.S1_STR);
		so2 = so1.computeNextRotation();
		
		st1 = new Piece(Piece.S2_STR);
		st2 = st1.computeNextRotation();
		
		lo1 = new Piece(Piece.L1_STR);
		lo2 = lo1.computeNextRotation();
		lo3 = lo2.computeNextRotation();
		lo4 = lo3.computeNextRotation();
		
		lt1 = new Piece(Piece.L2_STR);
		lt2 = lt1.computeNextRotation();
		lt3 = lt2.computeNextRotation();
		lt4 = lt3.computeNextRotation();
		
		stick = new Piece(Piece.STICK_STR);
		
		square = new Piece(Piece.SQUARE_STR);	
	}
	
	@Test
	public void testSampleSize() {
		// Check size of pyr piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());
		
		// Now try after rotation
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());
		
		// Now try with some other piece, made a different way
		Piece l = new Piece(Piece.STICK_STR);
		assertEquals(1, l.getWidth());
		assertEquals(4, l.getHeight());
	}
	
	
	// Test the skirt for some pieces
	@Test
	public void testSkirtsComputeNextRotation() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, so1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, so2.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0}, square.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0}, stick.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0}, lt1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 1, 0}, lt2.getSkirt()));
	}
	
	// Test the skirt for some fast rotations of all pieces
	@Test
	public void testSkirtFastRotation() {
		Piece[] testPieces = Piece.getPieces();
		Piece stick = testPieces[0];
		assertTrue(Arrays.equals(new int[] {0}, stick.getSkirt()));
		Piece stick2 = stick.fastRotation();
		assertTrue(Arrays.equals(new int[] {0, 0, 0, 0}, stick2.getSkirt()));
		
		Piece l1 = testPieces[1];
		assertTrue(Arrays.equals(new int[] {0, 0}, l1.getSkirt()));
		Piece l12 = l1.fastRotation();
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, l12.getSkirt()));
		
		Piece l2 = testPieces[2];
		assertTrue(Arrays.equals(new int[] {0, 0}, l2.getSkirt()));
		Piece l22 = l2.fastRotation();
		assertTrue(Arrays.equals(new int[] {1, 1, 0}, l22.getSkirt()));
		Piece l23 =l22.fastRotation();
		assertTrue(Arrays.equals(new int[] {0, 2}, l23.getSkirt()));
		
		Piece s1 = testPieces[3];
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s1.getSkirt()));
		Piece s12 = s1.fastRotation();
		assertTrue(Arrays.equals(new int[] {1, 0}, s12.getSkirt()));
		
		Piece s2 = testPieces[4];
		assertTrue(Arrays.equals(new int[] {1, 0, 0}, s2.getSkirt()));
		Piece s22 = s2.fastRotation();
		assertTrue(Arrays.equals(new int[] {0, 1}, s22.getSkirt()));
		Piece s23 = s22.fastRotation();
		assertTrue(Arrays.equals(new int[] {1, 0, 0}, s23.getSkirt()));
		
		Piece sq = testPieces[5];
		assertTrue(Arrays.equals(new int[] {0, 0}, sq.getSkirt()));
		Piece sq1 = sq.fastRotation();
		assertTrue(Arrays.equals(new int[] {0, 0}, sq1.getSkirt()));
		Piece sq2 = sq1.fastRotation();
		assertTrue(Arrays.equals(new int[] {0, 0}, sq2.getSkirt()));
		
		Piece p = testPieces[6];
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, p.getSkirt()));
		Piece p1 = p.fastRotation();
		assertTrue(Arrays.equals(new int[] {1, 0}, p1.getSkirt()));
		Piece p2 = p1.fastRotation();
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, p2.getSkirt()));
	}
	
	// Test equals
	@Test
	public void testEquals() {
		assertTrue(square.equals(square.computeNextRotation()));
		
		assertFalse(pyr2.equals(pyr3));
		assertFalse(pyr3.equals(pyr4));
		assertTrue(pyr1.equals(pyr4.computeNextRotation()));
		
		assertFalse(lo1.equals(lo3));
		assertFalse(lo2.equals(lo4));
		assertTrue(lo1.equals(lo4.computeNextRotation()));
		
		assertTrue(lt1.equals(lt4.computeNextRotation()));
	}
	
	// Test computeNextRotation compared to fastRotation
	@Test
	public void testRotationTypes() {
		Piece[] testPieces = Piece.getPieces();
		Piece stick = testPieces[0];
		
		assertTrue(stick.fastRotation().equals(this.stick.computeNextRotation()));
		
		Piece p = testPieces[6];
		assertTrue(p.fastRotation().equals(pyr1.computeNextRotation()));
		
		Piece s1 = testPieces[3];
		assertFalse(s1.fastRotation().equals(s1.fastRotation().fastRotation()));
		
		Piece square = testPieces[5];
		assertTrue(square.equals(square.fastRotation()));
	}
	
	// Test width and height
	@Test
	public void testPieceDimensions() {
		assertEquals(3, so1.getWidth());
		assertEquals(2, so2.getWidth());
		assertEquals(3, so2.getHeight());
		
		assertEquals(2, lt1.getWidth());
		assertEquals(3, lt1.getHeight());
		assertEquals(3, lt2.getWidth());
		assertEquals(2, lt2.getHeight());
		assertEquals(2, lt3.getWidth());
		assertEquals(3, lt3.getHeight());
		assertEquals(3, lt4.getWidth());
		assertEquals(2, lt4.getHeight());
		
		assertEquals(4, stick.getHeight());
		assertEquals(4, stick.computeNextRotation().getWidth());
		
		assertEquals(2, square.getHeight());
		assertEquals(2, square.computeNextRotation().getWidth());
	}
	
	// Test width and height
	@Test
	public void testPieceDimensions2() {
		assertEquals(3, st1.getWidth());
		assertEquals(2, st2.getWidth());
		assertEquals(3, st2.getHeight());
		
		assertEquals(2, lo1.getWidth());
		assertEquals(3, lo1.getHeight());
		assertEquals(3, lo2.getWidth());
		assertEquals(2, lo2.getHeight());
		
		assertEquals(2, pyr1.getHeight());
		assertEquals(2, pyr1.computeNextRotation().getWidth());
	}
	
	// Test rotation arrays
	@Test
	public void testRotationArray() {
		Piece[] testPieces = Piece.getPieces();
		Piece l2 = testPieces[2];
		assertEquals(2, l2.getWidth());
		assertEquals(3, l2.getHeight());
		
		assertFalse(l2.equals(l2.fastRotation()));
		assertFalse(l2.equals(l2.fastRotation().fastRotation()));
		assertFalse(l2.equals(l2.fastRotation().fastRotation().fastRotation()));
		assertTrue(l2.equals(l2.fastRotation().fastRotation().fastRotation().fastRotation()));

		Piece l1 = testPieces[1];
		assertFalse(l1.equals(l1.fastRotation()));
		assertFalse(l1.equals(l1.fastRotation().fastRotation()));
		assertFalse(l1.equals(l1.fastRotation().fastRotation().fastRotation()));
		assertTrue(l1.equals(l1.fastRotation().fastRotation().fastRotation().fastRotation()));
		
		assertTrue(square.equals(square.computeNextRotation()));
	}
	
}
