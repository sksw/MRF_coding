import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;
import java.util.LinkedHashMap;

public class Encoder {
	
	//0.07423857003201752
	// param 0 E_rook1_ver: 	 0.9874078155960595
	// param 1 E_rook1_hor: 	 1.1093909472631103
	// param 2 E_bishop1_diag: 	 0.4876016546382432
	// param 3 E_bishop1_xdiag:  0.31633943895052447
	// param 4 E_rook2_ver: 	-0.13196602767754476
	// param 5 E_rook2_hor: 	-0.06828063646179837
	
	public static void main(String[] Args) throws Exception{
		
		MRF mrf = new MRF();
		mrf.n_struct = graph_struct.DIAMOND2;
		mrf.c_struct = new LinkedHashMap<String, CliqueStructures.CliquePair>();
		//set default 0.5
		mrf.c_struct.put("E_rook1_ver", new CliqueStructures.CliquePair(graph_struct.C_E_r1v, mrf.n_struct, new pot_func.edge_ising_pot(1.010113211612499)) );
		mrf.c_struct.put("E_rook1_hor", new CliqueStructures.CliquePair(graph_struct.C_E_r1h, mrf.n_struct, new pot_func.edge_ising_pot(0.9952083160883871)) );
		mrf.c_struct.put("E_bishop1_diag", new CliqueStructures.CliquePair(graph_struct.C_E_b1d, mrf.n_struct, new pot_func.edge_ising_pot(0.31298388730425897)) );
		mrf.c_struct.put("E_bishop1_xdiag", new CliqueStructures.CliquePair(graph_struct.C_E_b1x, mrf.n_struct, new pot_func.edge_ising_pot(0.30809611657808794)) );
		
		//9.403808662565948E-16
		
		mrf.c_struct.put("E_rook2_ver", new CliqueStructures.CliquePair(graph_struct.C_E_r2v, mrf.n_struct, new pot_func.edge_ising_pot(-0.09722248023796966)) );
		mrf.c_struct.put("E_rook2_hor", new CliqueStructures.CliquePair(graph_struct.C_E_r2h, mrf.n_struct, new pot_func.edge_ising_pot(-0.3475323708774884)) );
		//mrf.c_struct.put("E_bishop2_diag", new CliqueStructures.CliquePair(graph_struct.C_E_b2d, mrf.n_struct, new pot_func.edge_ising_pot(1.2)) );
		//mrf.c_struct.put("E_bishop2_xdiag", new CliqueStructures.CliquePair(graph_struct.C_E_b2x, mrf.n_struct, new pot_func.edge_ising_pot(1.2)) );

		//mrf.c_struct.put("E_knight1_1+2", new CliqueStructures.CliquePair(graph_struct.C_E_k1a, mrf.n_struct, new pot_func.edge_ising_pot(1.2)) );
		//mrf.c_struct.put("E_knight1_2+1", new CliqueStructures.CliquePair(graph_struct.C_E_k1b, mrf.n_struct, new pot_func.edge_ising_pot(1.2)) );
		//mrf.c_struct.put("E_knight1_2-1", new CliqueStructures.CliquePair(graph_struct.C_E_k1c, mrf.n_struct, new pot_func.edge_ising_pot(1.2)) );
		//mrf.c_struct.put("E_knight1_1-2", new CliqueStructures.CliquePair(graph_struct.C_E_k1d, mrf.n_struct, new pot_func.edge_ising_pot(1.2)) );
		
		System.out.println("----- mrf specs -----");
		System.out.println("SIZE: "+mrf.dnSize());
		System.out.println("MINIMUM RADIUS: "+mrf.minRadius());
		
		System.out.println("----- load image -----");
		int[][] img = ImageDAQ.dec_int_bw(ImageDAQ.to_CS_GRAY(ImageDAQ.loadImage("img","Opp2.png")));
		System.out.println("----- least squares -----");
		double[] est_theta = Est_LS.leastSquaresEstimation(mrf,2,img);
		int i=0;
		for(Map.Entry<String,CliqueStructures.CliquePair> entry : mrf.c_struct.entrySet()){
			//entry.getValue().c_pot.THETA[0] = est_theta[i];
			System.out.println("param "+i+" "+entry.getKey()+": \t"+est_theta[i]);
			i++;
		}
		
		Cutset.AbstractCutset cutter = new Cutset.StripCutset(mrf,mrf.minRadius()*2);
		double codeLength = JunctionTreeCutsetAC(img,mrf,cutter,2);
		System.out.println("FINAL: "+codeLength);
	}

	//need to work on this automation
	public static void encodeImages(String dir, MRF mrf, int spacing) throws Exception{
		String[] Images = Utilities.fileList(dir,".png");
		
		int[][] img;
		for(int i=0; i<Images.length; i++){
			BufferedWriter out = makeRecorder(dir,Images[i]);
			img = ImageDAQ.dec_int_bw(ImageDAQ.to_CS_GRAY(ImageDAQ.loadImage(dir,Images[i])));
			//ENCODE IMAGE AND RECORD DATA
		}
	}
	
	public static double JunctionTreeCutsetAC(int[][] img, MRF mrf, Cutset.AbstractCutset cutter, int r){
		
		/*
		 * 1) Make graph
		 * 2) call function to get next "cutset"
		 * 3) call function to cluster the graph
		 * 4) call function to encode cutset
		 * 		a) calculate encode distribution for supernode
		 * 		b) observe and encode node (record?)
		 * 		c) either increment or absorb next node (hard-set first supernode beliefs?)
		 */
		/*
		System.out.println("-----making graph-----");
		ImageMRFGraph g = new ImageMRFGraph(img,r);
		g.makeMRF(mrf);
		System.out.println("\tw: "+g.w+"\th: "+g.h);
		System.out.println("\tcliques: "+g.cliques.size());
		*/
		System.out.println("-----making cutsets and clusters-----");
		Cutset.ImageCutter components = new Cutset.ImageCutter(img,r,cutter);
		ClusterTree sub_g;
		double codeLength = 0.0;
		System.out.println("-----encoding-----");
		while(components.hasNext()){
			sub_g = components.nextCluster();
			System.out.println("\ttotal length: "+codeLength);
			codeLength = codeLength + sub_g.encodeAll();
		}
		codeLength = codeLength/(double)components.size();
		return codeLength;
	}
	
	public static BufferedWriter makeRecorder(String dir, String file) throws Exception{
		FileWriter fOut = new FileWriter(dir+"/"+file.replaceFirst("[.][^.]+$","_data.txt"));
		return new BufferedWriter(fOut);
	}
	
	/*
	//----- OLD STUFF HERE
	
	public static void JT_AC(String dir, MRF mrf, int spacing) throws Exception{
		String[] Images = Utilities.fileList(dir,".png");

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
	*/
	
}
