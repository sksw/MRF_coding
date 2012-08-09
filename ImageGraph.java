import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ImageGraph extends Graph{
	
	public int w,h,r;
	public Node[][] img_nodes;

	//default constructor
	public ImageGraph(int width, int height, int range){
		super();
		w = width;
		h = height;
		r = range;
		img_nodes = new Node[w][h];
	}
	//construct graph from 2D int[][]
	public ImageGraph(int img[][], int range){
		this(img.length,img[0].length,range);
		constructFrom2DIntA(img,range);
	}
	
	//generate new graph ~75% 0 bilevel
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
	
	//create graph from int[][]
	public void constructFrom2DIntA(int[][] img, int r){
		Node Pix;
		for(int x=0; x<w; x++)
			for(int y=0; y<h; y++){
				Pix = new Node(x*h+y,img[x][y],r);
				V.add(Pix);
				img_nodes[x][y] = Pix;
			}
	}
	
	//output graph
	public void print(){
		for(int y=0; y<h; y++)
			for(int x=0; x<w; x++)
				if(x!=w-1)
					System.out.print(img_nodes[x][y].VAL+" ");
				else
					System.out.print(img_nodes[x][y].VAL+"\n");
	}

}
