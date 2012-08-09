
public class Statistics {
	
	public static double[] getStats(MRF mrf, int[][] img){
		double[] stats = new double[mrf.c_struct.size()];
		int inx = 0;
		for(CliqueStructures.CliquePair cliqueType : mrf.c_struct.values()){
			stats[inx] = collect(cliqueType,img);
			inx++;
		}
		return stats;
	}
	
	public static double collect(CliqueStructures.CliquePair cliqueType, int[][] img){
		int[] nodeVals;
		double stat = 0.0;
		for(int x=0; x<img.length; x++)
			for(int y=0; y<img[x].length; y++)
				if( (nodeVals = getStructNodes(x,y,cliqueType.geo,img)) != null )
					stat = stat + cliqueType.c_pot.alpha(nodeVals);
		return stat;
	}
	
	//find the nodes related to a specific int[][] graph structure at a given pixel
	public static int[] getStructNodes(int x, int y, int[][] struct, int[][] img){
		int[] nodeVals = new int[struct.length];
		int dx, dy;
		for(int i=0; i<struct.length; i++){
			dx = struct[i][0];
			dy = struct[i][1];
			if(x+dx>-1 && y+dy>-1 && x+dx<img.length && y+dy<img[0].length)
				nodeVals[i] = img[x+dx][y+dy];
			else
				return null;
		}
		return nodeVals;
	}
	
	/* OLD STUFF
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
	*/
	
}
