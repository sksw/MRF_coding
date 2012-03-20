import java.util.ArrayList;

public class SuperEdge {
	
	public SuperNode N1; //node pointers
	public SuperNode N2;
	
	public ArrayList<Edge> E; //edges between supernodes
	
	public double[] m12; //messages
	public double[] m21;
	public boolean[] m12d; //message calculated?
	public boolean[] m21d;
	
	public SuperEdge (SuperNode P1, SuperNode P2, ArrayList<Edge> e, int range){
		N1 = P1;
		N2 = P2;
		E = new ArrayList<Edge>(0);
		for(int i=0; i<e.size(); i++)
			if( (N1.V.contains(e.get(i).n1) && N2.V.contains(e.get(i).n2)) || 
					(N1.V.contains(e.get(i).n2) && N2.V.contains(e.get(i).n1)) )
				E.add(e.get(i));
		m12 = new double[(int)Math.pow(range,E.size())];
		m21 = new double[(int)Math.pow(range,E.size())];
		m12d = new boolean[(int)Math.pow(range,E.size())];
		m21d = new boolean[(int)Math.pow(range,E.size())];
		N1.dN.add(this);
		N2.dN.add(this);
	}
	
	public double epot(String Val1, String Val2){
		//Strings Val1 and Val2 should be in order of values for supernodes N1 and N2 
		//size of the Strings should match size of edge list
		double pot = 1.0;
		//multiply all self potential of individual nodes
		for(int i=0; i<E.size(); i++){
			if(N1.V.contains(E.get(i).n1))
				pot = pot*E.get(i).epot(
						Integer.parseInt(Val1.substring(N1.V.indexOf(E.get(i).n1),N1.V.indexOf(E.get(i).n1)+1)),
						Integer.parseInt(Val2.substring(N2.V.indexOf(E.get(i).n2),N2.V.indexOf(E.get(i).n2)+1)));
			else
				//node n1 of the edge is in N2 actually (so to get the value of n1, we need to search in N2)
				//note that n1 is still in first term of epot function (just N1 is switched to N2), this is because epot function for edges preserves order
				pot = pot*E.get(i).epot(
						Integer.parseInt(Val2.substring(N2.V.indexOf(E.get(i).n1),N2.V.indexOf(E.get(i).n1)+1)),
						Integer.parseInt(Val1.substring(N1.V.indexOf(E.get(i).n2),N1.V.indexOf(E.get(i).n2)+1)));
			
			//original
			//pot = pot*E.get(i).epot(Integer.parseInt(Val1.substring(i,i+1)),Integer.parseInt(Val2.substring(i,i+1)));
		}
		return pot;
	}


	//BP WITH MESSAGE NORMALIZATION - BAD

	public double get_m12(String LX2, int ref, boolean fresh){
		//values at supernode 2 may not be all needed, check for required values only
		//IMPORTANT NOTE: for this to work, the LX2 string must be of same order as node list in supernode
		String X2 = "";
		for(int i=0; i<E.size(); i++){
			if(N2.V.contains(E.get(i).n1)) //node 1 of edge is in supernode 2
				X2 = X2 + LX2.substring(N2.V.indexOf(E.get(i).n1),N2.V.indexOf(E.get(i).n1)+1);
			else //node 2 of edge is in supernode 2
				X2 = X2 + LX2.substring(N2.V.indexOf(E.get(i).n2),N2.V.indexOf(E.get(i).n2)+1);
		}
		//System.out.println("m_"+n1.id+"-->"+n2.id);
		//calculate only if message has not already been computed
		if(!m12d[Integer.parseInt(X2,2)] || fresh){
			double temp;
			for(int i=0; i<m12.length; i++)
				m12[i] = 0.0;
			//leaf node stopping condition (node 1 is a leaf node)
			if(N1.dN.size()==1){
				//System.out.print(" LEAF ");
				//for each value of node 2
				for(int j=0; j<m12.length; j++)
					//sum over alphabet size (values of node 1)
					for(int i=0; i<m12.length; i++)
						m12[i] = m12[i] + N1.npot(Utilities.intToPBS(j,N1.V.size()),false)*epot(Utilities.intToPBS(j,N1.V.size()),Utilities.intToPBS(i,N2.V.size()));
			}
			else{
				//for each value of node 2
				for(int i=0; i<m12.length; i++){
					//sum over alphabet size (values of node 1)
					for(int j=0; j<m12.length; j++){
						temp = 1.0;
						//product over messages from neighbours of node 1 excluding node 2 
						for(int k=0; k<N1.dN.size(); k++){
							//ignore message node 2 by comparing if edge object reference the same
							if(N1.dN.get(k)!=N2.dN.get(ref))
								//find "child" node of node 1
								if(N1.dN.get(k).N1.id!=N1.id)
									temp = temp*N1.dN.get(k).get_m12(Utilities.intToPBS(j,N1.V.size()),k,fresh);
								else
									temp = temp*N1.dN.get(k).get_m21(Utilities.intToPBS(j,N1.V.size()),k,fresh);
						}
						m12[i] = m12[i] + epot(Utilities.intToPBS(j,N1.V.size()),Utilities.intToPBS(i,N2.V.size()))*N1.npot(Utilities.intToPBS(j,N1.V.size()),false)*temp;
					}
				}
			}
			//normalization
			double sum = 0.0;
			for(int i=0; i<m12.length; i++)
				sum = sum + m12[i];
			for(int i=0; i<m12.length; i++)
				m12[i] = m12[i]/sum;
			
			for(int i=0; i<m12d.length; i++)
				m12d[i] = true;
		}
		//System.out.println(m12[x2]);
		return m12[Integer.parseInt(X2,2)];
	}
	
	//message from node 2 to node 1
	public double get_m21(String LX1, int ref, boolean fresh){
		//values at supernode ` may not be all needed, check for required values only
		//IMPORTANT NOTE: for this to work, the LX2 string must be of same order as node list in supernode
		String X1 = "";
		for(int i=0; i<E.size(); i++){
			if(N1.V.contains(E.get(i).n1)) //node 1 of edge is in supernode 1
				X1 = X1 + LX1.substring(N1.V.indexOf(E.get(i).n1),N1.V.indexOf(E.get(i).n1)+1);
			else //node 2 of edge is in supernode 1
				X1 = X1 + LX1.substring(N1.V.indexOf(E.get(i).n2),N1.V.indexOf(E.get(i).n2)+1);
		}
		
		//System.out.println("m_"+n2.id+"-->"+n1.id);
		//calculate only if message has not already been computed
		if(!m21d[Integer.parseInt(X1,2)] || fresh){
			double temp;
			for(int i=0; i<m21.length; i++)
				m21[i] = 0.0;
			//leaf node stopping condition (node 2 is a leaf node)
			if(N2.dN.size()==1){
				//System.out.print(" LEAF ");
				//for each value of node 1
				for(int i=0; i<m21.length; i++)
					//sum over alphabet size (values of node 2)
					for(int j=0; j<m21.length; j++)
						m21[i] = m21[i] + N2.npot(Utilities.intToPBS(j,N2.V.size()),false)*epot(Utilities.intToPBS(i,N1.V.size()),Utilities.intToPBS(j,N2.V.size()));
			}
			else{
				//for each value of node 1
				for(int i=0; i<m21.length; i++){
					//sum over alphabet size (values of node 2)
					for(int j=0; j<m21.length; j++){
						temp = 1.0;
						//product over messages from neighbours of node 2 excluding node 1 
						for(int k=0; k<N2.dN.size(); k++){
							//ignore message node 1 by comparing if edge object reference the same
							if(N2.dN.get(k)!=N1.dN.get(ref))
								//find "child" node of node 1
								if(N2.dN.get(k).N1.id!=N2.id)
									temp = temp*N2.dN.get(k).get_m12(Utilities.intToPBS(j,N2.V.size()),k,fresh);
								else
									temp = temp*N2.dN.get(k).get_m21(Utilities.intToPBS(j,N2.V.size()),k,fresh);
						}
						m21[i] = m21[i] + epot(Utilities.intToPBS(i,N1.V.size()),Utilities.intToPBS(j,N2.V.size()))*N2.npot(Utilities.intToPBS(j,N2.V.size()),false)*temp;
					}
				}
			}
			//normalization
			double sum = 0.0;
			for(int i=0; i<m21.length; i++)
				sum = sum + m21[i];
			for(int i=0; i<m21.length; i++)
				m21[i] = m21[i]/sum;
			
			for(int i=0; i<m21d.length; i++)
				m21d[i] = true;
		}
		//System.out.println(m21[x1]);
		return m21[Integer.parseInt(X1,2)];
	}


/*
	//ORIGINAL BP WITHOUT MESSAGE NORMALIZATION

	public double get_m12(String LX2, int ref, boolean fresh){
		//values at supernode 2 may not be all needed, check for required values only
		//IMPORTANT NOTE: for this to work, the LX2 string must be of same order as node list in supernode
		String X2 = "";
		for(int i=0; i<E.size(); i++){
			if(N2.V.contains(E.get(i).n1)) //node 1 of edge is in supernode 2
				X2 = X2 + LX2.substring(N2.V.indexOf(E.get(i).n1),N2.V.indexOf(E.get(i).n1)+1);
			else //node 2 of edge is in supernode 2
				X2 = X2 + LX2.substring(N2.V.indexOf(E.get(i).n2),N2.V.indexOf(E.get(i).n2)+1);
		}
		//System.out.println("m_"+n1.id+"-->"+n2.id);
		//calculate only if message has not already been computed
		if(!m12d[Integer.parseInt(X2,2)] || fresh){
			double temp;
			m12[Integer.parseInt(X2,2)] = 0.0;
			//leaf node stopping condition (node 1 is a leaf node)
			if(N1.dN.size()==1){
				//System.out.print(" LEAF ");
				//sum over alphabet size (values of node 1)
				for(int i=0; i<m12.length; i++)
					m12[Integer.parseInt(X2,2)] = m12[Integer.parseInt(X2,2)] + N1.npot(Utilities.intToPBS(i,N1.V.size()))*epot(Utilities.intToPBS(i,N1.V.size()),X2);
			}
			else
				//sum over alphabet size (values of node 1)
				for(int i=0; i<m12.length; i++){
					temp = 1.0;
					//product over messages from neighbours of node 1 excluding node 2 
					for(int j=0; j<N1.dN.size(); j++){
						//ignore message node 2 by comparing if edge object reference the same
						if(N1.dN.get(j)!=N2.dN.get(ref))
							//find "child" node of node 1
							if(N1.dN.get(j).N1.id!=N1.id)
								temp = temp*N1.dN.get(j).get_m12(Utilities.intToPBS(i,N1.V.size()),j,fresh);
							else
								temp = temp*N1.dN.get(j).get_m21(Utilities.intToPBS(i,N1.V.size()),j,fresh);
					}
					m12[Integer.parseInt(X2,2)] = m12[Integer.parseInt(X2,2)] + epot(Utilities.intToPBS(i,N1.V.size()),X2)*N1.npot(Utilities.intToPBS(i,N1.V.size()))*temp;
				}
			m12d[Integer.parseInt(X2,2)] = true;
		}
		//System.out.println(m12[x2]);
		return m12[Integer.parseInt(X2,2)];
	}
	
	//message from node 2 to node 1
	public double get_m21(String LX1, int ref, boolean fresh){
		//values at supernode ` may not be all needed, check for required values only
		//IMPORTANT NOTE: for this to work, the LX2 string must be of same order as node list in supernode
		String X1 = "";
		for(int i=0; i<E.size(); i++){
			if(N1.V.contains(E.get(i).n1)) //node 1 of edge is in supernode 1
				X1 = X1 + LX1.substring(N1.V.indexOf(E.get(i).n1),N1.V.indexOf(E.get(i).n1)+1);
			else //node 2 of edge is in supernode 1
				X1 = X1 + LX1.substring(N1.V.indexOf(E.get(i).n2),N1.V.indexOf(E.get(i).n2)+1);
		}
		
		//System.out.println("m_"+n2.id+"-->"+n1.id);
		//calculate only if message has not already been computed
		if(!m21d[Integer.parseInt(X1,2)] || fresh){
			double temp;
			m21[Integer.parseInt(X1,2)] = 0.0;
			//leaf node stopping condition (node 2 is a leaf node)
			if(N2.dN.size()==1){
				//System.out.print(" LEAF ");
				//sum over alphabet size (values of node 2)
				for(int i=0; i<m21.length; i++)
					m21[Integer.parseInt(X1,2)] = m21[Integer.parseInt(X1,2)] + N2.npot(Utilities.intToPBS(i,N2.V.size()))*epot(X1,Utilities.intToPBS(i,N2.V.size()));
			}
			else
				//sum over alphabet size (values of node 2)
				for(int i=0; i<m21.length; i++){
					temp = 1.0;
					//product over messages from neighbours of node 2 excluding node 1 
					for(int j=0; j<N2.dN.size(); j++){
						//ignore message node 1 by comparing if edge object reference the same
						if(N2.dN.get(j)!=N1.dN.get(ref))
							//find "child" node of node 1
							if(N2.dN.get(j).N1.id!=N2.id)
								temp = temp*N2.dN.get(j).get_m12(Utilities.intToPBS(i,N2.V.size()),j,fresh);
							else
								temp = temp*N2.dN.get(j).get_m21(Utilities.intToPBS(i,N2.V.size()),j,fresh);
					}
					m21[Integer.parseInt(X1,2)] = m21[Integer.parseInt(X1,2)] + epot(X1,Utilities.intToPBS(i,N2.V.size()))*N2.npot(Utilities.intToPBS(i,N2.V.size()))*temp;
				}
			m21d[Integer.parseInt(X1,2)] = true;
		}
		//System.out.println(m21[x1]);
		return m21[Integer.parseInt(X1,2)];
	}
*/
	
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
