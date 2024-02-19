package de.unileipzig.nw18a.nexbuild;

import de.unileipzig.nw18a.commontypes.GeneRelation;
import de.unileipzig.nw18a.commontypes.GeneRelationType;
import de.unileipzig.nw18a.commontypes.Triplet;
import de.unileipzig.nw18a.commontypes.TripletEntry;

import de.unileipzig.nw18a.nexbuild.buildutils.BuildUtils;
import de.unileipzig.nw18a.nexbuild.buildutils.BuildUtils.GeneTripletsResult;
import de.unileipzig.nw18a.nexbuild.buildutils.Node;
import de.unileipzig.nw18a.nexusreader.NexusFormatException;
import de.unileipzig.nw18a.nexusreader.NexusReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import picocli.CommandLine;

@CommandLine.Command(name = "nexbuild", mixinStandardHelpOptions = true, version = "v0.1")
public class NexBuild implements Runnable {


  /**
   * Starts the CLI parser (picocli).
   * @param args command line arguments
   */
  public static void main(String[] args) {
    CommandLine cmd = new CommandLine(new NexBuild());
    cmd.parseWithHandlers(
        new CommandLine.RunLast().andExit(0),
        CommandLine.defaultExceptionHandler().andExit(1), args);
  }

  @CommandLine.Option(names = {"-i", "--input"}, paramLabel = "NEXUS FILE",
      description = "Path to nexus input file.", required = true)
  private String nexPath;

  @CommandLine.Option(names = {"-v", "--verbose"},
      description = "Writes progress messages to stderr.",
      required = false)
  private boolean verbose;

  @CommandLine.Option(names = {"-vv", "--triplets"},
      description = "Like -v and writes out how many triplets were calculated.",
      required = false)
  private boolean triplets;

  @CommandLine.Option(names = {"-o", "--output"}, paramLabel = "OUTPUT", arity = "1",
      description = "Write output to a folder instead of the stdout.",
      required = false)
  private String outputFolder;


  @Override
  public void run() {

    if (outputFolder != null) {
      checkOutputPath();
    }

    printStatus("Reading in nexus file... ");
    NexusReader nexusReader = null;
    try {
      nexusReader = new NexusReader(nexPath);
      printStatusln(makeGreen("OK"));
    } catch (IOException e) {
      printStatusln(makeRed("ERR"));
      System.err.println(makeRed("Error reading file: " + e.getMessage()));
      System.exit(1);
    } catch (NexusFormatException e) {
      printStatusln(makeRed("ERR"));
      System.err.println(makeRed("Error parsing file: " + e.getMessage()));
      System.exit(1);
    }

    performBuild(nexusReader);

    printStatusln("Done. Exiting...");
  }

  private void performBuild(NexusReader nexusReader) {

    printStatusln("Performing BUILD algorithm:");
    printStatus("Extracting RCB relations... ");
    List<GeneRelation> rcbRelations = nexusReader.getGeneRelationsByType(GeneRelationType.RCB);
    if (rcbRelations == null) {
      printStatusln(makeRed("ERR"));
      System.err.println(makeRed("No RCB relation found in nexus file!"));
      System.exit(1);
    }
    printStatusln(makeGreen("OK"));

    printStatus("Generating triplets... ");
    Set<Triplet> speciesTreeTriplets = new LinkedHashSet<Triplet>();
    List<BuildUtils.GeneTripletsResult> geneTreeResults
        = new ArrayList<GeneTripletsResult>(rcbRelations.size());
    //first, calculate triplets for every gene tree
    for (GeneRelation relation : rcbRelations) {
      BuildUtils.GeneTripletsResult result
          = BuildUtils.makeGeneTriplets(relation);
      //merge species triplets in one set
      speciesTreeTriplets.addAll(result.getSpeciesTriplets());
      geneTreeResults.add(result);
    }
    //calling closure to generate additional species triplets
    BuildUtils.calculateClosure(speciesTreeTriplets);
    //calling complete to generate additional gene triplets
    for (GeneTripletsResult result : geneTreeResults) {
      result.completeGeneTriplets(speciesTreeTriplets);
    }
    printStatusln(makeGreen("OK"));
    if (triplets) {
      for (GeneTripletsResult result : geneTreeResults) {
        printStatusln(String.format("\ttree: %s\t#triplets: %d",
            result.getTreeName(), result.getGeneTriplets().size()));
      }
      printStatusln(String.format("\tspecies-tree\t#triplets: %d",
          speciesTreeTriplets.size()));
    }

    printStatus("Building trees... ");
    boolean error = false;
    TreeMap<String, Node> treeNodes = new TreeMap<String, Node>();
    //call BUILD for every set of gene triplets to get the gene trees
    for (int i = 0; i < geneTreeResults.size(); i++) {
      GeneTripletsResult geneResult = geneTreeResults.get(i);
      GeneRelation geneRcbRelation = rcbRelations.get(i);
      Node treeRoot = BuildUtils.buildTree(geneResult.getGeneTriplets(),
          new LinkedHashSet<TripletEntry>(geneRcbRelation.getGenes()));
      if (treeRoot != null) {
        treeNodes.put(geneResult.getTreeName(), treeRoot);
      } else {
        error = true;
        printStatusln(makeRed("ERR"));
        System.err.println("BUILD algorithm could not construct a tree for: "
            + rcbRelations.get(i).getTreeName() + ". Skipping...");
      }
    }
    //call BUILD for the species triplets to get the species tree
    Node treeRoot = BuildUtils.buildTree(speciesTreeTriplets,
        new LinkedHashSet<TripletEntry>(nexusReader.getSpecies().values()));
    if (treeRoot != null) {
      treeNodes.put("S", treeRoot);
    } else {
      if (!error) {
        printStatusln(makeRed("ERR"));
      }
      error = true;
      System.err.println("BUILD algorithm could not construct a species tree. Skipping...");
    }

    if (!error) {
      printStatusln(makeGreen("OK"));
    }

    printStatus("Writing them out... ");
    try {
      if (outputFolder == null) {
        BuildUtils.writeOutTrees(treeNodes, System.out);
      } else {
        BuildUtils.writeOutTrees(treeNodes, outputFolder);
      }
    } catch (IOException e) {
      printStatusln(makeRed("ERR"));
      System.err.println(makeRed("Error writing out trees: " + e.getMessage()));
      System.exit(1);
    }
    printStatusln(makeGreen("OK"));

  }

  private void checkOutputPath() {

    File outputFile = new File(outputFolder);
    if (!outputFile.exists()) {
      printStatus("Output path doesn't exist. Creating... ");
      boolean success = outputFile.mkdirs();
      if (success) {
        printStatusln(makeGreen("OK"));
      } else {
        printStatusln(makeRed("ERR"));
        System.err.println(makeRed("Error creating the output directory."));
        System.exit(1);
      }
    }
    if (!outputFile.isDirectory()) {
      printStatusln(makeRed("Output path is not a directory!"));
      System.exit(1);
    }
  }

  private void printStatus(String msg) {
    // if in verbose mode
    if (verbose || triplets) {
      // if in very verbose mode (-vv)
      System.err.print(msg);
    }
    // else (only -v) don't print the status
  }

  private void printStatusln(String msg) {
    // if in verbose mode
    if (verbose || triplets) {
      // if in very verbose mode (-vv)
      System.err.println(msg);
    }
    // else (only -v) don't print the status
  }

  private static String makeGreen(String message) {
    return (char)27 + "[32m" + message + (char)27 + "[39m";
  }

  private static String makeRed(String message) {
    return (char)27 + "[31m" + message + (char)27 + "[39m";
  }

}
