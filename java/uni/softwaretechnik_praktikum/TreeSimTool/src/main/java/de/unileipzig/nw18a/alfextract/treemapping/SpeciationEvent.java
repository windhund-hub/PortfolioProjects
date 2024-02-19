package de.unileipzig.nw18a.alfextract.treemapping;

/**
 * Represents a speciation event of a species to a new species.
 */
public class SpeciationEvent extends Event {

  /** Already existing species. */
  private SpeciesNode existingSpec;
  /** Newly created species. */
  private SpeciesNode newSpec;

  /**
   * Creates a speciation event for a speciation of an existing species to a new one.
   * @param existingSpec species that already existed
   * @param newSpec species that is created from the existing one
   */
  public SpeciationEvent(SpeciesNode existingSpec, SpeciesNode newSpec) {
    super(EventType.SPEC);
    this.existingSpec = existingSpec;
    this.newSpec = newSpec;
  }

  /**
   * Creates an empty speciation event (should eventually be removed).
   */
  public SpeciationEvent() {
    super(EventType.SPEC);
  }

  public SpeciesNode getExistingSpec() {
    return existingSpec;
  }

  public SpeciesNode getNewSpec() {
    return newSpec;
  }

  public void setExistingSpec(SpeciesNode existingSpec) {
    this.existingSpec = existingSpec;
  }

  public void setNewSpec(SpeciesNode newSpec) {
    this.newSpec = newSpec;
  }
}
