
import java.util.ArrayList;

public class Simple_BP {

	public static void main(String[] args) {
		ArrayList<Edge> E = new ArrayList<Edge>(0);
		ArrayList<Node> V = new ArrayList<Node>(0);
		Edge newE;
		
		int len = 5;
		int range = 2;
		
		//line of 5 nodes
			/*for(int i=0; i<len; i++){
			V.add(new Node(i,range));
			if(i>0){
				E.add(newE = new Edge(i-1,i,V.get(i-1),V.get(i),range));
				V.get(i).dN.add(newE);
				V.get(i-1).dN.add(newE);
			}
		}*/
		
		//tree of 5 nodes (0:2)(1:3)(2:1)(3:1)(4:1) , (0,1)(0,2)(1,3)(1,4)
		for(int i=0; i<len; i++){
			V.add(new Node(i,range));
			if(i==1 || i==2){
				E.add(newE = new Edge(V.get(0),V.get(i),range));
				V.get(i).dN.add(newE);
				V.get(0).dN.add(newE);
			}
			else if(i==3 || i==4){
				E.add(newE = new Edge(V.get(1),V.get(i),range));
				V.get(i).dN.add(newE);
				V.get(1).dN.add(newE);
			}
		}
		
		
		//checks nodes
		for(int i=0; i<len; i++)
			System.out.print(V.get(i));
		System.out.println();
		//checks edges
		for(int i=0; i<len-1; i++)
			System.out.print(E.get(i));
		System.out.println();
		//checks edge and node references
		for(int i=0; i<len; i++){
			System.out.print("node "+i+" edges: ");
			for(int j=0; j<V.get(i).dN.size(); j++){
				System.out.print(V.get(i).dN.get(j));
				System.out.print(V.get(i).dN.get(j).n1.id + "," + V.get(i).dN.get(j).n2.id);
			}
			System.out.println();
		}
		
		//BP!
		int testnode = 2; //this is the "root" index
		V.get(testnode).calcZ();
		//results
		System.out.println("--- beliefs BP ---");
		for(int i=0; i<range; i++)
			System.out.println(i+":"+V.get(testnode).Z[i]);
		
		/******************************* NOTE ******************************
		Brute force method in class graph
		*******************************************************************/
		//Brute force test!
		System.out.println("--- beliefs BF-test ---");
		double temp = 0.0;
		double[] beliefs = new double[2];
		for(int i=0; i<2; i++)
			beliefs[i] = 0.0;
		//need V.size()-1 nested for loops
		//use recursive for loop with an array containing current pixel values
		//(from previous loops)
		for(int k=0; k<2; k++){
			for(int i=0; i<2; i++)
				for(int j=0; j<2; j++)
					for(int x=0; x<2; x++)
						for(int y=0; y<2; y++)
						{
							temp = V.get(0).npot(i)*V.get(1).npot(j)*V.get(2).npot(k)*V.get(3).npot(x)*V.get(4).npot(y)
									//line of 5 nodes
									//*E.get(0).epot(i,j)*E.get(1).epot(j,k)*E.get(2).epot(k,x)*E.get(3).epot(x,y);
									//tree of 5 nodes
									*E.get(0).epot(i,j)*E.get(1).epot(i,k)*E.get(2).epot(j,x)*E.get(3).epot(j,y);
							beliefs[k] = beliefs[k]+temp;
						}
		}
		//results
		for(int i=0; i<range; i++)
			System.out.println(i+":"+beliefs[i]);

		//new brute force method
		System.out.println("--- beliefs BF-new ---");
		Graph.beliefBF(V.size(),testnode,V,E);
		//results
		for(int i=0; i<range; i++)
			System.out.println(i+":"+beliefs[i]);
	}
}

