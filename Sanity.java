import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Sanity {
	public static void main(String[] args){
		
		if(args[0].equals("basic")){
			//HOR = 1.227075;
			//VER = 1.227075;
			
			System.out.println("basic validation tests");
			
			//----- specify MRF
			MRF mrf = new MRF();		
			mrf.n_struct = graph_struct.ROOK1;
			mrf.c_struct = new LinkedHashMap<String, CliqueStructures.CliquePair>();
			mrf.c_struct.put("N_ISING", new CliqueStructures.CliquePair(graph_struct.C_N1, mrf.n_struct, new pot_func.node_ising_pot(0.0)) );
			mrf.c_struct.put("E_VER_ISING", new CliqueStructures.CliquePair(graph_struct.C_E_r1v, mrf.n_struct, new pot_func.edge_ising_pot(0.8)) );
			mrf.c_struct.put("E_HOR_ISING", new CliqueStructures.CliquePair(graph_struct.C_E_r1h, mrf.n_struct, new pot_func.edge_ising_pot(0.8)) );
			//mrf.c_struct.put("E_DIAG_ISING", new CliqueStructures.CliquePair(graph_struct.C_E3, new pot_func.node_ising_pot(0.0)) );
			//mrf.c_struct.put("E_XDIAG_ISING", new CliqueStructures.CliquePair(graph_struct.C_E4, new pot_func.node_ising_pot(0.0)) );
			
			//----- make graph
			ImageMRFGraph g = new ImageMRFGraph(3,4,2);
			g.randomGraph();
			g.makeMRF(mrf);
			g.print();
	
			//----- make acyclic
			Node check_start = g.V.get(0);
			int[] remove = {1,2,3,5,6,7};
			for(int i=0; i<remove.length ;i++){
				g.remove(g.E.get(remove[i]-i));
				g.E.get(remove[i]-i).detach();
				g.E.remove(remove[i]-i);
			}
			if(Graph.checkCyclic(check_start,check_start,check_start)){
				System.out.println("cyclic!");
				System.exit(0);
			}
			
			//----- graph cliques and connections
			System.out.println("----- edges");
			for(Edge edge : g.E)
				System.out.println(edge);
			System.out.println("----- cliques");
			for(CliqueStructures.Clique clique : g.cliques)
				System.out.println(clique.type+": "+clique);
			
			//----- setup supernodes and superdges
			double[] Z;
			int[] nodes = {7,5,9};
			List<Integer> Nodes = new ArrayList<Integer>();
			for(int i=0; i<nodes.length; i++)
				Nodes.add(nodes[i]);
			SuperNode B=new SuperNode(0,2), B_C=new SuperNode(1,2);
			SuperEdge dB;
			for(int i=0; i<g.V.size(); i++)
				if(Nodes.contains(new Integer(i)))
					B.V.add(g.V.get(i));
				else
					B_C.V.add(g.V.get(i));
			B.findInternalCliques(g.cliquesForSearch); B_C.findInternalCliques(g.cliquesForSearch);
			dB = new SuperEdge(B,B_C,g.cliquesForSearch,2);
			
			System.out.println("B: " + B);
			System.out.println("B_C: " + B_C);
			System.out.println("dB: " + dB);
			
			//----- brute force vs belief propagation
			System.out.println("----- supernode brute force");
			Z = g.belief_BF(B);
			Utilities.normalize(Z);
			for(int i=0; i<Z.length; i++)
				System.out.println(Utilities.toPString(i,nodes.length,2)+": "+Z[i]);
			
			System.out.println("----- supernode belief propagation");
			Z = B.Z();
			Utilities.normalize(Z);
			for(int i=0; i<Z.length; i++)
				System.out.println(Utilities.toPString(i,nodes.length,2)+": "+Z[i]);
			
			System.out.println("> BASIC SANITY COMPLETED");
		}
		else if(args[0].equals("advanced")){
			System.out.println("advanced validation tests");
			
			//----- specify MRF
			//Lets make something that favours a vertical ellipse
			MRF mrf = new MRF();		
			mrf.n_struct = graph_struct.DIAMOND2;
			mrf.c_struct = new LinkedHashMap<String, CliqueStructures.CliquePair>();
			mrf.c_struct.put("E_rook1_ver", new CliqueStructures.CliquePair(graph_struct.C_E_r1v, mrf.n_struct, new pot_func.edge_ising_pot(0.2)) );// 1.5
			mrf.c_struct.put("E_rook1_hor", new CliqueStructures.CliquePair(graph_struct.C_E_r1h, mrf.n_struct, new pot_func.edge_ising_pot(0.2)) );// 0.9
			mrf.c_struct.put("E_bishop1_diag", new CliqueStructures.CliquePair(graph_struct.C_E_b1d, mrf.n_struct, new pot_func.edge_ising_pot(0.2)) );// 0.6
			mrf.c_struct.put("E_bishop1_xdiag", new CliqueStructures.CliquePair(graph_struct.C_E_b1x, mrf.n_struct, new pot_func.edge_ising_pot(0.2)) );
			mrf.c_struct.put("E_rook2_ver", new CliqueStructures.CliquePair(graph_struct.C_E_r2v, mrf.n_struct, new pot_func.edge_ising_pot(0.2)) );// 1.2
			mrf.c_struct.put("E_rook2_hor", new CliqueStructures.CliquePair(graph_struct.C_E_r2h, mrf.n_struct, new pot_func.edge_ising_pot(0.2)) );// 0.3

			//----- make graph
			ImageMRFGraph g = new ImageMRFGraph(4,5,2);
			g.randomGraph();
			g.makeMRF(mrf);
			g.print();
			
			System.out.println("MIN RADIUS: "+mrf.minRadius());
			System.out.println("CLIQUES: "+g.cliques.size());
			for(int i=0; i<g.cliques.size(); i++)
				System.out.print(g.cliques.get(i));
			System.out.println();
			
			//---- making supernodes
			SuperNode cutset;
			ArrayList<SuperNode> nodes = new ArrayList<SuperNode>(0);
			ArrayList<SuperEdge> edges = new ArrayList<SuperEdge>(0);
			int cutInx = mrf.minRadius();
			// clustering
			for(int i=0; i*cutInx<g.img_nodes.length; i++){
				SuperNode newNode = new SuperNode(i,2);
				for(int x=0; x<cutInx; x++)
					for(int y=0; y<g.img_nodes[x].length-1; y++)
						newNode.V.add(g.img_nodes[i*cutInx+x][y]);
				newNode.findInternalCliques(g.cliquesForSearch);
				nodes.add(newNode);
			}
			// make bottom row the cutset
			cutset = new SuperNode(-1,2);
			for(int x=0; x<g.img_nodes.length; x++)
				cutset.V.add(g.img_nodes[x][g.img_nodes[x].length-1]);
			cutset.findInternalCliques(g.cliquesForSearch);
			//condition on cutset
			cutset.obs();
			// make edges and condition on cutset
			for(int i=0; i<nodes.size()-1; i++){
				SuperEdge newEdge = new SuperEdge(nodes.get(i),nodes.get(i+1),g.cliquesForSearch,g.r);
				for(CliqueStructures.Clique clique : newEdge.conditionCliques)
					System.out.println("> "+clique);
				newEdge.condition(cutset);
				edges.add(newEdge);
			}
			for(int i=0; i<nodes.size(); i++){
				SuperEdge newEdge = new SuperEdge(nodes.get(i),cutset,g.cliquesForSearch,g.r);
				if(newEdge.C.size()==0)
					newEdge.detach();
				else
					edges.add(newEdge);
			}
			
			System.out.println("CUTSET: "+cutset.V.size()+"|"+cutset.C.size()+" "+cutset);
			for(int i=0; i<nodes.size(); i++)
				System.out.println(nodes.get(i).V.size()+"|"+nodes.get(i).C.size()+" "+nodes.get(i));
			for(int i=0; i<edges.size(); i++)
				System.out.println(edges.get(i).N1Nodes.size()+","+edges.get(i).N2Nodes.size()+"|"+edges.get(i).C.size()+" "+edges.get(i));
			System.out.println("CLIQUES: "+g.cliques.size());
			System.out.println("MISSED CLIQUES: "+g.cliquesForSearch.size());
			for(int i=0; i<g.cliquesForSearch.size(); i++)
				System.out.println("g.cliquesForSearch.get(i)");
			
			double[] Z_BF, Z_BP;
			//----- brute force
			Z_BF = g.belief_BF(nodes.get(0),cutset);
			Utilities.normalize(Z_BF);
			//----- belief propagation
			Z_BP = nodes.get(0).Z();
			Utilities.normalize(Z_BP);
			
			System.out.println(Z_BF.length+" , "+Z_BP.length);
			System.out.println("config   | brute force       \t| belief propagation \t| delta");
			for(int i=0; i<Z_BF.length; i++){
				System.out.print(Utilities.toPString(i,nodes.get(0).V.size(),2)+": ");
				if(Z_BF[i]<10E-5)
					System.out.print("<10E-5          \t ");
				else
					System.out.print(Z_BF[i]+"\t ");
				if(Z_BP[i]<10E-5)
					System.out.print("<10E-5          \t ");
				else
					System.out.print(Z_BP[i]+"\t ");
				if((Z_BF[i]/Z_BP[i])<1E-16)
					System.out.print("N/A");
				else
					System.out.print(Z_BF[i]/Z_BP[i]);
				System.out.println();
			}

			System.out.println("> ADVANCED SANITY COMPLETED");
		}
		
	}
}
