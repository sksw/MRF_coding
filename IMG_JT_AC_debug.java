import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class IMG_JT_AC_debug {

	public static void main(String[] args) {
		
		ArrayList<Edge> E;
		ArrayList<Node> V;
		
		ArrayList<Node> sv;
		
		ArrayList<SuperEdge> SE;
		ArrayList<SuperNode> SV;

		int thickness = 3;
		int range = 2;
		
		Graph G, Gtemp;
		
		System.out.println("---------- image io ----------");
		BufferedImage Test = ImageOp.to_CS_GRAY(ImageOp.loadImage("img","test.png"));
		//ImageOp.printImg(Test,"CS_GRAY");
		G = ImageOp.dec_Graph(Test,"BILEVEL");
		G.make4ptEdges(range);
		System.out.println("<IMPORTED IMAGE INFO> height: "+G.h+" width: "+G.w);
		//outputs imported image
		/*
		for(int i=0;i<G.w;i++){
			System.out.println();
			for(int j=0;j<G.h;j++){
				System.out.print(G.V.get(i*G.h+j).N_VAL + " ");
			}
		}
		ImageOp.saveImage("img","reproduced.png","png",ImageOp.enc_Graph(G,"BILEVEL"));
		*/
		
		/*System.out.println("---------- run length ----------");
		String test = "";
		for(int i=0; i<G.V.size(); i++)
			test = test+G.V.get(i).N_VAL;
		System.out.println("ORIGN: "+G.V.size()+" NEW: "+Coder.RLE(test,8).length());
		//0.45
		*/
		
		
		int strips = 1;
		double totalAvgLen = 0.0;
		for(int stripNum=0; (stripNum+1)*(thickness+1)+1<G.h; stripNum++){
		//for(int stripNum=0; stripNum<1; stripNum++){
			Gtemp = null;
			V = null;
			E = null;
			sv = null;
			SE = null;
			SV = null;
			
			System.out.println("------------------------- GRABBING STRIP #"+stripNum+" -------------------------");
			V = new ArrayList<Node>(0);
			E = new ArrayList<Edge>(0);
			sv = new ArrayList<Node>(0);
			SE = new ArrayList<SuperEdge>(0);
			SV = new ArrayList<SuperNode>(0);
			Graph.getStrip(G.h,G.w,stripNum,thickness,G.V,G.E,V,E);
			Gtemp = new Graph(G.w,thickness,V,E);
			//ImageOp.saveImage("img","strip_"+stripNum+".png","png",ImageOp.enc_Graph(Gtemp,"BILEVEL"));
			
			System.out.println("---------- clustering ----------");
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
			
			System.out.println("---------- clustered graph ----------");
			for(int i=0; i<SV.size(); i++)
				System.out.println(SV.get(i));
			System.out.println();
			for(int i=0; i<SE.size(); i++)
				System.out.println(SE.get(i));
			System.out.println();

			System.out.println("---------- arithmetic encoding JT ----------");
			
			boolean keepGoing = true;
			int startNode=0;
			String PrevObs = "", Obs = "";
			double sum = 0.0, obsProb = 0.0;
			
			int iter = 1;
			double totalLength = 0.0;
			
			SuperNode EncNode = SV.get(startNode);
			EncNode.newItems.addAll(EncNode.V);
			EncNode.newItems.addAll(EncNode.E);
			while(keepGoing){
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
					if(Obs.endsWith(PrevObs+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length()))){
						System.out.println("  --->\t"+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length())+" : "+EncNode.Z[i]/sum);
						obsProb = EncNode.Z[i]/sum;
					}
					else
						System.out.println("\t"+Utilities.intToPBS(i,EncNode.V.size()-PrevObs.length())+" : "+EncNode.Z[i]/sum);
					
					//THIS IS DEBUG CODE
					if( Double.isInfinite(EncNode.Z[i]) || Double.isNaN(EncNode.Z[i]) ){
						keepGoing = false;
						stripNum = 1000;
						System.out.println("-------------------- INFINITY DETECTED --------------------");
						System.out.println("  BELIEFS");
						for(int j=0; j<EncNode.Z.length; j++)
							System.out.println("\t"+Utilities.intToPBS(j,EncNode.V.size()-PrevObs.length())+" : "+EncNode.Z[j]);
						System.out.println("  SELF POTENTIAL");
						for(int j=0; j<EncNode.Z.length; j++)
							System.out.println("\t"+Utilities.intToPBS(j,EncNode.V.size()-PrevObs.length())+" : "+EncNode.npot(PrevObs+Utilities.intToPBS(j,EncNode.V.size()-PrevObs.length()),false));
						System.out.println("  MESSAGES");
						for(int j=0; j<EncNode.dN.size(); j++)
							if(EncNode.dN.get(j).N1 == EncNode)
								for(int k=0; k<EncNode.dN.get(j).m21.length; k++)
									System.out.println("\tm"+k+": "+EncNode.dN.get(j).m21[k]);
							else
								for(int k=0; k<EncNode.dN.get(j).m12.length; k++)
									System.out.println("\tm"+k+": "+EncNode.dN.get(j).m12[k]);
						break;
					}
				}
				
				//use shannon-fano to code i.e. log_2(1/p(x))
				totalLength = totalLength + Coder.log2_1overpx(obsProb);
				
				System.out.println("\tshannon-fano: "+Coder.log2_1overpx(obsProb));
				//System.out.println("\thuffman: "+Coder.huffman(rank,(int)Math.pow(range,thickness)));
				
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
				iter++;
				//if(iterDebug == 5)
					//keepGoing = false;
			}
			System.out.println("AVERAGE LENGTH (" + iter + " supernodes): " + totalLength/(double)iter);
			totalAvgLen = totalAvgLen + totalLength/(double)iter;
			strips++;
		}
		System.out.println("AVERAGE LENGTH TOTAL: " + (totalAvgLen/(double)strips)/(double)thickness);
	
	}

}




