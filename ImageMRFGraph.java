import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ImageMRFGraph extends ImageGraph{
	
	public ArrayList<CliqueStructures.Clique> cliques;
	public ArrayList<CliqueStructures.Clique> cliquesForSearch;
	
	public ImageMRFGraph(int width, int height, int range){
		super(width,height,range);
		cliques = new ArrayList<CliqueStructures.Clique>(0);
		cliquesForSearch = new ArrayList<CliqueStructures.Clique>(0);
	}
	public ImageMRFGraph(int img[][], int range){
		this(img.length,img[0].length,range);
		constructFrom2DIntA(img,range);
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
		updateSearchList();
	}
	
	//make a particular clique for a particular node
	public boolean makeCliques(int x, int y, String clique_name, CliqueStructures.CliquePair clique){
		ArrayList<Node> cliqueNodes;
		if((cliqueNodes=getStructNodes(x,y,clique.geo))!=null){
			CliqueStructures.Clique newClique;
			/*CliqueStructures.Clique newClique = new CliqueStructures.Clique(clique_name,clique.c_pot,cliqueNodes,r);
			if(cliques.contains(newClique))
				return false;*/
			if(clique_name.substring(0,2).equals("N_")){
				img_nodes[x][y].type = clique_name;
				img_nodes[x][y].pot = clique.c_pot;
				if(cliques.contains(img_nodes[x][y]))
					return false;
				return cliques.add(img_nodes[x][y]);
			}
			else if(clique_name.substring(0,2).equals("E_")){
				Edge newEdge = new Edge(clique_name,clique.c_pot,cliqueNodes,r);
				if(cliques.contains(newEdge))
					return false;
				E.add(newEdge);
				return cliques.add(newEdge);
			}
			else{
				newClique = new CliqueStructures.Clique(clique_name,clique.c_pot,cliqueNodes,r);
				if(cliques.contains(newClique))
					return false;
				return cliques.add(newClique);
			}
		}
		else //clique goes out of bounds, don't make one
			return false;
	}
	
	//remove a clique from graph
	public boolean remove(CliqueStructures.Clique clique){
		boolean flag = true;
		flag = flag & cliques.remove(clique);
		flag = flag & cliquesForSearch.remove(clique);
		return flag;
	}
	
	//add any new nodes in clique to cliques for search
	public void updateSearchList(){
		for(CliqueStructures.Clique clique : cliques)
			if(!cliquesForSearch.contains(clique))
				cliquesForSearch.add(clique);
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
	
	//brute force belief calculation
	public double[] belief_BF(SuperNode A){
		//----- get radix
		int r = A.r;
		double[] Z_A = new double[(int)Math.pow(r,A.V.size())];
		Arrays.fill(Z_A,0.0);
		double pot;
		
		//----- for every possible configuration of set A
		String a, a_c, config;
		int a_inx, a_c_inx;
		for(int i=0; i<Z_A.length; i++){
			a = Utilities.toPString(i,A.V.size(),r);
			//----- marginalize over all configurations of A compliment 
			for(int j=0; j<(int)Math.pow(r,V.size()-A.V.size()); j++){
				a_c = Utilities.toPString(j,V.size()-A.V.size(),r);
				//----- construct a configuration over V
				config = "";
				a_inx=0; a_c_inx=0;
				for(int k=0; k<V.size(); k++){ //nodes
					if(A.V.contains(V.get(k))){
						config = config + a.substring(a_inx,a_inx+1);
						a_inx++;
					}
					else{
						config = config + a_c.substring(a_c_inx,a_c_inx+1);
						a_c_inx++;
					}
				}
				//----- calculate potential for given configuration
				pot = 1.0;
				for(CliqueStructures.Clique clique : cliques){
					int [] cliqueVals = new int[clique.nodes.size()];
					int inx;
					for(int k=0; k<clique.nodes.size(); k++){
						inx = V.indexOf(clique.nodes.get(k));
						cliqueVals[k] = Integer.parseInt(config.substring(inx,inx+1),r);
					}
					pot = pot * clique.pot.U(cliqueVals);
					/*
					if(cliqueVals.length>1)
						System.out.println(clique.type+"("+clique.nodes.size()+"): "+cliqueVals[0]+","+cliqueVals[1]+" "+clique.pot.U(cliqueVals));
					else
						System.out.println(clique.type+"("+clique.nodes.size()+"): "+cliqueVals[0]+" "+clique.pot.U(cliqueVals));
					*/
				}
				//System.exit(0);
				//----- construct belief (sum)
				Z_A[i] = Z_A[i]+pot;
				//System.out.println(a+" "+a_c+" "+config+" "+pot+" "+Z_A[i]);
			}
		}
		return Z_A;
	} 
	
	//brute force belief calculation
	public double[] belief_BF(SuperNode A, SuperNode cutset){
		//----- get radix
		int r = A.r;
		double[] Z_A = new double[(int)Math.pow(r,A.V.size())];
		Arrays.fill(Z_A,0.0);
		//----- for every possible configuration of set A
		String a, a_c, config;
		int a_inx, a_c_inx;
		double pot;
		for(int i=0; i<Z_A.length; i++){
			a = Utilities.toPString(i,A.V.size(),r);
			//----- marginalize over all configurations of A compliment minus the nodes in the cutset that are being conditioned on
			for(int j=0; j<(int)Math.pow( r,V.size()-(A.V.size()+cutset.V.size()) ); j++){
				a_c = Utilities.toPString(j,V.size()-(A.V.size()+cutset.V.size()),r);
				//----- construct a configuration over V
				config = "";
				pot = 1.0; a_inx=0; a_c_inx=0;
				for(int k=0; k<V.size(); k++){ //nodes
					if(A.V.contains(V.get(k))){
						a_inx = A.V.indexOf(V.get(k));
						config = config + a.substring(a_inx,a_inx+1);
						//a_inx++;
					}
					else if(cutset.V.contains(V.get(k))){
						config = config + V.get(k).VAL;
					}
					else{
						config = config + a_c.substring(a_c_inx,a_c_inx+1);
						a_c_inx++;
					}
				}
				//----- calculate potential for given configuration
				//System.out.println("----- "+config+" -----");
				for(CliqueStructures.Clique clique : cliques){
					//System.out.print(clique.type+" "+clique);
					if(!cutset.C.contains(clique)){
						int [] cliqueVals = new int[clique.nodes.size()];
						int inx;
						for(int k=0; k<clique.nodes.size(); k++){
							inx = V.indexOf(clique.nodes.get(k));
							cliqueVals[k] = Integer.parseInt(config.substring(inx,inx+1),r);
						}
						//System.out.print("\t"+cliqueVals[0]+","+cliqueVals[1]+" "+clique.pot.U(cliqueVals));
						pot = pot * clique.pot.U(cliqueVals);
					}
					//System.out.println();
				}
				//----- construct belief (sum)
				Z_A[i] = Z_A[i]+pot;
			}
			//System.exit(0);
		}
		return Z_A;
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
