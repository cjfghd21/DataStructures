package pqueue;

import org.junit.Test;
import pqueue.exceptions.InvalidCapacityException;
import pqueue.exceptions.InvalidPriorityException;
import pqueue.heaps.ArrayMinHeap;
import pqueue.heaps.EmptyHeapException;
import pqueue.heaps.LinkedMinHeap;
import pqueue.heaps.MinHeap;
import pqueue.priorityqueues.EmptyPriorityQueueException;
import pqueue.priorityqueues.LinearPriorityQueue;
import pqueue.priorityqueues.MinHeapPriorityQueue;
import pqueue.priorityqueues.PriorityQueue;

import static org.junit.Assert.*;

/**
 * {@link StudentTests} is a {@code jUnit} testing library which you should extend with your own tests.
 *
 * @author  <a href="https://github.com/JasonFil">Jason Filippou</a> and --- YOUR NAME HERE! ----
 */
public class StudentTests {

    private static String throwableInfo(Throwable thrown){
        return "Caught a " + thrown.getClass().getSimpleName() +
                " with message: " + thrown.getMessage();
    }

    private MinHeap<String> myHeap;
    private PriorityQueue<String> myQueue;

    @Test
    public void initAndAddOneElement() throws InvalidPriorityException {
        try {
            myHeap = new ArrayMinHeap<>();
            myQueue = new MinHeapPriorityQueue<>();
        } catch(Throwable t){
            fail(throwableInfo(t));
        }
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report that they are empty.",
                myHeap.isEmpty() && myQueue.isEmpty());
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report a size of 0.",
                (myHeap.size() == 0) && (myQueue.size() == 0));
        myHeap.insert("Mary");
        assertEquals("After inserting an element, ArrayMinHeap instances should report a size of 1.", 1, myHeap.size());

        // MinHeap::enqueue() declares that it checks InvalidPriorityException if priority <= 0 (from the docs of MinHeap).
        // In this case, we know for sure that InvalidPriorityException should *not* be thrown, since priority = 2 >= 0.
        // To avoid cluttering a code with "dummy" try-catch blocks, we declare InvalidPriorityException as checked from
        // this test as well. This is why we have the throws declaration after the name of the test.
        myQueue.enqueue("Jason", 2);
        assertEquals("After inserting an element, MinHeapPriorityQueue instances should report a size of 1.", 1, myQueue.size());
    }

    // Here is one simple way to write tests that expect an Exception to be thrown. Another, more powerful method is to
    // use the class org.junit.rules.ExpectedException: https://junit.org/junit4/javadoc/4.12/org/junit/rules/ExpectedException.html
    @Test(expected = InvalidCapacityException.class)
    public void ensureInvalidCapacityExceptionThrown() throws InvalidCapacityException{
         myQueue = new LinearPriorityQueue<>(-2);
    }

    @Test(expected = InvalidPriorityException.class)
    public void ensureInvalidPriorityExceptionThrown() throws InvalidPriorityException, InvalidCapacityException{
        myQueue = new LinearPriorityQueue<>(4);
        myQueue.enqueue("Billy", -1);
    }

    @Test
    public void testEnqueingOrder() throws InvalidPriorityException, EmptyPriorityQueueException {
        myQueue = new MinHeapPriorityQueue<>();
        myQueue.enqueue("Ashish", 8);
        myQueue.enqueue("Diana", 2);        // Lower priority, so should be up front.
        myQueue.enqueue("Adam", 2);        // Same priority, but should be second because of FIFO.
        assertEquals("We were expecting Diana up front.", "Diana", myQueue.getFirst());
    }

    @Test
    public void testDequeuingOrder() throws InvalidPriorityException, EmptyPriorityQueueException {
        testEnqueingOrder();    // To populate myQueue with the same elements.
        myQueue.dequeue();      // Now Adam should be up front.
        assertEquals("We were expecting Adam up front.", "Adam", myQueue.getFirst());
    }

    /* ******************************************************************************************************** */
    /* ********************** YOU SHOULD ADD TO THESE UNIT TESTS BELOW. *************************************** */
    /* ******************************************************************************************************** */
    @Test
    public void studentTestArryHeap() throws EmptyHeapException {
        try {
            myHeap = new ArrayMinHeap<>();
        } catch(Throwable t){
            fail(throwableInfo(t));
        }
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report that they are empty.",
                myHeap.isEmpty());
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report a size of 0.",
                (myHeap.size() == 0));
        myHeap.insert("8");
        myHeap.insert("1");
        myHeap.insert("2");
        myHeap.insert("3");
        myHeap.insert("4");

        assertEquals("min should be 1", "1", myHeap.getMin());
        myHeap.deleteMin();
        assertEquals("min should be 2", "2", myHeap.getMin());
        myHeap.deleteMin();
        assertEquals("min should be 3", "3", myHeap.getMin());
        myHeap.deleteMin();
        assertEquals("min should be 4", "4", myHeap.getMin());
        myHeap.deleteMin();
        assertEquals("min should be 8", "8", myHeap.getMin());
        // MinHeap::enqueue() declares that it checks InvalidPriorityException if priority <= 0 (from the docs of MinHeap).
        // In this case, we know for sure that InvalidPriorityException should *not* be thrown, since priority = 2 >= 0.
        // To avoid cluttering a code with "dummy" try-catch blocks, we declare InvalidPriorityException as checked from
        // this test as well. This is why we have the throws declaration after the name of the test.
    }

    @Test
    public void studentTestLinkHeap() throws EmptyHeapException {
        try {
            myHeap = new LinkedMinHeap<>();
        } catch(Throwable t){
            fail(throwableInfo(t));
        }
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report that they are empty.",
                myHeap.isEmpty());
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report a size of 0.",
                (myHeap.size() == 0));
        myHeap.insert("4");
        assertEquals("min should be 4","4" ,myHeap.getMin());
        myHeap.insert("8");
        assertEquals("min should be 4","4" ,myHeap.getMin());
        assertTrue("size should be 2", (myHeap.size() == 2));
        myHeap.insert("1");
        assertEquals("min should be 1","1" ,myHeap.getMin());
        assertTrue("size should be 3", (myHeap.size() == 3));
        myHeap.insert("3");
        assertEquals("min should be 1","1" ,myHeap.getMin());
        assertTrue("size should be 4", (myHeap.size() == 4));
        myHeap.insert("2");
        assertEquals("min should be 1","1" ,myHeap.getMin());
        assertTrue("size should be 5", (myHeap.size() == 5));
    
        
 

        // MinHeap::enqueue() declares that it checks InvalidPriorityException if priority <= 0 (from the docs of MinHeap).
        // In this case, we know for sure that InvalidPriorityException should *not* be thrown, since priority = 2 >= 0.
        // To avoid cluttering a code with "dummy" try-catch blocks, we declare InvalidPriorityException as checked from
        // this test as well. This is why we have the throws declaration after the name of the test.
    }

}
