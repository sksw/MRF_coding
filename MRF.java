import java.util.HashMap;
import java.util.LinkedHashMap;

public class MRF {
	
	public LinkedHashMap<String, int[][]> n_struct; //neighbourhood names <--> neighbourhood structures
	public LinkedHashMap<String, CliqueStructures.CliquePair> c_struct; //clique names <--> clique structures, clique definitions

	//this should be a function of potentials U
	public Object p_xy;
	
	//this should be a function of potentials U (specifically, the name)
	public Object LS_est;
	
	//a data collector - should be a function of potentials U (this is a very frequentist approach)
	public Object stats;

	public MRF(){
		
	}
	
	//size of neighbourhood (number of nodes)
	public int dnSize(){ //needs to be modified to have something to do with clique instead
		int dnSize = 0;
		for(int[][] entry : n_struct.values())
			dnSize = dnSize + entry.length;
		return dnSize;
		/*
		ArrayList<Integer> relevantNeighbours = new ArrayList<Integer>(0);
		for(CliqueStructures.CliquePair clique : c_struct.values())
			for(int i=0; i<clique.geo.length; i++)
				for(int j=0; j<clique.offsets.size(); j++){
					int[] coord = new int[clique.geo[i].length];
					for(int k=0; k<coord.length; k++)
						coord[k] = clique.geo[i][k] + clique.offsets.get(j)[k];
					if(!relevantNeighbours.contains(new Integer(Est_LS.pairHash(coord))))
						relevantNeighbours.add(Est_LS.pairHash(coord));
				}
		return relevantNeighbours.size()-1;
		*/
	}
	
	//minimum radius regarding how far out one needs to search in order to achieve conditional independence
	public int minRadius(){
		int radius = 0;
		for(int[][] neighbours : n_struct.values())
			for(int i=0; i<neighbours.length; i++)
				for(int j=0; j<neighbours[i].length; j++)
					if(neighbours[i][j]>radius)
						radius = neighbours[i][j]; //don't need to check for negative 
		return radius;
	}
	
}
