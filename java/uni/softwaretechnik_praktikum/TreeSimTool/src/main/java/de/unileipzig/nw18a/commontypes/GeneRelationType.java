package de.unileipzig.nw18a.commontypes;

public enum GeneRelationType {

  RCB, LCA, FITCH;

  //rcb
  public static final byte LESS_THAN = -1;
  public static final byte EQUAL = 0;
  public static final byte GREATER_THAN = 1;
  //homology
  public static final byte LEAF = 0;
  public static final byte ORTHO = 1;
  public static final byte PARA = 2;
  public static final byte XENO = 3;
  public static final byte XENO_LCA = 4;
}
