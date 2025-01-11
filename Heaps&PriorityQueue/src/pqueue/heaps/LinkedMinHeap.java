package pqueue.heaps; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORT IS NECESSARY FOR THE ITERATOR() METHOD'S SIGNATURE. FOR THIS
 * REASON, YOU SHOULD NOT ERASE IT! YOUR CODE WILL BE UNCOMPILABLE IF YOU DO!
 * ********************************************************************************** */

import java.util.Iterator;

import pqueue.exceptions.UnimplementedMethodException;



/**
 * <p>A {@link LinkedMinHeap} is a tree (specifically, a <b>complete</b> binary tree) where every node is
 * smaller than or equal to its descendants (as defined by the {@link Comparable#compareTo(Object)} overridings of the type T).
 * Percolation is employed when the root is deleted, and insertions guarantee maintenance of the heap property in logarithmic time. </p>
 *
 * <p>You <b>must</b> edit this class! To receive <b>any</b> credit for the unit tests related to this class,
 * your implementation <b>must</b> be a &quot;linked&quot;, <b>non-contiguous storage</b> implementation based on a
 * binary tree of nodes and references. Use the skeleton code we have provided to your advantage, but always remember
 * that the only functionality our tests can test is {@code public} functionality.</p>
 * 
 * @author --- Cheolhong Ahn! ---
 *
 * @param <T> The {@link Comparable} type of object held by {@code this}.
 *
 * @see MinHeap
 * @see ArrayMinHeap
 */
public class LinkedMinHeap<T extends Comparable<T>> implements MinHeap<T> {

	/* ***********************************************************************
	 * An inner class representing a minheap's node. YOU *SHOULD* BUILD YOUR *
	 * IMPLEMENTATION ON TOP OF THIS CLASS!                                  *
 	 * ********************************************************************* */
	private class MinHeapNode {
		private T data;
		private MinHeapNode lChild, rChild;

        /* *******************************************************************
         * Write any further data elements or methods for MinHeapNode here...*
         ********************************************************************* */
		private MinHeapNode parent;
	}

	/* *********************************
	  * Root of your tree: DO NOT ERASE!
	  * *********************************
	 */
	private MinHeapNode root;


    /* *********************************************************************************** *
     * Write any further private data elements or private methods for LinkedMinHeap here...*
     * *************************************************************************************/
	private int size;
	private int modify_counter=0;
	private MinHeapNode sub_root;  //root of subtree
	private MinHeapNode last;      //bottom rightmost node
	
	private int tree_height(int size) {
	    return (int) Math.ceil(log2(size + 1));
	}
	
	// returns the amount of space in the bottom row of a binary tree
	private int bottomRowSpace(int height) {
	    return (int) Math.pow(2, height - 1);
	}
	
	// returns the amount of filled spots in the bottom row of a binary tree
	private int bottomRowFilled(int size) {
	    return size - (bottomRowSpace(tree_height(size)) - 1);
	}
	
	// log base2
	private double log2(double a) {
	    return Math.log(a) / Math.log(2);
	}

    /* *********************************************************************************************************
     * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
     ***********************************************************************************************************/
	
	/**
	 * Default constructor.
	 */
	public LinkedMinHeap() {
		this.root = null;
		this.size = 0;
	}

	/**
	 * Second constructor initializes {@code this} with the provided element.
	 *
	 * @param rootElement the data to create the root with.
	 */
	public LinkedMinHeap(T rootElement) {
		this.root = new MinHeapNode();
		this.root.data = rootElement;
		this.root.lChild = null;
		this.root.rChild = null;
		this.root.parent = null;
		this.last = root;
		this.size = 1;

	}
	/**
	 * Copy constructor initializes {@code this} as a carbon
	 * copy of the parameter, which is of the general type {@link MinHeap}!
	 * Since {@link MinHeap} is an {@link Iterable} type, we can access all
	 * of its elements in proper order and insert them into {@code this}.
	 *
	 * @param other The {@link MinHeap} to copy the elements from.
	 */
	
	
	/*public LinkedMinHeap(MinHeap<T> other) {
		LinkedMinHeap<T> minheap = (LinkedMinHeap<T>) other;
		Iterator itOther = minheap.iterator();
		while (itOther.hasNext()) {
			itOther.next()
		}
			
	}
	
	implement later, next should return node, create new node accordingly */


    /**
     * Standard {@code equals} method. We provide this for you. DO NOT EDIT!
     * You should notice how the existence of an {@link Iterator} for {@link MinHeap}
     * allows us to access the elements of the argument reference. This should give you ideas
     * for {@link #LinkedMinHeap(MinHeap)}.
     * @return {@code true} If the parameter {@code Object} and the current MinHeap
     * are identical Objects.
     *
     * @see Object#equals(Object)
     * @see #LinkedMinHeap(MinHeap)
     */
	/**
	 * Standard equals() method.
	 *
	 * @return {@code true} If the parameter Object and the current MinHeap
	 * are identical Objects.
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MinHeap))
			return false;
		Iterator itThis = iterator();
		Iterator itOther = ((MinHeap) other).iterator();
		while (itThis.hasNext())
			if (!itThis.next().equals(itOther.next()))
				return false;
		return !itOther.hasNext();
	}

	@Override
	public boolean isEmpty() {
		if (this.size == 0)
			return true;
		else
			return false;
	}

	@Override
	public int size() {
		return this.size;
	}


	@Override
	public void insert(T element) {
		modify_counter++;
		
		
		MinHeapNode add = new MinHeapNode();
		add.data = element;
		
		if(this.root == null) { //empty list
			this.root = add;
			this.last = add;
			this.size ++;
		}
		else {
		  sub_root = this.root;
		  int subSize = this.size;
		  int height = tree_height(subSize);
		  
		  //inserting at bottom rightmost space
		  while(sub_root.lChild != null && sub_root.rChild !=null) {  //both child of root filled, thus traversing down the tree
			  if ((bottomRowSpace(height)/((float)(bottomRowFilled(subSize))) >= (0.5)) && (bottomRowSpace(height)/((bottomRowFilled(subSize))) != 1)) {   //more than half of bottom level leaf is filled, but not all filled, thus traversing right.
				  sub_root.rChild.parent = sub_root; //assign parent.
				  sub_root = sub_root.rChild;
				  subSize = ((((int) Math.pow(2,height))-1)/2) - ( bottomRowSpace(height)- bottomRowFilled(subSize));    // floor((2^h-1)/2) - (2^(h-1) - #leaves)     updating the size of new root.
				  height -= 1;
			  }else {   //less than half filled thus traversing left
				  sub_root.lChild.parent = sub_root; //assigning parent.
				  sub_root = sub_root.lChild;
				  subSize = ((((int) Math.pow(2,height))-1)/2) - ((bottomRowSpace(height)/2)- bottomRowFilled(subSize));
				  height-=1;
			  }
		  }
		  if (sub_root.lChild == null) { //left child of root not filled, adding to left 
			  sub_root.lChild = add;
			  add.parent = sub_root;
			  this.size++;
			  this.last = add;
		  }else { //right child of root not filled, adding to right
			  sub_root.rChild = add;
			  add.parent = sub_root;
			  this.size++;
			  this.last = add;
		  } //finish inserting at right most bottom
		  
		  //heapifying
		  height = tree_height(this.size);
		  while (height > 1) {  //loop until root level
			  if(add.data.compareTo(add.parent.data) <0) {  //parent node bigger, swapping data with parent node.
				  T tmp = add.parent.data;
				  add.parent.data = add.data;
				  add.data = tmp;
				  height-=1;
			  }else {  //node in correct pos, break from loop
				  break;
			  }  
		  }
		}
	}

	@Override
	public T getMin() throws EmptyHeapException {		// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(isEmpty()) {
			throw new EmptyHeapException("Empty heap: no min");
		}else {
			return this.root.data;
		}
	}

	@Override
	public T deleteMin() throws EmptyHeapException {    // DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(isEmpty()) {
			throw new EmptyHeapException("Empty heap: cannot delete empty heap");
		}
		else {
			modify_counter++;
			T ret = this.root.data;
			if(this.size == 1) { //only root node exist.
				this.root = null;
				this.size--;
				return ret;
			}else {
				
				T tmp = root.data;//swapping data of root and last node 
				this.root.data = this.last.data;
				this.last.data = tmp;
				
				//deleting last node
				if(this.last.parent.rChild != null) {  //last nodes' parent has rightchild thus rightchild of parent is the last node.
					last.parent.rChild = null;
					last = last.parent.lChild;
					size --;
				}else {   //last nodes' parent does not have right child, thus last node is left node of it's parent
					last.parent.lChild = null;
					last = last.parent;
					size --;
				}
				
				//re-heapifiyng
				MinHeapNode smaller = new MinHeapNode(); //to point to smaller child
				this.sub_root = this.root; //current node
				while (this.sub_root.lChild != null) { //node has left child
					if(this.sub_root.rChild != null) { //node has right child
						smaller = this.sub_root.lChild.data.compareTo(this.sub_root.rChild.data) < 0 ? this.sub_root.lChild : this.sub_root.rChild; //smaller child
					}
					else {
						smaller = this.sub_root.lChild; // only left child thus smallest child is left child.
					}
					if ((smaller.data.compareTo(sub_root.data)) <0) {  //smaller child data smaller than current node.
						T tmp2 = sub_root.data;        //swapping the two data
						sub_root.data = smaller.data;
						smaller.data = tmp2;
						sub_root = smaller;  //smaller child is now the curr node.
					}
					else { //node in correct position 
						return ret;
					}	
				}
				return ret;	

				
			}
		}
	}	
	



	@Override
	public Iterator<T> iterator() {
		throw new UnimplementedMethodException();
	}

	
}


