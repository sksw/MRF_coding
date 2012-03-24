import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Graph_img extends Graph{
	
	public static void main(String[] args){
		System.out.println("basic validation tests");
		
		//----- specify MRF
		//0.2 horizontal, 0.8 vertical, with no external field
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("SELF",new pot_func.n_ising(0.0));
		param.put("HOR",new pot_func.e_ising(0.2));
		param.put("VER",new pot_func.e_ising(0.8));
		MRF mrf = new MRF("ising","4pt","NOT IMPLEMENTED YET",param);
		
		//----- make graph
		Graph_img g = new Graph_img(3,4);
		g.randomGraph(mrf);

		//----- make acyclic
		int[] remove = {3,5,6,10,12,13};
		for(int i=0; i<remove.length ;i++){
			g.E.get(remove[i]-i).detach();
			g.E.remove(remove[i]-i);
		}
		if(Graph.checkCyclic(g.V,0,0,0)){
			System.out.println("cyclic!");
			System.exit(0);
		}
		
		//----- graph
		System.out.println("Nodes: " + g.V.size());
		for(int i=0; i<g.V.size() ;i++)
			System.out.println(i+":"+g.V.get(i));
		System.out.println("Edges: " + g.E.size());
		for(int i=0; i<g.E.size() ;i++)
			System.out.println(i+":"+g.E.get(i));
		
		double[] Z;
		//----- nodes & edges test
		int node_test = 4;
		System.out.println("----- BF belief");
		SuperNode A = new SuperNode(-1,2);
		A.V.add(g.V.get(node_test));
		Z = Graph.belief_BF(A,g.V,g.E);
		Utilities.normalize(Z);
		for(int i=0; i<Z.length; i++)
			System.out.println(i+": "+Z[i]);
		System.out.println("----- BP belief");
		g.V.get(node_test).Z();
		Z = g.V.get(node_test).Z;
		for(int i=0; i<Z.length; i++)
			System.out.println(i+": "+Z[i]);
		
		//----- supernodes & superedges test
		int[] nodes = {2,4,11};
		List<Integer> Nodes = new ArrayList<Integer>();
		for(int i=0; i<nodes.length; i++)
			Nodes.add(nodes[i]);
		SuperNode B,B_C;
		SuperEdge dB;
		B = new SuperNode(0,2);
		B_C = new SuperNode(1,2);
		for(int i=0; i<g.V.size(); i++)
			if(Nodes.contains(new Integer(i)))
				B.V.add(g.V.get(i));
			else
				B_C.V.add(g.V.get(i));
		B.resetZ(); B_C.resetZ();
		B.findInternalEdges(g.E); B_C.findInternalEdges(g.E);
		dB = new SuperEdge(B,B_C,g.E,2);
		System.out.println("----- supernode BF belief");
		System.out.println("B: " + B);
		System.out.println("B_C: " + B_C);
		System.out.println("dB: " + dB);
		Z = Graph.belief_BF(B,g.V,g.E);
		Utilities.normalize(Z);
		for(int i=0; i<Z.length; i++)
			System.out.println(Utilities.toPString(i,nodes.length,2)+": "+Z[i]);
		System.out.println("----- supernode BP belief");
		B.Z(true,false);
		Z = B.Z;
		Utilities.normalize(Z);
		for(int i=0; i<Z.length; i++)
			System.out.println(Utilities.toPString(i,nodes.length,2)+": "+Z[i]);
	}
	
	public int w,h;

	public Graph_img(int width, int height){
		super();
		w = width;
		h = height;
	}
	
	//generate new MRF graph ~75% 0 bilevel
	public void randomGraph(MRF mrf){
		V = new ArrayList<Node>(0);
		E = new ArrayList<Edge>(0);
		for(int i=0; i<w*h; i++)
			if(Math.random()>0.75)
				V.add(new Node(i,1,2));
			else
				V.add(new Node(i,0,2));
		makeEdges(mrf);
	}
	
	//make edges based on some MRF
	public void makeEdges(MRF mrf){
		mrf.graph_struct.mk_edges(w,h,V,E,mrf.THETA);
	}
	
	/*
	public void getStrip(int row, int thickness, ArrayList<Node> sV, ArrayList<Edge> sE, Map<String, Object> THETA){
		Node Trg,Src;
		for(int i=0; i<w; i++)
			for(int j=1; j<thickness+1; j++){
				Trg = V.get(i*h+thickness*stripNum+stripNum+j);
				//----- condition on the boundaries unless we are on top of very first strip OR bottom of very last strip
				if(j==1 || j==thickness){
					if(j==0) //condition on top boundary
						Src = V.get(i*h+thickness*stripNum+stripNum+j-1);
					else //condition on bottom boundary
						Src = V.get(i*h+thickness*stripNum+stripNum+j+1);
					for(int k=0; k<Trg.sf.length; k++) //collapse the self-potential of Src node into Trg node
						Trg.sf[k] = Trg.sf[k] * Src.npot(Src.VAL);
					for(int k=0; k<Src.dN.size(); k++) //collapse the edge between Src node and Trg node
						if(Src.dN.get(k).getOtherNode(Src) == Trg){
							if(Src.dN.get(k).n1 == Trg)
								for(int x=0; x<Trg.sf.length; x++)
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(x,Src.VAL);
							else
								for(int x=0; x<Trg.sf.length; x++)	
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(Src.VAL,x);
						}
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
	
	public void getStrip_4pt(int stripNum, int thickness, ArrayList<Node> sV, ArrayList<Edge> sE){
		Node Trg,Src;
		for(int i=0; i<w; i++)
			for(int j=1; j<thickness+1; j++){
				Trg = V.get(i*h+thickness*stripNum+stripNum+j);
				//----- condition on the boundaries unless we are on top of very first strip OR bottom of very last strip
				if(j==1 || j==thickness){
					if(j==0) //condition on top boundary
						Src = V.get(i*h+thickness*stripNum+stripNum+j-1);
					else //condition on bottom boundary
						Src = V.get(i*h+thickness*stripNum+stripNum+j+1);
					for(int k=0; k<Trg.sf.length; k++) //collapse the self-potential of Src node into Trg node
						Trg.sf[k] = Trg.sf[k] * Src.npot(Src.VAL);
					for(int k=0; k<Src.dN.size(); k++) //collapse the edge between Src node and Trg node
						if(Src.dN.get(k).getOtherNode(Src) == Trg){
							if(Src.dN.get(k).n1 == Trg)
								for(int x=0; x<Trg.sf.length; x++)
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(x,Src.VAL);
							else
								for(int x=0; x<Trg.sf.length; x++)	
									Trg.sf[x] = Trg.sf[x] * Src.dN.get(k).epot(Src.VAL,x);
						}
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

}
