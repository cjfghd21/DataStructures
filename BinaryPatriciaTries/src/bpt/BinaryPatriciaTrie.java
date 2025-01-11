package bpt;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>{@code BinaryPatriciaTrie} is a Patricia Trie over the binary alphabet &#123;	 0, 1 &#125;. By restricting themselves
 * to this small but terrifically useful alphabet, Binary Patricia Tries combine all the positive
 * aspects of Patricia Tries while shedding the storage cost typically associated with tries that
 * deal with huge alphabets.</p>
 *
 * @author Cheolhong Ahn
 */
public class BinaryPatriciaTrie {

    /* We are giving you this class as an example of what your inner node might look like.
     * If you would prefer to use a size-2 array or hold other things in your nodes, please feel free
     * to do so. We can *guarantee* that a *correct* implementation exists with *exactly* this data
     * stored in the nodes.
     */
    private static class TrieNode {
        private TrieNode left, right,prev;
        private String str;
        private boolean isKey;

        // Default constructor for your inner nodes.
        TrieNode() {
            this("", false);
        }

        // Non-default constructor.
        TrieNode(String str, boolean isKey) {
            left = right = prev = null;
            this.str = str;
            this.isKey = isKey;
        }
    }

    private TrieNode root;
    private TrieNode curr;
    private int size =0;
    private int insert_case;   //0 if string match but not key, 1 if hit null, 2 if new is prefix, 3 if sharing common prefix.
    private String add;   //for knowing what to add once search fails.
    /**
     * Simple constructor that will initialize the internals of {@code this}.
     */
    public BinaryPatriciaTrie() {
        this.root = new TrieNode();
        this.curr = this.root;
    }
    
    /**
     * 
     * @param a string to find gcp 
     * @param b string to find gcp
     * @return the greatest common prefix of the two strings.
     */
    public String greatestCommonPrefix(String a, String b) {
        int minLength = Math.min(a.length(), b.length());
        for (int i = 0; i < minLength; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return a.substring(0, i);
            }
        }
        return a.substring(0, minLength);
    }
    
    
    
    /**
     * Searches the trie for a given key.
     *
     * @param key The input {@link String} key.
     * @return {@code true} if and only if key is in the trie, {@code false} otherwise.
     */
    public boolean search  (String key) {
    	this.curr = this.root;
    	boolean res = searchHelp(key);
    	return res;
    }
    
    public boolean searchHelp(String key) {
    	this.add = key;  
        if(key.isEmpty() && this.curr.isKey) {  //key matched
        	this.insert_case = -1;
        	return true;
        }else if(key.isEmpty()){ //all consumed but curr isn't key
        	this.insert_case = 0;
        	return false;
        }else if (!key.isEmpty() && key.charAt(0) == '1' && this.curr.right == null) { //hits null
        	this.insert_case = 1;
        	return false;
        }else if (!key.isEmpty() && key.charAt(0) == '0' && this.curr.left == null) { //hits null
        	this.insert_case = 1;
        	return false;
        }else {
        	int i =0;
        	
        	if(!key.isEmpty() && key.charAt(0) =='1') {  //traverse right
            	//consume string
        		if(key.startsWith(curr.right.str)){ //node fully consumed
        			i = curr.right.str.length();
        			this.add = key; 
        			key = key.substring(i); 
        			this.curr = this.curr.right;
        		}else if(curr.right.str.startsWith(key)){ //new key is prefix 
        			this.insert_case = 2;
        			this.add = key; //current state of key after all previous successful consumption
        			return false;
        		}else { //search fails, new key shares common prefix.
        			this.insert_case = 3;
        			this.add = key;  //current state of key after all previous successful consumption
        			return false;
        		}		
        	}else if(!key.isEmpty() && key.charAt(0) == '0'){   //traverse left
            	//consume string
        		if(key.startsWith(curr.left.str)){ //node fully consumed
        			i = curr.left.str.length();
        			this.add = key;  
        			key = key.substring(i);
        			this.curr = this.curr.left;
        		}else if(curr.left.str.startsWith(key)){ //new key is prefix 
        			this.insert_case = 2;
        			this.add = key; //current state of key after all previous successful consumption
        			return false;
        		}else { //search fails, new key shares common prefix.
        			this.insert_case = 3;
        			this.add = key;  //current state of key after all previous successful consumption
        			return false;
        		}		
        	}
        	boolean res =searchHelp(key);
        	return res;
        }
    }

    /**
     * Inserts key into the trie.
     *
     * @param key The input {@link String}  key.
     * @return {@code true} if and only if the key was not already in the trie, {@code false} otherwise.
     */
    public boolean insert(String key) {
        this.curr = this.root; // resetting current node to root for search.
        if(search(key)) {  //search successful
        	return false;
        }else {
        	if(this.insert_case == 0) { // string found, set key to true
        		this.curr.isKey = true;
        		this.size ++;
        		return true;
        	}else if(this.insert_case == 1) { //hit null
        		if(this.add.charAt(0) == '1') { //node to be added starts with 1
        			curr.right = new TrieNode(add, true);
        			curr.right.isKey = true;
        			curr.right.prev = curr;
        		}else{ //node to be added starts with 0
        			curr.left = new TrieNode(add,true);
        			curr.left.isKey =true;
        			curr.left.prev = curr;
        		}
        		curr = this.root;
        		this.size++;
        		return true;
        	}else if(this.insert_case == 2) { //new is prefix.
        		if(this.add.charAt(0) == '1') { //prefix starts with 1
        			TrieNode tmp = new TrieNode(add, true); // new parent node.
        			tmp.prev = curr;
        			curr.right.str = curr.right.str.substring(add.length()); //removing prefix from existing node
        			if(curr.right.str.charAt(0) == '1'){//after removing, new child starts with 1
        				tmp.right = curr.right;
        				curr.right.prev = tmp;
        				curr.right = tmp;
        			}else {//aftrer removing, new child starts with 0
        				tmp.left = curr.right;
        				curr.right.prev = tmp;
        				curr.right = tmp;
        			}
        		}else{ // prefix starts with 0
        			TrieNode tmp = new TrieNode(add,true);
        			tmp.prev = curr;
        			curr.left.str = curr.left.str.substring(add.length());
           			if(curr.left.str.charAt(0) == '1'){//after removing, new child starts with 1
        				tmp.right = curr.left;
        				curr.left.prev = tmp;
        				curr.left = tmp;
        			}else {//after removing, new child starts with 0
        				tmp.left = curr.left;
        				curr.left.prev = tmp;
        				curr.left = tmp;
        			}
        		}
        		curr = this.root;
        		this.size ++;
        		return true;
        	}else if(this.insert_case == 3){ //shares common prefix.
        		if(this.add.charAt(0) == '1') { //prefix starts with 1
        			String gcp = greatestCommonPrefix(this.add,curr.right.str);
        			int i = gcp.length();
        			curr.right.str = curr.right.str.substring(i);
        			String child_S = this.add.substring(i);
        			
        			TrieNode parent = new TrieNode(gcp,false); //new splitter parent.
        			parent.prev = curr;
        			TrieNode child = new TrieNode(child_S,true); //new child
        			child.prev = parent;
        			
        			if(curr.right.str.charAt(0) == '1') {
        				parent.right = curr.right;
        				curr.right.prev = parent;
        				parent.left = child;
        				curr.right = parent;
        			}else {
        				parent.right = child;
        				parent.left = curr.right;
        				curr.right.prev = parent;
        				curr.right = parent;
        			}
        			
        		}else { //prefix starts with 0
        			String gcp = greatestCommonPrefix(this.add,curr.left.str);
        			int i = gcp.length();
        			curr.left.str = curr.left.str.substring(i);
        			String child_S = this.add.substring(i);
        			
        			TrieNode parent = new TrieNode(gcp,false); //new splitter parent.
        			parent.prev = curr;
        			TrieNode child = new TrieNode(child_S,true); //new child
        			child.prev = parent;
        			
        			if(curr.left.str.charAt(0) == '1') {
        				parent.right = curr.left;
        				curr.left.prev = parent;
        				parent.left = child;
        				curr.left = parent;
        			}else {
        				parent.right = child;
        				parent.left = curr.left;
        				curr.left.prev = parent;
        				curr.left = parent;
        			}
        		}
        		curr = this.root;
    			this.size++;
    			return true;
        	}
        	curr = this.root;
        	return false;
        }
    }


    /**
     * Deletes key from the trie.
     *
     * @param key The {@link String}  key to be deleted.
     * @return {@code true} if and only if key was contained by the trie before we attempted deletion, {@code false} otherwise.
     */
    public boolean delete(String key) {
        this.curr = this.root;
        if(!search(key)) {  //search unsuccessful;
        	return false;
        }else {
        	if(curr.left !=null && curr.right!=null){//node has more than one child
        		this.curr.isKey = false;
        		this.size--;
        	}else if((curr.left == null || curr.right == null)&&(curr.left!=null||curr.right!=null)){ //node only one child
        		if(curr.left != null){ //left child non null, merging with lc
        			curr.str = curr.str + curr.left.str;
        			curr.isKey = curr.left.isKey;
         			curr.right =curr.left.right;
        			curr.left = curr.left.left;
        		}else {//right child non null
        			curr.str = curr.str + curr.right.str;
        			curr.isKey = curr.right.isKey;
        			curr.left = curr.right.left;
        			curr.right = curr.right.right;
        		}
        		this.size--;
        	}else {// node has no children
        		if(curr.str.charAt(0) == '0') {
        			curr.prev.left = null;
        			curr = curr.prev;
        		}else {
        			curr.prev.right = null;
        			curr = curr.prev;
        		}
        		this.size--;
        		if(curr != null) {
        			deleteHelper(curr);
        		}
        	}
        	
        	return true;
        }
        
    }
    
    public void deleteHelper(TrieNode n){
    	//current is not key and has only one child
    	if(!curr.isKey && (curr.left == null || curr.right == null)&&(curr.left!=null||curr.right!=null) && !curr.str.isEmpty()) {
    		if(curr.left != null){ //left child non null, merging with lc
    			curr.str = curr.str + curr.left.str;
    			curr.isKey = curr.left.isKey;
    			curr.right =curr.left.right;
    			curr.left = curr.left.left;	
    		}else {//right child non null
    			curr.str = curr.str + curr.right.str;
    			curr.isKey = curr.right.isKey;
    			curr.left = curr.right.left;
    			curr.right = curr.right.right;
    		}
    		
    		if(curr.prev != null){
    			curr = curr.prev;
    			deleteHelper(curr);
    		}
    	}
    }
    	

    /**
     * Queries the trie for emptiness.
     *
     * @return {@code true} if and only if {@link #getSize()} == 0, {@code false} otherwise.
     */
    public boolean isEmpty() {
        if(this.getSize() == 0) {
        	return true;
        }
        return false;
    }

    /**
     * Returns the number of keys in the tree.
     *
     * @return The number of keys in the tree.
     */
    public int getSize() {
       	return this.size;
    }
    
 
    /**
     * <p>Performs an <i>inorder (symmetric) traversal</i> of the Binary Patricia Trie. Remember from lecture that inorder
     * traversal in tries is NOT sorted traversal, unless all the stored keys have the same length. This
     * is of course not required by your implementation, so you should make sure that in your tests you
     * are not expecting this method to return keys in lexicographic order. We put this method in the
     * interface because it helps us test your submission thoroughly and it helps you debug your code! </p>
     *
     * <p>We <b>neither require nor test </b> whether the {@link Iterator} returned by this method is fail-safe or fail-fast.
     * This means that you  do <b>not</b> need to test for thrown {@link java.util.ConcurrentModificationException}s and we do
     * <b>not</b> test your code for the possible occurrence of concurrent modifications.</p>
     *
     * <p>We also assume that the {@link Iterator} is <em>immutable</em>, i,e we do <b>not</b> test for the behavior
     * of {@link Iterator#remove()}. You can handle it any way you want for your own application, yet <b>we</b> will
     * <b>not</b> test for it.</p>
     *
     * @return An {@link Iterator} over the {@link String} keys stored in the trie, exposing the elements in <i>symmetric
     * order</i>.
     */
    public Iterator<String> inorderTraversal() {
        ArrayList<String> s = new ArrayList<String>();
        s = traversalHelper(s,this.root,"");
        return s.iterator();
        
    }
    
    private ArrayList<String> traversalHelper(ArrayList<String> list, TrieNode n, String c) {
    	if (n == null) {
    		return list;
    	}
    	c = c + n.str;
    	traversalHelper(list,n.left,c);
    	if(n.isKey) {
    		list.add(c);
    	}
    	traversalHelper(list,n.right,c);
    	
    	return list;
    }

    /**
     * Finds the longest {@link String} stored in the Binary Patricia Trie.
     * @return <p>The longest {@link String} stored in this. If the trie is empty, the empty string &quot;&quot; should be
     * returned. Careful: the empty string &quot;&quot;is <b>not</b> the same string as &quot; &quot;; the latter is a string
     * consisting of a single <b>space character</b>! It is also <b>not the same as the</b> null <b>reference</b>!</p>
     *
     * <p>Ties should be broken in terms of <b>value</b> of the bit string. For example, if our trie contained
     * only the binary strings 01 and 11, <b>11</b> would be the longest string. If our trie contained
     * only 001 and 010, <b>010</b> would be the longest string.</p>
     */
    public String getLongest() {
        Iterator<String> s = this.inorderTraversal();
        String res = "";
        while(s.hasNext()) {
        	String tmp = s.next();
        	if(res.length() < tmp.length()) {
        		res = tmp;
        	}else if(res.length() == tmp.length()) {
        		if(Integer.parseInt(res) < Integer.parseInt(tmp)) {
        			res = tmp;
        		}
        	}
        }
        return res;
    }

    /**
     * Makes sure that your trie doesn't have splitter nodes with a single child. In a Patricia trie, those nodes should
     * be pruned.
     * @return {@code true} iff all nodes in the trie either denote stored strings or split into two subtrees, {@code false} otherwise.
     */
    public boolean isJunkFree(){
        return isEmpty() || (isJunkFree(root.left) && isJunkFree(root.right));
    }

    private boolean isJunkFree(TrieNode n){
        if(n == null){   // Null subtrees trivially junk-free
            return true;
        }
        if(!n.isKey){   // Non-key nodes need to be strict splitter nodes
            return ( (n.left != null) && (n.right != null) && isJunkFree(n.left) && isJunkFree(n.right) );
        } else {
            return ( isJunkFree(n.left) && isJunkFree(n.right) ); // But key-containing nodes need not.
        }
    }
}
