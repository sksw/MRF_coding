public class Coder {

	// ******************** METHODS FOR QUICKLY CALCULATING APPROXIMATE CODEWORD LENGTHS ******************** //
	
	public static double log2_1overpx(double px){
		//px is probability
		return Math.log(1.0/px)/Math.log(2);
	}

	// ******************** ACTUAL ENCODING METHODS ******************** //
	
	public static String RLE(String Data, int packetWidth){
		String EncodedData = "";
		
		int maxLen = (int)Math.pow(2,packetWidth-1);
		int run = 1;
		char val = Data.charAt(0);
		for(int i=1; i<Data.length(); i++)
			if(Data.charAt(i) == val){
				if(run==maxLen){
					EncodedData = EncodedData + val + Utilities.intToPBS(run,packetWidth-1);
					run = 1;
				}
				else
					run++;
			}
			else{
				EncodedData = EncodedData + val + Utilities.intToPBS(run,packetWidth-1);
				val = Data.charAt(i);
				run = 1;
			}
		return EncodedData;
	}
	
	public static int[][] empiricalEntropy(String Data, int blockSize){
		int[] relFreq = new int[(int)Math.pow(2,blockSize)];
		for(int i=0; i<relFreq.length; i++)
			relFreq[i] = 0;
		int n = 0;
		int ch;
		for(int i=1; i*blockSize<Data.length(); i++){
			ch = Integer.parseInt(Data.substring((i-1)*blockSize,i*blockSize),2);
			//add count if a new character is found, this is to help clean up redundant characters later
			if(relFreq[ch]==0)
				n++;
			relFreq[ch]++;
		}
		//remove redundancies
		int[][] cleanRelFreq = new int[2][n]; //row 0 contains character value, row 1 contains frequency
		n = 0;
		for(int i=0; i<relFreq.length; i++)
			if(relFreq[i]!=0){
				cleanRelFreq[0][n] = i;
				cleanRelFreq[1][n] = relFreq[i];
				n++;
			}

		return cleanRelFreq;
	}
	
	public static double empiricalConditionalEntropy(String data, int blockLength, int markovOrder){
		String prevBlocks = "";
		int total,sum;
		double empCondEntropy;
		int[][] relFreqCond = new int[(int)Math.pow(2,blockLength*markovOrder)][(int)Math.pow(2,blockLength)];
		int[] relFreqJoint = new int[(int)Math.pow(2,blockLength*(markovOrder+1))];
		
		total = 0;
		for(int i=0; (i+1)*blockLength < data.length() ;i++){
			if(i<markovOrder)
				prevBlocks = prevBlocks + data.substring(i*blockLength,(i+1)*blockLength);
			else{
				relFreqCond[Integer.parseInt(prevBlocks,2)][Integer.parseInt(data.substring(i*blockLength,(i+1)*blockLength),2)]++;
				relFreqJoint[Integer.parseInt(prevBlocks+data.substring(i*blockLength,(i+1)*blockLength),2)]++;
				total++;
				prevBlocks = prevBlocks.substring(blockLength) + data.substring(i*blockLength,(i+1)*blockLength);
			}
		}
		
		empCondEntropy = 0.0d;
		for(int i=0; i<(int)Math.pow(2,blockLength*markovOrder); i++){
			sum = 0;
			for(int j=0; j<(int)Math.pow(2,blockLength); j++)
				sum = sum + relFreqCond[i][j];
			for(int j=0; j<(int)Math.pow(2,blockLength); j++)
				if(relFreqCond[i][j]!=0)
					empCondEntropy = empCondEntropy + ((double)relFreqJoint[Integer.parseInt(Utilities.intToPBS(i,blockLength*markovOrder)+Utilities.intToPBS(j,blockLength),2)]/(double)total)
							*Math.log(sum/relFreqCond[i][j])/Math.log(2);
		}
		
		return empCondEntropy;
	}
	
	public static String huffmanEE(String Data, int blockSize){
		//find empirical entropy (cleaned version, removes redundancies)
		int[][] relFreq = empiricalEntropy(Data,blockSize);
		//make huffman tree
		Huffman.Tree huffmanTree = Huffman.makeTree(relFreq[1]);
		//make encoder look up table
		Huffman.Code encoder = new Huffman.Code(huffmanTree,relFreq[0]);
		//encode
		//System.out.println(encoder.toString());
		String EncodedData = "";
		for(int i=1; i*blockSize<Data.length(); i++)
			EncodedData = EncodedData + encoder.code(Integer.parseInt(Data.substring((i-1)*blockSize,i*blockSize),2));
		return EncodedData;
	}
	
	
	
	public static double elias(String Data, String DGO){ //delta, gamma, or omega
		return 0.0;
	}
	
	public static double LZ(String Data){
		return 0.0;
	}

}
