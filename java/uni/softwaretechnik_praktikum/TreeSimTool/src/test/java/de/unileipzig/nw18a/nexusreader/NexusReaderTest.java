package de.unileipzig.nw18a.nexusreader;

import de.unileipzig.nw18a.alfextract.AlfExtract;
import de.unileipzig.nw18a.commontypes.GeneRelation;
import de.unileipzig.nw18a.commontypes.GeneRelationType;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import picocli.CommandLine;

public class NexusReaderTest {

  @Test
  public void testWriterAndReader() throws IOException, NexusFormatException {
    //Arrange
    CommandLine.run(new AlfExtract(), "--mapping", "--rcb", "--lca",
        "-i", "./src/test/resources/alf_examples/test_simulation_S5_G1",
        "-o", "output.nex");

    //Act
    NexusReader nexusReader = new NexusReader("output.nex");

    //Assert
    Assert.assertEquals("Size of species list",
        5, nexusReader.getSpecies().size());
    List<GeneRelation> rcbRelations = nexusReader.getGeneRelationsByType(GeneRelationType.RCB);
    Assert.assertEquals("Size of RCB relations",
        1, rcbRelations.size());
    GeneRelation rcb = rcbRelations.get(0);
    Assert.assertEquals("Size of genes in RCB relation",
        11, rcb.getGenes().size());
    Assert.assertEquals("Size of bytes in RCB relation",
        11, rcb.getRelation().size());
    List<Byte> testRow = Arrays.asList(
        GeneRelationType.EQUAL, GeneRelationType.EQUAL, GeneRelationType.EQUAL,
        GeneRelationType.EQUAL, GeneRelationType.LESS_THAN, GeneRelationType.LESS_THAN,
        GeneRelationType.LESS_THAN, GeneRelationType.LESS_THAN, GeneRelationType.LESS_THAN,
        GeneRelationType.EQUAL, GeneRelationType.EQUAL);
    for (int i = 0; i < rcb.getRelation().get(3).size(); i++) {
      byte actual = rcb.getRelation().get(3).get(i);
      byte expected = testRow.get(i);
      Assert.assertEquals("Byte 3," + i, expected, actual);
    }
  }
}
