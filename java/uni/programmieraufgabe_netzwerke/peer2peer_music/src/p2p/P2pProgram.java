package p2p;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * Ausführung des P2P-Programms.
 * 
 */

public class P2pProgram {

	public static ArrayList<Integer> possiblePorts;

	/**
	 * Konstruktor initialisiert ArrayList possiblePorts und befuellt die Liste mit
	 * moeglichen Ports.
	 * 
	 */
	public P2pProgram() {
		possiblePorts = new ArrayList<Integer>();
		possiblePorts.add(50001);
		possiblePorts.add(50002);
		possiblePorts.add(50003);
		possiblePorts.add(50004);
		possiblePorts.add(50005);
		possiblePorts.add(50006);
		possiblePorts.add(50007);
		possiblePorts.add(50008);
		possiblePorts.add(50009);
		possiblePorts.add(50010);
	}

	/**
	 * Anweisung und Hilfe fuer Peer.
	 * 
	 */
	public void peerOutput() {
		System.out.println("Usage: java -jar jar-File-Name Port; ");
		System.out.println("mögliche Ports: ");
		for (int ports : possiblePorts) {
			System.out.print(" " + ports + " ");
		}
	}

	/**
	 * Fuehrt P2P Anwendung aus.
	 * 
	 * @param args Portnummer die Peer eingegeben hat.
	 * @throws IOException          Wenn Stoerungen bei Ein- und Ausgaben
	 *                              stattfinden.
	 * @throws InterruptedException Falls Probleme bei Thread auftreten.
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// wenn keine Argumente übergeben
		P2pProgram program = new P2pProgram();
		if (args.length == 0) {
			program.peerOutput();
			return;
		}

		int port = 0;
		// wenn keine Zahl übergeben wurde
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			System.out.println("Eingabe war fehlerhaft, starte das Programm nochmal.");
			program.peerOutput();
			return;
		}

		// Port nicht im Bereich
		if (possiblePorts.contains(port) == false) {
			System.out.println("Port ist nicht möglich, starte das Program neu.");
			program.peerOutput();
			return;
		}

		possiblePorts.remove(possiblePorts.indexOf(port));

		Music music = new Music();
		music.loadMusicFromFile();
		Channel channel = new Channel(music);

		// lauscht auf diesem port
		channel.bind(port);
		System.out.println("Started.");
		channel.start(); // start receive

		// sendet an alle Ports jede Sekunde
		while (true) {

			Thread.sleep(1000);

			for (int destPort : possiblePorts) {
				InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), destPort);
				for (String line : music.getSendList()) {
					channel.sendTo(address, line);
					// ausgabe
					// System.out.println("port: " + destPort + " Musik: " + line);
				}
			}
		}
	}
}
