package de.unileipzig.nw18a.alfextract.treeutils;

import de.unileipzig.nw18a.alfextract.treemapping.DuplicationEvent;
import de.unileipzig.nw18a.alfextract.treemapping.EventType;
import de.unileipzig.nw18a.alfextract.treemapping.GeneNode;
import de.unileipzig.nw18a.alfextract.treemapping.LateralGeneTransferEvent;
import de.unileipzig.nw18a.alfextract.treemapping.SpeciationEvent;
import de.unileipzig.nw18a.alfextract.treemapping.SpeciesNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogfileParser {

  private static final String START_PATTERN
      = "^first organism \\(artificial sequences\\): (\\d+) proteins with \\d+ aa \\(average\\)";
  private static final String SPEC_PATTERN
      = "^time (\\d+\\.\\d+): speciation event of organism (\\d+) to organism (\\d+)";
  private static final String DUP_PATTERN
      = "^time (\\d+\\.\\d+): gene duplication in organism (\\d+) with gene (\\d+), "
      + "now gene (\\d+)";
  private static final String LGT_PATTERN
      = "^time (\\d+\\.\\d+): lgt from organism (\\d+) with gene (\\d+) to organism (\\d+), "
      + "now gene (\\d+)";
  private static final String DEL_PATTERN
      = "^time (\\d+\\.\\d+): gene loss in organism (\\d+) with gene (\\d+)";
  private static final String DEL_ORTHO_REPL_PATTERN
      = "^(?:\\s)+orthologues replacement, gene (\\d+) in organism (\\d+) deleted";

  private LogfileParser() {

  }

  /**
   * Reads a given logfile from ALFsim and constructs a tree mapping of it.
   *
   * @param alfDirectory the path to the ALFsim directory
   * @return the root of the constructed species-tree
   * @throws IOException if some kind of I/O problems occur
   * @throws IllegalTreeStateException if an illegal tree state appears
   */
  public static SpeciesNode parseLogfile(String alfDirectory) throws IOException,
      IllegalTreeStateException {

    //create matcher for regex filtering
    Matcher startMatcher = Pattern.compile(START_PATTERN).matcher("");
    Matcher specMatcher = Pattern.compile(SPEC_PATTERN).matcher("");
    Matcher dupMatcher = Pattern.compile(DUP_PATTERN).matcher("");
    Matcher lgtMatcher = Pattern.compile(LGT_PATTERN).matcher("");
    Matcher delMatcher = Pattern.compile(DEL_PATTERN).matcher("");
    Matcher delOrthoReplMatcher = Pattern.compile(DEL_ORTHO_REPL_PATTERN).matcher("");

    //create tree data structure
    boolean isStarted = false;
    SpeciesNode root = new SpeciesNode(1);
    List<SpeciesNode> leafs = new ArrayList<SpeciesNode>();
    leafs.add(root);

    //start counters for events
    int specCounter = -1;
    int dupCounter = 0;
    int lgtCounter = 0;

    //file input
    File logFile = new File(alfDirectory + File.separator + "logfile.txt");
    FileReader fileReader = null;
    BufferedReader bufferedReader = null;
    try {
      //open file stream
      fileReader = new FileReader(logFile);
      bufferedReader = new BufferedReader(fileReader);

      //read line by line
      String line = bufferedReader.readLine();
      while (line != null) {
        //match patterns
        startMatcher.reset(line);
        specMatcher.reset(line);
        dupMatcher.reset(line);
        lgtMatcher.reset(line);
        delMatcher.reset(line);
        delOrthoReplMatcher.reset(line);
        if (!isStarted && startMatcher.matches()) {
          //is start of file
          isStarted = true;
          int geneCount = Integer.parseInt(startMatcher.group(1));

          //add starting genes to root
          for (int i = 1; i <= geneCount; i++) {
            GeneNode geneNode = new GeneNode(i);
            geneNode.setSpeciesNode(root);
            root.addGene(geneNode);
          }

        } else {

          if (specMatcher.matches()) {
            //is speciation
            double time = Double.parseDouble(specMatcher.group(1));
            int originOrgansim = Integer.parseInt(specMatcher.group(2));
            int destOrgansim = Integer.parseInt(specMatcher.group(3));
            //perform speciation
            SpeciesNode newSpecies = performSpeciation(
                leafs.get(originOrgansim - 1), destOrgansim, specCounter, time);
            leafs.add(newSpecies);
            //increase event counter
            specCounter += newSpecies.getGenes().size() + 1;

          } else if (dupMatcher.matches()) {
            //is duplication
            double time = Double.parseDouble(dupMatcher.group(1));
            int organism = Integer.parseInt(dupMatcher.group(2));
            int oldGene = Integer.parseInt(dupMatcher.group(3));
            int newGene = Integer.parseInt(dupMatcher.group(4));
            //perform duplication
            performDuplication(leafs.get(organism - 1).getGenes().get(oldGene), newGene,
                dupCounter, time);
            dupCounter++;

          } else if (lgtMatcher.matches()) {
            //is lateral gene transfer
            double time = Double.parseDouble(lgtMatcher.group(1));
            int originOrganism = Integer.parseInt(lgtMatcher.group(2));
            int originGene = Integer.parseInt(lgtMatcher.group(3));
            int destOrganism = Integer.parseInt(lgtMatcher.group(4));
            int destGene = Integer.parseInt(lgtMatcher.group(5));
            //perform lgt
            performLateralGeneTransfer(
                leafs.get(originOrganism - 1).getGenes().get(originGene),
                leafs.get(destOrganism - 1), destGene, lgtCounter, time
            );
            lgtCounter++;

          } else if (delMatcher.matches()) {
            //is deletion
            int organism = Integer.parseInt(delMatcher.group(2));
            int gene = Integer.parseInt(delMatcher.group(3));
            //perform deletion
            performGeneLoss(leafs.get(organism - 1).getGenes().get(gene));

          } else if (delOrthoReplMatcher.matches()) {
            // is orthologues replacement
            int organism = Integer.parseInt(delOrthoReplMatcher.group(2));
            int gene = Integer.parseInt(delOrthoReplMatcher.group(1));
            //perform deletion
            performGeneLoss(leafs.get(organism - 1).getGenes().get(gene));
          }
        }

        line = bufferedReader.readLine();
      }

      //get leaf distances
      getLeafDistancesFromDarwin(leafs, alfDirectory);

    } catch (IllegalTreeStateException itse) {
      throw itse;
    } catch (FileNotFoundException e) {
      throw e;
    } catch (IOException e) {
      throw e;
    } finally {
      //close all streams
      if (bufferedReader != null) {
        bufferedReader.close();
      }
      if (fileReader != null) {
        fileReader.close();
      }
    }

    while (root.getParent() != null) {
      root = (SpeciesNode)root.getParent();
    }
    return root;
  }

  /**
   * Performs a speciation event on the given node in the species tree and returns the newly created
   * species node.
   *
   * @param speciesNode     Node in species tree that is subjected to a speciation event
   * @param newSpeciesId    Id for the newly created species
   * @param speciationCount Count of speciations nodes currently in the tree (only for naming)
   * @param time            Time of the speciation event
   * @return The newly generated species node that is a direct sibling of the given species node
   */
  public static SpeciesNode performSpeciation(SpeciesNode speciesNode, int newSpeciesId,
                                              int speciationCount, double time) {

    SpeciesNode newInnerNode = new SpeciesNode(++speciationCount);

    // move edge events list to new inner node
    for (GeneNode edgeNode : speciesNode.getEdgeEvents()) {
      edgeNode.setSpeciesNode(newInnerNode);
    }
    newInnerNode.setEdgeEvents(speciesNode.getEdgeEvents());
    speciesNode.setEdgeEvents(new TreeSet<GeneNode>(newInnerNode.getEdgeEvents().comparator()));

    // set distances of inner species node
    newInnerNode.setDistanceToRoot(time);
    SpeciesNode parentSpec = (SpeciesNode) speciesNode.getParent();
    if (parentSpec != null) {
      newInnerNode.setDistanceToParent(time - parentSpec.getDistanceToRoot());
    } else {
      newInnerNode.setDistanceToParent(time);
    }

    // if the given node has a parent replace the child that was the given node with the newly
    // created inner node
    if (parentSpec != null) {
      if (parentSpec.getLeftChild() == speciesNode) {
        parentSpec.setLeftChild(newInnerNode);
      } else {
        parentSpec.setRightChild(newInnerNode);
      }
    }

    SpeciesNode newLeafNode = new SpeciesNode(newSpeciesId);

    // copy alle genes to new species and create inner gene nodes in the new inner species node
    // mark those nodes with a speciation event and set its distances
    GeneNode parentGene = null;
    for (Map.Entry<Integer, GeneNode> geneEntry : speciesNode.getGenes().entrySet()) {
      GeneNode newLeafGene = new GeneNode(geneEntry.getKey());

      // Set gene - species relationship for new leaf gene  and new leaf species
      newLeafGene.setSpeciesNode(newLeafNode);
      newLeafNode.addGene(newLeafGene);
      // Set gene - species relationship for new inner gene  and new inner species
      GeneNode newInnerGene = new GeneNode(++speciationCount);
      newInnerGene.setSpeciesNode(newInnerNode);
      newInnerNode.addGene(newInnerGene);

      // set distances of new inner node
      newInnerGene.setDistanceToRoot(time);
      parentGene = (GeneNode)geneEntry.getValue().getParent();

      if (parentGene != null) {
        newInnerGene.setDistanceToParent(time - parentGene.getDistanceToRoot());
      } else {
        newInnerGene.setDistanceToParent(time);
      }

      // if the current gene node has a parent replace the child that was the given node whith the
      // newly created inner node
      if (parentGene != null) {
        if (parentGene.getLeftChild() == geneEntry.getValue()) {
          parentGene.setLeftChild(newInnerGene);
        } else {
          parentGene.setRightChild(newInnerGene);
        }
      }

      newInnerGene.setLeftChild(geneEntry.getValue());
      newInnerGene.setRightChild(newLeafGene);

      newInnerGene.setEvent(new SpeciationEvent(speciesNode, newLeafNode));
    }

    // set children of newly created inner species node
    newInnerNode.setLeftChild(speciesNode);
    newInnerNode.setRightChild(newLeafNode);

    return newLeafNode;
  }

  /**
   * Performs a duplication event on the given node of a gene tree.
   *
   * @param geneNode         Gene node that is subjected to a dublication event
   * @param newGeneId        Id for the newly created gene node
   * @param duplicationCount Number of duplications that already took place (new inner node will be
   *                         named, that is its id will be, "{@literal <}duplicationCount with three
   *                         digits filled with leading 0s{@literal >}")
   * @param time             Time of the duplication event
   */
  public static void performDuplication(GeneNode geneNode, int newGeneId, int duplicationCount,
                                        double time) {
    GeneNode newInnerNode = new GeneNode(duplicationCount + 1);

    // set distances
    newInnerNode.setDistanceToRoot(time);
    GeneNode parentGene = (GeneNode) geneNode.getParent();
    if (parentGene != null) {
      newInnerNode.setDistanceToParent(time - parentGene.getDistanceToRoot());
    } else {
      newInnerNode.setDistanceToParent(time);
    }

    // If the given gene node had a parent, replace the left or right child that was the given node
    // with the new inner node
    if (parentGene != null) {
      if (parentGene.getLeftChild() == geneNode) {
        parentGene.setLeftChild(newInnerNode);
      } else {
        parentGene.setRightChild(newInnerNode);
      }
    }

    GeneNode newLeafNode = new GeneNode(newGeneId);

    // Set children of new inner node appropriately
    newInnerNode.setLeftChild(geneNode);
    newInnerNode.setRightChild(newLeafNode);

    // Set gene - species relationship for new gene with species of given gene
    newLeafNode.setSpeciesNode(geneNode.getSpeciesNode());
    newInnerNode.setSpeciesNode(geneNode.getSpeciesNode());
    geneNode.getSpeciesNode().addGene(newLeafNode);

    // Insert inner node in edge event list of species node
    geneNode.getSpeciesNode().addEdgeEvent(newInnerNode);

    // Mark new inner node as duplication event by setting its event member accordingly
    newInnerNode.setEvent(new DuplicationEvent(geneNode, newLeafNode));
  }

  /**
   * Performs lateral gene transfer event for given donorGene and targetSpecies. Therefore setting
   *
   * @param donorGene     gene lgt is started from
   * @param targetSpecies species in which newGene will be inserted
   * @param lgtCount      lgtCount from parsingLogfile for event node ID
   * @param newGeneId     geneID for newGene
   * @param time          time of lgt event
   */
  public static void performLateralGeneTransfer(GeneNode donorGene, SpeciesNode targetSpecies,
      int newGeneId, int lgtCount, double time) {
    GeneNode newGene = new GeneNode(newGeneId);
    GeneNode newInnerEvent = new GeneNode(lgtCount + 1);

    // get parent
    GeneNode parentNode = (GeneNode) donorGene.getParent();

    // Create triple
    newInnerEvent.setRightChild(newGene);
    newInnerEvent.setLeftChild(donorGene);

    // link new inner node with parent
    if (parentNode != null && parentNode.getLeftChild() == donorGene) {
      parentNode.setLeftChild(newInnerEvent);
    } else if (parentNode != null) {
      parentNode.setRightChild(newInnerEvent);
    }

    // add new gene to target species
    targetSpecies.addGene(newGene);

    // set target species as spec node for newGene
    newGene.setSpeciesNode(targetSpecies);

    // set distances
    newInnerEvent.setDistanceToRoot(time);
    newInnerEvent.setDistanceToParent(time - parentNode.getDistanceToRoot());

    // Add newInnerEvent to donor spec edge event list and set donor species as SpeciesNode
    SpeciesNode donorSpecies = donorGene.getSpeciesNode();
    donorSpecies.addEdgeEvent(newInnerEvent);
    newInnerEvent.setSpeciesNode(donorSpecies);

    // create event
    LateralGeneTransferEvent lgtEvent =
        new LateralGeneTransferEvent(donorSpecies, donorGene, targetSpecies, newGene);

    // set event for newInnerEvent
    newInnerEvent.setEvent(lgtEvent);
  }

  /**
   * Performs deletion of given eventNode. Therefore setting the respective child of parent node to
   * null. {@link #deleteEvent(GeneNode, SpeciesNode) deleteEvent(parent, speciesNode)} is called
   * recursively, if parent has no more children.
   *
   * @param eventNode            event about to be deleted
   * @param speciesNodeWithEvent underlying species node, needed to handle edge events
   */
  public static void deleteEvent(
      GeneNode eventNode, SpeciesNode speciesNodeWithEvent) {
    //get event type
    EventType eventType = eventNode.getEvent().getType();
    //remove node from edge events
    switch (eventType) {
      case SPEC:
        speciesNodeWithEvent = eventNode.getSpeciesNode();
        speciesNodeWithEvent.removeGene(eventNode);
        break;
      case DUP:
        speciesNodeWithEvent.removeEdgeEvent(eventNode);
        break;
      case LGT:
        LateralGeneTransferEvent lgt = (LateralGeneTransferEvent) eventNode.getEvent();
        speciesNodeWithEvent = lgt.getDonorSpec();
        speciesNodeWithEvent.removeEdgeEvent(eventNode);
        break;
      default:
        //never reached
        break;
    }

    //get parentNode
    GeneNode parentNode = (GeneNode) eventNode.getParent();

    // check, whether given node is root of a gene tree
    if (parentNode == null) {
      // no more deletions necessary, since no more references to this node exist
      return;
    }


    //getting sibling of geneNode, null if there is no sibling
    GeneNode siblingNode = (parentNode.getLeftChild() == eventNode)
        ? (GeneNode) parentNode.getRightChild() : (GeneNode) parentNode.getLeftChild();

    //actual deletion
    if (parentNode.getLeftChild() == eventNode) {
      parentNode.setLeftChild(null);
    } else {
      parentNode.setRightChild(null);
    }

    //recursively remove parent, if it has no more children and is no root
    if (siblingNode == null) {
      if (parentNode.getParent() != null) { // parent is not root -> call recursion
        deleteEvent(parentNode, speciesNodeWithEvent);
      } else { // parent is root -> remove root from root species
        parentNode.getSpeciesNode().getGenes().remove(parentNode.getId());
      }
    }
  }

  /**
   * Performs loss of given geneNode. Therefore setting the respective parent's child to null. Calls
   * {@link #deleteEvent(GeneNode, SpeciesNode) deleteEvent(parent, speciesNode)} with underlying
   * speciesNode if parent has no more children.
   *
   * @param geneNode gene about to be deleted
   * @throws IllegalTreeStateException if geneNode is root of a tree
   */
  public static void performGeneLoss(GeneNode geneNode) throws IllegalTreeStateException {
    if (geneNode == null) {
      throw new IllegalTreeStateException(
          String.format("LogfileParser: Fatal error during performGeneLoss of gene %s!",
              "null"),
          new NullPointerException("Target Gene is null!"));
    }

    //get parentNode (should not be null)
    GeneNode parentNode = (GeneNode) geneNode.getParent();

    //get underlying species node
    SpeciesNode speciesNode = geneNode.getSpeciesNode();

    //throw exception when geneNode is a root
    if (parentNode == null) {
      throw new IllegalTreeStateException(
          String.format("LogfileParser: Fatal error during performGeneLoss of gene %s!",
              geneNode.toString()),
          new NullPointerException("Cannot remove roots of gene trees (parent is null)!"));
    }

    //remove geneNode from speciesNode gene list
    speciesNode.removeGene(geneNode);

    //getting sibling of geneNode, null if there is no sibling
    GeneNode siblingNode = (parentNode.getLeftChild() == geneNode)
        ? (GeneNode) parentNode.getRightChild() : (GeneNode) parentNode.getLeftChild();

    //actual deletion
    if (parentNode.getLeftChild() == geneNode) {
      parentNode.setLeftChild(null);
    } else {
      parentNode.setRightChild(null);
    }

    //recursively remove parent, if it has no more children
    if (siblingNode == null) {
      deleteEvent(parentNode, speciesNode);
    }
  }

  /**
   * Extracts the distances of the leaves to their parents for all trees generated by ALFsim.
   * @param  species       List of species that where extracted during the parsing of the
   *                       logfile
   * @param  alfOutputPath Directory that was created by ALF and that contains the \"RealTree.nwk\"
   *                       (containing the species tree) and a folgder \"GeneTrees\" containing all
   *                       gene trees that where created by ALF in newick-format
   * @throws IOException   If the specified ALF output directory does not exist or does not contain
   *                       the expected trees
   */
  public static void getLeafDistancesFromDarwin(List<SpeciesNode> species, String alfOutputPath)
      throws IOException {

    File alfDir = new File(alfOutputPath);
    if (!alfDir.exists()) {
      throw new IOException("Given ALF output directory does not exist!");
    }

    File speciesTreeDrw = new File(alfDir, "RealTree.drw");
    if (!speciesTreeDrw.exists()) {
      throw new IOException("File containing species tree must be called \"RealTree.nwk\" and "
          + "has to be located in the root of ALFs output folder!");
    }

    getLeafDistancesFromSpeciesTree(species, speciesTreeDrw);

    File geneTreeDir = new File(alfDir, "GeneTrees");
    if (!speciesTreeDrw.exists()) {
      throw new IOException("File containing gene trees must be called \"GeneTrees\" and "
          + "has to be located in the root of ALFs output folder!");
    }

    File[] geneTrees = geneTreeDir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.matches("GeneTree\\d+.drw");
      }
    });

    for (int i = 0; i < geneTrees.length; i++) {
      getLeafDistancesFromGeneTree(species, geneTrees[i]);
    }

  }

  /**
   * Extracts the distances of the leaves to their parents for a species tree given in darwin
   * format.
   * @param  species               List of species that where extracted during the parsing of the
   *                               logfile
   * @param  darwinFile            Darwin file that contains the species tree for the given species
   * @throws FileNotFoundException If the darwin file does not exist
   */
  public static void getLeafDistancesFromSpeciesTree(List<SpeciesNode> species, File darwinFile)
      throws FileNotFoundException {
    final Pattern leafPattern = Pattern.compile(
        "Leaf\\('SE(\\d{3})',(\\d+(?:\\.\\d+)?)");
    //          ^^^^^^^^^^^ Species id
    //                      ^ colon separates leaf from distance to parent
    //                       ^^^^^^^^^^^^^^^^^^ capture distance to parent as decimal number

    try {
      Scanner file = new Scanner(darwinFile);
      String match = file.findInLine(leafPattern);

      int specId;
      double distanceToRoot;
      SpeciesNode spec = null;
      while (match != null) {
        // extract id and distance from match
        specId = Integer.parseInt(file.match().group(1));
        distanceToRoot = Double.parseDouble(file.match().group(2));

        // set the distances of the species appropriately
        spec = species.get(specId - 1);
        spec.setDistanceToRoot(distanceToRoot);
        spec.setDistanceToParent(distanceToRoot - spec.getParent().getDistanceToRoot());
        match = file.findInLine(leafPattern);
      }

      file.close();
    } catch (FileNotFoundException e) {
      throw e;
    }
  }

  /**
   * Extracts the distances of the leaves to their parents for a gene tree given in darwin
   * format.
   * @param  species               List of species that where extracted during the parsing of the
   *                               logfile
   * @param  darwinFile            Darwin file that contains the species tree for the given species
   * @throws FileNotFoundException If the darwin file does not exist
   */
  public static void getLeafDistancesFromGeneTree(List<SpeciesNode> species, File darwinFile)
      throws FileNotFoundException {
    final Pattern leafPattern = Pattern.compile(
        "Leaf\\('SE(\\d{3})/(\\d{5})',(\\d+(?:\\.\\d+)?)");
    //          ^^^^^^^^^^^ Species id
    //                     ^ Slash to separate species id and gene id
    //                      ^^^^^^^^ Gene id
    //                               ^ comma separates leaf from distance to root
    //                                ^^^^^^^^^^^^^^^^^^ capture distance to root as decimal number

    try {
      Scanner file = new Scanner(darwinFile);
      String match = file.findInLine(leafPattern);

      int specId;
      int geneId;
      double distanceToRoot = 0;
      GeneNode gene = null;
      while (match != null) {
        // extract id and distance from match
        specId = Integer.parseInt(file.match().group(1));
        geneId = Integer.parseInt(file.match().group(2));
        distanceToRoot = Double.parseDouble(file.match().group(3));

        // set the distances of the species appropriately
        gene = species.get(specId - 1).getGenes().get(geneId);
        gene.setDistanceToRoot(distanceToRoot);
        gene.setDistanceToParent(distanceToRoot - gene.getParent().getDistanceToRoot());
        match = file.findInLine(leafPattern);
      }

      file.close();
    } catch (FileNotFoundException e) {
      throw e;
    }
  }

}
