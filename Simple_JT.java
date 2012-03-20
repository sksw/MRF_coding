
import java.util.ArrayList;
import java.util.Random;

public class Simple_JT {

	public static void main(String[] args) {

		ArrayList<Edge> E = new ArrayList<Edge>(0);
		ArrayList<Node> V = new ArrayList<Node>(0);
		
		ArrayList<Node> sv = new ArrayList<Node>(0);
		
		ArrayList<SuperEdge> SE = new ArrayList<SuperEdge>(0);
		ArrayList<SuperNode> SV = new ArrayList<SuperNode>(0);

		int h = 2;
		int w = 600;
		int range = 2;
		
		//make initial graph
		Graph.make4ptGraph(h,w,range,V,E);
		System.out.println("---------- orig graph ----------");
		for(int i=0; i<V.size(); i++)
			System.out.print(V.get(i));
		System.out.println();
		for(int i=0; i<E.size(); i++)
			System.out.print(E.get(i));
		System.out.println();
		System.out.println("Cyclic: " + Graph.checkCyclic(V,E,0,0,0));
		
		System.out.println("---------- Assigning dummy values to graph ----------");
		Random generator = new Random();
		int nodeVal;
		for(int i=0; i<w*h; i++){
			nodeVal = generator.nextInt(2);
			V.get(i).N_VAL = nodeVal;
			System.out.print(nodeVal + " ");
		}
		System.out.println();
		
		System.out.println("---------- clustering ----------");
		//cluster by columns
		for(int i=0; i<V.size(); i++){
			if(i%h!=0 || i==0){
				sv.add(V.get(i));
			}
			else{
				SV.add(new SuperNode(SV.size(),sv,range));
				sv = null;
				sv = new ArrayList<Node>(0);
				sv.add(V.get(i));
			}
		}
		//add last supernode (since loop finishes running before last one is added)
		SV.add(new SuperNode(SV.size(),sv,range));
		
		//find internal edges
		for(int i=0; i<SV.size(); i++)
			SV.get(i).findInternalEdges(E);
		//find superedges
		for(int i=0; i<SV.size()-1; i++)
			SE.add(new SuperEdge(SV.get(i),SV.get(i+1),E,range));
		
		System.out.println("---------- clustered graph ----------");
		for(int i=0; i<SV.size(); i++)
			System.out.println(SV.get(i));
		System.out.println();
		for(int i=0; i<SE.size(); i++)
			System.out.println(SE.get(i));
		System.out.println();
		
		System.out.println("---------- arithmetic encoding JT ----------");
		
		boolean justStarted = true;
		int startNode=0;
		String PrevObs = "", Obs = "";
		double prevObsBelief = 1.0, newObsBelief = 1.0;
		double sum = 0.0;
		
		SuperNode EncNode = SV.get(startNode);
		while(true){
			EncNode.calcZ(false,false);
			System.out.println("[ ENCODING SUPERNODE "+EncNode.id+" ]");
			System.out.print("  observed value: ");
			PrevObs = Obs;
			Obs = EncNode.observeValues();
			System.out.println(Obs);
			System.out.println("  coding distribution: ");
			prevObsBelief = newObsBelief;
			if(justStarted){
				prevObsBelief = 0.0;
				for(int i=0; i<EncNode.Z.length; i++)
					prevObsBelief = prevObsBelief+EncNode.Z[i];
				justStarted = false;
			}
			sum = 0.0;
			for(int i=0; i<EncNode.Z.length; i++){
				if(Obs.endsWith(PrevObs+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length()))){
					//System.out.println("  --->\t"+PrevObs+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length())+" : "+EncNode.Z[i]/prevObsBelief);
					System.out.println("  --->\t"+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length())+" : "+EncNode.Z[i]/prevObsBelief);
					newObsBelief = EncNode.Z[i];
					sum = sum + EncNode.Z[i]/prevObsBelief;
				}
				else{
					//System.out.println("\t"+PrevObs+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length())+" : "+EncNode.Z[i]/prevObsBelief);
					System.out.println("\t"+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length())+" : "+EncNode.Z[i]/prevObsBelief);
					sum = sum + EncNode.Z[i]/prevObsBelief;
				}
			}
			System.out.println("\ttotal: "+sum);
			
			if(new Double(sum).equals(new Double(Double.NaN)))
				break;
			
			if(EncNode.dN.size()==0)
				break;
			if ( EncNode.dN.get(0).N1 == EncNode ){
				EncNode.id = EncNode.dN.get(0).N2.id; //inherit id - this is just for output purpose (so we know which node is encoded next)
				EncNode.V.addAll(EncNode.dN.get(0).N2.V); //inherit nodes
				EncNode.E.addAll(EncNode.dN.get(0).N2.E); //inherit internal edges
				EncNode.dN.get(0).N2.dN.remove(EncNode.dN.get(0)); //remove connecting edge reference at target node 
				EncNode.dN.addAll(EncNode.dN.get(0).N2.dN); //inherit neighbours
				EncNode.E.addAll(EncNode.dN.get(0).E); //add connecting edges to internal edges
				for(int j=0; j<EncNode.dN.get(0).N2.dN.size();j++) //reroute superedges with other supernodes
					if(EncNode.dN.get(0).N2.dN.get(j).N1==EncNode.dN.get(0).N2)
						EncNode.dN.get(0).N2.dN.get(j).N1 = EncNode;
					else
						EncNode.dN.get(0).N2.dN.get(j).N2 = EncNode;
				SV.remove(EncNode.dN.get(0).N2);
				SE.remove(EncNode.dN.get(0));
				EncNode.dN.remove(0);
				EncNode.resetBeliefVectorSize(range);
			}
			else{
				EncNode.id = EncNode.dN.get(0).N1.id;
				EncNode.V.addAll(EncNode.dN.get(0).N1.V);
				EncNode.E.addAll(EncNode.dN.get(0).N1.E);
				EncNode.dN.get(0).N1.dN.remove(EncNode.dN.get(0)); //remove connecting edge reference at target node
				EncNode.dN.addAll(EncNode.dN.get(0).N1.dN);
				EncNode.E.addAll(EncNode.dN.get(0).E);
				for(int j=0; j<EncNode.dN.get(0).N1.dN.size();j++)
					if(EncNode.dN.get(0).N1.dN.get(j).N1==EncNode.dN.get(0).N1)
						EncNode.dN.get(0).N1.dN.get(j).N1 = EncNode;
					else
						EncNode.dN.get(0).N1.dN.get(j).N2 = EncNode;
				SV.remove(EncNode.dN.get(0).N1);
				SE.remove(EncNode.dN.get(0));
				EncNode.dN.remove(0);
				EncNode.resetBeliefVectorSize(range);
			}
		}
		
		/*System.out.println("---------- image io ----------");
		BufferedImage Test = ImageOp.to_CS_GRAY(ImageOp.loadImage("img","test.png"));
		//ImageOp.printImg(Test,"CS_GRAY");
		Graph G = ImageOp.dec_Graph(Test,"BILEVEL");
		for(int i=0;i<G.w;i++){
			System.out.println();
			for(int j=0;j<G.h;j++){
				System.out.print(G.V.get(i*G.h+j).N_VAL + " ");
			}
		}*/
		
		//ImageOp.saveImage("img","test_out.png","png",ImageOp.enc_Graph(G,"BILEVEL"));
	}

}




