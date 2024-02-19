package de.unileipzig.nw18a.alfextract.treeutils;

import de.unileipzig.nw18a.alfextract.treemapping.AbstractNode;
import de.unileipzig.nw18a.alfextract.treemapping.EventType;
import de.unileipzig.nw18a.alfextract.treemapping.GeneNode;
import de.unileipzig.nw18a.alfextract.treemapping.SpeciesNode;
import de.unileipzig.nw18a.commontypes.Gene;
import de.unileipzig.nw18a.commontypes.GeneDistances;
import de.unileipzig.nw18a.commontypes.GeneRelation;
import de.unileipzig.nw18a.commontypes.GeneRelationType;
import de.unileipzig.nw18a.commontypes.Matrix.MatrixFormat;
import de.unileipzig.nw18a.commontypes.Species;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



/**
 * Class provides several static methods which are used to determine tree artefacts.
 */
public class TreeUtils {

  private TreeUtils() {

  }

  /**
   * For a tree with nodes of an equal subclass of AbstractNode.
   * Method calculates iteratively the last common ancestor of two nodes by using the
   * given attribute DistanceToRoot.
   *
   * @param node1 from the same subclass of AbstractNode as node2
   * @param node2 from the same subclass of AbstractNode as node1
   * @return last common ancestor (the node that is part of the path between root and node1 and
   of the path between root and node2 and has biggest distance to the root), AbstractNode has to
   be casted to subclass of the input parameters
   * @throws IllegalTreeStateException if an illegal tree state appears
   */
  public static AbstractNode getLastCommonAncestor(AbstractNode node1, AbstractNode node2)
      throws IllegalTreeStateException, RuntimeException {
    //Exception if parameters are from different subclasses of AbstractNode
    if (!node1.getClass().equals(node2.getClass())) {
      throw new IllegalArgumentException("Node1 and Node2 are not from the same subclass of "
          + "AbstractNode");
    }
    //initialize local variable n1 with node1
    AbstractNode n1 = node1;
    //initialize local variable n2 with node2
    AbstractNode n2 = node2;
    //declare local doubles to ensure by exception there is no circle in tree
    double dist1;
    double dist2;

    //Search for common node. If lca is the root for safety reasons we iterate until both nodes
    // are root nodes (have no parents).
    while (n1.getParent() != null || n2.getParent() != null) {
      dist1 = n1.getDistanceToRoot();
      dist2 = n2.getDistanceToRoot();
      //Case nodes are equal
      if (n1 == n2) {
        return n1;
      } else {
        //In case nodes are not equal, go upwards in the tree. Choose parent of n1 if it has
        //bigger distanceToRoot, else n2.
        if (dist1 > dist2) {
          n1 = n1.getParent();
        } else {
          n2 = n2.getParent();
        }
      }
      //exception to make sure we go out of the loop if there is a circle in the tree by mistake
      if (dist2 < n2.getDistanceToRoot() || dist1 < n1.getDistanceToRoot()) {
        throw new
            IllegalTreeStateException("Circle in tree");
      }
    }
    if (n1 != n2) {
      throw new IllegalTreeStateException("Tree is not connected: Two different root nodes "
          + "received");
    }
    return n1;

  }

  /**
   * The method calculates RCB-Relations for all n gene trees given by the root node of the species
   * tree and saves the leaves of every gene tree in order from left to right as list of Genes.
   *
   * @param tree of SpeciesNodes given by the root node of the species tree
   * @return ArrayList with n geneRelations which own an ArrayList of Genes and a matrix with
   the calculated RCB-relations. Because the RCB-relation is symmetric and not reflexive the
   result is a right upper triangular matrix with one dimension less.
   Caution: null is not written in the rows so that the length of initialized components of the
   Array decreases and columns are shifted in every loop.
   * @throws IllegalTreeStateException if an illegal tree state appears
   */
  public static List<GeneRelation> buildReconciliationBased(SpeciesNode tree)
      throws IllegalTreeStateException, RuntimeException {

    List<GeneRelation> listOfGeneRelations =
        new ArrayList<GeneRelation>();
    //To create a Gene out of a GeneNode later species are preserved in a tree.
    Map<String, Species> speciesTreeMap = new TreeMap<String, Species>();

    //loop over GeneNodes given in SpeciesNode tree
    for (Map.Entry<Integer, GeneNode> geneRootEntry : tree.getGenes().entrySet()) {
      GeneRelation geneRelation =
          new GeneRelation(GeneRelationType.RCB, MatrixFormat.UPPER,
              String.format("G%d", geneRootEntry.getKey()));
      listOfGeneRelations.add(geneRelation);
      Iterator<AbstractNode> leafIt = geneRootEntry.getValue().leafIterator();
      //For every leaf in the gene tree:
      while (leafIt.hasNext()) {
        //Building an ArrayList which saves the RCBrelations the leaf has with all leaves on its
        //right.
        GeneNode leaf = (GeneNode) leafIt.next();
        ArrayList<Byte> row = new ArrayList<Byte>();
        geneRelation.getRelation().add(row);

        // diagonal elements i.e. self-loops are allways in the EQUAL-relation
        row.add(GeneRelationType.EQUAL);

        //Adding the leaf as Gene to the list GeneRelation.genes.
        if (!(speciesTreeMap.containsKey(leaf.getSpeciesNode().toString()))) {
          speciesTreeMap.put(leaf.getSpeciesNode().toString(),
              new Species(leaf.getSpeciesNode().toString()));
        }
        geneRelation.getGenes().add(new Gene(leaf.toString(),
            speciesTreeMap.get(leaf.getSpeciesNode().toString())));

        //determine RCB relations
        GeneNode child = leaf;
        while (child != geneRootEntry.getValue()) {

          GeneNode parent = (GeneNode) child.getParent();
          //If "child" was leftChild of "parent" and "parent" has right child we traverse the branch
          //on the right side of "parent".
          if ((parent.getLeftChild()) == child && parent.getRightChild() != null) {
            Iterator<AbstractNode> branchIt = parent.getRightChild().leafIterator();

            while (branchIt.hasNext()) {
              GeneNode leafAtBranch = (GeneNode) branchIt.next();
              //For every leaf on the branch "parent" is the lca of the GeneNodes "leafAtBranch" and
              //"leaf". We determine the lca of the speciesNodes of "leafAtBranch" and "leaf".
              SpeciesNode speciesLca = (SpeciesNode) getLastCommonAncestor(leaf.getSpeciesNode(),
                  leafAtBranch.getSpeciesNode());
              //To determine the RCB-relation between "leafAtBranch" and "leaf" we check if the
              //event at "parent" is a speciation and if the SpeciesNode of "parent" is equal
              //with "speciesLca".
              if (parent.getEvent().getType() == EventType.SPEC
                  && speciesLca == parent.getSpeciesNode()) {
                row.add(GeneRelationType.EQUAL);
              } else {
                //Otherwise we compare the distance of speciesLCA to the root in the
                //species tree with the distance of "parent" to "root" in the gene tree.
                if (speciesLca.getDistanceToRoot() > parent.getDistanceToRoot()) {
                  row.add(GeneRelationType.GREATER_THAN);
                }
                if (speciesLca.getDistanceToRoot() < parent.getDistanceToRoot()) {
                  row.add(GeneRelationType.LESS_THAN);
                }
              }
            }
          }
          child = parent;
        }
      }
    }
    return listOfGeneRelations;
  }

  /**
   * The method calculates LCA-Relations for all n gene trees given by the root node of the species
   * tree and saves the leaves of every gene tree in order from left to right as Genes.
   *
   * @param tree of SpeciesNodes given by the rootnode of the speciestree
   * @return ArrayList with n geneRelations which own an ArrayList of Genes and a matrix with
   the calculated LCA-relations
   * @throws IllegalTreeStateException if an illegal tree state appears
   */
  public static ArrayList<GeneRelation> buildLastCommonAncestor(SpeciesNode tree)
      throws IllegalTreeStateException, RuntimeException {

    List<GeneRelation> listOfGeneRelations =
        new ArrayList<GeneRelation>();
    //To create a Gene out of a GeneNode later, species are preserved in a tree.
    Map<String, Species> speciesTreeMap = new TreeMap<String, Species>();

    //loop over GeneNodes given in SpeciesNode tree
    for (Map.Entry<Integer, GeneNode> geneRootEntry : tree.getGenes().entrySet()) {
      GeneRelation geneRelation =
          new GeneRelation(GeneRelationType.LCA, MatrixFormat.UPPER,
              String.format("G%d", geneRootEntry.getKey()));
      listOfGeneRelations.add(geneRelation);
      Iterator<AbstractNode> leafIt = geneRootEntry.getValue().leafIterator();

      //For every leaf:
      while (leafIt.hasNext()) {
        //Build an ArrayList which saves the LCArelations the leaf has with all leaves on its right.
        GeneNode leaf = (GeneNode) leafIt.next();
        ArrayList<Byte> row = new ArrayList<Byte>();
        geneRelation.getRelation().add(row);

        // diagonal elements i.e. self-loops are marked as LEAF
        row.add(GeneRelationType.LEAF);

        //Adding the leaf as Gene to the list GeneRelation.genes.
        if (!(speciesTreeMap.containsKey(leaf.getSpeciesNode().toString()))) {
          speciesTreeMap.put(leaf.getSpeciesNode().toString(),
              new Species(leaf.getSpeciesNode().toString()));
        }
        geneRelation.getGenes().add(new Gene(leaf.toString(),
            speciesTreeMap.get(leaf.getSpeciesNode().toString())));

        //determine LCArelations
        GeneNode child = leaf;
        while (child != geneRootEntry.getValue()) {

          GeneNode parent = (GeneNode) child.getParent();
          //If "child" was leftChild of "parent" and "parent" has right child we traverse the branch
          // on the right side of "parent".
          if ((parent.getLeftChild()) == child && parent.getRightChild() != null) {
            //For every leave on the branch "parent" is the lca with "leaf". By getting the event
            // of "parent", we get the LCArelation between "leafAtBranch" and "leaf".
            Iterator<AbstractNode> branchIt = parent.getRightChild().leafIterator();
            if (parent.getEvent().getType() == EventType.SPEC) {
              while (branchIt.hasNext()) {
                branchIt.next();
                row.add(GeneRelationType.ORTHO);
              }
            } else if (parent.getEvent().getType() == EventType.DUP) {
              while (branchIt.hasNext()) {
                branchIt.next();
                row.add(GeneRelationType.PARA);
              }
            } else {
              if (parent.getEvent().getType() == EventType.LGT) {
                while (branchIt.hasNext()) {
                  branchIt.next();
                  row.add(GeneRelationType.XENO);
                }
              }
            }
          }
          child = parent;
        }
      }
    }
    return (ArrayList<GeneRelation>) listOfGeneRelations;
  }

  /**
   * This method computes Fitch Relations for all n gene trees given by the root node of the
   * species tree and saves the leaves of every gene tree in order from left to right as Genes.
   *
   * @param tree of SpeciesNodes given by the root node of the species tree
   * @return ArrayList with n geneRelations which own an ArrayList of Genes and a matrix with
   the calculated Fitch Relations.
   * @throws IllegalTreeStateException if an illegal tree state appears
   */
  public static List<GeneRelation> buildFitch(SpeciesNode tree)
      throws IllegalTreeStateException, RuntimeException {

    Iterator<GeneNode> iteratorRootsOfGeneTrees = tree.getGenes().values().iterator();
    List<GeneRelation> listOfGeneRelations =
        new ArrayList<GeneRelation>();
    //To create a Gene out of a GeneNode later, species are preserved in a tree.
    Map<String, Species> speciesTreeMap = new TreeMap<String, Species>();

    //Loop over GeneNodes given in SpeciesNode tree.
    while (iteratorRootsOfGeneTrees.hasNext()) {
      GeneNode geneTreeRoot = iteratorRootsOfGeneTrees.next();
      GeneRelation geneRelation
          = new GeneRelation(GeneRelationType.FITCH, MatrixFormat.BOTH, geneTreeRoot.toString());
      listOfGeneRelations.add(geneRelation);
      geneRelation.getRelation().add(new ArrayList<Byte>());
      Iterator<AbstractNode> leafIt = geneTreeRoot.leafIterator();
      int counterRow = -1;
      //For every leaf:
      while (leafIt.hasNext()) {
        //Building an ArrayList that contains the Fitch Relations of this leaf to each
        //distinct leaf on its right.
        GeneNode leaf = (GeneNode) leafIt.next();
        counterRow++;
        geneRelation.getRelation().get(counterRow).add(GeneRelationType.LEAF);
        int counterColumn = counterRow;

        //Adding the leaf as Gene to the list GeneRelation.genes.
        String speciesName;
        if (!(speciesTreeMap.containsKey(speciesName = leaf.getSpeciesNode().toString()))) {
          speciesTreeMap.put(speciesName, new Species(speciesName));
        }
        geneRelation.getGenes().add(new Gene(leaf.toString(),
            speciesTreeMap.get(speciesName)));
        //Flag indicates if an LGT happened on the way from leaf to root.
        boolean leafHasLgtOnWayToRoot = false;

        //determine Fitch Relations
        GeneNode child = leaf;
        //For each node from "leaf" to "geneTreeRoot":
        while (child != geneTreeRoot) {
          GeneNode parent = (GeneNode) child.getParent();
          //While going upwards to the root, every inner node "parent" is checked, whether an LGT
          // event occurred with "child" as acceptor gene.
          if (parent.getEvent().getType() == EventType.LGT && child.getSpeciesNode()
              != parent.getSpeciesNode()) {
            //If "parent" is labeled with GeneRelationType LGT and the species of the nodes "child"
            // and "parent" differ, then "child" is the acceptor gene of the donor gene "parent".
            leafHasLgtOnWayToRoot = true;
          }
          //If "child" is leftChild of "parent", traverse the branch to the right side of "parent".
          if (parent.getLeftChild() == child && parent.getRightChild() != null) {

            Iterator<AbstractNode> branchIt = parent.getRightChild().leafIterator();
            while (branchIt.hasNext()) {
              GeneNode leafOnBranch = (GeneNode) branchIt.next();
              counterColumn++;
              //Condition determines that only in the first iteration an ArrayList is created for
              //every visited leaf.
              if (counterRow == 0) {
                geneRelation.getRelation().add(new ArrayList<Byte>());
              }
              //While going upwards from "leafOnBranch" to the node "parent", every inner node
              // "branchParent" is checked, whether an LGT event occurred with "branchChild" as
              // acceptor gene.
              GeneNode branchChild = leafOnBranch;
              boolean leafOnBranchHasLgtOnWayToParent = false;
              while (branchChild != parent) {
                GeneNode branchParent = (GeneNode) branchChild.getParent();
                if (branchParent.getEvent().getType() == EventType.LGT
                    && branchChild.getSpeciesNode() != branchParent.getSpeciesNode()) {
                  //If "branchParent" is labeled with GeneRelationType LGT and the species of the
                  // nodes "branchChild" and "branchParent" differ, then "branchChild" is the
                  // acceptor gene of the donor gene "branchParent".
                  leafOnBranchHasLgtOnWayToParent = true;
                  break;
                }
                branchChild = branchParent;
              }
              //Set homology events.
              if (leafHasLgtOnWayToRoot) {
                if (leafOnBranchHasLgtOnWayToParent) {
                  //Case that on both branches happens LGT.
                  geneRelation.getRelation().get(counterRow).add(GeneRelationType.XENO);
                  geneRelation.getRelation().get(counterColumn).add(GeneRelationType.XENO);
                } else {
                  //Case that on the way from leaf to parent (left branch) happens LGT but not on
                  // the way from "leafOnBranch" to "parent" (right branch).
                  setRelation(parent.getEvent().getType(), geneRelation.getRelation().get(
                      counterRow), geneRelation.getRelation().get(counterColumn), parent);
                }
              } else {
                if (leafOnBranchHasLgtOnWayToParent) {
                  //Case that on the way from "leafOnBranch" to "parent" (right branch) happens LGT
                  // but not on the way from "leaf" to "parent" (left branch).
                  setRelation(parent.getEvent().getType(), geneRelation.getRelation().get(
                      counterColumn), geneRelation.getRelation().get(counterRow), parent);
                } else {
                  //Case that on both branches no LGT happens.
                  switch (parent.getEvent().getType()) {
                    case SPEC:
                      geneRelation.getRelation().get(counterRow).add(GeneRelationType.ORTHO);
                      geneRelation.getRelation().get(counterColumn).add(GeneRelationType.ORTHO);
                      break;
                    case DUP:
                      geneRelation.getRelation().get(counterRow).add(GeneRelationType.PARA);
                      geneRelation.getRelation().get(counterColumn).add(GeneRelationType.PARA);
                      break;
                    default:
                      throw new IllegalTreeStateException("Unlabeled node " + parent.toString());
                  }
                }
              }
            }
          }
          child = parent;
        }
      }
    }
    return listOfGeneRelations;
  }

  /**
   * The method is used in the method buildFitch. It sets the Fitch relations in the case that one
   * relation is xenolog.
   *
   * @param eventType          event type (speciation, duplication or LGT) of the regarded lca
   *                           gene node
   * @param listOtherEventType list with Fitch relations for the gene where the LGT is not happening
   * @param listXeno           list with Fitch relations for the gene where the LGT is happening
   * @param parent             the lca of the regarded gene nodes, only used to give a detailed
   *                           failure report
   * @throws IllegalTreeStateException reports unlabeled inner GeneNode
   */
  private static void setRelation(EventType eventType, List<Byte> listOtherEventType,
                              List<Byte> listXeno, GeneNode parent)
      throws IllegalTreeStateException {

    //switch in dependence of the event type (speciation, duplikation or LGT) of the regarded lca
    // gene node
    switch (eventType) {
      case SPEC:
        //On the way from one gene to the acceptor gene the Fitch relation is
        // "xenologic", in the other direction the Fitch relation is "orthologous".
        listOtherEventType.add(GeneRelationType.ORTHO);
        listXeno.add(GeneRelationType.XENO);
        break;
      case DUP:
        //On the way from one gene to the acceptor gene the Fitch relation is
        // "xenologic", in the other direction the Fitch relation is "paralogous".
        listOtherEventType.add(GeneRelationType.PARA);
        listXeno.add(GeneRelationType.XENO);
        break;
      case LGT:
        //On the way from one gene to the acceptor gene the Fitch relation is
        // "xenologic". In the other direction no LGT happens but the lca node is labeled with
        // the event LGT. To specify this special case a fourth constant "XENO_LCA" is
        // introduced.
        listOtherEventType.add(GeneRelationType.XENO_LCA);
        listXeno.add(GeneRelationType.XENO);
        break;
      default:
        throw new IllegalTreeStateException("Unlabeled node " + parent.toString()
            + " in buildFitch!");
    }
  }

  /**
   * For every gene tree contained in the obtained species tree this method calculates the distances
   * between all leaves.
   *
   * @param tree          species tree given by its root node
   * @param isUltraMetric true if tree is ultra metric (distances from the leaves to the root of the
   *                      tree are equal), false if tree is not ultra metric (distances can differ)
   * @return              list of the GeneDistances that we obtain for every gene tree
   */
  public static List<GeneDistances> getDistances(SpeciesNode tree, boolean isUltraMetric) {
    List<GeneDistances> listOfGeneDistances = new ArrayList<GeneDistances>();
    //To create a Gene out of a GeneNode later, species are preserved in a tree.
    Map<String, Species> speciesTreeMap = new TreeMap<String, Species>();

    //loop over GeneNodes given in SpeciesNode tree
    for (Map.Entry<Integer, GeneNode> geneRootEntry : tree.getGenes().entrySet()) {
      GeneDistances geneDistances = new GeneDistances(MatrixFormat.UPPER, String.format("G%d",
          geneRootEntry.getKey()));
      listOfGeneDistances.add(geneDistances);
      Iterator<AbstractNode> leafIt = geneRootEntry.getValue().leafIterator();

      //For every leaf:
      while (leafIt.hasNext()) {
        //Build an ArrayList which saves the distances the leaf has with all leaves on its left.
        GeneNode leaf = (GeneNode) leafIt.next();
        double leafDistToRoot = leaf.getDistanceToRoot();
        List<Double> row = new ArrayList<Double>();
        geneDistances.getDistances().add((ArrayList<Double>) row);

        // diagonal elements i.e. distance of node to itself is zero
        row.add((double) 0);
        //Adding the leaf as Gene to the list GeneRelation.genes.
        String speciesName;
        if (!(speciesTreeMap.containsKey(speciesName = leaf.getSpeciesNode().toString()))) {
          speciesTreeMap.put(leaf.getSpeciesNode().toString(),
              new Species(speciesName));
        }
        geneDistances.getGenes().add(new Gene(leaf.toString(),
            speciesTreeMap.get(speciesName)));

        //determine distances
        GeneNode child = leaf;
        while (child != geneRootEntry.getValue()) {

          GeneNode parent = (GeneNode) child.getParent();
          //If "child" was leftChild of "parent" and "parent" has right child we traverse the branch
          // on the right side of "parent".
          if ((parent.getLeftChild()) == child && parent.getRightChild() != null) {
            Iterator<AbstractNode> branchIt = parent.getRightChild().leafIterator();
            //The lca of each "leafOnBranch" and "leaf" is "parent". Distances are given by the
            //calculation (leaf.DistToRoot - parent.DistToRoot) + (leafOnBranch.DistToRoot -
            // parent.DistToRoot)
            if (!isUltraMetric) {
              //case that tree is not ultra metric or has unknown type
              double interimResult = leafDistToRoot - parent.getDistanceToRoot() * 2;
              while (branchIt.hasNext()) {
                row.add(interimResult + branchIt.next().getDistanceToRoot());
              }
            } else {
              //In case the tree is ultra metric distances are given by the less expensive
              // calculation:
              double distance = 2 * (leafDistToRoot - parent.getDistanceToRoot());
              while (branchIt.hasNext()) {
                branchIt.next();
                row.add(distance);
              }
            }
          }
          child = parent;
        }
      }
    }
    return listOfGeneDistances;
  }

  /**
   * This method calls the method getDistances with default value false for the treeType in case
   * the tree is not ultra metric or has unknown type.
   *
   * @param tree species tree given by its root node
   * @return     list of the GeneDistances that we obtain for every gene tree
   */
  public static List<GeneDistances> getDistances(SpeciesNode tree) {
    return getDistances(tree, false);
  }

}
