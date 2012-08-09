import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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
		System.out.println(c1.hashCode()+" and "+c2.hashCode());
	}
	
	//----- CliquePair contains clique structure and clique definition
	//(some cliques have same clique structure in rotational or translational tranformation, but different clique potential function)
	public static class CliquePair{
		public int[][] geo;
		public ArrayList<int[]> offsets;
		public pot_func.abstract_pot c_pot;
		public CliquePair(int[][] geometry, Map<String,int[][]> n_struct, pot_func.abstract_pot clique_potential){
			geo = geometry;
			offsets = new ArrayList<int[]>(0);
			findOffsets();
			c_pot = clique_potential;
		}
		
		public void findOffsets(){ //there may be more than 1 clique of a type within a given neighbourhood
			for(int i=0; i<geo.length; i++){
				int[] offset = new int[2];
				for(int j=0; j<geo[i].length; j++)
					offset[j] = -geo[i][j];
				offsets.add(offset);
			}
			/* this is the old offset method, it's bad because it tracks a lot of redundant cliques
			for(int[][] neighbourhood : n_struct.values())
				for(int i=0; i<neighbourhood.length; i++){
					boolean flag = true;
					int[] offset = new int[2]; offset[0] = neighbourhood[i][0]; offset[1] = neighbourhood[i][1];
					for(int j=0; j<geo.length; j++){
						int[] coord = new int[2];
						coord[0] = offset[0]+geo[j][0];
						coord[1] = offset[1]+geo[j][1];
						flag = flag & searchN(coord,n_struct);
					}
					if(flag) //valid clique offset
						offsets.add(offset);
				}
			*/
		
		}
		
		/* this was used in the old redundant offset search
		public boolean searchN(int[] coord, Map<String,int[][]> n_struct){
			for(int[][] neighbourhood : n_struct.values())
				for(int i=0; i<neighbourhood.length; i++)
					if(coord[0]==neighbourhood[i][0] && coord[1]==neighbourhood[i][1])
						return true;
			return false;
		}
		*/
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
			type = "";
			pot = null;
		}
		public Clique(String clique_type, pot_func.abstract_pot clique_pot, ArrayList<Node> clique_nodes, int range){
			R = range;
			nodes = clique_nodes;
			type = clique_type;
			pot = clique_pot;
		}
		public int[] getNodeIDs(){
			int[] IDs = new int[nodes.size()];
			for(int i=0; i<nodes.size(); i++)
				IDs[i] = nodes.get(i).ID;
			Arrays.sort(IDs);
			return IDs;
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
		public int hashCode(){
			int hash = 1;
			hash = hash * 11 + type.hashCode();
			hash = hash * 13 + nodes.size();
			int[] IDs = getNodeIDs();
			for(int i=0; i<IDs.length; i++)
				hash = hash * 17 + IDs[i];
			return hash;
		}
		public String toString(){
			String info = "(";
			for(int i=0; i<nodes.size(); i++)
				if(i!=nodes.size()-1)
					info = info + nodes.get(i).ID + ",";
				else
					info = info + nodes.get(i).ID;
			info = info+")";
			return info;
		}
	}
	
}
