import java.util.ArrayList;

public class CliqueStructures{
	
	public static void main(String[] args){
		ArrayList<Node> N1 = new ArrayList<Node>(0);
		ArrayList<Node> N2 = new ArrayList<Node>(0);
		Node n1 = new Node(1,1,2);
		Node n2 = new Node(2,1,2);
		Node n3 = new Node(3,0,2);
		N1.add(n1); N1.add(n2); N1.add(n3); 
		N2.add(n3); N2.add(n2); N2.add(n1);
		System.out.println(N1.equals(N2));
		
		Clique c1 = new Clique(2); Clique c2 = new Clique(2);
		c1.nodes = N1;
		c2.nodes = N2;
		System.out.println(c1.equals(c2)+" and "+c2.equals(c1));
	}
	
	//----- CliquePair contains clique structure and clique definition
	//(some cliques have same clique structure in rotational or translational tranformation, but different clique potential function)
	public static class CliquePair{
		public int[][] geo;
		public pot_func.abstract_pot c_pot;
		public CliquePair(int[][] geometry, pot_func.abstract_pot clique_potential){
			geo = geometry;
			c_pot = clique_potential;
		}
	}
	
	//----- general clique (nodes and edges are special cases of cliques)
	public static class Clique{
		public ArrayList<Node> nodes;
		public final int R;
		public String type;
		public pot_func.abstract_pot pot; //potential function on the clique
		public Clique(int range){
			R = range;
			nodes = new ArrayList<Node>(0);
			type = null;
			pot = null;
		}
		public Clique(String clique_type, pot_func.abstract_pot clique_pot, ArrayList<Node> clique_nodes, int range){
			R = range;
			nodes = clique_nodes;
			type = clique_type;
			pot = clique_pot;
		}
		public boolean equals(Clique otherClique){
			if(type.equals(otherClique.type))
				if(nodes.size() == otherClique.nodes.size())
					return nodes.containsAll(otherClique.nodes);
				else
					return false;
			else
				return false;
		}
	}
	
}
