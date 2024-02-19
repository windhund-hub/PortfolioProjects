package de.unileipzig.nw18a.nexmatch.matchutils;

import de.unileipzig.nw18a.commontypes.BasicNexusWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


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
   * Writes out trees of best matches and whether it is reciprocal or not.
   * @param  bestMatchGraphs      List of best match trees.
   */
  public void writeOutBestMatchGraphs(List<BestMatchGraph> bestMatchGraphs) {
    writer.println("BEGIN BMG;");
    writer.println("\t[best match to first gene, is second gene; 0=not reciprocal, 1=reciprocal]");
    for (BestMatchGraph tree: bestMatchGraphs) {
      writer.println(String.format("\tbmg %s", tree.getTreeName()));
      for (int i = 0; i < tree.getBestMatches().size(); i++) {
        writer.print(String.format("\t\t%s %s %d", tree.getBestMatches().get(i)[0],
                  tree.getBestMatches().get(i)[1], tree.getIsInReciprocal().get(i) ? 1 : 0));
        if (i != tree.getBestMatches().size() - 1) {
          writer.println(",");
        } else {
          writer.println(";");
        }
      }
    }
    writer.println("END;");
    writer.println();
  }
}


