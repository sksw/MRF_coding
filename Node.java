import java.util.ArrayList;
import java.util.Arrays;

public class Node {

	public final int id; //node id
	public final int VAL; //node value
	public final int r; //range of values
	public boolean obs; //encoded? (used in arithmetic coding)
	public ArrayList<Edge> dN; //connecting edges
	public pot_func.n_pot_func pot; //self-potential function object
	public double[] Z; //beliefs
	public double sf[]; //scaling factor for border conditioning

	public Node(int num, int data, int range){
		id = num;
		VAL = data;
		r = range;
		obs = false;
		dN = new ArrayList<Edge>(0);
		pot = null;
		Z = new double[r];
		sf = new double[r]; Arrays.fill(sf,1.0);
	}
	
	//self-potential function
	public double npot(){
		return npot(VAL);
	}	
	public double npot(int v){
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
			Z[i] = npot(i);
			for(int j=0; j<dN.size(); j++)
				if(id == dN.get(j).n1.id)
					Z[i] = Z[i] * dN.get(j).get_m21(i,j,fresh);
				else
					Z[i] = Z[i] * dN.get(j).get_m12(i,j,fresh);
		}
	}
	
	//output to string
	public String toString(){
		return "("+id+":"+dN.size()+")";
	}
}

