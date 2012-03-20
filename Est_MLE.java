import java.io.*;
import java.util.Scanner;

public class Est_MLE {
	
	public static void main(String[] args) throws IOException{
		est_MLE("img/MLE/gibbs","img/MLE/sample_images/aerials","EQ");
	}
	
	public static void est_MLE(String expDir, String sampleDir, String mode) throws IOException{
		File fDir = new File(sampleDir);
		String[] Images;
		//filter list of returned files for *.png's
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".png");
		    }
		};
		Images = fDir.list(filter);
		
		int dim; int imgSize; String trainingSet; int setSize;
		double[] tObs; 
		double[] theta;
		double alpha;
		double tolerance;
		
		System.out.println("<PROCESSING "+Images.length+" IMAGES>");
		for(int i=0;i<Images.length;i++){
			dim = ImageOp.loadImage(sampleDir,Images[i]).getWidth();
			if(dim==1024){
				trainingSet = "_64x64";
				imgSize = 64;
			}
			else if(dim==512){
				trainingSet = "_32x32";
				imgSize = 32;
			}
			else{
				trainingSet = "_16x16";
				imgSize = 16;
			}
			
			alpha = 1.0 / ( (double)((imgSize-1)*(imgSize)*2+(imgSize-1)*2) * 2.0 );
			setSize = (dim*dim)/(imgSize*imgSize);
			tObs = getTrainStatistics(sampleDir+"/"+Images[i].replaceFirst("[.][^.]+$",trainingSet),mode); //mode dependent
			theta = Est_LS.ising4ptEstimation(ImageOp.to_CS_GRAY(ImageOp.loadImage(sampleDir,Images[i])),mode); //mode dependent
			tolerance = 0.0;
			for(int j=0;j<tObs.length;j++)
				tolerance = tolerance+tObs[j];
			tolerance = tolerance*0.005;//0.5%
			
			//make sure result directory exists
			File storeDir = new File(expDir+"/learning_"+Images[i].replaceFirst("[.][^.]+$","")+"_"+imgSize+"x"+imgSize);
			if(!storeDir.exists())
				storeDir.mkdirs();
			FileWriter fOut = new FileWriter(expDir+"/learning_"+Images[i].replaceFirst("[.][^.]+$","")+"_"+imgSize+"x"+imgSize+"/LOG.TXT");
			BufferedWriter out = new BufferedWriter(fOut);
			
			System.out.println("\n---------- IMAGE "+Images[i]+" ----------\n");
			out.write("\r\n---------- IMAGE "+Images[i]+" ----------\r\n\r\n");
			System.out.print("\tstatistics: ");
			out.write("\tstatistics: ");
			for(int k=0;k<tObs.length;k++)
				if(k==tObs.length-1){
					System.out.print(tObs[k]+"\n");
					out.write(tObs[k]+"\r\n");
				}
				else{
					System.out.print(tObs[k]+", ");
					out.write(tObs[k]+", ");
				}
			System.out.print("\tLS theta: ");
			out.write("\tLS theta: ");
			for(int k=0;k<theta.length;k++)
				if(k==theta.length-1){
					System.out.print(theta[k]+"\n");
					out.write(theta[k]+"\r\n");
				}
				else{
					System.out.print(theta[k]+", ");
					out.write(theta[k]+", ");
				}
			System.out.println("\talpha: "+alpha);
			System.out.println("\ttolerance: "+tolerance);
			System.out.println("\tsample image size: "+imgSize);
			System.out.println("\tsample set size: "+setSize);
			System.out.println();
			out.write("\talpha: "+alpha+"\r\n");
			out.write("\ttolerance: "+tolerance+"\r\n");
			out.write("\tsample image size: "+imgSize+"\r\n");
			out.write("\tsample set size: "+setSize+"\r\n");
			out.write("\r\n");
			
			learn(expDir,out,Images[i].replaceFirst("[.][^.]+$",""),tObs,theta,alpha,tolerance,imgSize,setSize,200,mode); //mode dependent
			out.close();
		}
	}
	
	public static boolean learn(String expDir, BufferedWriter out, String title, double[] tObs, double[] theta, double alpha, double tolerance,
			int imgSize, int setSize, int maxIter, String mode) throws IOException{
		out.write("---------- IMAGE "+title+" ----------\r\n\r\n");
		int[][] img;
		int[] t_total;
		double[] d = new double[theta.length];
		
		int learningIter = 0;
		double totalRateOfChange;
		while(true){
			out.write("ITERATION "+learningIter+"\r\n");
			System.out.println("\tITERATION "+learningIter);
			t_total = new int[theta.length];
			for(int i=0;i<setSize;i++){
				//loads seed
				img = ImageOp.dec_int(ImageOp.to_CS_GRAY(ImageOp.loadImage("img/MLE/gibbs","noise_seed_"+imgSize+".png")),"BILEVEL");
				//50 iterations of full gibbs sampler sweeps with no annealing
				GibbsSampler.gibbssample(theta,1.0,img,50,mode); //GIBBS SAMPLER NOT READY FOR VHS
				//get statistics --- THIS SHOULD BE A MODE DEPENDENT FUNCTION IN STATS CLASS
				if(mode.equals("EQ"))
					t_total[0] = t_total[0] + Statistics.t_v(img) + Statistics.t_h(img);
				else if(mode.equals("VH")){
					t_total[0] = t_total[0] + Statistics.t_v(img);
					t_total[1] = t_total[1] + Statistics.t_h(img);
				}
				else if(mode.equals("VHS")){
					t_total[0] = t_total[0] + Statistics.t_v(img);
					t_total[1] = t_total[1] + Statistics.t_h(img);
					t_total[2] = t_total[2] + Statistics.t_s(img);
				}
				else
					t_total[0] = t_total[0] + Statistics.t_v(img) + Statistics.t_h(img);
				ImageOp.saveImage(expDir+"/learning_"+title+"_"+imgSize+"x"+imgSize+"/it"+learningIter,"s"+i+".png","png",ImageOp.enc_int(img,"BILEVEL"));			
			}
			//calculate directional derivatives
			totalRateOfChange = 0.0;
			for(int i=0;i<d.length;i++){
				d[i] = tObs[i] - (double)t_total[i]/(double)setSize;
				totalRateOfChange = totalRateOfChange + Math.abs(d[i]); //use absolute value
			}
			//record data for this iteration
			writeInfo(expDir+"/learning_"+title+"_"+imgSize+"x"+imgSize+"/it"+learningIter+"/INFO.TXT",theta,t_total,setSize,totalRateOfChange);
			//if not within tolerance, then move theta towards max
			
			out.write("\ttheta: ");
			for(int i=0;i<theta.length;i++)
				if(i==theta.length-1)
					out.write(theta[i]+"\r\n");
				else
					out.write(theta[i]+", ");
			out.write("\tstatistics: ");
			for(int i=0;i<t_total.length;i++)
				if(i==t_total.length-1)
					out.write(((double)t_total[i]/(double)setSize)+"\r\n");
				else
					out.write(((double)t_total[i]/(double)setSize)+", ");
			out.write("\tdel: ");
			for(int i=0;i<d.length;i++)
				if(i==d.length-1)
					out.write(d[i]+"\r\n");
				else
					out.write(d[i]+", ");
			out.write("\trate of change: "+totalRateOfChange+"\r\n");
			
			if(totalRateOfChange > tolerance){
				for(int i=0;i<theta.length;i++)
					theta[i] = theta[i]+d[i]*alpha;
				learningIter++;
			}
			else
				break;
			
			if(learningIter >= maxIter)
				return false;
			
			//write info to file	
		}
		return true;
	}
	
	public static void est_MLE() throws IOException{

		//observed statistics;
		double[] tOBS_16 = {222.191406,226.796875}; //448.9882813
		double[] tOBS_32 = {918.820312,937}; //1855.820313
		
		//gradient descent scaling constant;
		double ALPHA_16 = 1.0/(480.0 * 2.0); //total number of edges in 16x16 = 15*15*2+15*2 = 480
		double ALPHA_32 = 1.0/(1984.0 * 2.0); //total number of edges in 32x32 = 31*31*2+31*2 = 1984
		
		int imgSize = 32;
		int setSize = 256;
		double[] theta = {0.5};
		double tolerance = 0.925;
		double alpha;
		
		int[] t_total = new int[theta.length];
		double[] tObs = new double[theta.length];
		double[] d = new double[theta.length];
		int[][] img;
		
		if(imgSize == 16){
			if(theta.length==1)
				tObs[0] = 448.9882813;
			else
				for(int i=0;i<tObs.length;i++)
					tObs[i] = tOBS_16[i];
			alpha = ALPHA_16;
		}
		else{
			if(theta.length==1)
				tObs[0] = 1855.820313;
			else
				for(int i=0;i<tObs.length;i++)
					tObs[i] = tOBS_32[i];
			alpha = ALPHA_32;
		}
		
		int learningIter = 0;
		double totalRateOfChange;
		while(true){
			System.out.println("ITERATION "+learningIter);
			t_total = new int[theta.length];
			for(int i=0;i<setSize;i++){
				//loads seed
				img = ImageOp.dec_int(ImageOp.to_CS_GRAY(ImageOp.loadImage("img/MLE/gibbs","noise_seed_"+imgSize+".png")),"BILEVEL");
				//50 iterations of full gibbs sampler sweeps with no annealing
				GibbsSampler.gibbssample(theta,1.0,img,50);
				//get statistics
				if(theta.length==1)
					t_total[0] = t_total[0] + Statistics.t_v(img) + Statistics.t_h(img);
				else{
					t_total[0] = t_total[0] + Statistics.t_v(img);
					t_total[1] = t_total[1] + Statistics.t_h(img);
				}
				//save sampled image
				/*
				System.out.println("\n\n");
				for(int row=0; row<img.length; row++){
					System.out.println();
					for(int col=0; col<img[row].length; col++)
						if(img[row][col]==0)
							System.out.print("  ");
						else
							System.out.print("X ");
				}
				*/
				ImageOp.saveImage("img/MLE/gibbs/learning_"+imgSize+"x"+imgSize+"/it"+learningIter,"s"+i+".png","png",ImageOp.enc_int(img,"BILEVEL"));			
			}
			//calculate directional derivatives
			totalRateOfChange = 0.0;
			for(int i=0;i<d.length;i++){
				d[i] = tObs[i] - (double)t_total[i]/(double)setSize;
				totalRateOfChange = totalRateOfChange + Math.abs(d[i]); //use absolute value
			}
			//record data for this iteration
			writeInfo("img/MLE/gibbs/learning_"+imgSize+"x"+imgSize+"/it"+learningIter+"/INFO.TXT",theta,t_total,setSize,totalRateOfChange);
			//if not within tolerance, then move theta towards max
			
			
			System.out.print("\ttheta: ");
			for(int i=0;i<theta.length;i++)
				if(i==theta.length-1)
					System.out.print(theta[i]+"\n");
				else
					System.out.print(theta[i]+", ");
			System.out.print("\tstatistics: ");
			for(int i=0;i<t_total.length;i++)
				if(i==t_total.length-1)
					System.out.print(((double)t_total[i]/(double)setSize)+"\n");
				else
					System.out.print(((double)t_total[i]/(double)setSize)+", ");
			System.out.print("\tdel: ");
			for(int i=0;i<d.length;i++)
				if(i==d.length-1)
					System.out.print(d[i]+"\n");
				else
					System.out.print(d[i]+", ");
			System.out.print("\trate of change: "+totalRateOfChange+"\n");
			
			if(totalRateOfChange > tolerance){
				for(int i=0;i<theta.length;i++)
					theta[i] = theta[i]+d[i]*alpha;
				learningIter++;
			}
			else
				break;
			
			//write info to file	
		}
	}
	
	public static void writeInfo(String dir, double[] theta, int[] t_total, int setSize, double totalRateOfChange) throws IOException{
		FileWriter fOut = new FileWriter(dir);
		BufferedWriter out = new BufferedWriter(fOut);
		out.write("THETA: ");
		for(int i=0;i<theta.length;i++)
			if(i==theta.length-1)
				out.write(theta[i]+"\r\n");
			else
				out.write(theta[i]+", ");
		out.write("STATISTICS: ");
		for(int i=0;i<theta.length;i++)
			if(i==theta.length-1)
				out.write(((double)t_total[i]/(double)setSize)+"\r\n");
			else
				out.write(theta[i]+", ");
		out.write("TOTAL RATE OF CHANGE: "+totalRateOfChange);
		out.close();
	}
	
	//sifts through folder of images and gets average statistic
	public static double[] getTrainStatistics(String dir, String mode) throws IOException{
		File fDir = new File(dir);
		String[] trainingImages;
		int[][] img;
		//filter list of returned files for *.png's
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".png");
		    }
		};
		trainingImages = fDir.list(filter);
		//System.out.println("sampling from "+dir);
		//System.out.println("sampling from "+trainingImages.length+" training images...");
		//get statistics over list of training images
		double[] stats;
		int t_v_total, t_h_total, t_s_total;
		t_v_total = 0; t_h_total=0; t_s_total=0;
		for(int i=0;i<trainingImages.length;i++){
			img = ImageOp.dec_int(ImageOp.to_CS_GRAY(ImageOp.loadImage(dir,trainingImages[i])),"BILEVEL");
			t_v_total = t_v_total + Statistics.t_v(img);
			t_h_total = t_h_total + Statistics.t_h(img);
			t_s_total = t_s_total + Statistics.t_s(img);
		}
		//record stats based on mode
		if(mode.equals("EQ")){
			stats = new double[1];
			stats[0] = (double)t_v_total/(double)trainingImages.length + (double)t_h_total/(double)trainingImages.length;
		}
		else if(mode.equals("VH")){
			stats = new double[2];
			stats[0] = (double)t_v_total/(double)trainingImages.length;
			stats[1] = (double)t_h_total/(double)trainingImages.length;
		}
		else if(mode.equals("VHS")){
			stats = new double[3];
			stats[0] = (double)t_v_total/(double)trainingImages.length;
			stats[1] = (double)t_h_total/(double)trainingImages.length;
			stats[2] = (double)t_s_total/(double)trainingImages.length;
		}
		else{
			stats = new double[1];
			stats[0] = (double)t_v_total/(double)trainingImages.length + (double)t_h_total/(double)trainingImages.length;
		}
		//write results to file
		FileWriter fOut = new FileWriter(dir+"/INFO.TXT");
		BufferedWriter out = new BufferedWriter(fOut);
		out.write("# OF TRAINING IMAGES: " + trainingImages.length + "\n");
		out.write("STATISTICS: ");
		for(int i=0;i<stats.length;i++)
			if(i==stats.length-1)
				out.write(stats[i]+"\n");
			else
				out.write(stats[i]+", ");
		out.close();
		return stats;
	}
	
	public static void getTrainStatistics(){
		int[][] img;
		int t_v_total, t_h_total, t_s_total;
		
		//(512x512)/(16x16)=1024, 2^(9+9)/2^(4+4) = 2^10 = 32^2 = (2^5)^2
		t_v_total = 0; t_h_total=0; t_s_total=0;
		for(int i=0;i<1024;i++){
			img = ImageOp.dec_int(ImageOp.to_CS_GRAY(ImageOp.loadImage("img/MLE/train_16x16",i+".png")),"BILEVEL");
			t_v_total = t_v_total + Statistics.t_v(img);
			t_h_total = t_h_total + Statistics.t_h(img);
			t_s_total = t_s_total + Statistics.t_s(img);
		}
		System.out.println("t_v: "+t_v_total/1024+"("+t_v_total+")");
		System.out.println("t_h: "+t_h_total/1024+"("+t_h_total+")");
		System.out.println("t_s: "+t_s_total/1024+"("+t_s_total+")");
		//t_v: 222(227524) 222.191406
		//t_h: 226(232240) 226.796875
		//t_s: 60(61618) 60.1738281
		
		//(512x512)/(32x32)=256, 2^(9+9)/2^(5+5) = 2^8 = 16^2 = (2^4)^2
		t_v_total = 0; t_h_total=0; t_s_total=0;
		for(int i=0;i<256;i++){
			img = ImageOp.dec_int(ImageOp.to_CS_GRAY(ImageOp.loadImage("img/MLE/train_32x32",i+".png")),"BILEVEL");
			t_v_total = t_v_total + Statistics.t_v(img);
			t_h_total = t_h_total + Statistics.t_h(img);
			t_s_total = t_s_total + Statistics.t_s(img);
		}
		System.out.println("t_v: "+t_v_total/256+"("+t_v_total+")");
		System.out.println("t_h: "+t_h_total/256+"("+t_h_total+")");
		System.out.println("t_s: "+t_s_total/256+"("+t_s_total+")");
		//t_v: 918(235218) 918.820312
		//t_h: 937(239872) 937
		//t_s: 240(61618) 240.695312
	}
	
	public static void parseData(String dir) throws Exception{
		File fDir = new File("img/sample_images/"+dir);
		String[] Images;
		//filter list of returned files for *.png's
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".png");
		    }
		};
		Images = fDir.list(filter);
		int dim; String trainingSet;
		int iter;
		double theta;
		String DATA = "";
		for(int i=0; i<Images.length; i++){
			dim = ImageOp.loadImage("img/sample_images/"+dir,Images[i]).getWidth();
			if(dim==1024){
				trainingSet = "_64x64";
			}
			else if(dim==512){
				trainingSet = "_32x32";
			}
			else{
				trainingSet = "_16x16";
			}
			File ITERDIR = new File("img/MLE/gibbs/"+dir+"_learning/"+dir+"_ising4pt_EQ/"
					+"learning_"+Images[i].replaceFirst("[.][^.]+$",trainingSet));
			String[] ITEMS;
			ITEMS = ITERDIR.list();
			iter = ITEMS.length-2;
			
			FileReader fIn = new FileReader("img/MLE/gibbs/"+dir+"_learning/"+dir+"_ising4pt_EQ/"
					+"learning_"+Images[i].replaceFirst("[.][^.]+$",trainingSet)+"/LOG.TXT");
			Scanner scan = new Scanner(fIn);
			
			while(scan.findInLine("ITERATION "+iter) == null)
				System.out.println(scan.nextLine());
			System.out.println(scan.nextLine());
			scan.findInLine("\ttheta: ");
			theta = scan.nextDouble();
			DATA = DATA + theta + "\r\n";
		}
		FileWriter fOut = new FileWriter(dir+"_theta_dist.txt");
		BufferedWriter out = new BufferedWriter(fOut);
		out.write(DATA);
		out.close();
	}
	
}
