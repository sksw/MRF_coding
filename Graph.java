import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graph {
	
	public ArrayList<Node> V;
	public ArrayList<Edge> E;
	
	public Graph(){
		V = new ArrayList<Node>(0);
		E = new ArrayList<Edge>(0);
	}
	
	//check if graph cyclic - starts at node #inx, looks at neighbours to see if we've returned to previously visited node refInx, then check the neighbours of the neighbours recursively
	public static boolean checkCyclic(Node refInx, Node inx, Node pinx){
		//---- STOPPING CONDITION single node graph
		if(inx.dN.size() == 0)
			return false;
		//----- STOPPING CONDITION checks neighbours to see if we've returned to previously visited node refInx
		for(int i=0; i<inx.dN.size(); i++)
			if(inx.dN.get(i).getOtherNode(inx) != pinx)
				if(inx.dN.get(i).getOtherNode(inx) == refInx)
					return true;
		//----- RECURSIVELY check the neighbours of neighbours
		for(int i=0; i<inx.dN.size(); i++)
			if(inx.dN.get(i).getOtherNode(inx) != pinx)
				if(checkCyclic(refInx,inx.dN.get(i).getOtherNode(inx),inx))
					return true;
		return false;
	}
		
	//brute force belief calculation
	public static double[] belief_BF(SuperNode A, ArrayList<Node> V, ArrayList<Edge> E){
		//----- get radix
		int r = V.get(0).R;
		double[] Z_A = new double[(int)Math.pow(r,A.V.size())];
		Arrays.fill(Z_A,0.0);
		double pot;
		//----- get node list of set A
		List<Integer> Nodes = new ArrayList<Integer>();
		for(int i=0; i<A.V.size(); i++)
			Nodes.add(A.V.get(i).ID);
		//----- for every possible configuration of set A
		String a, a_c, config;
		int a_inx, a_c_inx;
		for(int i=0; i<Z_A.length; i++){
			a = Utilities.toPString(i,A.V.size(),r);
			//----- marginalize over all configurations of A compliment 
			for(int j=0; j<(int)Math.pow(r,V.size()-A.V.size()); j++){
				a_c = Utilities.toPString(j,V.size()-A.V.size(),r);
				//----- construct a configuration over V
				config = "";
				pot = 1.0; a_inx=0; a_c_inx=0;
				for(int k=0; k<V.size(); k++){ //nodes
					if(Nodes.contains(new Integer(k))){
						config = config + a.substring(a_inx,a_inx+1);
						a_inx++;
					}
					else{
						config = config + a_c.substring(a_c_inx,a_c_inx+1);
						a_c_inx++;
					}
				}
				//----- calculate potential for given configuration
				for(int k=0; k<V.size(); k++) //nodes
					pot = pot*V.get(k).pot(Integer.parseInt(config.substring(k,k+1)));
				for(int k=0; k<E.size(); k++) //edges
					pot = pot*E.get(k).pot(
							Integer.parseInt(config.substring(V.indexOf(E.get(k).n1),V.indexOf(E.get(k).n1)+1)),
							Integer.parseInt(config.substring(V.indexOf(E.get(k).n2),V.indexOf(E.get(k).n2)+1)));
				//----- construct belief (sum)
				Z_A[i] = Z_A[i]+pot;
			}
		}
		return Z_A;
	}

}
