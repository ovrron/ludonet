package com.ronny.ludo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import android.util.Log;


 /**
 * FORSLAG TIL SPILLEREGLER:
 * 1. 2-4 personer kan delta i spillet. Hver spiller får 4 brikker av samme farge og anbringer disse i et firkantet felt (gården) av samme farge som brikkene.
 * 2. Det kastes med en terning som viser 1-6. Det spilles på brettet fra høyre til venstre. Den som har de røde brikkene begynner, nestemann til venstre fortsetter.
 * 3. Ingen brikke kommer ut av gården før eieren kaster 6. (Her kan vi kanskje legge inn valg for flere for å få fortgang i spillet (f.eks. 1 og 6)) Seinere flyttes brikkene til venstre etter terningkastene.
 * 4. Kastes på nytt 6, kan en etter ønske enten føre en brikke ut av gården eller bringe en brikke videre på feltet. Alle kast på 6 gir rett til et nytt kast. Er en ikke i stand til å flytte en brikke, mister en kastet.
 * 5. Setter et kast en spiller i stand til å besette en plass hvor en motstanders brikke står, blir denne slått ut og må begynne forfra. Altså ikke mulig å stille seg ved siden av. 
 * 6. Er plassen opptatt av en av ens egne brikker, blir den nye brikken også anbrakt her. To brikker av samme farge sperrer vegen (danner port) for brikker av andre farger. Så lenge porten står, kan ingen av disse brikkene slås ut.
 * 7. Kan en motspiller ikke flytte andre av sine brikker, mister han kastet.
 * 8. Når en brikke den midtlinjen som fører til dens "hjem" (det sted på midtfeltet som har brikkens farge), føres den framover denne midtlinjen. Hjemmet nås bare ved å kaste det nøyaktige antall øyne. Kastes det for mange, må brikken bli stående der den er.
 * 9. Et tårn kan flyttes det antall plasser som man før øyne på terningen, delt på to. Før man for eksempel terningkast 4, kan man flytte et tårn 2 plasser fremover. *Dersom man får et oddetall over 1, kan man også flytte tårnet i sin helhet på samme måte, men man er da tvunget til å demontere det for å "bruke opp" det siste øyet. Får man terningkast 5, kan man altså flytte øverste brikke 5 felter eller flytte tårnet 2 felter og øverste brikke et felt. Det er ikke mulig å lage et tårn på første felt utenfor basen. En brikke må flyttes fra dette feltet før neste brikke kan flyttes ut.
 * Regel 9 over er kanskje vanskelig å implementere. Hvordan velge enkelt om man vil flytte begge eller bare en brikke?
 */

public class Game {

	// Siden vi er en av fargene, så må vi lagre 'vår' farge.
	// private PlayerColor localColor;

	private List<ILudoEventListener> requestListeners = new ArrayList<ILudoEventListener>();

	// TODO Tur må flyttes til comm-mgr og behandling av slikt...
	@SuppressWarnings("unused")
	private PlayerColor currentTurnColor = PlayerColor.RED; // Rød starter
															// alltid

	// TODO Må sees i sammenheng med flere lokale spillere.
	@SuppressWarnings("unused")
	// private IPlayer localPlayer; // who is the local player
	private Random randomNumbers = new Random(); // random number generator
	private ILudoBoard ludoBoard = new LudoBoard();
	private String gameImageName = null;

	// private IRules rules = new StandardRules();

	// PLACEHOLDER for TurnManager
	// private TurnManager turnManager = new TurnManager();

	// Singleton type game
	// private static Game INSTANCE = null;
	//
	// public static Game getInstance() {
	// if(INSTANCE==null) {
	// INSTANCE = new Game();
	// }
	// return INSTANCE;
	// }

	public Game() {
		// Set up game
		// rules.setTakeOffNumbers(2,4,6); // Flyttet til GameHolder
	}

	public void startGame() {

	}

	// public IRules getRules() // Flyttet til GameHolder
	// {
	// return rules;
	// }

	/**
	 * Returnere en spiller basert på farge
	 * 
	 * @param theColor
	 *            fargen på spiller
	 * @return IPlayer
	 */
	public IPlayer getPlayerInfo(String theColor) {
		IPlayer ret = getLudoBoard().getPlayer(theColor);
		return ret;
	}

	/**
	 * Returnere en spiller basert på farge
	 * 
	 * @param theColor
	 *            String med fargenavn
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
	 * @param gameImageName
	 *            the gameImageName to set
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
	 * Hente ut status på bordet akkurat nå - slik at nye klienter evt. kan
	 * hoppe på for å følge spillet...
	 * 
	 * @return string med definisjonen av bordet akkurat nå.
	 */
	public String getCurrentStatus() {
		// TODO ikke prioritert oppgave .
		String cs = "";
		return cs;
	}

	// /**
	// * Flytter en brikke for en spiller
	// *
	// * @param col
	// * Farge
	// * @param brikkeNumber
	// * brikkens nummer
	// * @param moves
	// * antall flytninger
	// * @param kickHome
	// * true=flytter motstanderens brikke hjem hvis treff.
	// */
	// public void doMove(PlayerColor col, int brikkeNumber, int moves,
	// boolean kickHome) {
	// // Move the item
	//
	// // Set next turn ?
	// }

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
	 * En IPlayer flytter en brikke et visst antall ruter eller ut fra hus,
	 * eller hjem/goal
	 * 
	 * @param theColor
	 * @param theBrikke
	 * @param theMove
	 * @return playercolor if kicked to the house.
	 */
	public PlayerColor playerMove(PlayerColor theColor, int theBrikke, int theMove) {
		PlayerColor retVal = PlayerColor.NONE;
		ArrayList<IPiece> pieces = new ArrayList<IPiece>();
		List<PieceAction> actionList = new ArrayList<PieceAction>();

		// Flytter current brikke
		IPiece brikkeFlyttet = getLudoBoard().playerMove(theColor, theBrikke, theMove);

		// Finner alle andre brikker på target posisjon
		pieces = this.findOtherPicesAtCoordinate(brikkeFlyttet);

		// Henter action for hver brikke på target posisjon
		actionList = GameHolder.getInstance().getRules().getPieceActionList(brikkeFlyttet, pieces);
		if (actionList != null) {
			for (PieceAction pa : actionList) {
				if (pa == PieceAction.MOVE_TO_BASE) {
					if (pieces.size() > 0) {
						retVal = pieces.get(0).getColor();
						break;
					}
				}
			}
		}

		// Utfører actions for hver brikke på target posisjon
		this.handlePieceActionList(brikkeFlyttet, pieces, actionList);
		// setnextTurnColorTest();
		return retVal;
	}

	/**
	 * handlePieceActionList utfører actions på brikker basert på actionlist
	 * 
	 * @param piece
	 * @param pieces
	 * @param actionList
	 * @return boolean hvis actions er utført
	 */
	private boolean handlePieceActionList(IPiece piece, ArrayList<IPiece> pieces, List<PieceAction> actionList) {

		int numElements = -1;
		boolean ret = true;
		if (piece == null || pieces == null || actionList == null) {
			ret = false;
		}
		for (IPiece pHit : pieces) {
			numElements = numElements + 1;
			PieceAction action = actionList.get(numElements);
			if (action != null) {
				switch (action) {

				case MOVE_TO_TOWER: // Tar eksisternde brikke / tårn inn i nytt
									// tårn
					if (pHit.getInTowerWith() != null) {
						for (IPiece pUnder : pHit.getInTowerWith()) {
							piece.addInTowerWith(pUnder);
						}
					}
					pHit.clearInTowerWith();
					pHit.setEnabled(false);
					piece.addInTowerWith(pHit);
					break;

				case MOVE_TO_BASE: // slår hjem brikken / brikker i tårnet
					if (pHit.getInTowerWith() != null) {
						for (IPiece pUnder : pHit.getInTowerWith()) {
							pUnder.setEnabled(true);
							pUnder.placePieceInHouse();
						}
					}
					pHit.clearInTowerWith();
					pHit.setEnabled(true);
					pHit.placePieceInHouse();
					break;

				case MOVE_SIDE_BY_SIDE:
					// Not implementet
					break;

				default:
					// ugyldig action
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * Finn ut om posisjonen oppgitt kan kalles et trykk p� en brikke som
	 * tilh�rer den som skal flytte.
	 * 
	 * @param xPos
	 *            x-posisjon p� skjerm
	 * @param yPos
	 *            y-posisjon p� skjerm
	 * @param delta
	 * @return IPiece hvis den finnes
	 */
	public IPiece getPieceNearPos(PlayerColor playerColor, int xPos, int yPos, double delta) {
		IPiece retP = null;
		int brikkeNo = 0;
		delta = delta * 2; // Større testflate for treff.
		// PlayerColor currentColor =
		// GameHolder.getInstance().getTurnManager().getCurrentPlayerColor();
		IPlayer player = getLudoBoard().getPlayer(playerColor);
		double retAvst = 9000.0;
		double thisAvst = 0.0;
		for (IPiece brikke : player.getBrikker()) {
			if (brikke.isEnabled()) {
				Coordinate c = brikke.getCurrentPosition();
				Log.d("getPieceNearPos(LB)", "handleMove: brikke " + brikkeNo + ": " + c.x + "," + c.y);

				// Punkt er innenfor yttergrensene til brikke
				if (((c.x - delta) < xPos) && ((c.x + delta) > xPos) && ((c.y - delta) < yPos)
						&& ((c.y + delta) > yPos)) {

					// Avstand fra punkt til senter av koordinat er nærmere enn
					// andre brikker
					thisAvst = Math.sqrt(((xPos - c.x) * (xPos - c.x)) + ((yPos - c.y) * (yPos - c.y)));
					if (thisAvst < retAvst) {
						retP = brikke;
						retAvst = thisAvst;
					}
					Log.d("getPieceNearPos(LB)", "handleMove: farge: " + playerColor);
					Log.d("getPieceNearPos(LB)", "handleMove: thisAvst: " + thisAvst);
					Log.d("getPieceNearPos(LB)", "handleMove: brikke " + brikkeNo + " KAN  flyttes.");
				}
			}
			brikkeNo++;
		}

		return retP;
	}

	/**
	 * Finner alle brikker på samme sted som current spiller nettopp flyttet til
	 * 
	 * @param brikkeFlytt
	 *            brikke som nettopp er flyttet
	 * @retur Arraylist over brikker som må håndters av rules.
	 */
	private ArrayList<IPiece> findOtherPicesAtCoordinate(IPiece brikkeFlytt) {
		ArrayList<IPiece> pices = new ArrayList<IPiece>();
		Coordinate currentC = brikkeFlytt.getCurrentPosition();
		for (PlayerColor playerColor : PlayerColor.values()) {
			IPlayer player = getLudoBoard().getPlayer(playerColor);
			if (player != null && player.isActive()) {
				for (IPiece brikkeTest : player.getBrikker()) {
					if (brikkeTest.isEnabled()) {
						Coordinate cTest = brikkeTest.getCurrentPosition();
						// Sjekker om brikkene er på samme koordinater og at
						// brikkene ikke er av samme farge og homeposition
						if (currentC.equals(cTest)
								&& !((brikkeFlytt.getOwner().getColor().compareTo(playerColor) == 0) && brikkeFlytt
										.getHousePosition() == brikkeTest.getHousePosition())) {
							pices.add(brikkeTest);
							Log.d("Game", "findOtherPicesAtCoordinate: rules må håndtere: " + brikkeTest);
						}
					}
				}
			}
		}
		return pices;
	}

	// Debug
	public void DumpGame() {
		Log.d("DUMP", "Board image : " + gameImageName);
		ludoBoard.DumpGame();

	}

	/**
	 * Reset the game
	 */
	public void resetGame() {
		ludoBoard.resetGame();
	}

}
