package de.unileipzig.nw18a.alfextract.treemapping;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Represents a node of a generic double-linked tree. Offers two iterators over the subtree under
 * the specific node, one iterating over all inner and leaf nodes and another iterating only over
 * its leaf set.
 */
public abstract class AbstractNode implements Iterable<AbstractNode> {

  /** Parent of this node. */
  protected AbstractNode parent;
  /** Left child of this node. */
  protected AbstractNode leftChild;
  /** Right child of this node. */
  protected AbstractNode rightChild;
  /** Distance to the parent of this node (time). */
  protected double distanceToParent;
  /** Distance to the root node of the tree (time). */
  protected double distanceToRoot;
  /** Id specifying this node. Should but needn't be unique. */
  protected int id;

  /**
   * Creates a node and sets all its members according to the given parameters.
   * @param parent           parent node of the new node
   * @param leftChild        left child of the new node
   * @param rightChild       right child of the new node
   * @param distanceToParent distance (corresponding to time) to the direct parent node
   * @param distanceToRoot   distance (cooresponding to time) to the root of the whole tree
   * @param id               id of this node (should but needn't be unique in the tree)
   */
  public AbstractNode(AbstractNode parent, AbstractNode leftChild, AbstractNode rightChild,
      double distanceToParent, double distanceToRoot, int id) {

    this.parent            = parent;
    this.leftChild         = leftChild;
    if (leftChild != null) {
      this.leftChild.parent  = this;
    }
    this.rightChild        = rightChild;
    if (rightChild != null) {
      this.rightChild.parent = this;
    }
    this.distanceToParent  = distanceToParent;
    this.distanceToRoot    = distanceToRoot;
    this.id                = id;
  }

  /**
   * Creates a node and sets its distances and id.
   * @param distanceToParent distance (corresponding to time) to the direct parent node
   * @param distanceToRoot   distance (cooresponding to time) to the root of the whole tree
   * @param id               id of this node (should but needn't be unique in the tree)
   */
  public AbstractNode(double distanceToParent, double distanceToRoot, int id) {
    this.distanceToParent = distanceToParent;
    this.distanceToRoot   = distanceToRoot;
    this.id               = id;
  }

  /**
   * Creates a node and sets only its id.
   * @param id               id of this node (should but needn't be unique in the tree)
   */
  public AbstractNode(int id) {
    this.id = id;
  }

  /**
   * Creates a node without setting any of its member variables.
   */
  public AbstractNode() {

  }

  /**
   * Checks whether this node is a leaf or not, by checking whether both children are null.
   * @return True iff this node is a leaf
   */
  public boolean isLeaf() {
    return leftChild == null && rightChild == null;
  }


  public AbstractNode getParent() {
    return parent;
  }

  public void setParent(AbstractNode parent) {
    this.parent = parent;
  }

  public AbstractNode getLeftChild() {
    return leftChild;
  }

  /**
   * Sets the left child of this node to the given node. If the given node is not null its parent
   * will be set to this node as well.
   * @param leftChild left child of this node
   */
  public void setLeftChild(AbstractNode leftChild) {
    this.leftChild = leftChild;
    if (leftChild != null) {
      this.leftChild.parent = this;
    }
  }

  public AbstractNode getRightChild() {
    return rightChild;
  }

  /**
   * Sets the right child of this node to the given node. If the given node is not null its parent
   * will be set to this node as well.
   * @param rightChild right child of this node
   */
  public void setRightChild(AbstractNode rightChild) {
    this.rightChild = rightChild;
    if (rightChild != null) {
      this.rightChild.parent = this;
    }
  }

  public double getDistanceToParent() {
    return distanceToParent;
  }

  public void setDistanceToParent(double distanceToParent) {
    this.distanceToParent = distanceToParent;
  }

  public double getDistanceToRoot() {
    return distanceToRoot;
  }

  public void setDistanceToRoot(double distanceToRoot) {
    this.distanceToRoot = distanceToRoot;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gives an iterator over the subtree with this node as root.
   * @return Iterator over the subtree with this node as root
   */
  public Iterator<AbstractNode> iterator() {
    return new NodeIterator();
  }

  /**
   * Gives an iterator over the leaf set of the subtree with this node as root.
   * @return Iterator over the leaf set of the subtree with this node as root
   */
  public Iterator<AbstractNode> leafIterator() {
    return new LeafIterator();
  }

  /**
   * Iterator over the subtree below this node, iterating over all inner and leaf nodes in post
   * order fashion.
   */
  public class NodeIterator implements Iterator<AbstractNode> {
    /**
     * Number of inner nodes where the current iterator position is in the left subtree, i.e. where
     * there would exist the possibility to go right instead of left.
     */
    private int rightNeighboursCount;
    /** Root node of the (sub-)tree that shall be traversed. */
    private AbstractNode root;
    /** Current position of the iterator in the (sub-)tree. */
    private AbstractNode cursor;

    /**
     * Creates an iterator for the underlying tree. It traverses the tree in post-order (thas is
     * first traverse the left subtree, then the right one and finally process the root between
     * those two subtrees). Therefore the first element visited is the left-most leaf.
     */
    public NodeIterator() {
      rightNeighboursCount = 0;
      root = AbstractNode.this;

      cursor = root;

      // as long as the node at cursor has another child
      while (cursor.leftChild != null || cursor.rightChild != null) {
        // if there is a left child go left
        if (cursor.leftChild != null) {
          // if there was a possibility to go right, increment the counter
          if (cursor.rightChild != null) {
            rightNeighboursCount++;
          }
          cursor = cursor.leftChild;
        } else {
          // else go right
          cursor = cursor.rightChild;
        }
      }
    }

    /**
     * Checks whether there is still another node following this one in the post order.
     * @return true iff there is a successor to this node
     */
    public boolean hasNext()  {
      return (rightNeighboursCount != 0 || cursor != root.parent);
    }

    /**
     * Gives the next node in the tree following the post-order pattern.
     * @return next node in the tree
     */
    public AbstractNode next() throws NoSuchElementException {
      if (!this.hasNext()) {
        throw new NoSuchElementException();
      }

      AbstractNode tmpNode = cursor;

      // treat special case separately for readability
      if (cursor == root) {
        cursor = cursor.parent;
        return tmpNode;
      }
      if (cursor == cursor.parent.rightChild) {
        // if we are the right child of our parent: go up (as demanded by post-order)
        cursor = cursor.parent;
        return tmpNode;
      } else {
        // else we are the left child of our parent: we search the right subtree first if such
        // exists and else we also go up
        if (cursor.parent.rightChild != null) {
          rightNeighboursCount--;
          cursor = cursor.parent.rightChild;

          // perform the same search for next leaf, as in constructor:
          // as long as the node at cursor has another child
          while (cursor.leftChild != null || cursor.rightChild != null) {
            // if there is a left child go left
            if (cursor.leftChild != null) {
              // if there was a possibility to go right, increment the counter
              if (cursor.rightChild != null) {
                rightNeighboursCount++;
              }
              cursor = cursor.leftChild;
            } else {
              // else go right
              cursor = cursor.rightChild;
            }
          }
        } else {
          cursor = cursor.parent;
        }
        return tmpNode;
      }
    }

    /**
     * Currently not supported.
     */
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Iterator over the subtree below this node, iterating only over the leaf nodes in post
   * order fashion.
   */
  public class LeafIterator implements Iterator<AbstractNode> {
    /**
     *  NodeIterator over the (sub-)tree that shall be traversed. (Points to a leaf node after each
     *  call to next())
     */
    private NodeIterator nodeIter;
    /** Current position of this iterator in the (sub-)tree. (Next leaf node in the tree) */
    private AbstractNode cursor;

    /**
     * Creates an iterator for the underlying tree. It traverses the leaves of the tree from left
     * to right.
     * @see NodeIterator
     */
    public LeafIterator() {
      nodeIter = new NodeIterator();

      cursor = nodeIter.next();
    }

    /**
     * Checks whether there is still another leaf to be visited.
     * @return true iff there is a successor to this node
     */
    public boolean hasNext()  {
      return (nodeIter.hasNext() || cursor != null);
    }

    /**
     * Gives the next leaf in the tree, in left-to-right order.
     * @return next leaf in the tree
     */
    public AbstractNode next() {
      if (!this.hasNext()) {
        throw new NoSuchElementException();
      }

      final AbstractNode tmpNode = cursor;

      // increment node iterator whenever possible
      if (nodeIter.hasNext()) {
        cursor = nodeIter.next();
      }
      // advance node iterator till next leaf in the tree or complete tree was searched
      while ((cursor.leftChild != null || cursor.rightChild != null) && nodeIter.hasNext()) {
        cursor = nodeIter.next();
      }
      // when there is no node left unvisited in the tree (especially no leaf node) set cursor
      // to null
      if (!nodeIter.hasNext()) {
        cursor = null;
      }
      // return leaf node that was saved in cursor when this function was entered
      return tmpNode;
    }

    /**
     * Currently not supported.
     */
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
