import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.color.*;

public class ImageDAQ{
	
	//save image to dir/filename
	public static void saveImage(String dir, String fileName, String type, BufferedImage src){
		File F = new File(dir);
		try{
			if(F.exists()) ImageIO.write(src, type, new File(dir,fileName));
			else{ F.mkdirs(); ImageIO.write(src, type, new File(dir,fileName)); }
		}
		catch (Exception e){
			System.out.println("error saving image! "+e.getMessage());
		}
	}
	
	//load image from dir/filename
	public static BufferedImage loadImage(String dir,String fileName){
		BufferedImage newImage;
		try{
			newImage = ImageIO.read(new File(dir,fileName)); return newImage;
		}
		catch (Exception e){
			System.out.println("error opening image! "+e.getMessage()); return null;
		}
	}
	
	//convert to CS_GRAY
	public static BufferedImage to_CS_GRAY(BufferedImage src){ 
		BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null); 
	    return op.filter(src, null);
	}
	
	//translate image to black and white int[][]
	public static int[][] dec_int_bw(BufferedImage src){ 
		int[] sample = new int[2];
		int w = src.getWidth(), h = src.getHeight();
		Raster R = src.getData();
		int[][] img = new int[w][h];
		for(int x=0; x<w; x++)
        	for(int y=0; y<h; y++){
        		R.getPixel(x,y,sample);
    			if(sample[0]<128)
    				img[x][y] = 0;
    			else
    				img[x][y] = 1;
        	}
        return img;
	}
	//encode image from black and white int[][]
	public static BufferedImage enc_int_bw(int[][] img){
		int[] sample = new int[2];
		BufferedImage Img = new BufferedImage(img.length,img[0].length,BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster R = Img.getRaster();
		for(int x=0; x<img.length; x++)
        	for(int y=0; y<img[x].length; y++){
        		if(img[x][y]==0)
        			sample[0] = 0;
        		else
        			sample[0] = 255;
        		R.setPixel(x,y,sample);
        	}
		Img.setData(R);
		return Img;
	}
	
	//----- USEFUL FUNCTIONS

	//generates black and white noise seed
	public static int[][] seed_bw(int w, int h, double prob_b){
		int[][] img = new int[w][h];
		for(int x=0; x<w; x++)
			for(int y=0; y<h; y++)
				if(Math.random() > prob_b)
					img[x][y] = 1;
				else
					img[x][y] = 0;
		return img;
	}
	
	//generates black and white barcode image
	public static int[][] barcode(int w, int h, double prob_b){
		int[][] img = new int[w][h];
		for(int y=0; y<h; y++)
			if(Math.random() > prob_b)
				for(int x=0; x<w; x++)
					img[x][y] = 1;
			else
				for(int x=0; x<w; x++)
					img[x][y] = 0;
		return img;
	}
	
	//segments folder of images into smaller image training set
	public static void segment(String dir, int[][] training_sizes){ //training_sizes[i][0] = height, training_sizes[i][1] = width
		File fDir = new File(dir);
		String[] Images;
		BufferedImage img;
		//filter list of returned files for *.png's
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".png");
		    }
		};
		Images = fDir.list(filter);
		//----- segment image into subimages
		for(int i=0; i<Images.length; i++)
			for(int j=0; j<training_sizes.length; j++){
				img = ImageDAQ.loadImage(dir,Images[i]);
				for(int y=0; y<img.getHeight()/training_sizes[j][0]; y++)
					for(int x=0; x<img.getWidth()/training_sizes[j][1]; x++)
						ImageDAQ.saveImage(dir+"/"+Images[i].replaceFirst("[.][^.]+$","_"+training_sizes[j][0]+"x"+training_sizes[j][1]),(y*img.getWidth()/training_sizes[j][1]+x)+".png","png",
								img.getSubimage(x*training_sizes[j][1],y*training_sizes[j][0],training_sizes[j][1],training_sizes[j][0]));
			}
	}
	
	//convert folder of images to black and white
	public static void toBW(String dir){
		File fDir = new File(dir);
		String[] Images;
		int[][] img;
		//filter list of returned files for *.png's
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".png");
		    }
		};
		Images = fDir.list(filter);
		//----- convert to black and white
		for(int i=0;i<Images.length;i++){
			System.out.println(Images[i]+"-->"+Images[i].replaceFirst("[.][^.]+$","_bw.png"));
			img = dec_int_bw(to_CS_GRAY(loadImage(dir,Images[i])));
			saveImage(dir,Images[i].replaceFirst("[.][^.]+$", "_bw.png"),"png",enc_int_bw(img));
		}
	}
}

