package de.unileipzig.nw18a.alfextract.treeutils;

import de.unileipzig.nw18a.alfextract.treemapping.SpeciesNode;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


class NexusWriterTest {

  @Test
  void testTreeToString() {
    //===Arrange===
    //This tree is from our examples S5G1
    //Speciations
    final SpeciesNode s1 = new SpeciesNode(0.0, 0.0, 1);
    final SpeciesNode s2 = new SpeciesNode(11.826483, 0.0, 2);
    final SpeciesNode s3 = new SpeciesNode(22.646547, 0.0, 3);
    final SpeciesNode s4 = new SpeciesNode(48.047977, 0.0, 4);
    //Leafs
    final SpeciesNode se1 = new SpeciesNode(12.405798, 0.0, 1);
    final SpeciesNode se2 = new SpeciesNode(30.004751, 0.0, 2);
    final SpeciesNode se3 = new SpeciesNode(11.084785, 0.0, 3);
    final SpeciesNode se4 = new SpeciesNode(14.758067, 0.0, 4);
    final SpeciesNode se5 = new SpeciesNode(21.506542, 0.0, 5);
    //Attach
    s1.setLeftChild(s4);
    s1.setRightChild(s2);
    s2.setLeftChild(se2);
    s2.setRightChild(s3);
    s3.setLeftChild(se3);
    s3.setRightChild(se4);
    s4.setLeftChild(se1);
    s4.setRightChild(se5);

    //===Act===
    String tree = NexusWriter.treeToString(s1);

    //===Assert===
    Assert.assertEquals(
        "((SE001:12.405798,SE005:21.506542)S004_$:48.047977,"
            + "(SE002:30.004751,(SE003:11.084785,SE004:14.758067)"
            + "S003_$:22.646547)S002_$:11.826483)S001_$:0.000000",
        tree);
  }

  @Test
  void testTreeToStringOneChild() {
    //Arrange
    SpeciesNode root = new SpeciesNode(0.0, 0.0, 1);
    SpeciesNode oneChild = new SpeciesNode(1.0, 1.0, 2);
    root.setRightChild(oneChild);

    //Act
    String tree = NexusWriter.treeToString(root);

    //Assert
    Assert.assertEquals("(SE002:1.000000)S001_$:0.000000", tree);
  }
}
