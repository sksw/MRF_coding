import java.util.ArrayList;


public class Graph {
	
	public int w,h;
	public ArrayList<Node> V;
	public ArrayList<Edge> E;

	//default constructor, generic graph does not necessarily have width or height
	public Graph(){
	}
	
	public Graph(int width, int height){
		w = width;
		h = height;
		V = new ArrayList<Node>(0);
		E = new ArrayList<Edge>(0);
	}
	
	public Graph(int width, int height, ArrayList<Node> Nodes, ArrayList<Edge> Edges){
		w = width;
		h = height;
		V = Nodes;
		E = Edges;
	}
	
	public void make8ptEdges(int r, Theta theta){
		//int n = 0;
		E = null;
		E = new ArrayList<Edge>(0);
		Edge newE;
		for(int i=0;i<V.size();i++)
			V.get(i).THETA = theta.G_SELF;
		for(int i=0; i<w; i++) //i = col
			for(int j=0; j<h ;j++){ //j = row
				//System.out.println(n++);
				/*
				Each generic node responsible for communicating its connection
				with "right", "down", lower "diag", and upper "xdiag" nodes.
				- Top row does not need to communicate with upper "xdiag"
				- Bottom row does not need to communicate with "down" or lower "diag"
				- Left column only need to communicate "down"
				- Bottom left corner does not need to do anything, all connections
					are communicated to it by other nodes
				*/
				if(i*h+j == w*h-1){//bottom right corner
					//do nothing since its neighbours will notify it of connection
				}
				else if((i*h+j)%h == 0 && i!=w-1){//top edge except top right corner
					//System.out.println("top");
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",theta.G_HOR)); //right
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",theta.G_VER)); //down
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h+1),r,"DIAG",theta.G_DIAG)); //down diag
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h+1).dN.add(newE);
				}
				else if((i*h+j)%h == h-1){//bottom edge
					//System.out.println("bottom");
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",theta.G_HOR)); //right
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h-1),r,"XDIAG",theta.G_XDIAG)); //upper xdiag
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h-1).dN.add(newE);	
				}
				else if(i*h+j > (w-1)*h-1){//right edge
					//System.out.println("right");
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",theta.G_VER)); //down
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
				}
				else{// other
					//System.out.println("other");
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",theta.G_HOR)); //right
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",theta.G_VER)); //down
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h+1),r,"DIAG",theta.G_DIAG)); //down diag
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h+1).dN.add(newE);
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h-1),r,"XDIAG",theta.G_XDIAG)); //upper xdiag
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h-1).dN.add(newE);	
				}
			}
	}
	
	public void make4ptEdges(int r, double[] theta, String mode){
		//int n = 0;
		E = null;
		E = new ArrayList<Edge>(0);
		Edge newE;
		if(theta.length==3)
			for(int i=0;i<V.size();i++)
				V.get(i).THETA = theta[2];
		for(int i=0; i<w; i++)
			for(int j=0; j<h ;j++){
				//System.out.println(n++);
				/*
				Each node responsible for communicating its connection
				with "right" and "down" node.
				This makes the left and top edges generic, but bottom 
				and right edge needs special case since there may not 
				be "right" and "down" nodes
				Also, special case needed for bottom right corner
				*/
				if(i*h+j == w*h-1){//bottom right corner
					//do nothing since its neighbours will notify it of connection
				}
				else if((i*h+j)%h == h-1){//bottom edge
					//System.out.println("bottom");
					if(mode.equals("EQ")){
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
					}
					else if(mode.equals("VH")){
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",theta[1]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
					}
					else if(mode.equals("VHS")){
						
					}
					else{
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
					}
				}
				else if(i*h+j > (w-1)*h-1){//right edge
					//System.out.println("right");
					if(mode.equals("EQ")){
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
					}
					else if(mode.equals("VH")){
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
					}
					else if(mode.equals("VHS")){
						
					}
					else{
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
					}
				}
				else{// other
					//System.out.println("other");
					if(mode.equals("EQ")){
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
					}
					else if(mode.equals("VH")){
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",theta[1]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
					}
					else if(mode.equals("VHS")){
						
					}
					else{
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
						E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
					}
				}
			}
	}
	
	public void make4ptEdges(int r, double[] theta){
		//int n = 0;
		E = null;
		E = new ArrayList<Edge>(0);
		Edge newE;
		if(theta.length==3)
			for(int i=0;i<V.size();i++)
				V.get(i).THETA = theta[2];
		for(int i=0; i<w; i++)
			for(int j=0; j<h ;j++){
				//System.out.println(n++);
				/*
				Each node responsible for communicating its connection
				with "right" and "down" node.
				This makes the left and top edges generic, but bottom 
				and right edge needs special case since there may not 
				be "right" and "down" nodes
				Also, special case needed for bottom right corner
				*/
				if(i*h+j == w*h-1){//bottom right corner
					//do nothing since its neighbours will notify it of connection
				}
				else if((i*h+j)%h == h-1){//bottom edge
					//System.out.println("bottom");
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",theta[1]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
				}
				else if(i*h+j > (w-1)*h-1){//right edge
					//System.out.println("right");
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
				}
				else{// other
					//System.out.println("other");
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",theta[0]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",theta[1]));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
				}
			}
	}
	
	public void make4ptEdges(int r){
		//int n = 0;
		E = null;
		E = new ArrayList<Edge>(0);
		Edge newE;
		for(int i=0; i<w; i++)
			for(int j=0; j<h ;j++){
				//System.out.println(n++);
				/*
				Each node responsible for communicating its connection
				with "right" and "down" node.
				This makes the left and top edges generic, but bottom 
				and right edge needs special case since there may not 
				be "right" and "down" nodes
				Also, special case needed for bottom right corner
				*/
				if(i*h+j == w*h-1){//bottom right corner
					//do nothing since its neighbours will notify it of connection
				}
				else if((i*h+j)%h == h-1){//bottom edge
					//System.out.println("bottom");
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",Constants.HOR));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
				}
				else if(i*h+j > (w-1)*h-1){//right edge
					//System.out.println("right");
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",Constants.VER));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
				}
				else{// other
					//System.out.println("other");
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r,"VER",Constants.VER));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r,"HOR",Constants.HOR));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
				}
			}
	}

	public static void make4ptGraph(int h, int w, int r, ArrayList<Node> V, ArrayList<Edge> E){
		//reset lists
		V.clear();
		E.clear();
		Edge newE;
		//make nodes
		for(int i=0; i<w; i++)
			for(int j=0; j<h ;j++)
				V.add(new Node(i*h+j,r));
		//make edges
		for(int i=0; i<w; i++)
			for(int j=0; j<h ;j++){
				/*
				Each node responsible for communicating its connection
				with "right" and "down" node.
				This makes the left and top edges generic, but bottom 
				and right edge needs special case since there may not 
				be "right" and "down" nodes
				Also, special case needed for bottom right corner
				*/
				if(i*h+j == w*h-1){//bottom right corner
					//do nothing since its neighbours will notify it of connection
				}
				else if((i*h+j)%h == h-1){//bottom edge
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
				}
				else if(i*h+j > (w-1)*h-1){//right edge
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
				}
				else{// other
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+1),r));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+1).dN.add(newE);
					E.add(newE = new Edge(V.get(i*h+j),V.get(i*h+j+h),r));
						V.get(i*h+j).dN.add(newE);
						V.get(i*h+j+h).dN.add(newE);
				}
			}
	}
	
	public void getStrip(int stripNum, int thickness, ArrayList<Node> sV, ArrayList<Edge> sE, boolean last){
		Node Trg,Src;
		for(int i=0; i<w; i++)
			for(int j=0; j<thickness; j++){
				Trg = V.get(i*h+thickness*stripNum+stripNum+j);
				//condition on the boundaries unless we are on: top of very first strip OR bottom of very last strip
				if( (j==0 && stripNum!=0) || (j==thickness-1 && !last)){
					//condition on top boundary
					if(j==0)
						Src = V.get(i*h+thickness*stripNum+stripNum+j-1);
					//condition on bottom boundary
					else
						Src = V.get(i*h+thickness*stripNum+stripNum+j+1);

					//collapse the self-potential of Src node into Trg node
					for(int k=0; k<Trg.sf.length; k++)
						Trg.sf[k] = Trg.sf[k] * Src.npot(Src.N_VAL);
					//collapse the edge between Src node and Trg node
					for(int k=0; k<Src.dN.size(); k++)
						if(Src.dN.get(k).getOtherNode(Src) == Trg){
							if(Src.dN.get(k).n1 == Trg)
								for(int x=0; x<Trg.sf.length; x++)
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(x,Src.N_VAL);
							else
								for(int x=0; x<Trg.sf.length; x++)	
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(Src.N_VAL,x);
						}
					sV.add(Trg);
				}
				//don't need to do anything with nodes not on boundaries
				else
					sV.add(Trg);
			}
		for(int i=0; i<E.size(); i++)
			if(sV.contains(E.get(i).n1) && sV.contains(E.get(i).n2))
				sE.add(E.get(i));
	}
	
	public static void getStrip_OLD(int h, int w, int stripNum, int thickness, ArrayList<Node> V, ArrayList<Edge> E,
			ArrayList<Node> sV, ArrayList<Edge> sE, boolean last){
		Node Trg,Src;
		for(int i=0; i<w; i++)
			for(int j=0; j<thickness; j++){
				Trg = V.get(i*h+thickness*stripNum+stripNum+j);
				//condition on the boundaries unless we are on: top of very first strip OR bottom of very last strip
				if( (j==0 && stripNum!=0) || (j==thickness-1 && !last)){
					//condition on top boundary
					if(j==0)
						Src = V.get(i*h+thickness*stripNum+stripNum+j-1);
					//condition on bottom boundary
					else
						Src = V.get(i*h+thickness*stripNum+stripNum+j+1);
					//collapse the self-potential of Src node into Trg node
					for(int k=0; k<Trg.sf.length; k++)
						Trg.sf[k] = Trg.sf[k] * Src.npot(Src.N_VAL);
					//collapse the edge between Src node and Trg node
					for(int k=0; k<Src.dN.size(); k++)
						if(Src.dN.get(k).getOtherNode(Src) == Trg){
							if(Src.dN.get(k).n1 == Trg)
								for(int x=0; x<Trg.sf.length; x++)
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(x,Src.N_VAL);
							else
								for(int x=0; x<Trg.sf.length; x++)	
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(Src.N_VAL,x);
						}
					sV.add(Trg);
				}
				//don't need to do anything with nodes not on boundaries
				else
					sV.add(Trg);
			}
		for(int i=0; i<E.size(); i++)
			if(sV.contains(E.get(i).n1) && sV.contains(E.get(i).n2))
				sE.add(E.get(i));
	}
	
	public static void getStrip(int h, int w, int stripNum, int thickness, ArrayList<Node> V, ArrayList<Edge> E,
			ArrayList<Node> sV, ArrayList<Edge> sE){
		Node Trg,Src;
		for(int i=0; i<w; i++)
			for(int j=0+1; j<thickness+1; j++){
				Trg = V.get(i*h+thickness*stripNum+stripNum+j);
				//condition on the boundaries
				if(j==1 ||j==thickness){
					//condition on top boundary
					if(j==1)
						Src = V.get(i*h+thickness*stripNum+stripNum+j-1);
					//condition on bottom boundary
					else
						Src = V.get(i*h+thickness*stripNum+stripNum+j+1);
					//collapse the self-potential of Src node into Trg node
					for(int k=0; k<Trg.sf.length; k++)
						Trg.sf[k] = Trg.sf[k] * Src.npot(Src.N_VAL);
					//collapse the edge between Src node and Trg node
					for(int k=0; k<Src.dN.size(); k++)
						if(Src.dN.get(k).getOtherNode(Src) == Trg){
							if(Src.dN.get(k).n1 == Trg)
								for(int x=0; x<Trg.sf.length; x++)
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(x,Src.N_VAL);
							else
								for(int x=0; x<Trg.sf.length; x++)	
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(Src.N_VAL,x);
						}
					sV.add(Trg);
				}
				//don't need to do anything with nodes not on boundaries
				else
					sV.add(Trg);
			}
		for(int i=0; i<E.size(); i++)
			if(sV.contains(E.get(i).n1) && sV.contains(E.get(i).n2))
				sE.add(E.get(i));
	}
	
	//none static version of previous function assuming graph already decoded from image
	public void getStrip(int stripNum, int thickness, ArrayList<Node> sV, ArrayList<Edge> sE){
		Node Trg,Src;
		for(int i=0; i<w; i++)
			for(int j=0+1; j<thickness+1; j++){
				Trg = V.get(i*h+thickness*stripNum+stripNum+j);
				//condition on the boundaries
				if(j==1 ||j==thickness){
					//condition on top boundary
					if(j==1)
						Src = V.get(i*h+thickness*stripNum+stripNum+j-1);
					//condition on bottom boundary
					else
						Src = V.get(i*h+thickness*stripNum+stripNum+j+1);
					//collapse the self-potential of Src node into Trg node
					for(int k=0; k<Trg.sf.length; k++)
						Trg.sf[k] = Trg.sf[k] * Src.npot(Src.N_VAL);
					//collapse the edge between Src node and Trg node
					for(int k=0; k<Src.dN.size(); k++)
						if(Src.dN.get(k).getOtherNode(Src) == Trg){
							if(Src.dN.get(k).n1 == Trg)
								for(int x=0; x<Trg.sf.length; x++)
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(x,Src.N_VAL);
							else
								for(int x=0; x<Trg.sf.length; x++)	
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(Src.N_VAL,x);
						}
					sV.add(Trg);
				}
				//don't need to do anything with nodes not on boundaries
				else
					sV.add(Trg);
			}
		for(int i=0; i<E.size(); i++)
			if(sV.contains(E.get(i).n1) && sV.contains(E.get(i).n2))
				sE.add(E.get(i));
	}
	
	public static ArrayList<Node> getEdgeCover4pt(int h, int w, ArrayList<Node> V, ArrayList<Edge> E){
		//get edge cover for generic 4 point graph
		int pInx = 0; //previous node index
		int cInx; //current node index for going "up and down", not in enumerative order
		int nInx = h*w; //node list enum (makes sure all nodes have unique id)
		int oInx; //temp index of neighbour node
		int badGap; //identifies edge to not remove
		int dNs; //number of copies to calculate ek
		Edge tempE;
		ArrayList<Node> L = new ArrayList<Node>(0); //loop cutset
		
		for(int i=0; i<w; i++)
			for(int j=0; j<h ;j++){
				//sets the correct node index for going "up and down"
				if(i%2==0)
					cInx = i*h+j;
				else
					cInx = (i+1)*h-j-1;
				//System.out.print(cInx + " ");
				
				if(!V.get(pInx).L){ //previous node not in cutset
					V.get(cInx).L=true;
					L.add(V.get(cInx));
					/*
					find total number of copies (including original)
					get neighbour set size (from 0-4)
					"up" and "down" are discounted automatically (-2)
					original counted (+1)
					*/
					dNs = V.get(cInx).dN.size()-2+1;
					
					//forbid cutting certain edges to make sure graph connected
					if(cInx==0){ //top left (starting)
						badGap = 0;
						dNs++; //1 copy node to right
					}
					//note that if height is even, the ending node will NEVER be in the cutset
					else if(h%2==1 && w%2==1 && cInx==w*h-1){ //bottom right (ending)
						badGap = 0;
						dNs++; //1 copy node to left
					}
					//do not add back to top and bottom edge nodes because of "crossing edge exception"
					else if(cInx-pInx==h) //just crossed column
						badGap = h;
					else if (cInx%h == 0 || cInx%h == h-1) //about to cross column
						badGap = 0-h;
					else //generic node in column
						badGap = 0;
					
					//calculate ek
					V.get(cInx).set_ek(dNs);

					for(int k=0; k<V.get(cInx).dN.size(); k++){
						//not crossed node or up/down nodes
						oInx = V.get(cInx).dN.get(k).getOtherNode(cInx);
						if(cInx-oInx!=badGap  &&  cInx-oInx!=1  &&  cInx-oInx!=-1){
							//make copy node and edge
							V.add(new Node(nInx,V.get(cInx).Z.length,dNs,cInx));
							V.get(cInx).C.add(V.get(nInx));
							//L.add(V.get(nInx));
							E.add(tempE = new Edge(V.get(nInx),V.get(oInx),V.get(cInx).Z.length));
							V.get(nInx).dN.add(tempE);
							V.get(oInx).dN.add(tempE);
							//remove old edge
							tempE = V.get(cInx).dN.get(k);
							V.get(oInx).dN.remove(tempE);
							V.get(cInx).dN.remove(tempE);
							E.remove(tempE);
							//increment node list enum
							nInx++;
						}
					}
				}
				//increment previous node tracker
				pInx = cInx;
			}
		return L;
	}
	
	static void collapseEdgeCover(ArrayList<Node> V, ArrayList<Edge>E){
		for(int i=0; i<V.size(); i++)
			if( V.get(i).L && !(V.get(i).id == V.get(i).ref) ){
				for(int j=0; j<V.get(i).dN.size(); j++){
					if(V.get(i).dN.get(j).n1==V.get(i))
						V.get(i).dN.get(j).n1 = V.get(V.get(i).ref);
					else
						V.get(i).dN.get(j).n2 = V.get(V.get(i).ref);
					V.get(V.get(i).ref).dN.add(V.get(i).dN.get(j));
				}
				V.remove(i);
			}		
	}
	
	public static boolean checkCyclic(ArrayList<Node> V, ArrayList<Edge> E, int refInx, int inx, int pinx){
		if(V.get(inx).dN.size() == 0){
			//System.out.println("leaf: "+inx+" "+V.get(inx).dN.size());
			return false;
		}
		for(int i=0; i<V.get(inx).dN.size(); i++){
			//System.out.print(inx+" neigh "+i+": ");
			if(V.get(inx).dN.get(i).getOtherNode(inx) != pinx)
				if(V.get(inx).dN.get(i).getOtherNode(inx) == refInx){
					//System.out.println(V.get(inx).dN.get(i).getOtherNode(inx));
					return true;
				}
		}
		//System.out.println(inx+" no n");
		
		for(int i=0; i<V.get(inx).dN.size(); i++){
			//System.out.print(inx+" path "+i+": ");
			if(V.get(inx).dN.get(i).getOtherNode(inx) != pinx)
				if(checkCyclic(V,E,refInx,V.get(inx).dN.get(i).getOtherNode(inx),inx))
					return true;
		}
		return false;
	}
	
	/*this function keeps the graph "clean" by updating some information that might have been missed
	- relevant nodes
	- copy node list for copy nodes
	*/
	public static void clean(ArrayList<Node> V, ArrayList<Edge> E){
		//find relevant nodes
		/*for(int i=0; i<V.size(); i++)
			V.get(i).findR();
		for(int i=0; i<V.get(12).R.size(); i++)
			System.out.print(V.get(12).R.get(i));
		System.out.println();*/
		
		//update copy node lists for copy nodes
		for(int i=0; i<V.size(); i++)
			if(V.get(i).id!=V.get(i).ref)
				V.get(i).C = V.get(V.get(i).ref).C;
		/*for(int i=0; i<V.size(); i++)
			if(V.get(i).id!=V.get(i).ref){
				for(int j=0; j<V.get(i).C.size(); j++)
					System.out.print(V.get(i).C.get(j));
				System.out.println();
			}*/
	}
	
	public static void updateCopies(int origGraphSize, ArrayList<Node> V, ArrayList<Edge> E){
		for(int i=0; i<origGraphSize; i++)
			for(int j=0; j<V.get(i).C.size(); j++)
				V.get(i).C.get(j).N_VAL = V.get(i).N_VAL;
	}
	
	public static int collapse(Node Src, Node Trg, ArrayList<Node> V, ArrayList<Edge> E, ArrayList<Node> L){
		ArrayList<Edge> SrcTrgE = new ArrayList<Edge>(0);
		for(int i=Src.id+1; i<V.size(); i++){
			V.get(i).id = i-1;
			if(V.get(i).ref>Src.id)
				V.get(i).ref = V.get(i).ref-1;
		}
		//collapse the self-potential of Src node into Trg node;
		for(int i=0; i<Trg.sf.length; i++)
			Trg.sf[i] = Trg.sf[i] * Src.npot(Src.N_VAL);//-1 is a dummy variable
		//deal with the neighbours of Src node
		for(int i=0; i<Src.dN.size(); i++)
			//edge between Src and Trg, so must also "collapse" the edge potential
			if(Src.dN.get(i).getOtherNode(Src) == Trg){
				SrcTrgE.add(Src.dN.get(i));
				if(Src.dN.get(i).n1 == Trg)
					for(int j=0; j<Trg.sf.length; j++)
						Trg.sf[j] = Trg.sf[j] * Src.dN.get(i).epot(j,Src.N_VAL);
				else
					for(int j=0; j<Trg.sf.length; j++)	
						Trg.sf[j] = Trg.sf[j] * Src.dN.get(i).epot(Src.N_VAL,j);
			}
			//edge not between Src and Trg, need to re-map Src neighbours to Trg and fix "Src" end of edge to observed value
			else{
				if(Src.dN.get(i).n1 == Src){
					Src.dN.get(i).n1 = Trg;
					if(Src.dN.get(i).fixedN==0){
						Src.dN.get(i).fixedN = 1;
						Src.dN.get(i).eN_VAL = Src.N_VAL;
					}
				}
				else{
					Src.dN.get(i).n2 = Trg;
					if(Src.dN.get(i).fixedN==0){
						Src.dN.get(i).fixedN = 2;
						Src.dN.get(i).eN_VAL = Src.N_VAL;
					}
				}
				Trg.dN.add(Src.dN.get(i));
			}
		if(SrcTrgE!=null){
			for(int i=0; i<SrcTrgE.size(); i++){
				E.remove(SrcTrgE.get(i));
				Trg.dN.remove(SrcTrgE.get(i));
			}
		}
		if(Src.L){
			for(int i=0; i<Src.C.size(); i++)
				Src.C.get(i).C.remove(V.get(Src.ref));
			L.remove(V.get(Src.ref));
			V.remove(Src);
		}
		else
			V.remove(Src);
		return Trg.id;
	}
	
	//brute force belief calculation
	public static void beliefBF(int size, int id, ArrayList<Node> V, ArrayList<Edge> E){
		if(V.size()==1){
			for(int i=0; i<V.get(id).Z.length; i++)
				V.get(id).Z[i]=V.get(id).npot(i);
		}
		else{
			int[] val = new int[size-1];
			for(int i=0; i<val.length; i++)
				val[i]=0;
			//double[] Z = new double[V.get(id).Z.length];
			double[] Z = V.get(id).Z;
			for(int i=0; i<Z.length; i++)
				Z[i]=0.0;
			for(int i=0; i<Z.length; i++)
				beliefBF_calc(id,i,0,V,E,val,Z);
		}
	}
	static void beliefBF_calc(int id, int id_val, int inx,  ArrayList<Node> V, ArrayList<Edge> E, int[] val, double[] Z){
		for(int i=0; i<Z.length; i++) //cycle through every possible value of that node
			if(inx==(val.length)-1){
				double temp = 1.0;
				val[(val.length)-1]=i;
				/*
				for(int j=0; j<val.length; j++)
					System.out.print(val[j]+" ");
				System.out.println();
				*/
				for(int j=0; j<V.size(); j++){
					if(V.get(j).ref<id)
						temp = temp*V.get(j).npot(val[V.get(j).ref]);
					else if(V.get(j).ref>id)
						temp = temp*V.get(j).npot(val[V.get(j).ref-1]);
					else
						temp = temp*V.get(j).npot(id_val);
				}
				for(int j=0; j<E.size(); j++){
					if(E.get(j).n1.ref==id)
						if(E.get(j).n2.ref<id)
							temp = temp*E.get(j).epot(id_val,val[E.get(j).n2.ref]);
						else
							temp = temp*E.get(j).epot(id_val,val[E.get(j).n2.ref-1]);
					else if(E.get(j).n2.ref==id)
						if(E.get(j).n1.ref<id)
							temp = temp*E.get(j).epot(val[E.get(j).n1.ref],id_val);
						else
							temp = temp*E.get(j).epot(val[E.get(j).n1.ref-1],id_val);
					else
						if(E.get(j).n1.ref<id)
							if(E.get(j).n2.ref<id)
								temp = temp*E.get(j).epot(val[E.get(j).n1.ref],val[E.get(j).n2.ref]);
							else
								temp = temp*E.get(j).epot(val[E.get(j).n1.ref],val[E.get(j).n2.ref-1]);
						else
							if(E.get(j).n2.ref<id)
								temp = temp*E.get(j).epot(val[E.get(j).n1.ref-1],val[E.get(j).n2.ref]);
							else
								temp = temp*E.get(j).epot(val[E.get(j).n1.ref-1],val[E.get(j).n2.ref-1]);
				}
				Z[id_val] = Z[id_val]+temp;
			}
			else{
				val[inx]=i;
				beliefBF_calc(id,id_val,inx+1,V,E,val,Z);
			}
	}
	
	//this is GC
	static void beliefGC(int id, int origGraphSize, ArrayList<Node> V, ArrayList<Edge> E, ArrayList<Node> L){
		int[] condVals = new int[L.size()];
		for(int i=0; i<condVals.length; i++)
			condVals[i]=0;
		double[] Z = new double[V.get(id).Z.length];
		for(int i=0; i<Z.length; i++)
			Z[i]=0.0;
		beliefGC_calc(id,0,origGraphSize,V,E,L,condVals,Z);
		for(int i=0; i<Z.length; i++)
			V.get(id).Z[i] = Z[i];
	}
	static void beliefGC_calc(int id, int inx, int origGraphSize, ArrayList<Node> V, ArrayList<Edge> E, ArrayList<Node> L, int[] condVals, double[] Z){
		if(L.size()==0){
			V.get(id).calcZ(true);
			//System.out.print("\nid "+id);
			for(int j=0; j<Z.length; j++){
				Z[j] = Z[j]+V.get(id).Z[j];
				//System.out.println(j+":"+Z[j]);
			}
		}
		else
			for(int i=0; i<Z.length; i++) //cycle through every possible value of that node
				if(inx==(condVals.length)-1){
					condVals[(condVals.length)-1]=i;
					String cond = "";
					for(int j=0; j<condVals.length; j++)
						cond = cond + String.valueOf(condVals[j]);
					//System.out.println(cond);
					setCond(cond,origGraphSize,V,L);
					V.get(id).calcZ(true);
					//System.out.print("\nid "+id);
					for(int j=0; j<Z.length; j++){
						Z[j] = Z[j]+V.get(id).Z[j];
						//System.out.println(j+":"+Z[j]);
					}
				}
				else{
					condVals[inx]=i;
					beliefGC_calc(id,inx+1,origGraphSize,V,E,L,condVals,Z);
				}
	}
	
	//loads conditioning values based on string of integers into the cutset nodes
	public static void setCond(String cond, int origGraphSize, ArrayList<Node> V, ArrayList<Node> L){
		for(int i=0; i<L.size(); i++)
			L.get(i).val = Integer.parseInt(cond.substring(i,i+1));
		for(int i=origGraphSize; i<V.size(); i++)
			V.get(i).val = V.get(V.get(i).ref).val;
	}
	
	//this is LC
	static void beliefLC(int id, int totalOrigN, ArrayList<Node> V, ArrayList<Edge> E, ArrayList<Node> L){
		int[] val = new int[L.size()];
		for(int i=0; i<val.length; i++)
			val[i]=0;
		double[] Z = new double[V.get(id).Z.length];
		for(int i=0; i<Z.length; i++)
			Z[i]=0.0;
		
		
		
		
		
		for(int i=0; i<Z.length; i++)
			V.get(id).Z[i] = Z[i];
	}
	static void beliefLC_calc(int id, int inx, int totalOrigN, ArrayList<Node> V, ArrayList<Edge> E, ArrayList<Node> L, int[] val, double[] Z){
		for(int i=0; i<Z.length; i++) //cycle through every possible value of that node
			if(inx==(val.length)-1){
				val[(val.length)-1]=i;
				String cond = "";
				for(int j=0; j<val.length; j++)
					cond = cond + String.valueOf(val[j]);
				//System.out.println(cond);
				setCond(cond,totalOrigN,V,L);
				V.get(id).calcZ(true);
				for(int j=0; j<Z.length; j++){
					//System.out.println(V.get(id).Z[j]);
					Z[j] = Z[j]+V.get(id).Z[j];
				}
			}
			else{
				val[inx]=i;
				//conds_calc(id,inx+1,totalOrigN,V,E,L,val,Z);
			}
	}
	
}

//http://algs4.cs.princeton.edu/41undirected/
//http://algs4.cs.princeton.edu/41undirected/Cycle.java.html
