import java.util.ArrayList;

public class SuperNode {

	public int id; //node id
	public int r; //range of values for 1 node
	public String ObsVal; //observed values for some nodes (MUST BE IN ORDER, first few nodes always observed first)
	public ArrayList<Node> V; //nodes in supernode
	public ArrayList<Edge> E; //edges in supernode
	public ArrayList<SuperEdge> dN; //connecting superedges
	public double[] Z; //belief vector for supernode (based on larger alphabet size using x-ary enumeration) --> length of Z needs to ALWAYS be r^(# of unobserved items in V)
	public ArrayList<Object> newItems; //keep track of new "RELEVANT" items added to supernode (solves precision overflow)
	
	public SuperNode(int num, int range){
		id = num;
		r = range;
		V = new ArrayList<Node>(0);
		E = new ArrayList<Edge>(0);
		dN = new ArrayList<SuperEdge>(0);
		newItems = new ArrayList<Object>(0);
		Z = null;
		//Z = new double[(int)Math.pow(range,V.size())];
		ObsVal = "";
	}
	
	//find edges internal to the supernode from list
	public void findInternalEdges(ArrayList<Edge> e){
		for(int i=0; i<e.size(); i++)
			if(V.contains(e.get(i).n1) && V.contains(e.get(i).n2)) //must contain both ends of edge
				E.add(e.get(i));
	}
	
	//construct string of observed values from internal nodes
	public void obsVals(){
		ObsVal = "";
		for(int i=0; i<V.size(); i++)
			ObsVal = ObsVal+V.get(i).VAL;
		resetZ();
	}
	
	//restructure belief vector size based on what nodes have not been observed (encoded) yet in supernode
	public void resetZ(){
		Z = new double[(int)Math.pow(r,V.size()-ObsVal.length())];
	}

	//supernode self-potential function
	public double npot(String Val){
		return npot(Val,false);
	}
	//newOnly - if there are old observed (encoded) items in the supernode, we only need to look at potentials of new items (since all the old ones simply form a scaling factor)
	public double npot(String Val, boolean newOnly){
		double pot = 1.0;
		int n_inx, n1_inx, n2_inx; Edge e;
		//----- value of nodes by matching node index in list and val substring, hence order matters (i.e. string char index matches order of nodes in V)		
		if(newOnly)
			for(int i=0; i<newItems.size(); i++){
				if(newItems.get(i) instanceof Node){
					n_inx = V.indexOf(newItems.get(i));
					pot = pot * V.get(n_inx).npot( Integer.parseInt(Val.substring(n_inx,n_inx+1),r) );
				}
				else{
					e = E.get(E.indexOf(newItems.get(i)));
					n1_inx = V.indexOf(e.n1);
					n2_inx = V.indexOf(e.n2);
					pot = pot * e.epot( Integer.parseInt(Val.substring(n1_inx,n1_inx+1),r) , Integer.parseInt(Val.substring(n2_inx,n2_inx+1),r) );
				}
			}
		else
			for(int i=0; i<V.size(); i++) //multiply all self potential of individual nodes
				pot = pot*V.get(i).npot(Integer.parseInt(Val.substring(i,i+1),r));
			for(int i=0; i<E.size(); i++){ //multiply all edge potential of edges in super node
				n1_inx = V.indexOf(E.get(i).n1);
				n2_inx = V.indexOf(E.get(i).n2);
				pot = pot*E.get(i).epot( Integer.parseInt(Val.substring(n1_inx,n1_inx+1)) , Integer.parseInt(Val.substring(n2_inx,n2_inx+1)) );
			}
		return pot;
	}

	//belief propagation
	public void Z(){
		Z(false,false);
	}
	public void Z(boolean fresh, boolean newOnly){
		String NodeVal;
		for(int i=0; i<Z.length; i++){ //----- THIS IS WHY length of Z needs to ALWAYS be r^(# of unobserved items in V)
			NodeVal = ObsVal + Utilities.toPString(i,V.size()-ObsVal.length(),r);
			Z[i] = npot(NodeVal,newOnly);
			//System.out.println("> "+NodeVal+": "+Z[i]);
			for(int j=0; j<dN.size(); j++)
				if(id==dN.get(j).N1.id)//{
					Z[i] = Z[i]*dN.get(j).get_m21(NodeVal,j,fresh);// System.out.println("\t"+dN.get(j).N2.id+"("+dN.get(j).N1_edgeEndVector(NodeVal)+")"+": "+dN.get(j).get_m21(NodeVal,j,fresh));}
				else//{
					Z[i] = Z[i]*dN.get(j).get_m12(NodeVal,j,fresh);// System.out.println("\t"+dN.get(j).N1.id+"("+dN.get(j).N2_edgeEndVector(NodeVal)+")"+": "+dN.get(j).get_m12(NodeVal,j,fresh));}
		}
	}
	
	//output to string
	public String toString(){
		String Info = "<SUPERNODE#"+id+": ";
		for(int i=0;i<V.size();i++)
			if(i!=V.size()-1)
				Info = Info + V.get(i).toString()+",";
			else
				Info = Info + V.get(i).toString()+" | ";
		for(int i=0;i<E.size();i++)
			if(i!=E.size()-1)
				Info = Info + E.get(i).toString()+",";
			else
				Info = Info + E.get(i).toString()+">";
		return Info;
	}
	
}
