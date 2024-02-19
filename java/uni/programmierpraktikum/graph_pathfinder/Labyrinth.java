/**
 *
 */
package labyrinth2.pkg0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * @author mo setzt den Namen des Labyrinths und liest alle Knoten ein
 *
 */
public class Labyrinth {

    private HashMap<Integer, Knoten> knote;

    /**
     * Konstruktor kreeiert Instanz von Labyrinth
     *
     */
    public Labyrinth() {

        knote = new HashMap<>();

    }

    /**
     * Methode liest Knoten von Datei in die Liste knoten ein
     *
     * @param graph : Name des Graphen, der eingelesen werden soll
     */
    public void knotenEinlesen(String graph) {

        try {
            File graphFile = new File(graph);
            FileReader fileReader = new FileReader(graphFile);

            BufferedReader reader = new BufferedReader(fileReader);

            String row = null;
            int knod;
            int neighbour;

            // kanten einlesen
            while ((row = reader.readLine()) != null) {

                knod = Integer.parseInt(row.substring(0, row.indexOf(" ")));
                neighbour = Integer.parseInt(row.substring(row.indexOf(" ") + 1));

                if (knote.containsKey(knod) == false) {
                    knote.put(knod, new Knoten(knod));
                }
                knote.get(knod).addNachbar(new Knoten(neighbour));

                if (knote.containsKey(neighbour) == false) {
                    knote.put(neighbour, new Knoten(neighbour));
                }
                knote.get(neighbour).addNachbar(new Knoten(knod));

            }
            /*for(Integer node : knote.keySet()) {
            	System.out.println("Knoten: " + knote.get(node).getNr() + " Nachbarn: " + knote.get(node).getNachbarn());
        		
        	}*/

            reader.close();

        } catch (Exception ex) {
            //System.out.println("Die Datei konnte nicht gefunden werden! Bitte versuchen Sie es erneut.");
            System.out.println(ex);

        }

    }

    /**
     * Methode gibt Knotenlisten zurück
     *
     * @return knoten
     */
    public HashMap<Integer, Knoten> getLab() {
        return knote;
    }

    public Knoten getStart() {
        return knote.get(28);
    }

    public Knoten getZiel() {
        return knote.get(123);
    }

}
