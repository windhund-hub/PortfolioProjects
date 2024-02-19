package de.unileipzig.nw18a.alfextract;

import de.unileipzig.nw18a.alfextract.treemapping.SpeciesNode;
import de.unileipzig.nw18a.alfextract.treeutils.IllegalTreeStateException;
import de.unileipzig.nw18a.alfextract.treeutils.LogfileParser;
import de.unileipzig.nw18a.alfextract.treeutils.NexusWriter;
import de.unileipzig.nw18a.alfextract.treeutils.TreeUtils;
import de.unileipzig.nw18a.commontypes.GeneDistances;
import de.unileipzig.nw18a.commontypes.GeneRelation;
import de.unileipzig.nw18a.commontypes.Matrix.MatrixFormat;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;

@Command(name = "alfextract", mixinStandardHelpOptions = true, version = "v0.1")
public class AlfExtract implements Runnable {

  /**
   * Starts the CLI parser (picocli).
   * @param args command line arguments
   */
  public static void main(String[] args) {
    CommandLine cmd = new CommandLine(new AlfExtract());
    cmd.parseWithHandlers(
        new CommandLine.RunLast().andExit(0),
        CommandLine.defaultExceptionHandler().andExit(1), args);
  }

  private static final String validMatrixFormats = "UPPER, LOWER, BOTH";

  @Option(names = {"-i", "--input"}, paramLabel = "ALFDIR",
      description = "The ALFsim output dir.", required = true)
  private String alfPath;

  @Option(names = {"--all"},
      description = "Writes out all available outputs: mapping, distances, rcb and lca.\n",
      required = false)
      private boolean all;

  @Option(names = {"--rcb"}, paramLabel = "MATRIX-FORMAT", arity = "0..1",
      description = "The format of the matrix representing the rcb-relation.\n"
          + "Accepted are: " + validMatrixFormats + " (default: BOTH)",
      required = false, converter = MatrixFormatConverter.class)
  private MatrixFormat rcbFormat;

  @Option(names = {"--lca"}, paramLabel = "MATRIX-FORMAT", arity = "0..1",
      description = "The format of the matrix representing the lca-relation.\n"
          + "Accepted are: " + validMatrixFormats + " (default: BOTH)",
      required = false, converter = MatrixFormatConverter.class)
  private MatrixFormat lcaFormat;

  @Option(names = {"--fitch"},
      description = "Generates the fitch-relation.",
      required = false)
  private boolean generateFitch;

  @Option(names = {"--mapping"},
      description = "Writes out the mapping of the gene trees into the species tree.",
      required = false)
  private boolean generateMapping;

  @Option(names = {"--distances"}, paramLabel = "MATRIX-FORMAT", arity = "0..1",
      description = "The format of the distance matrix.\n"
          + "Accepted are: " + validMatrixFormats + " (default: BOTH)",
      required = false, converter = MatrixFormatConverter.class)
  private MatrixFormat distancesFormat;

  @Option(names = {"-v", "--verbose"},
      description = "Writes progress messages to stderr.",
      required = false)
  private boolean verbose;

  @Option(names = {"-o", "--output"}, paramLabel = "OUTPUT", arity = "1",
      description = "Output file name (Nexus).",
      required = false)
  private String outputPath;


  /**
   * Is run when the program starts and all input parameters and options are assigned.
   */
  public void run() {
    //check input dir
    checkInputPath();

    //run program
    printStatus("Parsing of input data... ");
    SpeciesNode root = null;
    try {
      root = LogfileParser.parseLogfile(alfPath);
    } catch (IllegalTreeStateException e) {
      printStatusln(makeRed("ERR"));
      System.err.println(makeRed("Error parsing the logfile: " + e.getMessage()));
      System.exit(1);
    } catch (IOException e) {
      printStatusln(makeRed("ERR"));
      System.err.println(makeRed("Error reading logfile: " + e.getMessage()));
      System.exit(1);
    }
    printStatusln(makeGreen("OK"));

    //Open NexusWriter
    printStatus("Opening output stream... ");
    NexusWriter nexusWriter = null;
    try {
      if (outputPath == null) {
        nexusWriter = new NexusWriter(System.out, false);
      } else {
        nexusWriter = new NexusWriter(outputPath, false);
      }
    } catch (IOException e) {
      printStatusln(makeRed("ERR"));
      System.err.println(makeRed("Error opening an output stream: " + e.getMessage()));
      System.exit(1);
    }
    printStatusln(makeGreen("OK"));

    List<List<GeneRelation>> geneRelations = new ArrayList<List<GeneRelation>>();
    if (all) {
      generateMapping = true;
      if (distancesFormat == null) {
        distancesFormat = MatrixFormat.BOTH;
      }
      generateFitch = true;
      if (rcbFormat == null) {
        rcbFormat = MatrixFormat.BOTH;
      }

      if (lcaFormat == null) {
        lcaFormat = MatrixFormat.BOTH;
      }
    }
    if (generateMapping) {
      printStatus("Calculating MAPPING... ");
      nexusWriter.writeOutAllTrees(root);
      nexusWriter.writeOutReconciliation(root);
      printStatusln(makeGreen("OK"));
    }

    if (distancesFormat != null) {
      printStatus("Calculating DISTANCES... ");
      List<GeneDistances> distances = TreeUtils.getDistances(root);

      for (GeneDistances dists : distances) {
        dists.getDistances().changeFormat(distancesFormat);
      }
      nexusWriter.writeOutDistances(distances);
      printStatusln(makeGreen("OK"));
    }

    if (rcbFormat != null) {
      printStatus("Calculating RCB... ");
      try {
        List<GeneRelation> rcbRelations = TreeUtils.buildReconciliationBased(root);
        geneRelations.add(rcbRelations);

        for (GeneRelation relation : rcbRelations) {
          relation.getRelation().changeFormat(rcbFormat);
        }
        printStatusln(makeGreen("OK"));
      } catch (IllegalTreeStateException e) {
        printStatusln(makeRed("ERR"));
        System.err.println(makeRed("Error calculating RCB relations: " + e.getMessage()));
      } catch (IllegalArgumentException e) {
        printStatusln(makeRed("ERR"));
        System.err.println(makeRed("Error calculating RCB relations: " + e.getMessage()));
      }

    }
    if (lcaFormat != null) {
      printStatus("Calculating LCA... ");
      try {
        List<GeneRelation> lcaRelations = TreeUtils.buildLastCommonAncestor(root);
        geneRelations.add(lcaRelations);

        for (GeneRelation relation : lcaRelations) {
          relation.getRelation().changeFormat(lcaFormat);
        }
        printStatusln(makeGreen("OK"));
      } catch (IllegalTreeStateException e) {
        printStatusln(makeRed("ERR"));
        System.err.println(makeRed("Error calculating LCA relations: " + e.getMessage()));
      } catch (IllegalArgumentException e) {
        printStatusln(makeRed("ERR"));
        System.err.println(makeRed("Error calculating LCA relations: " + e.getMessage()));
      }
    }
    if (generateFitch) {
      printStatus("Calculating FITCH... ");
      try {
        geneRelations.add(TreeUtils.buildFitch(root));
        printStatusln(makeGreen("OK"));
      } catch (IllegalTreeStateException e) {
        printStatusln(makeRed("ERR"));
        System.err.println(makeRed("Error calculating Fitch relations: " + e.getMessage()));
      }
    }

    //write geneRelations
    if (!geneRelations.isEmpty()) {
      printStatus("Writing relations... ");
      nexusWriter.writeOutGeneRelations(geneRelations);
      printStatusln(makeGreen("OK"));
    }

    try {
      nexusWriter.close();
    } catch (IOException e) {
      System.err.println("Error closing the output stream.");
    }

    printStatusln("Done. Exiting...");
  }

  private void checkInputPath() {
    File alfDir = new File(alfPath);
    if (!alfDir.exists()) {
      System.err.println("Input directory doesn't exist.");
      System.exit(1);
    } else if (!alfDir.isDirectory()) {
      System.err.println("Input is not a directory.");
      System.exit(1);
    } else if (!alfDir.canRead()) {
      System.err.println("Input directory is not readable.");
      System.exit(1);
    } else if (!new File(alfPath + File.separator + "logfile.txt").exists()) {
      System.err.println("Input directory doesn't seem to contain an ALFsim logfile.");
      System.exit(1);
    } else if ((!new File(alfPath + File.separator + "GeneTrees").exists())) {
      System.err.println("Input directory doesn't seem to contain GeneTrees. "
          + "Please update your ALFsim config to generate them.");
      System.exit(1);
    }
  }

  private static String makeGreen(String message) {
    return (char)27 + "[32m" + message + (char)27 + "[39m";
  }

  private static String makeRed(String message) {
    return (char)27 + "[31m" + message + (char)27 + "[39m";
  }

  static class MatrixFormatConverter implements ITypeConverter<MatrixFormat> {

    public MatrixFormat convert(String value) throws Exception {
      if (value.equals("")) {
        return MatrixFormat.BOTH;
      } else if (value.equalsIgnoreCase("UPPER")) {
        return MatrixFormat.UPPER;
      } else if (value.equalsIgnoreCase("LOWER")) {
        return MatrixFormat.LOWER;
      } else if (value.equalsIgnoreCase("BOTH")) {
        return MatrixFormat.BOTH;
      } else {
        throw new Exception("Must be one of: " + validMatrixFormats);
      }
    }
  }

  private void printStatus(String msg) {
    if (verbose) {
      // if in very verbose mode
      System.err.print(msg);
    }
    // else don't print the status

  }

  private void printStatusln(String msg) {
    if (verbose) {
      // if in verbose mode
      System.err.println(msg);
    }
    // else (only -v) don't print the status
  }

}
