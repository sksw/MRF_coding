
public class pot_func {
	
	//===== GENERAL POTENTIAL FUNCTIONS =====
	
	//----- general potential function (this is for cliques)
	public static abstract class abstract_pot{
		//tunable parameter for this potential function
		public double THETA[];
		//potential function
		abstract public double U(int[] node_values);
		public abstract_pot(double theta[]){
			THETA = theta;
		}
	}

	//===== POTENTIAL FUNCTION CLASS DEFINITIONS =====
	
	//----- n_ising
	public static class node_ising_pot extends abstract_pot{
		public node_ising_pot(double theta[]){
			super(theta);
		}
		public double U(int[] v){
			return Math.exp(THETA[0]*v[0]);
		}
	}
	
	//----- e_ising
	public static class edge_ising_pot extends abstract_pot{
		public edge_ising_pot(double theta[]){
			super(theta);
		}
		public double U(int[] v){
			return Math.exp(THETA[0]*((double)v[0])*((double)v[1]));
		}
	}
	
}
