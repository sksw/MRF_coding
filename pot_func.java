
public class pot_func {

	//===== POTENTIAL FUNCTIONS =====
	
	public static abstract class n_pot_func {
		public double THETA;
		abstract public double U(int v);
		public n_pot_func(double theta){
			THETA = theta;
		}
	}
	
	public static abstract class e_pot_func {
		public double THETA;
		abstract public double U(int v1, int v2);
		public e_pot_func(double theta){
			THETA = theta;
		}
	}
	
	public abstract class clique {
		public Node[] Nodes;
		public final int R;
		public String TYPE;
		
		public abstract double pot(int[] node_values);
		
		public clique(int range){
			R = range;
		}
	}
	
	//===== CLASS DEFINITIONS =====
	
	//----- n_ising
	
	public static class n_ising extends n_pot_func{
		public n_ising(double theta){
			super(theta);
		}
		public double U(int v){
			return Math.exp(THETA*v);
		}
	}
	
	//----- e_ising
	
	public static class e_ising extends e_pot_func{
		public e_ising(double theta){
			super(theta);
		}
		public double U(int v1, int v2){
			return Math.exp(THETA*((double)v1)*((double)v2));
		}
	}
	
}
