import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sanity {
	public static void main(String[] args){
		System.out.println("basic validation tests");
		
		//----- specify MRF
		//0.2 horizontal, 0.8 vertical, with no external field
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("SELF",new pot_func.node_ising_pot(0.0));
		param.put("HOR",new pot_func.edge_ising_pot(0.2));
		param.put("VER",new pot_func.edge_ising_pot(0.8));
		
		
		
		MRF mrf = new MRF(param);
		
		//----- make graph
		ImageGraph g = new ImageGraph(3,4);
		g.randomGraph();
		g.makeEdges(mrf);
		g.print();

		//----- make acyclic
		Node check_start = g.V.get(0);
		int[] remove = {3,5,6,10,12,13};
		for(int i=0; i<remove.length ;i++){
			g.E.get(remove[i]-i).detach();
			g.E.remove(remove[i]-i);
		}
		if(Graph.checkCyclic(check_start,check_start,check_start)){
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
}
