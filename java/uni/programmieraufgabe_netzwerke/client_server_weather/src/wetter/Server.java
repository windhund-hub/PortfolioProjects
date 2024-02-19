package wetter;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.Socket;
import java.io.*;

/**
 * Der serverseitige Anteil der Wetteranwendung. Haelt alle Wetterdaten, nimmt
 * Verbindung von einem Client an und verarbeitet Ein- und Ausgaben.
 * 
 */
public class Server {

	private ArrayList<String> wetter;
	private Socket socket;

	/**
	 * Konstruktor initialisiert socket und befuellt Liste mit Wetterdaten.
	 * 
	 * @param socket Socket, über den Kommunikation stattfindet.
	 */
	public Server(Socket socket) {
		this.socket = socket;
		wetter = new ArrayList<>();
		wetter.add("Sonnig, 20 Grad");
		wetter.add("Sonnig, 23 Grad");
		wetter.add("Windig, 18 Grad");
		wetter.add("Regen, 19 Grad");
		wetter.add("Neblig, 24 Grad");
	}

	/**
	 * Startet die Serveranwendung.
	 * 
	 * @param args Standard
	 * @throws IOException Wenn Stoerungen bei Ein- und Ausgaben stattfinden.
	 */
	public static void main(String[] args) throws IOException {

		try (ServerSocket lauscher = new ServerSocket(50000)) {
			System.out.println("Server lauscht");
			while (true) {
				Server server = new Server(lauscher.accept());
				server.inOutServer();
			}
		}
	}

	/**
	 * Verarbeitet Output von Server und Input zu Server.
	 *
	 */
	public void inOutServer() {

		try {
			// Ausgaben für Client
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("Wetterabfrage: Von welcher Stadt möchten Sie das Wetter wissen."
					+ "Geben Sie die entsprechende Zahl ein!" + " 0 = Mittenwald" + " 1 = Tangermünde"
					+ " 2 = Bacharach" + " 3 = Monschau" + " 4 = Mölln");

			// Input für Server von Client
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				String input = in.readLine();
				// wenn kein Zeichen übergeben wird, wird Program beendet
				if (input == null || input.isEmpty()) {
					break;
				} else {
					// prüft ob input eine Zahl ist
					if (isNumber(input)) {

						int stadt = Integer.valueOf(input);
						//Ausgabe für Client
						if (stadt >= 0 && stadt <= wetter.size() - 1) {
							out.println(wetter.get(Integer.valueOf(input)));
						} else {
							out.println("Deine Eingabe war falsch. Wählen Sie Zahlen aus dem angebenen Bereich");
						}
					} else {
						out.println("Deine Eingabe war falsch. Nur Zeichen verwenden die erlaubt sind.");
					}
				}
			}
		} catch (IOException ioe) {
			System.out.println(ioe);
		} finally {
			try {
				socket.close();
			} catch (IOException ioe) {
				System.out.println(ioe);
			}

		}
	}

	/**
	 * Prueft ob Zeichenkette eine Zahl ist.
	 *
	 * @param number Zahl die geprueft wird.
	 * @return Wahrheitswert ob number eine Zahl ist.
	 */
	public boolean isNumber(String number) {
		for (int i = 0; i < number.length(); i++)
			if (Character.isDigit(number.charAt(i)) == false) {
				return false;
			}
		return true;
	}
}
