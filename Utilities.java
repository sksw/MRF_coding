
public class Utilities {

	public Utilities(){
	}

	public static String intToPBS(int n, int len){ //PBS stands for padded binary string
		int pad = len - Integer.toBinaryString(n).length();
		String PBS = "";
		for(int i=0; i<pad ;i++)
			PBS = PBS + Integer.toString(0);
		PBS = PBS + Integer.toBinaryString(n);
		return PBS;
	}
	
}
