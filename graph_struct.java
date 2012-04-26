import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

class graph_struct{

	
	//===== NEIGHBOURHOOD STRUCTURES =====
	
	//----- types of neighbourhoods
	//N_1 --> 4pt, ver/hor
	//N_2 --> 4pt, diag/xdiag
	private static final int[][] N_1 = {{-1,0},{0,1},{1,0},{0,-1}};
	private static final int[][] N_2 = {{-1,-1},{-1,1},{1,-1},{1,1}};
	
	//===== PREMADE NEIGHBOURHOOD SETS =====
	
	//----- ising 4pt
	public static final Map<String, int[][]> ISING_4PT = i4();
	private static Map<String, int[][]> i4() {
		Map<String,int[][]> N_LIB = new HashMap<String, int[][]> ();
		N_LIB.put("N_1",N_1); 
	    return Collections.unmodifiableMap(N_LIB);
	}
	
	//----- ising 8pt
	public static final Map<String, int[][]> ISING_8PT = i8();
	private static Map<String, int[][]> i8() {
		Map<String,int[][]> N_LIB = new HashMap<String, int[][]> ();
		N_LIB.put("N_1",N_1); 
		N_LIB.put("N_2",N_2); 
	    return Collections.unmodifiableMap(N_LIB);
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
	/*** 1-vertical */
	public static int[][] C_E1 = {{0,0},{0,1}};
	/*** 1-horizontal */
	public static int[][] C_E2 = {{0,0},{1,0}};
	/*** 1-diagonal */
	public static int[][] C_E3 = {{0,0},{1,1}};
	/*** 1-cross-diagonal */
	public static int[][] C_E4 = {{0,0},{1,-1}};
	
	//===== PREMADE CLIQUE SETS =====

}
