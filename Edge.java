import java.util.Arrays;

public class Edge extends Clique.AbstractClique{

	public Node n1; //node 1
	public Node n2; //node 2
	public double[] m12; //message from node 1 --> 2 
	public double[] m21; //message from node 2 --> 1
	public boolean m12d; //message already computed?
	public boolean m21d;

	//default constructor sets THETA = 0.6
	public Edge(Node p1, Node p2, String edge_type, pot_func.abstract_pot potential_function, int range){
		super(edge_type,range);
		pot = potential_function;
		n1 = p1;
		n2 = p2;
		m12 = new double[R]; Arrays.fill(m12,0.0);
		m21 = new double[R]; Arrays.fill(m21,0.0);
		m12d = false;
		m21d = false;
		attach();
	}
	
	//edge-potential function
	//v1 should always be value of node 1
	public double pot(int v1, int v2){
		int[] node_values = new int[2];
		node_values[0] = v1;
		node_values[1] = v2;
		return pot(node_values);
	}
	public double pot(int[] v){
		if(v[0]==0)
			v[0]=-1;
		if(v[1]==0)
			v[1]=-1;
		return pot.U(v);
	}

	//message from node 1 to node 2
	public double get_m12(int x2, int ref, boolean fresh){
		//----- compute message if not already done or if want new calculation
		if(!m12d || fresh){
			double temp;
			Arrays.fill(m12,0.0);
			//----- leaf node stopping condition (node 1 is a leaf node)
			if(n1.dN.size()==1)
				for(int i=0; i<m12.length; i++) //calculate for different values of node 2
					for(int j=0; j<m12.length; j++) //sum over alphabet size (values of node 1)
						m12[i] = m12[i] + n1.pot(j)*pot(j,i);
			//----- not stopping, must recursively find more messages
			else
				for(int i=0; i<m12.length; i++) //calculate for different values of node 2
					for(int j=0; j<m12.length; j++){ //sum over alphabet size (values of node 1)
						temp = 1.0;
						for(int k=0; k<n1.dN.size(); k++) //product over messages from neighbours of node 1 excluding node 2
							if(n1.dN.get(k)!=n2.dN.get(ref))
								if(n1.dN.get(k).n1!=n1)
									temp = temp*n1.dN.get(k).get_m12(j,k,fresh);
								else
									temp = temp*n1.dN.get(k).get_m21(j,k,fresh);
						m12[i] = m12[i] + pot(j,i)*n1.pot(j)*temp;
					}
			//----- normalization
			Utilities.normalize(m12);
			//----- set calculated flag to true
			m12d = true;
		}
		//----- return message
		return m12[x2];
	}
	
	//message from node 2 to node 1
	public double get_m21(int x1, int ref, boolean fresh){
		//----- compute message if not already done or if want new calculation
		if(!m21d || fresh){
			double temp;
			Arrays.fill(m21,0.0);
			//----- leaf node stopping condition (node 2 is a leaf node)
			if(n2.dN.size()==1)
				for(int i=0; i<m21.length; i++) //calculate for different values of node 1
					for(int j=0; j<m21.length; j++) //sum over alphabet size (values of node 2)
						m21[i] = m21[i] + n2.pot(j)*pot(i,j);
			//----- not stopping, must recursively find more messages
			else
				for(int i=0; i<m21.length; i++) //calculate for different values of node 1
					for(int j=0; j<m21.length; j++){ //sum over alphabet size (values of node 2)
						temp = 1.0;
						for(int k=0; k<n2.dN.size(); k++) //product over messages from neighbours of node 2 excluding node 1 
							if(n2.dN.get(k)!=n1.dN.get(ref))
								if(n2.dN.get(k).n1!=n2)
									temp = temp*n2.dN.get(k).get_m12(j,k,fresh);
								else
									temp = temp*n2.dN.get(k).get_m21(j,k,fresh);
						m21[i] = m21[i] + pot(i,j)*n2.pot(j)*temp;
					}
			//----- normalization
			Utilities.normalize(m21);
			//----- set calculated flag to true
			m21d = true;
		}
		//----- return message
		return m21[x1];
	}

	//adds this edge to neighbourhood lists of its ends (i.e. attach to nodes)
	public void attach(){
		n1.dN.add(this); n2.dN.add(this);
	}
	//removes this edge from neighbourhood lists of its ends (i.e. detach from nodes)
	public void detach(){
		n1.dN.remove(this); n2.dN.remove(this);
	}

	//returns id of other node
	public int getOtherNode(int ref){
		if(ref==n1.ID) return n2.ID;
		else return n1.ID;
	}
	//returns reference to other node
	public Node getOtherNode(Node ref){
		if(ref==n1) return n2;
		else return n1;
	}

	//output to string
	public String toString(){
		return "("+n1.ID+","+n2.ID+")";
	}
}
