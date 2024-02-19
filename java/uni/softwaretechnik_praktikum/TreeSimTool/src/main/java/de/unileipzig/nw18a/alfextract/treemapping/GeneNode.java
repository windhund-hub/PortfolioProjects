package de.unileipzig.nw18a.alfextract.treemapping;

import java.lang.IllegalStateException;

/**
 * Represents a node of a gene tree.
 */
public class GeneNode extends AbstractNode {

  /**
   * For inner nodes the event represented by this node and null for leaf nodes.
   */
  private Event event;
  /**
   * A node of the corresponding species tree. Either the node in this tree itself if the event of
   * this node is a speciation, or the node in the species tree on whiches incoming edge this event
   * occured.
   */
  private SpeciesNode speciesNode;

  /**
   * Creates a gene node and sets all its inherited members according to the given parameters.
   * @param parent           parent node of the new node
   * @param leftChild        left child of the new node
   * @param rightChild       right child of the new node
   * @param distanceToParent distance (corresponding to time) to the direct parent node
   * @param distanceToRoot   distance (cooresponding to time) to the root of the whole tree
   * @param id               for leaves the id of the gene; for inner nodes id of the gene event
   */
  public GeneNode(GeneNode parent, GeneNode leftChild, GeneNode rightChild,
      double distanceToParent, double distanceToRoot, int id) {

    super(parent, leftChild, rightChild, distanceToParent, distanceToRoot, id);
  }

  /**
   * Creates a gene node and sets its distances and id.
   * @param distanceToParent distance (corresponding to time) to the direct parent node
   * @param distanceToRoot   distance (cooresponding to time) to the root of the whole tree
   * @param id               for leaves the id of the gene; for inner nodes id of the gene event
   */
  public GeneNode(double distanceToParent, double distanceToRoot, int id) {
    super(distanceToParent, distanceToRoot, id);
  }

  /**
   * Creates a gene node and sets only its id.
   * @param id               for leaves the id of the gene; for inner nodes id of the gene event
   */
  public GeneNode(int id) {
    super(id);
  }

  /**
   * Creates a gene node without setting any of its member variables.
   */
  public GeneNode() {
    super();
  }

  public Event getEvent() {
    return event;
  }

  public SpeciesNode getSpeciesNode() {
    return speciesNode;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public void setSpeciesNode(SpeciesNode speciesNode) {
    this.speciesNode = speciesNode;
  }


  @Override
  public String toString() {
    if (event == null) {
      // leaf
      return String.format("SE%03d/%05d", this.getSpeciesNode().getId(), this.getId());
    } else {
      // inner node
      switch (this.getEvent().getType()) {
        case SPEC:
          return String.format("S%03d", this.getId());
        case DUP:
          return String.format("D%03d", this.getId());
        case LGT:
          return String.format("L%03d", this.getId());
        default:
          // should never be reached
          throw new IllegalStateException("Event type unknown!");
      }
    }
  }
}
