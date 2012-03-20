
import java.util.ArrayList;

public class Simple_EdgeCoverConditioning {

	public static void main(String[] args) {
		ArrayList<Edge> E = new ArrayList<Edge>(0);
		ArrayList<Node> V = new ArrayList<Node>(0);
		ArrayList<Node> L = new ArrayList<Node>(0);
		ArrayList<Edge> E_BF = new ArrayList<Edge>(0);
		ArrayList<Node> V_BF = new ArrayList<Node>(0);

		int h = 4;
		int w = 5;
		int range = 2;

		//make initial graph
		Graph.make4ptGraph(h,w,range,V,E);
		//make initial graph for brute force
		Graph.make4ptGraph(h,w,range,V_BF,E_BF);
		
		//output nodes and edges
		for(int i=0; i<V.size(); i++)
			System.out.print(V.get(i));
		System.out.println();
		for(int i=0; i<E.size(); i++)
			System.out.print(E.get(i));
		System.out.println();
		System.out.println("Cyclic: " + Graph.checkCyclic(V,E,0,0,0));
		
		//make the edge cover
		L=Graph.getEdgeCover4pt(h,w,V,E);
		
		//output nodes and edges
		for(int i=0; i<V.size(); i++)
			System.out.print(V.get(i));//+""+V.get(i).L+" "
		System.out.println();
		for(int i=0; i<E.size(); i++)
			System.out.print(E.get(i));
		System.out.println();
		System.out.println("Cyclic: " + Graph.checkCyclic(V,E,0,0,0));
		
		//output cutset nodes
		for(int i=0; i<L.size(); i++)
			System.out.print(L.get(i));//+""+V.get(i).L+" "
		System.out.println();
		
		//conditioning
		//System.out.println(L.size());
		//Graph.setCond("00001111",h*w,V,L);
		
		//BP on edge cover
		System.out.println("---------- beliefs GC ----------");
		//for(int i=0; i<w*h; i++)
		//	V.get(i).calcZ();

		for(int i=0; i<w*h; i++)
			Graph.beliefGC(i,h*w,V,E,L);
		for(int i=0; i<w*h; i++){
			System.out.print("<node "+i+":\t\t");
			for(int j=0; j<range; j++)
				System.out.print(" "+j+"#"+V.get(i).Z[j]);
			System.out.println(">");
		}
		
		//brute force on cyclic graph
		System.out.println("---------- beliefs BF ----------");
		for(int i=0; i<w*h; i++)
			Graph.beliefBF(w*h,i,V_BF,E_BF);
		for(int i=0; i<w*h; i++){
			System.out.print("<node "+i+":\t\t");
			for(int j=0; j<range; j++)
				System.out.print(" "+j+"#"+V_BF.get(i).Z[j]);
			System.out.println(">");
		}
	}
}