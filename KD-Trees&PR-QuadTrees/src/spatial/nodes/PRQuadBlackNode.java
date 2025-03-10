package spatial.nodes;


import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;
import spatial.trees.PRQuadTree;

import java.util.ArrayList;
import java.util.Collection;


/** <p>A {@link PRQuadBlackNode} is a &quot;black&quot; {@link PRQuadNode}. It maintains the following
 * invariants: </p>
 * <ul>
 *  <li>It does <b>not</b> have children.</li>
 *  <li><b>Once created</b>, it will contain at least one {@link KDPoint}. </li>
 * </ul>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author --- Cheolhong Ahn! ---
 */
public class PRQuadBlackNode extends PRQuadNode {
	
	private ArrayList<KDPoint> bucket;
	private boolean init = false;
	
    /**
     * The default bucket size for all of our black nodes will be 1, and this is something
     * that the interface also communicates to consumers.
     */
    public static final int DEFAULT_BUCKETSIZE = 1;

    /* ******************************************************************** */
    /* *************  PLACE ANY  PRIVATE FIELDS AND METHODS HERE: ************ */
    /* ********************************************************************** */
    

    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */


    /**
     * Creates a {@link PRQuadBlackNode} with the provided parameters.
     * @param centroid The {@link KDPoint} which will act as the centroid of the quadrant spanned by the current {@link PRQuadBlackNode}.
     * @param k An integer to which 2 is raised to define the side length of the quadrant spanned by the current {@link PRQuadBlackNode}.
     *          See {@link PRQuadTree#PRQuadTree(int, int)} for a full explanation of how k works.
     * @param bucketingParam The bucketing parameter provided to us {@link PRQuadTree}.
     * @see PRQuadTree#PRQuadTree(int, int)
     * @see #PRQuadBlackNode(KDPoint, int, int, KDPoint)
     */
    public PRQuadBlackNode(KDPoint centroid, int k, int bucketingParam){
        super(centroid, k, bucketingParam); // Call to the super class' protected constructor to properly initialize the object is necessary, even for a constructor that just throws!
        this.bucket = new ArrayList<>();
        this.init = true;
    }

    /**
     * Creates a {@link PRQuadBlackNode} with the provided parameters.
     * @param centroid The centroid of the quadrant spanned by the current {@link PRQuadBlackNode}.
     * @param k The exponent to which 2 is raised in order to define the side of the current quadrant. Refer to {@link PRQuadTree#PRQuadTree(int, int)} for
     *          a thorough explanation of this parameter.
     * @param bucketingParam The bucketing parameter of the {@link PRQuadBlackNode}, passed to us by the {@link PRQuadTree} or {@link PRQuadGrayNode} during
     *                       object construction.
     * @param p The {@link KDPoint} with which we want to initialize this.
     * @see #DEFAULT_BUCKETSIZE
     * @see PRQuadTree#PRQuadTree(int, int)
     * @see #PRQuadBlackNode(KDPoint, int, int)
     */
    public PRQuadBlackNode(KDPoint centroid, int k, int bucketingParam, KDPoint p){
    	  super(centroid, k, bucketingParam); // Call to the current class' other constructor, which takes care of the base class' initialization itself.
    	  this.bucket = new ArrayList<>();
    	  this.bucket.add(p);
    	  this.init = true;
    }


    /**
     * <p>Inserting a {@link KDPoint} into a {@link PRQuadBlackNode} can have one of two outcomes:</p>
     *
     * <ol>
     *     <li>If, after the insertion, the node's capacity is still <b>SMALLER THAN OR EQUAL TO </b> the bucketing parameter,
     *     we should simply store the {@link KDPoint} internally.</li>
     *
     *     <li>If, after the insertion, the node's capacity <b>SURPASSES</b> the bucketing parameter, we will have to
     *     <b>SPLIT</b> the current {@link PRQuadBlackNode} into a {@link PRQuadGrayNode} which will recursively insert
     *     all the available{@link KDPoint}s. This pprocess will continue until we reach a {@link PRQuadGrayNode}
     *     which successfully separates all the {@link KDPoint}s of the quadrant it represents. Programmatically speaking,
     *     this means that the method will polymorphically call itself, splitting black nodes into gray nodes as long as
     *     is required for there to be a set of 4 quadrants that separate the points between them. This is one of the major
     *     bottlenecks in PR-QuadTrees; the presence of a pair of {@link KDPoint}s with a very small {@link
     *     KDPoint#euclideanDistance(KDPoint) euclideanDistance} between them can negatively impact search in certain subplanes, because
     *     the subtrees through which those subplanes will be modeled will be &quot;unnecessarily&quot; tall.</li>
     * </ol>
     *
     * @param p A {@link KDPoint} to insert into the subtree rooted at the current node.
     * @param k The side length of the quadrant spanned by the <b>current</b> {@link PRQuadGrayNode}. It will need to be updated
     *           per recursive call to help guide the input {@link KDPoint} to the appropriate subtree.
     * @return The subtree rooted at the current node, potentially adjusted after insertion.
     */
    @Override
    public PRQuadNode insert(KDPoint p, int k) {
        if(this.bucket.size()+1 <= this.bucketingParam) {//bucket does not overload after adding, so just add.
        	this.bucket.add(p);
        	return this;
        }else {//need to split, creating new grey node.
        	PRQuadGrayNode grey = new PRQuadGrayNode(new KDPoint(centroid),k,bucketingParam);
            for (KDPoint point : this.bucket){ //splitting and re-inserting current points
                grey.insert(point, k);
            }
            grey.insert(p,k); // inserting the new point
            return grey;
        }
    }


    /**
     * <p><b>Successfully</b> deleting a {@link KDPoint} from a {@link PRQuadBlackNode} always decrements its capacity by 1. If, after
     * deletion, the capacity is at least 1, then no further changes need to be made to the node. Otherwise, it can
     * be scrapped and turned into a white node.</p>
     *
     * <p>If the provided {@link KDPoint} is <b>not</b> contained by this, no changes should be made to the internal
     * structure of this, which should be returned as is.</p>
     * @param p The {@link KDPoint} to delete from this.
     * @return Either this or null, depending on whether the node underflows.
     */
    @Override
    public PRQuadNode delete(KDPoint p) {
        if (this.bucket.remove(p)){
            if (this.count() < 1) { //underflow
                return null;
            }
        }
        return this;
    }

    @Override
    public boolean search(KDPoint p){
        return this.bucket.contains(p); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    @Override
    public int height(){
        if(init) {
        	return 0;  //black node is always leaf.
        }else {
        	return -1; //uninitialized node height
        }
    }

    @Override
    public int count()  {
        if(this.bucket !=null) {
        	return this.bucket.size();
        }else {
        	return 0;
        }
    }

    /** Returns all the {@link KDPoint}s contained by the {@link PRQuadBlackNode}. <b>INVARIANT</b>: the returned
     * {@link Collection}'s size can only be between 1 and bucket-size inclusive.
     *
     * @return A {@link Collection} that contains all the {@link KDPoint}s that are contained by the node. It is
     * guaranteed, by the invariants, that the {@link Collection} will not be empty, and it will also <b>not</b> be
     * a null reference.
     */
    public Collection<KDPoint> getPoints()  {
        return this.bucket; // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    @Override
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range) {
        for(KDPoint pt : this.bucket) {
        	if(anchor.euclideanDistance(pt) <= range) {
        		results.add(pt);
        	}
        } 
    }

    @Override
    public NNData<KDPoint> nearestNeighbor(KDPoint anchor, NNData<KDPoint> n) {
        for(KDPoint pt : this.bucket) {
        	if(n.getBestGuess() == null && !anchor.equals(pt)) { //initialze empty n
        		n.update(pt, anchor.euclideanDistance(pt));
        	}else if(n.getBestDist() > anchor.euclideanDistance(pt) && anchor.euclideanDistance(pt) > 0){
        		n.update(pt, anchor.euclideanDistance(pt)); 
        	}
        }
        return n;
    }

    @Override
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue){
        for(KDPoint pt : this.bucket) {
        	if(queue.size() < k ){ //queue is not full, enqueue. 
        		if(!anchor.equals(pt)) {
        			queue.enqueue(pt, anchor.euclideanDistance(pt));
        		}
        	}else if(anchor.euclideanDistance(pt) < anchor.euclideanDistance(queue.last()) && !anchor.equals(pt)) {//if queue is full check the distance with worst and queue if better.
        		queue.enqueue(pt, anchor.euclideanDistance(pt));
        	}
        }
    }
}
