/**
 *
 */
package labyrinth2.pkg0;

import java.util.List;

/**
 * @author mo
 *
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //prüfen ob benutzereingabe korrekt, starten der Abfrage

        start();
    }

    /**
     * Erfasst den Namen des Labyrinths und gibt diesen weiter; leitet die Berechnung ein.
     */
    public static void start() {
        Labyrinth lab = new Labyrinth();
        //Name des Labyrinths
        String graph1 = "labyrinth-1.graph";
        //String graph2 = "labyrinth-2.graph";		
        lab.knotenEinlesen(graph1);
        //lab.knotenEinlesen(graph2);

        Pfadfinder pfad = new Pfadfinder(lab, lab.getStart(), lab.getZiel());
        List<List<Integer>> wege = pfad.getWege();
        for (List<Integer> weg : wege) {
            System.out.print("[");
            for(int i = 0; i < weg.size()-1; i++){
                System.out.print("("+ weg.get(i)+","+weg.get(i+1)+")");
            }
            System.out.print("]");
            System.out.println("");
        }
        System.out.println("Das Labyrinth hat mit Startknoten "+ lab.getStart().getNr() + " und Endknoten " + lab.getZiel().getNr() + " hat " + wege.size() + " Wege.");
    }

}
