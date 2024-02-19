package de.unileipzig.nw18a.nexmatch;

import de.unileipzig.nw18a.commontypes.GeneDistances;
import de.unileipzig.nw18a.nexmatch.matchutils.BestMatchGraph;
import de.unileipzig.nw18a.nexmatch.matchutils.NexusWriter;
import de.unileipzig.nw18a.nexusreader.NexusFormatException;
import de.unileipzig.nw18a.nexusreader.NexusReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import picocli.CommandLine;

@CommandLine.Command(name = "nexmatch", mixinStandardHelpOptions = true, version = "v0.1")
public class NexMatch implements Runnable {


  /**
   * Starts the CLI parser (picocli).
   * @param args command line arguments
   */
  public static void main(String[] args) {
    CommandLine cmd = new CommandLine(new NexMatch());
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

  @CommandLine.Option(names = {"-o", "--output"}, paramLabel = "OUTPUT", arity = "1",
      description = "Write output to a file instead of the stdout.",
      required = false)
  private String outputPath;

  @CommandLine.Option(names = {"-r", "--inplace"},
      description = "Appends calculated data to the input nexus file.", required = false)
  private boolean writeInPlace;


  @Override
  public void run() {

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

    performBmg(nexusReader);

    printStatusln("Done. Exiting...");
  }

  private void performBmg(NexusReader nexusReader) {

    printStatusln("Building best match graphs:");

    printStatus("Extracting gene distances... ");
    Map<String, GeneDistances> distanceMap = nexusReader.getDistanceMap();
    if (distanceMap == null) {
      printStatusln(makeRed("ERR"));
      System.err.println("No DISTANCES or ALLDISTANCES block in the given nexus file!");
      System.exit(1);
    }
    printStatusln(makeGreen("OK"));

    printStatus("Calculating best matches... ");
    List<BestMatchGraph> bestMatchGraphs = new ArrayList<BestMatchGraph>();
    for (GeneDistances distances : distanceMap.values()) {
      bestMatchGraphs.add(
          new BestMatchGraph(distances, true)
      );
    }
    printStatusln(makeGreen("OK"));

    printStatus("Writing them out... ");
    NexusWriter nexusWriter = null;
    try {
      if (writeInPlace) {
        nexusWriter = new NexusWriter(nexPath, true);
      } else if (outputPath != null) {
        nexusWriter = new NexusWriter(outputPath, false);
      } else {
        nexusWriter = new NexusWriter(System.out, false);
      }
    } catch (IOException e) {
      printStatusln(makeRed("ERR"));
      System.err.println("Error writing file: " + e.getMessage());
      System.exit(1);
    }
    nexusWriter.writeOutBestMatchGraphs(bestMatchGraphs);
    printStatusln(makeGreen("OK"));

    try {
      nexusWriter.close();
    } catch (IOException e) {
      System.err.println("Error closing the output stream.");
    }

  }

  private void printStatus(String msg) {
    // if in verbose mode
    if (verbose) {
      // if in very verbose mode (-vv)
      System.err.print(msg);
    }
    // else (only -v) don't print the status
  }

  private void printStatusln(String msg) {
    // if in verbose mode
    if (verbose) {
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
