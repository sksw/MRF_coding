import java.util.ArrayList;
import java.util.Arrays;

public class Node {

	public final int ID; //node id
	public final int VAL; //node value
	public final int R; //range of values
	public boolean obs; //encoded? (used in arithmetic coding)
	public ArrayList<Edge> dN; //connecting edges
	public pot_func.n_pot_func pot; //self-potential function object
	public double[] Z; //beliefs
	public double sf[]; //scaling factor for border conditioning

	public Node(int num, int data, int range){
		ID = num;
		VAL = data;
		R = range;
		obs = false;
		dN = new ArrayList<Edge>(0);
		pot = null;
		Z = new double[R];
		sf = new double[R]; Arrays.fill(sf,1.0);
	}
	
	//self-potential function
	public double pot(int v){
		double scaling_factor = sf[v];
		if(v==0)
			v=-1;
		return scaling_factor*pot.U(v);
	}
	
	//belief propagation
	public void Z(){
		Z(false);
	}
	public void Z(boolean fresh){
		for(int i=0; i<Z.length; i++){
			Z[i] = pot(i);
			for(int j=0; j<dN.size(); j++)
				if(ID == dN.get(j).n1.ID)
					Z[i] = Z[i] * dN.get(j).get_m21(i,j,fresh);
				else
					Z[i] = Z[i] * dN.get(j).get_m12(i,j,fresh);
		}
	}
	
	//find edge that links with a particular neighbour
	public Edge findLink(Node Neighbour){
		Edge e = null;
		for(int i=0; i<dN.size(); i++)
			if(dN.get(i).getOtherNode(this) == Neighbour)
				e = dN.get(i);
		return e;
	}
	
	//output to string
	public String toString(){
		return "("+ID+":"+dN.size()+")";
	}
}

