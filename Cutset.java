import java.util.Arrays;

public class Cutset {

	public static class ImageCutter{
		private int next;
		private int total;
		private int r;
		private int[][] img;
		private Cutset.AbstractCutset cutter;
		public ImageCutter(int[][] image, int range, Cutset.AbstractCutset cutset_spec){
			img = image;
			r = range;
			cutter = cutset_spec;
			next = 0;
			total = cutter.numOfComponents(img);
		}
		public ClusterTree nextCluster(){
			return cutter.getClusteredComponent(next++,img,r);
		}
		public boolean hasNext(){
			return next<total;
		}
		public int size(){
			return total;
		}

	}
	
	public abstract static class AbstractCutset{
		MRF mrf;
		public AbstractCutset(MRF mrf_spec){
			mrf = mrf_spec;
		}
		public abstract int numOfComponents(int[][] img);
		public abstract ClusterTree getClusteredComponent(int inx, int[][] img, int range);
	}
	
	public static class StripCutset extends AbstractCutset{
		private int thickness;
		public StripCutset(MRF mrf_spec, int spacing){
			super(mrf_spec);
			thickness = spacing;
		}
		public int numOfComponents(int img[][]){
			return (img[0].length-mrf.minRadius()) / (thickness+mrf.minRadius());
		}
		public ClusterTree getClusteredComponent(int inx, int[][] img, int r){
			//System.out.println("\tstrip subgraph "+inx);
			ImageMRFGraph g = makeSubGraph(inx+1,img,r);
			System.out.println("NEW STRIP!");
			g.print();
			//System.out.println("\t\tgrabbing cutset");
			SuperNode cutset = findCutset(g);
			//System.out.println("\t\tclustering");
			ClusterTree cluster = cluster(g);
			//System.out.println("\t\tlinking");
			linkAndCondition(cluster,cutset,g);
			return cluster;
		}
		private ImageMRFGraph makeSubGraph(int inx, int[][] img, int r){
			int[][] sub_img = new int[img.length][thickness+2*mrf.minRadius()];
			int topInx = (inx-1)*(thickness+mrf.minRadius());
			for(int i=0; i<img.length; i++)
				sub_img[i] = Arrays.copyOfRange(img[i],topInx,topInx+thickness+2*mrf.minRadius());
			ImageMRFGraph g = new ImageMRFGraph(sub_img,r);
			g.makeMRF(mrf);
			return g;
		}
		private SuperNode findCutset(ImageMRFGraph g){
			SuperNode cutset = new SuperNode(-1,g.r);
			for(int x=0; x<g.w; x++){
				for(int y=0; y<mrf.minRadius(); y++)
					cutset.V.add(g.img_nodes[x][y]);
				for(int y=g.h-1; y>g.h-mrf.minRadius()-1; y--)
					cutset.V.add(g.img_nodes[x][y]);
			}
			cutset.findInternalCliques(g.cliquesForSearch);
			cutset.obs(); //<-- this is when we observe (encode) the cutset (conditioning happens here)
			/* DEBUG OUTPUT SEQUENCE
			System.out.println();
			for(int i=0; i<cutset.V.size(); i=i+2)
				System.out.print(cutset.V.get(i).VAL+" ");
			System.out.println();
			for(int i=1; i<cutset.V.size(); i=i+2)
				System.out.print(cutset.V.get(i).VAL+" ");
			System.out.println();
			*/
			return cutset;
		}
		private ClusterTree cluster(ImageMRFGraph g){
			ClusterTree cluster = new ClusterTree();
			for(int i=0; i*mrf.minRadius()<g.w; i++){
				//----- make supernode
				SuperNode newNode = new SuperNode(i,g.r);
				for(int x=i*mrf.minRadius(); x<(i+1)*mrf.minRadius(); x++)
					for(int y=mrf.minRadius(); y<mrf.minRadius()+thickness; y++)
						newNode.V.add(g.img_nodes[x][y]);
				newNode.findInternalCliques(g.cliquesForSearch);
				//----- add supernode into list of supernodes in cluster
				cluster.SV.add(newNode);
			}
			return cluster;
		}
		private void linkAndCondition(ClusterTree cluster, SuperNode cutset, ImageMRFGraph g){
			SuperEdge newEdge;
			for(int i=0; i<cluster.SV.size()-1; i++){
				newEdge = new SuperEdge(cluster.SV.get(i),cluster.SV.get(i+1),g.cliquesForSearch,g.r);
				newEdge.condition(cutset);
				cluster.SE.add(newEdge);
			}
			for(int i=0; i<cluster.SV.size(); i++){
				newEdge = new SuperEdge(cluster.SV.get(i),cutset,g.cliquesForSearch,g.r);
				if(newEdge.C.size()==0)
					newEdge.detach();
				else
					cluster.SE.add(newEdge);
			}
			//make pairwise neighbours
			//make each supernode neighbours with cutset
			//		check if there are actually cliques between a supernode and cutset before adding neighbour
			//process tri-cliques <-- add into special list in edges?  perhaps this should be added into normal search to be more efficient
			//epot procedure?
		}
	}
	
	//need a conditioning function to condition on nearby nodes when "stripping"
	/* 
	 * 1) use minimum radius to determine spacing nodes
	 * 2) observe spacing nodes
	 * 3) return supernode of condition neighbours
	 * 4) return array of internal node
	 */

	/*
	 * 1) use minimum radius to determine minimum cluster width
	 * 2) systematically segment into supernodes
	 * 3) search for internal-cliques
	 * 4) pairwise attach superedges
	 * 5) search for cross-cliques
	 * 
	 * supernodes add condition-node as a neighbour
	 * need to make cross-clique function in superedge more stringent
	 * superedges check to see if there are "tri-node" cliques
	 */
	
}
