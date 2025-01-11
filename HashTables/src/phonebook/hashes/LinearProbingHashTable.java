package phonebook.hashes;

import phonebook.utils.KVPair;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**
 * <p>{@link LinearProbingHashTable} is an Openly Addressed {@link HashTable} implemented with <b>Linear Probing</b> as its
 * collision resolution strategy: every key collision is resolved by moving one address over. It is
 * the most famous collision resolution strategy, praised for its simplicity, theoretical properties
 * and cache locality. It <b>does</b>, however, suffer from the &quot; clustering &quot; problem:
 * collision resolutions tend to cluster collision chains locally, making it hard for new keys to be
 * inserted without collisions. {@link QuadraticProbingHashTable} is a {@link HashTable} that
 * tries to avoid this problem, albeit sacrificing cache locality.</p>
 *
 * @author Cheolhong Ahn
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see OrderedLinearProbingHashTable
 * @see QuadraticProbingHashTable
 * @see CollisionResolver
 */
public class LinearProbingHashTable extends OpenAddressingHashTable {

    /* ********************************************************************/
    /* ** INSERT ANY PRIVATE METHODS OR FIELDS YOU WANT TO USE HERE: ******/
    /* ********************************************************************/
	private boolean soft_del;
	private int tombCount;//tracks number of elements marked for deletion on soft deletion to figure out actual size.
	
	
    /* ******************************************/
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
    /* **************************************** */

	

    /**
     * Constructor with soft deletion option. Initializes the internal storage with a size equal to the starting value of  {@link PrimeGenerator}.
     *
     * @param soft A boolean indicator of whether we want to use soft deletion or not. {@code true} if and only if
     *             we want soft deletion, {@code false} otherwise.
     */
    public LinearProbingHashTable(boolean soft) {
    	this.soft_del = soft;
    	this.count= 0; //occupied cells;
    	this.tombCount = 0; 
    	this.primeGenerator = new PrimeGenerator();
    	this.table = new KVPair[primeGenerator.getCurrPrime()];
    }

    /**
     * Inserts the pair &lt;key, value&gt; into this. The container should <b>not</b> allow for {@code null}
     * keys and values, and we <b>will</b> test if you are throwing a {@link IllegalArgumentException} from your code
     * if this method is given {@code null} arguments! It is important that we establish that no {@code null} entries
     * can exist in our database because the semantics of {@link #get(String)} and {@link #remove(String)} are that they
     * return {@code null} if, and only if, their key parameter is {@code null}. This method is expected to run in <em>amortized
     * constant time</em>.
     * <p>
     * Instances of {@link LinearProbingHashTable} will follow the writeup's guidelines about how to internally resize
     * the hash table when the capacity exceeds 50&#37;
     *
     * @param key   The record's key.
     * @param value The record's value.
     * @return The {@link phonebook.utils.Probes} with the value added and the number of probes it makes.
     * @throws IllegalArgumentException if either argument is {@code null}.
     */
    @Override
    public Probes put(String key, String value) {
        if (key == null || value == null) { // should not allow for null key, value
        	throw new IllegalArgumentException();
        }else {
        	int probeC = 0; // probe counter
        	//check if resize needed.
        	if(this.count > (0.5 * this.table.length)){ // n >= T * M, resize needed.
        		KVPair[] temp = this.table;   //old table to copy over to new. needed as this.hash uses this.table length for hash.
        		this.table = new KVPair[primeGenerator.getNextPrime()]; //new table
        		this.count = 0;  //count reset and recounted since tombstone in original counted but will not copy over.
        		this.tombCount = 0; //tombcount reset as tombstone do not carry over.
        		//re-inserting
        		for(int i=0; i<temp.length; i++){//check all original cells
        			if (temp[i] == null || temp[i] == TOMBSTONE){ //original cell empty or marked for deletion
        				probeC ++;  //one probe to check original cell
        			}else{  //original cell occupied need to copy over to new table
        				probeC ++;  //one probe to check original cell
        				int idx = this.hash(temp[i].getKey()); //hash to insert to new table
        				if (this.table[idx] == null) { //idx is empty on new table so insert
        					this.table[idx] = temp[i];
        					probeC ++;  //one probe to insert
        				}else {//idx is occupied on new cell. linear probing until empty cell found and insert.
        					while(this.table[idx] != null) {
        						probeC++;  //one probe check occupied table cell.
        						idx = (idx+1)%this.table.length;  //go to next cell
        					}
        					this.table[idx] = temp[i];
        					probeC++; //one probe to insert to empty cell.
        				}
        				this.count++; //copied element over from original.
        			}
        		}
        	}
        	
        	int insert_idx = this.hash(key); //
        	while(this.table[insert_idx] !=null){ // if not null(occupied or tomb stone), linear probe until empty cell.
        		probeC ++;
        		insert_idx = (insert_idx+1)%this.table.length;
        	}
        	probeC ++; //probe for inserting to target cell.
        	this.table[insert_idx] = new KVPair(key, value);
        	this.count ++;
        	return new Probes(value,probeC);
        }
    }

    @Override
    public Probes get(String key) {
        if(key == null) {  // key is null
        	return new Probes(null,0);
        }
    	
    	int address = this.hash(key);
        int probeC = 1; 
        
        while(this.table[address] != null) {
        	if(this.table[address].getKey().equals(key)){ // found matching
        		return new Probes(this.table[address].getValue(), probeC);  
        	}
        	probeC ++;
        	address = (address+1)% this.table.length;
        }
        
        return new Probes(null, probeC);
    }


    /**
     * <b>Return</b> the value associated with key in the {@link HashTable}, and <b>remove</b> the {@link phonebook.utils.KVPair} from the table.
     * If key does not exist in the database
     * or if key = {@code null}, this method returns {@code null}. This method is expected to run in <em>amortized constant time</em>.
     *
     * @param key The key to search for.
     * @return The {@link phonebook.utils.Probes} with associated value and the number of probe used. If the key is {@code null}, return value {@code null}
     * and 0 as number of probes; if the key doesn't exist in the database, return {@code null} and the number of probes used.
     */
    @Override
    public Probes remove(String key) {
        if (key == null) {  // key is null
        	return new Probes(null, 0);
        }
        
        int address = this.hash(key);
        int probeC=1; // probe counter
        
        if(this.soft_del){ //soft deletion
        	  while(this.table[address] != null) { //search
              	if(this.table[address].getKey().equals(key)){ // found matching
              		Probes ret = new Probes(this.table[address].getValue(), probeC); //return val.
              		this.table[address] = this.TOMBSTONE; //mark for deletion
              		this.tombCount ++;
              		return ret;	
              	}
              	probeC ++;
              	address = (address+1)% this.table.length;
        	  }
        	  return new Probes(null, probeC); //search failed.
        	
        }else {   //hard deletion
       	  while(this.table[address] != null) {  //search and delete.
            	if(this.table[address].getKey().equals(key)){ // found matching
            		String retVal = this.table[address].getValue(); //return val string
            		this.table[address] = null; //set cell ref to null
            		this.count --;
            		address = (address+1)% this.table.length;
            		//check rest of chain and reinsert as necessary
            		while(this.table[address] !=null){
            			KVPair pair = this.table[address];
            			this.table[address] = null;
            			count --; //above set to null, decrease count until re-insertion as put function used below increments count again.
            			probeC ++; //for initial read and writing null
            			//reinserting.
            			Probes p = put(pair.getKey(), pair.getValue()); //insertion;
            			probeC = probeC+p.getProbes(); //total + probe taken to insert
            			
            			address = (address+1)% this.table.length; //next cell 
            		}
            		probeC++; //probe that checked for null to terminate as the above loop doesn't run to increment probe for checking if null
            		return new Probes(retVal, probeC);
            	}
            	//keep searching
            	probeC ++;
            	address =(address+1)% this.table.length;
       	  }	
       	  return new Probes(null, probeC); //search failed.
       }   
    }

    @Override
    public boolean containsKey(String key) {
        return(this.get(key).getValue() != null);
    }

    @Override
    public boolean containsValue(String value) {   //check all key in the table for its value
       for(int i=0; i< this.table.length; i++) {
    	   if(this.table[i].getValue().equals(value))
    		   return true;
       }
       return false;
    }

    @Override
    public int size() {
        //total element - element marked for deletion
    	return this.count - this.tombCount;
    }

    @Override
    public int capacity() {
        return this.table.length;
    }
}
