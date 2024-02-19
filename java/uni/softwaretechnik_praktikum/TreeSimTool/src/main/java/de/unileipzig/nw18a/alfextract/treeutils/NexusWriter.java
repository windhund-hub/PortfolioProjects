package de.unileipzig.nw18a.alfextract.treeutils;

import de.unileipzig.nw18a.alfextract.treemapping.AbstractNode;
import de.unileipzig.nw18a.alfextract.treemapping.EventType;
import de.unileipzig.nw18a.alfextract.treemapping.GeneNode;
import de.unileipzig.nw18a.alfextract.treemapping.SpeciesNode;
import de.unileipzig.nw18a.commontypes.BasicNexusWriter;
import de.unileipzig.nw18a.commontypes.GeneDistances;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;


public class NexusWriter extends BasicNexusWriter {

  /**
   * Constructs a writer to a given output file.
   * @param  filepath    Path to file in which to write
   * @param  append      Whether or not to append to an existing file or override.
   * @throws IOException Error when opening the file stream.
   */
  public NexusWriter(String filepath, boolean append) throws IOException {
    super(filepath, append);
  }

  /**
   * Constructs a writer to a given output stream.
   * @param  stream      Stream in which to write
   * @param  append      Whether or not to start a new nexus file by printing '#NEXUS' at the
   *                     beginning
   */
  public NexusWriter(OutputStream stream, boolean append) {
    super(stream, append);
  }

  /**
   * Writes out species into a TAXA block.
   * @param  tree Root of the species tree.
   */
  private void writeOutSpeciesTaxa(SpeciesNode tree) {
    writer.write("BEGIN TAXA;");
    writer.println();
    writer.println("[further used species listed by name]");
    ArrayList<String> speciesList = new ArrayList<String>();
    Iterator<AbstractNode> iterator = tree.leafIterator();
    while (iterator.hasNext()) {
      speciesList.add(iterator.next().toString());
    }
    writer.println(String.format("\tDIMENSIONS NTAX = %d;", speciesList.size()));
    writer.println("\tTAXLABELS");
    for (String species : speciesList) {
      writer.println(String.format("\t\t%s", species));
    }
    writer.println("\t\t;");
    writer.println("END;");
    writer.println();
  }

  /**
   * Writes out all trees (species and gene) into a TREES block.
   * @param  speciesTree Root of the species tree.
   */
  public void writeOutAllTrees(SpeciesNode speciesTree) {
    writeOutSpeciesTaxa(speciesTree);
    writer.println("BEGIN TREES;");
    writer.println("[S:species trees and G:gene trees in Newick format]");
    writeOutTree(speciesTree, "S");
    for (Map.Entry<Integer, GeneNode> entry : speciesTree.getGenes().entrySet()) {
      writeOutTree(entry.getValue(), String.format("G%04d", entry.getKey()));
    }
    writer.println("END;");
    writer.println();
  }

  /**
   * Here a single species trees is written out.
   * @param tree the tree
   * @param name the name (like S001)
   */
  public void writeOutTree(SpeciesNode tree, String name) {
    writer.println(String.format("\tTREE %s = %s;", name,
        treeToString(tree).replace("_$", "")));
  }

  /**
   * Here a single gene tree is written out.
   * @param tree the tree
   * @param name the name (like G001)
   */
  public void writeOutTree(GeneNode tree, String name) {
    writer.println(String.format("\tTREE %s = %s;", name,
        treeToString(tree).replace("$", name)));
  }

  /**
   * This is a recursive method to make a tree into a string.
   * @param tree the root of the part tree
   * @return the finished output string
   */
  public static String treeToString(AbstractNode tree) {
    String output = "";
    if (tree.getLeftChild() != null && tree.getRightChild() != null) {
      String left = treeToString(tree.getLeftChild());
      String right = treeToString(tree.getRightChild());
      output = "(" + left + "," + right + ")";
    } else if (tree.getRightChild() == null && tree.getLeftChild() != null) {
      output = "(" + treeToString(tree.getLeftChild()) + ")";
    } else if (tree.getLeftChild() == null && tree.getRightChild() != null) {
      output = "(" + treeToString(tree.getRightChild()) + ")";
    }
    //the root has no distance to a parent.
    output += tree + (tree.isLeaf() ? "" : "_$")
        + String.format(Locale.ENGLISH,":%.6f", tree.getDistanceToParent());
    return output;
  }

  /**
   * Writes out a reconciliation block including mapping and events.
   * @param  speciesTree Root of the species tree.
   */
  public void writeOutReconciliation(SpeciesNode speciesTree) {
    writer.println("BEGIN RECONCILIATION;");
    writer.println("[reconcile the species with the gene trees to infer following events]");
    writeOutLabels(speciesTree);
    writeOutMapping(speciesTree);
    writer.println("END;");
    writer.println();
  }

  private void writeOutLabels(SpeciesNode speciesTree) {
    //add events to list
    ArrayList<TreeSet<String>> events = new ArrayList<TreeSet<String>>();
    events.add(new TreeSet<String>()); //SPEC
    events.add(new TreeSet<String>()); //DUP
    events.add(new TreeSet<String>()); //LGT
    List<EventType> typeList = Arrays.asList(EventType.SPEC, EventType.DUP, EventType.LGT);

    // speciation labels in species tree
    for (AbstractNode speciesNode : speciesTree) {
      if (!speciesNode.isLeaf()) {
        events.get(typeList.indexOf(EventType.SPEC)).add(speciesNode.toString());
      }
    }

    for (Map.Entry<Integer, GeneNode> entry : speciesTree.getGenes().entrySet()) {
      for (AbstractNode abstractNode : entry.getValue()) {
        GeneNode geneNode = (GeneNode) abstractNode;
        if (geneNode.getEvent() != null) {
          events.get(typeList.indexOf(geneNode.getEvent().getType())).add(
              String.format("%s_G%04d", geneNode.toString(), entry.getKey()));
        }
      }
    }

    //print out lists
    for (int i = 0; i < typeList.size(); i++) {
      writer.println(String.format("\t%s", typeList.get(i).toString()));
      switch (typeList.get(i)) {
        case DUP:
          writer.println("\t[gene duplication events]");
          break;
        case LGT:
          writer.println("\t[lateral gene transfer events]");
          break;
        case SPEC:
          writer.println("\t[species formation events]");
          break;
        default:
        // should not arise
      }

      for (String geneName : events.get(i)) {
        writer.println(String.format("\t\t%s", geneName));
      }
      writer.println("\t\t;");
    }
  }


  private void writeOutMapping(SpeciesNode speciesTree) {
    writer.println("\tMAPPING");
    writer.println("\t[mapping of nodes in gene tree to nodes in species tree]");
    writer.println("\t\t[origin tree, node name, mapped tree, mapped name; 0=to node, 1=to edge]");

    String leafPattern = "\t\tG%04d %s S %s %d";
    String innerPattern = "\t\tG%04d %s_G%04d S %s %d";

    for (Iterator<Map.Entry<Integer, GeneNode>> geneRootIterator
        = speciesTree.getGenes().entrySet().iterator(); geneRootIterator.hasNext();) {
      Map.Entry<Integer, GeneNode> geneRoot = geneRootIterator.next();
      AbstractNode abstractNode = geneRoot.getValue();
      for (Iterator<AbstractNode> iter = abstractNode.iterator(); iter.hasNext();) {
        GeneNode geneNode = (GeneNode) iter.next();
        if (geneNode.isLeaf()) {
          writer.print(String.format(
              leafPattern, geneRoot.getKey(), geneNode, geneNode.getSpeciesNode(),
              geneNode.getSpeciesNode().getEdgeEvents().contains(geneNode) ? 1 : 0
          ));
        } else {
          writer.print(String.format(
              innerPattern, geneRoot.getKey(), geneNode, geneRoot.getKey(),
              geneNode.getSpeciesNode(),
              geneNode.getSpeciesNode().getEdgeEvents().contains(geneNode) ? 1 : 0
          ));
        }
        if (iter.hasNext() || geneRootIterator.hasNext()) {
          writer.println(",");
        }
      }
    }
    writer.println();
    writer.println("\t\t;");
  }

  /**
   * Writes out a distances block.
   * @param  distancesList List with gene distances.
   */
  public void writeOutDistances(List<GeneDistances> distancesList) {
    writer.println("BEGIN ALLDISTANCES;");
    writer.println("[matrices with distances between two genes, one matrix per tree]");

    for (GeneDistances dist : distancesList) {
      writer.println("\tdistances");
      writer.println(String.format("\t\tname=%s triangle=%s",
          dist.getTreeName(), dist.getDistances().getFormat().name().toLowerCase()));

      for (int i = 0; i < dist.getDistances().size(); i++) {
        ArrayList<Double> line = dist.getDistances().get(i);
        String geneName;
        if (i < dist.getGenes().size()) {
          geneName = dist.getGenes().get(i).toString();
        } else {
          throw new IllegalStateException(String.format(
          "Distances and genes are not equally sized: distances: %d, genes: %d",
          dist.getDistances().size(), dist.getGenes().size()
          ));
        }
        writer.print(String.format("\t\t\t%s", geneName));
        for (Double r : line) {
          writer.write(String.format(Locale.ENGLISH, " %2f", r));
        }
        writer.println();
      }
      writer.println("\t\t\t;");
    }
    writer.println("END;");
    writer.println();
  }

}
