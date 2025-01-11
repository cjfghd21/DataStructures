package spatial.nodes;


import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;
import spatial.trees.CentroidAccuracyException;
import spatial.trees.PRQuadTree;

import java.util.Collection;

import javax.management.InvalidAttributeValueException;


/** <p>A {@link PRQuadGrayNode} is a gray (&quot;mixed&quot;) {@link PRQuadNode}. It
 * maintains the following invariants: </p>
 * <ul>
 *      <li>Its children pointer buffer is non-null and has a length of 4.</li>
 *      <li>If there is at least one black node child, the total number of {@link KDPoint}s stored
 *      by <b>all</b> of the children is greater than the bucketing parameter (because if it is equal to it
 *      or smaller, we can prune the node.</li>
 * </ul>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 *  @author --- Cheolhong Ahn! ---
 */
public class PRQuadGrayNode extends PRQuadNode{


    /* ******************************************************************** */
    /* *************  PLACE ANY  PRIVATE FIELDS AND METHODS HERE: ************ */
    /* ********************************************************************** */
	private int height, count;
	private PRQuadNode [] children; // four childrens.
	private KDPoint centroid;
	
	/**
	 *calculates a new quadrant given parent centroid x,y, and which quad it is.
	 * 
	 * @param x  the x value of this centroid
	 * @param y  the y value of this centroid
	 * @param k  the current k of this.
	 * @param quad  the quad new point is located  in z order: 0 == left top,  1 == right top, 2 == left bottom 3 == right bottom
	 * 
	 * @return returns the newly calculated centroid 
	 * @throws InvalidAttributeValueException 
	 * 
	 */
	private KDPoint newCentroid(int x, int y, int k, int quad){
		int newCalc = (int) Math.pow(2, k-2); //value used as part of new centroid formula  
		
		if(quad == 0) {//left top quad
			return new KDPoint(x-newCalc, y+newCalc);
		}else if(quad == 1) { //right top quad
			return new KDPoint(x+newCalc, y+newCalc);
		}else if(quad == 2) { // left bottom quad
			return new KDPoint(x-newCalc, y-newCalc);
		}else if(quad == 3) { // right bottom quad
			return new KDPoint(x+newCalc, y-newCalc);
		}
		return null;// invalid input
	}
	
	

    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */

    /**
     * Creates a {@link PRQuadGrayNode}  with the provided {@link KDPoint} as a centroid;
     * @param centroid A {@link KDPoint} that will act as the centroid of the space spanned by the current
     *                 node.
     * @param k The See {@link PRQuadTree#PRQuadTree(int, int)} for more information on how this parameter works.
     * @param bucketingParam The bucketing parameter fed to this by {@link PRQuadTree}.
     * @see PRQuadTree#PRQuadTree(int, int)
     */
    public PRQuadGrayNode(KDPoint centroid, int k, int bucketingParam){
        super(centroid, k, bucketingParam); // Call to the super class' protected constructor to properly initialize the object!
        this.centroid = centroid;
        this.k = k;
        this.bucketingParam = bucketingParam;
        this.count =0;
        this.height =1;
        this.children = new PRQuadNode[4];  //z order : [0] == left top,  [1] == right top, [2] == left bottom [3] ==right bottom
    }


    /**
     * <p>Insertion into a {@link PRQuadGrayNode} consists of navigating to the appropriate child
     * and recursively inserting elements into it. If the child is a white node, memory should be allocated for a
     * {@link PRQuadBlackNode} which will contain the provided {@link KDPoint} If it's a {@link PRQuadBlackNode},
     * refer to {@link PRQuadBlackNode#insert(KDPoint, int)} for details on how the insertion is performed. If it's a {@link PRQuadGrayNode},
     * the current method would be called recursively. Polymorphism will allow for the appropriate insert to be called
     * based on the child object's runtime object.</p>
     * @param p A {@link KDPoint} to insert into the subtree rooted at the current {@link PRQuadGrayNode}.
     * @param k The side length of the quadrant spanned by the <b>current</b> {@link PRQuadGrayNode}. It will need to be updated
     *          per recursive call to help guide the input {@link KDPoint}  to the appropriate subtree.
     * @return The subtree rooted at the current node, potentially adjusted after insertion.
     * @see PRQuadBlackNode#insert(KDPoint, int)
     */
    @Override
    public PRQuadNode insert(KDPoint p, int k) {
    	int cx = this.centroid.coords[0];
    	int cy = this.centroid.coords[1];

    	//p out of bounds, does not add and return.
    	if(this.k < 0){
    		throw new CentroidAccuracyException("out of bounds");
    	}else {
	    	if(p.coords[0] < cx && p.coords[1] >= cy){ //left top quad
	    		if(this.children[0] == null) {  //is white node, creating new blacknode.
	    			//assigning left top quad new blacknode.
	    			this.children[0] = new PRQuadBlackNode(newCentroid(cx,cy,k,0), k-1,  this.bucketingParam, p); 
	    			
	    		}else {//child node exists, either gray or black.
	    			this.children[0] =this.children[0].insert(p, k-1);  //insert using this children's insert function whether it be gray or black.
	    		}
	    		
	    		//rest of the quadrants do same thing as above
	    	}else if(p.coords[0] >= cx && p.coords[1] >=cy) { //right top quad
	    		if(this.children[1] == null) {
	    			this.children[1] = new PRQuadBlackNode(newCentroid(cx,cy,k,1), k-1,  this.bucketingParam, p); 
	    		}else {
	    			 this.children[1] = this.children[1].insert(p, k-1); 
	    		}
	    		
	    	}else if(p.coords[0] < cx && p.coords[1] < cy){ //left bottom quad
	    		if(this.children[2] == null) {
	    			this.children[2] = new PRQuadBlackNode(newCentroid(cx,cy,k,2), k-1,  this.bucketingParam, p); 
	    		}else {
	    			this.children[2] =this.children[2].insert(p, k-1); 
	    		}
	    		
	    	}else if(p.coords[0] >= cx && p.coords[1] < cy) { //right bottom quad
	    		if(this.children[3] == null) {
	    			this.children[3] = new PRQuadBlackNode(newCentroid(cx,cy,k,3), k-1,  this.bucketingParam, p); 
	    		}else {
	    			this.children[3] =this.children[3].insert(p, k-1); 
	    		}
	    	}else {//correct input must run either four of above, incorrect input would be filtered above but this is just here just in-case
	    		return this;
	    	}
	    	
	    	//below portion only runs if either one of four adding ran.
	    	this.count++;
	    	//updating height
	    	int maxChildHeight = -1;
	    	for(int i=0; i < this.children.length; i++) {
	    		if(this.children[i] !=null && this.children[i].height() > maxChildHeight) {
	    			maxChildHeight =this.children[i].height();
	    		}
	    	}
	    	this.height = maxChildHeight +1;
	    	return this;
    	}
    }

    /**
     * <p>Deleting a {@link KDPoint} from a {@link PRQuadGrayNode} consists of recursing to the appropriate
     * {@link PRQuadBlackNode} child to find the provided {@link KDPoint}. If no such child exists, the search has
     * <b>necessarily failed</b>; <b>no changes should then be made to the subtree rooted at the current node!</b></p>
     *
     * <p>Polymorphism will allow for the recursive call to be made into the appropriate delete method.
     * Importantly, after the recursive deletion call, it needs to be determined if the current {@link PRQuadGrayNode}
     * needs to be collapsed into a {@link PRQuadBlackNode}. This can only happen if it has no gray children, and one of the
     * following two conditions are satisfied:</p>
     *
     * <ol>
     *     <li>The deletion left it with a single black child. Then, there is no reason to further subdivide the quadrant,
     *     and we can replace this with a {@link PRQuadBlackNode} that contains the {@link KDPoint}s that the single
     *     black child contains.</li>
     *     <li>After the deletion, the <b>total</b> number of {@link KDPoint}s contained by <b>all</b> the black children
     *     is <b>equal to or smaller than</b> the bucketing parameter. We can then similarly replace this with a
     *     {@link PRQuadBlackNode} over the {@link KDPoint}s contained by the black children.</li>
     *  </ol>
     *
     * @param p A {@link KDPoint} to delete from the tree rooted at the current node.
     * @return The subtree rooted at the current node, potentially adjusted after deletion.
     */
    @Override
    public PRQuadNode delete(KDPoint p) {
    	int cx = this.centroid.coords[0];
    	int cy = this.centroid.coords[1];
	
    	if(this.k < 0) {
    		throw new CentroidAccuracyException("K below 0");
    	}else {
	    	
			if(p.coords[0] < cx && p.coords[1] >= cy){ //p is somewhere in left top child
				if(this.children[0] == null){ //but that quad white so search failed.
					return this;
				}else {
					this.children[0]= this.children[0].delete(p); // calls apporopriate delete function whether the children is gray or black.
				}
			}else if(p.coords[0] >= cx && p.coords[1] >=cy) { //right top quad
				if(this.children[1] == null){ //but that quad white so search failed.
					return this;
				}else {
					this.children[1]= this.children[1].delete(p); // calls apporopriate delete function whether the children is gray or black.
				}
			}else if(p.coords[0] < cx && p.coords[1] < cy){ //left bottom quad
				if(this.children[2] == null){ //but that quad white so search failed.
					return this;
				}else {
					this.children[2]= this.children[2].delete(p); // calls apporopriate delete function whether the children is gray or black.
				}
			}else { //right bottom quad
				if(this.children[3] == null){ //but that quad white so search failed.
					return this;
				}else {
					this.children[3]= this.children[3].delete(p); // calls apporopriate delete function whether the children is gray or black.
				}
			}
			
			//deletion unwrapping and checking changes.
			this.count --;
			boolean merge = true;
			int pointCount = 0;
			for(PRQuadNode node : this.children) {//check its children.
				if(node !=null) {
					if(node instanceof PRQuadGrayNode) {//gray node child found, can't merge.
						merge = false;
						break;
					}else if(node instanceof PRQuadBlackNode) { // black node found add #points to count
						pointCount += node.count(); //accumulating total points found.
						if(pointCount > this.bucketingParam) {//point count exceed bucket can't merge.
				    		merge=false;
				    		break;
						}
					}
				}
			}
			
			//checking merge
			if(pointCount == 0 && merge) { //merge is true but point count =0,  returning white node
				return null;
			}else if(merge){ //merging.
	    		PRQuadBlackNode newBlack = new PRQuadBlackNode(this.centroid, k , bucketingParam);
	    		for(PRQuadNode node : this.children) {
	    			if(node != null && node instanceof PRQuadBlackNode) {
	    				for(KDPoint pt : ((PRQuadBlackNode)node).getPoints()) {
	    					newBlack.insert(pt, k);
	    				}
	    			}
	    		}
	    		this.height --;
	    		return newBlack;
			}else{ // can't merge
				//updating height
		    	int maxChildHeight = -1;
		    	for(int i=0; i < this.children.length; i++) {
		    		if(this.children[i] !=null && this.children[i].height() > maxChildHeight) {
		    			maxChildHeight =this.children[i].height();
		    		}
		    	}
		    	this.height = maxChildHeight +1;
				return this;
			}
    	}

	
    }

    @Override
    public boolean search(KDPoint p){
    	int cx = this.centroid.coords[0];
    	int cy = this.centroid.coords[1];
    	
    	//the target point is out of bounds of current quadrant.
    	if(this.k < 0) {
    		return false;
    	}else {
    		if(p.coords[0] < cx && p.coords[1] >= cy){ //p is somewhere in left top quad
    			if(this.children[0] == null){ //but that quad white so search failed.
    				return false;
    			}else if(this.children[0] instanceof PRQuadGrayNode){ //gray node found, descent.
    				return this.children[0].search(p);
    			}else { //black node reached.
    				return this.children[0].search(p);
    			}
    		}else if(p.coords[0] >= cx && p.coords[1] >=cy) { //right top quad
    			if(this.children[1] == null){ //but that quad white so search failed.
    				return false;
    			}else if(this.children[1] instanceof PRQuadGrayNode){ //gray node found, descent.
    				return this.children[1].search(p);
    			}else { //black node reached.
    				return this.children[1].search(p);
    			}
    		}else if(p.coords[0] < cx && p.coords[1] < cy){ //left bottom quad
    			if(this.children[2] == null){ //but that quad white so search failed.
    				return false;
    			}else if(this.children[2] instanceof PRQuadGrayNode){ //gray node found, descent.
    				return this.children[2].search(p);
    			}else { //black node reached.
    				return this.children[2].search(p);
    			}
    		}else { //right bottom quad
    			if(this.children[3] == null){ //but that quad white so search failed.
    				return false;
    			}else if(this.children[3] instanceof PRQuadGrayNode){ //gray node found, descent.
    				return this.children[3].search(p);
    			}else { //black node reached.
    				return this.children[3].search(p);
    			}
    		}
    	}
    	
    }

    @Override
    public int height(){
    	 return this.height; // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    @Override
    public int count(){
        return this.count;// ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    /**
     * Returns the children of the current node in the form of a Z-ordered 1-D array.
     * @return An array of references to the children of {@code this}. The order is Z (Morton), like so:
     * <ol>
     *     <li>0 is NW</li>
     *     <li>1 is NE</li>
     *     <li>2 is SW</li>
     *     <li>3 is SE</li>
     * </ol>
     */
    public PRQuadNode[] getChildren(){
        return this.children; // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    @Override
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range) {
    	
    	int cx = this.centroid.coords[0];
    	int cy = this.centroid.coords[1];
    	int checked;
    	
    	if(anchor.coords[0] < cx && anchor.coords[1] >= cy){ //anchor is somewhere towards left top quad
    		checked = 0;
    		if(this.children[0] != null){
    			//runs appropriate range function. If black node, check and add, if gray, keep descend.
    			this.children[0].range(anchor, results, range); 
    		}
    	}else if(anchor.coords[0] >= cx && anchor.coords[1] >=cy) { //right top quad
    		checked = 1;
    		if(this.children[1] != null){
    			this.children[1].range(anchor, results, range); 
    		}
    	}else if(anchor.coords[0] < cx && anchor.coords[1] < cy){ //left bottom quad
    		checked = 2;
    		if(this.children[2] == null){ //but that quad white so search failed.
    			this.children[2].range(anchor, results, range); 
    		}
    	}else { //right bottom quad
    		checked = 3;
    		if(this.children[3] == null){ //but that quad white so search failed.
    			this.children[3].range(anchor, results, range); 
    		}
    	}
    	
    	//unwrapping and visiting in z order except visited. also check if pruneable
    	for(int i=0; i< this.children.length; i++) {
    		//child is non-null, non-visited and in range. otherwise, pruning.
    		if(this.children[i] != null && i!=checked && this.children[i].doesQuadIntersectAnchorRange(anchor, range)) {
    			this.children[i].range(anchor, results, range);;
    		}
    	}
    	
    }

    @Override
    public NNData<KDPoint> nearestNeighbor(KDPoint anchor, NNData<KDPoint> n)  {
    	//basically same as range except using best range to check prune.
    	nnPRQHelper(anchor,n);
    	return n;
    }
    
    public void nnPRQHelper(KDPoint anchor, NNData<KDPoint> n) {
    	int cx = this.centroid.coords[0];
    	int cy = this.centroid.coords[1];
    	int checked;
    	
    	if(anchor.coords[0] < cx && anchor.coords[1] >= cy){ //anchor is somewhere towards left top quad
    		checked = 0;
    		//greedy descent. once black node reached, black node's nearestNeibor function runs instead.
    		if(this.children[0] != null){
    			this.children[0].nearestNeighbor(anchor, n); 
    		}
    	}else if(anchor.coords[0] >= cx && anchor.coords[1] >=cy) { //right top quad
    		checked = 1;
    		if(this.children[1] != null){
    			this.children[1].nearestNeighbor(anchor, n); 
    		}
    	}else if(anchor.coords[0] < cx && anchor.coords[1] < cy){ //left bottom quad
    		checked = 2;
    		if(this.children[2] != null){
    			this.children[2].nearestNeighbor(anchor, n); 
    		}
    	}else { //right bottom quad
    		checked = 3;
    		if(this.children[3] != null){
    			this.children[3].nearestNeighbor(anchor, n); 
    		}
    	}
    	
    	//unwrapping and visiting in z order except visited. also check if pruneable
    	for(int i=0; i< this.children.length; i++) {
    		//if n not initialzed due to meeting null leaf, check other branch w/o checking distance.
    		//else check distance and see if can be pruned.
    		if(this.children[i] != null && n.getBestGuess() == null && i!=checked) {
    			this.children[i].nearestNeighbor(anchor,n);
    		}else if(this.children[i] != null && i!=checked && this.children[i].doesQuadIntersectAnchorRange(anchor, n.getBestDist())) {
    			this.children[i].nearestNeighbor(anchor,n);
    		}
    	}
    }

    @Override
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue) {
    	int cx = this.centroid.coords[0];
    	int cy = this.centroid.coords[1];
    	int checked;
    	
    	if(anchor.coords[0] < cx && anchor.coords[1] >= cy){ //anchor is somewhere towards left top quad
    		checked = 0;
    		//greedy descent. once black node reached, black node's knearestNeibor function runs instead.
    		if(this.children[0] != null){
    			this.children[0].kNearestNeighbors(k, anchor, queue);
    		}
    	}else if(anchor.coords[0] >= cx && anchor.coords[1] >=cy) { //right top quad
    		checked = 1;
    		if(this.children[1] != null){
    			this.children[1].kNearestNeighbors(k, anchor, queue);
    		}
    	}else if(anchor.coords[0] < cx && anchor.coords[1] < cy){ //left bottom quad
    		checked = 2;
    		if(this.children[2] != null){
    			this.children[2].kNearestNeighbors(k, anchor, queue);
    		}
    	}else { //right bottom quad
    		checked = 3;
    		if(this.children[3] != null){
    			this.children[3].kNearestNeighbors(k, anchor, queue);
    		}
    	}
    	
    	//unwrapping and visiting in z order except visited. also check if pruneable
    	for(int i=0; i< this.children.length; i++) {
    		//checks whether queue is not full, if not full, don't check prune,  else, check for worst distance and see if can be pruned
    		if(this.children[i] != null && queue.size() < k && i!=checked) {
    			this.children[i].kNearestNeighbors(k, anchor, queue);
    		}else if(this.children[i] != null && i!=checked && this.children[i].doesQuadIntersectAnchorRange(anchor,anchor.euclideanDistance(queue.last()))){
    			this.children[i].kNearestNeighbors(k, anchor, queue);
    		}
    	}
    }
}

