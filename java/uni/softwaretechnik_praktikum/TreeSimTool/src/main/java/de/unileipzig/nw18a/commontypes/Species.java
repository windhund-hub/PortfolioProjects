package de.unileipzig.nw18a.commontypes;

import java.util.ArrayList;

public class Species implements TripletEntry {

  private String name;
  private ArrayList<Gene> genes;

  /*Constructor without arguments.
   */
  public Species() {
    this.genes = new ArrayList<Gene>();
  }

  /*Constructor sets name.
   */
  public Species(String name) {
    this.name = name;
    this.genes = new ArrayList<Gene>();
  }

  /*Method for setting name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /*Method for getting name.
   */
  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return name;
  }
  
  /*Method to add one gene to the end of the list of genes.
   */
  public void addGene(Gene gene) {
    this.genes.add(gene);
  }

  /*Method to get one gene of the list of genes at an certain index.
   */
  public Gene getGene(int index) {
    return this.genes.get(index);
  }

  /*Method to get the list of genes.
   */
  public ArrayList<Gene> getGenes() {
    return this.genes;
  }

  @Override
  public int compareTo(TripletEntry otherSpecies) {
    if (otherSpecies == null) {
      throw new NullPointerException("Cannot compare null!");
    }
    if (getClass() != otherSpecies.getClass()) {
      throw new IllegalArgumentException("Cannot compare different Types of TripletEntry: Species "
          + "with " + otherSpecies.getClass().getSimpleName());
    }

    return name.compareTo(((Species)otherSpecies).name);
  }
}
