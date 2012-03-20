import java.util.ArrayList;
import java.lang.Math;

public class Node {
	
	//***** could probably save quite a bit of space by using inheritance (orig/copy nodes) *****//
	
	public int id;
	public boolean L; //is node in cutset/copy node?
	public int ref; // copy reference
	
	public ArrayList<Edge> dN; //neighbours
	public ArrayList<Node> R; //relevant nodes
	public ArrayList<Node> C; //node copies
	
	public double[] Z; //beliefs
	public double ek; //exponent coefficient for copy nodes,
	public double sf[]; //scaling factor for border conditioning
	
	public boolean observed; //node value has been observed, freeze node value (used in arithmetic coding)
	public int val; //value of node used in conditioning
	public int N_ID; //original node id (id and ref can be lost when doing graph collapsing)
	public int N_VAL; //observed node value
	
	public double THETA = 0.0;

	public Node(int num, int r){
		id = num;
		L = false;
		ref = num;
		
		dN = new ArrayList<Edge>(0);
		R = new ArrayList<Node>(0);
		C = new ArrayList<Node>(0);
		C.add(this);
		
		Z = new double[r];
		ek = 1.0;
		sf = new double[r];
		for(int i=0; i<r; i++)
			sf[i] = 1.0;
		
		observed = false;
		val = -1;
		N_VAL = -1;
		N_ID = ref;
	}
	
	public Node(int num, int r, int pixel){
		id = num;
		L = false;
		ref = num;
		
		dN = new ArrayList<Edge>(0);
		R = new ArrayList<Node>(0);
		C = new ArrayList<Node>(0);
		C.add(this);
		
		Z = new double[r];
		ek = 1.0;
		sf = new double[r];
		for(int i=0; i<r; i++)
			sf[i] = 1.0;
		
		observed = false;
		val = -1;
		N_VAL = pixel;
		N_ID = ref;
	}

	public Node(int num, int r, int cons, int origNid){
		id = num;
		L = true;
		ref = origNid;
		
		dN = new ArrayList<Edge>(0);
		R = new ArrayList<Node>(0);
		C = new ArrayList<Node>(0);
		C.add(this);
		
		Z = new double[r];
		ek = 1.0/((double)cons);
		sf = new double[r];
		for(int i=0; i<r; i++)
			sf[i] = 1.0;
		
		observed = false;
		val = -1;
		N_VAL = -1;
		N_ID = ref;
	}
	
	public void set_ek(int cons){
		if(cons==0)
			cons=1;
		ek = 1.0/((double)cons);
	}
	
	//self-potential function
	public double npot(int v){
		int x=1; //actual value to use (i.e. in ising, the values a -1, 1, so if v=0 --> x=-1, v=1 --> x=1)
		if(observed){
			if(v==N_VAL){
				if(N_VAL==0)
					x=-1;
				else
					x=N_VAL;
				if(L)
					return sf[N_VAL]*Math.pow(Math.exp(THETA*x),ek);
				else
					return sf[N_VAL]*Math.exp(THETA*x);
			}
			else
				return 0.0;
		}
		else if(L){//if conditioning is happening
			if(v==val){
				if(v==0)
					x=-1;
				else
					x=v;
				return sf[v]*Math.pow(Math.exp(THETA*x),ek); //dirac on the conditioned value
			}
			else
				return 0.0;
		}
		else
			if(v==0)
				x=-1;
			else
				x=v;
			return sf[v]*Math.exp(THETA*x);
	}
	
	//used for arithmetic coding - uses IMG_VAL and fixes this node + all copy nodes to that value for BP
	public void observe(){
		for(int i=0; i<C.size(); i++){
			C.get(i).observed = true;
			C.get(i).val = N_VAL;
			C.get(i).N_VAL = N_VAL;
		}
	}
	
	public void findR()
	{
		if(L)
			R.add(this);
		for(int i=0; i<dN.size(); i++)
			dN.get(i).getOtherNode(this).findR_calc(R,id,id);
	}
	
	public void findR_calc(ArrayList<Node> R, int prev, int src){
		if(id==ref && L==true){
			boolean relevant = false;
			for(int i=0; i<C.size(); i++)
				if(id > src){
					for(int j=0; j<C.get(i).dN.size(); j++)
						if( C.get(i).dN.get(j).getOtherNode(C.get(i).id) < src ){
							relevant = true;
							break;
						}
				}
				else{
					for(int j=0; j<C.get(i).dN.size(); j++)
						if( C.get(i).dN.get(j).getOtherNode(C.get(i).id) > src ){
							relevant = true;
							break;
						}
				}
			if(relevant)
				R.add(this);
		}
		for(int i=0; i<dN.size(); i++)
			if(dN.get(i).getOtherNode(id)!=prev)
				dN.get(i).getOtherNode(this).findR_calc(R,id,src);
	}
	
	public void calcZ(){
		calcZ(false);
	}
	
	public void calcZ(boolean fresh){
		for(int i=0; i<Z.length; i++){
			Z[i] = npot(i);
			//System.out.println(Z[i]);
			for(int j=0; j<dN.size(); j++){
				if(id==dN.get(j).n1.id)
					Z[i] = Z[i]*dN.get(j).get_m21(i,j,fresh);
				else
					Z[i] = Z[i]*dN.get(j).get_m12(i,j,fresh);
			}
		}
	}
	
	public String toString(){
		if(id!=ref)
			return "(" + id + "<" + ref + ">" + "[" + N_ID + "] " + dN.size() /*+ ":" + ek*/ + ")";
		else
			return "(" + id + "[" + N_ID + "] " + dN.size() /*+ ":" + ek*/ + ")";
	}
}

