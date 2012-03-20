import java.util.*;

public class Sanity_LC_AC {

	public static void main(String[] args) {

		ArrayList<Edge> E = new ArrayList<Edge>(0);
		ArrayList<Node> V = new ArrayList<Node>(0);
		ArrayList<Node> L = new ArrayList<Node>(0);
		ArrayList<Edge> E_BF = new ArrayList<Edge>(0);
		ArrayList<Node> V_BF = new ArrayList<Node>(0);
		ArrayList<Node> L_BF = new ArrayList<Node>(0);

		int h = 3;
		int w = 4;
		int range = 2;
		
		//make initial graph
		Graph.make4ptGraph(h,w,range,V,E);
		Graph.make4ptGraph(h,w,range,V_BF,E_BF);
		System.out.println("---------- orig graph ----------");
		for(int i=0; i<V.size(); i++)
			System.out.print(V.get(i));
		System.out.println();
		for(int i=0; i<E.size(); i++)
			System.out.print(E.get(i));
		System.out.println();
		System.out.println("Cyclic: " + Graph.checkCyclic(V,E,0,0,0));
		
		//make the edge cover
		L=Graph.getEdgeCover4pt(h,w,V,E);
		//Use this in a 2x2 case to make an acyclic graph to test GC and BF arithmetic encoding - results are consistent
		/*E.get(1).n1.dN.remove(E.get(1));
		E.get(1).n2.dN.remove(E.get(1));
		E.remove(1);
		E_BF.get(1).n1.dN.remove(E_BF.get(1));
		E_BF.get(1).n2.dN.remove(E_BF.get(1));
		E_BF.remove(1);*/
		System.out.println("---------- edge cover ----------");
		for(int i=0; i<V.size(); i++)
			System.out.print(V.get(i));
		System.out.println();
		for(int i=0; i<E.size(); i++)
			System.out.print(E.get(i));
		System.out.println();
		System.out.println("Cyclic: " + Graph.checkCyclic(V,E,0,0,0));
		
		System.out.println("---------- cutset nodes ----------");
		for(int i=0; i<L.size(); i++)
			System.out.print(L.get(i));
		System.out.println();
		
		System.out.println("---------- updating graph ----------");
		Graph.clean(V,E); //something is wrong with relevant nodes function
		
		System.out.println("---------- Assigning dummy values to graph ----------");
		Random generator = new Random();
		int nodeVal;
		for(int i=0; i<w*h; i++){
			nodeVal = generator.nextInt(2);
			V.get(i).N_VAL = nodeVal;
			V_BF.get(i).N_VAL = nodeVal;
			System.out.print(nodeVal + " ");
		}
		System.out.println();
		//update values to copy nodes
		Graph.updateCopies(w*h,V,E);
		
		//---------- reference page 100,103,170 ----------
		
		System.out.println("---------- arithmetic encoding GC ----------");
		/*
		1. encode root node
		2. YANK MESSAGE VALUES INTO MATRIX? <-- do this when LC has been implemented
		3. "collapse" next node into previous set - via edge potential and self potentials (can do this iteratively)
		4. calculate set belief by pulling messages
		5. divide by previous set belief
		6. repeat 3-5 until entire graph traversed
		- if node is in cutset, then other copy nodes become fixed too
		- self potential for all other values become zero
		- message sum becomes a single term <-- if algorithm relying on existing messages in tree,
			then some messages need to be recomputed
		
		- need a collapse function to merge nodes and relabel node id's?
		- need to traverse graph in a way as verifiable with BF, numerical order?
		*/
		
		//start on node nextNodeRef
		int nextNodeRef = 0;
		int prevNodeRef = 0;
		int counter = 0;
		int encN = 0;
		boolean newN = true;
		int enc_order[] = new int[w*h]; //using this to track order of nodes encoded to repeat in brute force mode
		
		do{
			System.out.print("<iteration "+counter+">");
			if(newN)
				enc_order[encN] = V.get(nextNodeRef).N_ID;
			
			//Get beliefs to form coding distribution
			Graph.beliefGC(nextNodeRef,w*h-encN,V,E,L);
			System.out.print(" beliefs ");
			for(int j=0; j<range; j++)
				System.out.print(" "+j+"#"+V.get(nextNodeRef).Z[j]);
			
			//Encode
			if(V.get(nextNodeRef).observed){
				System.out.println(" > encoding [n/a] COPY ALREADY ENCODED");
			}
			else{
				System.out.println(" > encoding ["+V.get(nextNodeRef).N_VAL+"] --> node ("+enc_order[encN]+")"+V.get(nextNodeRef));
				V.get(nextNodeRef).observe();
				encN++;
			}
			
			if(V.size()==1)
				V.clear();
			else{
				//Pick first neighbour to go to next
				prevNodeRef = nextNodeRef;
				nextNodeRef = V.get(prevNodeRef).dN.get(0).getOtherNode(prevNodeRef);
				nextNodeRef = Graph.collapse(V.get(prevNodeRef),V.get(nextNodeRef),V,E,L);
				if(!V.get(nextNodeRef).observed)
					newN = true;
				else
					newN = false;
				
				// display graph information for debugging
				/*
				System.out.println("next node "+nextNodeRef);
				System.out.print("V: ");
				for(int i=0; i<V.size(); i++)
					System.out.print(V.get(i));
				System.out.println();
				System.out.print("E: ");
				for(int i=0; i<E.size(); i++)
					System.out.print(E.get(i));
				System.out.println();
				System.out.print("C: ");
				for(int i=0; i<L.size(); i++)
					System.out.print(L.get(i));
				System.out.println();
				*/
				counter++;
			}
		}while(V.size()!=0);
		
		System.out.println("---------- encoding enumeration ----------");
		for(int i=0; i<enc_order.length; i++)
			System.out.print(" "+enc_order[i]);
		System.out.println();
		
		System.out.println("---------- arithmetic encoding BF ----------");
		/*
		1. brute force root node belief
		2. "collapse" next node into previous node to form super node
			"drag" all edges to super node
		3. brute force super node belief
		4. divide by previous belief
		5. repeat 3-5 until entire graph traversed
		*/
		nextNodeRef = enc_order[0];
		prevNodeRef = 0;
		for(int i=0; i<enc_order.length; i++){
			System.out.print("<iteration "+i+">");
			Graph.beliefBF(w*h-i,nextNodeRef,V_BF,E_BF);
			System.out.print(" beliefs ");
			for(int j=0; j<range; j++)
				System.out.print(" "+j+"#"+V_BF.get(nextNodeRef).Z[j]);
			System.out.println(" > encoding ["+V_BF.get(nextNodeRef).N_VAL+"] --> node ("+V_BF.get(nextNodeRef).N_ID+")"+V_BF.get(nextNodeRef));
			V_BF.get(nextNodeRef).observe();
			if(V_BF.size()==1)
				V_BF.clear();
			else{
				prevNodeRef = nextNodeRef;
				for(int j=0; j<V_BF.get(prevNodeRef).dN.size(); j++)
					if(V_BF.get(prevNodeRef).dN.get(j).getOtherNode(V_BF.get(prevNodeRef)).N_ID==enc_order[i+1]){
						nextNodeRef = V_BF.get(prevNodeRef).dN.get(j).getOtherNode(prevNodeRef);
						break;
					}
				nextNodeRef = Graph.collapse(V_BF.get(prevNodeRef),V_BF.get(nextNodeRef),V_BF,E_BF,L_BF);
				// display graph information for debugging
				/*
				System.out.println("next node "+nextNodeRef);
				System.out.print("V_BF: ");
				for(int k=0; k<V_BF.size(); k++)
					System.out.print(V_BF.get(k));
				System.out.println();
				System.out.print("E_BF: ");
				for(int k=0; k<E_BF.size(); k++)
					System.out.print(E_BF.get(k));
				System.out.println();
				System.out.print("C_BF: ");
				for(int k=0; k<L_BF.size(); k++)
					System.out.print(L_BF.get(k));
				System.out.println();
				*/
			}
		}
	}
}




