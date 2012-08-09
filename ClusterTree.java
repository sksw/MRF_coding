import java.util.ArrayList;

public class ClusterTree{
	
	int next;
	ArrayList<SuperNode> SV;
	ArrayList<SuperEdge> SE;
	
	public ClusterTree(){
		next = 0;
		SV = new ArrayList<SuperNode>(0);
		SE = new ArrayList<SuperEdge>(0);
	}
	
	public class ClusterTreeEncoder{
		
	}
	
	public double encodeAll(){
		double codeLength = 0;
		while(next<SV.size())
			codeLength = codeLength + encodeNext();
		//System.out.println(SV.size()+" supernodes");
		return codeLength/(double)SV.size();
	}

	public double encodeNext(){
		double[] dist = codingDist(next);
		double nodeCodeLength = code(next,dist);
		next++;
		return nodeCodeLength;
	}
	
	public boolean hasNext(){
		return next<SV.size();
	}
	
	public double[] codingDist(int nodeInx){
		//calculate belief for node nodeInx
		double[] belief = SV.get(nodeInx).Z();
		double[] codeDist = new double[belief.length];
		double sum = 0.0;
		//get probability distribution
		for(int i=0; i<belief.length; i++)
			sum = sum + belief[i];
		for(int i=0; i<codeDist.length; i++)
			codeDist[i] = belief[i]/sum;
		/* DEBUG OUTPUT SEQUENCE
		for(int i=0; i<belief.length; i++)
			System.out.println(Utilities.toPString(i,SV.get(nodeInx).V.size(),SV.get(nodeInx).r)+": " + codeDist[i]+" , "+belief[i]);
		*/
		return codeDist;
	}
	
	public double code(int nodeInx, double[] codingDist){
		double obsProb = codingDist[Integer.parseInt(SV.get(nodeInx).obs(),SV.get(nodeInx).r)]; //find out what was observed and record its probability for rate calulation
		//System.out.println("OBSERVED: "+SV.get(nodeInx).obs()+" "+Integer.parseInt(SV.get(nodeInx).obs(),SV.get(nodeInx).r));
		//System.out.println(SV.get(nodeInx).V.size()+" nodes in supernode");
		return Coder.log2_1overpx(obsProb)/(double)SV.get(nodeInx).V.size(); //return rate calculation (shannon-fano to code i.e. log_2(1/p(x)))
	}
}