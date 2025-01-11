package spatial.knnutils;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;



/**
 * <p>{@link BoundedPriorityQueue} is a priority queue whose number of elements
 * is bounded. Insertions are such that if the queue's provided capacity is surpassed,
 * its length is not expanded, but rather the maximum priority element is ejected
 * (which could be the element just attempted to be enqueued).</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  <a href = "https://github.com/jasonfillipou/">Jason Filippou</a>
 *
 * @see PriorityQueue
 * @see PriorityQueueNode
 */
public class BoundedPriorityQueue<T> implements PriorityQueue<T>{

	/* *********************************************************************** */
	/* *************  PLACE YOUR PRIVATE FIELDS AND METHODS HERE: ************ */
	/* *********************************************************************** */
	private int size; //size of pqueue
	private int count; //count of element in queue
	private int insertOrder;
	private int modifier_count;
	private ArrayList<PriorityQueueNode<T>> BoundedPqueue;
	


	/* *********************************************************************** */
	/* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
	/* *********************************************************************** */
	
	public void print(){
		for(int i =0; i < BoundedPqueue.size(); i++) {
			System.out.println(("("+BoundedPqueue.get(i).getData() + "," +BoundedPqueue.get(i).getPriority()+")") + "," + this.size());
		
		}
	
	}

	/**
	 * Constructor that specifies the size of our queue.
	 * @param size The static size of the {@link BoundedPriorityQueue}. Has to be a positive integer.
	 * @throws IllegalArgumentException if size is not a strictly positive integer.
	 */
	public BoundedPriorityQueue(int size) throws IllegalArgumentException{
		if(size <= 0 ) {
			throw new IllegalArgumentException();
		}else {
			this.BoundedPqueue = new ArrayList<>(size);
			this.size = size;
			this.count =0;
			this.insertOrder =0;
		}
	}

	/**
	 * <p>Enqueueing elements for BoundedPriorityQueues works a little bit differently from general case
	 * PriorityQueues. If the queue is not at capacity, the element is inserted at its
	 * appropriate location in the sequence. On the other hand, if the object is at capacity, the element is
	 * inserted in its appropriate spot in the sequence (if such a spot exists, based on its priority) and
	 * the maximum priority element is ejected from the structure.</p>
	 * 
	 * @param element The element to insert in the queue.
	 * @param priority The priority of the element to insert in the queue.
	 */
	@Override
	public void enqueue(T element, double priority) {
		PriorityQueueNode<T> newNode = new PriorityQueueNode<>(element, priority, this.insertOrder);
		this.insertOrder ++;
		boolean inserted = false;
		for(int i = 0;i < this.BoundedPqueue.size(); i++){
			PriorityQueueNode<T> curr =  this.BoundedPqueue.get(i);
			if(curr.getPriority() > priority){
				this.modifier_count ++;
				inserted = true;
				this.BoundedPqueue.add(i, newNode);
				break;
			}
		}
		
		if (inserted== false){
			this.modifier_count ++;
			if(this.BoundedPqueue.isEmpty()){
				this.BoundedPqueue.add(0,newNode);
			}else{
				this.BoundedPqueue.add(newNode);
			}
		}
		this.count++;
		// eject the last one if the size exceed the max bound.
		if (this.BoundedPqueue.size() > this.size){
			this.BoundedPqueue.remove(this.BoundedPqueue.size()-1);
			this.count--;
		}
	}

	@Override
	public T dequeue() {
		if(this.BoundedPqueue.size() != 0){
			this.modifier_count ++;
			T retVal = this.BoundedPqueue.get(0).getData();
			this.BoundedPqueue.remove(0);
			this.count --;
			return retVal;
		}else{
			return null;
		}
		
	}

	@Override
	public T first() {
		if(count ==0) {
			return null;
		}else {
			return this.BoundedPqueue.get(0).getData(); 
		}
	}
	
	/**
	 * Returns the last element in the queue. Useful for cases where we want to 
	 * compare the priorities of a given quantity with the maximum priority of 
	 * our stored quantities. In a minheap-based implementation of any {@link PriorityQueue},
	 * this operation would scan O(n) nodes and O(nlogn) links. In an array-based implementation,
	 * it takes constant time.
	 * @return The maximum priority element in our queue, or null if the queue is empty.
	 */
	public T last() {
		if(count ==0) {
			return null;
		}else {
			return this.BoundedPqueue.get(this.count-1).getData();
		}
	}

	/**
	 * Inspects whether a given element is in the queue. O(N) complexity.
	 * @param element The element to search for.
	 * @return {@code true} iff {@code element} is in {@code this}, {@code false} otherwise.
	 */
	public boolean contains(T element)
	{
		for(PriorityQueueNode<T> curr : this.BoundedPqueue) {
			if(curr.equals(element)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int size() {
		return this.count;
	}

	@Override
	public boolean isEmpty() {
		if(this.count ==0) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public Iterator<T> iterator() {return new BoundedPriorityQueueIter();}
	private class BoundedPriorityQueueIter implements Iterator<T>{
		private ArrayList<PriorityQueueNode<T>> cpy = BoundedPqueue;
		private int modify = modifier_count;
		int i = 0;
	
		@Override
		public boolean hasNext() {
			if(i < cpy.size()) 
				return true;
			else
				return false;
		}
		
		@Override
		public T next() {
			if(modify != modifier_count) {
				throw new ConcurrentModificationException();
			}
			T ret = cpy.get(i).getData();
			i++;
			return ret;
		}
	}
}
