import java.util.LinkedHashMap;

class graph_struct{

	
	//===== NEIGHBOURHOOD STRUCTURES =====
	
	//----- types of neighbourhoods
	//N_1 --> 4pt, ver/hor
	//N_2 --> 4pt, diag/xdiag
	private static final int[][] N_rook1 = {{-1,0},{0,1},{1,0},{0,-1}};
	private static final int[][] N_bishop1 = {{-1,-1},{1,1},{-1,1},{1,-1}};
	private static final int[][] N_rook2 = {{-2,0},{0,2},{2,0},{0,-2}};
	private static final int[][] N_knight1 = {{-2,1},{-1,2},{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1}};
	private static final int[][] N_bishop2 = {{-2,-2},{2,2},{-2,2},{2,-2}};
	
	//===== PREMADE NEIGHBOURHOOD SETS =====
	
	//----- ising 4pt
	public static final LinkedHashMap<String, int[][]> ROOK1 = dia1();
	private static LinkedHashMap<String, int[][]> dia1() {
		LinkedHashMap<String,int[][]> N_LIB = new LinkedHashMap<String,int[][]> ();
		N_LIB.put("rook1",N_rook1); 
	    return N_LIB;
	}

	//----- ising 4pt-cross
	public static final LinkedHashMap<String, int[][]> BISHOP1 = bishop1();
	private static LinkedHashMap<String, int[][]> bishop1() {
		LinkedHashMap<String,int[][]> N_LIB = new LinkedHashMap<String,int[][]> ();
		N_LIB.put("bishop1",N_bishop1); 
	    return N_LIB;
	}
	
	//----- ising 8pt
	public static final LinkedHashMap<String, int[][]> SQUARE1 = sq1();
	private static LinkedHashMap<String, int[][]> sq1() {
		LinkedHashMap<String,int[][]> N_LIB = new LinkedHashMap<String,int[][]> ();
		N_LIB.put("rook1",N_rook1); 
		N_LIB.put("bishop1",N_bishop1); 
	    return N_LIB;
	}
	
	public static final LinkedHashMap<String, int[][]> DIAMOND2 = dia2();
	private static LinkedHashMap<String, int[][]> dia2() {
		LinkedHashMap<String,int[][]> N_LIB = new LinkedHashMap<String,int[][]> ();
		N_LIB.put("rook1",N_rook1); 
		N_LIB.put("bishop1",N_bishop1); 
		N_LIB.put("rook2",N_rook2);
	    return N_LIB;
	}
	
	//----- additional circle
	public static final LinkedHashMap<String, int[][]> OCTOGON1 = octo1();
	private static LinkedHashMap<String, int[][]> octo1() {
		LinkedHashMap<String,int[][]> N_LIB = new LinkedHashMap<String,int[][]> ();
		N_LIB.put("rook1",N_rook1); 
		N_LIB.put("bishop1",N_bishop1);
		N_LIB.put("rook2",N_rook2);
		N_LIB.put("knight1",N_knight1);
		N_LIB.put("bishop2",N_bishop2);
	    return N_LIB;
	}
	
	public static final LinkedHashMap<String, int[][]> SQUARE2 = sq2();
	private static LinkedHashMap<String, int[][]> sq2() {
		LinkedHashMap<String,int[][]> N_LIB = new LinkedHashMap<String,int[][]> ();
		N_LIB.put("rook1",N_rook1); 
		N_LIB.put("bishop1",N_bishop1);
		N_LIB.put("rook2",N_rook2);
		N_LIB.put("knight1",N_knight1);
		N_LIB.put("bishop2",N_bishop2);
	    return N_LIB;
	}
	//===== CLIQUE STRUCTURES =====
	
	//----- types of cliques
	//C_N1 --> self
	//C_E1 --> ver
	//C_E2 --> hor
	//C_E3 --> diag
	//C_E4 --> xdiag
	/*** self */
	public static int[][] C_N1 = {{0,0}};
	
	/*** 1-rook1_vertical */
	public static int[][] C_E_r1v = {{0,0},{0,1}};
	/*** 1-rook1_horizontal */
	public static int[][] C_E_r1h = {{0,0},{1,0}};
	/*** 1-bishop1_diagonal */
	public static int[][] C_E_b1d = {{0,0},{1,1}};
	/*** 1-bishop1_cross-diagonal */
	public static int[][] C_E_b1x = {{0,0},{1,-1}};
	
	/*** 2-rook2_vertical */
	public static int[][] C_E_r2v = {{0,0},{0,2}};
	/*** 2-rook2_horizontal */
	public static int[][] C_E_r2h = {{0,0},{2,0}};
	/*** 2-bishop2_diagonal */
	public static int[][] C_E_b2d = {{0,0},{2,2}};
	/*** 2-bishop2_cross-diagonal */
	public static int[][] C_E_b2x = {{0,0},{2,-2}};
	/*** 2-knight1_1+2 */
	public static int[][] C_E_k1a = {{0,0},{1,2}};
	/*** 2-knight1_2+1 */
	public static int[][] C_E_k1b = {{0,0},{2,1}};
	/*** 2-knight1_2-1 */
	public static int[][] C_E_k1c = {{0,0},{2,-1}};
	/*** 1-knight1_1-2 */
	public static int[][] C_E_k1d = {{0,0},{1,-2}};
	
	//===== PREMADE CLIQUE SETS =====

}
