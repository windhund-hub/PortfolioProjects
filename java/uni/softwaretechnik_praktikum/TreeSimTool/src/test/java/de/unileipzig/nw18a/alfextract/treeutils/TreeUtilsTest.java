package de.unileipzig.nw18a.alfextract.treeutils;

import static de.unileipzig.nw18a.alfextract.treeutils.TreeUtils.buildLastCommonAncestor;
import static de.unileipzig.nw18a.alfextract.treeutils.TreeUtils.getDistances;

import de.unileipzig.nw18a.alfextract.treemapping.DuplicationEvent;
import de.unileipzig.nw18a.alfextract.treemapping.GeneNode;
import de.unileipzig.nw18a.alfextract.treemapping.LateralGeneTransferEvent;
import de.unileipzig.nw18a.alfextract.treemapping.SpeciationEvent;
import de.unileipzig.nw18a.alfextract.treemapping.SpeciesNode;
import de.unileipzig.nw18a.commontypes.GeneDistances;
import de.unileipzig.nw18a.commontypes.GeneRelation;
import de.unileipzig.nw18a.commontypes.GeneRelationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


/**
 * Testclass for class TreeUtils.
 */
public class TreeUtilsTest {

  /**
   * Test for method getLastCommonAncestor.
   * Case parameters are from different subclasses of AbstractNode.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetLastCommonAncestorIllegalArgumentException() throws IllegalTreeStateException {
    //Arrange
    SpeciesNode spec = new SpeciesNode();
    GeneNode gen = new GeneNode();
    //Act
    TreeUtils.getLastCommonAncestor(spec, gen);
  }

  /**
   * Test for method getLastCommonAncestor.
   * Case that distanceToParent is at one point not decreasing.
   * Constructed case root is separated from circle.
   */
  @Test(expected = IllegalTreeStateException.class)
  public void testGetLastCommonAncestorCircleInTreeException() throws IllegalTreeStateException {
    //Arrange
    GeneNode fakeRoot = new GeneNode(2, 1, 0);
    GeneNode child1 = new GeneNode(2, 3, 1);
    GeneNode child2 = new GeneNode(2, 5, 2);
    fakeRoot.setParent(child2);
    child1.setParent(fakeRoot);
    child2.setParent(child1);
    GeneNode root = new GeneNode(0);
    //Act
    TreeUtils.getLastCommonAncestor(root, child1);
  }


  /**
   * Test for method getLastCommonAncestor.
   * Case that we receive two different root nodes.
   *                             7
   *                             |
   * +-----5-----+               |
   * |           |               |
   * |        +--4--+            |
   * |        |     |            |
   * 1        2     3            6
   */
  @Test(expected = IllegalTreeStateException.class)
  public void testGetLastCommonAncestorNotConnectedTreeException()
      throws IllegalTreeStateException {
    //Arrange
    GeneNode seven = new GeneNode(7);
    GeneNode five = new GeneNode(0, 0, 5);
    GeneNode six = new GeneNode(9, 9, 6);
    GeneNode one = new GeneNode(4, 4, 1);
    GeneNode four = new GeneNode(2, 2, 4);
    seven.setRightChild(six);
    five.setLeftChild(one);
    five.setRightChild(four);
    GeneNode two = new GeneNode(1, 3, 2);
    four.setLeftChild(two);
    GeneNode three = new GeneNode(1.8765443222209, 3.8765443222209, 3);
    four.setRightChild(three);
    //Act
    TreeUtils.getLastCommonAncestor(one, six);
  }


  /**
   * Test for method getLastCommonAncestor.
   * Cases where lca is not the root (solved inside the while loop).
   *       +----------7----------+
   *       |                     |
   * +-----5-----+               |
   * |           |               |
   * |        +--4--+            |
   * |        |     |            |
   * 1        2     3            6
   */
  @Test
  public void testGetLastCommonAncestor1() throws IllegalTreeStateException {
    //Arrange
    GeneNode seven = new GeneNode(7);
    GeneNode five = new GeneNode(4, 4, 5);
    GeneNode six = new GeneNode(9, 9, 6);
    GeneNode one = new GeneNode(4, 8, 1);
    seven.setLeftChild(five);
    seven.setRightChild(six);
    five.setLeftChild(one);
    GeneNode four = new GeneNode(2, 6, 4);
    GeneNode two = new GeneNode(1, 7, 2);
    five.setRightChild(four);
    four.setLeftChild(two);
    GeneNode three = new GeneNode(1.876, 7.876, 3);
    four.setRightChild(three);
    //Act
    GeneNode result = (GeneNode) TreeUtils.getLastCommonAncestor(two, three);
    Assert.assertEquals(four, result);
    result = (GeneNode) TreeUtils.getLastCommonAncestor(two, four);
    Assert.assertEquals(four, result);
    result = (GeneNode) TreeUtils.getLastCommonAncestor(four, three);
    Assert.assertEquals(four, result);
    result = (GeneNode) TreeUtils.getLastCommonAncestor(one, three);
    Assert.assertEquals(five, result);
    result = (GeneNode) TreeUtils.getLastCommonAncestor(two, one);
    Assert.assertEquals(five, result);
    result = (GeneNode) TreeUtils.getLastCommonAncestor(four, one);
    Assert.assertEquals(five, result);
    result = (GeneNode) TreeUtils.getLastCommonAncestor(one, five);
    Assert.assertEquals(five, result);
    result = (GeneNode) TreeUtils.getLastCommonAncestor(one, one);
    Assert.assertEquals(one, result);
    result = (GeneNode) TreeUtils.getLastCommonAncestor(one, seven);
    Assert.assertEquals(seven, result);
    result = (GeneNode) TreeUtils.getLastCommonAncestor(one, six);
    Assert.assertEquals(seven, result);
    result = (GeneNode) TreeUtils.getLastCommonAncestor(seven, seven);
    Assert.assertEquals(seven, result);

  }

  /**
   * Test for method build Reconciliationbased().
   *
   * @throws IllegalTreeStateException thrown by used method getLastCommonAncestor
   * @throws RuntimeException          thrown by used method getLastCommonAncestor
   */
  @Test
  public void testBuildReconciliationBased() throws IllegalTreeStateException, RuntimeException {
    /**Arange speciestree.
     *         +------E-----+
     *    (4)  |            |
     *    +----D----+       |(6)
     *(2) |         |       |
     *    A         B       C
     */

    SpeciesNode speciesE = new SpeciesNode(0, 7, 104);
    SpeciesNode speciesD = new SpeciesNode(4, 11, 103);
    speciesE.setLeftChild(speciesD);
    SpeciesNode speciesC = new SpeciesNode(6, 13, 102);
    speciesE.setRightChild(speciesC);
    SpeciesNode speciesA = new SpeciesNode(2, 13, 101);
    speciesD.setLeftChild(speciesA);
    SpeciesNode speciesB = new SpeciesNode(2, 13, 100);
    speciesD.setRightChild(speciesB);

    /**Arrange genetrees.
     *                     +---------------e---------------------+      e2       +------e1-----+
     *                     |                                     |               |             |
     *                     m-------------------------------+     |               |             |
     *                     |                               |     |              b2            c3
     *                     n--+                            |     |
     *                     |  |                            |     |
     *                 +---d---------------+               |     |
     *                 |      |            |               |     |
     *                 a1     a2           b               c1    c2
     */

    GeneNode e = new GeneNode(0, 7, 1008);
    e.setSpeciesNode(speciesE);
    e.setEvent(new SpeciationEvent());
    GeneNode m = new GeneNode(2, 9, 1007);
    m.setSpeciesNode(speciesD);
    m.setEvent(new LateralGeneTransferEvent());
    GeneNode c2 = new GeneNode(6, 13, 1004);
    c2.setSpeciesNode(speciesC);
    e.setLeftChild(m);
    e.setRightChild(c2);
    GeneNode c1 = new GeneNode(4, 13, 1003);
    c1.setSpeciesNode(speciesC);
    GeneNode n = new GeneNode(1, 10, 1006);
    n.setSpeciesNode(speciesD);
    n.setEvent(new DuplicationEvent());
    m.setLeftChild(n);
    m.setRightChild(c1);
    GeneNode d = new GeneNode(1, 11, 1005);
    d.setSpeciesNode(speciesD);
    d.setEvent(new SpeciationEvent());
    GeneNode a2 = new GeneNode(3, 13, 1002);
    a2.setSpeciesNode(speciesA);
    n.setLeftChild(d);
    n.setRightChild(a2);
    GeneNode a1 = new GeneNode(2, 13, 1000);
    a1.setSpeciesNode(speciesA);
    GeneNode b = new GeneNode(2, 13, 1001);
    b.setSpeciesNode(speciesB);
    d.setLeftChild(a1);
    d.setRightChild(b);

    GeneNode e1 = new GeneNode(0, 0, 1010);
    GeneNode c3 = new GeneNode(2, 2, 1011);
    GeneNode b2 = new GeneNode(5, 5, 1012);
    c3.setSpeciesNode(speciesC);
    b2.setSpeciesNode(speciesB);
    e1.setSpeciesNode(speciesE);
    e1.setLeftChild(b2);
    e1.setRightChild(c3);
    e1.setEvent(new SpeciationEvent());
    GeneNode e2 = new GeneNode(0, 0, 1020);
    e2.setSpeciesNode(speciesE);
    //add e,e1,e2 to list of genes of speciesE
    speciesE.getGenes().put(0, e);
    speciesE.getGenes().put(1, e1);
    speciesE.getGenes().put(2, e2);

    /** Building matrix geneRelation0.dummy with right items
     *   ( = = > < = )
     *   |   = > < = |
     *   |     = < = |
     *   |       = > )
     *   |         = )
     */
    List<List<Byte>> dummy =
        new ArrayList<List<Byte>>();
    ArrayList<Byte> row1 = new ArrayList<Byte>(Arrays.asList(GeneRelationType.EQUAL,
        GeneRelationType.EQUAL, GeneRelationType.GREATER_THAN, GeneRelationType.LESS_THAN,
        GeneRelationType.EQUAL));
    dummy.add(row1);
    List<Byte> row2 =
        new ArrayList<Byte>(Arrays.asList(GeneRelationType.EQUAL, GeneRelationType.GREATER_THAN,
            GeneRelationType.LESS_THAN, GeneRelationType.EQUAL));
    dummy.add(row2);
    List<Byte> row3 = new ArrayList<Byte>(Arrays.asList(GeneRelationType.EQUAL,
        GeneRelationType.LESS_THAN, GeneRelationType.EQUAL));
    dummy.add(row3);
    List<Byte> row4 =
        new ArrayList<Byte>(Arrays.asList(GeneRelationType.EQUAL, GeneRelationType.GREATER_THAN));
    dummy.add(row4);
    List<Byte> row5 =
        new ArrayList<Byte>(Arrays.asList(GeneRelationType.EQUAL));
    dummy.add(row5);

    /**Small matrix geneRelation1.dummy1
     * (= =)
     * (  =)
     */
    List<List<Byte>> dummy1 =
        new ArrayList<List<Byte>>();
    List<Byte> rowX1 = new ArrayList<Byte>(Arrays.asList(GeneRelationType.EQUAL,
        GeneRelationType.EQUAL));
    List<Byte> rowX2 = new ArrayList<Byte>(Arrays.asList(GeneRelationType.EQUAL));
    dummy1.add(rowX1);
    dummy1.add(rowX2);


    /** geneRelation2.dummy2 has only one entry, because one node is related to itself via equal-rel
     * (=)
     */
    List<List<Byte>> dummy2 =
        new ArrayList<List<Byte>>();
    List<Byte> rowY2 = new ArrayList<Byte>(Arrays.asList(GeneRelationType.EQUAL));
    dummy2.add(rowY2);

    //Act
    List<GeneRelation> relation = TreeUtils.buildReconciliationBased(speciesE);

    //test if object are equal geneRelationk.dummyk with relation.get(k).getRelation()

    Assert.assertEquals(dummy, relation.get(0).getRelation());
    Assert.assertEquals(dummy1, relation.get(1).getRelation());
    Assert.assertEquals(dummy1.get(0), relation.get(1).getRelation().get(0));
    Assert.assertEquals(dummy1.get(1), relation.get(1).getRelation().get(1));
    Assert.assertEquals(dummy2, relation.get(2).getRelation());

    //test if list geneRelationk.genes.getName is equal to relation.get(k).getGenes().get(i)
    // .getName()

    //for geneRelation0,
    // test if Gene was added to the list of species
    // first node a1
    Assert.assertEquals("SE101/01000",
        relation.get(0).getGenes().get(0).getSpecies().getGene(0).getName());
    //for geneRelation0, node c1
    Assert.assertEquals("SE102/01003",
        relation.get(0).getGenes().get(3).getSpecies().getGene(0).getName());
    //for geneRelation0, node b
    Assert.assertEquals("SE100/01001",
        relation.get(0).getGenes().get(1).getSpecies().getGene(0).getName());

    //for geneRelation0, check list genes
    //a1
    Assert.assertEquals("SE101/01000",
        relation.get(0).getGenes().get(0).getName());
    //b
    Assert.assertEquals("SE100/01001",
        relation.get(0).getGenes().get(1).getName());
    //a2
    Assert.assertEquals("SE101/01002",
        relation.get(0).getGenes().get(2).getName());
    //c1
    Assert.assertEquals("SE102/01004",
        relation.get(0).getGenes().get(4).getName());

    //for geneRelation1, check list genes
    //c3
    Assert.assertEquals("SE102/01011",
        relation.get(1).getGenes().get(1).getName());
    //b2
    Assert.assertEquals("SE100/01012",
        relation.get(1).getGenes().get(0).getName());

    //for geneRelation2, check list genes
    //e2
    Assert.assertEquals("SE104/01020",
        relation.get(2).getGenes().get(0).getName());


  }


  /**
   * Test for method buildLastCommonAncestor().
   *
   * @throws IllegalTreeStateException thrown by used method getLastCommonAncestor
   * @throws RuntimeException          thrown by used method getLastCommonAncestor
   */
  @Test
  public void testBuildLastCommonAncestor() throws IllegalTreeStateException, RuntimeException {
    /**Arrange speciestree.
     *         +------E-----+
     *    (4)  |            |
     *    +----D----+       |(6)
     *(2) |         |       |
     *    A         B       C
     */

    SpeciesNode speciesE = new SpeciesNode(0, 7, 104);
    SpeciesNode speciesD = new SpeciesNode(4, 11, 103);
    speciesE.setLeftChild(speciesD);
    SpeciesNode speciesC = new SpeciesNode(6, 13, 102);
    speciesE.setRightChild(speciesC);
    SpeciesNode speciesA = new SpeciesNode(2, 13, 101);
    speciesD.setLeftChild(speciesA);
    SpeciesNode speciesB = new SpeciesNode(2, 13, 100);
    speciesD.setRightChild(speciesB);

    /**Arrange genetree.
     *                     +---------------e---------------------+
     *                  (2)|                                     |
     *                     m-------------------------------+     |
     *                  (1)|                               |     | (6)
     *                     n--+                            |     |
     *                  (1)|  |                            |     |
     *                 +---d---------------+               |     |
     *            (2)  |      | (3)        |               |     |
     *                 a1     a2           b               c1    c2
     */

    GeneNode e = new GeneNode(0, 7, 1008);
    e.setSpeciesNode(speciesE);
    e.setEvent(new SpeciationEvent());
    GeneNode m = new GeneNode(2, 9, 1007);
    m.setSpeciesNode(speciesD);
    m.setEvent(new LateralGeneTransferEvent());
    GeneNode c2 = new GeneNode(6, 13, 1004);
    c2.setSpeciesNode(speciesC);
    e.setLeftChild(m);
    e.setRightChild(c2);
    GeneNode c1 = new GeneNode(4, 13, 1003);
    c1.setSpeciesNode(speciesC);
    GeneNode n = new GeneNode(1, 10, 1006);
    n.setSpeciesNode(speciesD);
    n.setEvent(new DuplicationEvent());
    m.setLeftChild(n);
    m.setRightChild(c1);
    GeneNode d = new GeneNode(1, 11, 1005);
    d.setSpeciesNode(speciesD);
    d.setEvent(new SpeciationEvent());
    GeneNode a2 = new GeneNode(3, 13, 1002);
    a2.setSpeciesNode(speciesA);
    n.setLeftChild(d);
    n.setRightChild(a2);
    GeneNode a1 = new GeneNode(2, 13, 1000);
    a1.setSpeciesNode(speciesA);
    GeneNode b = new GeneNode(2, 13, 1001);
    b.setSpeciesNode(speciesB);
    d.setLeftChild(a1);
    d.setRightChild(b);

    GeneNode e2 = new GeneNode(0, 0, 1020);
    e2.setSpeciesNode(speciesE);
    //add e to list of genes of sE
    speciesE.getGenes().put(0, e);
    speciesE.getGenes().put(1, e2);

    /** Building matrix geneRelation0.dummy with right items
     *   ( L O P X O )
     *   |   L P X O |
     *   |     L X O |
     *   |       L O |
     *   |         L )
     */
    List<List<Byte>> dummy =
        new ArrayList<List<Byte>>();
    List<Byte> row1 =
        new ArrayList<Byte>(Arrays.asList(GeneRelationType.LEAF, GeneRelationType.ORTHO,
            GeneRelationType.PARA, GeneRelationType.XENO, GeneRelationType.ORTHO));
    dummy.add(row1);
    List<Byte> row2 =
        new ArrayList<Byte>(Arrays.asList(GeneRelationType.LEAF, GeneRelationType.PARA,
            GeneRelationType.XENO, GeneRelationType.ORTHO));
    dummy.add(row2);
    List<Byte> row3 = new ArrayList<Byte>(Arrays.asList(GeneRelationType.LEAF,
        GeneRelationType.XENO, GeneRelationType.ORTHO));
    dummy.add(row3);
    List<Byte> row4 =
        new ArrayList<Byte>(Arrays.asList(GeneRelationType.LEAF, GeneRelationType.ORTHO));
    dummy.add(row4);
    List<Byte> row5 =
        new ArrayList<Byte>(Arrays.asList(GeneRelationType.LEAF));
    dummy.add(row5);

    //Act
    ArrayList<GeneRelation> relation = buildLastCommonAncestor(speciesE);
    List<ArrayList<Byte>> dummy1 = new ArrayList<ArrayList<Byte>>();
    dummy1.add(new ArrayList<Byte>(Arrays.asList(GeneRelationType.LEAF)));
    //test if objects are equal: geneRelationk.dummyk with relation.get(k).getRelation()
    Assert.assertEquals(dummy, relation.get(0).getRelation());
    Assert.assertEquals(dummy1, relation.get(1).getRelation());
  }


  /**
   * Test for method buildFitch().
   *
   * @throws IllegalTreeStateException thrown by used method getLastCommonAncestor
   * @throws RuntimeException          thrown by used method getLastCommonAncestor
   */
  @Test
  public void testBuildFitch() throws IllegalTreeStateException, RuntimeException {
    /**Arrange speciestree.
     *         +------E-----+
     *    (4)  |            |
     *    +----D----+       |(6)
     *(2) |         |       |
     *    A         B       C
     */

    SpeciesNode speciesE = new SpeciesNode(0, 7, 104);
    SpeciesNode speciesD = new SpeciesNode(4, 11, 103);
    speciesE.setLeftChild(speciesD);
    SpeciesNode speciesC = new SpeciesNode(6, 13, 102);
    speciesE.setRightChild(speciesC);
    SpeciesNode speciesA = new SpeciesNode(2, 13, 101);
    speciesD.setLeftChild(speciesA);
    SpeciesNode speciesB = new SpeciesNode(2, 13, 100);
    speciesD.setRightChild(speciesB);

    /**Arrange genetree.
     *                     +---------------e---------------------+
     *                     |              (S)                    |
     *               (LGT) m-------------------------------+     |
     *                     |                               |     |
     *                 (D) n--+                            |     |
     *                     |  |                            |     |
     *                 +---d---------------+               |     |
     *            (2)  |  (S) |            |               |     |
     *                 |      |            |               |     |
     *                 |      |     +----- o (LGT)         |     |
     *                 |      |     |      |               |     |
     *                 a1     a2   a3      b               c1    c2
     *
     *
     */

    GeneNode e = new GeneNode(0, 7, 1008);
    e.setSpeciesNode(speciesE);
    e.setEvent(new SpeciationEvent());
    GeneNode m = new GeneNode(2, 9, 1007);
    m.setSpeciesNode(speciesD);
    m.setEvent(new LateralGeneTransferEvent());
    GeneNode c2 = new GeneNode(6, 13, 1004);
    c2.setSpeciesNode(speciesC);
    e.setLeftChild(m);
    e.setRightChild(c2);
    GeneNode c1 = new GeneNode(4, 13, 1003);
    c1.setSpeciesNode(speciesC);
    GeneNode n = new GeneNode(1, 10, 1006);
    n.setSpeciesNode(speciesD);
    n.setEvent(new DuplicationEvent());
    m.setLeftChild(n);
    m.setRightChild(c1);
    GeneNode d = new GeneNode(1, 11, 1005);
    d.setSpeciesNode(speciesD);
    d.setEvent(new SpeciationEvent());
    GeneNode a2 = new GeneNode(3, 13, 1002);
    a2.setSpeciesNode(speciesA);
    n.setLeftChild(d);
    n.setRightChild(a2);
    GeneNode a1 = new GeneNode(2, 13, 1000);
    a1.setSpeciesNode(speciesA);
    GeneNode o = new GeneNode(2, 13, 1010);
    o.setEvent(new LateralGeneTransferEvent());
    o.setSpeciesNode(speciesB);
    GeneNode b = new GeneNode(1, 14, 1001);
    b.setSpeciesNode(speciesB);
    d.setLeftChild(a1);
    d.setRightChild(o);
    GeneNode a3 = new GeneNode(3, 15, 1009);
    a3.setSpeciesNode(speciesA);
    o.setLeftChild(a3);
    o.setRightChild(b);


    /** Building matrix geneRelation0.dummy with right items
     *   ( 0 3 1 2 3 1 )
     *   | 1 0 4 2 3 1 |
     *   | 1 3 0 2 3 1 |
     *   | 2 3 2 0 3 1 |
     *   | 4 3 4 4 0 1 |
     *   | 1 3 1 1 3 0 )
     */
    List<List<Byte>> dummy =
        new ArrayList<List<Byte>>();
    List<Byte> row1 = new ArrayList<Byte>(Arrays.asList(GeneRelationType.LEAF,
        GeneRelationType.XENO, GeneRelationType.ORTHO, GeneRelationType.PARA, GeneRelationType.XENO,
        GeneRelationType.ORTHO));
    dummy.add(row1);
    List<Byte> row2 =
        new ArrayList<Byte>(Arrays.asList(GeneRelationType.ORTHO,
            GeneRelationType.LEAF, GeneRelationType.XENO_LCA, GeneRelationType.PARA,
            GeneRelationType.XENO, GeneRelationType.ORTHO));
    dummy.add(row2);
    List<Byte> row3 = new ArrayList<Byte>(Arrays.asList(GeneRelationType.ORTHO,
        GeneRelationType.XENO, GeneRelationType.LEAF, GeneRelationType.PARA, GeneRelationType.XENO,
        GeneRelationType.ORTHO));
    dummy.add(row3);
    List<Byte> row4 =
        new ArrayList<Byte>(Arrays.asList(GeneRelationType.PARA, GeneRelationType.XENO,
            GeneRelationType.PARA, GeneRelationType.LEAF, GeneRelationType.XENO,
            GeneRelationType.ORTHO));
    dummy.add(row4);
    List<Byte> row5 =
        new ArrayList<Byte>(Arrays.asList(GeneRelationType.XENO_LCA, GeneRelationType.XENO,
            GeneRelationType.XENO_LCA, GeneRelationType.XENO_LCA, GeneRelationType.LEAF,
            GeneRelationType.ORTHO));
    dummy.add(row5);
    List<Byte> row6 =
        new ArrayList<Byte>(Arrays.asList(GeneRelationType.ORTHO, GeneRelationType.XENO,
            GeneRelationType.ORTHO, GeneRelationType.ORTHO, GeneRelationType.XENO,
            GeneRelationType.LEAF));
    dummy.add(row6);

    //isolated node
    GeneNode e2 = new GeneNode(0, 0, 1020);
    e2.setSpeciesNode(speciesC);

    //Building matrix geneRelation1.dummy2 for isolated node e2
    List<List<Byte>> dummy2 = new ArrayList<List<Byte>>();
    List<Byte> rowDummy2 = new ArrayList<Byte>(Arrays.asList(GeneRelationType.LEAF));
    dummy2.add(rowDummy2);

    //add e,e2 to list of genes of sE
    speciesE.getGenes().put(0, e);
    speciesE.getGenes().put(1, e2);

    //Act
    List<GeneRelation> relation = TreeUtils.buildFitch(speciesE);

    //Assert
    Assert.assertTrue("The computed matrix with Fitch relations for the gene tree given by"
            + "GeneNode e is equal to the constructed matrix dummy.",
        relation.get(0).getRelation().equals(dummy));
    Assert.assertTrue("The name of the created gene in the list genes for the"
        + " isolated node e2 is SE102/01020",
        relation.get(1).getGenes().get(0).getName().equals("SE102/01020"));
    Assert.assertTrue("The computed matrix with Fitch relations for the isolated GeneNode"
        + "e2 is equal to the constructed matrix dummy2 with single entry.",
        relation.get(1).getRelation().equals(dummy2));
  }

  @Test
  public void testGetDistancesNotUltraMetricTree() {

    //Arrange

    //species tree
    SpeciesNode speciesP = new SpeciesNode(0,0,200);
    SpeciesNode speciesL = new SpeciesNode(3,0,201);
    SpeciesNode speciesR = new SpeciesNode(4,4,202);
    speciesP.setRightChild(speciesR);
    speciesP.setLeftChild(speciesL);
    //gene tree
    GeneNode parent = new GeneNode(0,0,2000);
    parent.setSpeciesNode(speciesP);
    GeneNode leftChild = new GeneNode(3,3,2001);
    leftChild.setSpeciesNode(speciesL);
    GeneNode rightChild = new GeneNode(4,4,2002);
    rightChild.setSpeciesNode(speciesR);
    parent.setLeftChild(leftChild);
    parent.setRightChild(rightChild);

    speciesP.getGenes().put(0,parent);
    //Act
    List<GeneDistances> distancesList = TreeUtils.getDistances(speciesP);

    //Assert
    Assert.assertEquals("The distance between leftChild and leftChild is 0",(double)0,
        distancesList.get(0).getDistances().get(0).get(0), 1.e-4);
    Assert.assertEquals("The distance between leftChild and rightChild is 7",(double)7,
        distancesList.get(0).getDistances().get(0).get(1), 1.e-4);
    Assert.assertEquals("The distance between rightChild and rightChild is 0",(double)0,
        distancesList.get(0).getDistances().get(1).get(0), 1.e-4);
  }


  @Test
  public void testGetDistancesUltraMetricTree() {

    //Arrange

    //species tree
    SpeciesNode speciesP = new SpeciesNode(0,0,200);
    SpeciesNode speciesL = new SpeciesNode(4,4,201);
    SpeciesNode speciesR = new SpeciesNode(4,4,202);
    speciesP.setRightChild(speciesR);
    speciesP.setLeftChild(speciesL);
    //gene tree
    GeneNode parent = new GeneNode(0,0,2000);
    parent.setSpeciesNode(speciesP);
    GeneNode leftChild = new GeneNode(4,4,2001);
    leftChild.setSpeciesNode(speciesL);
    GeneNode rightChild = new GeneNode(4,4,2002);
    rightChild.setSpeciesNode(speciesR);
    parent.setLeftChild(leftChild);
    parent.setRightChild(rightChild);

    speciesP.getGenes().put(0,parent);
    //Act
    List<GeneDistances> distancesList = TreeUtils.getDistances(speciesP, true);

    //Assert
    Assert.assertEquals("The distance between leftChild and leftChild is 0",(double)0,
        distancesList.get(0).getDistances().get(0).get(0), 1.e-4);
    Assert.assertEquals("The distance between leftChild and rightChild is 8",(double)8,
        distancesList.get(0).getDistances().get(0).get(1), 1.e-4);
    Assert.assertEquals("The distance between rightChild and rightChild is 0",(double)0,
        distancesList.get(0).getDistances().get(1).get(0), 1.e-4);
  }

  @Test
  public void testGetDistancesTrivialTree() {

    //Arrange

    //species tree
    SpeciesNode speciesP = new SpeciesNode(0,0,200);
    //gene tree
    GeneNode p = new GeneNode(0,0,2000);
    p.setSpeciesNode(speciesP);
    speciesP.getGenes().put(0,p);

    //Act
    List<GeneDistances> distancesList = TreeUtils.getDistances(speciesP);

    //Assert
    Assert.assertEquals("The distance between one node and itself is 0",(double)0,
        distancesList.get(0).getDistances().get(0).get(0), 1.e-4);
    Assert.assertTrue("The name of the one gene in the list genes is SE200/02000",
        distancesList.get(0).getGenes().get(0).getName().equals("SE200/02000"));
  }

  @Test
  public void testGetDistancesDoubiceTree() {
    /**Arrange speciestree.
     *         +------E-----+
     *    (4)  |            |
     *    +----D----+       |(6)
     *(2) |         |       |
     *    A         B       C
     */

    SpeciesNode speciesE = new SpeciesNode(0, 7, 104);
    SpeciesNode speciesD = new SpeciesNode(4, 11, 103);
    speciesE.setLeftChild(speciesD);
    SpeciesNode speciesC = new SpeciesNode(6, 13, 102);
    speciesE.setRightChild(speciesC);
    SpeciesNode speciesA = new SpeciesNode(2, 13, 101);
    speciesD.setLeftChild(speciesA);
    SpeciesNode speciesB = new SpeciesNode(2, 13, 100);
    speciesD.setRightChild(speciesB);

    /**Arrange genetree.
     *                     +---------------e---------------------+
     *                  (2)|                                     |
     *                     m-------------------------------+     |
     *                  (1)|                               |     | (6)
     *                     n--+                            |     |
     *                  (1)|  |                            |     |
     *                 +---d---------------+               |     |
     *            (2)  |      | (3)        |               |     |
     *                 a1     a2           b               c1    c2
     */

    GeneNode e = new GeneNode(0, 7, 1008);
    e.setSpeciesNode(speciesE);
    e.setEvent(new SpeciationEvent());
    GeneNode m = new GeneNode(2, 9, 1007);
    m.setSpeciesNode(speciesD);
    m.setEvent(new LateralGeneTransferEvent());
    GeneNode c2 = new GeneNode(6, 13, 1004);
    c2.setSpeciesNode(speciesC);
    e.setLeftChild(m);
    e.setRightChild(c2);
    GeneNode c1 = new GeneNode(4, 13, 1003);
    c1.setSpeciesNode(speciesC);
    GeneNode n = new GeneNode(1, 10, 1006);
    n.setSpeciesNode(speciesD);
    n.setEvent(new DuplicationEvent());
    m.setLeftChild(n);
    m.setRightChild(c1);
    GeneNode d = new GeneNode(1, 11, 1005);
    d.setSpeciesNode(speciesD);
    d.setEvent(new SpeciationEvent());
    GeneNode a2 = new GeneNode(3, 13, 1002);
    a2.setSpeciesNode(speciesA);
    n.setLeftChild(d);
    n.setRightChild(a2);
    GeneNode a1 = new GeneNode(2, 13, 1000);
    a1.setSpeciesNode(speciesA);
    GeneNode b = new GeneNode(2, 13, 1001);
    b.setSpeciesNode(speciesB);
    d.setLeftChild(a1);
    d.setRightChild(b);

    //add e to list of genes of sE
    speciesE.getGenes().put(0, e);

    /** Building matrix geneRelation0.dummy with right items
     *   (  0  4  6  8 12 )
     *   |  0  6  8 12 |
     *   |  0  8 12 |
     *   |  0 12 |
     *   |  0 )
     */
    List<List<Double>> dummy =
        new ArrayList<List<Double>>();
    List<Double> row1 =
        new ArrayList<Double>(Arrays.asList((double)0,(double)4,(double)6,(double)8,(double)12));
    dummy.add(row1);
    List<Double> row2 =
        new ArrayList<Double>(Arrays.asList((double) 0, (double) 6,(double) 8, (double) 12));
    dummy.add(row2);
    List<Double> row3 = new ArrayList<Double>(Arrays.asList((double)0,(double)8,(double)12));
    dummy.add(row3);
    List<Double> row4 =
        new ArrayList<Double>(Arrays.asList((double)0, (double)12));
    dummy.add(row4);
    List<Double> row5 =
        new ArrayList<Double>(Arrays.asList((double)0));
    dummy.add(row5);

    //Act
    List<GeneDistances> dist = getDistances(speciesE);
    List<GeneDistances> dist2 = getDistances(speciesE, true);

    //Assert

    Assert.assertTrue("Calculated matrix with distances is equal to matrix dummy",
        dist.get(0).getDistances().equals(dummy));
    Assert.assertTrue("Calculated matrix with distances (mode ultra metric) "
           + "is equal to matrix dummy", dist2.get(0).getDistances().equals(dummy));
  }

}
