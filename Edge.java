import java.lang.Math;

public class Edge {
	//public final int n1id; //node id's
	//public final int n2id;
	public Node n1; //node pointers
	public Node n2;
	public double[] m12; //messages
	public double[] m21;
	public boolean[] m12d; //message calculated?
	public boolean[] m21d;
	public int fixedN;
	public int eN_VAL; //observed node value when conditioning
	
	public double THETA;
	public String TYPE;

	//default constructor sets THETA = 0.6
	public Edge(Node p1, Node p2, int range){
		n1 = p1;
		n2 = p2;
		m12 = new double[range];
		m21 = new double[range];
		m12d = new boolean[range];
		m21d = new boolean[range];
		fixedN = 0;
		eN_VAL = -1;
		for(int i=0; i<m12d.length; i++)
			m12d[i] = false;
		for(int i=0; i<m21d.length; i++)
			m21d[i] = false;
		
		THETA = 0.6;
	}
	
	public Edge(Node p1, Node p2, int range, String type, double theta){
		n1 = p1;
		n2 = p2;
		m12 = new double[range];
		m21 = new double[range];
		m12d = new boolean[range];
		m21d = new boolean[range];
		fixedN = 0;
		eN_VAL = -1;
		for(int i=0; i<m12d.length; i++)
			m12d[i] = false;
		for(int i=0; i<m21d.length; i++)
			m21d[i] = false;
		
		TYPE = type;
		THETA = theta;
	}
	
	//v1 should always be value of node 1
	public double epot(int v1, int v2){
		int x;
		if(fixedN==1){
			if(v2==0)
				v2=-1;
			if(eN_VAL==0)
				x=-1;
			else
				x=eN_VAL;
			return Math.exp(THETA*((double)x)*((double)v2));
		}
		else if(fixedN==2){
			if(v1==0)
				v1=-1;
			if(eN_VAL==0)
				x=-1;
			else
				x=eN_VAL;
			return Math.exp(THETA*((double)v1)*((double)x));
		}
		else{
			if(v1==0)
				v1=-1;
			if(v2==0)
				v2=-1;
			return Math.exp(THETA*((double)v1)*((double)v2));
		}
	}


	//message from node 1 to node 2
	public double get_m12(int x2, int ref, boolean fresh){
		//System.out.println("m_"+n1.id+"-->"+n2.id);
		//calculate only if message has not alreay been computed
		if(!m12d[x2] || fresh){
			double temp;
			for(int i=0; i<m12.length; i++)
				m12[i] = 0.0;
			//leaf node stopping condition (node 1 is a leaf node)
			if(n1.dN.size()==1){
				//System.out.print(" LEAF ");
				//calculate for different values of node 2
				for(int i=0; i<m12.length; i++)
					//sum over alphabet size (values of node 1)
					for(int j=0; j<m12.length; j++)
						m12[i] = m12[i] + n1.npot(j)*epot(j,i);
			}
			else{
				//calculate for different values of node 2
				for(int i=0; i<m12.length; i++){
					//sum over alphabet size (values of node 1)
					for(int j=0; j<m12.length; j++){
						temp = 1.0;
						//product over messages from neighbours of node 1 excluding node 2 
						for(int k=0; k<n1.dN.size(); k++){
							//ignore message node 2 by comparing if edge object reference the same
							if(n1.dN.get(k)!=n2.dN.get(ref))
								//find "child" node of node 1
								if(n1.dN.get(k).n1.id!=n1.id)
									temp = temp*n1.dN.get(k).get_m12(j,k,fresh);
								else
									temp = temp*n1.dN.get(k).get_m21(j,k,fresh);
						}
						m12[i] = m12[i] + epot(j,i)*n1.npot(j)*temp;
					}
				}
			}
			//normalization
			temp = 0.0;
			for(int i=0; i<m12.length; i++)
				temp = temp + m12[i];
			for(int i=0; i<m12.length; i++)
				m12[i] = m12[i]/temp;
			
			for(int i=0; i<m12d.length; i++)
				m12d[x2] = true;
		}
		//System.out.println(m12[x2]);
		return m12[x2];
	}
	
	//message from node 2 to node 1
	public double get_m21(int x1, int ref, boolean fresh){
		//System.out.println("m_"+n2.id+"-->"+n1.id);
		//calculate only if message has not already been computed
		if(!m21d[x1] || fresh){
			double temp;
			for(int i=0; i<m21.length; i++)
				m21[i] = 0.0;
			//leaf node stopping condition (node 2 is a leaf node)
			if(n2.dN.size()==1){
				//System.out.print(" LEAF ");
				//calculate for different values of node 1
				for(int i=0; i<m21.length; i++)
					//sum over alphabet size (values of node 2)
					for(int j=0; j<m21.length; j++)
						m21[i] = m21[i] + n2.npot(j)*epot(i,j);
			}
			else{
				//calculate for different values of node 1
				for(int i=0; i<m21.length; i++){
					//sum over alphabet size (values of node 2)
					for(int j=0; j<m21.length; j++){
						temp = 1.0;
						//product over messages from neighbours of node 2 excluding node 1 
						for(int k=0; k<n2.dN.size(); k++){
							//ignore message node 1 by comparing if edge object reference the same
							if(n2.dN.get(k)!=n1.dN.get(ref))
								//find "child" node of node 1
								if(n2.dN.get(k).n1.id!=n2.id)
									temp = temp*n2.dN.get(k).get_m12(j,k,fresh);
								else
									temp = temp*n2.dN.get(k).get_m21(j,k,fresh);
						}
						m21[i] = m21[i] + epot(i,j)*n2.npot(j)*temp;
					}
				}
			}
			//normalization
			temp = 0.0;
			for(int i=0; i<m21.length; i++)
				temp = temp + m21[i];
			for(int i=0; i<m21.length; i++)
				m21[i] = m21[i]/temp;
			
			for(int i=0; i<m21d.length; i++)
				m21d[i] = true;
		}
		//System.out.println(m21[x1]);
		return m21[x1];
	}


/*
	//ORIGINAL BP WITHOUT MESSAGE NORMALIZATION
	
	//message from node 1 to node 2
	public double get_m12(int x2, int ref, boolean fresh){
		//System.out.println("m_"+n1.id+"-->"+n2.id);
		//calculate only if message has not alreay been computed
		if(!m12d[x2] || fresh){
			double temp;
			m12[x2] = 0.0;
			//leaf node stopping condition (node 1 is a leaf node)
			if(n1.dN.size()==1){
				//System.out.print(" LEAF ");
				//sum over alphabet size (values of node 1)
				for(int i=0; i<m12.length; i++)
					m12[x2] = m12[x2] + n1.npot(i)*epot(i,x2);
			}
			else
				//sum over alphabet size (values of node 1)
				for(int i=0; i<m12.length; i++){
					temp = 1.0;
					//product over messages from neighbours of node 1 excluding node 2 
					for(int j=0; j<n1.dN.size(); j++){
						//ignore message node 2 by comparing if edge object reference the same
						if(n1.dN.get(j)!=n2.dN.get(ref))
							//find "child" node of node 1
							if(n1.dN.get(j).n1.id!=n1.id)
								temp = temp*n1.dN.get(j).get_m12(i,j,fresh);
							else
								temp = temp*n1.dN.get(j).get_m21(i,j,fresh);
					}
					m12[x2] = m12[x2] + epot(i,x2)*n1.npot(i)*temp;
				}
			m12d[x2] = true;
		}
		System.out.println(m12[x2]);
		return m12[x2];
	}
	
	//message from node 2 to node 1
	public double get_m21(int x1, int ref, boolean fresh){
		//System.out.println("m_"+n2.id+"-->"+n1.id);
		//calculate only if message has not already been computed
		if(!m21d[x1] || fresh){
			double temp;
			m21[x1] = 0.0;
			//leaf node stopping condition (node 2 is a leaf node)
			if(n2.dN.size()==1){
				//System.out.print(" LEAF ");
				//sum over alphabet size (values of node 2)
				for(int i=0; i<m21.length; i++)
					m21[x1] = m21[x1] + n2.npot(i)*epot(x1,i);
			}
			else
				//sum over alphabet size (values of node 2)
				for(int i=0; i<m21.length; i++){
					temp = 1.0;
					//product over messages from neighbours of node 2 excluding node 1 
					for(int j=0; j<n2.dN.size(); j++){
						//ignore message node 1 by comparing if edge object reference the same
						if(n2.dN.get(j)!=n1.dN.get(ref))
							//find "child" node of node 1
							if(n2.dN.get(j).n1.id!=n2.id)
								temp = temp*n2.dN.get(j).get_m12(i,j,fresh);
							else
								temp = temp*n2.dN.get(j).get_m21(i,j,fresh);
					}
					m21[x1] = m21[x1] + epot(x1,i)*n2.npot(i)*temp;
				}
			m21d[x1] = true;
		}
		System.out.println(m21[x1]);
		return m21[x1];
	}
*/
	
	public int getOtherNode(int ref){
		if(ref==n1.id)
			return n2.id;
		else
			return n1.id;
	}
	
	public Node getOtherNode(Node ref){
		if(ref==n1)
			return n2;
		else
			return n1;
	}
	
	public String toString(){
		return "(" + n1.id + "[" + n1.N_ID + "]" + "," + n2.id + "[" + n2.N_ID + "]"  + ")";
	}
}
