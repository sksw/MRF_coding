import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

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
	
	//euclidean length
	public static double l_euclidean(double[]a){
		double temp = 0.0;
		for(int i=0; i<a.length; i++)
			temp = temp + Math.pow(a[i],2);
		return Math.pow(temp,0.5);
	}
	
	//euclidean distance
	public static double d_euclidean(double[] a, double[] b){
		if(a.length != b.length)
			return Double.NaN;
		else{
			double temp = 0.0;
			for(int i=0; i<a.length; i++)
				temp = temp + Math.pow(a[i]-b[i],2);
			return Math.pow(temp,0.5);
		}
	}
	
    public static <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<T>();
        set.addAll(list1);
        set.addAll(list2);
        return new ArrayList<T>(set);
    }

    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();
        for (T t : list1)
            if(list2.contains(t))
                list.add(t);
        return list;
    }
	
	public static int map_NxNtoN(int x, int y){
		return (x+y)*(x+y+1)/2+y;
	}
	
	public static int map_ZtoN(int k){
		if(k<0)
			return -k*2-1;
		else
			return k*2;
	}
	
	public static int factorial(int n){
		if(n==0) return 1;
		return n*factorial(n-1);
	}
	
	public static int comb(int n, int k){
		return factorial(n)/(factorial(k)*factorial(n-k));
	}
	
	public static double[] cdf(double[] pmf){
		double[] cdf = new double[pmf.length];
		cdf[0] = pmf[0];
		if(pmf.length==1)
			return cdf;
		for(int i=1; i<cdf.length; i++)
			cdf[i] = cdf[i-1]+pmf[i];
		return cdf;
	}
	
	public static int draw(double[] cdf){
		double num = Math.random();
		//should replace this squential search if r big
		for(int i=0; i<cdf.length; i++)
			if(num<cdf[i])
				return i;
		return cdf.length-1;
	}
	
	
	
	public static String[] fileList(String dir, String extension){
		File fDir = new File(dir);
		FilenameFilter filter = new fileExtFilter(extension);
		return fDir.list(filter);
	}
	
	public static class fileExtFilter implements FilenameFilter{
		String ext;
		public fileExtFilter(String extension){
			ext = extension;
		}
	    public boolean accept(File dir, String name){
	        return name.endsWith(ext);
	    }
	}
    
}
