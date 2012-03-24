
public class Utilities {

	public static void main(String[] args) {
		String newStr;
		System.out.println(newStr=toPString(7,5,3));
		System.out.println(Integer.parseInt(newStr,3));
	}
	
	//converts integer to padded string of a certain radix
	public static String toPString(int n, int len, int radix){
		String PBS = Integer.toString(n,radix);
		int pad = len - PBS.length();
		for(int i=0; i<pad ;i++)
			PBS = "0"+PBS;
		return PBS;
	}
	
	//normalize double vector
	public static void normalize(double[] a){
		double temp = 0.0;
		for(int i=0; i<a.length; i++)
			temp = temp + a[i];
		for(int i=0; i<a.length; i++)
			a[i] = a[i]/temp;
	}
	
}
