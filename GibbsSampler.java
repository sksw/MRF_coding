public class GibbsSampler {

	public static void main(String[] args){
		test();
	}

	public static void test(){
		//simple test of the gibbs sampler using equipotential model
		int[][] img = new int[512][512];
		for(int row=0; row<img.length; row++)
			for(int col=0; col<img[row].length; col++)
				if(Math.random()>0.5)
					img[row][col]=1;
				else
					img[row][col]=0;
		double[] theta = {0.857,0.857};
		int gap = 50;
		for(int i=0;i<2000/gap;i++){
			GibbsSampler.gibbssample(theta,1.0,img,gap);
			ImageOp.saveImage("img/MLE/gibbs/sample_progression_0857","gibbs0857#"+((i+1)*gap)+".png","png",ImageOp.enc_int(img,"BILEVEL"));
		}
		/*for(int row=0; row<img.length; row++){
			System.out.println();
			for(int col=0; col<img[row].length; col++)
				if(img[row][col]==0)
					System.out.print("  ");
				else
					System.out.print("X ");
		}*/
		//System.out.println(Statistics.t_v(img));
		//System.out.println(Statistics.t_h(img));
		//System.out.println(Statistics.t_s(img));
	}
	
	public static void gibbssample(double[] theta, double temperature, int[][] img, int iter, String mode){
		for(int i=0; i<iter; i++)
			for(int row=0; row<img.length; row++)
				for(int col=0; col<img[row].length; col++)
					if(mode.equals("EQ"))
						img[row][col] = p_xy_uni(theta,temperature,img,row,col);
					else if(mode.equals("VH"))
						img[row][col] = p_xy(theta,temperature,img,row,col);
					else if(mode.equals("VH"))
						img[row][col] = p_xy(theta,temperature,img,row,col); //THIS NEEDS CHANGING
					else
						img[row][col] = p_xy_uni(theta,temperature,img,row,col);
	}
	
	public static void gibbssample(double[] theta, double temperature, int[][] img, int iter){
		for(int i=0; i<iter; i++)
			for(int row=0; row<img.length; row++)
				for(int col=0; col<img[row].length; col++)
					if(theta.length==1)
						img[row][col] = p_xy_uni(theta,temperature,img,row,col);
					else
						img[row][col] = p_xy(theta,temperature,img,row,col);
	}
	
	public static int p_xy_uni(double[] theta, double beta, int[][] img, int row, int col){
		double[] pmf = new double[2];
		double temp = 0.0;
		for(int x=0;x<pmf.length;x++){
			pmf[x] = 1.0;
			//top
			if(row-1>-1)
				pmf[x] = pmf[x]*e(x,img[row-1][col],theta[0],beta);
			//bottom
			if(row+1<img.length)
				pmf[x] = pmf[x]*e(x,img[row+1][col],theta[0],beta);
			//left
			if(col-1>-1)
				pmf[x] = pmf[x]*e(x,img[row][col-1],theta[0],beta);
			//right
			if(col+1<img[row].length)
				pmf[x] = pmf[x]*e(x,img[row][col+1],theta[0],beta);
			temp = temp+pmf[x];
		}
		for(int i=0;i<pmf.length;i++)
			pmf[i] = pmf[i]/temp;
		temp = Math.random();
		if(temp>pmf[0])
			return 1;
		else
			return 0;
	}
	
	public static int p_xy(double[] theta, double beta, int[][] img, int row, int col){
		double[] pmf = new double[2];
		double temp = 0.0;
		for(int x=0;x<pmf.length;x++){
			pmf[x] = 1.0;
			//top
			if(row-1>-1)
				pmf[x] = pmf[x]*e(x,img[row-1][col],theta[0],beta);
			//bottom
			if(row+1<img.length)
				pmf[x] = pmf[x]*e(x,img[row+1][col],theta[0],beta);
			//left
			if(col-1>-1)
				pmf[x] = pmf[x]*e(x,img[row][col-1],theta[1],beta);
			//right
			if(col+1<img[row].length)
				pmf[x] = pmf[x]*e(x,img[row][col+1],theta[1],beta);
			temp = temp+pmf[x];
		}
		for(int i=0;i<pmf.length;i++)
			pmf[i] = pmf[i]/temp;
		temp = Math.random();
		if(temp>pmf[0])
			return 1;
		else
			return 0;
	}
	
	public static double e(int x, int x_n, double theta, double beta){
		if(x == 0)
			x = -1;
		if(x_n == 0)
			x_n = -1;
		return Math.exp(beta*theta*((double)x)*((double)x_n));
	}
	
	public static void gibbsProgression(){
		double[] param = {0.5,0.5};
		int[][] img;
		
		img = ImageOp.dec_int(ImageOp.to_CS_GRAY(ImageOp.loadImage("img/MLE/gibbs","noise_seed_32.png")),"BILEVEL");
		for(int i=0;i<25;i++){
			gibbssample(param,1.0,img,2);
			ImageOp.saveImage("img/MLE","gibbs32#"+((i+1)*2)+".png","png",ImageOp.enc_int(img,"BILEVEL"));
		}
	}
}
