package de.unileipzig.nw18a.alfextract.treemapping;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class AbstractNodeTest {

  /*
   * Tests the NodeIterator of class AbstractNode with a tree of the form
   *
   *                      +----------15----------+
   *                      |                      |
   *                +-----7-----+          +-----14-----+
   *                |           |          |            |
   *             +--3--+     +--6--+    +--10--+     +--13--+
   *             |     |     |     |    |      |     |      |
   *             1     2     4     5    8      9     11     12
   *
   */
  @Test
  public void testNodeIterator1() {
    // Arrange
    SpeciesNode node15 = new SpeciesNode(15);
    SpeciesNode node07 = new SpeciesNode(7);
    SpeciesNode node14 = new SpeciesNode(14);
    node15.setLeftChild(node07);
    node15.setRightChild(node14);

    SpeciesNode node03 = new SpeciesNode(3);
    SpeciesNode node06 = new SpeciesNode(6);
    node07.setLeftChild(node03);
    node07.setRightChild(node06);

    SpeciesNode node01 = new SpeciesNode(1);
    SpeciesNode node02 = new SpeciesNode(2);
    node03.setLeftChild(node01);
    node03.setRightChild(node02);

    SpeciesNode node04 = new SpeciesNode(4);
    SpeciesNode node05 = new SpeciesNode(5);
    node06.setLeftChild(node04);
    node06.setRightChild(node05);

    SpeciesNode node10 = new SpeciesNode(10);
    SpeciesNode node13 = new SpeciesNode(13);
    node14.setLeftChild(node10);
    node14.setRightChild(node13);

    SpeciesNode node08 = new SpeciesNode(8);
    SpeciesNode node09 = new SpeciesNode(9);
    node10.setLeftChild(node08);
    node10.setRightChild(node09);

    SpeciesNode node11 = new SpeciesNode(11);
    SpeciesNode node12 = new SpeciesNode(12);
    node13.setLeftChild(node11);
    node13.setRightChild(node12);

    ArrayList<Integer> result = new ArrayList<Integer>();

    // Act
    Iterator<AbstractNode> it = node15.iterator();

    try {
      do {
        result.add(it.next().getId());
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }

    // Assert
    assertEquals("Number of nodes in the tree should be 15", 15, result.size());
    for (int i = 1; i <= result.size(); i++) {
      assertEquals("The " + i + "th element of the tree should be " + result.get(i - 1),
          i, result.get(i - 1).intValue());
    }
  }

  /*
   * Tests the NodeIterator of class AbstractNode with a tree of the form
   *
   *                      +----------7----------+
   *                      |                     |
   *                +-----5-----+               |
   *                |           |               |
   *                |        +--4--+            |
   *                |        |     |            |
   *                1        2     3            6
   *
   */
  @Test
  public void testNodeIterator2() {
    // Arrange
    SpeciesNode node07 = new SpeciesNode(7);
    SpeciesNode node05 = new SpeciesNode(5);
    SpeciesNode node06 = new SpeciesNode(6);
    node07.setLeftChild(node05);
    node07.setRightChild(node06);

    SpeciesNode node01 = new SpeciesNode(1);
    SpeciesNode node04 = new SpeciesNode(4);
    node05.setLeftChild(node01);
    node05.setRightChild(node04);

    SpeciesNode node02 = new SpeciesNode(2);
    SpeciesNode node03 = new SpeciesNode(3);
    node04.setLeftChild(node02);
    node04.setRightChild(node03);

    ArrayList<Integer> result = new ArrayList<Integer>();

    // Act
    Iterator<AbstractNode> it = node07.iterator();

    try {
      do {
        result.add(it.next().getId());
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }

    // Assert
    assertEquals("Number of nodes in the tree should be 7", 7, result.size());
    for (int i = 1; i <= result.size(); i++) {
      assertEquals("The " + i + "th element of the tree should be " + result.get(i - 1),
          i, result.get(i - 1).intValue());
    }
  }

  /*
   * Tests the NodeIterator of class AbstractNode with a tree of the form
   *
   *      1
   *
   * that is a tree only consisting of one node
   *
   */
  @Test
  public void testNodeIterator3() {
    // Arrange
    SpeciesNode node01 = new SpeciesNode(1);

    ArrayList<Integer> result = new ArrayList<Integer>();

    // Act
    Iterator<AbstractNode> it = node01.iterator();

    try {
      do {
        result.add(it.next().getId());
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }

    // Assert
    assertEquals("Number of nodes in the tree should be 1", 1, result.size());
    for (int i = 1; i <= result.size(); i++) {
      assertEquals("The " + i + "th element of the tree should be " + result.get(i - 1),
          i, result.get(i - 1).intValue());
    }
  }

  /*
   * Tests the NodeIterator of class AbstractNode with a tree of the form
   *
   *                      +----------15----------+
   *                      |                      |
   *                      4-----+          +-----10-----+
   *                            |          |            |
   *                         +--3--+    +--7--+      +--9
   *                         |     |    |     |      |
   *                         1     2    5     6      8
   *
   * That is an non-binary tree
   */
  @Test
  public void testNodeIterator4() {
    // Arrange
    SpeciesNode node11 = new SpeciesNode(11);
    SpeciesNode node04 = new SpeciesNode(4);
    SpeciesNode node10 = new SpeciesNode(10);
    node11.setLeftChild(node04);
    node11.setRightChild(node10);

    SpeciesNode node03 = new SpeciesNode(3);
    node04.setRightChild(node03);

    SpeciesNode node01 = new SpeciesNode(1);
    SpeciesNode node02 = new SpeciesNode(2);
    node03.setLeftChild(node01);
    node03.setRightChild(node02);

    SpeciesNode node07 = new SpeciesNode(7);
    SpeciesNode node09 = new SpeciesNode(9);
    node10.setLeftChild(node07);
    node10.setRightChild(node09);

    SpeciesNode node05 = new SpeciesNode(5);
    SpeciesNode node06 = new SpeciesNode(6);
    node07.setLeftChild(node05);
    node07.setRightChild(node06);

    SpeciesNode node08 = new SpeciesNode(8);
    node09.setLeftChild(node08);

    ArrayList<Integer> result = new ArrayList<Integer>();

    // Act
    Iterator<AbstractNode> it = node11.iterator();

    try {
      do {
        result.add(it.next().getId());
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }

    // Assert
    assertEquals("Number of nodes in the tree should be 11", 11, result.size());
    for (int i = 1; i <= result.size(); i++) {
      assertEquals("The " + i + "th element of the tree should be " + result.get(i - 1),
          i, result.get(i - 1).intValue());
    }
  }

  /*
   * Tests the NodeIterator of class AbstractNode for a subtree of the following tree
   *
   *                      +----------7----------+
   *                      |                     |
   *                +-----5-----+               |
   *                |           |               |
   *                |        +--4--+            |
   *                |        |     |            |
   *                1        2     3            6
   *
   */
  @Test
  public void testNodeIteratorSubtree() {
    // Arrange
    SpeciesNode node07 = new SpeciesNode(7);
    SpeciesNode node05 = new SpeciesNode(5);
    SpeciesNode node06 = new SpeciesNode(6);
    node07.setLeftChild(node05);
    node07.setRightChild(node06);

    SpeciesNode node01 = new SpeciesNode(1);
    SpeciesNode node04 = new SpeciesNode(4);
    node05.setLeftChild(node01);
    node05.setRightChild(node04);

    SpeciesNode node02 = new SpeciesNode(2);
    SpeciesNode node03 = new SpeciesNode(3);
    node04.setLeftChild(node02);
    node04.setRightChild(node03);

    ArrayList<Integer> result = new ArrayList<Integer>();

    // Act
    Iterator<AbstractNode> it = node05.iterator();

    try {
      do {
        result.add(it.next().getId());
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }

    // Assert
    assertEquals("Number of nodes in the subtree should be 5", 5, result.size());
    for (int i = 1; i <= result.size(); i++) {
      assertEquals("The " + i + "th element of the tree should be " + result.get(i - 1),
          i, result.get(i - 1).intValue());
    }
  }

  /*
   * Test that node iterator raises exception when iterating beyond the last node
   *
   *                +--3--+
   *                |     |
   *                1     2
   *
   */
  @Test(expected = NoSuchElementException.class)
  public void testIterateBeyondLastNodeIterator() {
    // Arrange
    SpeciesNode node03 = new SpeciesNode(3);
    SpeciesNode node01 = new SpeciesNode(1);
    SpeciesNode node02 = new SpeciesNode(2);

    node03.setLeftChild(node01);
    node03.setRightChild(node02);

    // Act
    Iterator<AbstractNode> it = node03.iterator();

    try {
      do {
        it.next();
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }
    it.next();
  }

  /*
   * Tests the LeafIterator of class AbstractNode with a tree of the form
   *
   *                      +----------+----------+
   *                      |                     |
   *                +-----+-----+         +-----+-----+
   *                |           |         |           |
   *             +--+--+     +--+--+   +--+--+     +--+--+
   *             |     |     |     |   |     |     |     |
   *             1     2     3     4   5     6     7     8
   *
   */
  @Test
  public void testLeafIterator1() {
    // Arrange
    SpeciesNode node1to8 = new SpeciesNode(0);
    SpeciesNode node1to4 = new SpeciesNode(0);
    SpeciesNode node5to8 = new SpeciesNode(0);
    node1to8.setLeftChild(node1to4);
    node1to8.setRightChild(node5to8);

    SpeciesNode node1to2 = new SpeciesNode(0);
    SpeciesNode node3to4 = new SpeciesNode(0);
    node1to4.setLeftChild(node1to2);
    node1to4.setRightChild(node3to4);

    SpeciesNode node1 = new SpeciesNode(1);
    SpeciesNode node2 = new SpeciesNode(2);
    node1to2.setLeftChild(node1);
    node1to2.setRightChild(node2);

    SpeciesNode node3 = new SpeciesNode(3);
    SpeciesNode node4 = new SpeciesNode(4);
    node3to4.setLeftChild(node3);
    node3to4.setRightChild(node4);

    SpeciesNode node5to6 = new SpeciesNode(0);
    SpeciesNode node7to8 = new SpeciesNode(0);
    node5to8.setLeftChild(node5to6);
    node5to8.setRightChild(node7to8);

    SpeciesNode node5 = new SpeciesNode(5);
    SpeciesNode node6 = new SpeciesNode(6);
    node5to6.setLeftChild(node5);
    node5to6.setRightChild(node6);

    SpeciesNode node7 = new SpeciesNode(7);
    SpeciesNode node8 = new SpeciesNode(8);
    node7to8.setLeftChild(node7);
    node7to8.setRightChild(node8);

    ArrayList<Integer> result = new ArrayList<Integer>();

    // Act
    Iterator<AbstractNode> it = node1to8.leafIterator();

    try {
      do {
        result.add(it.next().getId());
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }

    // Assert
    assertEquals("Number of leaves in the tree should be 8", 8, result.size());
    for (int i = 1; i <= result.size(); i++) {
      assertEquals("The " + i + "th leaf of the tree should be " + result.get(i - 1),
          i, result.get(i - 1).intValue());
    }
  }

  /*
   * Tests the LeafIterator of class AbstractNode with a tree of the form
   *
   *                      +----------+----------+
   *                      |                     |
   *                +-----+-----+               |
   *                |           |               |
   *                |        +--+--+            |
   *                |        |     |            |
   *                1        2     3            4
   *
   */
  @Test
  public void testLeafIterator2() {
    // Arrange
    SpeciesNode node1to4 = new SpeciesNode(0);
    SpeciesNode node1to3 = new SpeciesNode(0);
    SpeciesNode node4 = new SpeciesNode(4);
    node1to4.setLeftChild(node1to3);
    node1to4.setRightChild(node4);

    SpeciesNode node1 = new SpeciesNode(1);
    SpeciesNode node2to3 = new SpeciesNode(0);
    node1to3.setLeftChild(node1);
    node1to3.setRightChild(node2to3);

    SpeciesNode node2 = new SpeciesNode(2);
    SpeciesNode node3 = new SpeciesNode(3);
    node2to3.setLeftChild(node2);
    node2to3.setRightChild(node3);

    ArrayList<Integer> result = new ArrayList<Integer>();

    // Act
    Iterator<AbstractNode> it = node1to4.leafIterator();

    try {
      do {
        result.add(it.next().getId());
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }

    // Assert
    assertEquals("Number of leaves in the tree should be 4", 4, result.size());
    for (int i = 1; i <= result.size(); i++) {
      assertEquals("The " + i + "th leaf of the tree should be " + result.get(i - 1),
          i, result.get(i - 1).intValue());
    }
  }

  /*
   * Tests the LeafIterator of class AbstractNode with a tree of the form
   *
   *      1
   *
   * that is a tree only consisting of one node
   *
   */
  @Test
  public void testLeafIterator3() {
    // Arrange
    SpeciesNode node1 = new SpeciesNode(1);


    ArrayList<Integer> result = new ArrayList<Integer>();

    // Act
    Iterator<AbstractNode> it = node1.leafIterator();

    try {
      do {
        result.add(it.next().getId());
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }

    // Assert
    assertEquals("Number of leaves in the tree should be 1", 1, result.size());
    for (int i = 1; i <= result.size(); i++) {
      assertEquals("The " + i + "th leaf of the tree should be " + result.get(i - 1),
          i, result.get(i - 1).intValue());
    }
  }

  /*
   * Tests the NodeIterator of class AbstractNode with a tree of the form
   *
   *                      +----------+----------+
   *                      |                     |
   *                      +-----+         +-----+-----+
   *                            |         |           |
   *                         +--+--+   +--+--+     +--+
   *                         |     |   |     |     |
   *                         1     2   3     4     5
   *
   * That is an non-binary tree
   */
  @Test
  public void testLeafIterator4() {
    // Arrange
    SpeciesNode node1to5 = new SpeciesNode(0);
    SpeciesNode node1to2first = new SpeciesNode(0);
    SpeciesNode node3to5 = new SpeciesNode(0);
    node1to5.setLeftChild(node1to2first);
    node1to5.setRightChild(node3to5);

    SpeciesNode node1to2second = new SpeciesNode(0);
    node1to2first.setRightChild(node1to2second);

    SpeciesNode node1 = new SpeciesNode(1);
    SpeciesNode node2 = new SpeciesNode(2);
    node1to2second.setLeftChild(node1);
    node1to2second.setRightChild(node2);

    SpeciesNode node3to4 = new SpeciesNode(0);
    SpeciesNode node5to5 = new SpeciesNode(0);
    node3to5.setLeftChild(node3to4);
    node3to5.setRightChild(node5to5);

    SpeciesNode node3 = new SpeciesNode(3);
    SpeciesNode node4 = new SpeciesNode(4);
    node3to4.setLeftChild(node3);
    node3to4.setRightChild(node4);

    SpeciesNode node5 = new SpeciesNode(5);
    node5to5.setLeftChild(node5);

    ArrayList<Integer> result = new ArrayList<Integer>();

    // Act
    Iterator<AbstractNode> it = node1to5.leafIterator();

    try {
      do {
        result.add(it.next().getId());
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }

    // Assert
    assertEquals("Number of leaves in the tree should be 5", 5, result.size());
    for (int i = 1; i <= result.size(); i++) {
      assertEquals("The " + i + "th leaf of the tree should be " + result.get(i - 1),
          i, result.get(i - 1).intValue());
    }
  }

  /*
   * Tests the LeafIterator of class AbstractNode for a subtree of the following tree
   *
   *                      +----------+----------+
   *                      |                     |
   *                +-----+-----+               |
   *                |           |               |
   *                |        +--+--+            |
   *                |        |     |            |
   *                1        2     3            4
   *
   */
  @Test
  public void testLeafIteratorSubtree() {
    // Arrange
    SpeciesNode node1to4 = new SpeciesNode(0);
    SpeciesNode node1to3 = new SpeciesNode(0);
    SpeciesNode node4 = new SpeciesNode(4);
    node1to4.setLeftChild(node1to3);
    node1to4.setRightChild(node4);

    SpeciesNode node1 = new SpeciesNode(1);
    SpeciesNode node2to3 = new SpeciesNode(0);
    node1to3.setLeftChild(node1);
    node1to3.setRightChild(node2to3);

    SpeciesNode node2 = new SpeciesNode(2);
    SpeciesNode node3 = new SpeciesNode(3);
    node2to3.setLeftChild(node2);
    node2to3.setRightChild(node3);

    ArrayList<Integer> result = new ArrayList<Integer>();

    // Act
    Iterator<AbstractNode> it = node1to3.leafIterator();

    try {
      do {
        result.add(it.next().getId());
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }

    // Assert
    assertEquals("Number of leaves in the subtree should be 3", 3, result.size());
    for (int i = 1; i <= result.size(); i++) {
      assertEquals("The " + i + "th leaf of the subtree should be " + result.get(i - 1),
          i, result.get(i - 1).intValue());
    }
  }

  /*
   * Test that leaf iterator raises exception when iterating beyond the last node
   *
   *                +--+--+
   *                |     |
   *                1     2
   *
   */
  @Test(expected = NoSuchElementException.class)
  public void testIterateBeyondLastLeafIterator() {
    // Arrange
    SpeciesNode node1to2 = new SpeciesNode(0);
    SpeciesNode node1 = new SpeciesNode(1);
    SpeciesNode node2 = new SpeciesNode(2);

    node1to2.setLeftChild(node1);
    node1to2.setRightChild(node2);

    // Act
    Iterator<AbstractNode> it = node1to2.iterator();

    try {
      do {
        it.next();
      } while (it.hasNext());
    } catch (NumberFormatException e) {
      throw e;
    }
    it.next();
  }

}
