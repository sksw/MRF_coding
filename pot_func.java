
public class pot_func {
	
	//===== GENERAL POTENTIAL FUNCTIONS =====
	
	//----- general potential function (this is for cliques)
	public static abstract class abstract_pot{
		//tunable parameter for this potential function
		public double THETA[];
		//potential function
		abstract public double U(int[] node_values);
		public abstract_pot(double theta[]){
			THETA = (double[])theta.clone();
		}
		public abstract_pot(){
			THETA = new double[1];
		}
	}

	//===== POTENTIAL FUNCTION CLASS DEFINITIONS =====
	
	//----- n_ising
	public static class node_ising_pot extends abstract_pot{
		public node_ising_pot(double theta){
			THETA[0] = theta;
		}
		public double U(int[] v){
			if(v[0]==0)
				v[0]=-1;
			return Math.exp(THETA[0]*v[0]);
		}
	}
	
	//----- e_ising
	public static class edge_ising_pot extends abstract_pot{
		public edge_ising_pot(double theta){
			THETA[0] = theta;
		}
		public double U(int[] v){
			for(int i=0; i<v.length; i++)
				if(v[i]==0)
					v[i]=-1;
			return Math.exp(THETA[0]*((double)v[0])*((double)v[1]));
		}
	}
	
}
