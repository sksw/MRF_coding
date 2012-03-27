import java.util.Map;

public class MRF {
	
	public Map<String, int[][]> n_struct; //neighbourhood structures for this MRF
	public graph_struct.img_struct graph_struct;
	//find cliques function returns list of cliques in sample output for user to copy and make potentials
	//i then code it into a map of cliques and potential functions
	
	//need to add a map between clique names and clique configurations
	
	//list of parameters in the potential functions?
	//this should be named U - a mapping of pot_names to potential functions
		//Potential object should contain - string containing clique name, potential function, tune-able parameter for potential function
	public Map<String,Object> THETA; //MRF parameters - dictionary with string specifying potential type and object specifying potential function
	
	//this should be a function of potentials U
	public Object p_xy;
	
	//this should be a function of potentials U (specifically, the name)
	public Object LS_est;
	
	//a data collector - should be a function of potentials U (this is a very frequentist approach)
	public Object stats;

	public MRF(Map<String,Object> params){
		THETA = params;		graph_struct = new graph_struct.ising4pt();
	}

}
