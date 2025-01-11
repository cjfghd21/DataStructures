package phonebook.hashes;


import phonebook.utils.KVPair;
import phonebook.utils.KVPairList;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**<p>{@link SeparateChainingHashTable} is a {@link HashTable} that implements <b>Separate Chaining</b>
 * as its collision resolution strategy, i.e the collision chains are implemented as actual
 * Linked Lists. These Linked Lists are <b>not assumed ordered</b>. It is the easiest and most &quot; natural &quot; way to
 * implement a hash table and is useful for estimating hash function quality. In practice, it would
 * <b>not</b> be the best way to implement a hash table, because of the wasted space for the heads of the lists.
 * Open Addressing methods, like those implemented in {@link LinearProbingHashTable} and {@link QuadraticProbingHashTable}
 * are more desirable in practice, since they use the original space of the table for the collision chains themselves.</p>
 *
 * @author Cheolhong Ahn
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see LinearProbingHashTable
 * @see OrderedLinearProbingHashTable
 * @see CollisionResolver
 */
public class SeparateChainingHashTable implements HashTable{

    /* ****************************************************************** */
    /* ***** PRIVATE FIELDS / METHODS PROVIDED TO YOU: DO NOT EDIT! ***** */
    /* ****************************************************************** */

    private KVPairList[] table;
    private int count;
    private PrimeGenerator primeGenerator;

    // We mask the top bit of the default hashCode() to filter away negative values.
    // Have to copy over the implementation from OpenAddressingHashTable; no biggie.
    private int hash(String key){
        return (key.hashCode() & 0x7fffffff) % table.length;
    }

    /* **************************************** */
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  */
    /* **************************************** */
    /**
     *  Default constructor. Initializes the internal storage with a size equal to the default of {@link PrimeGenerator}.
     */
    public SeparateChainingHashTable(){
        count = 0;
        primeGenerator = new PrimeGenerator();
        table = new KVPairList[primeGenerator.getCurrPrime()];
        for(int i =0; i < table.length; i++) {//setting up each bucket with head of linked list.
        	table[i] = new KVPairList();
        }
    }

    @Override
    public Probes put(String key, String value) {
    	if (key == null || value == null) {
    		throw new IllegalArgumentException();
    	}
        this.table[this.hash(key)].addBack(key, value);
        this.count++;
        return new Probes(value,1);  //probe count always 1 since adding to tail using tail pointer.
        
    }

    @Override
    public Probes get(String key) {
    	 return this.table[this.hash(key)].getValue(key);
    }

    @Override
    public Probes remove(String key) {
        Probes ret = this.table[this.hash(key)].removeByKey(key);
        if (ret.getValue() != null){ //Successful search: value is null if search failed. 
            this.count--;
        }
        return ret;
    }

    @Override
    public boolean containsKey(String key) {
    	 return this.table[this.hash(key)].containsKey(key);
    }

    @Override
    public boolean containsValue(String value) {
        for (int i=0;i<this.table.length;i++){  //check each bucket
            if (this.table[i].containsValue(value)){  //using containsValue func from KVPairList
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
    public int capacity() {
        return table.length; // Or the value of the current prime.
    }

    /**
     * Enlarges this hash table. At the very minimum, this method should increase the <b>capacity</b> of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the enlargement heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     * @see PrimeGenerator#getNextPrime()
     */
    public void enlarge() {
    	KVPairList[] temp = this.table;
    	this.table = new KVPairList[primeGenerator.getNextPrime()];
    	this.count =0;
    	
    	//setting up new table.
    	for(int i=0; i < this.table.length; i++) {
    		this.table[i] = new KVPairList();
    	}
    	
    	//going through each element of old table and re-inserting to new
    	for(int i =0; i < temp.length; i++) { //each index
    		for(KVPair p : temp[i]) { //each element in each head
    			this.put(p.getKey(), p.getValue());  //calling put func for each element to reinsert.
    		}
    	}
    
    }

    /**
     * Shrinks this hash table. At the very minimum, this method should decrease the size of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the shrinking heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     *
     * @see PrimeGenerator#getPreviousPrime()
     */
    public void shrink(){
    	KVPairList[] temp = this.table;
    	this.table = new KVPairList[primeGenerator.getPreviousPrime()];
    	this.count =0;
    	
    	//setting up new table.
    	for(int i=0; i < this.table.length; i++) {
    		this.table[i] = new KVPairList();
    	}
    	
    	//going through each element of old table and re-inserting to new
    	for(int i =0; i < temp.length; i++) { //each index
    		for(KVPair p : temp[i]) { //each element in each head
    			this.put(p.getKey(), p.getValue());  //calling put func for each element to reinsert.
    		}
    	}
    
    }
 
}
