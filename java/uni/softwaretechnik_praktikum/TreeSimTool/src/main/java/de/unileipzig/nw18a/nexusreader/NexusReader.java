package de.unileipzig.nw18a.nexusreader;

import de.unileipzig.nw18a.commontypes.Gene;
import de.unileipzig.nw18a.commontypes.GeneDistances;
import de.unileipzig.nw18a.commontypes.GeneRelation;
import de.unileipzig.nw18a.commontypes.GeneRelationType;
import de.unileipzig.nw18a.commontypes.Matrix.MatrixFormat;
import de.unileipzig.nw18a.commontypes.Species;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NexusReader {

  private static final String SINGLE_DIST_KEY = "$DEFAULT$";

  private HashMap<String, Species> speciesMap;
  /**
   * Map (SrcTreeName, Map(SrcGeneName, [DestTree, DestGene, OnEdge])).
   */
  private HashMap<String, HashMap<String, String[]>> mappingMap;
  private HashMap<GeneRelationType, List<GeneRelation>> relationMap;
  private HashMap<String, GeneDistances> distanceMap;

  /**
   * Opens a nexus file and reads in all data that is important to this program.
   * @param filePath path to the nexus file
   * @throws IOException error reading the file
   * @throws NexusFormatException error parsing the file
   */
  public NexusReader(String filePath) throws IOException, NexusFormatException {

    BufferedReader bufferedReader = null;
    FileReader fileReader = null;
    try {
      fileReader = new FileReader(filePath);
      bufferedReader = new BufferedReader(fileReader);

      int state = 0; //0=start of file, 1=after #NEXUS. 2=inside block
      boolean commentMode = false;
      TreeMap<Integer, String> blockLines = null;
      String blockName = null;
      Matcher beginBlockMatcher = Pattern.compile("^(?i)BEGIN ([A-Z]+);").matcher("");
      String line = bufferedReader.readLine();
      int lineNumber = 0;
      while (line != null) {
        lineNumber++;
        line = line.trim();
        //end multiline comments
        if (commentMode) {
          int endIndex = line.indexOf("]");
          if (endIndex == -1) {
            line = bufferedReader.readLine();
            continue;
          } else {
            line = line.substring(endIndex);
            commentMode = false;
          }
        }
        //remove one line comments
        line = line.replaceAll("\\[.*]", "");
        //remove multi line comments
        int startIndex = line.indexOf("[");
        if (startIndex != -1) {
          line = line.substring(0, startIndex);
          commentMode = true;
        }
        //skip emtpy lines
        if (line.isEmpty()) {
          line = bufferedReader.readLine();
          continue;
        }

        //===read lines===
        switch (state) { //0=start of file, 1=after #NEXUS. 2=inside block
          case 0:
            if (!line.equalsIgnoreCase("#NEXUS")) {
              throw new NexusFormatException("File must start with #NEXUS. Line: " + lineNumber);
            } else {
              state++;
            }
            break;
          case 1:
            beginBlockMatcher.reset(line);
            if (beginBlockMatcher.matches()) {
              blockLines = new TreeMap<Integer, String>();
              blockName = beginBlockMatcher.group(1);
              state++;
            } else {
              throw new NexusFormatException("Error parsing BEGIN statement. Line " + lineNumber);
            }
            break;
          case 2:
            if (line.equalsIgnoreCase("END;")) {
              makeBlock(blockName, blockLines);
              state--;
            } else {
              blockLines.put(lineNumber, line);
            }
            break;
          default:
            throw new RuntimeException("NexusReader reached illegal state: " + state);
        }

        line = bufferedReader.readLine();
      }
      if (state == 2) {
        throw new NexusFormatException("Didn't close block at end of file.");
      }
      if (state == 0) {
        throw new NexusFormatException("File seems empty.");
      }

      bufferedReader.close();

    } catch (IOException e) {
      if (bufferedReader != null) {
        bufferedReader.close();
      }
      if (fileReader != null) {
        fileReader.close();
      }
      throw e;
    } catch (NexusFormatException e) {
      if (bufferedReader != null) {
        bufferedReader.close();
      }
      if (fileReader != null) {
        fileReader.close();
      }
      throw e;
    }
  }

  private void makeBlock(String blockName, Map<Integer, String> lines)
      throws NexusFormatException {
    if (blockName.equalsIgnoreCase("TAXA")) {
      readInSpecies(lines);
    } else if (blockName.equalsIgnoreCase("TREES")) {
      readInTrees(lines);
    } else if (blockName.equalsIgnoreCase("RECONCILIATION")) {
      readInMapping(lines);
    } else if (blockName.equalsIgnoreCase("RELATIONS")) {
      readInRelations(lines);
    } else if (blockName.equalsIgnoreCase("ALLDISTANCES")) {
      readInAllDistances(lines);
    } else if (blockName.equalsIgnoreCase("DISTANCES")) {
      readInSingleDistances(lines);
    }
    //ignore unknown blocks

  }

  private void readInSingleDistances(Map<Integer, String> lines) throws NexusFormatException {

    if (distanceMap == null) {
      distanceMap = new HashMap<String, GeneDistances>();
    }

    MatrixFormat format = null;
    List<ArrayList<Double>> matrix = null;
    List<Gene> geneList = null;

    int state = 0; //0=out of command, 1=in format, 2=in matrix
    for (Map.Entry<Integer, String> lineEntry : lines.entrySet()) {

      int lineNumber = lineEntry.getKey();
      String line = lineEntry.getValue();

      String[] commands = line.split("\\s+");
      if (state == 0 && commands[0].equalsIgnoreCase("format")) {
        state = 1;
        if (commands.length > 1) {
          line = line.substring(6);
          line = line.trim();
          commands = line.split("\\s+");
        } else {
          continue;
        }
      }
      if (state == 0 && commands[0].equalsIgnoreCase("matrix")) {
        state = 2;
        matrix = new ArrayList<ArrayList<Double>>();
        geneList = new ArrayList<Gene>();
        if (commands.length > 1) {
          line = line.substring(6);
          line = line.trim();
          commands = line.split("\\s+");
        } else {
          continue;
        }
      }
      if (state == 1) { //in format
        for (String command : commands) {
          String[] data = command.split("[=;]");
          if (data.length != 2) {
            throw new NexusFormatException("No data given to sub-command. Line: " + lineNumber);
          }
          if (data[0].equalsIgnoreCase("triangle")) {
            try {
              format = MatrixFormat.valueOf(data[1].toUpperCase());
            } catch (IllegalArgumentException e) {
              throw new NexusFormatException(
                  String.format("Matrix format unknown: %s. Line: %d", data[1], lineNumber));
            }
          }
          //ignore unknown sub-commands
        }
        if (line.endsWith(";")) {
          state = 0;
          continue;
        }
      }
      if (state == 2) { //in matrix
        if (line.equals(";")) {
          state = 0;
        } else {
          geneList.add(
              new Gene(commands[0], findSpeciesForGene(commands[0], lineNumber)));
          ArrayList<Double> row = new ArrayList<Double>();
          for (int i = 1; i < commands.length; i++) {
            if (commands[i].equals(";")) {
              continue;
            } else if (commands[i].endsWith(";")) {
              commands[i] = commands[i].replace(";", "");
            }
            try {
              row.add(Double.valueOf(commands[i]));
            } catch (NumberFormatException e) {
              throw new NexusFormatException("Cannot read double number. Line: " + lineNumber, e);
            }
          }
          matrix.add(row);

          if (line.endsWith(";")) {
            state = 0;
          }
        }
      }
      //ignore everything unknown, let matrix and format commands be overridden
    }
    //finished reading, should have all data
    if (format != null && matrix != null) {
      GeneDistances distances = new GeneDistances(format, SINGLE_DIST_KEY);
      distances.getGenes().addAll(geneList);
      distances.getDistances().addAll(matrix);
      distanceMap.put(SINGLE_DIST_KEY, distances);
    } else {
      throw new NexusFormatException("Not enough information in the DISTANCES block. "
         + "Format and/or matrix is missing.");
    }
  }

  private void readInAllDistances(Map<Integer, String> lines) throws NexusFormatException {

    int state = 0; //0=out of command, 1=in config, 2=in matrix, 3=end of matrix
    if (distanceMap == null) {
      distanceMap = new HashMap<String, GeneDistances>();
    }

    String curName = null;
    MatrixFormat curFormat = null;
    GeneDistances curDistances = null;
    for (Map.Entry<Integer, String> lineEntry : lines.entrySet()) {
      int lineNumber = lineEntry.getKey();
      String line = lineEntry.getValue();

      String[] commands = line.split("\\s+");
      if (state == 0 && commands[0].equalsIgnoreCase("distances")) {
        state++;
        if (commands.length > 1) {
          line = line.substring(9);
          line = line.trim();
          commands = line.split("\\s+");
        } else {
          continue;
        }
      }
      if (state == 1) {
        for (String command : commands) {
          String[] data = command.split("=");
          if (data.length != 2) {
            throw new NexusFormatException("No data given to sub-command. Line: " + lineNumber);
          }
          if (data[0].equalsIgnoreCase("name")) {
            curName = data[1];
          } else if (data[0].equalsIgnoreCase("triangle")) {
            try {
              curFormat = MatrixFormat.valueOf(data[1].toUpperCase());
            } catch (IllegalArgumentException e) {
              throw new NexusFormatException(
                  String.format("Matrix format unknown: %s. Line: %d", data[1], lineNumber));
            }
          }
          //ignore unknown sub-commands
        }

        if (curName != null && curFormat != null) {
          curDistances = new GeneDistances(curFormat, curName);
          state++;
        }
      } else if (state == 2) {
        //read in matrix
        if (line.equals(";")) {
          state++;
        } else {
          curDistances.getGenes().add(
              new Gene(commands[0], findSpeciesForGene(commands[0], lineNumber)));
          ArrayList<Double> row = new ArrayList<Double>();
          for (int i = 1; i < commands.length; i++) {
            if (commands[i].equals(";")) {
              continue;
            } else if (commands[i].endsWith(";")) {
              commands[i] = commands[i].replace(";", "");
            }
            try {
              row.add(Double.valueOf(commands[i]));
            } catch (NumberFormatException e) {
              throw new NexusFormatException("Cannot read double number. Line: " + lineNumber, e);
            }
          }
          curDistances.getDistances().add(row);

          if (line.endsWith(";")) {
            state++;
          }
        }
        if (state == 3) {
          //add distances to map
          distanceMap.put(curName, curDistances);
          //accept new ones
          state = 0;
        }
      }
      //ignore other commands
    }

  }

  private void readInMapping(Map<Integer, String> lines) throws NexusFormatException {

    mappingMap = new HashMap<String, HashMap<String, String[]>>();

    boolean inMapping = false;
    int mappingLine = -1;
    for (Map.Entry<Integer, String> lineEntry : lines.entrySet()) {
      String line = lineEntry.getValue();
      int lineNumber = lineEntry.getKey();

      String[] commands = line.split(" ", 2);
      //ignore other commands
      if (!inMapping && commands[0].equalsIgnoreCase("MAPPING")) {
        mappingLine = lineNumber;
        inMapping = true;
        if (commands.length > 1) {
          line = commands[1];
        } else {
          continue;
        }
      }

      if (inMapping) {
        if (line.equals(";")) {
          inMapping = false;
          break;
        }

        String[] mappings = line.split(",");
        for (String mapping : mappings) {
          String[] mapEntries = mapping.split(" ", 6);
          if (mapEntries.length != 5) {
            throw new NexusFormatException("Mapping entry must have size 5. Line: " + lineNumber);
          }
          //add mapping entry
          HashMap<String, String[]> geneMap = mappingMap.get(mapEntries[0]);
          if (geneMap == null) {
            //add missing tree
            geneMap = new HashMap<String, String[]>();
            mappingMap.put(mapEntries[0], geneMap);
          }
          geneMap.put(
              mapEntries[1], new String[]{mapEntries[2], mapEntries[3], mapEntries[4]}
          );
        }
        if (line.endsWith(";")) {
          inMapping = false;
          break;
        }
      }
    }
    if (inMapping) {
      throw new NexusFormatException("Mapping not closed with a semicolon. Line: " + mappingLine);
    }
  }

  private void readInTrees(Map<Integer, String> lines) {
    //TODO
  }

  private void readInSpecies(Map<Integer, String> lines) throws NexusFormatException {

    speciesMap = new HashMap<String, Species>();

    boolean inLables = false;
    int ntax = -1;
    int dimensionsLine = -1;
    int taxlabelsLine = -1;
    for (Map.Entry<Integer, String> lineEntry : lines.entrySet()) {
      String line = lineEntry.getValue();
      int lineNumber = lineEntry.getKey();

      String[] commands = line.split(" ");
      //DIMENSIONS
      if (!inLables && commands[0].equalsIgnoreCase("DIMENSIONS")) {
        Matcher dimMatcher = Pattern.compile("(?i)DIMENSIONS NTAX ?= ?(\\d+);").matcher(line);
        if (dimMatcher.matches()) {
          ntax = Integer.valueOf(dimMatcher.group(1));
          dimensionsLine = lineNumber;
        } else {
          throw new NexusFormatException(
              "DIMENSIONS command syntax error. Line: " + lineNumber);
        }
      }
      //TAXLABEL
      if (!inLables && commands[0].equalsIgnoreCase("TAXLABELS")) {
        taxlabelsLine = lineNumber;
        if (commands.length > 1) {
          for (int i = 1; i < commands.length; i++) {
            speciesMap.put(commands[i], new Species(commands[i]));
          }
          if (!line.endsWith(";")) {
            inLables = true;
          }
        } else {
          inLables = true;
        }
      } else if (inLables) {
        if (line.equals(";")) {
          inLables = false;
          break;
        } else {
          for (String label : commands) {
            speciesMap.put(label, new Species(label));
          }
          if (line.endsWith(";")) {
            inLables = false;
            break;
          }
        }
      }
      //ignore other commands
    }
    if (inLables) {
      throw new NexusFormatException(
          "TAXLABELS block not closed with semicolon. Line: " + taxlabelsLine);
    }
    if (ntax > 0 && ntax != speciesMap.size()) {
      throw new NexusFormatException(
          "DIMENSIONS doesn't correspond to label number. Line: " + dimensionsLine);
    }
  }

  private void readInRelations(Map<Integer, String> lines)
      throws NexusFormatException {

    int state = 0; //0=out of command, 1=in config, 2=in matrix, 3=end of matrix
    relationMap = new HashMap<GeneRelationType, List<GeneRelation>>();

    GeneRelationType curType = null;
    String curName = null;
    MatrixFormat curFormat = null;
    GeneRelation curRelation = null;
    for (Map.Entry<Integer, String> lineEntry : lines.entrySet()) {
      int lineNumber = lineEntry.getKey();
      String line = lineEntry.getValue();

      String[] commands = line.split("\\s+");
      if (state == 0 && commands[0].equalsIgnoreCase("relation")) {
        state++;
        if (commands.length > 1) {
          line = line.substring(9);
          line = line.trim();
          commands = line.split("\\s+");
        } else {
          continue;
        }
      }
      if (state == 1) {
        for (String command : commands) {
          String[] data = command.split("=");
          if (data.length != 2) {
            throw new NexusFormatException("No data given to sub-command. Line: " + lineNumber);
          }
          if (data[0].equalsIgnoreCase("type")) {
            try {
              curType = GeneRelationType.valueOf(data[1].toUpperCase());
            } catch (IllegalArgumentException e) {
              throw new NexusFormatException(
                  String.format("Unknown relation type: %s. Line: %d", data[1], lineNumber));
            }
          } else if (data[0].equalsIgnoreCase("name")) {
            curName = data[1];
          } else if (data[0].equalsIgnoreCase("triangle")) {
            try {
              curFormat = MatrixFormat.valueOf(data[1].toUpperCase());
            } catch (IllegalArgumentException e) {
              throw new NexusFormatException(
                  String.format("Matrix format unknown: %s. Line: %d", data[1], lineNumber));
            }
          }
          //ignore unknown sub-commands
        }

        if (curType != null && curName != null && curFormat != null) {
          curRelation = new GeneRelation(curType, curFormat, curName);
          state++;
        }
      } else if (state == 2) {
        //read in matrix
        if (line.equals(";")) {
          state++;
        } else {
          curRelation.getGenes().add(
              new Gene(commands[0], findSpeciesForGene(commands[0], lineNumber)));
          ArrayList<Byte> byteList = new ArrayList<Byte>();
          for (int i = 1; i < commands.length; i++) {
            if (commands[i].equals(";")) {
              continue;
            } else if (commands[i].endsWith(";")) {
              commands[i] = commands[i].replace(";", "");
            }
            try {
              byteList.add(Byte.valueOf(commands[i]));
            } catch (NumberFormatException e) {
              throw new NexusFormatException("Cannot read byte number. Line: " + lineNumber, e);
            }
          }
          curRelation.getRelation().add(byteList);

          if (line.endsWith(";")) {
            state++;
          }
        }
        if (state == 3) {
          //add relation to list
          List<GeneRelation> relationList = relationMap.get(curType);
          if (relationList == null) {
            relationList = new ArrayList<GeneRelation>();
            relationMap.put(curType, relationList);
          }
          relationList.add(curRelation);
          //accept new ones
          state = 0;
        }
      }
      //ignore other commands
    }

  }

  private Species findSpeciesForGene(String geneName, int lineNumber) throws NexusFormatException {

    if (mappingMap == null || speciesMap == null) {
      throw new NexusFormatException("TAXA block and MAPPING block must be defined "
          + "before this block! Line: " + lineNumber);
    }

    for (HashMap<String, String[]> geneMap : mappingMap.values()) {
      String[] values = geneMap.get(geneName);
      if (values != null) {
        Species species = speciesMap.get(values[1]);
        if (species == null) {
          throw new NexusFormatException(String.format("Species %s is not listed in the "
              + "TAXA block, but is in mapping. Line: %d", values[1], lineNumber));
        }
        return species;
      }
    }
    throw new NexusFormatException(String.format(
        "Gene %s is not listed in the mapping block. Line: %d", geneName, lineNumber));
  }

  /**
   * Returns a list of all generelations of the given type, prenent in the RELATIONS block.
   * @param type the type of relations wanted
   * @return a list of GeneRelation, null if no RELATIONS block is present
   */
  public List<GeneRelation> getGeneRelationsByType(GeneRelationType type) {

    if (relationMap == null) {
      return null;
    }
    return relationMap.get(type);
  }

  /**
   * Returns the data from the TAXA block.
   * @return a HashMap with the speciesname as key.
   */
  public HashMap<String, Species> getSpecies() {
    return speciesMap;
  }

  /**
   * Returns the data from the ALLDISTANCES block combines with the DISTANCES block
   * (if present).
   * @return a HashMap with the treename as key, null if no ALLDISTANCES or DISTANCES block
   *         is present
   */
  public HashMap<String, GeneDistances> getDistanceMap() {

    return distanceMap;
  }

  /**
   * Gets the data from the DISTANCES block.
   * @return a GeneDistances instance or null if not DISTANCES block is present.
   */
  public GeneDistances getSingleDistances() {

    if (distanceMap != null) {
      return distanceMap.get(SINGLE_DIST_KEY);
    } else {
      return null;
    }
  }
}
