package wetter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Der clientseitige Anteil der Wetteranwendung. Startet Client, baut Verbindung
 * zu Server auf und verarbeitet Ein- und Ausgaben.
 *
 */
public class Client {

	/**
	 * Startet den Client.
	 * 
	 * @param args Standard
	 * @throws IOException          Wenn Stoerungen bei Ein- und Ausgaben
	 *                              stattfinden.
	 * @throws UnknownHostException Wenn Hostadresse nicht bekannt ist.
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {

		Client client = new Client();
		client.inOutClient(client.connectionToServer());
	}

	/**
	 * Baut Verbindung zu Server auf.
	 * 
	 * @return socket: Kommunikationsschnittstelle zwischen Server und Client.
	 * @throws IOException          Wenn Stoerungen bei Ein- und Ausgaben
	 *                              stattfinden.
	 * @throws UnknownHostException Wenn Hostadresse nicht bekannt ist.
	 */
	public Socket connectionToServer() throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", 50000);
		return socket;
	}

	/**
	 * Verarbeitet Ein- und Ausgaben von Client.
	 * 
	 * @param socket Kommunikationsschnittstelle zwischen Server und Client.
	 * @throws IOException Wenn Stoerungen bei Ein- und Ausgaben stattfinden.
	 */
	public void inOutClient(Socket socket) throws IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		System.out.println(in.readLine());

		Scanner s = new Scanner(System.in);
		System.out.println("Um das Programm zu beenden, betätige Enter ohne Eingabe.");

		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		while (true) {
			String input = s.nextLine();
			if (input == null || input.isEmpty()) {
				System.out.println("Programm beendet");
				break;
			} else {
				// Anfrage an Server
				out.println(input);
				// Antwort von Server
				System.out.println(in.readLine());
			}
		}
		s.close();
	}

}
