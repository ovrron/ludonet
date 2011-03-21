package com.ronny.ludo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Game implements ILudoEventListener {

	// Siden vi er en av fargene, s� m� vi lagre 'v�r' farge.
	// private PlayerColor localColor;

	private List<ILudoEventListener> requestListeners = new ArrayList<ILudoEventListener>();
	private PlayerColor currentTurnColor = PlayerColor.RED; // R�d starter
															// alltid

	private HashMap<PlayerColor, Player> players; // All players
	private Player localPlayer; // who is the local player
	private Random randomNumbers = new Random(); // random number generator
	private LudoBoard ludoBoard = new LudoBoard();

	// Singleton type game
	private static Game INSTANCE = new Game();

	public static Game getInstance() {
		return INSTANCE;
	}

	
	// Returnere en spiller basert p� farge
	public Player getPlayerInfo(String theColor) {
		Player ret = null;
		if (theColor.compareToIgnoreCase("RED") == 0) {
			ret = players.get(PlayerColor.RED);
		} else if (theColor.compareToIgnoreCase("GREEN") == 0) {
			ret = players.get(PlayerColor.GREEN);
		} else if (theColor.compareToIgnoreCase("BLUE") == 0) {
			ret = players.get(PlayerColor.BLUE);
		} else if (theColor.compareToIgnoreCase("YELLOW") == 0) {
			ret = players.get(PlayerColor.YELLOW);
		}
		return ret;
	}

	private Game() {
		// Set up game
		players = new HashMap<PlayerColor, Player>();
		players.put(PlayerColor.RED, new Player(PlayerColor.RED));
		players.put(PlayerColor.GREEN, new Player(PlayerColor.GREEN));
		players.put(PlayerColor.YELLOW, new Player(PlayerColor.YELLOW));
		players.put(PlayerColor.BLUE, new Player(PlayerColor.BLUE));

		// Add player items

	}

	public void startGame() {

	}

	/**
	 * Flytter en brikke for en spiller
	 * 
	 * @param col
	 *            Farge
	 * @param brikkeNumber
	 *            brikkens nummer
	 * @param moves
	 *            antall flytninger
	 * @param kickHome
	 *            true=flytter motstanderens brikke hjem hvis treff.
	 */
	public void doMove(PlayerColor col, int brikkeNumber, int moves,
			boolean kickHome) {
		// Move the item

		// Set next turn ?
	}

	/**
	 * Roll the die
	 * 
	 * @return
	 */
	public int rollDie() {
		int eyes = 1 + randomNumbers.nextInt(6);
		return eyes;
	}

	/**
	 * Sets the local players color
	 * 
	 * @param color
	 */
	public void setLocalPlayerColor(PlayerColor color) {
		localPlayer = players.get(color);
	}

	// Listener sink for event.
	public void ludoActionEvent(IGameEvent event) {
		// Lokale endringer gj�r vi selv. - eller s� gj�r vi det p� en
		// eventbasert total-l�sning
		// Mer om dette senere i diskusjon av modellen
		// if(event.getColor() == localColor) {
		// return;
		// }

		// Behandle event

	}

	// Game moves

	// Event listener og h�ndtering
	public void addEventListener(ILudoEventListener client) {
		requestListeners.add(client);
	}

	public void removeEventListener(ILudoEventListener client) {
		requestListeners.remove(client);
	}

	public void fireGameEvent(IGameEvent event) {
		for (ILudoEventListener l : requestListeners) {
			l.ludoActionEvent(event);
		}

	}

	public LudoBoard getLudoBoard() {
		return ludoBoard;
	}

}
