import java.util.ArrayList;

public class Sanity_JT_AC {
	public static void main(String[] args){
		ArrayList<Node> V = new ArrayList<Node>(0);
		ArrayList<Edge> E = new ArrayList<Edge>(0);
		
		ArrayList<Node> sv = new ArrayList<Node>(0);
		
		ArrayList<SuperEdge> SE = new ArrayList<SuperEdge>(0);
		ArrayList<SuperNode> SV = new ArrayList<SuperNode>(0);
		
		makeDefaultGraph(V,E);
		
		System.out.println("---------- beliefs BF ----------");
		//brute force calculate - set up string
		String constNodeVal;
		double Z;
		double tot;
		
		System.out.println("[ SUPERNODE 0 ]");
		constNodeVal = "";
		tot = 0.0;
		for(int i=0; i<(int)Math.pow(2,4); i++){
			for(int j=0; j<(int)Math.pow(2,8); j++){
				constNodeVal = Utilities.intToPBS(i,4)+Utilities.intToPBS(j,8);
				tot = tot + belief(constNodeVal,V,E);
			}
		}
		for(int i=0; i<(int)Math.pow(2,4); i++){
			Z = 0.0;
			for(int j=0; j<(int)Math.pow(2,8); j++){
				constNodeVal = Utilities.intToPBS(i,4)+Utilities.intToPBS(j,8);
				Z = Z + belief(constNodeVal,V,E);
			}
			System.out.println("\t\t "+Utilities.intToPBS(i,4)+": "+Z/tot);
		}
		
		System.out.println("[ SUPERNODE 1 ]");
		constNodeVal = "";
		tot = 0.0;
		for(int i=0; i<(int)Math.pow(2,4); i++){
			for(int j=0; j<(int)Math.pow(2,4); j++){
				constNodeVal = "0001"+Utilities.intToPBS(i,4)+Utilities.intToPBS(j,4);
				tot = tot + belief(constNodeVal,V,E);
			}
		}
		for(int i=0; i<(int)Math.pow(2,4); i++){
			Z = 0.0;
			for(int j=0; j<(int)Math.pow(2,4); j++){
				constNodeVal = "0001"+Utilities.intToPBS(i,4)+Utilities.intToPBS(j,4);
				Z = Z + belief(constNodeVal,V,E);
			}
			System.out.println("\t\t "+Utilities.intToPBS(i,4)+": "+Z/tot);
		}
		
		System.out.println("[ SUPERNODE 2 ]");
		constNodeVal = "";
		tot = 0.0;
		for(int i=0; i<(int)Math.pow(2,4); i++){
			constNodeVal = "00010001"+Utilities.intToPBS(i,4);
			tot = tot + belief(constNodeVal,V,E);
		}
		Z = 0.0;
		for(int i=0; i<(int)Math.pow(2,4); i++){
			constNodeVal = "00010001"+Utilities.intToPBS(i,4);
			Z = belief(constNodeVal,V,E);
			System.out.println("\t\t "+Utilities.intToPBS(i,4)+": "+Z/tot);
		}
		
		
		V = new ArrayList<Node>(0);
		E = new ArrayList<Edge>(0);
		//makeDefaultGraph(V,E);
		
		ArrayList<Node> tV = new ArrayList<Node>(0);
		ArrayList<Edge> tE = new ArrayList<Edge>(0);
		makeDefaultGraph(tV,tE);
		
		Graph.getStrip(4,3,0,2,tV,tE,V,E);
		
		System.out.println("---------- beliefs clustering ----------");
		//cluster by columns
		for(int i=0; i<V.size(); i++){
			if(i%2!=0 || i==0)
				sv.add(V.get(i));
			else{
				SV.add(new SuperNode(SV.size(),sv,2));
				sv = new ArrayList<Node>(0);
				sv.add(V.get(i));
			}
		}
		//add last supernode (since loop finishes running before last one is added)
		SV.add(new SuperNode(SV.size(),sv,2));
		//find internal edges
		for(int i=0; i<SV.size(); i++)
			SV.get(i).findInternalEdges(E);
		//find superedges
		for(int i=0; i<SV.size()-1; i++)
			SE.add(new SuperEdge(SV.get(i),SV.get(i+1),E,2));
		
		int startNode=0;
		String PrevObs = "", Obs = "";
		double sum = 0.0;
		
		SuperNode EncNode = SV.get(startNode);
		EncNode.newItems.addAll(EncNode.V);
		EncNode.newItems.addAll(EncNode.E);
		while(true){
			EncNode.calcZ(false,true);
			EncNode.newItems.clear();
			System.out.println("[ ENCODING SUPERNODE "+EncNode.id+" ]");
			System.out.print("  observed value: ");
			PrevObs = Obs;
			Obs = EncNode.observeValues();
			System.out.println(Obs);
			System.out.println("  coding distribution: ");
			
			sum = 0.0;
			for(int i=0; i<EncNode.Z.length; i++)
				sum = sum+EncNode.Z[i];
			System.out.println("\ttotal belief: "+sum);
			
			for(int i=0; i<EncNode.Z.length; i++){
				if(Obs.endsWith(PrevObs+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length())))
					System.out.println("  --->\t"+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length())+" : "+EncNode.Z[i]/sum);
				else
					System.out.println("\t"+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length())+" : "+EncNode.Z[i]/sum);
			}
			System.out.println("\ttotal conditional probability: "+sum);
			
			/*if(new Double(sum).equals(new Double(Double.NaN)))
				break;*/
			
			if(EncNode.dN.size()==0)
				break;
			if ( EncNode.dN.get(0).N1 == EncNode ){
				EncNode.id = EncNode.dN.get(0).N2.id; //inherit id - this is just for output purpose (so we know which node is encoded next)
				EncNode.V.addAll(EncNode.dN.get(0).N2.V); //inherit nodes
				EncNode.newItems.addAll(EncNode.dN.get(0).N2.V);
				EncNode.E.addAll(EncNode.dN.get(0).N2.E); //inherit internal edges
				EncNode.newItems.addAll(EncNode.dN.get(0).N2.E);
				EncNode.dN.get(0).N2.dN.remove(EncNode.dN.get(0)); //remove connecting edge reference at target node 
				EncNode.dN.addAll(EncNode.dN.get(0).N2.dN); //inherit neighbours
				EncNode.E.addAll(EncNode.dN.get(0).E); //add connecting edges to internal edges
				EncNode.newItems.addAll(EncNode.dN.get(0).E);
				for(int j=0; j<EncNode.dN.get(0).N2.dN.size();j++) //reroute superedges with other supernodes
					if(EncNode.dN.get(0).N2.dN.get(j).N1==EncNode.dN.get(0).N2)
						EncNode.dN.get(0).N2.dN.get(j).N1 = EncNode;
					else
						EncNode.dN.get(0).N2.dN.get(j).N2 = EncNode;
				SV.remove(EncNode.dN.get(0).N2);
				SE.remove(EncNode.dN.get(0));
				EncNode.dN.remove(0);
				EncNode.resetBeliefVectorSize(2);
			}
			else{
				EncNode.id = EncNode.dN.get(0).N1.id;
				EncNode.V.addAll(EncNode.dN.get(0).N1.V);
				EncNode.newItems.addAll(EncNode.dN.get(0).N1.V);
				EncNode.E.addAll(EncNode.dN.get(0).N1.E);
				EncNode.newItems.addAll(EncNode.dN.get(0).N1.E);
				EncNode.dN.get(0).N1.dN.remove(EncNode.dN.get(0)); //remove connecting edge reference at target node
				EncNode.dN.addAll(EncNode.dN.get(0).N1.dN);
				EncNode.E.addAll(EncNode.dN.get(0).E);
				EncNode.newItems.addAll(EncNode.dN.get(0).E);
				for(int j=0; j<EncNode.dN.get(0).N1.dN.size();j++)
					if(EncNode.dN.get(0).N1.dN.get(j).N1==EncNode.dN.get(0).N1)
						EncNode.dN.get(0).N1.dN.get(j).N1 = EncNode;
					else
						EncNode.dN.get(0).N1.dN.get(j).N2 = EncNode;
				SV.remove(EncNode.dN.get(0).N1);
				SE.remove(EncNode.dN.get(0));
				EncNode.dN.remove(0);
				EncNode.resetBeliefVectorSize(2);
			}
		}
		
	}
	
	static void makeDefaultGraph(ArrayList<Node> V, ArrayList<Edge> E){
		Graph.make4ptGraph(4,3,2,V,E);
		for(int i=0; i<12; i++){
			if(i%4==3)
				V.get(i).N_VAL = 1;
			else
				V.get(i).N_VAL = 0;
			/*
			 * 0 0 0
			 * 0 0 0
			 * 0 0 0
			 * 1 1 1
			 */
		}
	}
	
	static double belief(String config, ArrayList<Node> V, ArrayList <Edge> E){
		double Z = 1.0;
		for(int i=0; i<V.size(); i++)
			Z = Z*V.get(i).npot(Integer.parseInt(config.substring(i,i+1)));
		for(int i=0; i<E.size(); i++)
			Z = Z*E.get(i).epot( Integer.parseInt( config.substring( E.get(i).n1.id , E.get(i).n1.id+1 ) ),
					Integer.parseInt( config.substring( E.get(i).n2.id , E.get(i).n2.id+1 ) ) );
		return Z;
	}
}
