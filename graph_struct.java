import java.util.ArrayList;
import java.util.Map;

class graph_struct{
	
	//----- IMAGE GRAPH STRUCTURES -----
	
	//interface START
	
	public static interface img_struct {
		// - SHOULD NOTE THAT img_struct.mk_edges(...) does not use a deep copy of potential function object, this gives MRF full control of edges
		public void mk_edges(Graph_img G, Map<String, Object> THETA);
		public abstract void getCutset(int row, int spacing, Graph_img G, ArrayList<Node> sV, ArrayList<Edge> sE);
	}
	
	//interface END
	
	public static class ising4pt implements img_struct{
		public void getCutset(int row, int spacing, Graph_img G, ArrayList<Node> sV, ArrayList<Edge> sE){
			int w = G.w, h = G.h;
			ArrayList<Node> V = G.V; ArrayList<Edge> E = G.E;
			Node Trg,Src; Edge Link;
			for(int x=0; x<w; x++)
				for(int y=0; y<spacing; y++){
					Trg = V.get(x*h+row+y);
					//----- condition on the boundaries unless we are on top of very first strip OR bottom of very last strip
					if(y==0 ||y==spacing-1){
						if(y==0) //condition on top boundary
							Src = V.get(x*h+row-1);
						else //condition on bottom boundary
							Src = V.get(x*h+row+1);;
						for(int k=0; k<Trg.r; k++) //collapse the self-potential of Src node into Trg node
							Trg.sf[k] = Trg.sf[k] * Src.npot(Src.VAL);
						Link = Trg.findLink(Src);
						if(Link.n1 == Trg)
							for(int v=0; v<Trg.r; v++)
								Trg.sf[v] = Trg.sf[v] * Link.epot(v,Src.VAL);
						else
							for(int v=0; x<Trg.r; v++)	
								Trg.sf[v] = Trg.sf[v] * Link.epot(Src.VAL,v);
						sV.add(Trg);
					}
					//----- don't need to do anything with nodes not on boundaries
					else
						sV.add(Trg);
				}
			//----- is internal edge if both ends are in the created set of nodes
			for(int i=0; i<E.size(); i++)
				if(sV.contains(E.get(i).n1) && sV.contains(E.get(i).n2))
					sE.add(E.get(i));
		}
		public void mk_edges(Graph_img G, Map<String, Object> THETA){
			int w = G.w, h = G.h;
			ArrayList<Node> V = G.V; ArrayList<Edge> E = G.E;
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
			for(int x=0; x<w; x++)
				for(int y=0; y<h ;y++)
					if(x*h+y != w*h-1)//bottom right corner - do nothing since its neighbours will notify it of connection
						if((x*h+y)%h == h-1){//bottom edge
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+h),"HOR",HOR,r));
						}
						else if(x*h+y > (w-1)*h-1){//right edge
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+1),"VER",VER,r));
						}
						else{// other
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+1),"VER",VER,r));
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+h),"HOR",HOR,r));
						}
		}
	}	
	
	public static class ising8pt implements img_struct{
		public void getCutset(int row, int spacing, Graph_img G, ArrayList<Node> sV, ArrayList<Edge> sE){

		}
		public void mk_edges(Graph_img G, Map<String, Object> THETA){
			int w = G.w, h = G.h;
			ArrayList<Node> V = G.V; ArrayList<Edge> E = G.E;
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
			for(int x=0; x<w; x++)
				for(int y=0; y<h ;y++)
					//System.out.println(n++);
					if(x*h+y != w*h-1)//bottom right corner - do nothing since its neighbours will notify it of connection
						if((x*h+y)%h == 0 && x!=w-1){//top edge except top right corner
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+h),"HOR",HOR,r)); //right
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+1),"VER",VER,r)); //down
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+h+1),"DIAG",DIAG,r)); //down diag
						}
						else if((x*h+y)%h == h-1){//bottom edge
							//System.out.println("bottom");
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+h),"HOR",HOR,r)); //right
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+h-1),"XDIAG",XDIAG,r)); //upper xdiag
						}
						else if(x*h+y > (w-1)*h-1){//right edge
							//System.out.println("right");
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+1),"VER",VER,r)); //down
						}
						else{// other
							//System.out.println("other");
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+h),"HOR",HOR,r)); //right
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+1),"VER",VER,r)); //down
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+h+1),"DIAG",DIAG,r)); //down diag
							E.add(new Edge(V.get(x*h+y),V.get(x*h+y+h-1),"XDIAG",XDIAG,r)); //upper xdiag
						}
		}
	}
}
