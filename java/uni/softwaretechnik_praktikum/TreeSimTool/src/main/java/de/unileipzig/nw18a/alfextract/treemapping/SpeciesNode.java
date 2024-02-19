package de.unileipzig.nw18a.alfextract.treemapping;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Represents a speciation event of one gene to a new gene.
 */
public class SpeciesNode extends AbstractNode {

  /**
   * List of genes contained in this node of the species tree.
   */
  private TreeMap<Integer, GeneNode> genes;
  /**
   * List of gene events alongside the incoming edge of this node, sorted in ascending order
   * according to the time when they occured.
   */
  private TreeSet<GeneNode> edgeEvents;

  /**
   * Creates a species node and sets all its inherited members according to the given parameters.
   *
   * @param parent           parent node the new node
   * @param leftChild        left child of the new node
   * @param rightChild       right child of the new node
   * @param distanceToParent distance (corresponding to time) to the direct parent node
   * @param distanceToRoot   distance (cooresponding to time) to the root of the whole tree
   * @param id               for leaves the id of the species; for inner nodes id of the speciation
   */
  public SpeciesNode(SpeciesNode parent, SpeciesNode leftChild, SpeciesNode rightChild,
                     double distanceToParent, double distanceToRoot, int id) {

    super(parent, leftChild, rightChild, distanceToParent, distanceToRoot, id);
    edgeEvents = new TreeSet<GeneNode>(new DistanceComparator());
    genes = new TreeMap<Integer, GeneNode>();
  }

  /**
   * Creates a species node and sets its distances and id.
   *
   * @param distanceToParent distance (corresponding to time) to the direct parent node
   * @param distanceToRoot   distance (cooresponding to time) to the root of the whole tree
   * @param id               for leaves the id of the species; for inner nodes id of the speciation
   */
  public SpeciesNode(double distanceToParent, double distanceToRoot, int id) {
    super(distanceToParent, distanceToRoot, id);
    edgeEvents = new TreeSet<GeneNode>(new DistanceComparator());
    genes = new TreeMap<Integer, GeneNode>();
  }

  /**
   * Creates a species node and sets only its id.
   *
   * @param id for leaves the id of the species; for inner nodes id of the speciation
   */
  public SpeciesNode(int id) {
    super(id);
    edgeEvents = new TreeSet<GeneNode>(new DistanceComparator());
    genes = new TreeMap<Integer, GeneNode>();
  }

  /**
   * Creates a species node without setting any of its member variables.
   */
  public SpeciesNode() {
    super();
    edgeEvents = new TreeSet<GeneNode>(new DistanceComparator());
    genes = new TreeMap<Integer, GeneNode>();
  }

  public TreeMap<Integer, GeneNode> getGenes() {
    return genes;
  }

  public void setEdgeEvents(TreeSet<GeneNode> edgeEvents) {
    this.edgeEvents = edgeEvents;
  }

  public TreeSet<GeneNode> getEdgeEvents() {
    return edgeEvents;
  }

  /**
   * Adds the event node to the species nodes list of events that occurred until the last
   * speciation.
   *
   * <b>ATTENTION:</b> The TreeSet holding the edge events is sorted by the distToRoot of its
   * elements. So distToRoot of the newly inserted element must already been set before calling this
   * function in order to maintain the order of the edge events tree set.
   *
   * @param event event node to insert
   * @return <code>true</code> if this set did not already contain the specified element;
   *         <code>false</code> otherwise
   */
  public boolean addEdgeEvent(GeneNode event) {
    return edgeEvents.add(event);
  }

  /**
   * Remove the given event node from the species nodes list of events.
   *
   * @param event event node to remove
   * @return <code>true</code> if this set contained the specified element;
   *         <code>false</code> otherwise
   */
  public boolean removeEdgeEvent(GeneNode event) {
    return edgeEvents.remove(event);
  }

  @Override
  public String toString() {
    if (this.leftChild == null && this.rightChild == null) {
      // leaf
      return String.format("SE%03d", this.getId());
    } else {
      // inner node
      return String.format("S%03d", this.getId());
    }
  }

  /**
   * Defines an order between genes based on the canonical order of their distances to the root.
   */
  class DistanceComparator implements Comparator<GeneNode> {
    /**
     * Compares two gene nodes based on their distance to the root of the tree.
     *
     * @param a first gene node to compare
     * @param b second gene node to compare
     * @return value if a {@literal <} b, 0 if a == b and positive value if a {@literal >} b
     */
    @Override
    public int compare(GeneNode a, GeneNode b) {
      int distComp = Double.compare(a.distanceToRoot, b.distanceToRoot);
      if (distComp == 0) {
        // tie-breaker:
        return (a.getId() < b.getId()) ? -1 : ((a.getId() > b.getId()) ? 1 : 0);
      }
      return distComp;
    }
  }

  /**
   * Adds the GeneNode gn to the genes array.
   * @param gn the GeneNode that is added
   */
  public void addGene(GeneNode gn) {
    genes.put(gn.getId(),gn);
  }

  /**
   * removes the GeneNode gn form the array.
   * @param gn the GeneNode that is removed
   */
  public void removeGene(GeneNode gn) {
    genes.remove(gn.getId());
  }
}
