import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ImageGraph extends Graph{
	
	public int w,h,r;
	public Node[][] img_nodes;
	public ArrayList<CliqueStructures.Clique>[][] cliques; //for MRF's

	public ImageGraph(int width, int height){
		super();
		w = width;
		h = height;
		img_nodes = new Node[w][h];
		cliques = (ArrayList<CliqueStructures.Clique>[][])new ArrayList[w][h];
		for(int x=0; x<w; x++)
			for(int y=0; y<h; y++)
				cliques[x][y] = new ArrayList<CliqueStructures.Clique>(0);
	}
	
	public ImageGraph(int img[][], int range){
		this(img.length,img[0].length);
		constructFrom2DIntA(img,range);
	}
	
	//generate new MRF graph ~75% 0 bilevel
	public void randomGraph(){
		r = 2;
		for(int x=0; x<w; x++)
			for(int y=0; y<h; y++){
				if(Math.random()>0.75)
					img_nodes[x][y] = new Node(x*h+y,1,r);
				else
					img_nodes[x][y] = new Node(x*h+y,0,r);
				V.add(img_nodes[x][y]);
			}
	}
	
	//construct graph from int[][]
	public void constructFrom2DIntA(int[][] img, int r){
		Node Pix;
		for(int x=0; x<w; x++)
			for(int y=0; y<h; y++){
				Pix = new Node(x*h+y,img[x][y],r);
				V.add(Pix);
				img_nodes[x][y] = Pix;
			}
	}
	
	//make an MRF with all cliques (nodes, edges, other cliques)
	public void makeMRF(MRF mrf){
		Iterator< Map.Entry<String,CliqueStructures.CliquePair> > entries = mrf.c_struct.entrySet().iterator();
		while(entries.hasNext()){
			Map.Entry<String,CliqueStructures.CliquePair> entry = entries.next();
			for(int x=0; x<w; x++)
				for(int y=0; y<h; y++)
					makeCliques(x,y,entry.getKey(),entry.getValue());
		}
	}
	
	//make a particular clique for a particular node
	public boolean makeCliques(int x, int y, String clique_name, CliqueStructures.CliquePair clique){
		ArrayList<Node> cliqueNodes;
		Edge tempEdge;
		for(int i=0; i<cliques[x][y].size(); i++)
			if(cliques[x][y].get(i).type.equals(clique_name))
				return false; //clique already exists, don't make new one
		if((cliqueNodes=getStructNodes(x,y,clique.geo))!=null)
			if(clique_name.substring(0,2).equals("N_")){
				img_nodes[x][y].type = clique_name;
				img_nodes[x][y].pot = clique.c_pot;
				return cliques[x][y].add(img_nodes[x][y]);
			}
			else if(clique_name.substring(0,2).equals("E_")){
				E.add(tempEdge = new Edge(clique_name,clique.c_pot,cliqueNodes,r));
				return cliques[x][y].add(tempEdge);
			}
			else
				return cliques[x][y].add(new CliqueStructures.Clique(clique_name,clique.c_pot,cliqueNodes,r));
		else //clique goes out of bounds, don't make one
			return false;
	}
	
	//find the relevant nodes for a specific int[][] graph structure at a given pixel
	public ArrayList<Node> getStructNodes(int x, int y, int[][] struct){
		ArrayList<Node> relevantNodes = new ArrayList<Node>(0);
		int dx, dy;
		for(int i=0; i<struct.length; i++){
			dx = struct[i][0];
			dy = struct[i][1];
			if(x+dx>-1 && y+dy>-1 && x+dx<w && y+dy<h)
				relevantNodes.add(img_nodes[x+dx][y+dy]);
			else
				return null;
		}
		return relevantNodes;
	}
	
	/*
	//hmmm, do i need this
	public boolean removeRedundantCliques(int x, int y){
		for(int i=0; i<cliques[x][y].size(); i++)
			for(int j=0; j<cliques[x][y].size(); j++)
				if(cliques[x][y].get(i).type.equals(cliques[x][y].get(j).type))
					return true;
		return false;
	}
	*/

	//get cutset
	public void getCutset(int row, int spacing, ArrayList<Node> sV, ArrayList<Edge> sE, MRF mrf){
		//mrf.graph_struct.getCutset(row, spacing, this, sV, sE);
	}


	//output graph
	public void print(){
		for(int y=0; y<h; y++)
			for(int x=0; x<w; x++)
				if(x!=w-1)
					System.out.print(V.get(y*w+x).VAL+" ");
				else
					System.out.print(V.get(y*w+x).VAL+"\n");
	}

}
