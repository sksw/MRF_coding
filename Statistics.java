
public class Statistics {
	
	public static int t_v(int[][] img){
		int t_v = 0;
		for(int row=1;row<img.length;row++)
			for(int col=0;col<img[row].length;col++)
				if(img[row][col]!=img[row-1][col])
					t_v--;
				else
					t_v++;
		return t_v;
	}
	
	public static int t_h(int [][] img){
		int t_h = 0;
		for(int row=0;row<img.length;row++)
			for(int col=1;col<img[row].length;col++)
				if(img[row][col]!=img[row][col-1])
					t_h--;
				else
					t_h++;
		return t_h;
	}
	
	public static int t_s(int [][] img){
		int t_s = 0;
		for(int row=0;row<img.length;row++)
			for(int col=0;col<img[row].length;col++)
				if(img[row][col]==0)
					t_s--;
				else
					t_s++;
		return t_s;
	}
	
}
