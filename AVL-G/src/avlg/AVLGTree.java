package avlg;

import avlg.exceptions.UnimplementedMethodException;
import avlg.exceptions.EmptyTreeException;
import avlg.exceptions.InvalidBalanceException;

/** <p>{@link AVLGTree}  is a class representing an <a href="https://en.wikipedia.org/wiki/AVL_tree">AVL Tree</a> with
 * a relaxed balance condition. Its constructor receives a strictly  positive parameter which controls the <b>maximum</b>
 * imbalance allowed on any subtree of the tree which it creates. So, for example:</p>
 *  <ul>
 *      <li>An AVL-1 tree is a classic AVL tree, which only allows for perfectly balanced binary
 *      subtrees (imbalance of 0 everywhere), or subtrees with a maximum imbalance of 1 (somewhere). </li>
 *      <li>An AVL-2 tree relaxes the criteria of AVL-1 trees, by also allowing for subtrees
 *      that have an imbalance of 2.</li>
 *      <li>AVL-3 trees allow an imbalance of 3.</li>
 *      <li>...</li>
 *  </ul>
 *
 *  <p>The idea behind AVL-G trees is that rotations cost time, so maybe we would be willing to
 *  accept bad search performance now and then if it would mean less rotations. On the other hand, increasing
 *  the balance parameter also means that we will be making <b>insertions</b> faster.</p>
 *
 * @author Cheolhong Ahn
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */
	private int count= 0; //for keeping number of elements in tree
	private Node root = null;  //root node of this tree
	private Node prev = null;  //prev used for bst check
	private int maxImbalance =0; 
	private final int COUNT = 10;
	
	private class Node{
		private T key;
		private Node lc;
		private Node rc;
		private int nodeHeight;   //
	}
	
	
	/**
	 * checks whether the tree is BST by in-order traversal
	 * 
	 * @param node  current node
	 * @return true if bst, false if not.
	 */
    private Boolean BST_Check(Node node) {
    	if(node !=null) {
    		if(!BST_Check(node.lc)) {
    			return false;
    		}
    		if(this.prev != null && (node.key.compareTo(this.prev.key) <= 0)) {
    			return false;
    		}
    		this.prev = node;
    		return BST_Check(node.rc);
    	}
    	return true;
    }

    /**
     *  gets in-oder successor key.
     *  
     * @param node  node to get in-order successor value of
     * @return param nodes' in-oder successor node's key value
     */
    private T inorderSuccessor(Node node) {
    	if(node == null) return null;
    	if(node.lc == null) return node.key;
    	
    	return inorderSuccessor(node.lc);
    }
    
    /**
     * traverses tree to find the target.
     * 
     * @param node the current node in traversal
     * @param target stops traversal if node key is equal to target
     * @return true if target found, false if not.
     */
    private Boolean traverse(Node node, T target) { 
    	if (node == null) {
    		return false; 
    	}
    	if (node.key.compareTo(target) == 0) {	
    		return true;
    	}
    	boolean res = traverse(node.lc, target);
    	
    	if(res) {
    		return true;
    	}
    	boolean res2 = traverse(node.rc, target);
    	
    	return res2;
    }

 
    
    
	//returns new root of given subtree
    /**
     * takes in node and rotates left about the node once.
     * 
     * @param node root node of subtree to rotate
     * @return the new root
     */
	private Node rotateLeft(Node node) {
		Node right = node.rc;
		node.rc = right.lc;
		right.lc = node;
		

		
		node.nodeHeight = Math.max(height(node.lc), height(node.rc)) +1;  //updating heights
		right.nodeHeight = Math.max(height(right.lc), height(right.lc)) + 1;
		
		
		return right;
	}
	
	  /**
     *
     * 
     * @param node root node of subtree to rotate
     * @return the new root
     */
	
	private Node rotateLeftRight(Node node) {
		node.lc = rotateLeft(node.lc);
		node = rotateRight(node);
		return node;
	}
	
	/**
	 * takes in node and rotates right about the node once.
	 * 
	 * @param node root node 
	 * @return the new root
	 */
	private Node rotateRight(Node node) {
		Node left = node.lc;
		
		node.lc = left.rc;
		left.rc = node;
		
		node.nodeHeight = Math.max(height(node.lc), height(node.rc))+1;
		left.nodeHeight = Math.max(height(left.lc), height(left.lc)) + 1;
		
		return left;
	}
	/**
	 * 
	 * @param node root node
	 * @return new root
	 */
	private Node rotateRightLeft(Node node) {
		node.rc = rotateRight(node.rc);
		node = rotateLeft(node);
		return node;
	}
	
	/**
	 * 
	 * @param n node to get height
	 * @return returns -1 if node is null, otherwise return it's height field.
	 */
    private int height(Node n) {
        return n == null ? -1 : n.nodeHeight;
    }
	
	
    /**
     * Utility method for insert. 
     * Inserts node as BST tree then balances according to maxImbalance
     *  
     * @param node  the root node
     * @param key   the key to be inserted
     * @return  the unchanged node.
     */
	private Node insertHelper(Node node, T key) {
		if (node == null) {
			Node n = new Node();
			n.key = key;
			return n;
		}else if (key.compareTo(node.key) < 0) {  // key < curr node key
			node.lc = insertHelper(node.lc, key);
			if((height(node.lc) - height(node.rc)) == (this.maxImbalance+1)) {  //avl-g checks for g
				if (key.compareTo(node.lc.key) <= 0) {
					node = rotateRight(node);
				}else{
					node = rotateLeftRight(node);
				}
			}
			node.nodeHeight = Math.max(height(node.lc),height(node.rc)) +1;
		}else{
			node.rc = insertHelper(node.rc, key);
			if((height(node.rc) - height(node.lc)) == (this.maxImbalance+1)) {//avl-g checks for g
				if(key.compareTo(node.rc.key) >= 0) {
					node = rotateLeft(node);
				}else {
					node = rotateRightLeft(node);
				}
			}
			node.nodeHeight = Math.max(height(node.lc),height(node.rc)) +1;
		}
		return node;
	}
	/**
	 * utility method for delete.  
	 * Deletes node according to key then re-balances
	 * 
	 * @param node  takes in root node
	 * @param key   key to delete
	 * @return the new root node
	 */
	private Node deleteHelper(Node node, T key) {
		if(node == null) {
			return node;
		}else if(key.compareTo(node.key) < 0) {     //target less than cur node
			node.lc = deleteHelper(node.lc, key);
		}else if(key.compareTo(node.key) > 0 ){ //target greater than cur node
			node.rc = deleteHelper(node.rc, key);
		}else {
			//node contains one child
			if(node.lc == null) {           
				node = node.rc;
			}else if(node.rc == null){
				node = node.lc;
			}else { //two child, need to find in-order successor
				T successor = inorderSuccessor(node.rc);
				node.key = successor;
				node.rc = deleteHelper(node.rc, node.key);
				node.nodeHeight = Math.max(height(node.lc),height(node.rc)) +1;
			}
		}

		//rebalancing
		if(node != null) {
			node.nodeHeight = Math.max(height(node.lc),height(node.rc)) +1;
    		if(getBalance(node) < -1) {  //right heavy
    			if((height(node.rc) - height(node.lc)) == (this.maxImbalance+1)) {//avl-g checks for g
    				if(getBalance(node.rc) <= 0) {
    					node = rotateLeft(node);
    				}else {
    					node = rotateRightLeft(node);
    				}
    			}
    		}else if(getBalance(node) > 1){ //left heavy
    			if((height(node.lc) - height(node.rc)) == (this.maxImbalance+1)) {	//avl-g checks for g
    				if(getBalance(node.lc) >=0) {
    					node = rotateRight(node);
    				}else {
    					node = rotateLeftRight(node);
    				}
    			}

    		}
    		node.nodeHeight = Math.max(height(node.lc),height(node.rc)) +1;
		}
		return node;
	}
	
	
    /* ******************************************************** *
     * ************************ PUBLIC METHODS **************** *
     * ******************************************************** */
	/**
	 * utility method for printTree.  
	 * traverses tree and print out the tree.
	 * used for testing
	 * 
	 * @param root
	 * @param space  print spacing for each node
	 */
	public void printTreeUtil(Node root, int space) {
        // Base case
        if (root == null)
            return;
 
        // Increase distance between levels
        space += COUNT;
 
        // Process right child first
        printTreeUtil(root.rc, space);
 
        // Print current node after space
        // count
        System.out.print("\n");
        for (int i = COUNT; i < space; i++)
            System.out.print(" ");
        System.out.print(root.key + "\n");
 
        // Process left child
        printTreeUtil(root.lc, space);
    }
	
	/**
	 * prints tree for testing purposes.
	 * 
	 * @param node root node
	 */
	public void printTree(Node node) {
		 printTreeUtil(this.root,0);
	}
	
	/**
	 * getter method used for testing
	 * 
	 * @return right child of root
	 */
    public T getRc() {
    	return this.root.rc.key;
    }

    /**
     * getter method used for testing
     *
     * 
     * @return left child of root
     */
   
    public T getLc() {
    	return this.root.lc.key;
    }
	
    /**
     * getter method for testing
     * 
     * @return root node of the tree
     * @throws EmptyTreeException
     */
    public Node getRootNode() throws EmptyTreeException{
        if (this.root == null) {
        	throw new EmptyTreeException("Tree is empty");
        }else {
        	return this.root;
        }
    }
    
    
	/**
	 * takes in node and returns the balance of the node.
	 * 
	 * @param node  node to get balance of
	 * @return the balance of parameter node
	 */
	public int getBalance(Node node) {
		return (node == null) ? 0 : height(node.lc) - height(node.rc); 
	}
	
	
	
	
    /**
     * The class constructor provides the tree with the maximum imbalance allowed.
     * @param maxImbalance The maximum imbalance allowed by the AVL-G Tree.
     * @throws InvalidBalanceException if maxImbalance is a value smaller than 1.
     */
    public AVLGTree(int maxImbalance) throws InvalidBalanceException {
        if (maxImbalance < 1) {
        	throw new InvalidBalanceException("max Imbalance cannot be smaller than 1");
        }
        else {
        	this.maxImbalance = maxImbalance;
        }
    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * s
     * @param key The key to insert in the tree.
     */
    public void insert(T key) {
        this.root = insertHelper(this.root, key);// ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        this.count++;  //updates number of element in tree.
    }

    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
    
    public T delete(T key) throws EmptyTreeException {
    	if(search(key) ==null) {   //search tree for key,  search method throws empty tree exception if tree is empty
    		return null;
    	}else {
 
    		this.root = deleteHelper(this.root, key); //root after deletion only, no rotation
    		count--;
    		return key;
    	}
    }


    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
        if(this.root == null) {
        	throw new EmptyTreeException("Empty tree");
        }
        else {
        	if (traverse(this.root, key)) {
        		return key;
        	}else {
        		return null;
        	}
        }
    }

    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
       return this.maxImbalance;       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }


    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {
    	return height(this.root);       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
    	if (height(this.root) == -1) {
    		return true;
    	}else {
    		return false;
    	}
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException{
        if (this.root == null) {
        	throw new EmptyTreeException("Tree is empty");
        }else {
        	return this.root.key;
        }
    }

    

    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {
    	this.prev = null; //reset prev incase search was used before.
    	return BST_Check(this.root);
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
    	if(getBalance(this.root) <= this.maxImbalance) {
    		return true;
    	}else
    		return false;
    	
    }

    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear(){
        this.root = null;
        this.count = 0;
        this.prev = null;
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){
        return this.count;       
    }
    

}
