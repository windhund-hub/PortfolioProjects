package de.unileipzig.nw18a.tool2.reconstructionutils;

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

  public void writeOutBestMatchGraphs(List<BestMatchGraph> bestMatchGraphs) {
    //TODO #112
  }

}
