package de.unileipzig.nw18a.nexbuild.buildutils;

import de.unileipzig.nw18a.commontypes.TripletEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Node {

  private Node parent;
  private List<Node> children;
  private TripletEntry taxon;
  private String nodeName;

  /**
   * Default constructor.
   * 
   * @param name Description of node
   */
  public Node(String name) {
    this.children = new ArrayList<Node>();
    this.nodeName = name;
  }

  /**
   * Constructor for leaves.
   * 
   * @param name Description of node
   * @param leafTaxon Taxon of the leaf
   */
  public Node(String name, TripletEntry leafTaxon) {
    this(name);
    this.taxon = leafTaxon;
  }

  /**
   * Constructor with children (for root, inner nodes).
   * 
   * @param name Description of Node
   * @param children Children of node
   */
  public Node(String name, Collection<Node> children) {
    this(name);
    this.addChildren(children);
  }
  
  /**
   * Converts Nodes into Newick-Format.
   *
   * @param node Node which will be converted.
   * @return output Node as Newick-String.
   */
  public String toNewick(Node node) {
    
    String output = ""; 
    
    if (!node.children.isEmpty()) {
      output += "(";
      for (Iterator<Node> it = node.children.iterator();it.hasNext();) {
        Node child = (Node) it.next();
        output += toNewick(child);
        if (it.hasNext()) {
          output += ",";
        }
      }
      output += ")";
    }
    output += node.getNodeName();
    return output;
  }

  public Node getParent() {
    return parent;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  public List<Node> getChildren() {
    return this.children;
  }

  public void addChild(Node child) {
    children.add(child);
    child.setParent(this);
  }

  /**
   * Adds all Nodes of given Collection to the list of children.
   *
   * @param childrenCollection Collection of Nodes to be added as children.
   */
  public void addChildren(Collection<Node> childrenCollection) {
    this.children.addAll(childrenCollection);
    for (Node child : childrenCollection) {
      child.setParent(this);
    }
  }

  public String getNodeName() {
    return nodeName;
  }

  public void setNodeName(String treeName) {
    this.nodeName = treeName;
  }

  public TripletEntry getTaxon() {
    return taxon;
  }

  public void setTaxon(TripletEntry taxon) {
    this.taxon = taxon;
  }

  @Override
  public boolean equals(Object other) {
    if ((other == null) || (other.getClass() != this.getClass())) {
      return false;
    } else {
      Node otherNode = (Node) other;
      if (otherNode.getTaxon() == null || this.getTaxon() == null) {
        return false;
      } else {
        return this.getTaxon().equals(otherNode.getTaxon());
      }
    }
  }
}
