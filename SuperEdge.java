import java.util.ArrayList;
import java.util.Arrays;

public class SuperEdge {
	
	public SuperNode N1; //supernode 1
	public SuperNode N2; //supernode 2
	public int r; //range of values for 1 node
	public ArrayList<Edge> E; //internal edge list (edges between supernodes)
	public double[] m12; //message from supernode 1 --> 2
	public double[] m21; //message from supernode 2 --> 1
	public boolean m12d; //message already computed?
	public boolean m21d;
	
	public SuperEdge (SuperNode P1, SuperNode P2, ArrayList<Edge> e, int range){
		N1 = P1;
		N2 = P2;
		r = range;
		E = new ArrayList<Edge>(0);
		findInternalEdges(e);
		m12 = new double[(int)Math.pow(r,E.size())];
		m21 = new double[(int)Math.pow(r,E.size())];
		m12d = false;
		m21d = false;
		attach();
	}
	
	//find edges internal to the supernode from list
	public void findInternalEdges(ArrayList<Edge> e){
		for(int i=0; i<e.size(); i++)
			//each end need to be in respective nodes
			if( (N1.V.contains(e.get(i).n1) && N2.V.contains(e.get(i).n2)) || 
					(N1.V.contains(e.get(i).n2) && N2.V.contains(e.get(i).n1)) )
				E.add(e.get(i));
	}
	
	//edge-potential function
	public double epot(String V1, String V2){
		double pot = 1.0;
		//multiply all edge potentials
		for(int i=0; i<E.size(); i++)
			pot = pot * E.get(i).pot( Integer.parseInt(V1.substring(i,i+1),r) , Integer.parseInt(V2.substring(i,i+1),r) );
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
				X2 = Utilities.toPString(i,E.size(),r);
				for(int j=0; j<(int)Math.pow(r,N1.V.size()); j++){ //sum over configurations of supernode 1 - must use "false" when calculating npot or we will skew calculations
					N1_VAL = Utilities.toPString(j,N1.V.size(),r);
					X1 = N1_edgeEndVector(N1_VAL);
					//----- leaf node stopping condition (supernode 1 is a leaf node)
					if(N1.dN.size()==1)
						m12[i] = m12[i] + N1.npot(N1_VAL,false) * epot(X1,X2);
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
						m12[i] = m12[i] + epot(X1,X2) * N1.npot(N1_VAL,false) * temp;
					}
				}
			}
			//----- normalization
			Utilities.normalize(m12);
			//----- set calculated flag to true
			m12d = true;
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
		if(!m21d || fresh){
			double temp;
			Arrays.fill(m21,0.0);
			for(int i=0; i<m21.length; i++){ //for each configuration of edge ends in supernode 1
				X1 = Utilities.toPString(i,E.size(),r);
				for(int j=0; j<(int)Math.pow(r,N2.V.size()); j++){ //sum over configurations of supernode 2 - must use "false" when calculating npot or we will skew calculations
					N2_VAL = Utilities.toPString(j,N2.V.size(),r);
					X2 = N2_edgeEndVector(N2_VAL);
					//----- leaf node stopping condition (supernode 2 is a leaf node)
					if(N2.dN.size()==1)
						m21[i] = m21[i] + N2.npot(N2_VAL,false) * epot(X1,X2);
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
						m21[i] = m21[i] + epot(X1,X2) * N2.npot(N2_VAL,false) * temp;
					}
				}
			}
			//----- normalization
			Utilities.normalize(m21);
			//----- set calculated flag to true
			m21d = true;
		}
		X1 = N1_edgeEndVector(N1_VAL);
		//System.out.println(N2.id+"-->"+N1.id+" ("+N1_VAL+") "+(!m21d || fresh));
		//System.out.println(X1+": "+m21[Integer.parseInt(X1,r)]);
		return m21[Integer.parseInt(X1,r)];
	}
	
	//values at supernode 2 may not be all needed, check for required values only
	public String N2_edgeEndVector(String N2_VAL){
		String X2 = "";
		for(int i=0; i<E.size(); i++)
			//----- node 1 of edge is in supernode 2
			if(N2.V.contains(E.get(i).n1)) 
				X2 = X2 + N2_VAL.substring(N2.V.indexOf(E.get(i).n1),N2.V.indexOf(E.get(i).n1)+1);
			//----- node 2 of edge is in supernode 2
			else
				X2 = X2 + N2_VAL.substring(N2.V.indexOf(E.get(i).n2),N2.V.indexOf(E.get(i).n2)+1);
		return X2;
	}
	//values at supernode 1 may not be all needed, check for required values only
	public String N1_edgeEndVector(String N1_VAL){
		String X1 = "";
		for(int i=0; i<E.size(); i++)
			//----- node 1 of edge is in supernode 1
			if(N1.V.contains(E.get(i).n1))
				X1 = X1 + N1_VAL.substring(N1.V.indexOf(E.get(i).n1),N1.V.indexOf(E.get(i).n1)+1);
			else //node 2 of edge is in supernode 1
				X1 = X1 + N1_VAL.substring(N1.V.indexOf(E.get(i).n2),N1.V.indexOf(E.get(i).n2)+1);
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
		for(int i=0;i<E.size();i++)
			if(i!=E.size()-1)
				Info = Info + E.get(i).toString()+",";
			else
				Info = Info + E.get(i).toString()+">";
		return Info;
	}
	
}
