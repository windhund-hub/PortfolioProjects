package de.unileipzig.nw18a.nexmatch.matchutils;

import de.unileipzig.nw18a.commontypes.Gene;
import de.unileipzig.nw18a.commontypes.GeneDistances;
import de.unileipzig.nw18a.commontypes.Species;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BestMatchGraph {

  private List<Gene[]> bestMatches;
  private List<Boolean> isInReciprocal;
  private String treeName;

  public BestMatchGraph(GeneDistances distances) {
    this(distances, false);
  }
  
  /**
   * <par>This constructor provides an algorithm to compute the best-matching pairs of all 
   * given genes.</par>
   * <par>A gene B in another species is a best match to gene A, 
   * if the distance between A and B is minimal compared to all other genes in the species of gene B
   * They are also a reciprocal best match, if gene A is best match of gene B and vice versa.
   * In that case, the <i>isInReciprocal</i> value for the respective pair's
   * index is set to true.</par>
   * Note, if <i>reciprocal</i> flag is set to <i>false</i>, 
   * the reciprocal best match graph will not be computed.
   * @param distances the distances from the genes
   * @param reciprocal should the reciprocal BMG be computed
   */
  public BestMatchGraph(GeneDistances distances, boolean reciprocal) {
    this.treeName = distances.getTreeName();
    bestMatches = new ArrayList<Gene[]>();
    //Iterating all given genes
    for (int indexGeneI = 0;indexGeneI < distances.getGenes().size();indexGeneI++) {
      //In these maps the best matches for geneI that have been found are stored.
      Map<Species, Double> bestmatchDistances = new HashMap<Species,Double>();
      Map<Species, Gene> bestmatchGenes = new HashMap<Species,Gene>();
      Gene geneI = distances.getGenes().get(indexGeneI);
      Species speciesI = geneI.getSpecies();
      //Iterating all other genes, to find best match for each species
      for (int indexGeneJ = 0;indexGeneJ < distances.getDistances().get(indexGeneI).size();
          indexGeneJ++) {
        Gene geneJ = distances.getGenes().get(indexGeneJ);
        Species speciesJ = geneJ.getSpecies();
        double distanceGeneJtoGeneI = distances.getDistances().get(indexGeneI).get(indexGeneJ);
        //If the species of geneJ is in the Map the algorithm looks if it's a better match
        if (bestmatchGenes.get(speciesJ) != null) {
          if (distanceGeneJtoGeneI < bestmatchDistances.get(speciesJ)) {
            bestmatchDistances.put(speciesJ, distanceGeneJtoGeneI);
            bestmatchGenes.put(speciesJ, geneJ);
          }
        //If the species isn't already in the Map the algorithm puts it in the Map,
        //if geneJ is in a different species than geneI
        } else if (!speciesJ.equals(speciesI)) {
          bestmatchDistances.put(speciesJ, distanceGeneJtoGeneI);
          bestmatchGenes.put(speciesJ, geneJ);
        }
      }
      //Here the best matches for geneI are added to the bestMatch ArrayList
      for (Map.Entry<Species, Gene> e : bestmatchGenes.entrySet()) {
        this.bestMatches.add(new Gene[] {geneI,e.getValue()});
      }
    }
    
    if (reciprocal) {
      calculateReciprocal();
    }
  }
  
  /**
   * Here the ArrayList isInReciprocal is calculated.
   * The arrayList is true at position x 
   * if the bestMatch from the bestMatchesArrayList is reciprocal at position x
   */
  public void calculateReciprocal() {
    isInReciprocal = new ArrayList<Boolean>();
    //Iterating over all bestmatches
    for (int i = 0;i < bestMatches.size();i++) {
      boolean found = false;
      //iterating over all other bestmatches
      for (int j = 0;j < bestMatches.size(); j++) {
        //checking, of the best Match at position i is reciprocal
        if (bestMatches.get(i)[0].equals(bestMatches.get(j)[1]) 
            && bestMatches.get(i)[1].equals(bestMatches.get(j)[0])) {
          found = true;
        }
      }
      isInReciprocal.add(found);
    }
  }

  /*public boolean isReciprocal() {
    return isInReciprocal != null;
  }*/

  public List<Gene[]> getBestMatches() {
    return bestMatches;
  }

  public List<Boolean> getIsInReciprocal() {
    return isInReciprocal;
  }

  public String getTreeName() {
    return treeName;
  }

  public void setTreeName(String treeName) {
    this.treeName = treeName;
  }
}
