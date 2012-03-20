import Jama.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Arrays;

public class Est_LS {
	
	public static void main(String[] args){
		BufferedImage img = ImageOp.to_CS_GRAY(ImageOp.loadImage("img","test.png"));
		double[] theta = ising4ptEstimation(img,"VHS");
		for(int i=0;i<theta.length;i++)
			System.out.println(theta[i]);
	}
	
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
	
}
