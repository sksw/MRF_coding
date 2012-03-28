import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ImageGraph extends Graph{

	public static void main(String[] args){

	}
	
	public int w,h;
	public Node[][] Img;

	public ImageGraph(int width, int height){
		super();
		w = width;
		h = height;
		Img = new Node[w][h];
	}
	
	public ImageGraph(int img[][], int range){
		this(img.length,img[0].length);
		construct(img,range);
	}
	
	//generate new MRF graph ~75% 0 bilevel
	public void randomGraph(){
		V = new ArrayList<Node>(0);
		E = new ArrayList<Edge>(0);
		for(int x=0; x<w; x++)
			for(int y=0; y<h; y++){
				if(Math.random()>0.75)
					Img[x][y] = new Node(x*h+y,1,2);
				else
					Img[x][y] = new Node(x*h+y,0,2);
				V.add(Img[x][y]);
			}
	}
	
	//construct graph from int[][]
	public void construct(int[][] img, int r){
		V = new ArrayList<Node>(0);
		E = new ArrayList<Edge>(0);
		Node Pix;
		for(int x=0; x<w; x++)
			for(int y=0; y<h; y++){
				Pix = new Node(x*h+y,img[x][y],r);
				V.add(Pix);
				Img[x][y] = Pix;
			}
	}
	
	//make edges based on some MRF
	public void makeEdges(MRF mrf){
		if(E.size()!=0)
			System.out.println("warning! populating non-empty edge set");
		Iterator< Map.Entry<String,Clique.CliquePair> > entries = mrf.c_struct.entrySet().iterator();
		while(entries.hasNext()){
			Map.Entry<String,Clique.CliquePair> entry = entries.next();
			if(entry.getKey().substring(0,2).equals("E_")){
				
			}
		}
	}
	
	public void makeCliques(MRF mrf){
		
	}
	
	public void attachClique(Clique.CliquePair clique){
		int[] struct = new int[2];
		for(int i=0; i<clique.geo.length; i++){
			for(int j=0; j<clique.geo[i].length; j++){
				struct[j] = clique.geo[i][j];
			}
			System.out.println(struct[0]+" "+struct[1]);
		}
	}
	
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
