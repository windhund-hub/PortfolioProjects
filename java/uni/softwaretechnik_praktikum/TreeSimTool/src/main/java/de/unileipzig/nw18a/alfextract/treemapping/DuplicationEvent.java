package de.unileipzig.nw18a.alfextract.treemapping;

/**
 * Represents a duplication event of a gene to a new gene.
 */
public class DuplicationEvent extends Event {

  /** Gene that was duplicated. */
  private GeneNode donorGene;
  /** Newly created gene. */
  private GeneNode newGene;

  /**
   * Creates a duplication event for a duplication of a donor gene to a new gene.
   * @param donorGene gene that is duplicated
   * @param newGene   gene that results from the duplication
   */
  public DuplicationEvent(GeneNode donorGene, GeneNode newGene) {
    super(EventType.DUP);
    this.donorGene = donorGene;
    this.newGene = newGene;
  }

  /**
   * Creates an empty duplication event (should eventually be removed).
   */
  public DuplicationEvent() {
    super(EventType.DUP);
  }

  public GeneNode getDonorGene() {
    return donorGene;
  }

  public GeneNode getNewGene() {
    return newGene;
  }

  public void setDonorGene(GeneNode donorGene) {
    this.donorGene = donorGene;
  }

  public void setNewGene(GeneNode newGene) {
    this.newGene = newGene;
  }
}
