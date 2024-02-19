/**
 * 
 */
package labyrinth2.pkg0;


import java.util.ArrayList;

/**
 * Es werden für jeden Knoten die Nachbarn gespeichert und 
 * ob der Knoten schon besucht wurde.
 * @author mo
 *
 */
public class Knoten {
	
	private int nr;
	private ArrayList<Knoten>nachbarn;
	private boolean besucht;
	
	/**
     * Konstruktor kreeiert Instanz von Knoten und setzt Standardwerte  
     * @param nr: Nummer des Knotens
     */
	public Knoten(int nr) {
		this.nr = nr;
		nachbarn = new ArrayList<>();
		besucht = false;
	}
	/**
     * Methode gibt Knotennummer(-bezeichnung) zurück
     * @return nr
     */
	public int getNr() {
		return nr;
	}
	/**
     * Methode fügt Nachbarknoten in die Menge eines Knoten x 
     * @param knoten: Knoten, der als Nachbar gesetzt wird
     */
	public void addNachbar(Knoten knoten) {
		nachbarn.add(knoten);
	}
	/**
     * Methode gibt die Nachbarknoten eines Knoten x zurück
     * @return nachbarn
     */
	public ArrayList<Knoten> getNachbarn(){
		return nachbarn;
	}
	/**
     * Methode setzt Wahrheitswert, ob Knoten besucht wurde 
     * @param besucht: setzt ob Knoten besucht wurde
     */
	public void setBesucht(boolean besucht) {
		this.besucht = besucht;
	}
	/**
     * Methode gibt Wahrheitswert zurück,ob Knoten besucht wurde
     * @return besucht
     */
	public boolean getBesucht() {
		return besucht;
	}
	
	
	

}
