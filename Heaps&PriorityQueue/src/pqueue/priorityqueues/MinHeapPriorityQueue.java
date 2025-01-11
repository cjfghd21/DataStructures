package pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******


/* *****************************************************************************************
 * THE FOLLOWING IMPORTS WILL BE NEEDED BY YOUR CODE, BECAUSE WE REQUIRE THAT YOU USE
 * ANY ONE OF YOUR EXISTING MINHEAP IMPLEMENTATIONS TO IMPLEMENT THIS CLASS. TO ACCESS
 * YOUR MINHEAP'S METHODS YOU NEED THEIR SIGNATURES, WHICH ARE DECLARED IN THE MINHEAP
 * INTERFACE. ALSO, SINCE THE PRIORITYQUEUE INTERFACE THAT YOU EXTEND IS ITERABLE, THE IMPORT OF ITERATOR
 * IS NEEDED IN ORDER TO MAKE YOUR CODE COMPILABLE. THE IMPLEMENTATIONS OF CHECKED EXCEPTIONS
 * ARE ALSO MADE VISIBLE BY VIRTUE OF THESE IMPORTS.
 ** ********************************************************************************* */

import pqueue.exceptions.*;
import pqueue.heaps.ArrayMinHeap;
import pqueue.heaps.EmptyHeapException;
import pqueue.heaps.MinHeap;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
/**
 * <p>{@link MinHeapPriorityQueue} is a {@link PriorityQueue} implemented using a {@link MinHeap}.</p>
 *
 * <p>You  <b>must</b> implement the methods of this class! To receive <b>any credit</b> for the unit tests
 * related to this class, your implementation <b>must</b> use <b>whichever</b> {@link MinHeap} implementation
 * among the two that you should have implemented you choose!</p>
 *
 * @author  ---- Cheolhong Ahn ----
 *
 * @param <T> The Type held by the container.
 *
 * @see LinearPriorityQueue
 * @see MinHeap
 * @see PriorityQueue
 */
public class MinHeapPriorityQueue<T> implements PriorityQueue<T>{

	/* ***********************************************************************************
	 * Write any private data elements or private methods for MinHeapPriorityQueue here...*
	 * ***********************************************************************************/
	 private ArrayMinHeap<E> arrQ;
	 private int modify_count;

	 private class E implements Comparable<E>{
		protected T data;
		protected int priority;

		public E(T data, int priority) {
			this.data = data;
			this.priority = priority;
		}

		public int compareTo(E other){
			return (this.priority - other.priority);
		}
	 }

	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/
		/**
	 * Simple default constructor.
	 */
	public MinHeapPriorityQueue(){
		this.arrQ = new ArrayMinHeap<>();
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (priority < 1) {
			throw new InvalidPriorityException("Invalid Priority");
		}
		modify_count++;
		arrQ.insert(new E(element,priority));
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException {		// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (arrQ.size() == 0){
			throw new EmptyPriorityQueueException("Queue empty for dequeue");
		}
		T ret;
		try{
			ret = arrQ.getMin().data;
			arrQ.deleteMin();
		}catch(EmptyHeapException e){
			throw new EmptyPriorityQueueException("Queue empty for dequeue");
		}
		return ret;
	}

	

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		T ret;
		try {
			ret = this.arrQ.getMin().data;
		}catch(EmptyHeapException e){
			throw new EmptyPriorityQueueException("Queue empty for getFirst");
		}
		return ret;
	}

	@Override
	public int size() {
		return this.arrQ.size();
	}

	@Override
	public boolean isEmpty() {
		return (this.arrQ.size() ==0);
	}


	@Override
	public Iterator<T> iterator() {
		return new HeapPQIter();
	}
	private class HeapPQIter implements Iterator<T>{
		ArrayMinHeap<E> curr = arrQ;
		private int modify = modify_count;
		Iterator<E> iter = curr.iterator();
		@Override
		
		public boolean hasNext() {
			if(modify != modify_count) {
				throw new ConcurrentModificationException();
			}else {
				return iter.hasNext();
			}

		}

	@Override
		public T next() {
			if(modify != modify_count) {
				throw new ConcurrentModificationException();
			}else
				return iter.next().data;
			}

	
	}
}
