import java.util.Map;

public class MRF {
	
	public Map<String, int[][]> n_struct; //neighbourhood names <--> neighbourhood structures
	public Map<String, CliqueStructures.CliquePair> c_struct; //clique names <--> clique structures, clique definitions

	//this should be a function of potentials U
	public Object p_xy;
	
	//this should be a function of potentials U (specifically, the name)
	public Object LS_est;
	
	//a data collector - should be a function of potentials U (this is a very frequentist approach)
	public Object stats;

	public MRF(){
		
	}

}
