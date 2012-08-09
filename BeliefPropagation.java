
public class BeliefPropagation {
	
	public static interface BPNode<T> {
		public double npot(T nVal);
		public double[] Z();
	}
	public static interface BPEdge<T> {
		public double epot(T n1Val, T n2Val);
		public double get_m12(T n2Val, int ref, boolean fresh);
		public double get_m21(T n1Val, int ref, boolean fresh);
	}

}
