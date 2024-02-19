package de.unileipzig.nw18a.commontypes;

import static org.junit.Assert.assertEquals;

import de.unileipzig.nw18a.commontypes.Matrix.MatrixFormat;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class MatrixTest {

  private GeneRelation upperTriangular;
  private GeneRelation lowerTriangular;
  private GeneRelation fullMatrix;

  /**
   * Initialize the test cases using the following matrix and its upper and lower triangular
   * submatrices.
   *
   * <p>+-        -+
   *    |  = > < = |
   *    |  > < < = |
   *    |  < < > = |
   *    |  = = = = |
   *    +-        -+
   *
   */
  @Before
  public void setupExampleMatrices() {
    upperTriangular = new GeneRelation(GeneRelationType.RCB, MatrixFormat.UPPER, "");
    lowerTriangular = new GeneRelation(GeneRelationType.RCB, MatrixFormat.LOWER, "");
    fullMatrix      = new GeneRelation(GeneRelationType.RCB, MatrixFormat.BOTH,  "");

    upperTriangular.getRelation().add(new ArrayList<Byte>(Arrays.asList(
        GeneRelationType.EQUAL, GeneRelationType.GREATER_THAN,
        GeneRelationType.LESS_THAN, GeneRelationType.EQUAL)));
    upperTriangular.getRelation().add(new ArrayList<Byte>(Arrays.asList(
        GeneRelationType.LESS_THAN, GeneRelationType.LESS_THAN, GeneRelationType.EQUAL)));
    upperTriangular.getRelation().add(new ArrayList<Byte>(Arrays.asList(
        GeneRelationType.GREATER_THAN, GeneRelationType.EQUAL)));
    upperTriangular.getRelation().add(new ArrayList<Byte>(Arrays.asList(GeneRelationType.EQUAL)));

    lowerTriangular.getRelation().add(new ArrayList<Byte>(Arrays.asList(GeneRelationType.EQUAL)));
    lowerTriangular.getRelation().add(new ArrayList<Byte>(Arrays.asList(
        GeneRelationType.GREATER_THAN, GeneRelationType.LESS_THAN)));
    lowerTriangular.getRelation().add(new ArrayList<Byte>(Arrays.asList(
        GeneRelationType.LESS_THAN, GeneRelationType.LESS_THAN, GeneRelationType.GREATER_THAN)));
    lowerTriangular.getRelation().add(new ArrayList<Byte>(Arrays.asList(
        GeneRelationType.EQUAL, GeneRelationType.EQUAL,
        GeneRelationType.EQUAL, GeneRelationType.EQUAL)));

    fullMatrix.getRelation().add(new ArrayList<Byte>(Arrays.asList(
        GeneRelationType.EQUAL, GeneRelationType.GREATER_THAN,
        GeneRelationType.LESS_THAN, GeneRelationType.EQUAL)));
    fullMatrix.getRelation().add(new ArrayList<Byte>(Arrays.asList(
        GeneRelationType.GREATER_THAN, GeneRelationType.LESS_THAN,
        GeneRelationType.LESS_THAN, GeneRelationType.EQUAL)));
    fullMatrix.getRelation().add(new ArrayList<Byte>(Arrays.asList(
        GeneRelationType.LESS_THAN, GeneRelationType.LESS_THAN,
        GeneRelationType.GREATER_THAN, GeneRelationType.EQUAL)));
    fullMatrix.getRelation().add(new ArrayList<Byte>(Arrays.asList(
        GeneRelationType.EQUAL, GeneRelationType.EQUAL,
        GeneRelationType.EQUAL, GeneRelationType.EQUAL)));
  }


  @Test
  public void testNewFormatIsSetCorrectly() {
    // Arrange: (see above)

    // Act:
    fullMatrix.getRelation().changeFormat(MatrixFormat.UPPER);

    // Assert:
    assertEquals(MatrixFormat.UPPER, fullMatrix.getRelation().getFormat());
  }

  @Test
  public void testFullToLower() {
    // Arrange: (see above)

    // Act:
    fullMatrix.getRelation().changeFormat(MatrixFormat.LOWER);

    // Assert:
    assertEquals(lowerTriangular.getRelation(), fullMatrix.getRelation());
  }

  @Test
  public void testFullToUpper() {
    // Arrange: (see above)

    // Act:
    fullMatrix.getRelation().changeFormat(MatrixFormat.UPPER);

    // Assert:
    assertEquals(upperTriangular.getRelation(), fullMatrix.getRelation());
  }

  @Test
  public void testLowerToUpper() {
    // Arrange: (see above)

    // Act:
    lowerTriangular.getRelation().changeFormat(MatrixFormat.UPPER);

    // Assert:
    assertEquals(upperTriangular.getRelation(), lowerTriangular.getRelation());
  }

  @Test
  public void testLowerToFull() {
    // Arrange: (see above)

    // Act:
    lowerTriangular.getRelation().changeFormat(MatrixFormat.BOTH);

    // Assert:
    assertEquals(fullMatrix.getRelation(), lowerTriangular.getRelation());
  }

  @Test
  public void testUpperToLower() {
    // Arrange: (see above)

    // Act:
    upperTriangular.getRelation().changeFormat(MatrixFormat.LOWER);

    // Assert:
    assertEquals(lowerTriangular.getRelation(), upperTriangular.getRelation());
  }

  @Test
  public void testUpperToFull() {
    // Arrange: (see above)

    // Act:
    upperTriangular.getRelation().changeFormat(MatrixFormat.BOTH);

    // Assert:
    assertEquals(fullMatrix.getRelation(), upperTriangular.getRelation());
  }
}
