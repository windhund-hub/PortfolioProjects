package de.unileipzig.nw18a.commontypes;

import de.unileipzig.nw18a.commontypes.Matrix;
import de.unileipzig.nw18a.commontypes.Matrix.MatrixFormat;

import java.util.ArrayList;
import java.util.List;


public class GeneRelation {

  /**
   * The genes over which this relation is declared.
   */
  private List<Gene> genes;
  /**
   * Matrix holding the relation.
   */
  private Matrix<Byte> relation;
  /**
   * Type of this gene relation (e.g. RCB, LCA, FITCH).
   */
  private GeneRelationType type;
  /**
   * Name of the tree (that is its root) this relation represents.
   */
  private String treeName;

  /**
   * Constructor declares ArrayList genes and ArrayList relation and sets properties of the
   * relation.
   * @param type     Type of relation (e.g. RCB, LCA, FITCH)
   * @param format   Format of the matrix holding the relation (i.e. upper triangular, lower
   *                 triangular or full matrix)
   * @param treeName Name of the tree (that is its root) this relation represents.
   */
  public GeneRelation(GeneRelationType type, MatrixFormat format, String treeName) {

    this.genes = new ArrayList<Gene>();
    this.relation = new Matrix<Byte>(format);
    this.type = type;
    this.treeName = treeName;
  }

  /**
   * Getter.
   * @return ArrayList of Gene
   */
  public List<Gene> getGenes() {
    return this.genes;
  }

  /**
   * Getter.
   * @return ArrayList of ArrayList of a Byte
   */
  public Matrix<Byte> getRelation() {
    return this.relation;
  }

  public GeneRelationType getType() {
    return this.type;
  }

  /**
   * Returns the name of this GeneRelationType.
   * @return the gene relation name
   */
  public String getTypeName() {

    return type.toString();
  }



  public String getTreeName() {
    return treeName;
  }

  public void setTreeName(String treeName) {
    this.treeName = treeName;
  }



}
