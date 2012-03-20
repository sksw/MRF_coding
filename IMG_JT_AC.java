import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class IMG_JT_AC{

	public static void main(String[] args) throws Exception{
		JT_AC("img/sample_images/misc","EQ",9,5);
	}
	
	public static double[] readTheta(String dir, String name, String mode) throws Exception{
		FileReader fIn = new FileReader(dir + "/" + name);
		Scanner scan = new Scanner(fIn);
		double[] theta;
		
		if(mode.equals("EQ"))
			theta = new double[1];
		else if(mode.equals("VH"))
			theta = new double[2];
		else if(mode.equals("VHS"))
			theta = new double[3];
		else
			theta = new double[1];
		
		while(scan.findInLine(">"+mode+": ") == null)
			scan.nextLine();
		scan.useDelimiter(",");
		for(int i=0; i<theta.length; i++)
			if(scan.hasNext())
				theta[i] = scan.nextDouble();
			else
				theta[i] = Double.NaN;
		return theta;
	}
	
	public static void JT_AC(String dir, String mode, int testRange, int maxSpacing) throws Exception{
		File fDir = new File(dir);
		String[] Images;
		//filter list of returned files for *.png's
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".png");
		    }
		};
		Images = fDir.list(filter);

		BufferedImage img;
		double[] theta;
		double[] test_theta;
		String d_theta;
		String data;
		
		for(int i=0; i<Images.length; i++){
			img = ImageOp.to_CS_GRAY(ImageOp.loadImage(dir,Images[i]));
			theta = readTheta(dir,Images[i].replaceFirst("[.][^.]+$","_info.txt"),mode);
			test_theta = new double[theta.length];
			FileWriter fOut = new FileWriter(dir+"/"+Images[i].replaceFirst("[.][^.]+$","_data.txt"));
			BufferedWriter out = new BufferedWriter(fOut);
			//loop for testing a wide range of theta's
			for(int d=0; d<Math.pow(testRange,theta.length); d++){
				data = "";
				d_theta = Integer.toString(d,testRange);//represent d in base_testRange
				//use the jth digit of d_theta to find out how much to add/subtract from jth theta term
				for(int j=0; j<theta.length; j++){
					test_theta[j] = theta[j] + 0.1*(Integer.parseInt(d_theta.substring(j,j+1),testRange)-testRange/2);
					if(j==theta.length-1)
						data = data + test_theta[j] + ":";
					else
						data = data + test_theta[j] + ",";
				}
				//test with different column spacing
				for(int n=2; n<=maxSpacing; n++){
					System.out.println(Images[i]+" "+d+" "+n);
					if(n==maxSpacing)
						data = data + JT_AC_img(img,n,test_theta,mode) + "\r\n";
					else
						data = data + JT_AC_img(img,n,test_theta,mode) + ",";
				}
				System.out.print(data);
				out.write(data);
			}//d
			out.close();
		}
	}
		
	public static double JT_AC_img(BufferedImage img, int col_spacing, double[] theta, String mode){
		int strips = 1;
		double totalStripAvgLen = 0.0;
		ArrayList<Edge> E; ArrayList<Node> V;
		ArrayList<Node> sv;
		ArrayList<SuperEdge> SE; ArrayList<SuperNode> SV;
		
		// ----- convert image to graph
		Graph G = ImageOp.dec_Graph(img,"BILEVEL");
		G.make4ptEdges(2,theta,mode);
		// ----- encode strip by strip
		for(int stripNum=0; (stripNum+1)*(col_spacing+1)+1<G.h; stripNum++){
			//reset node/edge lists
			V = null; E = null;
			sv = null;
			SE = null; SV = null;
			
			// ----- grab strip and cluster START
			V = new ArrayList<Node>(0); E = new ArrayList<Edge>(0);
			sv = new ArrayList<Node>(0);
			SE = new ArrayList<SuperEdge>(0); SV = new ArrayList<SuperNode>(0);
			//populates V and E with nodes/edges from strip (conditioned on upper and lower bounds)
			G.getStrip(stripNum,col_spacing,V,E);
			//cluster by columns
			for(int i=0; i<V.size(); i++){
				if(i%col_spacing!=0 || i==0){
					sv.add(V.get(i));
				}
				else{
					SV.add(new SuperNode(SV.size(),sv,2));
					sv = null;
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
			// ----- grab strip and cluster END

			// ----- encode supernode and record information about length
			totalStripAvgLen = totalStripAvgLen + encode_strip(SV,SE)/(double)G.w;
			strips++;
			System.out.print(".");
		}
		// ----- encoding through entire image finishes
		System.out.println();
		return (totalStripAvgLen/(double)strips)/(double)col_spacing;
	}
	
	public static double encode_strip(ArrayList<SuperNode> SV, ArrayList<SuperEdge> SE){
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
		return totalLength;
	}
	
	public static void JT_AC_single_image_test(){
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




