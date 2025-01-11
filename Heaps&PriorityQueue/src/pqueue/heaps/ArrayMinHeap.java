package pqueue.heaps; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORT IS NECESSARY FOR THE ITERATOR() METHOD'S SIGNATURE. FOR THIS
 * REASON, YOU SHOULD NOT ERASE IT! YOUR CODE WILL BE UNCOMPILABLE IF YOU DO!
 * ********************************************************************************** */

import pqueue.exceptions.UnimplementedMethodException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;


/**
 * <p>{@link ArrayMinHeap} is a {@link MinHeap} implemented using an internal array. Since heaps are <b>complete</b>
 * binary trees, using contiguous storage to store them is an excellent idea, since with such storage we avoid
 * wasting bytes per {@code null} pointer in a linked implementation.</p>
 *
 * <p>You <b>must</b> edit this class! To receive <b>any</b> credit for the unit tests related to this class,
 * your implementation <b>must</b> be a <b>contiguous storage</b> implementation based on a linear {@link java.util.Collection}
 * like an {@link java.util.ArrayList} or a {@link java.util.Vector} (but *not* a {@link java.util.LinkedList} because it's *not*
 * contiguous storage!). or a raw Java array. We provide an array for you to start with, but if you prefer, you can switch it to a
 * {@link java.util.Collection} as mentioned above. </p>
 *
 * @author -- Cheolhong Ahn ---
 *
 * @see MinHeap
 * @see LinkedMinHeap
 * @see demos.GenericArrays
 */

public class ArrayMinHeap<T extends Comparable<T>> implements MinHeap<T> {

	/* *****************************************************************************************************************
	 * This array will store your data. You may replace it with a linear Collection if you wish, but
	 * consult this class' 	 * JavaDocs before you do so. We allow you this option because if you aren't
	 * careful, you can end up having ClassCastExceptions thrown at you if you work with a raw array of Objects.
	 * See, the type T that this class contains needs to be Comparable with other types T, but Objects are at the top
	 * of the class hierarchy; they can't be Comparable, Iterable, Clonable, Serializable, etc. See GenericArrays.java
	 * under the package demos* for more information.
	 * *****************************************************************************************************************/
	private Object[] data;

	/* *********************************************************************************** *
	 * Write any further private data elements or private methods for LinkedMinHeap here...*
	 * *************************************************************************************/
	int modify_counter = 0;


	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/

	/**
	 * Default constructor initializes the data structure with some default
	 * capacity you can choose.
	 */
	public ArrayMinHeap(){
		this.data = new Object[0]; //empty heap
	}

	/**
	 *  Second, non-default constructor which provides the element with which to initialize the heap's root.
	 *  @param rootElement the element to create the root with.
	 */
	public ArrayMinHeap(T rootElement){
		this.data = new Object[0]; 
		this.data[0] = rootElement;
	}

	/**
	 * Copy constructor initializes {@code this} as a carbon copy of the {@link MinHeap} parameter.
	 *
	 * @param other The MinHeap object to base construction of the current object on.
	 */
	public ArrayMinHeap(MinHeap<T> other){
		ArrayMinHeap<T> minheap = (ArrayMinHeap<T>) other;
		Object[] cpy = minheap.data;
		this.data = new Object[cpy.length -1];
		for(int i=0; i <= cpy.length-1; i++) {
			this.data[i]= cpy[i];
		}
		
	}

	/**
	 * Standard {@code equals()} method. We provide it for you: DO NOT ERASE! Consider its implementation when implementing
	 * {@link #ArrayMinHeap(MinHeap)}.
	 * @return {@code true} if the current object and the parameter object
	 * are equal, with the code providing the equality contract.
	 * @see #ArrayMinHeap(MinHeap)
	 */
	@Override
	public boolean equals(Object other){
		if(other == null || !(other instanceof MinHeap))
			return false;
		Iterator itThis = iterator();
		Iterator itOther = ((MinHeap) other).iterator();
		while(itThis.hasNext())
			if(!itThis.next().equals(itOther.next()))
				return false;
		return !itOther.hasNext();
	}


	@Override
	@SuppressWarnings("unchecked")
	public void insert(T element) {
		modify_counter ++;
		//inserting the element at the last position.
		Object[] updArr = Arrays.copyOf(this.data, this.data.length +1);
		updArr[this.data.length] = element;
		this.data = updArr;
		
		int i = this.data.length-1;  //the index of newly added element.
		//heapify
		while (i >0) {   //loop from new node to root and break once heapifying is complete.
			int parent = (i-1)/2;   //parent node index
			if (((T) this.data[i]).compareTo ((T)this.data[parent]) < 0){    //parent node is bigger
				//swapping new element with parent
				T tmp = (T) this.data[i];
				this.data[i] = this.data[parent];
				this.data[parent] = tmp;
				i = parent; //updating location of the inserted element after swap.
			} else {
				break;   //element not greater than its parent. In its correct loc so break from loop
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public T first() {
		return (T) this.data[0];
	}

	@Override
	@SuppressWarnings("unchecked")
	public T deleteMin() throws EmptyHeapException { // DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (this.data.length >0) // not empty heap
		{	
			modify_counter++;
			T min_Val = (T) this.data[0]; //root
			
			if(this.data.length == 1)  //only root elem exists in heap
			{
				this.data = new Object[0];  //empty heap
				return min_Val;
			}
			else {
				
				T elem = (T) this.data[this.data.length-1] ;//right most leaf to swap. target for heapifying
				this.data[0] = elem;  //swapping root to last elem 
				Object[] cpy = Arrays.copyOf(this.data, this.data.length-1); //copy array except last elem
				this.data = cpy;
				
				int i = 0; //location of node to heapify
				int smaller;
				//heapifying
				while ((i*2+1) < this.data.length) { //has left child
					if((i*2+2) < this.data.length){//has right child 
						smaller = (((T) this.data[i*2+1]).compareTo((T)this.data[i*2+2]) < 0) ? i*2+1 : i*2+2; //index of smaller child 
						if (((T)this.data[smaller]).compareTo((T)this.data[i]) < 0){// smaller child is smaller then target elem 
							//swapping with samller child
							T tmp = (T) this.data[i];
							this.data[i] = this.data[smaller];
							this.data[smaller] = tmp;
							i = smaller;
						}
						else {
							return min_Val;  //target node in correct position, break from loop
						}
					}
					else {  //has left child no right child
						if(((T)this.data[i*2+1]).compareTo((T)this.data[i]) < 0){// child is smaller
							//swapping with left child
							T tmp = (T) this.data[i];
							this.data[i] = this.data[i*2+1];
							this.data[i*2+1] = tmp;
							i = i*2+1;
						}else {
							return min_Val; //target node is smaller, break from loop
						}
					}
				}					
				return min_Val;
			}
		}
		else {
			throw new EmptyHeapException("deletion on empty heap");
		}
	}

		@Override
		@SuppressWarnings("unchecked")
	public T getMin() throws EmptyHeapException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (this.data.length >0) {
			return (T)this.data[0];
		}
			throw new EmptyHeapException("No Min: Empty heap");
	}
		
	public int size() {
		return this.data.length; 
	}	
		
	@Override
	public boolean isEmpty() {
		boolean res = this.data.length == 0 ? true : false ; 
		return res;
	}

	/**
	 * Standard equals() method.
	 * @return {@code true} if the current object and the parameter object
	 * are equal, with the code providing the equality contract.
	 */


	@Override
	public Iterator<T> iterator() {
		return new ArrIterator<T>(this.data);
	}
	private class ArrIterator<T extends Comparable<T>> implements Iterator<T>{
		int modify = modify_counter;
		private Object[] curr;
		
		public ArrIterator(Object[] data) {
			curr = Arrays.copyOf(data, data.length);
		}
		@Override
		public boolean hasNext() {
			if (modify != modify_counter) {
				throw new ConcurrentModificationException();
			}else
			{
				return this.curr.length > 0;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public T next() {
			if(modify != modify_counter) {
				throw new ConcurrentModificationException();
			}
			else {
				T ret = (T)curr[0];
				if(curr.length == 1) {   //one element
					curr = new Object[0];  
					return ret;
				}
				else {
					T next = (T) curr[curr.length-1] ;//right most leaf to swap. target for heapifying
					curr[0] = next;  //swapping root to last elem 
					Object[] cpy = Arrays.copyOf(curr,curr.length -1); //copy array except last elem
					curr = cpy;
					
					int i = 0; //location of node to heapify
					int smaller;
					//heapifying
					while ((i*2+1) < curr.length) { //has left child
						if((i*2+2) < curr.length){//has right child 
							smaller = (((T)curr[i*2+1]).compareTo((T)curr[i*2+2]) < 0) ? i*2+1 : i*2+2; //index of smaller child 
							if (((T)curr[smaller]).compareTo((T)curr[i]) < 0){// smaller child is smaller then target elem 
								//swapping with samller child
								T tmp = (T) curr[i];
								curr[i] = curr[smaller];
								curr[smaller] = tmp;
								i = smaller;
							}
							else {
								return ret;  //target node in correct position, break from loop
							}
						}
						else {  //has left child no right child
							if(((T)curr[i*2+1]).compareTo((T)curr[i]) < 0){// child is smaller
								//swapping with left child
								T tmp = (T) curr[i];
								curr[i] = curr[i*2+1];
								curr[i*2+1] = tmp;
								i = i*2+1;
							}else {
								return ret; //target node is smaller, break from loop
							}
						}
					}					
					return ret;
				}
				
			}
			
		}
		
	}
	
}
