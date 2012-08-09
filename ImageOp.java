import java.util.HashMap;

public class ImageOp {

	public static String dnVal(int x, int y, int[][] img, MRF mrf, int r){
		String dnString = "";
		dnString = dnString + img[x][y];
		for(int[][] neighbours : mrf.n_struct.values())
			for(int i=0; i<neighbours.length; i++)
				dnString = dnString + neighbourVal(x,y,img,neighbours[i],r);
		return dnString;
	}
		
	public static String neighbourVal(int x, int y, int[][] img, int[] offset, int r){
		int dx = offset[0], dy = offset[1], w = img.length, h = img[0].length;
		if(x+dx>-1 && y+dy>-1 && x+dx<w && y+dy<h)
			return Integer.toString(img[x+dx][y+dy],r);
		else
			return "_";
	}
	
	public static int[] matchNodeVals(HashMap<Integer,Integer> map, int[][] cliqueGeo, int[] offset){
		int[] nodeVals = new int[cliqueGeo.length];
		for(int i=0; i<cliqueGeo.length; i++){
			int[] coord = new int[2];
			coord[0] = offset[0]+cliqueGeo[i][0];
			coord[1] = offset[1]+cliqueGeo[i][1];
			nodeVals[i] = map.get(pairHash(coord));
		}
		return nodeVals;
	}
	
	public static HashMap<Integer,Integer> genCoordMap(String dnString, MRF mrf, int r){
		HashMap<Integer,Integer> coordVals = new HashMap<Integer,Integer>();
		coordVals.put(0,Integer.parseInt(dnString.substring(0,1),r));
		int inx = 1;
		for(int[][] neighbours : mrf.n_struct.values())
			for(int i=0; i<neighbours.length; i++)
				if(dnString.charAt(inx)=='_')
					coordVals.put(pairHash(neighbours[i]),-1);
				else{
					coordVals.put(pairHash(neighbours[i]),Integer.parseInt(dnString.substring(inx,inx+1),r));
					inx++;
				}
		return coordVals;
	}
	
	public static int pairHash(int[] coord){
		int x, y;
		x = Utilities.map_ZtoN(coord[0]);
		y = Utilities.map_ZtoN(coord[1]);
		return Utilities.map_NxNtoN(x,y);
	}
	
}
