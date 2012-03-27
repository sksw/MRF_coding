import java.util.ArrayList;

public class Graph_img extends Graph{
	
	public int w,h;
	public Node[][] Img;

	public Graph_img(int width, int height){
		super();
		w = width;
		h = height;
		Img = new Node[w][h];
	}
	
	public Graph_img(int img[][], int range){
		this(img.length,img[0].length);
		construct(img,range);
	}
	
	//make edges based on some MRF
	public void makeEdges(MRF mrf){
		mrf.graph_struct.mk_edges(this,mrf.THETA);
	}
	
	//get cutset
	public void getCutset(int row, int spacing, ArrayList<Node> sV, ArrayList<Edge> sE, MRF mrf){
		mrf.graph_struct.getCutset(row, spacing, this, sV, sE);
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
