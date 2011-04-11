package com.ronny.ludo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;



/**
 * FORSLAG TIL SPILLEREGLER:
 * 1. 2-4 personer kan delta i spillet. Hver spiller f�r 4 brikker av samme farge og anbringer disse i et firkantet felt (g�rden) av samme farge som brikkene.
 * 2. Det kastes med en terning som viser 1-6. Det spilles p� brettet fra h�yre til venstre. Den som har de r�de brikkene begynner, nestemann til venstre fortsetter.
 * 3. Ingen brikke kommer ut av g�rden f�r eieren kaster 6. (Her kan vi kanskje legge inn valg for flere for � f� fortgang i spillet (f.eks. 1 og 6)) Seinere flyttes brikkene til venstre etter terningkastene.
 * 4. Kastes p� nytt 6, kan en etter �nske enten f�re en brikke ut av g�rden eller bringe en brikke videre p� feltet. Alle kast p� 6 gir rett til et nytt kast. Er en ikke i stand til � flytte en brikke, mister en kastet.
 * 5. Setter et kast en spiller i stand til � besette en plass hvor en motstanders brikke st�r, blir denne sl�tt ut og m� begynne forfra. Alts� ikke mulig � stille seg ved siden av. 
 * 6. Er plassen opptatt av en av ens egne brikker, blir den nye brikken ogs� anbrakt her. To brikker av samme farge sperrer vegen (danner port) for brikker av andre farger. S� lenge porten st�r, kan ingen av disse brikkene sl�s ut.
 * 7. Kan en motspiller ikke flytte andre av sine brikker, mister han kastet.
 * 8. N�r en brikke den midtlinjen som f�rer til dens �hjem� (det sted p� midtfeltet som har brikkens farge), f�res den framover denne midtlinjen. Hjemmet n�s bare ved � kaste det n�yaktige antall �yne. Kastes det for mange, m� brikken bli st�ende der den er.
 * 9. Et t�rn kan flyttes det antall plasser som man f�r �yne p� terningen, delt p� to. F�r man for eksempel terningkast 4, kan man flytte et t�rn 2 plasser fremover. *Dersom man f�r et oddetall over 1, kan man ogs� flytte t�rnet i sin helhet p� samme m�te, men man er da tvunget til � demontere det for � "bruke opp" det siste �yet. F�r man terningkast 5, kan man alts� flytte �verste brikke 5 felter eller flytte t�rnet 2 felter og �verste brikke et felt. Det er ikke mulig � lage et t�rn p� f�rste felt utenfor basen. En brikke M� flyttes fra dette feltet f�r neste brikke kan flyttes ut.
 * Regel 9 over er kanskje vanskelig � implementere. Hvordan velge enkelt om man vil flytte begge eller bare en brikke?
 */

public class Game implements ILudoEventListener {

	// Siden vi er en av fargene, s� m� vi lagre 'v�r' farge.
	// private PlayerColor localColor;

	private List<ILudoEventListener> requestListeners = new ArrayList<ILudoEventListener>();
	
	//TODO Tur m� flyttes til comm-mgr og behandling av slikt...
	@SuppressWarnings("unused")
	private PlayerColor currentTurnColor = PlayerColor.RED; // R�d starter
															// alltid

	//TODO M� sees i sammenheng med flere lokale spillere.	
	@SuppressWarnings("unused")
	private IPlayer localPlayer; // who is the local player
	private Random randomNumbers = new Random(); // random number generator
	private ILudoBoard ludoBoard = new LudoBoard();
	private String gameImageName = null;

	
	// Singleton type game
	private static Game INSTANCE = null; 

	public static Game getInstance() {
		if(INSTANCE==null) {
			INSTANCE = new Game();
		}
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
	 *  Returnere en spiller basert p� farge
	 *  
	 * @param theColor fargen p� spiller
	 * @return IPlayer
	 */
	public IPlayer getPlayerInfo(String theColor) {
		IPlayer ret = getLudoBoard().getPlayer(theColor);
		return ret;
	}

	/**
	 *  Returnere en spiller basert p� farge
	 *  
	 * @param theColor String med fargenavn
	 * @return
	 */
	public IPlayer getPlayerInfo(PlayerColor theColor) {
		IPlayer ret = getLudoBoard().getPlayer(theColor);
		return ret;
	}
	
	/**
	 * @return the gameImageName
	 */
	public String getGameImageName() {
		return gameImageName;
	}

	/**
	 * @param gameImageName the gameImageName to set
	 */
	public void setGameImageName(String gameImageName) {
		this.gameImageName = gameImageName;
	}

    public PlayerColor getcurrentTurnColor() {

        return currentTurnColor;
    }

    public void setnextTurnColorTest() {

        if (currentTurnColor.compareTo(PlayerColor.RED) == 0) {
            currentTurnColor = PlayerColor.GREEN;
        } else if (currentTurnColor.compareTo(PlayerColor.GREEN) == 0) {
            currentTurnColor = PlayerColor.YELLOW;
        } else if (currentTurnColor.compareTo(PlayerColor.BLUE) == 0) {
            currentTurnColor = PlayerColor.RED;
        } else if (currentTurnColor.compareTo(PlayerColor.YELLOW) == 0) {
            currentTurnColor = PlayerColor.BLUE;
        }
    }
	
	/**
	 * Hente ut status p� bordet akkurat n� - slik at nye klienter evt. kan hoppe p�
	 * for � f�lge spillet...
	 * 
	 * @return string med definisjonen av bordet akkurat n�.
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

	public ILudoBoard getLudoBoard() {
		return ludoBoard;
	}

	// Utils
	// Returnere en spiller basert p� farge
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
	
    /**
     * H�ndterer et flytt hvis gyldig brikke er valgt
     * 
     * @param xPos x-posisjon valgt
     * @param yPos y-posisjon valgt
     * @param delta tillegg for yttergrenser
     */
    public boolean handleMove(int xPos, int yPos, double delta) {

        Log.d("IPiece(LB)", "handleMove: klikket: " + xPos + "," + yPos);
        IPlayer player = getLudoBoard().getPlayer(getcurrentTurnColor());
        int brikkeNo = -1;
        boolean brikkeFound = false;
        for (IPiece brikke : player.getBrikker()) {
            brikkeNo += 1;
            Coordinate c = brikke.getCurrentPosition();
            Log.d("IPiece(LB)", "handleMove: brikke " + brikkeNo + ": " + c.x + "," + c.y);
            if (((c.x - delta) < xPos) && ((c.x + delta) > xPos) && ((c.y - delta) < yPos)
                    && ((c.y + delta) > yPos)) {
                brikkeFound = true;
                Log.d("IPiece(LB)", "handleMove: farge: " + getcurrentTurnColor());
                Log.d("IPiece(LB)", "handleMove: brikke " + brikkeNo + " skal flyttes.");
                break;
            }
        }
        if (brikkeFound) {
            // Die for test
            Die terning = new Die();
            int move = terning.roll();
            Log.d("IPiece(LB)", "Die: " + move);
            playerMove(getcurrentTurnColor(), brikkeNo, move);
            // for test setter neste farge sin tur
            setnextTurnColorTest();
        }
        Log.d("IPiece(LB)", "handleMove: neste sin tur: " + getcurrentTurnColor());
        return true;
    }

	// Debug
	public void DumpGame() {
		Log.d("DUMP","Board image : "+gameImageName);
		ludoBoard.DumpGame();
		
	}
}
