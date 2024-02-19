package de.unileipzig.nw18a.nexbuild.buildutils;

import java.io.IOException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NodeTest {

  Node node1;
  Node node2;
  Node node3;
  Node node4;
  Node node5;


  @BeforeEach
  void setUp() throws Exception {

    node1 = new Node("a");
    node2 = new Node("b");
    node3 = new Node("c");
    node4 = new Node("d");
    node5 = new Node("e");
    node1.addChild(node2);
    node1.addChild(node5);
    node2.addChild(node3);
    node2.addChild(node4);

  }

  @Test
  void testToNewick() throws IOException {
    String expected = "((c,d)b,e)a";
    String actual = node1.toNewick(node1);
    Assert.assertEquals(expected,actual);
  }
}
