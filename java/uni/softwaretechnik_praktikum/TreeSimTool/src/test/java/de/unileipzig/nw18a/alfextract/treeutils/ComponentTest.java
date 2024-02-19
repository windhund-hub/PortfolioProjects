package de.unileipzig.nw18a.alfextract.treeutils;

import de.unileipzig.nw18a.alfextract.treemapping.SpeciesNode;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class ComponentTest {

  /**
   * Tests the newick trees of the first example S5G1.
   * @throws IOException input errors
   * @throws IllegalTreeStateException if the logfile is corrupt
   */
  @Test
  public void compareNewickTrees() throws IOException, IllegalTreeStateException {
    //Arrange
    String speciesTree = readResourceFile(
        "alf_examples/test_simulation_S5_G1/RealTree.nwk");
    speciesTree = makeNewickTreeStandard(speciesTree);
    String geneTree1 = readResourceFile(
        "alf_examples/test_simulation_S5_G1/GeneTrees/GeneTree1.nwk");
    geneTree1 = makeNewickTreeStandard(geneTree1);

    //Act
    SpeciesNode root = LogfileParser.parseLogfile(
        "./src/test/resources/alf_examples/test_simulation_S5_G1/");
    String ourSpeciesTree = NexusWriter.treeToString(root);
    ourSpeciesTree = makeNewickTreeStandard(ourSpeciesTree);
    String ourGeneTree1 = NexusWriter.treeToString(root.getGenes().firstEntry().getValue());
    ourGeneTree1 = makeNewickTreeStandard(ourGeneTree1);

    //Assert
    Assert.assertEquals(speciesTree, ourSpeciesTree);
    Assert.assertEquals(geneTree1, ourGeneTree1);

  }

  /**
   * Reads in ressource files without newline break.
   * @param relativePath a relative path to the file from the resources directory,
   *                     not starting with /
   * @return the file content without newlines
   * @throws IOException input errors
   */
  private String readResourceFile(String relativePath) throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new FileReader(
        "./src/test/resources/" + relativePath
    ));
    StringBuilder result = new StringBuilder();
    String line = bufferedReader.readLine();
    while (line != null) {
      result.append(line);
      line = bufferedReader.readLine();
    }
    bufferedReader.close();

    return result.toString();
  }

  /**
   * Takes a newick tree as string and strips away information, so that
   * it is comparable with other newick trees.
   * @param newickTree a newick tree as string
   * @return the standardizes newick tree
   */
  private String makeNewickTreeStandard(String newickTree) {
    //Pattern distancePattern = Pattern.compile("(:\\d+\\.\\d\\d)\\d+");
    //Matcher distanceMatcher = distancePattern.matcher(newickTree);
    //newickTree = distanceMatcher.replaceAll("$1");
    newickTree = newickTree.replaceAll(":\\d+(\\.\\d+)?", "");
    newickTree = newickTree.replaceAll(";", "");
    newickTree = newickTree.replaceAll("\\[&&NHX:D=.\\]", "");
    return newickTree.replaceAll("\\)[S|D|L]\\d+_\\$", ")");
  }
}
