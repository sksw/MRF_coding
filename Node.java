import java.util.ArrayList;
import java.util.Arrays;

public class Node extends CliqueStructures.Clique implements BeliefPropagation.BPNode<Integer>{

	public final int ID; //node id
	public final int VAL; //node value
	//public boolean isObs; //encoded? (used in arithmetic coding)
	public ArrayList<Edge> dN; //connecting edges
	public double[] Z; //beliefs
	public double sf[]; //scaling factor for border conditioning

	public Node(int num, int data, int range){
		super(range);
		nodes.add(this);
		ID = num;
		VAL = data;
		//isObs = false;
		dN = new ArrayList<Edge>(0);
		Z = new double[R];
		sf = new double[R]; Arrays.fill(sf,1.0);
		nodes.add(this);
	}
	
	//self-potential function
	public double npot(Integer v){ //relying on autoboxing!
		int[] node_values = new int[1];
		node_values[0] = v.intValue();
		return sf[v.intValue()]*pot.U(node_values);
	}
	
	//belief propagation
	public void Z(){
		Z(false);
	}
	public void Z(boolean fresh){
		for(int i=0; i<Z.length; i++){
			Z[i] = npot(i);
			for(int j=0; j<dN.size(); j++)
				if(ID == dN.get(j).n1.ID)
					Z[i] = Z[i] * dN.get(j).get_m21(i,j,fresh);
				else
					Z[i] = Z[i] * dN.get(j).get_m12(i,j,fresh);
		}
	}
	
	//find edge that links with a particular neighbour
	/*public Edge findLink(Node Neighbour){
		Edge e = null;
		for(int i=0; i<dN.size(); i++)
			if(dN.get(i).getOtherNode(this) == Neighbour)
				e = dN.get(i);
		return e;
	}*/
	
	//output to string
	public String toString(){
		return "("+ID+":"+dN.size()+")";
	}
}

