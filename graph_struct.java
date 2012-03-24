import java.util.ArrayList;
import java.util.Map;

class graph_struct{
	
	//----- IMAGE GRAPH STRUCTURES -----
	//interface START
	public static interface img_struct {
		// - SHOULD NOTE THAT img_struct.mk_edges(...) does not use a deep copy of potential function object, this gives MRF full control of edges
		void mk_edges(int w, int h, ArrayList<Node> V, ArrayList<Edge> E, Map<String, Object> THETA);
	}
	//interface END
	
	public static class ising4pt implements img_struct{
		public void mk_edges(int w, int h, ArrayList<Node> V, ArrayList<Edge> E, Map<String, Object> THETA){
			//----- unpack THETA for the proper items
			int r = V.get(0).r; //range of node values dictated by node list
			pot_func.n_pot_func SELF = (pot_func.n_pot_func)THETA.get("SELF");
			pot_func.e_pot_func HOR = (pot_func.e_pot_func)THETA.get("HOR");
			pot_func.e_pot_func VER = (pot_func.e_pot_func)THETA.get("VER");
			//----- ADD self potentials to nodes
			for(int i=0;i<V.size();i++)
				V.get(i).pot = SELF;
			//----- ADD edges with potentials
			//Each node responsible for communicating its connection with "right" and "down" node.
			// - left and top edges generic
			// - special cases at bottom edge, right edge, and bottom right corner are special cases
			//E = new ArrayList<Edge>(0);
			for(int i=0; i<w; i++) //i = col
				for(int j=0; j<h ;j++) //j = row
					if(i*h+j != w*h-1)//bottom right corner - do nothing since its neighbours will notify it of connection
						if((i*h+j)%h == h-1){//bottom edge
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+h),"HOR",HOR,r));
						}
						else if(i*h+j > (w-1)*h-1){//right edge
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+1),"VER",VER,r));
						}
						else{// other
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+1),"VER",VER,r));
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+h),"HOR",HOR,r));
						}
		}
	}	
	
	public static class ising8pt implements img_struct{
		public void mk_edges(int w, int h, ArrayList<Node> V, ArrayList<Edge> E, Map<String, Object> THETA){
			//----- unpack THETA for the proper items
			int r = V.get(0).r; //range of node values dictated by node list
			pot_func.n_pot_func SELF = (pot_func.n_pot_func)THETA.get("SELF");
			pot_func.e_pot_func HOR = (pot_func.e_pot_func)THETA.get("HOR");
			pot_func.e_pot_func VER = (pot_func.e_pot_func)THETA.get("VER");
			pot_func.e_pot_func DIAG = (pot_func.e_pot_func)THETA.get("DIAG");
			pot_func.e_pot_func XDIAG = (pot_func.e_pot_func)THETA.get("XDIAG");
			//----- ADD self potentials to nodes
			for(int i=0;i<V.size();i++)
				V.get(i).pot = SELF;
			//----- ADD edges with potentials
			//Each generic node responsible for communicating its connection with "right", "down", lower "diag", and upper "xdiag" nodes.
			//- Top row does not need to communicate with upper "xdiag"
			//- Bottom row does not need to communicate with "down" or lower "diag"
			//- Left column only need to communicate "down"
			//- Bottom left corner does not need to do anything, all connections are communicated to it by other nodes
			E = new ArrayList<Edge>(0);
			Edge newE;
			for(int i=0; i<w; i++) //i = col
				for(int j=0; j<h ;j++) //j = row
					//System.out.println(n++);
					if(i*h+j != w*h-1)//bottom right corner - do nothing since its neighbours will notify it of connection
						if((i*h+j)%h == 0 && i!=w-1){//top edge except top right corner
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+h),"HOR",HOR,r)); //right
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+1),"VER",VER,r)); //down
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+h+1),"DIAG",DIAG,r)); //down diag
						}
						else if((i*h+j)%h == h-1){//bottom edge
							//System.out.println("bottom");
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+h),"HOR",HOR,r)); //right
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+h-1),"XDIAG",XDIAG,r)); //upper xdiag
						}
						else if(i*h+j > (w-1)*h-1){//right edge
							//System.out.println("right");
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+1),"VER",VER,r)); //down
						}
						else{// other
							//System.out.println("other");
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+h),"HOR",HOR,r)); //right
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+1),"VER",VER,r)); //down
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+h+1),"DIAG",DIAG,r)); //down diag
							E.add(new Edge(V.get(i*h+j),V.get(i*h+j+h-1),"XDIAG",XDIAG,r)); //upper xdiag
						}
		}
	}
}
