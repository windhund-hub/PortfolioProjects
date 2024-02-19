package de.unileipzig.nw18a.nexbuild.buildutils;

import de.unileipzig.nw18a.commontypes.Gene;
import de.unileipzig.nw18a.commontypes.GeneRelation;
import de.unileipzig.nw18a.commontypes.GeneRelationType;
import de.unileipzig.nw18a.commontypes.Matrix.MatrixFormat;
import de.unileipzig.nw18a.commontypes.Triplet;
import de.unileipzig.nw18a.commontypes.TripletEntry;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BuildUtils {


  /**
   * This method constructs with regard to the RCB-relation of one given gene tree a set of gene
   * triplets, a set of species triplets with and a set of species with no constraints.
   *
   * @param rcbRelation gene relation of the type RCB.
   * @return geneTripletsResult container class for gene triplets, species triplets and
   no-constraint triplets.
   */
  public static GeneTripletsResult makeGeneTriplets(GeneRelation rcbRelation) {
    //Initialising container class and sets.
    GeneTripletsResult geneTripletsResult = new GeneTripletsResult(new LinkedHashSet<Triplet>(),
        new LinkedHashSet<Triplet>(), new LinkedHashSet<Triplet>(), rcbRelation.getTreeName());

    // expecting upper format matrix
    rcbRelation.getRelation().changeFormat(MatrixFormat.UPPER);

    // iterating over rows in matrix. last 2 rows don't need to be looked at, hence -2.
    for (int row = 0; row < rcbRelation.getRelation().size() - 2; row++) {
      //colLeft points to left hand side gene of triplet
      for (int colLeft = 1; colLeft < rcbRelation.getRelation().get(0).size() - 1 - row;
           colLeft++) {
        //colRight is the iterator to the right of colLeft.
        // Decreasing upper bound by row, because columns of matrix are shifted.
        for (int colRight = colLeft + 1; colRight < rcbRelation.getRelation().get(0).size() - row;
             colRight++) {

          //Extracting relations from the matrix. To differ between the combinations of the
          //relations it is useful to convert them from byte to int (0-->2,1-->3,-1-->5) and
          //save them in an Array.
          int[] tripleRelations = new int[4];
          tripleRelations[0] = relationToPrime(rcbRelation.getRelation().get(row).get(colLeft));
          tripleRelations[1] = relationToPrime(rcbRelation.getRelation().get(row).get(colRight));
          tripleRelations[2] = relationToPrime(rcbRelation.getRelation().get(colLeft + row)
              .get(colRight - colLeft));
          //Computing the product of the three primes to create unique checksum for switch/case.
          tripleRelations[3] = tripleRelations[0] * tripleRelations[1] * tripleRelations[2];

          //Creating an Array with the indices of the present loop. The indices row, colLeft,
          //colRight represent genes a,b,c. Depending on the respective case, changes
          //of the order of the indices row, colLeft, colRight might be needed.
          int[] tripleOfIndices = new int[3];
          tripleOfIndices[0] = row;
          tripleOfIndices[1] = colLeft + row;
          tripleOfIndices[2] = colRight + row;

          //There are six distinguished main cases, implied by the product of the primes:
          //tripleRelations[3] = tripleRelations[0] * tripleRelations[1] * tripleRelations[2].
          switch (tripleRelations[3]) {
            //Case 1.
            case 30:
              //All three RCB-relations are different.
              //Triple of indices can differ from default triple [3,5,2] in five ways.
              if (tripleRelations[0] == 2 && tripleRelations[1] == 5
                  || tripleRelations[0] == 5 && tripleRelations[1] == 3
                  || tripleRelations[0] == 3 && tripleRelations[1] == 2) {
                //1.1 [2,5,3], 1.2 [5,3,2], 1.3 [3,2,5]. It is necessary to mirror, by swapping the
                // first and the last index.
                tripleOfIndices[0] += tripleOfIndices[2];
                tripleOfIndices[2] = tripleOfIndices[0] - tripleOfIndices[2];
                tripleOfIndices[0] -= tripleOfIndices[2];
                //Change of the order of the relation too.
                tripleRelations[0] += tripleRelations[2];
                tripleRelations[2] = tripleRelations[0] - tripleRelations[2];
                tripleRelations[0] -= tripleRelations[2];
              }
              //1.4 [3,5,2]: Already right order.
              //1.5 [2,3,5]
              if (tripleRelations[0] == 2) {
                //1.5 Rotate index triple to the right.
                rotateTriple(tripleOfIndices, false);
              }
              //1.6 [5,2,3]
              if (tripleRelations[0] == 5) {
                //1.6 Rotate index triple to the left.
                rotateTriple(tripleOfIndices, true);
              }
              // Initialising a gene triplet (ac|b).
              Triplet geneTriplet1 = new Triplet(rcbRelation.getGenes().get(tripleOfIndices[0]),
                  rcbRelation.getGenes().get(tripleOfIndices[2]),
                  rcbRelation.getGenes().get(tripleOfIndices[1]));
              //Adding triplet to list in geneTripletResult.
              geneTripletsResult.getGeneTriplets().add(geneTriplet1);

              //Initialising a species triplet (AB|C).
              Triplet speciesTriplet1 =
                  new Triplet(rcbRelation.getGenes().get(tripleOfIndices[0]).getSpecies(),
                      rcbRelation.getGenes().get(tripleOfIndices[1]).getSpecies(),
                      rcbRelation.getGenes().get(tripleOfIndices[2]).getSpecies());
              //Checking if two Species of the Triplet are the same
              if (!(speciesTriplet1.getFirst().equals(speciesTriplet1.getSecond())
                  || speciesTriplet1.getFirst().equals(speciesTriplet1.getThird())
                  || speciesTriplet1.getSecond().equals(speciesTriplet1.getThird()))) {
                //Adding species triplet to list in geneTripletResult.
                geneTripletsResult.getSpeciesTriplets().add(speciesTriplet1);
              }
              break;

            //Case 2.
            case 12:
            case 20:
              //Two RCB-relations are of the type EQUAL.
              //Triple of indices can differ from default triples [3,2,2] or [5,2,2] in two ways.
              //2.1 [3,2,2] or [5,2,2]: Already right order.
              //2.2 [2,3,2] or [2,5,2]
              if (tripleRelations[1] == 3
                  || tripleRelations[1] == 5) {
                //2.2 Rotate index triple to the right.
                rotateTriple(tripleOfIndices, false);
              }
              //2.3 [2,2,3] or [2,2,5]
              if (tripleRelations[2] == 3
                  || tripleRelations[2] == 5) {
                //2.3 Rotate index triple to the left.
                rotateTriple(tripleOfIndices, true);
              }
              // Initialising a gene triplet (ab|c).
              Triplet geneTriplet2 = new Triplet(rcbRelation.getGenes().get(tripleOfIndices[0]),
                  rcbRelation.getGenes().get(tripleOfIndices[1]),
                  rcbRelation.getGenes().get(tripleOfIndices[2]));
              //Adding triplet to list in geneTripletResult.
              geneTripletsResult.getGeneTriplets().add(geneTriplet2);

              //Initialising a species triplet (AB|C).
              Triplet speciesTriplet2 =
                  new Triplet(rcbRelation.getGenes().get(tripleOfIndices[0]).getSpecies(),
                      rcbRelation.getGenes().get(tripleOfIndices[1]).getSpecies(),
                      rcbRelation.getGenes().get(tripleOfIndices[2]).getSpecies());
              //Checking if two Species of the Triplet are the same
              if (!(speciesTriplet2.getFirst().equals(speciesTriplet2.getSecond())
                  || speciesTriplet2.getFirst().equals(speciesTriplet2.getThird())
                  || speciesTriplet2.getSecond().equals(speciesTriplet2.getThird()))) {
                //Adding species triplet to list in geneTripletResult.
                geneTripletsResult.getSpeciesTriplets().add(speciesTriplet2);
              }
              break;

            //Case 3.
            case 18:
            case 45:
              //Two RCB-relations are of the type GREATER_THAN.
              //Triple of indices can differ from default triples [2,3,3] or [5,3,3] in two ways.
              //3.1 [2,3,3] or [5,3,3]: Already right order.
              //3.2 [3,5,3] or [3,2,3]
              if (tripleRelations[1] == 5
                  || tripleRelations[1] == 2) {
                //3.2 Rotate index triple to the right.
                rotateTriple(tripleOfIndices, false);
              }
              //3.3 [3,3,5] or [3,3,2]
              if (tripleRelations[2] == 5
                  || tripleRelations[2] == 2) {
                //3.3 Rotate index triple to the left.
                rotateTriple(tripleOfIndices, true);
              }
              // Initialising a gene triplet (ab|c).
              Triplet geneTriplet3 = new Triplet(rcbRelation.getGenes().get(tripleOfIndices[0]),
                  rcbRelation.getGenes().get(tripleOfIndices[1]),
                  rcbRelation.getGenes().get(tripleOfIndices[2]));
              //Adding triplet to list in geneTripletResult.
              geneTripletsResult.getGeneTriplets().add(geneTriplet3);
              //No species triplet to create.
              break;

            //Case 4.
            case 50:
            case 75:
              //Two RCB-relations are of the type LESS_THAN.
              //Triple of indices can differ from default triples [2,5,5] or [3,5,5] in two ways.
              //4.1 [2,5,5] or [3,5,5]: Already right order.
              //4.2 [5,2,5] or [5,3,5]
              if (tripleRelations[1] == 2
                  || tripleRelations[1] == 3) {
                //4.2 Rotate index triple to the right.
                rotateTriple(tripleOfIndices, false);
              }
              //4.3 [5,5,3] or [5,5,2]
              if (tripleRelations[2] == 3
                  || tripleRelations[2] == 2) {
                //4.3 Rotate index triple to the left.
                rotateTriple(tripleOfIndices, true);
              }
              //No gene triplets to build in case 4.
              //Initialising a species triplet (AB|C).
              Triplet speciesTriplet4 =
                  new Triplet(rcbRelation.getGenes().get(tripleOfIndices[0]).getSpecies(),
                      rcbRelation.getGenes().get(tripleOfIndices[1]).getSpecies(),
                      rcbRelation.getGenes().get(tripleOfIndices[2]).getSpecies());
              //Checking if two Species of the Triplet are the same
              if (!(speciesTriplet4.getFirst().equals(speciesTriplet4.getSecond())
                  || speciesTriplet4.getFirst().equals(speciesTriplet4.getThird())
                  || speciesTriplet4.getSecond().equals(speciesTriplet4.getThird()))) {
                //Adding species triplet to list in geneTripletResult.
                geneTripletsResult.getSpeciesTriplets().add(speciesTriplet4);
              }
              break;

            //Case 5.
            case 125:
            case 27:
              //All three RCB-relations are either of the type GREATER_THAN or of the type
              //LESS_THAN. No gene triplet and no species triplet to create. No switch of order
              //needed.
              break;

              //Case 6.
            case 8:
              // All three RCB-relations are of the type EQUAL. Initialising array for
              //no-constraint species similar {A,B,C}.
              Triplet noConstraintTriplet =
                  new Triplet(rcbRelation.getGenes().get(tripleOfIndices[0]),
                      rcbRelation.getGenes().get(tripleOfIndices[1]),
                      rcbRelation.getGenes().get(tripleOfIndices[2]), false);
              //Adding triplet to list in geneTripletResult.
              geneTripletsResult.getNoConstraintTriplets().add(noConstraintTriplet);
              break;
            default: throw new RuntimeException("Illegal RCBrelation in method "
                + "makeGeneTriplets.");
          }
        }
      }
    }
    return geneTripletsResult;
  }

  /**
   * Methode rotates values of an array to the left or to the right.
   *
   * @param tripleOfIndices array with three integer values.
   * @param rotation        Boolean describes direction of rotation. True is rotation to the left,
   *                        false is rotation to the right.
   */
  private static void rotateTriple(int[] tripleOfIndices, boolean rotation) {

    if (rotation) {
      //rotate left
      int copy = tripleOfIndices[0];
      tripleOfIndices[0] = tripleOfIndices[1];
      tripleOfIndices[1] = tripleOfIndices[2];
      tripleOfIndices[2] = copy;
    } else {
      //rotate right
      int copy = tripleOfIndices[0];
      tripleOfIndices[0] = tripleOfIndices[2];
      tripleOfIndices[2] = tripleOfIndices[1];
      tripleOfIndices[1] = copy;
    }
    return;
  }

  /**
   * Method replaces byte values of RCBrealtions with integers.
   * @param relationAsByte value of RCBrelations: 0==EQUAL,1==GREATER_THAN,-1==LESS_THAN
   * @return               value of RCBrelation as integer: 0--&gt;2,1--&gt;3,-1--&gt;5
   */
  private static int relationToPrime(byte relationAsByte) {
    int result = -1;
    switch (relationAsByte) {
      case GeneRelationType.EQUAL:
        result = 2;
        break;
      case GeneRelationType.GREATER_THAN:
        result = 3;
        break;
      case GeneRelationType.LESS_THAN:
        result = 5;
        break;
      default:
        // never reached
        break;
    }
    return result;
  }


  /**
   * This Method takes a set of triplets to compute the Ahograph of the given leafSet within the
   * given constraints. The algorithm should run in <i>O(|R|)</i> time. Where <i>R</i> represents
   * the given set of triplets.
   *
   * @param tripletSet Set of triplets, i.e. field with 3 entries of either Genes or Species to
   *                   represent triplets from gene relations.
   * @param leafSet    Set of leaves to initialise the keys for ahoGraph.
   * @return Ahograph for the given set of triplets as HashMap&lt;K, V&gt;, in which K are the
   vertices (leaves) and V their respective neighbours as LinkedHashSets.
   */
  public static Map<TripletEntry, Set<TripletEntry>> buildAhoGraph(
      Set<Triplet> tripletSet, Set<TripletEntry> leafSet) {

    // init return value
    Map<TripletEntry, Set<TripletEntry>> ahoGraph =
        new HashMap<TripletEntry, Set<TripletEntry>>();

    // iterate over leafSet to create vertices
    for (TripletEntry vertex : leafSet) {
      // create empty set for edges
      Set<TripletEntry> edges = new LinkedHashSet<TripletEntry>();

      // add vertex to graph
      ahoGraph.put(vertex, edges);
    }

    // iterate over set of triplets
    for (Triplet triplet : tripletSet) {
      // set vertices
      TripletEntry firstNode = triplet.getFirst();
      TripletEntry secondNode = triplet.getSecond();

      // add edges
      ahoGraph.get(firstNode).add(secondNode);
      ahoGraph.get(secondNode).add(firstNode);
    }
    return ahoGraph;
  }

  /**
   * This Method runs through graph and visits every neighbour of the given TripletEntry.
   *
   * @param gene  Gene which neighbour-genes will be searched.
   * @param visit Contains all genes that were visited.
   * @param graph The graph which will be searched.
   *              The graph is split in the key gene and the value neighbours.
   * @param component Component to which the gene will be added
   */
  public static void depthFirstSearch(
      TripletEntry gene, Set<TripletEntry> visit,
      Map<TripletEntry, Set<TripletEntry>> graph,
      Set<TripletEntry> component
  ) {
    visit.add(gene);
    component.add(gene);

    if (graph.containsKey(gene)) {
      if (!graph.get(gene).isEmpty()) {
        for (TripletEntry neighbour : graph.get(gene)) {
          if (!visit.contains(neighbour)) {

            depthFirstSearch(neighbour, visit, graph, component);
          }
        }
      }
    }
  }

  /**
   * This Method finds connected Components of a given graph.
   *
   * @param graph The graph which will be searched.
   *              The graph is split in the key gene and the value neighbours.
   * @return Components All connected components in the graph.
   */
  public static List<Set<TripletEntry>> buildConnectedComponents(
      Map<TripletEntry, Set<TripletEntry>> graph) {

    List<Set<TripletEntry>> componentSets = new LinkedList<Set<TripletEntry>>();
    HashSet<TripletEntry> visit = new HashSet<TripletEntry>();

    for (TripletEntry gene : graph.keySet()) {
      if (!visit.contains(gene)) {
        Set<TripletEntry> component = new LinkedHashSet<TripletEntry>();
        depthFirstSearch(gene, visit, graph, component);
        componentSets.add(component);
      }
    }
    return componentSets;
  }

  /**
   * Implements the BUILD algorithm as described in Semple and Steel (2003). Note that this
   * algorithm calls itself recursively.<br> The expected input type is a LinkedHashSet of Triplets,
   * from which the algorithm will extract the expected leaves and attempt to recursively build a
   * tree, consistent with the given triplets and vertices.<br>
   * BUILD is expected to run in <i>O(|tripleSet|#ofLeaves)</i> time.
   *
   * @param tripletSet LinkedHashSet of triplets
   * @param vertices   regarded vertices. Required to establish base cases.
   * @return Null, if triplet set is not consistent, root of built tree, else.
   */
  public static Node buildTree(Set<Triplet> tripletSet, Set<TripletEntry> vertices) {
    // init Node to return
    Node root = new Node("");

    // recursion base case 1: return single leaf node as root if |vertices| == 1
    if (vertices.size() == 1) {
      // setting up taxon
      TripletEntry taxon = vertices.iterator().next();
      // set root as single node of built tree and return
      return new Node(taxon.getName(), taxon);
    } else if (vertices.size() == 2) {
      // recursion base case 2: return leaves attached to root node if there are only two leaves
      for (TripletEntry taxon : vertices) {
        root.addChild(new Node(taxon.getName(), taxon));
      }
      return root;
    } else {
      // case 3: actual recursion
      // get connected components of Aho graph
      List<Set<TripletEntry>> connectedComponents =
          buildConnectedComponents(buildAhoGraph(tripletSet, vertices));
      if (connectedComponents.size() == 1) {
        // triplet set is not consistent
        return null;
      }
      // map connected components to their respective filtered triplet sets
      Map<Set<TripletEntry>, Set<Triplet>> mappedComponents =
          filterTriplets(connectedComponents, tripletSet);

      // call buildTree for each restricted triplet set and connected component
      for (Map.Entry<Set<TripletEntry>, Set<Triplet>> entry : mappedComponents.entrySet()) {
        // value = vertices of con. comp., key = corresponding connected component
        Node innerRoot = buildTree(entry.getValue(), entry.getKey());
        if (innerRoot != null) {
          // add edge {root, innerRoot}, i.e. add innerRoot to children of root
          root.addChild(innerRoot);
        } else {
          // triplet set is inconsistent
          return null;
        }
      }
      return root;
    }
  }

  /**
   * Writes out trees into files in Newick-Format .
   *
   * @param treeRootList Map with treeroot-nodes.
   * @param folderPath Path of the Folder where treefiles going to be written.
   * @throws IOException Error writing the file.
   */
  public static void writeOutTrees(Map<String, Node> treeRootList, String folderPath)
      throws IOException {

    for (Map.Entry<String, Node> tree : treeRootList.entrySet()) {
      File file = new File(folderPath + "/" + tree.getKey() + ".nwk");
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter output = new BufferedWriter(fw);
      output.write(tree.getValue().toNewick(tree.getValue()) + ";");
      output.close();
    }
  }

  /**
   * Writes out trees into stream in Newick-Format .
   *
   * @param treeRootList Map with treeroot-nodes.
   * @param stream Stream where trees going to be written.
   * @throws IOException Error writing to stream.
   */
  public static void writeOutTrees(Map<String, Node> treeRootList, OutputStream stream)
      throws IOException {

    String output = "";
    for (Map.Entry<String, Node> tree : treeRootList.entrySet()) {
      output = tree.getKey() + "\n";
      stream.write(output.getBytes(Charset.forName("UTF-8")));
      output = tree.getValue().toNewick(tree.getValue()) + ";\n";
      stream.write(output.getBytes(Charset.forName("UTF-8")));
    }
  }

  /**
   * This Method can be used to map a triplet set to a connected component of a graph, such that it
   * contains only triplets <i>ab|c</i> iff a,b and c are also nodes in the associated connected
   * component.
   *
   * @param connectedComponents LinkedList of connected components (represented by a LinkedHashSet).
   *                            Performance of this method can be enhanced by passing a sorted set
   *                            of connected components (ascending order of number of vertices).
   * @param triplets            LinkedHashSet of triplets.
   * @return Mapping (LinkedHashMap&lt;K, V&gt;), such that K is a connected component, represented
   by a LinkedHashSet, and V is the mapped set of triplets, also a LinkedHashSet.
   **/
  public static Map<Set<TripletEntry>, Set<Triplet>> filterTriplets(
      List<Set<TripletEntry>> connectedComponents, Set<Triplet> triplets)
      throws IllegalArgumentException {
    // throw exception if no components are passed
    if (connectedComponents.isEmpty()) {
      throw new IllegalArgumentException("Cannot filter triplets! "
          + "Given connected component list is empty.");
    }

    // create object to return
    Map<Set<TripletEntry>, Set<Triplet>> result =
        new LinkedHashMap<Set<TripletEntry>, Set<Triplet>>();

    // init K = connected component and V = triplet set for Map
    for (Set<TripletEntry> component : connectedComponents) {

      // init triplet set for current component
      Set<Triplet> tripletsForGivenComponent =
          new LinkedHashSet<Triplet>();

      // put both in map, component as key and respective triplet set as its value
      result.put(component, tripletsForGivenComponent);

    }


    // filter triplet set, and add triplets ab|c to mapped triplet set of
    // respective connected component, iff a and b and c are also vertices in current component
    for (Triplet triplet : triplets) {

      // iterate over connected components and map triplets to each
      for (Set<TripletEntry> component : connectedComponents) {


        // check if current component has enough vertices to contain a triplet
        if (component.size() < 3) {
          // next connected component
          continue;
        }

        // setting up bools for break-conditions given triplet ab|c
        boolean taxonAInComponent = component.contains(triplet.getFirst());
        boolean taxonBInComponent = component.contains(triplet.getSecond());
        boolean taxonCInComponent = component.contains(triplet.getThird());

        //check if current triplet ab|c is contained in current connected component
        if (taxonAInComponent && taxonBInComponent && taxonCInComponent) {
          result.get(component).add(triplet);
          // triplet cannot be in any more than one component
          break;
        }

        // break if any taxa of the triplet is vertex in current component
        if (taxonAInComponent || taxonBInComponent || taxonCInComponent) {
          break;
        }
      }
    }
    return result;
  }

  /**
   * Removes all triplets ab|c from the given triplet set where at least one of the taxas a,b or c
   * is not part of the given leaf set. Thereby only maintaining those triplets in the set, that are
   * defined on the leaf set.
   *
   * @param leafSet  Leaf set of taxa
   * @param triplets Set of triplets that is to be filtered
   */
  public static void filterTriplets(Set<TripletEntry> leafSet, Set<Triplet> triplets) {

    // check if leaf set has enough vertices to contain a triplet
    if (leafSet.size() < 3) {
      // next connected component
      triplets.clear();
    }

    // remove all triplets that are not defined on the given leaf set
    for (Iterator<Triplet> tripletIt = triplets.iterator(); tripletIt.hasNext(); ) {
      Triplet triplet = tripletIt.next();

      //check if current triplet ab|c is contained in current connected component
      if (!leafSet.contains(triplet.getFirst())
          || !leafSet.contains(triplet.getSecond())
          || !leafSet.contains(triplet.getThird())) {
        // triplet cannot be defined over the given leaf set
        tripletIt.remove();
      }
    }
  }

  /**
   * Given a set of triplets, this function computes the set of all taxa that appear in one (or
   * more) of those triplets.
   *
   * @param triplets Set of triplets of which all contained taxa are to be retrieved
   * @return Set of taxa contained in at least one of the given triplets
   */
  private static Set<TripletEntry> computeLeafSet(Set<Triplet> triplets) {
    Set<TripletEntry> leafSet = new LinkedHashSet<TripletEntry>();

    for (Triplet triplet : triplets) {
      leafSet.add(triplet.getFirst());
      leafSet.add(triplet.getSecond());
      leafSet.add(triplet.getThird());
    }

    return leafSet;
  }

  /**
   * Compute L<sup>*</sup><sub>R</sub>(R) for a given set R of triplets.
   *
   * <p>See arXiv:1707.01667v2 for more detailed information on the meaning and the algorithm for
   * the computation of L<sup>*</sup><sub>R</sub>(R).
   *
   * @param triplets Triplet set R for which to compute L<sup>*</sup><sub>R</sub>(R).
   * @return L<sup>*</sup><sub>R</sub>(R). The innermost sets are nodes of connected
   components of Aho-graphs, the middle sets contain exactly two such components
   each, and the outer set contains multiple of such unordered pairs.
   */
  private static Set<Set<Set<TripletEntry>>> computeLStar(Set<Triplet> triplets) {
    Set<Set<Set<TripletEntry>>> lstar = new LinkedHashSet<Set<Set<TripletEntry>>>();

    // contains LinkedHashSet
    Set<TripletEntry> leafSet = computeLeafSet(triplets);

    // will act as input for the aho graph
    Set<TripletEntry> selectedNodes = new HashSet<TripletEntry>();
    Set<Triplet> selectedTriplets = new LinkedHashSet<Triplet>();

    // for each triplet ab|c
    for (Triplet triplet : triplets) {

      // start with complete leaf and triplet set
      selectedNodes.addAll(leafSet);
      selectedTriplets.addAll(triplets);

      // will hold a connected component containing a or b of the triplet ab|c
      Set<TripletEntry> compAB;
      // will hold a connected component containing c of the triplet ab|c
      Set<TripletEntry> compC;

      while (true) {

        // compute aho from triplets and current version of selectedNodes
        // (note that either this is the first iteration of the loop and thus selectedNodes
        // holds exactly the leaf set of the whole triplet set, or we are in a later iteration and
        // thus the triplets have been filtered as last action in the preceding iteration, either
        // way the current set of triplets is defined on the current set of taxa)
        Map<TripletEntry, Set<TripletEntry>> ahoGraph = buildAhoGraph(
            selectedTriplets, selectedNodes);

        // find connected components of aho graph
        List<Set<TripletEntry>> connectedComponents = buildConnectedComponents(ahoGraph);

        // get component that contains one of the more closely related taxa, i.e. for
        // ab|c the one containing a or b and the one containing the less closely related taxon
        // i.e. c
        compAB = null;
        compC = null;
        for (Set<TripletEntry> component : connectedComponents) {
          if (compAB == null && component.contains(triplet.getFirst())) {
            compAB = component;
          }
          if (compC == null && component.contains(triplet.getThird())) {
            compC = component;
          }
          if (compAB != null && compC != null) {
            break;
          }
        }

        // if # of connected components is exactly two and the above components differ, we break
        // from this loop
        if (connectedComponents.size() == 2 && compAB != compC) {
          break;
        }

        // else we set selectedNodes as the union of these two components (note that size is a
        // O(1) operation, so chose the bigger one and add the smaller one to it)
        selectedNodes = (compAB.size() > compC.size() ? compAB : compC);
        selectedNodes.addAll(compAB.size() > compC.size() ? compC : compAB);
        // finally we prepare the next iteration by filtering out the triplets that are no longer
        // defined on the new leaf set given by selectedNodes
        filterTriplets(selectedNodes, selectedTriplets);
      }

      // update L^* by adding the two newly found components to it
      // note that add in this case really checks for equality on the basis of the elements in
      // compAB and compC respectively and not only their addresses
      Set<Set<TripletEntry>> lstarEntry = new HashSet<Set<TripletEntry>>();
      lstarEntry.add(compAB);
      lstarEntry.add(compC);
      lstar.add(lstarEntry);
    }

    return lstar;
  }

  /**
   * Computes the closure of a given set of triplets in-situ.
   *
   * @param triplets Set R of triplets of which the closure is to be computed
   */
  public static void calculateClosure(Set<Triplet> triplets) {
    // compute L^*
    Set<Set<Set<TripletEntry>>> lstar = computeLStar(triplets);

    // Note that the following nested loops will run in O(|L_R|**3) by construction of the
    // components of L^*.

    // for each ordered tuple of connected components in lstar (i.e. its elements) (A,B)
    for (Set<Set<TripletEntry>> lstarEntry : lstar) {
      for (Set<TripletEntry> compA : lstarEntry) {
        for (Set<TripletEntry> compB : lstarEntry) {
          if (compA == compB) {
            continue;
          }
          // for each triplet ab|c where a and b are contained in A and c is contained in B
          for (TripletEntry taxonAinA : compA) {
            for (TripletEntry taxonBinA : compA) {
              // triplets of the form aa|x are inadmissible
              if (taxonAinA == taxonBinA) {
                continue;
              }
              for (TripletEntry taxonCinB : compB) {
                // triplets of the form ax|a or xa|a are also inadmissible
                // with other words a,b and c will have to be different TripletEntries
                if (taxonAinA == taxonCinB || taxonBinA == taxonCinB) {
                  continue;
                }
                // add each admissible triplet to the set of triplets
                triplets.add(new Triplet(taxonAinA, taxonBinA, taxonCinB));
              }
            }
          }
        }
      }
    }
  }


  public static class GeneTripletsResult {

    private Set<Triplet> geneTriplets;
    private Set<Triplet> speciesTriplets;
    private Set<Triplet> noConstraintTriplets;
    private String treeName;

    public GeneTripletsResult() {

    }

    /**
     * Container class for gene triplets, species triplets and no-constraint triplets.
     *
     * @param geneTriplets         gene triplets
     * @param speciesTriplets      species triplets
     * @param noConstraintTriplets no-constrain triplets
     * @param treeName             Name of tree
     */
    public GeneTripletsResult(Set<Triplet> geneTriplets, Set<Triplet> speciesTriplets,
                              Set<Triplet> noConstraintTriplets, String treeName) {
      this.geneTriplets = geneTriplets;
      this.speciesTriplets = speciesTriplets;
      this.noConstraintTriplets = noConstraintTriplets;
      this.treeName = treeName;
    }

    /**
     *  Searches through noConstraintTriplets for a triplet of genes of which the corresponding
     *  species are constraint by a triplet from speciesTriplets and if so adds the corresponding
     *  gene triplet to geneTriplets.
     *
     *  <p>For example if (abc)'s species triplet (ABC) is constraint via a triplet AB|C in
     *  speciesTriplets we add (ab|c) to the set of gene triplets.
     *  Furthermore noConstraintTriplets will be cleared by this function as it holds no relevant
     *  information for this GeneTripletsResult after the extraction of potential triplets from it.
     * 
     * @param speciesTriplets Triplets from species triplet, that species may be constraint by
     */
    public void completeGeneTriplets(Set<Triplet> speciesTriplets) {

      // for each gene triplet that represents no constraint, check whether there is a species
      // triplet that constrains the involved species and if so, add the corresponding gene triplet

      // for each triplet abc where a in species A, b in species B and  c in species C
      for (Triplet triplet : noConstraintTriplets) {
        // check for existence of triplet AB|C or BA|C
        if (speciesTriplets.contains(
            new Triplet(((Gene)triplet.getFirst()).getSpecies(),
                        ((Gene)triplet.getSecond()).getSpecies(),
                        ((Gene)triplet.getThird()).getSpecies()))) {
          // add triplet ab|c to gene triplets
          geneTriplets.add(
              new Triplet(triplet.getFirst(), triplet.getSecond(), triplet.getThird()));
          continue;
        }

        // check for existence of triplet AC|B or CA|B
        if (speciesTriplets.contains(
            new Triplet(((Gene)triplet.getFirst()).getSpecies(),
                        ((Gene)triplet.getThird()).getSpecies(),
                        ((Gene)triplet.getSecond()).getSpecies()))) {
          // add triplet ac|b to gene triplets
          geneTriplets.add(
              new Triplet(triplet.getFirst(), triplet.getThird(), triplet.getSecond()));
          continue;
        }

        // check for existence of triplet BC|A or CB|B
        if (speciesTriplets.contains(
            new Triplet(((Gene)triplet.getSecond()).getSpecies(),
                        ((Gene)triplet.getThird()).getSpecies(),
                        ((Gene)triplet.getFirst()).getSpecies()))) {
          // add triplet bc|a to gene triplets
          geneTriplets.add(
              new Triplet(triplet.getSecond(), triplet.getThird(), triplet.getFirst()));
          continue;
        }
      }

      // remove all unconstraint triplets:
      noConstraintTriplets.clear();
    }

    public Set<Triplet> getGeneTriplets() {
      return geneTriplets;
    }

    public void setGeneTriplets(Set<Triplet> geneTriplets) {
      this.geneTriplets = geneTriplets;
    }

    public Set<Triplet> getSpeciesTriplets() {
      return speciesTriplets;
    }

    public void setSpeciesTriplets(Set<Triplet> speciesTriplets) {
      this.speciesTriplets = speciesTriplets;
    }

    public Set<Triplet> getNoConstraintTriplets() {
      return noConstraintTriplets;
    }

    public void setNoConstraintTriplets(Set<Triplet> noConstraintTriplets) {
      this.noConstraintTriplets = noConstraintTriplets;
    }

    public String getTreeName() {
      return treeName;
    }

    public void setTreeName(String treeName) {
      this.treeName = treeName;
    }
  }
}
