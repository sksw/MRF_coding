
public class pot_func {

	//----- SELF POTENTIAL FUNCTIONS -----
	//abstract class START
	public static abstract class n_pot_func {
		public double THETA;
		abstract public double U(int v);
		public n_pot_func(double theta){
			THETA = theta;
		}
	}
	//abstract class END
	
	public static class n_ising extends n_pot_func{
		public n_ising(double theta){
			super(theta);
		}
		public double U(int v){
			return Math.exp(THETA*v);
		}
	}
	
	//----- EDGE POTENTIAL FUNCTIONS -----
	//abstract class START
	public static abstract class e_pot_func {
		public double THETA;
		abstract public double U(int v1, int v2);
		public e_pot_func(double theta){
			THETA = theta;
		}
	}
	//abstract class END
	
	public static class e_ising extends e_pot_func{
		public e_ising(double theta){
			super(theta);
		}
		public double U(int v1, int v2){
			return Math.exp(THETA*((double)v1)*((double)v2));
		}
	}
	
}
