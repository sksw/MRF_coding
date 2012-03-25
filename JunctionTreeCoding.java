import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;


public class JunctionTreeCoding {

	public static void JT_AC(String dir, MRF mrf, int spacing) throws Exception{
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
	
}
