package de.unileipzig.nw18a.commontypes;

public class Gene implements TripletEntry {

  private String name;
  private Species species;

  /**
   * Constructor sets given values name and species and adds Gene to list of genes of species.
   * @param name Name of gene
   * @param species Species of the gene
   */
  public Gene(String name, Species species) {

    this.name = name;
    this.species = species;
    species.addGene(this);
  }

  public String getName() {
    return this.name;
  }

  public Species getSpecies() {
    return this.species;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int compareTo(TripletEntry otherGene) {
    if (otherGene == null) {
      throw new NullPointerException("Cannot compare null!");
    }
    if (getClass() != otherGene.getClass()) {
      throw new IllegalArgumentException("Cannot compare different Types of TripletEntry: Gene "
          + "with " + otherGene.getClass().getSimpleName());
    }
    Gene other = (Gene)otherGene;

    int specComp = species.compareTo(other.getSpecies());
    int geneComp = name.compareTo(other.name);

    // first sort by species with its own compareTo
    if (specComp != 0) {
      return specComp;
    }
    // then by genes
    return geneComp;
  }
}
