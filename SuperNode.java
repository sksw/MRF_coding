import java.util.ArrayList;


public class SuperNode {

	public int id;
	
	public ArrayList<Node> V; //nodes in supernode
	/*
	ORDER OF NODE LIST
	1. nodes from top to bottom
	2. columns from left to right
	 */
	public ArrayList<Edge> E; //edges in supernode
	/*
	ORDER OF EDGE LIST
	edge order not needed
	 */
	public ArrayList<SuperEdge> dN; //superedges linking to neighbours
	
	public ArrayList<Object> newItems; //keep track of new "RELEVANT" items absorbed to proceed in calculating next belief phase, this should only be used on markov sources
	
	public double[] Z; //belief vector for supernode (based on larger alphabet size using x-ary enumeration)
	
	public String ObsVal; //observed values for some nodes (MUST BE IN ORDER, first few nodes always observed first)
	
	public SuperNode(int num, ArrayList<Node> Nodes, int range){
		id = num;
		
		V = Nodes; //should be in sequence from top to bottom
		E = new ArrayList<Edge>(0);
		dN = new ArrayList<SuperEdge>(0);
		newItems = new ArrayList<Object>(0);
	
		Z = new double[(int)Math.pow(range,V.size())]; //let the beliefs be enumerated by node x-ary count
		
		ObsVal = "";
	}
	
	public void findInternalEdges(ArrayList<Edge> e){
		for(int i=0; i<e.size(); i++)
			if(V.contains(e.get(i).n1) && V.contains(e.get(i).n2))
				E.add(e.get(i));
	}
	
	public String observeValues(){
		ObsVal = "";
		for(int i=0; i<V.size(); i++)
			ObsVal = ObsVal+V.get(i).N_VAL;
		return ObsVal;
	}
	
	public void resetBeliefVectorSize(int range){
		Z = new double[(int)Math.pow(range,V.size()-ObsVal.length())];
	}

	/*	
	//OLD NPOT BEFORE MARKOV
	public double npot(String Val){
		double pot = 1.0;
		//multiply all self potential of individual nodes
		for(int i=0; i<V.size(); i++)
			pot = pot*V.get(i).npot(Integer.parseInt(Val.substring(i,i+1)));
		//multiply all edge potential of edges in super node
		for(int i=0; i<E.size(); i++){
			pot = pot*E.get(i).epot(
					Integer.parseInt(Val.substring(V.indexOf(E.get(i).n1),V.indexOf(E.get(i).n1)+1)),
					Integer.parseInt(Val.substring(V.indexOf(E.get(i).n2),V.indexOf(E.get(i).n2)+1)));
			// original
			//pot = pot*E.get(i).epot(Integer.parseInt(Val.substring(i,i+1)),Integer.parseInt(Val.substring(i+1,i+2)));
		}
		return pot;
	}*/
	
	public double npot(String Val, boolean markov){
		double pot = 1.0;
		
		if(markov){
			//System.out.println("new items size: "+newItems.size());
			for(int i=0; i<newItems.size(); i++){
				if(newItems.get(i) instanceof Node)
					pot = pot*V.get(V.indexOf(newItems.get(i))).npot(
							Integer.parseInt(
								Val.substring(V.indexOf(newItems.get(i)),V.indexOf(newItems.get(i))+1)));
				else
					pot = pot*E.get(E.indexOf(newItems.get(i))).epot(
							Integer.parseInt(Val.substring(V.indexOf(E.get(E.indexOf(newItems.get(i))).n1),
									V.indexOf(E.get(E.indexOf(newItems.get(i))).n1)+1)),
							Integer.parseInt(Val.substring(V.indexOf(E.get(E.indexOf(newItems.get(i))).n2),
									V.indexOf(E.get(E.indexOf(newItems.get(i))).n2)+1)));
			}
		}
		else{
			//multiply all self potential of individual nodes
			for(int i=0; i<V.size(); i++)
				pot = pot*V.get(i).npot(Integer.parseInt(Val.substring(i,i+1)));
			//multiply all edge potential of edges in super node
			for(int i=0; i<E.size(); i++)
				pot = pot*E.get(i).epot(
						Integer.parseInt(Val.substring(V.indexOf(E.get(i).n1),V.indexOf(E.get(i).n1)+1)),
						Integer.parseInt(Val.substring(V.indexOf(E.get(i).n2),V.indexOf(E.get(i).n2)+1)));
		}
		return pot;
	}
	
	/*
	
	...
	
	 */

	public void calcZ(boolean fresh, boolean markov){
		for(int i=0; i<Z.length; i++){
			Z[i] = npot(ObsVal+Utilities.intToPBS(i,V.size()-ObsVal.length()),markov);
			//System.out.println(Z[i]);
			for(int j=0; j<dN.size(); j++){
				if(id==dN.get(j).N1.id)
					Z[i] = Z[i]*dN.get(j).get_m21(ObsVal+Utilities.intToPBS(i,V.size()-ObsVal.length()),j,fresh);
				else
					Z[i] = Z[i]*dN.get(j).get_m12(ObsVal+Utilities.intToPBS(i,V.size()-ObsVal.length()),j,fresh);
			}
		}
	}
	
	public String toString(){
		String Info = "<SUPERNODE#"+id+": ";
		for(int i=0;i<V.size();i++)
			if(i!=V.size()-1)
				Info = Info + V.get(i).toString()+",";
			else
				Info = Info + V.get(i).toString()+"|";
		for(int i=0;i<E.size();i++)
			if(i!=E.size()-1)
				Info = Info + E.get(i).toString()+",";
			else
				Info = Info + E.get(i).toString()+">";
		return Info;
	}
	
}
