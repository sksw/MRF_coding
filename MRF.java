import java.util.Map;

public class MRF {
	
	public String I_TYPE; //interaction type (i.e. ising)
	public String N_TYPE; //neighbourhood structure type (i.e. 4 point)
	public String OPTION; //option (i.e. equipotential)
	public Map<String,Object> THETA; //MRF parameters - dictionary with string specifying potential type and object specifying potential function
	public graph_struct.img_struct graph_struct;
	public int min_col_spacing;
	
	public Object p_xy;
	public Object LS_est;
	public Object stats;

	public MRF(String interaction, String neighbourhood, String option, Map<String,Object> params){
		I_TYPE = interaction;
		N_TYPE = neighbourhood;
		OPTION = option;
		THETA = params;
		if(I_TYPE.equals("ising"))
			if(N_TYPE.equals("4pt"))
				graph_struct = new graph_struct.ising4pt();
	}

}
