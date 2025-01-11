package pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORTS ARE HERE ONLY TO MAKE THE JAVADOC AND iterator() METHOD SIGNATURE
 * "SEE" THE RELEVANT CLASSES. SOME OF THOSE IMPORTS MIGHT *NOT* BE NEEDED BY YOUR OWN
 * IMPLEMENTATION, AND IT IS COMPLETELY FINE TO ERASE THEM. THE CHOICE IS YOURS.
 * ********************************************************************************** */

import demos.GenericArrays;
import pqueue.exceptions.*;
import pqueue.fifoqueues.FIFOQueue;
import pqueue.heaps.ArrayMinHeap;

import java.util.*;
/**
 * <p>{@link LinearPriorityQueue} is a {@link PriorityQueue} implemented as a linear {@link java.util.Collection}
 * of common {@link FIFOQueue}s, where the {@link FIFOQueue}s themselves hold objects
 * with the same priority (in the order they were inserted).</p>
 *
 * <p>You  <b>must</b> implement the methods in this file! To receive <b>any credit</b> for the unit tests related to
 * this class, your implementation <b>must</b>  use <b>whichever</b> linear {@link Collection} you want (e.g
 * {@link ArrayList}, {@link LinkedList}, {@link java.util.Queue}), or even the various {@link List} and {@link FIFOQueue}
 * implementations that we provide for you. You can also use <b>raw</b> arrays, but take a look at {@link GenericArrays}
 * if you intend to do so. Note that, unlike {@link ArrayMinHeap}, we do not insist that you use a contiguous storage
 * {@link Collection}, but any one available (including {@link LinkedList}) </p>
 *
 * @param <T> The type held by the container.
 *
 * @author  ---- Cheolhong Ahn ----
 *
 * @see MinHeapPriorityQueue
 * @see PriorityQueue
 * @see GenericArrays
 */
public class LinearPriorityQueue<T> implements PriorityQueue<T> {

	/* ***********************************************************************************
	 * Write any private data elements or private methods for LinearPriorityQueue here...*
	 * ***********************************************************************************/
	private ArrayList<E> arr;
	private int cap=99; //default capacity;
	private int modify_count;

	private class E{
		T data;
		int priority;
		public E(T data, int priority){
			this.data = data;
			this.priority = priority;
		}
	}


	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/

	/**
	 * Default constructor initializes the element structure with
	 * a default capacity. This default capacity will be the default capacity of the
	 * underlying element structure that you will choose to use to implement this class.
	 */
	public LinearPriorityQueue(){
		this.arr = new ArrayList<>(cap);
	}

	/**
	 * Non-default constructor initializes the element structure with
	 * the provided capacity. This provided capacity will need to be passed to the default capacity
	 * of the underlying element structure that you will choose to use to implement this class.
	 * @see #LinearPriorityQueue()
	 * @param capacity The initial capacity to endow your inner implementation with.
	 * @throws InvalidCapacityException if the capacity provided is less than 1.
	 */
	public LinearPriorityQueue(int capacity) throws InvalidCapacityException{	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if( capacity < 1) {
			throw new InvalidCapacityException("invalid capacity");
		}
		this.arr = new ArrayList<>(capacity);
		
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException{	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (priority <=0) {
			throw new InvalidPriorityException("Invalid Priority");
		}else {
			modify_count ++;
			boolean flag_add = false;
			if(arr.size() == 0){
				arr.add(new E(element, priority));
			}
			else {
				for(int i=0; i < arr.size();i++) { //look thorugh pre-existing elements
					E curr = this.arr.get(i);
					if(curr.priority > priority) { //higher prioirty.
						arr.add(i,new E(element, priority));
						flag_add = true;
						break;
					}
				}
				if (flag_add == false) {
					arr.add(new E(element, priority));
				}
			}
		}
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException { 	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(this.arr.size()==0){
			throw new EmptyPriorityQueueException("Queue is empty");
		}else {
			modify_count++;
			T ret = arr.get(0).data; 
			arr.remove(0);
			return ret;
		}
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(arr.size() ==0) {
			throw new EmptyPriorityQueueException("Queue is empty");
		}else {
			return this.arr.get(0).data;
		}
	}

	@Override
	public int size() {
		return arr.size();
	}

	@Override
	public boolean isEmpty() {
		if(arr.size() ==0) {
			return true;
		}else {
			return false;
		}
	}


	@Override
	public Iterator<T> iterator() {
		return new LinearPQIter();
	}
	private class LinearPQIter implements Iterator<T>{
		private ArrayList<E> cpy = arr;
		private int modify = modify_count;
		int i = 0;
		
		
		@Override
		public boolean hasNext() {
			if (i < cpy.size())
				return true;
			else
				return false;
		}

		@Override
		public T next() {
			if(modify != modify_count) {
				throw new ConcurrentModificationException();
			}
			T ret = cpy.get(i).data;
			i++;
			return ret;
		}
		
	}

}