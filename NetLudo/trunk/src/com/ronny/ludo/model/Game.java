package com.ronny.ludo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ronny.ludo.model.impl.LudoBoardImpl;

public class Game implements LudoEventListener {

	// Siden vi er en av fargene, så må vi lagre 'vår' farge.
	// private PlayerColor localColor;

	private List<LudoEventListener> requestListeners = new ArrayList<LudoEventListener>();
	private PlayerColor currentTurnColor = PlayerColor.RED; // Rød starter
															// alltid

//	private HashMap<PlayerColor, Player> players; // All players
	private Player localPlayer; // who is the local player
	private Random randomNumbers = new Random(); // random number generator
	private LudoBoard ludoBoard = new LudoBoardImpl();

	// Singleton type game
	private static Game INSTANCE = new Game();

	public static Game getInstance() {
		return INSTANCE;
	}

	private Game() {
		
		// Set up game
		// Players are added by the LudoBoard
//		players = new HashMap<PlayerColor, Player>();
//		players.put(PlayerColor.RED, new Player(PlayerColor.RED));
//		players.put(PlayerColor.GREEN, new Player(PlayerColor.GREEN));
//		players.put(PlayerColor.YELLOW, new Player(PlayerColor.YELLOW));
//		players.put(PlayerColor.BLUE, new Player(PlayerColor.BLUE));
		
		// Add player items
	}
	
	public void startGame() {
		
	}

	
	// Returnere en spiller basert på farge
	public Player getPlayerInfo(String theColor) {
		Player ret = getLudoBoard().getPlayer(theColor);
		return ret;
	}

	// Returnere en spiller basert på farge
	public Player getPlayerInfo(PlayerColor theColor) {
		Player ret = getLudoBoard().getPlayer(theColor);
		return ret;
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
		localPlayer = getLudoBoard().getPlayer(color);
	}

	// Listener sink for event.
	public void ludoActionEvent(GameEvent event) {
		// Lokale endringer gjør vi selv. - eller så gjør vi det på en
		// eventbasert total-løsning
		// Mer om dette senere i diskusjon av modellen
		// if(event.getColor() == localColor) {
		// return;
		// }

		// Behandle event

	}

	// Game moves

	// Event listener og håndtering
	public void addEventListener(LudoEventListener client) {
		requestListeners.add(client);
	}

	public void removeEventListener(LudoEventListener client) {
		requestListeners.remove(client);
	}

	public void fireGameEvent(GameEvent event) {
		for (LudoEventListener l : requestListeners) {
			l.ludoActionEvent(event);
		}

	}

	public LudoBoard getLudoBoard() {
		return ludoBoard;
	}

	// Utils
	// Returnere en spiller basert på farge
	public static PlayerColor convertPlayerColor(String theColor) {
		PlayerColor ret = null;
		if (theColor.compareToIgnoreCase("RED") == 0) {
			ret = PlayerColor.RED;
		} else if (theColor.compareToIgnoreCase("GREEN") == 0) {
			ret = PlayerColor.GREEN;
		} else if (theColor.compareToIgnoreCase("BLUE") == 0) {
			ret = PlayerColor.BLUE;
		} else if (theColor.compareToIgnoreCase("YELLOW") == 0) {
			ret = PlayerColor.YELLOW;
		}
		return ret;
	}


	/**
	 * En Player flytter en brikke et visst antall ruter eller ut fra hus, eller hjem/goal
	 * @param theColor
	 * @param theBrikke
	 * @param theMove
	 */
	public void playerMove(PlayerColor theColor, int theBrikke, int theMove) {
		getLudoBoard().playerMove( theColor,  theBrikke,  theMove);		
	}
}
