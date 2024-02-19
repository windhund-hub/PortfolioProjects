package de.unileipzig.nw18a.commontypes;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;


public class BasicNexusWriter implements Closeable {

  protected PrintWriter writer;

  /**
   * Constructs a writer to a given output file.
   * @param  filepath    Path to file in which to write
   * @param  append      Whether or not to append to an existing file or override.
   * @throws IOException Error when opening the file stream.
   */
  public BasicNexusWriter(String filepath, boolean append) throws IOException {

    try {
      File outputFile = new File(filepath);
      boolean fileExists = outputFile.exists();
      writer = new PrintWriter(new FileWriter(outputFile, append));

      if (!append || !fileExists) {
        //start file
        writer.println("#NEXUS");
      }
    } catch (IOException e) {

      if (writer != null) {
        writer.close();
      }
      System.err.println("The specified file does not exist and a new could not be created!");
      throw e;
    }
  }

  /**
   * Constructs a writer to a given output stream.
   * @param  stream      Stream in which to write
   * @param  append      Whether or not to start a new nexus file by printing '#NEXUS' at the
   *                     beginning
   */
  public BasicNexusWriter(OutputStream stream, boolean append) {
    writer = new PrintWriter(stream);

    if (!append) {
      writer.println("#NEXUS");
    }
  }

  /**
   * Writes a RELATIONS block with all the given gene relations.
   * @param  relationsList The gene relations to write out.
   */
  public void writeOutGeneRelations(List<List<GeneRelation>> relationsList) {
    writer.println("BEGIN RELATIONS;");
    writer.println("[matrices each describing a type of relation between genes]");

    for (List<GeneRelation> geneRelations : relationsList) {
      if (geneRelations.isEmpty()) {
        continue;
      }
      //add comment
      switch (geneRelations.get(0).getType()) {
        case RCB:
          writer.println("\t[===RCB===]");
          writer.println("\t\t[evolutionary distances in gene tree and species tree are compared]");
          writer.println(String.format("\t\t[LESS_THAN=%d, EQUAL=%d, GREATER_THAN=%d]",
              GeneRelationType.LESS_THAN, GeneRelationType.EQUAL, GeneRelationType.GREATER_THAN));
          break;
        case LCA:
          writer.println("\t[===LCA===]");
          writer.println("\t\t[last common ancestor of genes had one of the events]");
          writer.println(String.format("\t\t[LEAF=%d, ORTHO=%d, PARA=%d, XENO=%d]",
              GeneRelationType.LEAF, GeneRelationType.ORTHO, GeneRelationType.PARA,
              GeneRelationType.XENO));
          break;
        case FITCH:
          writer.println("\t[===FITCH===]");
          writer.println("\t\t[genes are separated by at least one lateral gene transfer event]");
          break;
        default:
          throw new IllegalStateException("Unknown relation type.");
      }

      for (GeneRelation relation : geneRelations) {
        writer.println(String.format("\trelation"));
        writer.println(String.format("\t\ttype=%s name=%s triangle=%s",
            relation.getTypeName(), relation.getTreeName(),
            relation.getRelation().getFormat().name().toLowerCase()));
        //write out matrix
        for (int i = 0; i < relation.getRelation().size(); i++) {
          ArrayList<Byte> line = relation.getRelation().get(i);
          String geneName;
          if (i < relation.getGenes().size()) {
            geneName = relation.getGenes().get(i).toString();
          } else {
            throw new IllegalStateException(String.format(
                "Relation and genes are not equally sized: relation: %d, genes: %d",
                relation.getRelation().size(), relation.getGenes().size()
            ));
          }
          writer.print(String.format("\t\t\t%s", geneName));
          for (Byte r : line) {
            writer.write(String.format(" %2d", r));
          }
          writer.println();
        }
        writer.println("\t\t\t;");
      }
    }

    writer.println("END;");
    writer.println();
  }

  /**
   * Closes writer after writing.
   */
  public void close() throws IOException {
    if (writer != null) {
      writer.close();
    }
  }
}
