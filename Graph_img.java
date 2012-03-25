import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Graph_img extends Graph{
	
	public int w,h;

	public Graph_img(int width, int height){
		super();
		w = width;
		h = height;
	}
	
	public Graph_img(int img[][], int range){
		w=img.length;
		h=img[0].length;
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
		for(int i=0; i<w*h; i++)
			if(Math.random()>0.75)
				V.add(new Node(i,1,2));
			else
				V.add(new Node(i,0,2));
	}
	
	//construct graph from int[][]
	public void construct(int[][] img, int r){
		V = new ArrayList<Node>(0);
		E = new ArrayList<Edge>(0);
		for(int x=0; x<w; x++)
			for(int y=0; y<h; y++)
				V.add(new Node(x*h+y,img[x][y],r));
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
