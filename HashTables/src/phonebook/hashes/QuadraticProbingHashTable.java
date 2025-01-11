package phonebook.hashes;


import phonebook.utils.KVPair;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**
 * <p>{@link QuadraticProbingHashTable} is an Openly Addressed {@link HashTable} which uses <b>Quadratic
 * Probing</b> as its collision resolution strategy. Quadratic Probing differs from <b>Linear</b> Probing
 * in that collisions are resolved by taking &quot; jumps &quot; on the hash table, the length of which
 * determined by an increasing polynomial factor. For example, during a key insertion which generates
 * several collisions, the first collision will be resolved by moving 1^2 + 1 = 2 positions over from
 * the originally hashed address (like Linear Probing), the second one will be resolved by moving
 * 2^2 + 2= 6 positions over from our hashed address, the third one by moving 3^2 + 3 = 12 positions over, etc.
 * </p>
 *
 * <p>By using this collision resolution technique, {@link QuadraticProbingHashTable} aims to get rid of the
 * &quot;key clustering &quot; problem that {@link LinearProbingHashTable} suffers from. Leaving more
 * space in between memory probes allows other keys to be inserted without many collisions. The tradeoff
 * is that, in doing so, {@link QuadraticProbingHashTable} sacrifices <em>cache locality</em>.</p>
 *
 * @author Cheolhong Ahn!
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see OrderedLinearProbingHashTable
 * @see LinearProbingHashTable
 * @see CollisionResolver
 */
public class QuadraticProbingHashTable extends OpenAddressingHashTable {

    /* ********************************************************************/
    /* ** INSERT ANY PRIVATE METHODS OR FIELDS YOU WANT TO USE HERE: ******/
    /* ********************************************************************/
	private boolean soft_del;
	private int tombCount;
	
    /* ******************************************/
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
    /* **************************************** */

    /**
     * Constructor with soft deletion option. Initializes the internal storage with a size equal to the starting value of  {@link PrimeGenerator}.
     * @param soft A boolean indicator of whether we want to use soft deletion or not. {@code true} if and only if
     *               we want soft deletion, {@code false} otherwise.
     */
    public QuadraticProbingHashTable(boolean soft) {
        this.soft_del = soft;
        this.count = 0;
        this.tombCount = 0;
        this.primeGenerator = new PrimeGenerator();
        this.table = new KVPair[primeGenerator.getCurrPrime()];
    }

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
        					int hk = idx;// used for mqp(k,j)  hk = h(k); 
        					int j = 2;; // mqp(k,j).  j starts from 2 as already at 1.
        					while(this.table[idx] != null) {
        						probeC++;  //one probe check occupied table cell.
        						idx = (hk+(j-1)+((j-1)*(j-1)))%this.table.length;  //go to next cell [h(k)+(j-1)+(j-1)^2] mod M
        						j++;
        					}
        					this.table[idx] = temp[i];
        					probeC++; //one probe to insert to empty cell.
        				}
        				this.count++; //copied element over from original.
        			}
        		}
        	}
        	
        	int insert_idx = this.hash(key); //current index. 
        	int hk = insert_idx;// h(k)
        	int j = 2;  //used for mqp(k,j);
        	while(this.table[insert_idx] !=null){ // if not null(occupied or tomb stone), linear probe until empty cell.
        		probeC ++;
        		insert_idx = (hk+(j-1)+((j-1)*(j-1)))%this.table.length;
        		j++;
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
        int hk = address; //h(k)
    	int j = 2; //used for mqp(k,j);
        int probeC = 1;
        
        while(this.table[address] != null) {
        	if(this.table[address].getKey().equals(key)){ // found matching
        		return new Probes(this.table[address].getValue(), probeC);  
        	}
        	probeC ++;
        	address = (hk+(j-1)+((j-1)*(j-1)))%this.table.length;
        	j++;
        }
        
        return new Probes(null, probeC);
    }
    
    @Override
    public Probes remove(String key) {
        if (key == null) {  // key is null
        	return new Probes(null, 0);
        }
        
        int address = this.hash(key); //current location.
        int hk = address;  // h(k)
        int j = 2;    // mpq(k,j);
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
              	address = (hk+(j-1)+((j-1)*(j-1)))%this.table.length;
              	j++;
        	  }
        	  return new Probes(null, probeC); //search failed.
        	
        }else {   //hard deletion
       	  while(this.table[address] != null) {  //search and delete.
            	if(this.table[address].getKey().equals(key)){ // found matching
            		String retVal = this.table[address].getValue(); //return val string
            		this.table[address] = null; //set cell ref to null
            		this.count --;
            		//check all original and reinsert.
            		for(int i =0; i < this.table.length; i++){
            			if (this.table[i] == null || this.table[i] == TOMBSTONE){ //original cell empty or marked for deletion
            				probeC ++;  //one probe to check original empty cell
            			}else{
	            			KVPair pair = this.table[i];
	            			this.table[i] = null;
	            			count --; //above set to null, decrease count until re-insertion as put function used below increments count again.
	            			probeC ++; //for initial read and writing null
	            			//reinserting.
	            			Probes p = put(pair.getKey(), pair.getValue()); //insertion, quadratic probing handled by put func.
	            			probeC = probeC+p.getProbes(); //total + probe taken to insert
            			}
            		}
            		return new Probes(retVal, probeC);
            	}
            	else {	//not found. keep searching
            		probeC ++;
            		address =(hk+(j-1)+((j-1)*(j-1)))%this.table.length; //quadratic probe.
            	}
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
    public int size(){
    	return this.count - this.tombCount; 
    }

    @Override
    public int capacity() {
    	return this.table.length; 
    }

}