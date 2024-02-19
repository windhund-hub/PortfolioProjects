/**
 *
 */
package labyrinth2.pkg0;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mo berechnet kreisfreie Wege und gibt diese aus
 */
public class Pfadfinder {

    private Labyrinth labyrinth;
    private List<List<Integer>> wege;
    private Knoten ende;

    /**
     * Konstruktor kreiert Instanz von Knoten
     *
     * @param labyrinth : Labyrinth
     * @param start: Startknoten
     * @param ende: Endknoten
     */
    public Pfadfinder(Labyrinth labyrinth, Knoten start, Knoten ende) {
        this.labyrinth = labyrinth;
        wege = new ArrayList<>();
        this.ende = ende;
        
        if (start.equals(ende)) {
            System.out.println("Start- und Endknoten  gleich!");
        } else {
            berechneWege(start, new ArrayDeque<>());
        }
    }

    public void berechneWege(Knoten aktuellerKnoten, ArrayDeque<Integer> weg) {

        labyrinth.getLab().get(aktuellerKnoten.getNr()).setBesucht(true);
        weg.add(aktuellerKnoten.getNr());

        if (aktuellerKnoten.getNr() == ende.getNr()) {
            wege.add(new ArrayList<>(weg));
        } else {
            if (labyrinth.getLab().containsKey(aktuellerKnoten.getNr())) {
                for (Knoten nachbar : labyrinth.getLab().get(aktuellerKnoten.getNr()).getNachbarn()) {
                    if (!labyrinth.getLab().get(nachbar.getNr()).getBesucht()) {
                        berechneWege(nachbar, weg); // hier ist eine Rekursion \o/
                    }
                }
            }
        }
        weg.removeLast();
        labyrinth.getLab().get(aktuellerKnoten.getNr()).setBesucht(false);
    }

    /**
     * Methode gibt alle kreisfreien Wege des Labyrinths aus.
     *
     *
     * @return wege : Liste der Wege
     */
    public List<List<Integer>> getWege() {
        return wege;
    }

}
