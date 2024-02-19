package de.unileipzig.nw18a.nexbuild.buildutils;

import static de.unileipzig.nw18a.commontypes.GeneRelationType.EQUAL;
import static de.unileipzig.nw18a.commontypes.GeneRelationType.GREATER_THAN;
import static de.unileipzig.nw18a.commontypes.GeneRelationType.LESS_THAN;
import static de.unileipzig.nw18a.commontypes.GeneRelationType.RCB;
import static de.unileipzig.nw18a.commontypes.Matrix.MatrixFormat.UPPER;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.unileipzig.nw18a.commontypes.Gene;
import de.unileipzig.nw18a.commontypes.GeneRelation;
import de.unileipzig.nw18a.commontypes.Species;
import de.unileipzig.nw18a.commontypes.Triplet;
import de.unileipzig.nw18a.commontypes.TripletEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BuildUtilsTest {
  Species specA;
  Gene a1;
  Gene a2;
  Gene a3;
  Gene geneA;

  Species specB;
  Gene b1;
  Gene b2;
  Gene b3;
  Gene geneB;

  Species specC;
  Gene c1;
  Gene c2;
  Gene c3;
  Gene geneC;

  Species specD;
  Gene d1;
  Gene d2;
  Gene d3;
  Gene geneD;

  //some triplets for further testing
  Triplet a1b1c1;
  Triplet b2a2c2;
  Triplet a1b2c3;
  Triplet a2b2c1;

  //example in BUILD.pdf
  Triplet acb;
  Triplet adb;
  Triplet cda;
  Triplet cdb;
  Set<Triplet> exampleTripletSet;
  Set<TripletEntry> exampleLeafs;

  //example in Closure.pdf (Figure 1)
  Triplet abc;
  Triplet acd;
  Triplet bcd;
  Triplet abd;
  Set<Triplet> closureExampleTripletSet;

  // example species triplets
  Triplet specTripletAcb;
  Triplet specTripletAdb;
  Triplet specTripletCda;
  Triplet specTripletCdb;
  Set<Triplet> exampleSpeciesTripletSet;


  //graph
  Map<TripletEntry, Set<TripletEntry>> graph;
  LinkedHashSet<TripletEntry> neighboursA1;
  LinkedHashSet<TripletEntry> neighboursA2;
  LinkedHashSet<TripletEntry> neighboursA3;
  LinkedHashSet<TripletEntry> neighboursB1;
  LinkedHashSet<TripletEntry> neighboursB2;
  LinkedHashSet<TripletEntry> neighboursB3;
  LinkedHashSet<TripletEntry> neighboursC1;
  LinkedHashSet<TripletEntry> neighboursC2;
  LinkedHashSet<TripletEntry> neighboursC3;

  /**
   * Init genes and species.
   */
  @Before
  public void generateGeneAndSpeciesTriplets() {
    //generate genes and species
    specA = new Species("A");
    a1 = new Gene("a1", specA);
    a2 = new Gene("a2", specA);
    a3 = new Gene("a3", specA);
    geneA = new Gene("a", specA);

    specB = new Species("B");
    b1 = new Gene("b1", specB);
    b2 = new Gene("b2", specB);
    b3 = new Gene("b3", specB);
    geneB = new Gene("b", specB);

    specC = new Species("C");
    c1 = new Gene("c1", specC);
    c2 = new Gene("c2", specC);
    c3 = new Gene("c3", specC);
    geneC = new Gene("c", specC);

    specD = new Species("D");
    d1 = new Gene("d1", specD);
    d2 = new Gene("d2", specD);
    d3 = new Gene("d3", specD);
    geneD = new Gene("d", specD);

    //init triplets from example
    acb = new Triplet(geneA, geneC, geneB);
    adb = new Triplet(geneA, geneD, geneB);
    cda = new Triplet(geneC, geneD, geneA);
    cdb = new Triplet(geneC, geneD, geneB);
    exampleTripletSet = new HashSet<Triplet>();
    exampleTripletSet.add(acb);
    exampleTripletSet.add(adb);
    exampleTripletSet.add(cda);
    exampleTripletSet.add(cdb);
    exampleLeafs
        = new HashSet<TripletEntry>(Arrays.asList(
        new TripletEntry[] {geneA, geneB, geneC, geneD}
    ));

    //init other triplets
    a1b1c1 = new Triplet(a1, b1, c1);
    b2a2c2 = new Triplet(b2, a2, c2);
    a1b2c3 = new Triplet(a1, b2, c3);
    a2b2c1 = new Triplet(a2, b2, c1);

    //init triplets for test in Closure.pdf
    abc = new Triplet(geneA, geneB, geneC);
    acd = new Triplet(geneA, geneC, geneD);
    bcd = new Triplet(geneB, geneC, geneD);
    abd = new Triplet(geneA, geneB, geneD);
    closureExampleTripletSet = new HashSet<Triplet>();
    closureExampleTripletSet.add(abc);
    closureExampleTripletSet.add(acd);
    closureExampleTripletSet.add(bcd);

    specTripletAcb = new Triplet(specA, specC, specB);
    specTripletAdb = new Triplet(specA, specD, specB);
    specTripletCda = new Triplet(specC, specD, specA);
    specTripletCdb = new Triplet(specC, specD, specB);

    exampleSpeciesTripletSet = new HashSet<Triplet>();
    exampleSpeciesTripletSet.add(specTripletAcb);
    exampleSpeciesTripletSet.add(specTripletAdb);
    exampleSpeciesTripletSet.add(specTripletCda);
    exampleSpeciesTripletSet.add(specTripletCdb);

    //init graph
    graph = new HashMap<TripletEntry, Set<TripletEntry>>();

    neighboursA1 = new LinkedHashSet<TripletEntry>();
    neighboursA2 = new LinkedHashSet<TripletEntry>();
    neighboursA3 = new LinkedHashSet<TripletEntry>();
    neighboursB1 = new LinkedHashSet<TripletEntry>();
    neighboursB2 = new LinkedHashSet<TripletEntry>();
    neighboursB3 = new LinkedHashSet<TripletEntry>();
    neighboursC1 = new LinkedHashSet<TripletEntry>();
    neighboursC2 = new LinkedHashSet<TripletEntry>();
    neighboursC3 = new LinkedHashSet<TripletEntry>();
  }

  @Test
  public void testCase1_1MakeGeneTriplets() {
    /**
     * Init matrix rcbRelation for testMakeGeneTriplets.
     *     a  b  c   as
     *   a 0  1  0       a 0  1  0
     *   b    0 -1       b 0 -1
     *   c       0       c 0
     *   indices: [a,b,c] with tripleRelation [1,0,-1]: case 1.1
     *   --> triple after swapping: [c,b,a] with tripleRelation [-1,0,1]: case 1.6
     *   --> triple after rotateLeft: [b,a,c] with tripleRelation [1,-1,0]
     *   --> the desired form of case 1, print gene triple [b,c,a] and species triple [B,A,C]
     */
    //Arrange
    GeneRelation relation = new GeneRelation(RCB, UPPER, "SE100/1000");
    List<Byte> row0 = new ArrayList<Byte>(Arrays.asList(EQUAL, GREATER_THAN, EQUAL));
    relation.getRelation().add((ArrayList<Byte>) row0);
    List<Byte> row1 = new ArrayList<Byte>(Arrays.asList(EQUAL, LESS_THAN));
    relation.getRelation().add((ArrayList<Byte>) row1);
    List<Byte> row2 = new ArrayList<Byte>(Arrays.asList(EQUAL));
    relation.getRelation().add((ArrayList<Byte>) row2);

    Species speciesA = new Species("SE100");
    Species speciesB = new Species("SE101");
    Species speciesC = new Species("SE102");
    Gene a = new Gene("SE100/1000", speciesA);
    Gene b = new Gene("SE101/1001", speciesB);
    Gene c = new Gene("SE102/1002", speciesC);
    relation.getGenes().add(a);
    relation.getGenes().add(b);
    relation.getGenes().add(c);

    Triplet resultGeneTriplet = new Triplet(b, c, a);
    Set<Triplet> dummyGene = new LinkedHashSet<Triplet>();
    dummyGene.add(resultGeneTriplet);
    Triplet resultSpeciesTriplet = new Triplet(speciesB, speciesA, speciesC);
    Set<Triplet>  dummySpec = new LinkedHashSet<Triplet>();
    dummySpec.add(resultSpeciesTriplet);

    //Act
    BuildUtils.GeneTripletsResult geneTripletsResult1 =
        BuildUtils.makeGeneTriplets(relation);
    Assert.assertEquals(dummyGene, geneTripletsResult1.getGeneTriplets());
    Assert.assertEquals(dummySpec, geneTripletsResult1.getSpeciesTriplets());
    Assert.assertTrue(geneTripletsResult1.getNoConstraintTriplets().isEmpty());

  }

  @Test
  public void testCase1_2MakeGeneTriplets() {
    /**
     * Init matrix rcbRelation for testMakeGeneTriplets.
     *     a  b  c   as
     *   a 0 -1  1       a 0 -1  1
     *   b    0  0       b 0  0
     *   c       0       c 0
     *   indices: [a,b,c] with tripleRelation [-1,1,0]: case 1.2
     *   --> triple after swapping: [c,b,a] with tripleRelation [0,1,-1]: case 1.5
     *   --> triple after rotateRight: [a,c,b] with tripleRelation [1,-1,0]
     *   --> have the desired form of case 1, print gene triple [a,b,c] and species triple [A,C,B]
     */
    //Arrange
    GeneRelation relation = new GeneRelation(RCB, UPPER, "SE100/1000");
    List<Byte> row0 = new ArrayList<Byte>(Arrays.asList(EQUAL, LESS_THAN, GREATER_THAN));
    relation.getRelation().add((ArrayList<Byte>) row0);
    List<Byte> row1 = new ArrayList<Byte>(Arrays.asList(EQUAL, EQUAL));
    relation.getRelation().add((ArrayList<Byte>) row1);
    List<Byte> row2 = new ArrayList<Byte>(Arrays.asList(EQUAL));
    relation.getRelation().add((ArrayList<Byte>) row2);

    Species speciesA = new Species("SE100");
    Species speciesB = new Species("SE101");
    Species speciesC = new Species("SE102");
    Gene a = new Gene("SE100/1000", speciesA);
    Gene b = new Gene("SE101/1001", speciesB);
    Gene c = new Gene("SE102/1002", speciesC);
    relation.getGenes().add(a);
    relation.getGenes().add(b);
    relation.getGenes().add(c);

    Triplet resultGeneTriplet = new Triplet(a, b, c);
    Set<Triplet>  dummyGene = new LinkedHashSet<Triplet>();
    dummyGene.add(resultGeneTriplet);
    Triplet resultSpeciesTriplet = new Triplet(speciesA, speciesC, speciesB);
    Set<Triplet>  dummySpec = new LinkedHashSet<Triplet>();
    dummySpec.add(resultSpeciesTriplet);

    //Act
    BuildUtils.GeneTripletsResult geneTripletsResult1 =
        BuildUtils.makeGeneTriplets(relation);
    Assert.assertEquals(dummyGene, geneTripletsResult1.getGeneTriplets());
    Assert.assertEquals(dummySpec, geneTripletsResult1.getSpeciesTriplets());
    Assert.assertTrue(geneTripletsResult1.getNoConstraintTriplets().isEmpty());

  }

  @Test
  public void testCase5MakeGeneTriplets() {
    /**
     * Init matrix rcbRelation for testMakeGeneTriplets.
     *     a  b  c   as
     *   a 0 -1 -1       a 0 -1 -1
     *   b    0 -1       b 0 -1
     *   c        0      c 0
     *   indices: [a,b,c] with tripleRelation [0,0,0]: case 5
     *   --> that print neither a gene triple nor a species triple
     */
    //Arrange
    GeneRelation relation = new GeneRelation(RCB, UPPER, "SE100/1000");
    List<Byte> row0 = new ArrayList<Byte>(Arrays.asList(LESS_THAN, LESS_THAN));
    relation.getRelation().add((ArrayList<Byte>) row0);
    List<Byte> row1 = new ArrayList<Byte>(Arrays.asList(LESS_THAN));
    relation.getRelation().add((ArrayList<Byte>) row1);
    List<Byte> row2 = new ArrayList<Byte>();
    relation.getRelation().add((ArrayList<Byte>) row2);

    Species speciesA = new Species("SE100");
    Species speciesB = new Species("SE101");
    Species speciesC = new Species("SE102");
    Gene a = new Gene("SE100/1000", speciesA);
    Gene b = new Gene("SE101/1001", speciesB);
    Gene c = new Gene("SE102/1002", speciesC);
    relation.getGenes().add(a);
    relation.getGenes().add(b);
    relation.getGenes().add(c);

    //Act
    BuildUtils.GeneTripletsResult geneTripletsResult1 =
        BuildUtils.makeGeneTriplets(relation);
    Assert.assertTrue(geneTripletsResult1.getGeneTriplets().isEmpty());
    Assert.assertTrue(geneTripletsResult1.getSpeciesTriplets().isEmpty());
    Assert.assertTrue(geneTripletsResult1.getNoConstraintTriplets().isEmpty());

  }

  @Test
  public void testDoubiceTreeMakeGeneTriplets() {
    /**Arange speciestree.
     *         +------E-----+
     *    (4)  |            |
     *    +----D----+       |(6)
     *(2) |         |       |
     *    A         B       C
     */

    /**Arrange genetree.
     *                     +---------------e---------------------+
     *                     |                                     |
     *                     m-------------------------------+     |
     *                     |                               |     |
     *                     n--+                            |     |
     *                     |  |                            |     |
     *                 +---d---------------+               |     |
     *                 |      |            |               |     |
     *                 a1     a2           b               c1    c2
     */

    Species speciesA = new Species("SE100");
    Species speciesB = new Species("SE101");
    Species speciesC = new Species("SE102");
    Gene a1 = new Gene("SE100/1000", speciesA);
    Gene b = new Gene("SE101/1001", speciesB);
    Gene a2 = new Gene("SE100/1002", speciesA);
    Gene c1 = new Gene("SE102/1003", speciesC);
    Gene c2 = new Gene("SE102/1004", speciesC);
    GeneRelation genes1 = new GeneRelation(RCB, UPPER, "SE104/1008");
    genes1.getGenes().add(a1);
    genes1.getGenes().add(b);
    genes1.getGenes().add(a2);
    genes1.getGenes().add(c1);
    genes1.getGenes().add(c2);

    /** Building matrix geneRelation0.dummy with right items
     *  ( = = > < = ) as  ( = = > < = )
     *  |   = > < = |     | = > < = |
     *  |     = < = |     | = < = |
     *  |       = > )     | = > )
     *  |         = )     | = )
     */

    genes1.getRelation().add(new ArrayList<Byte>());
    genes1.getRelation().add(new ArrayList<Byte>());
    genes1.getRelation().add(new ArrayList<Byte>());
    genes1.getRelation().add(new ArrayList<Byte>());
    genes1.getRelation().add(new ArrayList<Byte>());
    genes1.getRelation().get(0).add(EQUAL);
    genes1.getRelation().get(0).add(EQUAL);
    genes1.getRelation().get(0).add(GREATER_THAN);
    genes1.getRelation().get(0).add(LESS_THAN);
    genes1.getRelation().get(0).add(EQUAL);
    genes1.getRelation().get(1).add(EQUAL);
    genes1.getRelation().get(1).add(GREATER_THAN);
    genes1.getRelation().get(1).add(LESS_THAN);
    genes1.getRelation().get(1).add(EQUAL);
    genes1.getRelation().get(2).add(EQUAL);
    genes1.getRelation().get(2).add(LESS_THAN);
    genes1.getRelation().get(2).add(EQUAL);
    genes1.getRelation().get(3).add(EQUAL);
    genes1.getRelation().get(3).add(GREATER_THAN);

    //Arrange solution

    Set<Triplet> geneTriplets1 = new LinkedHashSet<Triplet>();
    Set<Triplet> speciesTriplets1 = new LinkedHashSet<Triplet>();
    Set<Triplet> noConstraintTriplets1 = new LinkedHashSet<Triplet>();

    //1. row=a1,colLeft=b,colRight=a2: case 3.1,(0,1,1) --> [a1,b,a2].
    geneTriplets1.add(new Triplet(a1, b, a2));
    //2. row=a1,colLeft=b,colRight=c1: case 4.1,(0,-1,-1) --> [A,B,C].
    speciesTriplets1.add(new Triplet(speciesA, speciesB, speciesC));
    //3. row=a1,colLeft=b,colRight=c2: case 6,(0,0,0) --> [A,B,C].
    noConstraintTriplets1.add(new Triplet(a1, b, c2, false));
    //4. row=a1,colLeft=a2,colRight=c1: case 4.1,(1,-1,-1) --> [A,A,C].
    //5. row=a1,colLeft=a2,colRight=c2: case 2.1,(1,0,0) --> [a1,a2,c2],[A,A,C].
    geneTriplets1.add(new Triplet(a1, a2, c2));
    //6. row=a1,colLeft=c1,colRight=c2: case 1.6,(-1,0,1) after rotateLeft: c1,c2,a1
    // --> [c1,a1,c2],[C,C,A].
    geneTriplets1.add(new Triplet(c1, a1, c2));
    //7. row=b,colLeft=a2,colRight=c1: case 4.1,(1,-1,-1) --> [B,A,C]
    speciesTriplets1.add(new Triplet(speciesB, speciesA, speciesC));
    //8. row=b,colLeft=a2,colRight=c2: case 2.1,(1,0,0) --> [b,a2,c2],[B,A,C]
    geneTriplets1.add(new Triplet(b, a2, c2));
    speciesTriplets1.add(new Triplet(speciesB, speciesA, speciesC));
    //9. row=b,colLeft=c1,colRight=c2: case 1.6,(-1,0,1) after rotateLeft: c1,c2,b
    // --> [c1,b,c2],[C,C,B]
    geneTriplets1.add(new Triplet(c1, b, c2));
    //10. row=a2,colLeft=c1,colRight=c2: case 1.6,(-1,0,1)after rotateLeft: c1,c2,a2
    // --> [c1,a2,c2],[C,C,A]
    geneTriplets1.add(new Triplet(c1, a2, c2));
    //speciesTriplets1.add(new Triplet(speciesC, speciesC, speciesA));

    //Act
    BuildUtils.GeneTripletsResult geneTripletsResult1 =
        BuildUtils.makeGeneTriplets(genes1);
    Assert.assertEquals(geneTriplets1.size(),
        geneTripletsResult1.getGeneTriplets().size());
    Assert.assertEquals(geneTriplets1, geneTripletsResult1.getGeneTriplets());
    Assert.assertEquals(speciesTriplets1, geneTripletsResult1.getSpeciesTriplets());
    Assert.assertEquals(noConstraintTriplets1, geneTripletsResult1.getNoConstraintTriplets());
    Assert.assertEquals("SE104/1008", geneTripletsResult1.getTreeName());
  }

  @Test
  public void buildAhoShouldReturnEmptyMapForEmptyTripleSet() {
    //Arrange
    Set<Triplet> inputTripleSet = new HashSet<Triplet>();
    Set<TripletEntry> inputLeafSet = new TreeSet<TripletEntry>();

    //Act
    Map<TripletEntry, Set<TripletEntry>> outputGraph = BuildUtils.buildAhoGraph(
        inputTripleSet, inputLeafSet
    );

    //Assert
    assertTrue("Empty input set should generate empty graph.",
        outputGraph.isEmpty());
  }

  @Test
  public void buildAhoReturnsGraphWithTwoConnectedNodesForSingleTriplet() {
    //Arrange
    Set<Triplet> inputTripleSet = new HashSet<Triplet>();
    inputTripleSet.add(a1b1c1);
    Set<TripletEntry> inputLeafSet
        = new HashSet<TripletEntry>(Arrays.asList(new TripletEntry[] {a1, b1, c1}));


    //Act
    Map<TripletEntry, Set<TripletEntry>> outputGraph = BuildUtils.buildAhoGraph(
        inputTripleSet, inputLeafSet
    );

    //Assert
    assertTrue("Output graph should have vertex a1.",
        outputGraph.containsKey(a1));
    assertTrue("Output graph should have vertex b1.",
        outputGraph.containsKey(b1));
    assertTrue("Output graph should have vertex a1 with edge to b1",
        outputGraph.get(a1).contains(b1));
    assertTrue("Output graph should have vertex b1 with edge to a1",
        outputGraph.get(b1).contains(a1));
  }

  @Test
  public void buildAhoBuildExample() {
    //Act
    Map<TripletEntry, Set<TripletEntry>> outputGraph =
        BuildUtils.buildAhoGraph(exampleTripletSet, exampleLeafs);

    //Assert
    assertTrue("Output graph should have vertex a.",
        outputGraph.containsKey(geneA));
    assertTrue("Output graph should have vertex b.",
        outputGraph.containsKey(geneB));
    assertTrue("Output graph should have vertex c.",
        outputGraph.containsKey(geneC));
    assertTrue("Output graph should have vertex d.",
        outputGraph.containsKey(geneD));
    assertTrue("Output graph should have vertex a with edges to c and d",
        outputGraph.get(geneA).contains(geneC)
            && outputGraph.get(geneA).contains(geneD));
    assertTrue("Output graph should have vertex c with edges to a and d",
        outputGraph.get(geneC).contains(geneA)
            && outputGraph.get(geneC).contains(geneD));
    assertTrue("Output graph should have vertex d with edges to c and a",
        outputGraph.get(geneD).contains(geneC)
            && outputGraph.get(geneD).contains(geneA));
    assertTrue("Output graph should have vertex b with no edges",
        outputGraph.get(geneB).isEmpty());
  }

  /**
   * Test for method buildConnectedComponent.
   * The graph contains three connected components.
   * +----------A2----------+
   * C3                   |          |           |
   * |          |           |
   * C2------A3         |           B1
   * +--A1--+              |          |           |
   * |      |              |          |           |
   * B3     B2             +----------C1----------+
   */
  @Test
  public void testConnectedComponent() {

    //Arrange
    neighboursA1.add(b2);
    neighboursA1.add(b3);
    neighboursA2.add(a3);
    neighboursA2.add(b1);
    neighboursA2.add(c1);
    neighboursA3.add(a2);
    neighboursA3.add(c1);
    neighboursA3.add(c2);
    neighboursB1.add(a2);
    neighboursB1.add(c1);
    neighboursB2.add(a1);
    neighboursB3.add(a1);
    neighboursC1.add(a2);
    neighboursC1.add(a3);
    neighboursC1.add(b1);
    neighboursC2.add(a3);

    graph.put(a1, neighboursA1);
    graph.put(a2, neighboursA2);
    graph.put(a3, neighboursA3);
    graph.put(b1, neighboursB1);
    graph.put(b2, neighboursB2);
    graph.put(b3, neighboursB3);
    graph.put(c1, neighboursC1);
    graph.put(c2, neighboursC2);
    graph.put(c3, neighboursC3);

    LinkedHashSet<TripletEntry> component1 = new LinkedHashSet<TripletEntry>();
    component1.add(a1);
    component1.add(b2);
    component1.add(b3);

    LinkedHashSet<TripletEntry> component2 = new LinkedHashSet<TripletEntry>();
    component2.add(a3);
    component2.add(a2);
    component2.add(b1);
    component2.add(c1);
    component2.add(c2);

    LinkedHashSet<TripletEntry> component3 = new LinkedHashSet<TripletEntry>();
    component3.add(c3);

    List<LinkedHashSet<TripletEntry>> componentsExpected =
        new LinkedList<LinkedHashSet<TripletEntry>>();
    componentsExpected.add(component1);
    componentsExpected.add(component2);
    componentsExpected.add(component3);

    //Act
    List<Set<TripletEntry>> componentsActual =
        BuildUtils.buildConnectedComponents(graph);

    //Arrange
    assertTrue("The graph should contain three connected components",
        componentsActual.containsAll(componentsExpected));

    for (Set<TripletEntry> components : componentsActual) {
      switch (components.size()) {
        case 3:
          assertTrue("The component should contain all genes from component1 ({a1, b2, b3}).",
              components.containsAll(component1));
          break;
        case 5:
          assertTrue(
              "The component should contain all genes from component2 ({a2, a3, b1, c1, c2}).",
              components.containsAll(component2));
          break;
        case 1:
          assertTrue("The component should contain all genes from component3 ({c3}).",
              components.containsAll(component3));
          break;
        default:
          break;
      }
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void filterMethodShouldFailIfConnectedComponentsSetIsEmpty() {

    //Act
    BuildUtils.filterTriplets(
        new ArrayList<Set<TripletEntry>>(),
        exampleTripletSet);
  }

  @Test
  public void filterMethodShouldReturnEmptyValueSetIfEmptyTripletSetIsPassedAsArgument() {
    // Arrange
    List<Set<TripletEntry>> conComps = new LinkedList<Set<TripletEntry>>();
    Set<TripletEntry> componentA = new LinkedHashSet<TripletEntry>();
    conComps.add(componentA);
    componentA.add(a1);
    componentA.add(b1);

    // Act
    Map<Set<TripletEntry>, Set<Triplet>> result
        = BuildUtils.filterTriplets(
        conComps,
        new LinkedHashSet<Triplet>());

    // Assert
    assertTrue("Value set for componentA should be empty.",
        result.get(componentA).isEmpty());
  }

  @Test
  public void filterTripletsShouldDropTripletsNotContainedInComponent() {
    // Arrange
    List<Set<TripletEntry>> conComps = new LinkedList<Set<TripletEntry>>();
    Set<TripletEntry> componentA = new LinkedHashSet<TripletEntry>();
    conComps.add(componentA);
    componentA.add(a1);
    componentA.add(b1);
    componentA.add(c1);

    Set<Triplet> triplets = new LinkedHashSet<Triplet>();
    triplets.add(a1b1c1);
    triplets.add(a1b2c3);

    // Act
    Map<Set<TripletEntry>, Set<Triplet>> result
        = BuildUtils.filterTriplets(
        conComps, triplets);

    // Assert
    assertTrue("Triplet a1b1c1 should be contained in resulting map for componentA.",
        result.get(componentA).contains(a1b1c1));
    assertFalse("Triplet a1b2c3 should not be contained in resulting map for componentA.",
        result.get(componentA).contains(a1b2c3));
  }

  @Test
  public void filterTripletsTestingExample() {
    // Arrange
    List<Set<TripletEntry>> conComps = new LinkedList<Set<TripletEntry>>();

    Set<TripletEntry> componentAcd = new LinkedHashSet<TripletEntry>();
    conComps.add(componentAcd);
    componentAcd.add(geneA);
    componentAcd.add(geneD);
    componentAcd.add(geneC);

    Set<TripletEntry> componentB = new LinkedHashSet<TripletEntry>();
    conComps.add(componentB);
    componentB.add(geneB);

    // Act
    Map<Set<TripletEntry>, Set<Triplet>> result
        = BuildUtils.filterTriplets(
        conComps, exampleTripletSet);

    // Assert
    assertFalse("Triplet acb should not be contained in resulting map for componentA.",
        result.get(componentAcd).contains(acb));
    assertFalse("Triplet adb should not be contained in resulting map for componentA.",
        result.get(componentAcd).contains(adb));
    assertTrue("Triplet cda should be contained in resulting map for componentAcd.",
        result.get(componentAcd).contains(cda));
    assertFalse("Triplet cdb should not be contained in resulting map for componentA.",
        result.get(componentAcd).contains(cdb));
    assertTrue("Triplet set for componentB should be empty.",
        result.get(componentB).isEmpty());
  }

  @Test
  public void insituFilterTripletsShouldDropTripletsNotContainedInComponent() {
    // Arrange
    Set<TripletEntry> leafSet = new LinkedHashSet<TripletEntry>();
    leafSet.add(a1);
    leafSet.add(b1);
    leafSet.add(c1);

    Set<Triplet> triplets = new LinkedHashSet<Triplet>();
    triplets.add(a1b1c1);
    triplets.add(a1b2c3);

    // Act
    BuildUtils.filterTriplets(leafSet, triplets);

    // Assert
    assertTrue("Triplet a1b1c1 should be contained in resulting map for componentA.",
        triplets.contains(a1b1c1));
    assertFalse("Triplet a1b2c3 should not be contained in resulting map for componentA.",
        triplets.contains(a1b2c3));
  }

  @Test
  public void insituFilterTripletsTestingExample() {
    // Arrange
    Set<TripletEntry> leafSet = new LinkedHashSet<TripletEntry>();
    leafSet.add(geneA);
    leafSet.add(geneD);
    leafSet.add(geneC);

    // Act
    BuildUtils.filterTriplets(leafSet, exampleTripletSet);

    // Assert
    assertFalse("Triplet acb should not be contained in resulting map for componentA.",
        exampleTripletSet.contains(acb));
    assertFalse("Triplet adb should not be contained in resulting map for componentA.",
        exampleTripletSet.contains(adb));
    assertTrue("Triplet cda should be contained in resulting map for componentAcd.",
        exampleTripletSet.contains(cda));
    assertFalse("Triplet cdb should not be contained in resulting map for componentA.",
        exampleTripletSet.contains(cdb));
  }

  @Test
  public void testClosureExampleFromPaper() {
    // Arrange:
    // see @Before

    // Act:
    BuildUtils.calculateClosure(closureExampleTripletSet);

    // Assert:
    assertTrue("Closure should contain previously contained triplet ab|c",
        closureExampleTripletSet.contains(abc));
    assertTrue("Closure should contain previously contained triplet ac|d",
        closureExampleTripletSet.contains(acd));
    assertTrue("Closure should contain previously contained triplet bc|d",
        closureExampleTripletSet.contains(bcd));
    assertTrue("Closure should contain additional triplet ab|d",
        closureExampleTripletSet.contains(abd));
  }

  @Test
  public void testCompleteGeneTriplets() {
    // Arrange
    Set<Triplet> geneTriplets = new HashSet<Triplet>();
    Set<Triplet> noConstraints = new HashSet<Triplet>();
    noConstraints.add(new Triplet(geneA, geneC, geneB, false));
    noConstraints.add(new Triplet(geneA, geneD, geneB, false));
    noConstraints.add(new Triplet(geneC, geneD, geneA, false));
    noConstraints.add(new Triplet(geneC, geneD, geneB, false));

    BuildUtils.GeneTripletsResult gtr = new BuildUtils.GeneTripletsResult(
        geneTriplets, exampleSpeciesTripletSet, noConstraints, "Tree");

    // Act
    gtr.completeGeneTriplets(gtr.getSpeciesTriplets());

    // Assert
    assertTrue(gtr.getGeneTriplets().equals(exampleTripletSet));
  }

  @Test
  public void buildCreatesSingleTreeIfGivenOnlyOneTaxon() {
    // Arrange
    Triplet allSameTaxon = new Triplet(a1,a1,a1,true);
    Set<Triplet> oneTaxonTriplet = new LinkedHashSet<Triplet>();
    oneTaxonTriplet.add(allSameTaxon);
    Set<TripletEntry> leafSet = new LinkedHashSet<TripletEntry>();
    leafSet.add(a1);

    // Act
    Node resultingRoot = BuildUtils.buildTree(oneTaxonTriplet, leafSet);

    // Assert
    assertTrue("resultingRoot should have no children",
        resultingRoot.getChildren().isEmpty());
    assertEquals("returned root should be single leaf as root",
        a1, resultingRoot.getTaxon());
  }

  @Test
  public void buildCreatesTreeOfSize3GivenOnly2DifferentTaxa() {
    // Arrange
    Triplet twoDifferentTaxa1 = new Triplet(a1,a1,b1);
    Triplet twoDifferentTaxa2 = new Triplet(b1,b1,a1);
    Set<Triplet> twoTaxaTriplet = new LinkedHashSet<Triplet>();
    twoTaxaTriplet.add(twoDifferentTaxa1);
    twoTaxaTriplet.add(twoDifferentTaxa2);
    Set<TripletEntry> leafSet = new LinkedHashSet<TripletEntry>();
    leafSet.add(a1);
    leafSet.add(b1);

    // Act
    Node resultingRoot = BuildUtils.buildTree(twoTaxaTriplet, leafSet);

    // Assert
    assertTrue("resultingRoot should have no children",
        resultingRoot.getChildren().size() == 2);
  }

  @Test
  public void buildReturnsNullIfAhoGraphHasOnlyOneConnectedComponent() {
    // Arrange
    Triplet abc2 = new Triplet(geneA, geneB, geneC);
    Triplet acb2 = new Triplet(geneA, geneC, geneB);

    Set<Triplet> testSet = new LinkedHashSet<Triplet>();
    testSet.add(abc2);
    testSet.add(acb2);

    Set<TripletEntry> leafSet = new LinkedHashSet<TripletEntry>();
    leafSet.add(geneA);
    leafSet.add(geneB);
    leafSet.add(geneC);

    // Act
    Node resultingRoot = BuildUtils.buildTree(testSet, leafSet);

    //Assert
    assertNull("Given triplet set should not be consistent."
        + " Therefore resultingRoot should be null", resultingRoot);
  }


  @Test
  public void buildTreeTestExample() {
    /*
     *       root
     *      /    \
     *     i1     \
     *    /  \     \
     *   /   i2     \
     *  /   /  \     \
     * a   c    d     b
     *
     * */

    // Arrange
    // create leaves
    final Node nodeC = new Node(geneC.toString(), geneC);
    final Node nodeD = new Node(geneD.toString(), geneD);
    final Node nodeA = new Node(geneA.toString(), geneA);
    final Node nodeB = new Node(geneB.toString(), geneB);

    // Act
    Node resultingRoot = BuildUtils.buildTree(exampleTripletSet, exampleLeafs);
    //find out index of i1
    int indexI1 = 1 - resultingRoot.getChildren().indexOf(nodeB);
    Node builtI1 = resultingRoot.getChildren().get(indexI1);
    // find out index of i2
    int indexI2 = 1 - builtI1.getChildren().indexOf(nodeA);
    Node builtI2 = builtI1.getChildren().get(indexI2);

    // Assert
    assertEquals("root should have 2 children.",
        resultingRoot.getChildren().size(), 2);
    assertEquals("builtI1 should have 2 children.",
        builtI1.getChildren().size(), 2);
    assertEquals("builtI2 should have 2 children.",
        builtI2.getChildren().size(), 2);
    assertTrue("b should be a child of root.",
        resultingRoot.getChildren().contains(nodeB));
    assertTrue("a should be a child of builtI1.",
        builtI1.getChildren().contains(nodeA));
    assertTrue("c should be a child of builtI2.",
        builtI2.getChildren().contains(nodeC));
    assertTrue("d should be a child of builtI2.",
        builtI2.getChildren().contains(nodeD));
  }
}
