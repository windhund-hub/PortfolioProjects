package p2p;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Verwaltet Musikdaten.
 * 
 */
public class Music {

	private ArrayList<String> receiveList;
	private ArrayList<String> sendList;

	/**
	 * Konstruktor initialisiert ArrayLists.
	 * 
	 */
	public Music() {
		receiveList = new ArrayList<>();
		sendList = new ArrayList<>();

	}

	/**
	 * Laedt Musik aus Datei.
	 * 
	 * @throws IOException Wenn Stoerungen bei Ein- und Ausgaben stattfinden.
	 */
	public void loadMusicFromFile() throws IOException {

		FileReader fr = new FileReader("music.txt");
		BufferedReader br = new BufferedReader(fr);

		String sendMusic = br.readLine();

		// läuft Datei zeilenweise ab
		while (sendMusic != null) {
			sendList.add(sendMusic);
			receiveList.add(sendMusic);
			sendMusic = br.readLine();
			// System.out.println(sMusic);
		}
		br.close();
	}

	/**
	 * Erhaltene Musik wird zur Liste hinzugefuegt.
	 * 
	 * @param musicData Musikdaten aus Liste.
	 * @throws IOException Wenn Stoerungen bei Ein- und Ausgaben stattfinden.
	 */
	public void addToReceiveList(String musicData) throws IOException {
		if (receiveList.contains(musicData)) {
			return;
		}
		receiveList.add(musicData);
		addMusicToFile();
	}

	/**
	 * Schreibt die erhaltenen Musikdaten in Peers Datei.
	 * 
	 * @throws IOException Wenn Stoerungen bei Ein- und Ausgaben stattfinden.
	 */
	public void addMusicToFile() throws IOException {

		FileWriter fw = new FileWriter("musicReceive.txt");
		BufferedWriter bw = new BufferedWriter(fw);

		for (String music : receiveList) {
			bw.append(music + System.lineSeparator());
		}
		bw.close();
	}

	/**
	 * Gibt Liste mit den zu sendenden Musikdaten.
	 * 
	 * @return sendList zu sendende Musikdaten
	 */
	public ArrayList<String> getSendList() {
		return sendList;
	}

}
