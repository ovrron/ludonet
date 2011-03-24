package com.ronny.ludo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



/**
 * FORSLAG TIL SPILLEREGLER:
 * 1. 2-4 personer kan delta i spillet. Hver spiller får 4 brikker av samme farge og anbringer disse i et firkantet felt (gården) av samme farge som brikkene.
 * 2. Det kastes med en terning som viser 1-6. Det spilles på brettet fra høyre til venstre. Den som har de røde brikkene begynner, nestemann til venstre fortsetter.
 * 3. Ingen brikke kommer ut av gården før eieren kaster 6. (Her kan vi kanskje legge inn valg for flere for å få fortgang i spillet (f.eks. 1 og 6)) Seinere flyttes brikkene til venstre etter terningkastene.
 * 4. Kastes på nytt 6, kan en etter ønske enten føre en brikke ut av gården eller bringe en brikke videre på feltet. Alle kast på 6 gir rett til et nytt kast. Er en ikke i stand til å flytte en brikke, mister en kastet.
 * 5. Setter et kast en spiller i stand til å besette en plass hvor en motstanders brikke står, blir denne slått ut og må begynne forfra. Altså ikke mulig å stille seg ved siden av. 
 * 6. Er plassen opptatt av en av ens egne brikker, blir den nye brikken også anbrakt her. To brikker av samme farge sperrer vegen (danner port) for brikker av andre farger. Så lenge porten står, kan ingen av disse brikkene slås ut.
 * 7. Kan en motspiller ikke flytte andre av sine brikker, mister han kastet.
 * 8. Når en brikke den midtlinjen som fører til dens «hjem» (det sted på midtfeltet som har brikkens farge), føres den framover denne midtlinjen. Hjemmet nås bare ved å kaste det nøyaktige antall øyne. Kastes det for mange, må brikken bli stående der den er.
 * 9. Et tårn kan flyttes det antall plasser som man får øyne på terningen, delt på to. Får man for eksempel terningkast 4, kan man flytte et tårn 2 plasser fremover. *Dersom man får et oddetall over 1, kan man også flytte tårnet i sin helhet på samme måte, men man er da tvunget til å demontere det for å "bruke opp" det siste øyet. Får man terningkast 5, kan man altså flytte øverste brikke 5 felter eller flytte tårnet 2 felter og øverste brikke et felt. Det er ikke mulig å lage et tårn på første felt utenfor basen. En brikke MÅ flyttes fra dette feltet før neste brikke kan flyttes ut.
 * Regel 9 over er kanskje vanskelig å implementere. Hvordan velge enkelt om man vil flytte begge eller bare en brikke?
 */

public class Game implements ILudoEventListener {

	// Siden vi er en av fargene, så må vi lagre 'vår' farge.
	// private PlayerColor localColor;

	private List<ILudoEventListener> requestListeners = new ArrayList<ILudoEventListener>();
	
	//TODO Tur må flyttes til comm-mgr og behandling av slikt...
	@SuppressWarnings("unused")
	private PlayerColor currentTurnColor = PlayerColor.RED; // Rød starter
															// alltid

	//TODO Må sees i sammenheng med flere lokale spillere.	
	@SuppressWarnings("unused")
	private IPlayer localPlayer; // who is the local player
	private Random randomNumbers = new Random(); // random number generator
	private ILudoBoard ludoBoard = new LudoBoard();

	// Singleton type game
	private static Game INSTANCE = new Game();

	public static Game getInstance() {
		return INSTANCE;
	}

	private Game() {
		
		// Set up game
		// Players are added by the ILudoBoard
//		players = new HashMap<PlayerColor, IPlayer>();
//		players.put(PlayerColor.RED, new IPlayer(PlayerColor.RED));
//		players.put(PlayerColor.GREEN, new IPlayer(PlayerColor.GREEN));
//		players.put(PlayerColor.YELLOW, new IPlayer(PlayerColor.YELLOW));
//		players.put(PlayerColor.BLUE, new IPlayer(PlayerColor.BLUE));
		
		// Add player items
	}
	
	public void startGame() {
		
	}

	
	/**
	 *  Returnere en spiller basert på farge
	 *  
	 * @param theColor fargen på spiller
	 * @return IPlayer
	 */
	public IPlayer getPlayerInfo(String theColor) {
		IPlayer ret = getLudoBoard().getPlayer(theColor);
		return ret;
	}

	/**
	 *  Returnere en spiller basert på farge
	 *  
	 * @param theColor String med fargenavn
	 * @return
	 */
	public IPlayer getPlayerInfo(PlayerColor theColor) {
		IPlayer ret = getLudoBoard().getPlayer(theColor);
		return ret;
	}
	
	
	/**
	 * Hente ut status på bordet akkurat nå - slik at nye klienter evt. kan hoppe på
	 * for å følge spillet...
	 * 
	 * @return string med definisjonen av bordet akkurat nå.
	 */
	public String getCurrentStatus() {
		//TODO ikke prioritert oppgave .
		String cs = "";
		return cs;
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
	public void ludoActionEvent(IGameEvent event) {
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

	public ILudoBoard getLudoBoard() {
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
	 * En IPlayer flytter en brikke et visst antall ruter eller ut fra hus, eller hjem/goal
	 * @param theColor
	 * @param theBrikke
	 * @param theMove
	 */
	public void playerMove(PlayerColor theColor, int theBrikke, int theMove) {
		getLudoBoard().playerMove( theColor,  theBrikke,  theMove);		
	}
}
