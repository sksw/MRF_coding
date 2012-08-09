import Jama.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public class Est_LS {
	
	public static double[] leastSquaresEstimation(MRF mrf, int r, int[][] img){
		
		int radius = mrf.minRadius();
		int params = mrf.c_struct.size();
		int dnDiversity = (int)Math.pow(r,mrf.dnSize()); //needs to be modified
		int equations = Utilities.comb(r,2)*dnDiversity;
		double[][] matrix_a = new double[equations][params]; //to each neighbourhood configuration, there are two possible x_s
		double[] vector_b = new double[equations]; //least squares estimate to this vector
		double residual;
		
		System.out.println("\tstaring estimation");
		
		String dnString;
		Map<String,Integer> dnFreq = new HashMap<String,Integer>(0);
		//----- track occurrences of different neighbourhood configurations
		for(int x=radius; x<img.length-radius; x++){
			for(int y=radius; y<img[x].length-radius; y++)
				if(dnFreq.containsKey(dnString=ImageOp.dnVal(x,y,img,mrf,r)))
					dnFreq.put(dnString,dnFreq.get(dnString).intValue()+1);
				else
					dnFreq.put(dnString,1);
			System.out.print(".");
		}
		System.out.println();
		
		System.out.println("---dn1--- ---dn2--- | ----- matrix_a ----- \t----- vector_b -----");
		double[] eqConsts;
		String dnString_i,dnString_j;
		double freq_i, freq_j;
		int inx = 0;
		for(int k=0; k<dnDiversity; k++)
			for(int i=0; i<r; i++)
				for(int j=i+1; j<r; j++){
					dnString_i = i+Utilities.toPString(k,mrf.dnSize(),r);
					System.out.print(i+Utilities.toPString(k,mrf.dnSize(),r));
					dnString_j = j+Utilities.toPString(k,mrf.dnSize(),r);
					System.out.print(" "+j+Utilities.toPString(k,mrf.dnSize(),r)+"  ");

					//----- set matrix_a
					eqConsts = getConsts(dnString_i,dnString_j,mrf,r);
					for(int n=0; n<params; n++)
						matrix_a[inx][n] = eqConsts[n];
					for(int n=0; n<matrix_a[inx].length; n++)
						System.out.print("  "+matrix_a[inx][n]);
					//----- set vector_b
					if(dnFreq.containsKey(dnString_i))
						freq_i = (double)dnFreq.get(dnString_i);
					else
						freq_i = 0.00000000001;
					if(dnFreq.containsKey(dnString_j))
						freq_j = (double)dnFreq.get(dnString_j);
					else
						freq_j = 0.00000000001;
					vector_b[inx] = Math.log(freq_i/freq_j);
					System.out.println("\t "+vector_b[inx]+" ("+freq_i+","+freq_j+")");
					inx++;
				}
		System.out.println("-----abundances-----");
		for(Map.Entry<String,Integer> entry : dnFreq.entrySet())
			System.out.println(entry.getKey()+" : "+entry.getValue());
				
		Matrix A = new Matrix(matrix_a,equations,params);
		Matrix b = new Matrix(vector_b,equations);
		Matrix x = A.solve(b);
		Matrix res = A.times(x).minus(b);
		residual = res.normInf();
		//A.print(2,4);
		//b.print(2,4);
		//x.print(2,4);
		System.out.println(residual+"\n");
		return x.getRowPackedCopy();
	}
	
	public static double[] getConsts(String nConfig1, String nConfig2, MRF mrf, int r){
		double[] coefficients = new double[mrf.c_struct.size()];
		HashMap<Integer,Integer> config1 = ImageOp.genCoordMap(nConfig1,mrf,r);
		HashMap<Integer,Integer> config2 = ImageOp.genCoordMap(nConfig2,mrf,r);
		int inx = 0;
		// need to shift the clique offset to diff places in the neighbourhood ... this is HARD
		for(CliqueStructures.CliquePair cliqueType : mrf.c_struct.values()){
			coefficients[inx] = 0;
			for(int[] offset : cliqueType.offsets){
				coefficients[inx] = coefficients[inx]+cliqueType.c_pot.alpha(ImageOp.matchNodeVals(config1,cliqueType.geo,offset));
				coefficients[inx] = coefficients[inx]-cliqueType.c_pot.alpha(ImageOp.matchNodeVals(config2,cliqueType.geo,offset));
			}
			inx++;
		}
		return coefficients;
	}
	
	/*
	public static double[] ising4ptEstimation(BufferedImage img, String mode){
		int[] freq = new int[32]; //2^5 = 32 (the number of possible neighbourhood combinations
		double[] prob = new double[32];
		int w = img.getWidth();
		int h = img.getHeight();
		Raster R = img.getData();
		
		// x_s, x_t, x_b, x_l, x_r
		int total = (w-2)*(h-2);
		String N;
		for(int i=1;i<w-1;i++)
        	for(int j=1;j<h-1;j++){
    			N = "";
    			N = N + getBinaryPixel(i,j,R); //middle
    			N = N + getBinaryPixel(i-1,j,R); //top
    			N = N + getBinaryPixel(i+1,j,R); //bottom
    			N = N + getBinaryPixel(i,j-1,R); //left
    			N = N + getBinaryPixel(i,j+1,R); //right
    			//System.out.println("w:"+i+" h:"+j+" N:"+N);
    			freq[Integer.parseInt(N,2)]++;
        	}
		
		for(int i=0;i<freq.length;i++){
			prob[i] = (double)freq[i]/(double)total;
			if(freq[i]==0) //this is if we have zero entries causing infinity issues - crudely introduce some bias
				prob[i] = 0.00000000001/(double)total;
			//System.out.println("N: "+Utilities.intToPBS(i,5)+" probability: "+prob[i]);
		}
		
		int paramCount;
		double[][] matrix_a; //to each neighbourhood configuration, there are two possible x_s
		double[] vector_b = new double[16]; //least squares estimate to this vector
		double residual;
		
		if(mode.equals("EQ")){
			matrix_a = new double[16][1];
			paramCount = 1;
		}
		else if(mode.equals("VH")){
			matrix_a = new double[16][2];
			paramCount = 2;
		}
		else if(mode.equals("VHS")){
			matrix_a = new double[16][3];
			paramCount = 3;
		}
		else{
			matrix_a = new double[16][1];
			paramCount = 1;
		}
		for(int i=0;i<matrix_a.length;i++)
			Arrays.fill(matrix_a[i],0.0);
		
		for(int i=0;i<16;i++){
			N = "";
			vector_b[i] = Math.log(prob[i]/prob[i+16]); //natural logarithm to get rid of exponential
			N = Utilities.intToPBS(i,5);
			if(mode.equals("EQ"))
				for(int j=1;j<N.length();j++)
					if(N.charAt(j)=='0')
						matrix_a[i][0] = matrix_a[i][0] + 2.0;
					else
						matrix_a[i][0] = matrix_a[i][0] - 2.0;
			else if(mode.equals("VH"))
				for(int j=1;j<N.length();j++)
					if(j==1 || j==2)
						if(N.charAt(j)=='0')
							matrix_a[i][0] = matrix_a[i][0] + 2.0;
						else
							matrix_a[i][0] = matrix_a[i][0] - 2.0;
					else
						if(N.charAt(j)=='0')
							matrix_a[i][1] = matrix_a[i][1] + 2.0;
						else
							matrix_a[i][1] = matrix_a[i][1] - 2.0;
			else if(mode.equals("VHS")){
				matrix_a[i][2] = -2.0;
				for(int j=1;j<N.length();j++)
					if(j==1 || j==2)
						if(N.charAt(j)=='0')
							matrix_a[i][0] = matrix_a[i][0] + 2.0;
						else
							matrix_a[i][0] = matrix_a[i][0] - 2.0;
					else
						if(N.charAt(j)=='0')
							matrix_a[i][1] = matrix_a[i][1] + 2.0;
						else
							matrix_a[i][1] = matrix_a[i][1] - 2.0;
			}
			else
				for(int j=1;j<N.length();j++)
					if(N.charAt(j)=='0')
						matrix_a[i][0] = matrix_a[i][0] + 2.0;
					else
						matrix_a[i][0] = matrix_a[i][0] - 2.0;
		}
		
		//preferences > java > compiler > errors/warnings > deprecated and restricted api > forbidden reference (access rules) : [error --> warning]
		Matrix A = new Matrix(matrix_a,16,paramCount);
		Matrix b = new Matrix(vector_b,16);
		Matrix x = A.solve(b);
		Matrix r = A.times(x).minus(b);
		residual = r.normInf();
		//A.print(2,4);
		//b.print(2,4);
		//x.print(2,4);
		//System.out.println(residual+"\n");
		
		return x.getRowPackedCopy();
	}
	
	public static void ising4ptEstimation(BufferedImage img){
		int[] freq = new int[32]; //2^5 = 32
		double[] prob = new double[32];
		double[][] matrix_a = new double[16][5]; //to each neighbourhood configuration, there are two possible x_s
		double[] vector_b = new double[16]; //least squares estimate to this vector
		int w = img.getWidth();
		int h = img.getHeight();
		Raster R = img.getData();
		
		// x_s, x_l, x_r, x_t, x_b
		int total = (w-2)*(h-2);
		String N;
		for(int i=1;i<w-1;i++)
        	for(int j=1;j<h-1;j++){
    			N = "";
    			N = N + getBinaryPixel(i,j,R); //middle
    			N = N + getBinaryPixel(i,j-1,R); //left
    			N = N + getBinaryPixel(i,j+1,R); //right
    			N = N + getBinaryPixel(i-1,j,R); //top
    			N = N + getBinaryPixel(i+1,j,R); //bottom
    			//System.out.println("w:"+i+" h:"+j+" N:"+N);
    			freq[Integer.parseInt(N,2)]++;
        	}
		
		for(int i=0;i<freq.length;i++){
			prob[i] = (double)freq[i]/(double)total;
			if(freq[i]==0) //this is if we have zero entries causing infinity issues - crudely introduce some tiny bias
				prob[i] = 0.00000000001/(double)total;
			System.out.println("N: "+Utilities.intToPBS(i,5)+" probability: "+prob[i]);
		}
		
		for(int i=0;i<16;i++){
			N = "";
			vector_b[i] = Math.log(prob[i]/prob[i+16]); //natural logarithm to get rid of exponential
			N = Utilities.intToPBS(i,5);
			matrix_a[i][0] = -2.0;
			for(int j=1;j<N.length();j++)
				if(N.charAt(j)=='0')
					matrix_a[i][j] = 2.0;
				else
					matrix_a[i][j] = -2.0;
		}
		
		//preferences > java > compiler > errors/warnings > deprecated and restricted api > forbidden reference (access rules) : [error --> warning]
		Matrix A = new Matrix(matrix_a,16,5);
		Matrix b = new Matrix(vector_b,16);
		Matrix x = A.solve(b);
		Matrix r = A.times(x).minus(b);
		double residual = r.normInf();
		
		A.print(2,5);
		b.print(2,5);
		x.print(2,5);
		System.out.println(residual);
	}
	
	public static int getBinaryPixel(int x, int y, Raster R){
		int[] sample = new int[2];
		R.getPixel(x,y,sample);
		if(sample[0]<128)
			return 0;
		else
			return 1;
	}
	*/
	
}
