package de.unileipzig.nw18a.nexmatch.matchutils;

import static org.junit.jupiter.api.Assertions.fail;

import de.unileipzig.nw18a.commontypes.Gene;
import de.unileipzig.nw18a.commontypes.GeneDistances;
import de.unileipzig.nw18a.commontypes.Matrix;
import de.unileipzig.nw18a.commontypes.Species;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


class BestMatchGraphTest {
  /**
   * The test will have this distance Matrix:
   *     {a} [b] (c) (d)
   * {a}  0   1   2   3
   * [b]  1   0   3   4
   * (c)  2   4   0   0.5
   * (d)  3   3  0.5  0
   * {}=S1 []=S2 ()=S3
   */
  @Test
  void testBestMatchGraphGeneDistancesString() {
    Species s1 = new Species("S1");
    Species s2 = new Species("S2");
    Species s3 = new Species("S3");
    Gene a = new Gene("a", s1);
    Gene b = new Gene("b", s2);
    Gene c = new Gene("c", s3);
    Gene d = new Gene("d", s3);
    GeneDistances gd = new GeneDistances(Matrix.MatrixFormat.BOTH, "Testtree");
    
    gd.getGenes().add(a);
    gd.getGenes().add(b);
    gd.getGenes().add(c);
    gd.getGenes().add(d);
    
    ArrayList<Double> distancesa = new ArrayList<Double>();
    distancesa.add(0.0);
    distancesa.add(1.0);
    distancesa.add(2.0);
    distancesa.add(3.0);
    ArrayList<Double> distancesb = new ArrayList<Double>();
    distancesb.add(1.0);
    distancesb.add(0.0);
    distancesb.add(4.0);
    distancesb.add(3.0);
    ArrayList<Double> distancesc = new ArrayList<Double>();
    distancesc.add(2.0);
    distancesc.add(4.0);
    distancesc.add(0.0);
    distancesc.add(0.5);
    ArrayList<Double> distancesd = new ArrayList<Double>();
    distancesd.add(3.0);
    distancesd.add(3.0);
    distancesd.add(0.5);
    distancesd.add(0.0);
    gd.getDistances().add(distancesa);
    gd.getDistances().add(distancesb);
    gd.getDistances().add(distancesc);
    gd.getDistances().add(distancesd);
    
    ArrayList<Gene[]> bestMatches = new ArrayList<Gene[]>();
    bestMatches.add(new Gene[] {a,c});
    bestMatches.add(new Gene[] {a,b});
    bestMatches.add(new Gene[] {b,a});
    bestMatches.add(new Gene[] {b,d});
    bestMatches.add(new Gene[] {c,a});
    bestMatches.add(new Gene[] {c,b});
    bestMatches.add(new Gene[] {d,a});
    bestMatches.add(new Gene[] {d,b});

    BestMatchGraph bmg = new BestMatchGraph(gd);

    //Because the Order of the Arraylist differs from System to System it has to be done like that
    //The loops test if everery toubple that should be in the 
    //bestMatch list is in the bestMatch list
    boolean[] contains = new boolean[8];
    for (int i = 0; i < 8; i++) {
      //Because ArrayList.contains() doesn't work it has to be done like that
      for (int j = 0; j < bmg.getBestMatches().size();j++) {
        if (bmg.getBestMatches().get(j)[0].equals(bestMatches.get(i)[0])
            && bmg.getBestMatches().get(j)[1].equals(bestMatches.get(i)[1])) {
          contains[i] = true;
        } 
      }
    }
    
    boolean[] testcontains = new boolean[] {true,true,true,true,true,true,true,true};
    Assert.assertArrayEquals(testcontains, contains);
    //Here it's tested if there are touples in the bestMatch list,
    //that shouldn't be in the bestMatch list
    Assert.assertEquals(bmg.getBestMatches().size(), 8);
  }

  @Test
  void testBestMatchGraphGeneDistancesStringBoolean() {
    Species s1 = new Species("S1");
    Species s2 = new Species("S2");
    Species s3 = new Species("S3");
    Gene a = new Gene("a", s1);
    Gene b = new Gene("b", s2);
    Gene c = new Gene("c", s3);
    Gene d = new Gene("d", s3);
    GeneDistances gd = new GeneDistances(Matrix.MatrixFormat.BOTH, "Testtree");
    
    gd.getGenes().add(a);
    gd.getGenes().add(b);
    gd.getGenes().add(c);
    gd.getGenes().add(d);
    
    ArrayList<Double> distancesa = new ArrayList<Double>();
    distancesa.add(0.0);
    distancesa.add(1.0);
    distancesa.add(2.0);
    distancesa.add(3.0);
    ArrayList<Double> distancesb = new ArrayList<Double>();
    distancesb.add(1.0);
    distancesb.add(0.0);
    distancesb.add(4.0);
    distancesb.add(3.0);
    ArrayList<Double> distancesc = new ArrayList<Double>();
    distancesc.add(2.0);
    distancesc.add(4.0);
    distancesc.add(0.0);
    distancesc.add(0.5);
    ArrayList<Double> distancesd = new ArrayList<Double>();
    distancesd.add(3.0);
    distancesd.add(3.0);
    distancesd.add(0.5);
    distancesd.add(0.0);
    gd.getDistances().add(distancesa);
    gd.getDistances().add(distancesb);
    gd.getDistances().add(distancesc);
    gd.getDistances().add(distancesd);
    
    ArrayList<Gene[]> inReciprocal = new ArrayList<Gene[]>();
    inReciprocal.add(new Gene[] {a,c});
    inReciprocal.add(new Gene[] {a,b});
    inReciprocal.add(new Gene[] {b,a});
    inReciprocal.add(new Gene[] {b,d});
    inReciprocal.add(new Gene[] {c,a});
    inReciprocal.add(new Gene[] {d,b});
    
    //This is the only way to test the isInReciprocal array, because one 
    //doesn't know the Order of it before calculating the bestMatchgraph
    boolean[] isInReciprocal = new boolean[8];
    BestMatchGraph bmg = new BestMatchGraph(gd, true);
    for (int i = 0; i < bmg.getBestMatches().size(); i++) {
      //Because ArrayList.contains() doesn't work it has to be done like that
      for (int j = 0; j < 6;j++) {
        if (bmg.getBestMatches().get(i)[0].equals(inReciprocal.get(j)[0])
            && bmg.getBestMatches().get(i)[1].equals(inReciprocal.get(j)[1])) {
          isInReciprocal[i] = true;
        } 
      }
    }
    //This has to be done because isinreciprocal.toArray makes Object[] and not boolean[]
    boolean[] testedValues = new boolean[bmg.getIsInReciprocal().size()];
    for (int i = 0; i < testedValues.length;i++) {
      testedValues[i] = bmg.getIsInReciprocal().get(i);
    }
    Assert.assertArrayEquals(testedValues,isInReciprocal);
  }

}
