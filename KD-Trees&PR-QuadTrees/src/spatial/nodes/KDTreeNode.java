package spatial.nodes;

import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;

import java.util.Collection;

/**
 * <p>{@link KDTreeNode} is an abstraction over nodes of a KD-Tree. It is used extensively by
 * {@link spatial.trees.KDTree} to implement its functionality.</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  ---- Cheolhong Ahn! -----
 *
 * @see spatial.trees.KDTree
 */
public class KDTreeNode {


    /* *************************************************************************** */
    /* ************* WE PROVIDE THESE FIELDS TO GET YOU STARTED.  **************** */
    /* ************************************************************************** */
    private KDPoint p;
    private KDTreeNode left, right;

    /* *************************************************************************************** */
    /* *************  PLACE ANY OTHER PRIVATE FIELDS AND YOUR PRIVATE METHODS HERE: ************ */
    /* ************************************************************************************* */
    //get children for testing purposes
    public KDPoint lcPoint() {
    	return this.left.p;
    }
    
    public KDPoint rcPoint() {
    	return this.right.p;
    }
    
    //check next dimension, if over the k, goes back to 0 else increment current dimension
    private int newDim(int currDim,int dims){
    	int nextDim;
    	if(currDim +1 == dims) { //if current dim is at k-th dimension
			nextDim = 0;   //check from first dim again
		}else {
			nextDim = currDim+1;  //else go to next dim
		}
    	return nextDim;
    }
    
    //find min function described in module video.
    private KDTreeNode findMin(KDTreeNode currNode, int soughtDim, int currDim, int numDims){
    	if(null ==currNode) return null;
    	if(null ==currNode.left && null==currNode.right) return currNode;
    	if(soughtDim == currDim) return findMin(currNode.left, soughtDim, newDim(currDim,numDims), numDims);
    	KDTreeNode lMin = findMin(currNode.left, soughtDim, newDim(currDim,numDims), numDims);
    	KDTreeNode rMin = findMin(currNode.right, soughtDim, newDim(currDim,numDims), numDims);
    	return min3(lMin,rMin,currNode,soughtDim);
    }
    
    //returns minimum of three nodes. if same value order is left,curr,right.
    private KDTreeNode min3(KDTreeNode lMin, KDTreeNode rMin, KDTreeNode currNode, int soughtDim) {
    	if(lMin == null && rMin == null) {  //lchild rchild null
    		return currNode;
    	}else if(lMin == null && rMin !=null) { //lchild null, comparing right child with curr
    		return (currNode.p.coords[soughtDim] <= rMin.p.coords[soughtDim] ? currNode : rMin);
    	}else if(lMin !=null && rMin == null) { //right child null, comparing left child with curr
    		return(currNode.p.coords[soughtDim] < lMin.p.coords[soughtDim] ? currNode : lMin);
    	}else { // all children non-null.   current is always non-null checked by method calling this.
    		if(lMin.p.coords[soughtDim] <= rMin.p.coords[soughtDim]) { //lmin is smaller child of two
    			return(currNode.p.coords[soughtDim] < lMin.p.coords[soughtDim] ? currNode : lMin);
    		}else { //rMin is smaller child of two
    			return (currNode.p.coords[soughtDim] <= rMin.p.coords[soughtDim] ? currNode : rMin);
    		}
    	}
    }

    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */
    

    /**
     * 1-arg constructor. Stores the provided {@link KDPoint} inside the freshly created node.
     * @param p The {@link KDPoint} to store inside this. Just a reminder: {@link KDPoint}s are
     *          <b>mutable!!!</b>.
     */
    public KDTreeNode(KDPoint p){
        KDPoint copy = new KDPoint(p);
        this.p = copy;   
        // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    /**
     * <p>Inserts the provided {@link KDPoint} in the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left.</p>
     * @param currDim The current dimension to consider
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #delete(KDPoint, int, int)
     */
    public  void insert(KDPoint pIn, int currDim, int dims){
        //compare inserted node pIn to current node's current dimension value.  if insert node greater go right.
    	if(pIn.coords.length == dims) { //checking pIn to add is correct dim
    		if(this.p != null) {
		    	if(pIn.coords[currDim] >= this.p.coords[currDim]){
		        	if(this.right == null) {  // empty right subtree, inserting there.
		        		KDPoint copy = new KDPoint(pIn); //copy of point to insert
		        		this.right = new KDTreeNode(copy);
		        	}else {
		        		this.right.insert(pIn,newDim(currDim,dims),dims);
		        	}
		        }else { // current node dim smaller so traversing to left subtree and doing same as right.
		    		if(this.left == null) {
		    			KDPoint copy = new KDPoint(pIn); //copy of point to insert
		        		this.left = new KDTreeNode(copy);
		    	
		    		}else {
		    			this.left.insert(pIn, newDim(currDim,dims), dims);
		    		}
		        }
    		}else {
    			this.p  = new KDPoint(pIn);
    		}
    	}
    }

    /**
     * <p>Deletes the provided {@link KDPoint} from the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left. There exist two special cases of deletion,
     * depending on whether we are deleting a {@link KDPoint} from a node who either:</p>
     *
     * <ul>
     *      <li>Has a NON-null subtree as a right child.</li>
     *      <li>Has a NULL subtree as a right child.</li>
     * </ul>
     *
     * <p>You should consult the class slides, your notes, and the textbook about what you need to do in those two
     * special cases.</p>
     * @param currDim The current dimension to consider.
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #insert(KDPoint, int, int)
     * @return A reference to this after the deletion takes place.
     */
    public KDTreeNode delete(KDPoint pIn, int currDim, int dims){
        if(this.p.equals(pIn)) { //current point is deletion target
        	if(this.left == null && this.right == null) { //both subtree null, this is leaf node
        		return null;
        	}else if(this.right != null){ //right sub-tree exists
        		KDTreeNode min = this.right.findMin(this.right, currDim,newDim(currDim,dims), dims); //finding min successor from right subtree
        		this.p = new KDPoint(min.p);  //replacing current point with found min
        		this.right = this.right.delete(new KDPoint(min.p), newDim(currDim,dims), dims);//recursively delete the min node found below the current node
        		return this;
        	}else{ //right child null, left child non-null
        		KDTreeNode min = this.left.findMin(this.left, currDim, newDim(currDim,dims), dims); //finding min scucessor from left subtree
        		this.p = new KDPoint(min.p);
        		this.right = this.left; // flipping leftsubtree to right
        		this.left = null;
        		this.right = this.right.delete(new KDPoint(min.p), newDim(currDim,dims), dims);
        		return this;
        	}
        }else if(pIn.coords[currDim] >= this.p.coords[currDim]) {  //go right 
        	this.right = this.right.delete(pIn, newDim(currDim,dims), dims);
        	return this;
        }else { //go left.
        	this.left = this.left.delete(pIn, newDim(currDim,dims), dims);
        	return this;
        }
    }

    /**
     * Searches the subtree rooted at the current node for the provided {@link KDPoint}.
     * @param pIn The {@link KDPoint} to search for.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @return true iff pIn was found in the subtree rooted at this, false otherwise.
     */
    public  boolean search(KDPoint pIn, int currDim, int dims){
       if(this.p.equals(pIn)) {
    	   return true;
       }else if(pIn.coords[currDim] >= this.p.coords[currDim]){ // traversing right.
    	   if(this.right == null) {
    		   return false;
    	   }else {
    		   return this.right.search(pIn, newDim(currDim,dims), dims);
    	   }
       }else { // left
    	   if(this.left == null) {
    		   return false;
    	   }else {
    		   return this.left.search(pIn, newDim(currDim,dims), dims);
    	   }
       }
    }

    /**
     * <p>Executes a range query in the given {@link KDTreeNode}. Given an &quot;anchor&quot; {@link KDPoint},
     * all {@link KDPoint}s that have a {@link KDPoint#euclideanDistance(KDPoint) euclideanDistance} of <b>at most</b> range
     * <b>INCLUSIVE</b> from the anchor point <b>except</b> for the anchor itself should be inserted into the {@link Collection}
     * that is passed.</p>
     *
     * <p>Remember: range queries behave <em>greedily</em> as we go down (approaching the anchor as &quot;fast&quot;
     * as our currDim allows and <em>prune subtrees</em> that we <b>don't</b> have to visit as we backtrack. Consult
     * all of our resources if you need a reminder of how these should work.</p>
     *
     * @param anchor The centroid of the hypersphere that the range query implicitly creates.
     * @param results A {@link Collection} that accumulates all the {@link }
     * @param currDim The current dimension examined by the {@link KDTreeNode}.
     * @param dims The total number of dimensions of our {@link KDPoint}s.
     * @param range The <b>INCLUSIVE</b> range from the &quot;anchor&quot; {@link KDPoint}, within which all the
     *              {@link KDPoint}s that satisfy our query will fall. The euclideanDistance metric used} is defined by
     *              {@link KDPoint#euclideanDistance(KDPoint)}.
     */
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range, int currDim , int dims){
    	if(anchor.euclideanDistance(this.p) > 0 && anchor.euclideanDistance(this.p) <= range && !anchor.equals(this.p))  {// 0<d(x,p)<=r  adding to collection
    		results.add(this.p);
    	}
    	if(anchor.coords[currDim] >= this.p.coords[currDim]) { //anchor on right, checking right sub
    		//greedy descent
    		if(this.right != null) {
    			this.right.range(anchor, results, range, newDim(currDim,dims), dims);
    		}
    		//right backtracking and checking other child if can be pruned
    		if(Math.abs((anchor.coords[currDim] - this.p.coords[currDim])) <= range && this.left != null) {
    			this.left.range(anchor, results, range, newDim(currDim,dims), dims);
    		}
    	}else{ //anchor on left, checking left sub.
    		//greedy descent
    		if(this.left != null) {
    			this.left.range(anchor, results, range, newDim(currDim,dims), dims);
    		}
    		//backtrack and checking other child.
    		if(Math.abs((anchor.coords[currDim] - this.p.coords[currDim])) <= range && this.right != null) {
    			this.right.range(anchor, results, range, newDim(currDim,dims), dims);
    		}
    	}
    	//if either of two above did not run, its a leaf node. unwrapping.
    }


    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>best solution</b>, which is passed as
     * an argument. This approach is known in Computer Science as &quot;branch-and-bound&quot; and it helps us solve an
     * otherwise exponential complexity problem (nearest neighbors) efficiently. Remember that when we want to determine
     * if we need to recurse to a different subtree, it is <b>necessary</b> to compare the euclideanDistance reported by
     * {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences! Those are comparable with each other because they
     * are the same data type ({@link Double}).</p>
     *
     * @return An object of type {@link NNData}, which exposes the pair (distance_of_NN_from_anchor, NN),
     * where NN is the nearest {@link KDPoint} to the anchor {@link KDPoint} that we found.
     *
     * @param anchor The &quot;ancor&quot; {@link KDPoint}of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param n An object of type {@link NNData}, which will define a nearest neighbor as a pair (distance_of_NN_from_anchor, NN),
     *      * where NN is the nearest neighbor found.
     *
     * @see NNData
     * @see #kNearestNeighbors(int, KDPoint, BoundedPriorityQueue, int, int)
     */
    public  NNData<KDPoint> nearestNeighbor(KDPoint anchor, int currDim,  NNData<KDPoint> n, int dims){
    	NNHelper(anchor,currDim,n,dims);
    	return n;
    	
    }
    
    public void NNHelper(KDPoint anchor, int currDim, NNData<KDPoint> n, int dims){
    	if(n.getBestGuess() == null && !anchor.equals(this.p)){  //initialzing n with root.
    		n.update(this.p, anchor.euclideanDistance(this.p));
    	}
    	
    	if(anchor.euclideanDistance(this.p) > 0 && anchor.euclideanDistance(this.p) < n.getBestDist()) {//this point distance is strictly shorter than best dist so far
    		n.update(this.p, anchor.euclideanDistance(this.p));
    	}
    	
    	if(anchor.coords[currDim] >= this.p.coords[currDim] && this.right !=null) { //anchor on right, checking right sub
    		this.right.nearestNeighbor(anchor, newDim(currDim,dims), n ,dims);
    		//right backtracking and checking other child
    		if(Math.abs((anchor.coords[currDim] - this.p.coords[currDim])) <= n.getBestDist() && this.left != null) {
    			 this.left.NNHelper(anchor, newDim(currDim,dims), n ,dims);
    		}
    	}else if(anchor.coords[currDim] < this.p.coords[currDim] && this.left !=null){ //anchor on left, checking left sub.
    		this.left.nearestNeighbor(anchor, newDim(currDim,dims), n ,dims);
    		//backtrack and checking other child.
    		if(Math.abs((anchor.coords[currDim] - this.p.coords[currDim])) <= n.getBestDist() && this.right != null) {
    			this.right.NNHelper(anchor, newDim(currDim,dims), n ,dims);
    		}
    	}
    	
    	//if either of two above did not run, its a leaf node. unwrapping.	
    }
    
    
    
    

    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>worst solution</b>, which is maintained as the
     * last element of the provided {@link BoundedPriorityQueue}. This is another instance of &quot;branch-and-bound&quot;
     * Remember that when we want to determine if we need to recurse to a different subtree, it is <b>necessary</b>
     * to compare the euclideanDistance reported by* {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences!
     * Those are comparable with each other because they are the same data type ({@link Double}).</p>
     *
     * <p>The main difference of the implementation of this method and the implementation of
     * {@link #nearestNeighbor(KDPoint, int, NNData, int)} is the necessity of using the class
     * {@link BoundedPriorityQueue} effectively. Consult your various resources
     * to understand how you should be using this class.</p>
     *
     * @param k The total number of neighbors to retrieve. It is better if this quantity is an odd number, to
     *          avoid ties in Binary Classification tasks.
     * @param anchor The &quot;anchor&quot; {@link KDPoint} of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param queue A {@link BoundedPriorityQueue} that will maintain at most k nearest neighbors of
     *              the anchor point at all times, sorted by euclideanDistance to the point.
     *
     * @see BoundedPriorityQueue
     */
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue, int currDim, int dims){
      	 	
    	if(!this.p.equals(anchor)){
    		queue.enqueue(this.p, this.p.euclideanDistance(anchor));
        }
    	
    	if (anchor.coords[currDim] >= this.p.coords[currDim]){ //go right
        
            if(this.right != null) {//keep geedly descending
            	   this.right.kNearestNeighbors(k, anchor, queue, newDim(currDim,dims), dims);
            }
            //back tracking, check left child
            if (this.left != null){ 
            	if (queue.size() < k){ //queue not full add the next child
            		this.left.kNearestNeighbors(k, anchor, queue, newDim(currDim,dims), dims);
                }else{ // queue full so check if can be pruned.
                	if(queue.last().euclideanDistance(anchor) >= this.left.p.euclideanDistance(anchor)){
                		this.left.kNearestNeighbors(k, anchor, queue, newDim(currDim,dims), dims);
                    }
                 }
            }
        }else{//go left
            if(this.left != null) {//
            	this.left.kNearestNeighbors(k, anchor, queue, newDim(currDim,dims), dims);
            }
            
            if (this.right != null){ //right is not null have to check
            	if (queue.size() < k){ // if queue not full go right w/o checking                 
            		this.right.kNearestNeighbors(k, anchor, queue, newDim(currDim,dims), dims);
                }else{ //queue is full check if can be pruned.
                	if(queue.last().euclideanDistance(anchor) >= this.right.p.euclideanDistance(anchor)){
                		this.right.kNearestNeighbors(k, anchor, queue, newDim(currDim,dims), dims);
                    }
                }
            } 
        }
    }




    /**
     * Returns the height of the subtree rooted at the current node. Recall our definition of height for binary trees:
     * <ol>
     *     <li>A null tree has a height of -1.</li>
     *     <li>A non-null tree has a height equal to max(height(left_subtree), height(right_subtree))+1</li>
     * </ol>
     * @return the height of the subtree rooted at the current node.
     */
    public int height(){
        if(p == null) {
        	return -1;
        }else if(this.left == null && this.right == null) {//p exists with no child, height ==0 
        	return 0;
        }else if(this.left == null && this.right != null){ //only right subtree exists, height == right subtree height +1
        	return this.right.height()+1;
        }else if(this.left != null && this.right == null) {
        	return this.left.height()+1;
        }else{
        	return Math.max(this.left.height(), this.right.height()) + 1;
        }
        
    }

    /**
     * A simple getter for the {@link KDPoint} held by the current node. Remember: {@link KDPoint}s ARE
     * MUTABLE, SO WE NEED TO DO DEEP COPIES!!!
     * @return The {@link KDPoint} held inside this.
     */
    public KDPoint getPoint(){
        KDPoint retP = new KDPoint(this.p);
        return retP;
    }

    public KDTreeNode getLeft(){
        return this.left;// ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    public KDTreeNode getRight(){
        return this.right; // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }
}
