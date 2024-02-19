package de.unileipzig.nw18a.alfextract.treemapping;

/**
 * Represents a lateral gene transfer event of one gene of a donor species to a new gene in another
 * species.
 */
public class LateralGeneTransferEvent extends Event {

  /** Species that contains the gene that is transfered. */
  private SpeciesNode donorSpec;
  /** Gene that is transfered. */
  private GeneNode donorGene;
  /** Species to which the gene is transfered. */
  private SpeciesNode acceptorSpec;
  /** Newly created gene in the acceptor species. */
  private GeneNode newGene;

  /**
   * Creates a lateral gene transfer event for a lgt of a donor gene of a donor species to new gene
   * of an other species.
   * @param donorSpec     species containing the transfered gene
   * @param donorGene     gene that is transfered
   * @param acceptorSpec  species to which the gene is transfered
   * @param newGene       newly generated gene in the acceptor species
   */
  public LateralGeneTransferEvent(SpeciesNode donorSpec, GeneNode donorGene,
      SpeciesNode acceptorSpec, GeneNode newGene) {
    super(EventType.LGT);
    this.donorSpec = donorSpec;
    this.donorGene = donorGene;
    this.acceptorSpec = acceptorSpec;
    this.newGene = newGene;
  }

  /**
   * Creates an empty lgt event (should eventually be removed).
   */
  public LateralGeneTransferEvent() {
    super(EventType.LGT);
  }

  public SpeciesNode getDonorSpec() {
    return donorSpec;
  }

  public GeneNode getDonorGene() {
    return donorGene;
  }

  public SpeciesNode getAcceptorSpec() {
    return acceptorSpec;
  }

  public GeneNode getNewGene() {
    return newGene;
  }

  public void setDonorSpec(SpeciesNode donorSpec) {
    this.donorSpec = donorSpec;
  }

  public void setDonorGene(GeneNode donorGene) {
    this.donorGene = donorGene;
  }

  public void setAcceptorSpec(SpeciesNode acceptorSpec) {
    this.acceptorSpec = acceptorSpec;
  }

  public void setNewGene(GeneNode newGene) {
    this.newGene = newGene;
  }
}
