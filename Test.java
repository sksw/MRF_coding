import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Test{

	public static void main(String[] args) {
		
		Graph G;
		
		ArrayList<Edge> E;
		ArrayList<Node> V;
		
		ArrayList<Node> sv;
		
		ArrayList<SuperEdge> SE;
		ArrayList<SuperNode> SV;

		int range = 2;
		int thickness;
		int strips;
		double totalAvgLen;
		
		BufferedImage Test = ImageOp.to_CS_GRAY(ImageOp.loadImage("img","test.png"));
		
		for(int m=-1;m<0;m++){
			Constants.HOR = 1.227075 + 0.1*(double)m;
			Constants.VER = 1.227075 + 0.1*(double)m;
	
			System.out.println("----- VER: "+Constants.VER+"\tHOR: "+Constants.HOR+ " -----");
			
			for(int n=5;n<6;n++){
				thickness = n;
				strips = 1;
				totalAvgLen = 0.0;
				
				// ----- import image START
				G = ImageOp.dec_Graph(Test,"BILEVEL");
				G.make4ptEdges(range);
				// ----- import image END
				
				// ----- run encoding through entire image
				for(int stripNum=0; (stripNum+1)*(thickness+1)+1<G.h; stripNum++){
					V = null;
					E = null;
					sv = null;
					SE = null;
					SV = null;
					
					// ----- grab strip and cluster START
					V = new ArrayList<Node>(0);
					E = new ArrayList<Edge>(0);
					sv = new ArrayList<Node>(0);
					SE = new ArrayList<SuperEdge>(0);
					SV = new ArrayList<SuperNode>(0);
					Graph.getStrip(G.h,G.w,stripNum,thickness,G.V,G.E,V,E);
					
					//cluster by columns
					for(int i=0; i<V.size(); i++){
						if(i%thickness!=0 || i==0){
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
					// ----- grab strip and cluster END
		
					// ----- JT algorithm START
					String PrevObs = "";
					String Obs = "";
					double obsProb = 0.0;
					double sum;
					double totalLength = 0.0;
					
					//always encode node 0 - i.e. keep collapsing nodes into super node at node 0
					SuperNode EncNode = SV.get(0);
					
					while(true){
						//calculate probability on node 0
						EncNode.calcZ(false,true); //fresh,markov
		
						//new values become "old" as they are observed
						EncNode.newItems.clear();
						PrevObs = Obs;
						Obs = EncNode.observeValues();
		
						//sum up beliefs
						sum = 0.0;
						for(int i=0; i<EncNode.Z.length; i++)
							sum = sum+EncNode.Z[i];
		
						//find out what was observed and record its probability for rate calulation
						for(int i=0; i<EncNode.Z.length; i++){
							if(Obs.endsWith(PrevObs+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length())))
								obsProb = EncNode.Z[i]/sum;
						}
						
						//rate calculation (shannon-fano to code i.e. log_2(1/p(x)))
						totalLength = totalLength + Coder.log2_1overpx(obsProb);
						
						//collapse
						if(EncNode.dN.size()==0)
							break; //LOOP STOPPING CONDITION HERE
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
					}//while loop running JT on strip
					totalAvgLen = totalAvgLen + totalLength/(double)G.w;
					strips++;
				}//for loop running image segmentation
				// ----- encoding through entire image finishes
				
				System.out.println("spacing: "+thickness+"\taverage length: " + (totalAvgLen/(double)strips)/(double)thickness);
			}//n
		}//m
	
	}

}




