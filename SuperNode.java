import java.util.ArrayList;

public class SuperNode implements BeliefPropagation.BPNode<String>{

	public int id; //node id
	public int r; //range of values for 1 node
	public boolean observed; //has node value been observed?  i.e. encoded?
	public String obsVal;
	public ArrayList<Node> V; //nodes in supernode
	public ArrayList<CliqueStructures.Clique> C; //cliques in supernode
	public ArrayList<SuperEdge> dN; //connecting superedges
	public double[] Z; //belief vector for supernode (based on larger alphabet size using x-ary enumeration) --> length of Z needs to ALWAYS be r^(# of unobserved items in V)
	
	public SuperNode(int num, int range){
		id = num;
		r = range;
		V = new ArrayList<Node>(0);
		C = new ArrayList<CliqueStructures.Clique>(0);
		dN = new ArrayList<SuperEdge>(0);
		Z = null;
		observed = false;
		obsVal = "";
	}
	
	//find edges internal to the supernode from list
	public void findInternalCliques(ArrayList<CliqueStructures.Clique> c){
		ArrayList<CliqueStructures.Clique> c_copy = (ArrayList<CliqueStructures.Clique>)c.clone();
		for(CliqueStructures.Clique clique : c_copy)
			if(V.containsAll(clique.nodes)){
				C.add(clique);
				c.remove(clique); //this is to cut down on search time =(
			}
	}
	
	//construct string of observed values from internal nodes
	public String obs(){
		observed = true;
		obsVal = "";
		for(int i=0; i<V.size(); i++)
			obsVal = obsVal+V.get(i).VAL;
		return obsVal;
	}
	
	//supernode self potential
	public double npot(String Val){
		double pot = 1.0;
		//----- value of nodes by matching node index in list and val substring, hence order matters (i.e. string char index matches order of nodes in V)		
		for(CliqueStructures.Clique clique : C){
			int [] cliqueVals = new int[clique.nodes.size()];
			int inx;
			for(int j=0; j<clique.nodes.size(); j++){
				inx = V.indexOf(clique.nodes.get(j));
				cliqueVals[j] = Integer.parseInt(Val.substring(inx,inx+1),r);
			}
			pot = pot * clique.pot.U(cliqueVals);
		}
		return pot;
	}
	
	/*
	//newOnly - if there are old observed (encoded) items in the supernode, we only need to look at potentials of new items (since all the old ones simply form a scaling factor)
	public double npot(String Val, boolean newOnly){
		double pot = 1.0;
		//----- value of nodes by matching node index in list and val substring, hence order matters (i.e. string char index matches order of nodes in V)		
		if(newOnly)
			for(CliqueStructures.Clique clique : newItems){
				int [] cliqueVals = new int[clique.nodes.size()];
				int inx;
				for(int j=0; j<clique.nodes.size(); j++){
					inx = V.indexOf(clique.nodes.get(j));
					cliqueVals[j] = Integer.parseInt(Val.substring(inx,inx+1),r);
				}
				pot = pot * clique.pot.U(cliqueVals);
			}
		else
			for(CliqueStructures.Clique clique : C){
				int [] cliqueVals = new int[clique.nodes.size()];
				int inx;
				for(int j=0; j<clique.nodes.size(); j++){
					inx = V.indexOf(clique.nodes.get(j));
					cliqueVals[j] = Integer.parseInt(Val.substring(inx,inx+1),r);
				}
				pot = pot * clique.pot.U(cliqueVals);
			}
		return pot;
	}
	*/

	//belief propagation
	public double[] Z(){
		return Z(false);
	}
	public double[] Z(boolean fresh){
		Z = new double[(int)Math.pow(r,V.size())];
		String NodeVal;
		for(int i=0; i<Z.length; i++){ //----- THIS IS WHY length of Z needs to ALWAYS be r^(# of unobserved items in V)
			NodeVal = Utilities.toPString(i,V.size(),r);
			Z[i] = npot(NodeVal);
			//System.out.println("> NODEVALUE: "+NodeVal);
			for(int j=0; j<dN.size(); j++){
				/*
				// DEBUG OUTPUT SEQUENCE
				System.out.println("> "+dN.get(j).getOtherNode(this).id+": "+dN.get(j).getOtherNode(this).observed+" links: "+dN.get(j).C.size());
				for(int k=0; k<dN.get(j).C.size(); k++){
					System.out.print("\tlink "+k+" "+dN.get(j).C.get(k).type+" "+dN.get(j).C.get(k).pot.THETA[0]+":");
					for(Node node : dN.get(j).C.get(k).nodes){
						if(!V.contains(node))
							System.out.print(" "+node.toString()+node.VAL);
						else
							System.out.print(" =>"+node.toString()+node.VAL);
					}
					System.out.println();
				}
				System.exit(0);
				*/
				if(id==dN.get(j).N1.id){
					Z[i] = Z[i]*dN.get(j).get_m21(NodeVal,j,fresh);
					//System.out.println("\t"+dN.get(j).get_m21(NodeVal,j,fresh));
				}
				else{
					Z[i] = Z[i]*dN.get(j).get_m12(NodeVal,j,fresh);
					//System.out.println("\t"+dN.get(j).get_m12(NodeVal,j,fresh));
				}
			}
		}
		return Z;
	}
	
	//output to string
	public String toString(){
		String Info = "<SUPERNODE#"+id+": ";
		for(int i=0;i<V.size();i++)
			if(i!=V.size()-1)
				Info = Info + V.get(i).toString()+",";
			else
				Info = Info + V.get(i).toString()+" | ";
		for(int i=0;i<C.size();i++)
			if(i!=C.size()-1)
				Info = Info + C.get(i).toString()+",";
			else
				Info = Info + C.get(i).toString()+">";
		return Info;
	}
	
}
