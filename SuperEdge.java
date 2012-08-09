import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class SuperEdge implements BeliefPropagation.BPEdge<String>{
	
	public SuperNode N1; //supernode 1
	public SuperNode N2; //supernode 2
	public int r; //range of values for 1 node
	public ArrayList<CliqueStructures.Clique> C; //internal edge list (edges between supernodes)
	public ArrayList<CliqueStructures.Clique> conditionCliques; //cliques that may cross into 3 different spernodes due to conditioning
	public ArrayList<Node> N1Nodes; //To keep track of "relevant" nodes in N1
	public ArrayList<Node> N2Nodes;
	public double[] m12; //message from supernode 1 --> 2
	public double[] m21; //message from supernode 2 --> 1
	public boolean m12d; //message already computed?
	public boolean m21d;
	
	public SuperEdge (SuperNode P1, SuperNode P2, ArrayList<CliqueStructures.Clique> c, int range){
		N1 = P1;
		N2 = P2;
		r = range;
		C = new ArrayList<CliqueStructures.Clique>(0);
		conditionCliques = new ArrayList<CliqueStructures.Clique>(0);
		N1Nodes = new ArrayList<Node>(0);
		N2Nodes = new ArrayList<Node>(0);
		findCrossCliques(c);
		m12 = new double[(int)Math.pow(r,N2Nodes.size())];
		m21 = new double[(int)Math.pow(r,N1Nodes.size())];
		m12d = false;
		m21d = false;
		attach();
	}
	
	//find cliques that span across the two supernodes
	public void findCrossCliques(ArrayList<CliqueStructures.Clique> c){
		ArrayList<CliqueStructures.Clique> c_copy = (ArrayList<CliqueStructures.Clique>)c.clone();
		ArrayList<Node> stack1 = new ArrayList<Node>(0), stack2 = new ArrayList<Node>(0);
		for(CliqueStructures.Clique clique : c_copy) //clique must have nodes in both supernodes
			if( !N1.C.contains(clique) && !N2.C.contains(clique) ){
				if(crossClique(stack1,stack2,clique)){
					C.add(clique);
					c.remove(clique); //this is to cut down on search time =(
					N1Nodes.addAll(stack1);
					N2Nodes.addAll(stack2);
				}
				stack1.clear();
				stack2.clear();
			}
	}
	
	private boolean crossClique(List<Node> s1, List<Node> s2, CliqueStructures.Clique clique){
		boolean n1flag = false, n2flag = false, contained = true;
		for(Node node : clique.nodes)
			if(N1.V.contains(node)){
				n1flag = true;
				if(!N1Nodes.contains(node))
					s1.add(node);
			}
			else if(N2.V.contains(node)){
				n2flag = true;
				if(!N2Nodes.contains(node))
					s2.add(node);
			}
			else
				contained = false; //this is to make cross-clique requirements stringent against cutset conditioning where there might be a node not in N1 or N2, but in L
		if(n1flag & n2flag & !contained)
			conditionCliques.add(clique);
		return n1flag & n2flag & contained;
	}
	
	public void condition(SuperNode cutset){
		for(CliqueStructures.Clique clique : conditionCliques)
			for(Node node : clique.nodes)
				if(cutset.V.contains(node)){
					C.add(clique);
					break;
				}
	}
	
	//edge-potential function
	public double epot(String V1, String V2){
		double pot = 1.0;
		//multiply all clique potentials
		for(CliqueStructures.Clique clique : C){
			int [] cliqueVals = new int[clique.nodes.size()];
			int inx;
			for(int i=0; i<clique.nodes.size(); i++){
				inx = N1Nodes.indexOf(clique.nodes.get(i));
				if(inx!=-1)
					cliqueVals[i] = Integer.parseInt(V1.substring(inx,inx+1),r);
				else{
					inx = N2Nodes.indexOf(clique.nodes.get(i));
					if(inx!=-1)
						cliqueVals[i] = Integer.parseInt(V2.substring(inx,inx+1),r);
					else{ //this is the case where the node is a conditioned node from the cutset that has already been observed
						cliqueVals[i] = clique.nodes.get(i).VAL;
						System.out.println("MI YAAAA HEEEEEEEE");
					}
				}
			}
			/*
			System.out.print(clique.type+" "+clique+": ");
			for(int i=0; i<cliqueVals.length; i++)
				System.out.print(cliqueVals[i]+" ");
			System.out.println(clique.pot.U(cliqueVals));
			*/
			pot = pot * clique.pot.U(cliqueVals);
		}
		//System.out.println(pot);
		return pot;
	}

	//message from node 1 to node 2
	public double get_m12(String N2_VAL, int ref, boolean fresh){
		String X1,X2, N1_VAL;
		//----- compute message if not already done or if want new calculation
		if(!m12d || fresh){
			double temp;
			Arrays.fill(m12,0.0);
			for(int i=0; i<m12.length; i++){ //for each configuration of edge ends in supernode 2
				X2 = Utilities.toPString(i,N2Nodes.size(),r);
				//----- N1 already observed (encoded) --> only need to condition on observed values of N1
				if(N1.observed)
					m12[i] = m12[i] + epot(N1_edgeEndVector(N1.obsVal),X2);
				else
					for(int j=0; j<(int)Math.pow(r,N1.V.size()); j++){ //sum over configurations of supernode 1 - must use "false" when calculating npot or we will skew calculations
						N1_VAL = Utilities.toPString(j,N1.V.size(),r);
						X1 = N1_edgeEndVector(N1_VAL);
						//----- leaf node stopping condition (supernode 1 is a leaf node)
						if(N1.dN.size()==1)
							m12[i] = m12[i] + N1.npot(N1_VAL) * epot(X1,X2);
							//System.out.println("\t("+N1_VAL+")"+N1.npot(N1_VAL,false)+": "+"("+X1+","+X2+")"+epot(X1,X2));
						//----- not stopping, must recursively find more messages
						else{
							temp = 1.0;
							for(int k=0; k<N1.dN.size(); k++) //product over messages from neighbours of supernode 1 excluding supernode 2 
								if(N1.dN.get(k)!=N2.dN.get(ref))
									if(N1.dN.get(k).N1.id!=N1.id)
										temp = temp * N1.dN.get(k).get_m12(N1_VAL,k,fresh);
									else
										temp = temp * N1.dN.get(k).get_m21(N1_VAL,k,fresh);
							m12[i] = m12[i] + epot(X1,X2) * N1.npot(N1_VAL) * temp;
						}
					}
			}
			//----- normalization
			if(!N1.observed)
				Utilities.normalize(m12);
			//----- set calculated flag to true
			m12d = true;
			//for(int i=0; i<m12.length; i++)
			//	System.out.println("1-->2 "+m12[i]);
		}
		X2 = N2_edgeEndVector(N2_VAL);
		//System.out.println(N1.id+"-->"+N2.id+" ("+N2_VAL+") "+(!m12d || fresh));
		//System.out.println(X2+": "+m12[Integer.parseInt(X2,r)]);
		return m12[Integer.parseInt(X2,r)];
	}
	
	//message from node 2 to node 1
	public double get_m21(String N1_VAL, int ref, boolean fresh){
		String X1,X2, N2_VAL;
		//----- compute message if not already done or if want new calculation
		/*System.out.println("HELLO! "+m21d+" "+N1.id+"("+N1.dN.size()+")"+","+N2.id+"("+N2.dN.size()+")");
		for(int u=0; u<N2Nodes.size(); u++)
			System.out.print(N2Nodes.get(u)+" ");
		System.out.println("\n--------------------------------------");
		for(int u=0; u<N1Nodes.size(); u++)
			System.out.print(N1Nodes.get(u)+" ");
		for(int u=0; u<C.size(); u++)
			System.out.println(C.get(u));
		System.out.println();*/
		if(!m21d || fresh){
			double temp;
			Arrays.fill(m21,0.0);
			for(int i=0; i<m21.length; i++){ //for each configuration of edge ends in supernode 1
				X1 = Utilities.toPString(i,N1Nodes.size(),r);
				//----- N2 already observed (encoded) --> only need to condition on observed values of N2
				if(N2.observed){
					//System.out.println("HERE AT CONDTIONING! "+X1);
					m21[i] = m21[i] + epot(X1,N2_edgeEndVector(N2.obsVal));
					//System.out.println(N2_edgeEndVector(N2.obsVal));
				}
				else
					for(int j=0; j<(int)Math.pow(r,N2.V.size()); j++){ //sum over configurations of supernode 2 - must use "false" when calculating npot or we will skew calculations
						N2_VAL = Utilities.toPString(j,N2.V.size(),r);
						X2 = N2_edgeEndVector(N2_VAL);
						//----- leaf node stopping condition (supernode 2 is a leaf node)
						if(N2.dN.size()==1)
							m21[i] = m21[i] + N2.npot(N2_VAL) * epot(X1,X2);
							//System.out.println("\t("+N2_VAL+")"+N2.npot(N2_VAL,false)+": "+"("+X1+","+X2+")"+epot(X1,X2));
						//----- not stopping, must recursively find more messages
						else{
							temp = 1.0;
							for(int k=0; k<N2.dN.size(); k++) //product over messages from neighbours of supernode 2 excluding supernode 1 
								if(N2.dN.get(k)!=N1.dN.get(ref))
									if(N2.dN.get(k).N1.id!=N2.id)
										temp = temp * N2.dN.get(k).get_m12(N2_VAL,k,fresh);
									else
										temp = temp * N2.dN.get(k).get_m21(N2_VAL,k,fresh);
							m21[i] = m21[i] + epot(X1,X2) * N2.npot(N2_VAL) * temp;
						}
					}
			}
			//----- normalization
			if(!N2.observed)
				Utilities.normalize(m21);
			//----- set calculated flag to true
			m21d = true;
			//for(int i=0; i<m21.length; i++)
			//	System.out.println("2-->1 "+m21[i]);
		}
		X1 = N1_edgeEndVector(N1_VAL);
		//System.out.println(N2.id+"-->"+N1.id+" ("+N1_VAL+") "+(!m21d || fresh));
		//System.out.println(X1+": "+m21[Integer.parseInt(X1,r)]);
		/*
		for(int i=0; i<m21.length; i++)
			if(i==Integer.parseInt(X1,r))
				System.out.println(Utilities.toPString(i,N1Nodes.size(),r)+": "+m21[i]+"<--");
			else
				System.out.println(Utilities.toPString(i,N1Nodes.size(),r)+": "+m21[i]);
		System.exit(0);
		*/
		return m21[Integer.parseInt(X1,r)];
	}
	
	//values at supernode 2 may not be all needed, check for required values only
	public String N2_edgeEndVector(String N2_VAL){
		String X2 = "";
		int inx;
		for(Node node : N2Nodes){
			inx = N2.V.indexOf(node);
			X2 = X2 + N2_VAL.substring(inx,inx+1);
		}
		return X2;
	}
	//values at supernode 1 may not be all needed, check for required values only
	public String N1_edgeEndVector(String N1_VAL){
		String X1 = "";
		int inx;
		for(Node node : N1Nodes){
			inx = N1.V.indexOf(node);
			X1 = X1 + N1_VAL.substring(inx,inx+1);
		}
		return X1;
	}
	
	//adds this edge to neighbourhood lists of its ends (i.e. attach to nodes)
	public void attach(){
		N1.dN.add(this); N2.dN.add(this);
	}
	//removes this edge from neighbourhood lists of its ends (i.e. detach from nodes)
	public void detach(){
		N1.dN.remove(this); N2.dN.remove(this);
	}
	
	//returns id of other node
	public int getOtherNode(int ref){
		if(ref==N1.id) return N2.id;
		else return N1.id;
	}
	//returns reference to other node
	public SuperNode getOtherNode(SuperNode ref){
		if(ref==N1) return N2;
		else return N1;
	}
	
	//output to string
	public String toString(){
		String Info = "<SUPEREDGE "+N1.id+"-"+N2.id+": ";
		for(int i=0;i<C.size();i++)
			if(i!=C.size()-1)
				Info = Info + C.get(i).toString()+",";
			else
				Info = Info + C.get(i).toString()+">";
		return Info;
	}
	
}
