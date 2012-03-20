import java.util.Stack;

class Huffman{

	static abstract class Tree{
		int weight; Tree head;
    }
	
	static class Node extends Tree{
		Tree left, right;
		
		public Node(Tree l, Tree r, int w){
			left = l; right = r; weight = w;
		}
	}

	static class Leaf extends Tree{
		int ch;

		public Leaf(int c, int w){
			ch = c; weight = w;
		}

		public String toString(){
        	return "#" + ch;
        }
	}

	static class Code {
		String[] code;
		int[] chars;
		
		public Code(Tree tree, int[] data) { //data contains the look up table to match integer indices in huffman tree to data
			Stack<Tree> bfs = new Stack();
			code = new String[data.length];
			chars = new int[data.length];
			
			//search for each item
			for(int i=0; i<data.length; i++){
				chars[i] = data[i];
				bfs.push(tree);
				//breadth first search
				while(!bfs.empty()){
					Tree root = bfs.pop();
					//reached leaf
					if(root instanceof Leaf){
						//found item
						if( ((Leaf)root).ch == i ){
							code[i] = "";
							//back trace to make codeword
							while(root.head!=null){
								if( ((Node)root.head).left == root){
									code[i] = "0"+code[i];
								}
								else
									code[i] = "1"+code[i];
								root = root.head;
							}
							//empty stack
							while(!bfs.empty())
								bfs.pop();
						}
					}
					//not on leaf, on node - queue up the children for search
					else{
						bfs.push( ((Node)root).left );
						bfs.push( ((Node)root).right );
					}
				}
			}
			sort(0,chars.length-1);
		}
		
		public String toString(){
			String codeWords = "";
			for(int i=0; i<chars.length; i++)
				codeWords = codeWords + chars[i] + ":" + code[i] + "\n";
			return codeWords;
		}
		
		public String code(int ch){
			return search(ch,0,chars.length-1);
		}
		
		// binary search
		private String search(int ch, int left, int right){
			int pivot = (right+left)/2;
			if(ch == chars[pivot])
				return code[pivot];
			else if(right-left <= 0)
				return "["+ch+"]";
			else if(ch > chars[pivot])
				return search(ch,pivot+1,right);
			else
				return search(ch,left,pivot-1);
		}
		
		// quicksort
		private void sort(int left, int right) {
			int l=left, r=right;
			int pivot = chars[(right+left)/2];
			do{
				while(chars[l] < pivot)
					l++;
				while(chars[r] > pivot)
					r--;
				if(l<=r){
					swap(l,r);
					l++;
					r--;
				}
			}while(l<=r);
			if(left < r)
				sort(left,r);
			if(l < right)
				sort(l,right);
		}
		
		private void swap(int x, int y){
			int ch;
			ch = chars[y];
			chars[y] = chars[x];
			chars[x] = ch;
			String cd;
			cd = code[y];
			code[y] = code[x];
			code[x] = cd;
		}
	}

	public static Tree makeTree(int[] data){
		Tree root[] = new Tree[data.length];
		int n = 0;
		for(int i=0; i<data.length; i++)
			if(data[i] != 0)
        		root[n++] = new Leaf(i,data[i]);

		while(n>1){
			//start by assuming least-weight indices are at 0,1 and arrange appropriately
			int least = 0; //indices of 2 least-weight roots
			int small = 1;
			if(root[least].weight > root[small].weight){
				least = 1;
                small = 0;
			}
			//search through list for lesser weighted items and replace appropriately
			for(int i=2; i<n; i++)
				if(root[i].weight < root[least].weight) {
					small = least;
					least = i;
                }
				else if(root[i].weight < root[small].weight)
					small = i;
			//combine least weighted items into "least" slot, remove obsolete item small
			int combinedWeight = root[least].weight + root[small].weight;
			Node parent = new Node(root[least],root[small],combinedWeight);
			parent.left.head = parent;
			parent.right.head = parent;
			root[least] = parent;
			for(int i=small+1; i<n; i++)
				root[i-1] = root[i];
			n--;
		}
		
        return root[0];
    }
	
	
	
}
