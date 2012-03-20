import java.io.*;

import javax.imageio.*;
import java.awt.image.*;
import java.awt.color.*;

public class ImageOp{
	
	public static void main(String[] args){
		//noiseSeed_16_32_64();
		segment("img/MLE/sample_images/aerials");
		segment("img/MLE/sample_images/misc");
		segment("img/MLE/sample_images/textures");
	}
	
	public static void saveImage(String dir, String fileName, String type, BufferedImage src){
		File F = new File(dir);
		try{ //System.out.println("save: "+dir+"/"+fileName);
			if(F.exists())
				ImageIO.write(src, type, new File(dir,fileName));
			else{
				F.mkdir();
				ImageIO.write(src, type, new File(dir,fileName));
			}
		}
		catch (Exception e){
			System.out.println("error saving image! "+e.getMessage());
		}
	}
	
	public static BufferedImage loadImage(String dir,String fileName){
		BufferedImage newImage;
		try{ //System.out.println("load: "+dir+"/"+fileName);
			newImage = ImageIO.read(new File(dir,fileName));
			return newImage;
		}
		catch (Exception e){
			System.out.println("error opening image! "+e.getMessage());
			return null;
		}
	}
	
	public static void printImg(BufferedImage src, String type){ 
		int[] sample = {-1};
		int w = src.getWidth();
		int h = src.getHeight();
		Raster R = src.getData();
		for(int i=0;i<w;i++){
			System.out.println();
        	for(int j=0;j<h;j++){
        		R.getPixel(i,j,sample);
        		if(type.equals("CS_GRAY"))
        			System.out.print(sample[0] + " ");
        	}
		}
	}
	
	public static BufferedImage to_CS_GRAY(BufferedImage src){ 
		BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null); 
	    return op.filter(src, null);
	}
	
	public static int[][] dec_int(BufferedImage src, String type){ 
		int[] sample = new int[2];
		int w = src.getWidth();
		int h = src.getHeight();
		Raster R = src.getData();
		int[][] img = new int[w][h];
		for(int row=0;row<w;row++)
        	for(int col=0;col<h;col++){
        		R.getPixel(row,col,sample);
        		if(type.equals("BILEVEL"))
        			if(sample[0]<128)
        				img[row][col] = 0;
        			else
        				img[row][col] = 1;
        	}
        return img;
	}
	
	public static BufferedImage enc_int(int[][] img, String type){
		int[] sample = new int[2];
		BufferedImage Img = null;
		if(type.equals("BILEVEL")){
			Img = new BufferedImage(img.length,img[0].length,BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster R = Img.getRaster();
			for(int row=0;row<img.length;row++)
	        	for(int col=0;col<img[row].length;col++){
	        		if(img[row][col]==0)
	        			sample[0] = 0;
	        		else
	        			sample[0] = 255;
	        		R.setPixel(row,col,sample);
	        	}
			Img.setData(R);
		}
		return Img;
	}
	
	public static Graph dec_Graph(BufferedImage src, String type){ 
		//int[] sample = {-1};
		int[] sample = new int[2];
		int w = src.getWidth();
		int h = src.getHeight();
		Raster R = src.getData();
		Graph G = new Graph(w,h);
		//System.out.println("w:"+w+" h:"+h);
		for(int i=0;i<w;i++)
        	for(int j=0;j<h;j++){
        		//System.out.print(i+":"+j);
        		R.getPixel(i,j,sample);
        		//System.out.println(":"+sample[0]);
        		if(type.equals("BILEVEL")){
        			if(sample[0]<128)
        				sample[0]=0;
        			else
        				sample[0]=1;
        			G.V.add(new Node(G.V.size(),2,sample[0]));
        		}
        	}
        return G;
	}
	
	public static BufferedImage enc_Graph(Graph src, String type){
		int[] sample;
		BufferedImage Img = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);//java complains if not initialized
		if(type.equals("BILEVEL")){
			sample = new int[1];
			Img = new BufferedImage(src.w,src.h,BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster R = Img.getRaster();
			for(int i=0;i<src.w;i++)
	        	for(int j=0;j<src.h;j++){
	        		if(src.V.get(i*src.h+j).N_VAL==0)
	        			sample[0] = 0;
	        		else
	        			sample[0] = 255;
	        		R.setPixel(i,j,sample);
	        	}
			Img.setData(R);
		}
		return Img;
	}

	public static void barcode(){
		BufferedImage Test = ImageOp.to_CS_GRAY(ImageOp.loadImage("img","test.png"));
		Graph G = ImageOp.dec_Graph(Test,"BILEVEL");
		for(int i=0;i<512;i++){
			if(Math.random()>0.25)
				for(int j=0;j<512;j++)
					G.V.get(i*512+j).N_VAL = 1;
			else
				for(int j=0;j<512;j++)
					G.V.get(i*512+j).N_VAL = 0;
		}
		ImageOp.saveImage("img","lined.png","png",ImageOp.enc_Graph(G,"BILEVEL"));	
	}
	
	public static void segment(String dir){
		File fDir = new File(dir);
		String[] Images;
		int[][] img, sample;
		//filter list of returned files for *.png's
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".png");
		    }
		};
		Images = fDir.list(filter);
		for(int i=0;i<Images.length;i++){
			img = ImageOp.dec_int(ImageOp.to_CS_GRAY(ImageOp.loadImage(dir,Images[i])),"BILEVEL");
			sample = new int[16][16];
			for(int w=0;w<img.length/16;w++)
				for(int l=0;l<img[w].length/16;l++)
					for(int row=0;row<16;row++)
						for(int col=0;col<16;col++){
							sample[row][col] = img[w*16+row][l*16+col];
							ImageOp.saveImage(dir+"/"+Images[i].replaceFirst("[.][^.]+$","_16x16"),(w*img.length/16+l)+".png","png",ImageOp.enc_int(sample,"BILEVEL"));
						}
			sample = new int[32][32];
			for(int w=0;w<img.length/32;w++)
				for(int l=0;l<img[w].length/32;l++)
					for(int row=0;row<32;row++)
						for(int col=0;col<32;col++){
							sample[row][col] = img[w*32+row][l*32+col];
							ImageOp.saveImage(dir+"/"+Images[i].replaceFirst("[.][^.]+$","_32x32"),(w*img.length/32+l)+".png","png",ImageOp.enc_int(sample,"BILEVEL"));
						}
			sample = new int[64][64];
			for(int w=0;w<img.length/64;w++)
				for(int l=0;l<img[w].length/64;l++)
					for(int row=0;row<64;row++)
						for(int col=0;col<64;col++){
							sample[row][col] = img[w*64+row][l*64+col];
							ImageOp.saveImage(dir+"/"+Images[i].replaceFirst("[.][^.]+$","_64x64"),(w*img.length/64+l)+".png","png",ImageOp.enc_int(sample,"BILEVEL"));
						}
		}
	}
	
	public static void segment_16_32(){
		int[][] sample;
		int[][] img = ImageOp.dec_int(ImageOp.to_CS_GRAY(ImageOp.loadImage("img","test.png")),"BILEVEL");
		//(512x512)/(16x16)=1024, 2^(9+9)/2^(4+4) = 2^10 = 32^2 = (2^5)^2
		sample = new int[16][16];
		for(int i=0;i<32;i++)
			for(int j=0;j<32;j++)
				for(int row=0;row<16;row++)
					for(int col=0;col<16;col++){
						sample[row][col] = img[i*16+row][j*16+col];
						ImageOp.saveImage("img/train_16x16",(i*32+j)+".png","png",ImageOp.enc_int(sample,"BILEVEL"));
					}
		//(512x512)/(32x32)=256, 2^(9+9)/2^(5+5) = 2^8 = 16^2 = (2^4)^2
		sample = new int[32][32];
		for(int i=0;i<16;i++)
			for(int j=0;j<16;j++)
				for(int row=0;row<32;row++)
					for(int col=0;col<32;col++){
						sample[row][col] = img[i*32+row][j*32+col];
						ImageOp.saveImage("img/train_32x32",(i*16+j)+".png","png",ImageOp.enc_int(sample,"BILEVEL"));
					}
	}
	
	public static void noiseSeed_16_32_64(){
		int[][] sample;
		/*
		sample = new int[16][16];
		for(int row=0;row<16;row++)
			for(int col=0;col<16;col++)
				if(Math.random()>0.5)
					sample[row][col] = 1;
				else
					sample[row][col] = 0;
		ImageOp.saveImage("img","noise_seed_16.png","png",ImageOp.enc_int(sample,"BILEVEL"));
		
		sample = new int[32][32];
		for(int row=0;row<32;row++)
			for(int col=0;col<32;col++)
				if(Math.random()>0.5)
					sample[row][col] = 1;
				else
					sample[row][col] = 0;
		ImageOp.saveImage("img","noise_seed_32.png","png",ImageOp.enc_int(sample,"BILEVEL"));
		*/
		sample = new int[64][64];
		for(int row=0;row<64;row++)
			for(int col=0;col<64;col++)
				if(Math.random()>0.5)
					sample[row][col] = 1;
				else
					sample[row][col] = 0;
		ImageOp.saveImage("img","noise_seed_64.png","png",ImageOp.enc_int(sample,"BILEVEL"));
	}
	
	public static void toBW(){
		String type = "textures";
		File dir = new File("img/sample_images/"+type+"_png");
		String[] Images;
		int[][] img;
		
		// Filter list of returned files
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".png");
		    }
		};
		
		Images = dir.list(filter);

		for(int i=0;i<Images.length;i++){
			System.out.println(Images[i]);
			img = dec_int(to_CS_GRAY(loadImage("img/sample_images/"+type+"_png",Images[i])),"BILEVEL");
			saveImage("img/sample_images/"+type+"_bw",Images[i].replaceFirst("[.][^.]+$", "_bw.png"),"png",enc_int(img,"BILEVEL"));
		}
	}
}

