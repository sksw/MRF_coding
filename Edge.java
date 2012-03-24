import java.util.Arrays;

public class Edge {

	public Node n1; //node 1
	public Node n2; //node 2
	public final int r; //range of values
	public double[] m12; //message from node 1 --> 2 
	public double[] m21; //message from node 2 --> 1
	public boolean m12d; //message already computed?
	public boolean m21d;
	public String TYPE; //edge type
	public pot_func.e_pot_func pot; //edge potential function parameter

	//default constructor sets THETA = 0.6
	public Edge(Node p1, Node p2, String type, pot_func.e_pot_func potential_function, int range){
		n1 = p1;
		n2 = p2;
		r = range;
		m12 = new double[r]; Arrays.fill(m12,0.0);
		m21 = new double[r]; Arrays.fill(m21,0.0);
		m12d = false;
		m21d = false;
		TYPE = type;
		pot = potential_function;
		attach();
	}
	
	//edge-potential function
	//v1 should always be value of node 1
	public double epot(int v1, int v2){
		if(v1==0)
			v1=-1;
		if(v2==0)
			v2=-1;
		return pot.U(v1,v2);
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
						m12[i] = m12[i] + n1.npot(j)*epot(j,i);
			//----- not stopping, must recursively find more messages
			else
				for(int i=0; i<m12.length; i++) //calculate for different values of node 2
					for(int j=0; j<m12.length; j++){ //sum over alphabet size (values of node 1)
						temp = 1.0;
						for(int k=0; k<n1.dN.size(); k++) //product over messages from neighbours of node 1 excluding node 2
							if(n1.dN.get(k)!=n2.dN.get(ref))
								if(n1.dN.get(k).n1.id!=n1.id)
									temp = temp*n1.dN.get(k).get_m12(j,k,fresh);
								else
									temp = temp*n1.dN.get(k).get_m21(j,k,fresh);
						m12[i] = m12[i] + epot(j,i)*n1.npot(j)*temp;
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
						m21[i] = m21[i] + n2.npot(j)*epot(i,j);
			//----- not stopping, must recursively find more messages
			else
				for(int i=0; i<m21.length; i++) //calculate for different values of node 1
					for(int j=0; j<m21.length; j++){ //sum over alphabet size (values of node 2)
						temp = 1.0;
						for(int k=0; k<n2.dN.size(); k++) //product over messages from neighbours of node 2 excluding node 1 
							if(n2.dN.get(k)!=n1.dN.get(ref))
								if(n2.dN.get(k).n1.id!=n2.id)
									temp = temp*n2.dN.get(k).get_m12(j,k,fresh);
								else
									temp = temp*n2.dN.get(k).get_m21(j,k,fresh);
						m21[i] = m21[i] + epot(i,j)*n2.npot(j)*temp;
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
		if(ref==n1.id) return n2.id;
		else return n1.id;
	}
	//returns reference to other node
	public Node getOtherNode(Node ref){
		if(ref==n1) return n2;
		else return n1;
	}

	//output to string
	public String toString(){
		return "("+n1.id+","+n2.id+")";
	}
}
