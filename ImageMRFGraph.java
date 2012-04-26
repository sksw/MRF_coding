import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ImageMRFGraph extends ImageGraph{
	
	public ArrayList<CliqueStructures.Clique>[][] cliques;
	
	public ImageMRFGraph(int width, int height){
		super(width,height);
		w = width;
		h = height;
		img_nodes = new Node[w][h];
		cliques = (ArrayList<CliqueStructures.Clique>[][])new ArrayList[w][h];
		for(int x=0; x<w; x++)
			for(int y=0; y<h; y++)
				cliques[x][y] = new ArrayList<CliqueStructures.Clique>(0);
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
	
	//find the nodes related to a specific int[][] graph structure at a given pixel
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
	//hmmm, do i need this?
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

	/*
	public void getCutset(int row, int spacing, ImageGraph G, ArrayList<Node> sV, ArrayList<Edge> sE){
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
					for(int k=0; k<Trg.R; k++) //collapse the self-potential of Src node into Trg node
						Trg.sf[k] = Trg.sf[k] * Src.pot(Src.VAL);
					Link = Trg.findLink(Src);
					if(Link.n1 == Trg)
						for(int v=0; v<Trg.R; v++)
							Trg.sf[v] = Trg.sf[v] * Link.pot(v,Src.VAL);
					else
						for(int v=0; x<Trg.R; v++)	
							Trg.sf[v] = Trg.sf[v] * Link.pot(Src.VAL,v);
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
	*/

}
