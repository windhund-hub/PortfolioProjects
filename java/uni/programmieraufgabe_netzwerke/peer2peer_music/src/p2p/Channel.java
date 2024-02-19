package p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Verwaltet Verbindung des P2P-Programms.
 * 
 */

public class Channel implements Runnable {

	private DatagramSocket socket;
	private boolean running;
	private Music music;

	/**
	 * Konstruktor initialisiert music.
	 * 
	 * @param music Instanz von Music.
	 */
	public Channel(Music music) {
		this.music = music;
	}

	/**
	 * Verbindet zu Socket per UDP.
	 * 
	 * @param port Musikdaten
	 * @throws SocketException Wenn Fehler bei Erstellung oder Abruf von Socket
	 *                         entstehen.
	 */
	public void bind(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	/**
	 * Startet neuen Thread.
	 * 
	 */
	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	/**
	 * Schliesst Socket.
	 * 
	 */
	public void stop() {
		running = false;
		socket.close();
	}

	/**
	 * Holt die Musikdaten von anderen Peers.
	 * 
	 */
	@Override
	public void run() {

		byte[] buffer = new byte[50];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		running = true;

		while (running) {
			try {
				socket.receive(packet);
				String msg = new String(buffer, 0, packet.getLength());
				music.addToReceiveList(msg);
			} catch (IOException ioe) {
				break;
			}
		}
	}

	/**
	 * Sendet Musikdaten.
	 * 
	 * @param address   IP-Adresse von Ziel.
	 * @param musicData Musikdaten
	 * @throws IOException Wenn Stoerungen bei Ein- und Ausgaben stattfinden.
	 */
	public void sendTo(InetSocketAddress address, String musicData) throws IOException {

		byte[] buffer = musicData.getBytes();

		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		packet.setSocketAddress(address);

		socket.send(packet);
	}

}
