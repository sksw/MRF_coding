import java.util.ArrayList;

public class Clique{
	
	//===== GENERAL CLIQUE FUNCTIONS =====
	
	//----- CliquePair contains clique structure and clique definition
	//(some cliques have same clique structure in rotational or translational geometry, but different clique definition i.e. function)
	public static class CliquePair{
		int[][] geo;
		AbstractClique c_def;
		public CliquePair(int[][] geometry, AbstractClique clique_def){
			geo = geometry;
			c_def = clique_def;
		}
	}
	
	//----- general clique (nodes and edges are special cases of cliques)
	public static abstract class AbstractClique{
		public ArrayList<Node> nodes;
		public final int R;
		public String type;
		public pot_func.abstract_pot pot;
		//----- potential function on the clique
		abstract public double pot(int[] node_values);
		//----- default constructor for clique
		public AbstractClique(String clique_type, int range){
			R = range;
			nodes = new ArrayList<Node>(0);
			type = clique_type;
			pot = null;
		}
	}
	
	//===== CLIQUE CLASS DEFINITIONS =====
	
	public static class NewClique extends AbstractClique{
		public NewClique(String clique_type, int range){
			super(clique_type,range);
		}
		public double pot(int[] node_values){
			return 1.0;
		}
	}
	
}
