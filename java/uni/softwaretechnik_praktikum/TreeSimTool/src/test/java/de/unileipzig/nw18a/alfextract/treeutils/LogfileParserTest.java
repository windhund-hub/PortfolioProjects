package de.unileipzig.nw18a.alfextract.treeutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.unileipzig.nw18a.alfextract.treemapping.DuplicationEvent;
import de.unileipzig.nw18a.alfextract.treemapping.Event;
import de.unileipzig.nw18a.alfextract.treemapping.EventType;
import de.unileipzig.nw18a.alfextract.treemapping.GeneNode;
import de.unileipzig.nw18a.alfextract.treemapping.LateralGeneTransferEvent;
import de.unileipzig.nw18a.alfextract.treemapping.SpeciationEvent;
import de.unileipzig.nw18a.alfextract.treemapping.SpeciesNode;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.junit.Test;

public class LogfileParserTest {

  /*==============================================================================================*/
  /* Tests for performSpeciation()                                                                */
  /*==============================================================================================*/
  @Test
  public void speciationShouldYieldTriplet1() {
    // Arrange
    SpeciesNode specNode = new SpeciesNode();

    // Act
    SpeciesNode newSpec = LogfileParser.performSpeciation(specNode, 1, 0, 0.);

    // Assert
    assertEquals("Left sibling of new node should be input node",
        newSpec.getParent().getLeftChild(),
        specNode);
    assertEquals("Right sibling of input node should be new node",
        specNode.getParent().getRightChild(),
        newSpec);
  }

  @Test
  public void speciationShouldYieldTriplet2() {
    // Arrange
    SpeciesNode rootSpec = new SpeciesNode();
    SpeciesNode leftSpec = new SpeciesNode();
    SpeciesNode rightSpec = new SpeciesNode();

    rootSpec.setLeftChild(leftSpec);
    rootSpec.setRightChild(rightSpec);

    // Act
    SpeciesNode newSpec = LogfileParser.performSpeciation(leftSpec, 2, 1, 5.);

    // Assert
    assertEquals("Left sibling of new node should be input node",
        newSpec.getParent().getLeftChild(),
        leftSpec);
    assertEquals("Right sibling of input node should be new node",
        leftSpec.getParent().getRightChild(),
        newSpec);
  }

  @Test
  public void speciationShouldYieldTriplet3() {
    // Arrange
    SpeciesNode rootSpec = new SpeciesNode();
    SpeciesNode leftSpec = new SpeciesNode();
    SpeciesNode rightSpec = new SpeciesNode();

    rootSpec.setLeftChild(leftSpec);
    rootSpec.setRightChild(rightSpec);

    // Act
    SpeciesNode newSpec = LogfileParser.performSpeciation(rightSpec, 2, 1, 5.);

    // Assert
    assertEquals("Left sibling of new node should be input node",
        newSpec.getParent().getLeftChild(),
        rightSpec);
    assertEquals("Right sibling of input node should be new node",
        rightSpec.getParent().getRightChild(),
        newSpec);
  }

  @Test
  public void newSpeciesShouldContainCopiesOfAllGenesInOldSpecies() {
    // Arrange
    SpeciesNode specNode = new SpeciesNode(1);

    specNode.getGenes().put(1, new GeneNode(1));
    specNode.getGenes().put(2, new GeneNode(2));

    // Act
    SpeciesNode newSpec = LogfileParser.performSpeciation(specNode, 1, 0, 0.);

    // Assert
    assertEquals(newSpec.getGenes().get(1).getId(), 1);
    assertEquals(newSpec.getGenes().get(2).getId(), 2);
  }

  @Test
  public void speciationShouldYieldGeneTripletsForEachGenes1() {
    // Arrange
    SpeciesNode specNode = new SpeciesNode(1);

    specNode.getGenes().put(1, new GeneNode(1));
    specNode.getGenes().put(2, new GeneNode(2));

    // Act
    SpeciesNode newSpec = LogfileParser.performSpeciation(specNode, 1, 0, 0.);

    // Assert
    assertEquals("Left sibling of new node should be input node",
        newSpec.getGenes().get(1).getParent().getLeftChild(),
        specNode.getGenes().get(1));
    assertEquals("Right sibling of input node should be new node",
        specNode.getGenes().get(1).getParent().getRightChild(),
        newSpec.getGenes().get(1));
  }

  @Test
  public void speciationShouldYieldGeneTripletsForEachGenes2() {
    // Arrange
    SpeciesNode rootSpec = new SpeciesNode(1);
    SpeciesNode leftSpec = new SpeciesNode(1);
    SpeciesNode rightSpec = new SpeciesNode(2);

    rootSpec.getGenes().put(1, new GeneNode(1));
    rootSpec.getGenes().put(2, new GeneNode(2));

    leftSpec.getGenes().put(1, new GeneNode(1));
    leftSpec.getGenes().put(2, new GeneNode(2));

    rightSpec.getGenes().put(1, new GeneNode(1));
    rightSpec.getGenes().put(2, new GeneNode(2));

    rootSpec.setLeftChild(leftSpec);
    rootSpec.setRightChild(rightSpec);

    rootSpec.getGenes().get(1).setLeftChild(leftSpec.getGenes().get(1));
    rootSpec.getGenes().get(1).setRightChild(rightSpec.getGenes().get(1));
    rootSpec.getGenes().get(2).setLeftChild(leftSpec.getGenes().get(2));
    rootSpec.getGenes().get(2).setRightChild(rightSpec.getGenes().get(2));


    // Act
    SpeciesNode newSpec = LogfileParser.performSpeciation(leftSpec, 2, 1, 5.);

    // Assert
    assertEquals("Left sibling of new node should be input node",
        newSpec.getGenes().get(1).getParent().getLeftChild(),
        leftSpec.getGenes().get(1));
    assertEquals("Right sibling of input node should be new node",
        leftSpec.getGenes().get(1).getParent().getRightChild(),
        newSpec.getGenes().get(1));
  }

  @Test
  public void speciationShouldYieldGeneTripletsForEachGenes3() {
    // Arrange
    SpeciesNode rootSpec = new SpeciesNode(1);
    SpeciesNode leftSpec = new SpeciesNode(1);
    SpeciesNode rightSpec = new SpeciesNode(2);

    rootSpec.getGenes().put(1, new GeneNode(1));
    rootSpec.getGenes().put(2, new GeneNode(1));

    leftSpec.getGenes().put(1, new GeneNode(1));
    leftSpec.getGenes().put(2, new GeneNode(2));

    rightSpec.getGenes().put(1, new GeneNode(1));
    rightSpec.getGenes().put(2, new GeneNode(2));

    rootSpec.setLeftChild(leftSpec);
    rootSpec.setRightChild(rightSpec);

    rootSpec.getGenes().get(1).setLeftChild(leftSpec.getGenes().get(1));
    rootSpec.getGenes().get(1).setRightChild(rightSpec.getGenes().get(1));
    rootSpec.getGenes().get(2).setLeftChild(leftSpec.getGenes().get(2));
    rootSpec.getGenes().get(2).setRightChild(rightSpec.getGenes().get(2));


    // Act
    SpeciesNode newSpec = LogfileParser.performSpeciation(rightSpec, 2, 1, 5.);

    // Assert
    assertEquals("Left sibling of new node should be input node",
        newSpec.getGenes().get(1).getParent().getLeftChild(),
        rightSpec.getGenes().get(1));
    assertEquals("Right sibling of input node should be new node",
        rightSpec.getGenes().get(1).getParent().getRightChild(),
        newSpec.getGenes().get(1));
  }

  @Test
  public void eachGeneInNewSpeciesShouldKnowItsSpecies() {
    // Arrange
    SpeciesNode rootSpec = new SpeciesNode(1);
    SpeciesNode leftSpec = new SpeciesNode(1);
    SpeciesNode rightSpec = new SpeciesNode(2);

    rootSpec.getGenes().put(1, new GeneNode(1));
    rootSpec.getGenes().get(1).setSpeciesNode(rootSpec);

    leftSpec.getGenes().put(1, new GeneNode(1));
    leftSpec.getGenes().get(1).setSpeciesNode(leftSpec);

    rightSpec.getGenes().put(1, new GeneNode(1));
    rightSpec.getGenes().get(1).setSpeciesNode(rightSpec);

    rootSpec.setLeftChild(leftSpec);
    rootSpec.setRightChild(rightSpec);

    rootSpec.getGenes().get(1).setLeftChild(leftSpec.getGenes().get(1));
    rootSpec.getGenes().get(1).setRightChild(rightSpec.getGenes().get(1));

    // Act
    SpeciesNode newSpec = LogfileParser.performSpeciation(leftSpec, 2, 1, 5.);

    // Assert
    assertEquals("Gene Nodes in speciation event node (inner node) should now the speciation node",
        ((SpeciesNode) newSpec.getParent()).getGenes().firstEntry().getValue().getSpeciesNode(),
        newSpec.getParent());
    assertEquals("Genes of new species should now that species",
        newSpec.getGenes().get(1).getSpeciesNode(), newSpec);
    assertEquals("Genes of existing species should now that species",
        leftSpec.getGenes().get(1).getSpeciesNode(), leftSpec);
  }

  @Test
  public void edgeEventsOfExistingNodeShouldBelongToInnerNodeAfterSpeciation() {
    // Arrange
    SpeciesNode rootSpec = new SpeciesNode(1);
    rootSpec.getGenes().put(1, new GeneNode(1));
    rootSpec.getGenes().get(1).setSpeciesNode(rootSpec);

    SpeciesNode leftSpec = new SpeciesNode(1);
    leftSpec.getGenes().put(1, new GeneNode(1));
    leftSpec.getGenes().get(1).setSpeciesNode(leftSpec);

    leftSpec.addEdgeEvent(new GeneNode(1));

    SpeciesNode rightSpec = new SpeciesNode(2);
    rightSpec.getGenes().put(1, new GeneNode(1));
    rightSpec.getGenes().get(1).setSpeciesNode(rightSpec);

    rootSpec.setLeftChild(leftSpec);
    rootSpec.setRightChild(rightSpec);

    rootSpec.getGenes().get(1).setLeftChild(leftSpec.getGenes().get(1));
    rootSpec.getGenes().get(1).setRightChild(rightSpec.getGenes().get(1));

    // Act
    TreeSet<GeneNode> edgeEventsOfSpec = leftSpec.getEdgeEvents();
    SpeciesNode newSpec = LogfileParser.performSpeciation(leftSpec, 2, 1, 5.);

    // Assert
    assertEquals("New inner node should contain the edge events that belonged to the existing "
            + "species after the speciation occured",
        edgeEventsOfSpec,
        ((SpeciesNode) leftSpec.getParent()).getEdgeEvents());
    assertTrue("Edge events of given species node should be empty",
        leftSpec.getEdgeEvents().isEmpty());
    assertTrue("Edge events of new species node should be empty",
        newSpec.getEdgeEvents().isEmpty());
  }

  @Test
  public void checkCorrectnessOfSpeciationEvent() {
    // Arrange
    SpeciesNode rootSpec = new SpeciesNode(1);
    SpeciesNode leftSpec = new SpeciesNode(1);
    SpeciesNode rightSpec = new SpeciesNode(2);

    rootSpec.getGenes().put(1, new GeneNode(1));
    rootSpec.getGenes().get(1).setSpeciesNode(rootSpec);

    leftSpec.getGenes().put(1, new GeneNode(1));
    leftSpec.getGenes().get(1).setSpeciesNode(leftSpec);

    rightSpec.getGenes().put(1, new GeneNode(1));
    rightSpec.getGenes().get(1).setSpeciesNode(rightSpec);

    rootSpec.setLeftChild(leftSpec);
    rootSpec.setRightChild(rightSpec);

    rootSpec.getGenes().get(1).setLeftChild(leftSpec.getGenes().get(1));
    rootSpec.getGenes().get(1).setRightChild(rightSpec.getGenes().get(1));

    // Act
    SpeciesNode newSpec = LogfileParser.performSpeciation(leftSpec, 2, 1, 5.);
    Event event = ((SpeciesNode) leftSpec.getParent()).getGenes().firstEntry().getValue()
        .getEvent();

    // Assert
    assertEquals(
        ((SpeciationEvent) event).getNewSpec(),
        newSpec);
    assertNotNull("New inner node should contain an event", event);
    assertTrue("Event should be a speciation", event instanceof SpeciationEvent);
    assertEquals("Event should be a speciation",event.getType(), EventType.SPEC);
    assertEquals(((SpeciationEvent) event).getExistingSpec(), leftSpec);

  }

  /*==============================================================================================*/
  /* Tests for performDuplication()                                                               */
  /*==============================================================================================*/

  @Test
  public void checkNewGeneName() {
    // Arrange
    SpeciesNode specNode = new SpeciesNode(1);
    GeneNode geneNode = new GeneNode(1);
    geneNode.setSpeciesNode(specNode);

    // Act
    LogfileParser.performDuplication(geneNode, 2, 0, 5.);

    // Assert
    assertEquals(2, geneNode.getParent().getRightChild().getId());
    assertEquals("SE001/00002", geneNode.getParent().getRightChild().toString());
  }

  @Test
  public void checkNewInnerDuplicationNodeName() {
    // Arrange
    SpeciesNode specNode = new SpeciesNode(1);
    GeneNode geneNode = new GeneNode(1);
    geneNode.setSpeciesNode(specNode);

    // Act
    LogfileParser.performDuplication(geneNode, 2, 12, 5.);

    // Assert
    assertEquals(13, geneNode.getParent().getId());
    assertEquals("D013", geneNode.getParent().toString());
  }

  /**
   * Test that duplication creates a gene triplet.
   * Test case: Input gene is root (should only occur in rare occasions)
   */
  @Test
  public void duplicationShouldYieldTriplet1() {
    // Arrange
    SpeciesNode specNode = new SpeciesNode(1);
    GeneNode geneNode = new GeneNode(1);
    geneNode.setSpeciesNode(specNode);

    // Act
    LogfileParser.performDuplication(geneNode, 2, 0, 5.);

    // Assert
    assertEquals("Input node should be left child of its new parent",
        geneNode.getParent().getLeftChild(), geneNode);
    assertNotNull("Right sibling of input node should be another node",
        geneNode.getParent().getRightChild());
  }

  /**
   * Test that duplication creates a gene triplet.
   * Test case: Input gene is NOT root
   */
  @Test
  public void duplicationShouldYieldTriplet2() {
    // Arrange
    SpeciesNode specNode = new SpeciesNode(1);
    GeneNode geneNode1 = new GeneNode(1);
    GeneNode geneNode2 = new GeneNode(2);
    geneNode1.setSpeciesNode(specNode);
    geneNode2.setSpeciesNode(specNode);
    geneNode2.setParent(geneNode1);

    // Act
    LogfileParser.performDuplication(geneNode2, 3, 0, 5.);

    // Assert
    assertEquals("Input node should be left child of its new parent",
        geneNode2.getParent().getLeftChild(), geneNode2);
    assertNotNull("Right sibling of input node should be another node",
        geneNode2.getParent().getRightChild());
  }

  @Test
  public void newGeneNodeShouldBelongToSameSpeciesNode() {
    // Arange
    SpeciesNode specNode = new SpeciesNode(1);
    GeneNode geneNode = new GeneNode(1);
    geneNode.setSpeciesNode(specNode);

    // Act
    LogfileParser.performDuplication(geneNode, 2, 0, 5.);

    // Assert
    GeneNode newLeafNode = (GeneNode) geneNode.getParent().getRightChild();
    assertEquals("New gene node should belong to the same species as the given one",
        geneNode.getSpeciesNode(), newLeafNode.getSpeciesNode());
  }

  @Test
  public void duplicationEventShouldBelongToEdgeEventListOfSpecies() {
    // Arange
    SpeciesNode specNode = new SpeciesNode(1);
    GeneNode geneNode1 = new GeneNode(1);
    GeneNode geneNode2 = new GeneNode(2);
    geneNode1.setSpeciesNode(specNode);
    geneNode2.setSpeciesNode(specNode);
    geneNode2.setParent(geneNode1);

    // Act
    LogfileParser.performDuplication(geneNode2, 3, 0, 5.);

    // Assert
    assertTrue("Edge event list of species should contain new inner node",
        geneNode2.getSpeciesNode().getEdgeEvents().contains(geneNode2.getParent()));
  }

  @Test
  public void checkCorrectnessOfDuplicationEvent() {
    // Arange
    SpeciesNode specNode = new SpeciesNode(1);
    GeneNode geneNode = new GeneNode(1);
    geneNode.setSpeciesNode(specNode);

    // Act
    LogfileParser.performDuplication(geneNode, 2, 0, 5.);

    // Assert
    GeneNode newLeafNode = (GeneNode) geneNode.getParent().getRightChild();
    assertNotNull("New inner node should contain an event",
        ((GeneNode) newLeafNode.getParent()).getEvent());
    assertTrue("Event should be a duplication",
        ((GeneNode) newLeafNode.getParent()).getEvent() instanceof DuplicationEvent);
    assertEquals("Event should be a duplication",
        ((GeneNode) newLeafNode.getParent()).getEvent().getType(), EventType.DUP);
    assertEquals(((DuplicationEvent) ((GeneNode) geneNode.getParent()).getEvent()).getDonorGene(),
        geneNode);
    assertEquals(((DuplicationEvent) ((GeneNode) geneNode.getParent()).getEvent()).getNewGene(),
        newLeafNode);
  }

  /*==============================================================================================*/
  /* Tests for performGeneLoss                                                                    */
  /*==============================================================================================*/
  /*.............root.............root
   *...........//....\\
   *..........A........B..........grandparentEvents
   *........//.\\....//.\\
   *.......Al...Ar..Bl...Br.......parentEvents
   *.......|....|...|....|
   *......al....ar.bl....br.......Genes
   */
  @Test
  public void performGeneLossParentIsSpeciationWithSibling() {
    //Arrange
    SpeciesNode rootSpec = new SpeciesNode(0);

    GeneNode rootEvent = new GeneNode(0, 0, 0);
    rootSpec.getGenes().put(rootEvent.getId(), rootEvent);
    rootEvent.setParent(null);
    rootEvent.setSpeciesNode(rootSpec);

    SpeciesNode specA = new SpeciesNode(1);
    SpeciesNode specB = new SpeciesNode(2);
    rootEvent.setEvent(new SpeciationEvent(specA, specB));


    GeneNode geneA = new GeneNode(1, 1, 1);
    geneA.setParent(rootEvent);
    geneA.setSpeciesNode(specA);
    specA.getGenes().put(geneA.getId(), geneA);


    GeneNode geneB = new GeneNode(2,2,2);
    geneB.setParent(rootEvent);
    geneB.setSpeciesNode(specB);
    specB.getGenes().put(geneB.getId(),geneB);

    rootEvent.setRightChild(geneB);
    rootEvent.setLeftChild(geneA);



    //Act
    try {
      LogfileParser.performGeneLoss(geneA);
    } catch (IllegalTreeStateException itse) {
      itse.getMessage();
    }


    //Assert
    assertEquals("Left child of rootEvent is null.",
        rootEvent.getLeftChild(), null);

  }

  @Test
  public void performGeneLossParentIsSpeciationNoSibling() {
    //Arrange
    SpeciesNode rootSpec = new SpeciesNode(0);

    GeneNode rootEvent = new GeneNode(1);
    rootSpec.getGenes().put(rootEvent.getId(), rootEvent);
    rootEvent.setParent(null);
    rootEvent.setSpeciesNode(rootSpec);

    SpeciesNode specA = new SpeciesNode(2);
    SpeciesNode specB = new SpeciesNode(3);
    rootEvent.setEvent(new SpeciationEvent(specA, specB));

    GeneNode eventA = new GeneNode(4);
    eventA.setParent(rootEvent);
    rootEvent.setLeftChild(eventA);
    rootEvent.setRightChild(new GeneNode());
    specA.getGenes().put(eventA.getId(), eventA);
    eventA.setSpeciesNode(specA);

    SpeciesNode specALeft = new SpeciesNode(5);
    SpeciesNode specARight = new SpeciesNode(6);
    eventA.setEvent(new SpeciationEvent(specALeft, specARight));

    GeneNode geneALeft = new GeneNode(7);
    geneALeft.setParent(eventA);
    eventA.setLeftChild(geneALeft);
    geneALeft.setSpeciesNode(specALeft);
    specALeft.getGenes().put(geneALeft.getId(), geneALeft);

    eventA.setRightChild(null);

    //Act
    try {
      LogfileParser.performGeneLoss(geneALeft);
    } catch (IllegalTreeStateException itse) {
      itse.getMessage();
    }

    //Assert
    assertFalse("specA does not contain eventA anymore (cascaded deletion)",
        specA.getGenes().containsValue(geneALeft));
    assertEquals("Left Child (formerly eventA) of root should be null.",
        rootEvent.getLeftChild(), null);

  }


  @Test
  public void deleteEventDuplicationWithSibling() {
    //Arrange
    SpeciesNode rootSpec = new SpeciesNode(0);

    GeneNode rootEvent = new GeneNode(0, 0, 0);
    rootSpec.getGenes().put(rootEvent.getId(), rootEvent);
    rootEvent.setParent(null);
    rootEvent.setSpeciesNode(rootSpec);

    SpeciesNode specA = new SpeciesNode(1);

    GeneNode geneA = new GeneNode(1, 1, 1);
    geneA.setParent(rootEvent);
    geneA.setSpeciesNode(specA);
    specA.getGenes().put(geneA.getId(), geneA);

    GeneNode geneB = new GeneNode(2, 2, 2);
    geneB.setParent(rootEvent);
    geneB.setSpeciesNode(specA);

    rootEvent.setEvent(new DuplicationEvent(geneA, geneB));
    rootEvent.setRightChild(geneB);
    rootEvent.setLeftChild(geneA);

    //Act
    try {
      LogfileParser.performGeneLoss(geneA);
    } catch (IllegalTreeStateException itse) {
      itse.getMessage();
    }

    //Assert
    assertFalse("specA does not contain geneA anymore (cascaded deletion)",
        specA.getGenes().containsValue(geneA));
    assertEquals("Left Child (formerly geneA) of root should be null.",
        rootEvent.getLeftChild(), null);
  }

  /*           root
  *             ___
  * ___________(_O_)___________
  * I            |            I
  * I +----------+--------+   I
  * I |        _________  |   I
  * I | +------I-------I--L1  I
  * I | |      I       I  |   I
  * I | |  +---I-------I--L2  I
  * I_|_|__|___I       I  |   I
  * ( a b1 |   )       I__|___I
  * (______b2__)       (__b___)
  *     A                 B
  * */
  @Test
  public void cascadeDeletionOfTwoLgts() {
    //Arrange
    SpeciesNode rootSpec = new SpeciesNode(0);

    GeneNode rootEvent = new GeneNode(1);
    rootSpec.getGenes().put(rootEvent.getId(), rootEvent);
    rootEvent.setParent(null);
    rootEvent.setSpeciesNode(rootSpec);

    SpeciesNode specA = new SpeciesNode(2);

    GeneNode geneA = new GeneNode(3);
    GeneNode geneB1 = new GeneNode(4);
    GeneNode geneB2 = new GeneNode(5);

    specA.getGenes().put(geneA.getId(), geneA);
    specA.getGenes().put(geneB1.getId(), geneB1);
    specA.getGenes().put(geneB2.getId(), geneB2);

    geneA.setParent(rootEvent);
    rootEvent.setLeftChild(geneA);
    geneA.setSpeciesNode(specA);

    SpeciesNode specB = new SpeciesNode(6);

    rootEvent.setEvent(new SpeciationEvent(specA, specB));

    GeneNode geneB = new GeneNode(5);
    GeneNode lgt1 = new GeneNode(1, 1, 6);
    lgt1.setSpeciesNode(specB);
    GeneNode lgt2 = new GeneNode(1, 2, 7);
    lgt2.setSpeciesNode(specB);

    specB.getGenes().put(geneB.getId(), geneB);
    specB.addEdgeEvent(lgt1);
    specB.addEdgeEvent(lgt2);
    geneB.setSpeciesNode(specB);

    lgt1.setEvent(new LateralGeneTransferEvent(specB, geneB, specA, geneB1));
    lgt2.setEvent(new LateralGeneTransferEvent(specB, geneB, specA, geneB2));

    lgt1.setRightChild(lgt2);
    lgt1.setLeftChild(geneB1);
    lgt1.setParent(rootEvent);
    rootEvent.setRightChild(lgt1);

    lgt2.setRightChild(geneB);
    lgt2.setLeftChild(geneB2);
    lgt2.setParent(lgt1);

    geneA.setParent(rootEvent);
    geneB.setParent(lgt2);
    geneB1.setParent(lgt1);
    geneB1.setSpeciesNode(specA);
    geneB2.setParent(lgt2);
    geneB2.setSpeciesNode(specA);

    //Act
    try {
      LogfileParser.performGeneLoss(geneB);
      LogfileParser.performGeneLoss(geneB2);
    } catch (IllegalTreeStateException itse) {
      itse.printStackTrace();
    }

    //Assert
    assertEquals("specB has only one edge event", 1, specB.getEdgeEvents().size());
    assertTrue("specB still contains L1", specB.getEdgeEvents().contains(lgt1));
    assertTrue("specB has no more genes", specB.getGenes().isEmpty());
    assertTrue("specA only contains geneA and geneB1",
        specA.getGenes().size() == 2 && specA.getGenes().containsValue(geneA)
        && specA.getGenes().containsValue(geneB1));
    assertEquals("L1s rightChild is null",
        lgt1.getRightChild(), null);
  }


  @Test(expected = IllegalTreeStateException.class)
  public void performGeneLossRootGene() throws IllegalTreeStateException {
    //Arrange
    SpeciesNode rootSpec = new SpeciesNode(0);

    GeneNode rootGene = new GeneNode(0, 0, 0);
    rootSpec.getGenes().put(rootGene.getId(), rootGene);
    rootGene.setRightChild(null);
    rootGene.setLeftChild(null);
    rootGene.setSpeciesNode(rootSpec);

    //Act
    LogfileParser.performGeneLoss(rootGene);
  }

  @Test(expected = IllegalTreeStateException.class)
  public void performGeneLossNull() throws IllegalTreeStateException {
    //Act
    LogfileParser.performGeneLoss(null);
  }

  /*==============================================================================================*/
  /* Tests the retrieval of distances for leaf nodes                                              */
  /*==============================================================================================*/
  @Test
  public void testLeafDistanceExtractionForSpeciesTree() {
    // Arrange
    List<SpeciesNode> species = new ArrayList<SpeciesNode>();
    species.add(new SpeciesNode(1));
    species.add(new SpeciesNode(2));
    species.add(new SpeciesNode(3));
    species.add(new SpeciesNode(4));
    species.add(new SpeciesNode(5));


    species.get(1 - 1).setParent(new SpeciesNode(48.047977, 48.0480, 4));
    species.get(5 - 1).setParent(species.get(1 - 1).getParent());
    species.get(3 - 1).setParent(new SpeciesNode(22.646547, 34.4730, 3));
    species.get(4 - 1).setParent(species.get(3 - 1).getParent());
    species.get(2 - 1).setParent(new SpeciesNode(11.826483, 11.826483, 2));
    species.get(3 - 1).getParent().setParent(species.get(2 - 1).getParent());

    // Act
    try {
      String drwFile = "src" + File.separator + "test" + File.separator + "resources"
          + File.separator + "alf_examples" + File.separator + "test_simulation_S5_G1"
          + File.separator + "RealTree.drw";
      LogfileParser.getLeafDistancesFromSpeciesTree(species, new File(drwFile));
    } catch (IOException e) {
      System.out.println("File does not exist");
      e.printStackTrace();
    }

    // Assert
    assertEquals(12.405798, species.get(1 - 1).getDistanceToParent(), 1.e-4);
    assertEquals(48.0480 + 12.405798, species.get(1 - 1).getDistanceToRoot(), 1.e-4);
  }

  @Test
  public void testLeafDistanceExtractionForGeneTree() {
    // Arrange
    List<SpeciesNode> species = new ArrayList<SpeciesNode>();
    final SpeciesNode species1 = new SpeciesNode(1);
    final SpeciesNode species2 = new SpeciesNode(2);
    final SpeciesNode species3 = new SpeciesNode(3);
    final SpeciesNode species4 = new SpeciesNode(4);
    final SpeciesNode species5 = new SpeciesNode(5);
    species.add(species1);
    species.add(species2);
    species.add(species3);
    species.add(species4);
    species.add(species5);

    final GeneNode gene1in1 = new GeneNode(1);
    final GeneNode gene2in1 = new GeneNode(2);
    final GeneNode gene3in1 = new GeneNode(3);
    final GeneNode gene5in1 = new GeneNode(5);
    species1.getGenes().put(1, gene1in1);
    species1.getGenes().put(2, gene2in1);
    species1.getGenes().put(3, gene3in1);
    species1.getGenes().put(5, gene5in1);

    final GeneNode gene1in2 = new GeneNode(1);
    species2.getGenes().put(1, gene1in2);

    final GeneNode gene1in3 = new GeneNode(1);
    species3.getGenes().put(1, gene1in3);

    final GeneNode gene1in4 = new GeneNode(1);
    species4.getGenes().put(1, gene1in4);

    final GeneNode gene1in5 = new GeneNode(1);
    final GeneNode gene2in5 = new GeneNode(2);
    final GeneNode gene3in5 = new GeneNode(3);
    final GeneNode gene4in5 = new GeneNode(4);
    species5.getGenes().put(1, gene1in5);
    species5.getGenes().put(2, gene2in5);
    species5.getGenes().put(3, gene3in5);
    species5.getGenes().put(4, gene4in5);

    final GeneNode inner1inS4 = new GeneNode(0, 48.0480, 4);
    final GeneNode inner2inS4 = new GeneNode(0, 48.0480,4);
    final GeneNode inner3inS4 = new GeneNode(0, 48.0480, 4);
    final GeneNode innerD1 = new GeneNode(0, 59.1260, 4);
    final GeneNode innerL3 = new GeneNode(0, 60.4854, 3);
    final GeneNode inner1inS3 = new GeneNode(0, 34.4730, 3);

    inner1inS4.setLeftChild(gene1in1);
    inner1inS4.setRightChild(innerD1);
    inner2inS4.setLeftChild(gene2in1);
    inner2inS4.setRightChild(gene2in5);
    inner3inS4.setLeftChild(gene3in1);
    inner3inS4.setRightChild(gene3in5);
    innerD1.setLeftChild(gene1in5);
    innerD1.setRightChild(gene4in5);
    innerL3.setLeftChild(gene1in2);
    innerL3.setRightChild(gene5in1);
    inner1inS3.setLeftChild(gene1in3);
    inner1inS3.setRightChild(gene1in4);

    // Act
    try {
      String drwFile = "src" + File.separator + "test" + File.separator + "resources"
          + File.separator + "alf_examples" + File.separator + "test_simulation_S5_G1"
          + File.separator + "GeneTrees" + File.separator + "GeneTree1.drw";
      LogfileParser.getLeafDistancesFromGeneTree(species, new File(drwFile));
    } catch (IOException e) {
      System.out.println("File does not exist");
      e.printStackTrace();
    }

    // Assert
    assertEquals(21.506542, gene1in1.getDistanceToParent(), 1.e-4);
    assertEquals(69.5545, gene1in1.getDistanceToRoot(), 1.e-4);
    assertEquals(21.506542, gene2in1.getDistanceToParent(), 1.e-4);
    assertEquals(69.5545, gene2in1.getDistanceToRoot(), 1.e-4);
    assertEquals(21.506542, gene3in1.getDistanceToParent(), 1.e-4);
    assertEquals(69.5545, gene3in1.getDistanceToRoot(), 1.e-4);
    assertEquals(9.0691446, gene5in1.getDistanceToParent(), 1.e-4);
    assertEquals(69.5545, gene5in1.getDistanceToRoot(), 1.e-4);
    assertEquals(9.0691446, gene1in2.getDistanceToParent(), 1.e-4);
    assertEquals(69.5545, gene1in2.getDistanceToRoot(), 1.e-4);
    assertEquals(35.081489, gene1in3.getDistanceToParent(), 1.e-4);
    assertEquals(69.5545, gene1in3.getDistanceToRoot(), 1.e-4);
    assertEquals(35.081489, gene1in4.getDistanceToParent(), 1.e-4);
    assertEquals(69.5545, gene1in4.getDistanceToRoot(), 1.e-4);
    assertEquals(10.428535, gene1in5.getDistanceToParent(), 1.e-4);
    assertEquals(69.5545, gene1in5.getDistanceToRoot(), 1.e-4);
    assertEquals(21.506542, gene2in5.getDistanceToParent(), 1.e-4);
    assertEquals(69.5545, gene2in5.getDistanceToRoot(), 1.e-4);
    assertEquals(21.506542, gene3in5.getDistanceToParent(), 1.e-4);
    assertEquals(69.5545, gene3in5.getDistanceToRoot(), 1.e-4);
    assertEquals(10.428535, gene4in5.getDistanceToParent(), 1.e-4);
    assertEquals(69.5545, gene4in5.getDistanceToRoot(), 1.e-4);
  }

  /*==============================================================================================*/
  /* Tests for performLateralGeneTransfer                                             */
  /*==============================================================================================*/
  @Test
  public void performLgtShouldYieldTriplet() {
    //Arrange
    GeneNode donorGene = new GeneNode(0);

    SpeciesNode donorSpecies = new SpeciesNode(66);

    donorGene.setSpeciesNode(donorSpecies);

    GeneNode parentNode = new GeneNode(2);
    parentNode.setDistanceToRoot(11);
    parentNode.setLeftChild(donorGene);

    SpeciesNode targetSpecies = new SpeciesNode(1);

    //Act
    LogfileParser.performLateralGeneTransfer(
        donorGene, targetSpecies, 2, 3, 0);
    GeneNode newAcceptorGene = (donorGene.getParent().getLeftChild() == donorGene)
        ? (GeneNode) donorGene.getParent().getRightChild()
        : (GeneNode) donorGene.getParent().getLeftChild();

    //Assert
    assertNotNull("parent of donor gene should not be null",
        donorGene.getParent());
    assertNotNull("sibling should not be null",
        newAcceptorGene);
    assertNotEquals("siblings should not match",
        newAcceptorGene, donorGene);

  }

  @Test
  public void performLgtEventNodeShouldReplaceDonorGeneAsChild() {
    //Arrange
    GeneNode parentEvent = new GeneNode(0);
    GeneNode donorGene = new GeneNode(1);
    SpeciesNode targetSpecies = new SpeciesNode(3);

    parentEvent.setLeftChild(donorGene);

    SpeciesNode donorSpecies = new SpeciesNode(66);

    donorGene.setSpeciesNode(donorSpecies);

    //Act
    LogfileParser.performLateralGeneTransfer(
        donorGene, targetSpecies, 2, 3, 0);

    //Assert
    assertSame("parentEvent's leftChild should be donorGene's new parent",
        donorGene.getParent(), parentEvent.getLeftChild());
  }

  @Test
  public void performLgtCheckIDs() {
    //Arrange
    int parentId = 11;
    int donorId = 22;
    int givenId = 33;
    int specId = 44;
    int lgtCount = 55;

    GeneNode parentEvent = new GeneNode(parentId);
    GeneNode donorGene = new GeneNode(donorId);
    SpeciesNode targetSpecies = new SpeciesNode(specId);

    parentEvent.setLeftChild(donorGene);

    SpeciesNode donorSpecies = new SpeciesNode(66);

    donorGene.setSpeciesNode(donorSpecies);

    //Act
    LogfileParser.performLateralGeneTransfer(
        donorGene, targetSpecies, givenId, lgtCount, 0);
    int newGeneId;
    GeneNode newParent = (GeneNode) donorGene.getParent();
    newGeneId = donorGene == newParent.getRightChild()
        ? newParent.getLeftChild().getId()
        : newParent.getRightChild().getId();

    //Assert
    assertEquals("givenId should be donor's sibling's ID", givenId, newGeneId);
    assertEquals("lgtCount + 1 should be donorGene's parent's ID",
        lgtCount + 1, donorGene.getParent().getId());
    assertNotEquals("donor gene should have different ID than it's sibling",
        donorGene.getId(), newGeneId);
  }

  @Test
  public void performLgtCheckEventsAndSpecies() {
    //Arrange
    GeneNode parentEvent = new GeneNode(11);
    GeneNode donorGene = new GeneNode(22);

    SpeciesNode donorSpec = new SpeciesNode(33);

    parentEvent.setLeftChild(donorGene);

    donorGene.setSpeciesNode(donorSpec);

    donorSpec.addEdgeEvent(parentEvent);
    donorSpec.getGenes().put(donorGene.getId(), donorGene);

    SpeciesNode targetSpecies = new SpeciesNode(44);

    //Act
    LogfileParser.performLateralGeneTransfer(
        donorGene, targetSpecies, 55, 65, 0);
    GeneNode newParentEvent = (GeneNode) donorGene.getParent();
    GeneNode newGene = (GeneNode) (donorGene == newParentEvent.getRightChild()
        ? newParentEvent.getLeftChild()
        : newParentEvent.getRightChild());

    //Assert
    assertTrue("newGene should be in gene list of target species",
        targetSpecies.getGenes().containsValue(newGene));
    assertSame("newGene's species node should match the target species",
        newGene.getSpeciesNode(), targetSpecies);
    assertTrue("newParentEvent should be contained in donorSpec edge events",
        donorSpec.getEdgeEvents().contains(newParentEvent));
    assertSame("newParentEvent species node should match donor species",
        newParentEvent.getSpeciesNode(), donorGene.getSpeciesNode());
  }

  @Test
  public void performLgtCheckEvent() {
    //Arrange
    GeneNode parentEvent = new GeneNode(11);
    GeneNode donorGene = new GeneNode(22);

    SpeciesNode donorSpec = new SpeciesNode(33);

    parentEvent.setLeftChild(donorGene);

    donorGene.setSpeciesNode(donorSpec);

    donorSpec.addEdgeEvent(parentEvent);
    donorSpec.getGenes().put(donorGene.getId(), donorGene);

    SpeciesNode targetSpecies = new SpeciesNode(44);

    //Act
    LogfileParser.performLateralGeneTransfer(
        donorGene, targetSpecies, 55, 65, 0);
    GeneNode newParentEvent = (GeneNode) donorGene.getParent();
    GeneNode newGene = (GeneNode) (donorGene == newParentEvent.getRightChild()
        ? newParentEvent.getLeftChild()
        : newParentEvent.getRightChild());
    LateralGeneTransferEvent lgtEvent = (LateralGeneTransferEvent) newParentEvent.getEvent();

    //Assert
    assertSame("donor gene should be the same for lgtEvent",
        donorGene, lgtEvent.getDonorGene());
    assertSame("donor species should be the same for lgtEvent",
        donorSpec, lgtEvent.getDonorSpec());
    assertSame("new gene should be the same for lgtEvent",
        newGene, lgtEvent.getNewGene());
    assertSame("target species should match lgtEvent's acceptor species",
        targetSpecies, lgtEvent.getAcceptorSpec());
  }

  @Test
  public void performLgtEvaluateDistances() {
    //Arrange

    GeneNode parentEvent = new GeneNode(11);
    GeneNode donorGene = new GeneNode(22);

    SpeciesNode donorSpec = new SpeciesNode(33);

    parentEvent.setLeftChild(donorGene);
    parentEvent.setDistanceToRoot(123.456);

    donorGene.setSpeciesNode(donorSpec);

    donorSpec.addEdgeEvent(parentEvent);
    donorSpec.getGenes().put(donorGene.getId(), donorGene);

    SpeciesNode targetSpecies = new SpeciesNode(44);

    double time = 666.666;

    //Act
    LogfileParser.performLateralGeneTransfer(
        donorGene, targetSpecies, 55, 65, time);
    GeneNode newParentEvent = (GeneNode) donorGene.getParent();

    //Assert
    assertEquals("newParentEvent's distance to root should match time",
        time, newParentEvent.getDistanceToRoot(), 10e-4);
    assertEquals("newParentEvent's distance to parent should be as expected",
        time - parentEvent.getDistanceToRoot(), newParentEvent.getDistanceToParent(),
        10e-4);

  }
}
