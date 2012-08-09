import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GibbsSampler {

	public static void main(String[] args){
		
		//0.07423857003201752
		// param 0 E_rook1_ver: 	 0.9874078155960595
		// param 1 E_rook1_hor: 	 1.1093909472631103
		// param 2 E_bishop1_diag: 	 0.4876016546382432
		// param 3 E_bishop1_xdiag:  0.31633943895052447
		// param 4 E_rook2_ver: 	-0.13196602767754476
		// param 5 E_rook2_hor: 	-0.06828063646179837
		
		MRF mrf = new MRF();
		mrf.n_struct = graph_struct.DIAMOND2;
		//mrf.n_struct = graph_struct.ROOK1;
		//mrf.n_struct = graph_struct.BISHOP1;
		mrf.c_struct = new LinkedHashMap<String, CliqueStructures.CliquePair>();
		//set default 0.5
		mrf.c_struct.put("E_rook1_ver", new CliqueStructures.CliquePair(graph_struct.C_E_r1v, mrf.n_struct, new pot_func.edge_ising_pot(0.9874078155960595)) );
		mrf.c_struct.put("E_rook1_hor", new CliqueStructures.CliquePair(graph_struct.C_E_r1h, mrf.n_struct, new pot_func.edge_ising_pot(1.1093909472631103)) );
		mrf.c_struct.put("E_bishop1_diag", new CliqueStructures.CliquePair(graph_struct.C_E_b1d, mrf.n_struct, new pot_func.edge_ising_pot(0.4876016546382432)) );
		mrf.c_struct.put("E_bishop1_xdiag", new CliqueStructures.CliquePair(graph_struct.C_E_b1x, mrf.n_struct, new pot_func.edge_ising_pot(0.31633943895052447)) );
		mrf.c_struct.put("E_rook2_ver", new CliqueStructures.CliquePair(graph_struct.C_E_r2v, mrf.n_struct, new pot_func.edge_ising_pot(-0.13196602767754476)) );
		mrf.c_struct.put("E_rook2_hor", new CliqueStructures.CliquePair(graph_struct.C_E_r2h, mrf.n_struct, new pot_func.edge_ising_pot(-0.06828063646179837)) );
		
		int[][] img = new int[64][64];
		for(int row=0; row<img.length; row++)
			for(int col=0; col<img[row].length; col++)
				if(Math.random()>0.5)
					img[row][col]=1;
				else
					img[row][col]=0;
		
		for(int sweep=0;sweep<1000;sweep++)
			sweep(img,mrf,2);
		
		ImageDAQ.saveImage("img", "test.png", "png", ImageDAQ.enc_int_bw(img));
	}
	
	public static int[][] genSample(int w, int h, MRF mrf, int r, int cycles){
		int[][] sampImg = new int[w][h];
		for(int row=0; row<sampImg.length; row++) //seed random image
			for(int col=0; col<sampImg[row].length; col++)
				sampImg[row][col] = (int)(Math.random()*r);
		
		for(int sweep=0; sweep<cycles; sweep++) //gibbs sample for a certain number of cycles
			GibbsSampler.sweep(sampImg,mrf,r);
		
		return sampImg;
	}

	public static void sweep(int[][] img, MRF mrf, int r){
		for(int x=0; x<img.length; x++)
			for(int y=0; y<img[x].length; y++)
				img[x][y] = p_xy(x,y,img,mrf,r);
	}
	
	public static int p_xy(int x, int y, int[][] img, MRF mrf, int r){
		double[] pmf = new double[r];
		Arrays.fill(pmf,1.0);
		double sum = 0.0;
		String config = ImageOp.dnVal(x,y,img,mrf,r);
		for(int i=0; i<r; i++){
			config = i + config.substring(1);
			pmf[i] = U(config,mrf,r);
			sum = sum + pmf[i];
		}
		for(int i=0;i<pmf.length;i++)
			pmf[i] = pmf[i]/sum;
		return Utilities.draw(Utilities.cdf(pmf));
	}
	
	public static double U(String config, MRF mrf, int r){
		double pot = 1.0;
		HashMap<Integer,Integer> configMap = ImageOp.genCoordMap(config,mrf,r);
		for(CliqueStructures.CliquePair cliqueType: mrf.c_struct.values())
			for(int[] offset : cliqueType.offsets)
				pot = pot * cliqueType.c_pot.U(ImageOp.matchNodeVals(configMap,cliqueType.geo,offset));
		return pot;
	}

}
