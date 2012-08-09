import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Experiments {
	
	public static void main(String[] args){
		
		ArrayList<Edge> E;
		ArrayList<Node> V;
		
		ArrayList<Node> sv;
		
		ArrayList<SuperEdge> SE;
		ArrayList<SuperNode> SV;

		int thickness = 2;
		int range = 2;
		
		Graph G, Gtemp;
		
		String file = "test.png";
			
		System.out.println("---------- image io ----------");
		BufferedImage Test = ImageDAQ.to_CS_GRAY(ImageDAQ.loadImage("img",file));
		//ImageOp.printImg(Test,"CS_GRAY");
//		G = ImageOp.dec_Graph(Test,"BILEVEL");
//		G.make4ptEdges(range);
//		System.out.println("<IMPORTED IMAGE INFO> height: "+G.h+" width: "+G.w);
		//outputs imported image
		/*for(int i=0;i<G.w;i++){
			System.out.println();
			for(int j=0;j<G.h;j++){
				System.out.print(G.V.get(i*G.h+j).N_VAL + " ");
			}
		}*/
		
		String data = "";
//		for(int i=0; i<G.V.size(); i++)
//			data = data+G.V.get(i).val;
		
		String test;
		
		/*
		
		test = "";
		System.out.println("---------- RLE raw ----------");
		test = Coder.RLE(data,9);
		System.out.println(file + " ORIGIN: "+G.V.size()+" NEW: "+test.length() + " (" + (double)test.length()/(double)G.V.size() + ") PARAMETER(S): ");
		// RESULTS
		// test_OLD.png ORIGIN: 54516 NEW: 24887 (0.4565081810844523) PARAMETER(S): 8
		// test.png ORIGIN: 262144 NEW: 120560 (0.45989990234375) PARAMETER(S): 16
		// test.png ORIGIN: 262144 NEW: 120560 (0.45989990234375) PARAMETER(S): 16
		// test.png ORIGIN: 262144 NEW: 68535 (0.2614402770996094) PARAMETER(S): 9
		//
		
		test = "";
		System.out.println("---------- huffmanEE ----------");
		test = Coder.huffmanEE(data,24);
		//System.out.println(test);
		System.out.println(file + " ORIGIN: "+G.V.size()+" NEW: "+test.length() + " (" + (double)test.length()/(double)G.V.size() + ") PARAMETER(S): ");
		// RESULTS
		// test_OLD.png ORIGIN: 54516 NEW: 15512 (0.28454031843862354) PARAMETER(S): 24
		// test.png ORIGIN: 262144 NEW: 58235 (0.22214889526367188) PARAMETER(S): 16
		// test.png ORIGIN: 262144 NEW: 53443 (0.20386886596679688) PARAMETER(S): 24
		//
		
		test = "";
		System.out.println("---------- RLE huffmanEE ----------");
		test = Coder.huffmanEE(Coder.RLE(data,8),24);
		System.out.println(file + " ORIGIN: "+G.V.size()+" NEW: "+test.length() + " (" + (double)test.length()/(double)G.V.size() + ") PARAMETER(S): ");
		// RESULTS
		// test_OLD.png ORIGIN: 54516 NEW: 9529 (0.1747927214028909) PARAMETER(S): 7,24
		// test.png ORIGIN: 262144 NEW: 54461 (0.20775222778320312) PARAMETER(S): 16,16
		// test.png ORIGIN: 262144 NEW: 31009 (0.11828994750976562) PARAMETER(S): 8,24
		//
		
		*/
		
		System.out.println("---------- Empirical Conditional Entropy ----------");
		System.out.println(file + " EMPIRICAL CONDITIONAL ENTROPY: (" + Coder.empiricalConditionalEntropy(data,5,2) + ") PARAMETER(S): ");


		// RESULTS - IMG_JT_AC
		// 2: 0.06611076942342578
		// 3: 0.07171558264970233
		// 4: 0.0753215425221591
		// 5: 0.07778317975247692
		// 6: 0.08035852744263765
		// 7: 0.08350433138796429
		// 8: 0.08459631712296141
		// 9: 0.08626900885126157
		//
		
		// PRECODING RESULTS - IMG_JT_AC using 1.20
		// 2: 0.05338742932235417
		
		/*RESULTS
		 * test_OLD.png
		 * line spacing 2:  0.6214348257928058
		 * line spacing 3:  0.47046918939582283
		 * line spacing 4:  0.3923002583786618
		 * line spacing 5:  0.36578375577178557
		 * line spacing 6:  0.3460954078168455
		 * line spacing 7:  0.3296973399004556
		 */
		
		/*RESULTS
		 * test.png
		 * line spacing 2:  
		 * line spacing 3:  
		 * line spacing 4:  
		 * line spacing 5:  
		 * line spacing 6:  
		 * line spacing 7: 
		 */
	}

}
