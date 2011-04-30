package com.ronny.ludo.model;

import java.util.Vector;

import android.util.Log;

/**
 * Player
 */ 

public class Player implements IPlayer{
	@SuppressWarnings("unused")
	private String TAG = "PLAYER:";
	
	private ILudoBoard owner;
	private PlayerColor color;
	private String iconPrefix = null;
	
	private boolean isActive = true; // Satt for test. Skal være false og styres av controller.
	private int firstPositionOnBoard = 0; // Første posisjon i definisjonen av hovedsporet rundt bordet - se ILudoBoard.movingPath
	private int lastPositionBeforeWayHome = 0; // Hvor mange steg skal gås før vi starter 'innover'
	private Vector<Coordinate> homePositions = new Vector<Coordinate>();
	private Vector<Coordinate> wayHomePositions = new Vector<Coordinate>();

	// Brikkene tilhører Game, men er fordelt på spiller
	private IPiece pieces[] = new Piece[4];

	
    /**
     * Konstruktør som oppretter en spiller
     *
     * @param color, spillerens farge
     * @param owner, ludobrettet som eier spiller
     */ 
	public Player(PlayerColor color,ILudoBoard owner) {
		this.owner = owner;
		this.setColor(color);
		for (int i = 0; i < 4; i++) {
			pieces[i] = new Piece(this);
			pieces[i].setHousePosition(i);
		}
		// default prefix er farge hvis ingen ting er gitt...
		iconPrefix = ""+color.toString().toLowerCase().charAt(0);
	}

	/**
     * Finner brikkene til spiller
     *
     * @return pieces, spillerens brikker
     */
	public IPiece[] getBrikker() {
		return pieces; 
	}
	

	/**
	 * Finner ikonprefix
	 * 
	 * @return the iconPrefix
	 */
	public String getIconPrefix() {
		return iconPrefix;
	}

	/**
	 * Setter ikonprefix
	 * 
	 * @param iconPrefix the iconPrefix to set
	 */
	public void setIconPrefix(String iconPrefix) {
		this.iconPrefix = iconPrefix;
		// Default if err in settings
		if(this.iconPrefix==null) {
			this.iconPrefix = ""+color.toString().toLowerCase().charAt(0);
		}
	}

	 /**
     * Sjekker om en spiller er aktiv
     *
     * @return isAvtive, true hvis spiller er aktiv, false hvis ikke
     */
	public boolean isActive() {
		return isActive;
	}
	
    /**
     * Setter en spiller som aktiv eller ikke
     *
     * @param isAvtive, true hvis spiller skal være aktiv, false hvis ikke
     */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

    /**
     * Setter farge til spiller
     *
     * @param color, fargen som skal settes
     */
	public void setColor(PlayerColor color) {
		this.color = color;
	}

    /**
     * Finner farge til spiller
     *
     * @return color, fargen til spiller
     */
	public PlayerColor getColor() {
		return color;
	}

    /**
     * Setter første posisjon ut fra home
     *
     * @param firstMovePosition, første posisjon til spiller
     */
	public void setFirstBoardPosition(int firsMovePosition) {
		firstPositionOnBoard = firsMovePosition;
	}

    /**
     * Finner første posisjon for spiller
     *
     * @return firstMovePosition, første posisjon til spiller
     */
	public int getFirstBoardPosition() {
		return firstPositionOnBoard;
	}

    /**
     * Setter siste posisjon før start av spiller sin vei hjem
     *
     * @param fieldToStartWayHome, siste posisjon før spiller går hjem
     */	
	public void setStartWayHomePosition(int fieldToStartWayHome) {
		lastPositionBeforeWayHome = fieldToStartWayHome;
	}
	
    /**
     * Finner siste posisjon før spiller sin vei hjem
     *
     * @return lastPositionBeforeWayHome, 
     */	
	public int getStartWayHomePosition() {
		return lastPositionBeforeWayHome;
	}

    /**
     * Setter koordinater for posisjon i home
     *
     * @param baseHome, vektor med posisjoner i home
     */ 	
	public void setHomePositions(Vector<Coordinate> baseHome) {
		homePositions = baseHome;
//		Log.d(TAG,"IPlayer hom<"+color.toString()+" + base home + "+homePositions);
	}
	
    /**
     * Finner koordinater for 'home positions'
     *
     * @return homePositions, vektor med koordinater
     */
	public Vector<Coordinate> getHomePositions() {
//		Log.d(TAG,"IPlayer hom>"+color.toString()+" + base home + "+homePositions);
		return homePositions;
	}

    /**
     * Setter koordinater for 'way home' til mål
     *
     * @param wayHome, vektor med koordinater for way home
     */ 	
	public void setWayHomePositions(Vector<Coordinate> wayHome) {
		wayHomePositions = wayHome;		
//		Log.d(TAG,"IPlayer way<"+color.toString()+" + way home + "+wayHomePositions);
	}

    /**
     * Finner koordinater for 'way home' til mål
     *
     * @return homePositions, vektor med koordinater
     */ 	
	public Vector<Coordinate> getWayHomePositions() {
//		Log.d(TAG,"IPlayer way>"+color.toString()+" + way home + "+wayHomePositions);
		return wayHomePositions;		
	}
	
    /**
     * Finner string repr. av spiller
     *
     * @return String, navn på spiller
     */ 	
	public String toString() {
		String str = "IPlayer: " +color.toString();
		return str;
	}

    /**
     * Finner koordinatene til brikke som er i posisjon numberOfMovesFromhome
     * 
     * @param numberOfMovesFromhome, antall flytt fra home
     * @return Coordinate, koordinatene til posisjonen
     */ 
	public Coordinate getBoardPosition(int numberOfMovesFromhome) {
		int boardIndex = owner.getPathNumberFromRelativeMove(numberOfMovesFromhome, firstPositionOnBoard);
		Coordinate co = owner.getBoardPosition(boardIndex);
		return co;
	}

    /**
     * Flytter en brikke et gitt antall steg
     * 
     * @param theBrikke, brikken som skal flyttes
     * @param theMove, antall flytt som skal gjøres
     */ 	
	public void moveBrikke(int theBrikke, int theMove) {
		IPiece b = pieces[theBrikke];
		b.moveForward(theMove);
	}
	
    /**
     * Dump av spill for debug
     */ 
	public void DumpGame() {
		Log.d("DUMP","  Player : "+color.toString());
		for(Coordinate c : homePositions) {
			Log.d("DUMP","    Homes : "+c);
		}
		Log.d("DUMP","  Player : "+color.toString());
		for(IPiece b : pieces) {
			if(b.isHome()) { 
				Log.d("DUMP","    Brikkepos : Home");
			} else {
				Log.d("DUMP","    Brikkepos : "+b);
			}
		}
	}

	/**
	 * Sjekker om spiller har aktive brikker i spill
	 * 
	 * @return, false hvis alle spillernes pieces er hjemme eller i mål, ellers true
	 */
	public boolean hasPiecesInPlay()
	{
		for(IPiece piece:pieces)
		{
			if(!piece.isAtGoal() && !piece.isHome())
			{
				return true;
			}
		}
		return false;
	}
	
	 /**
	 * Sjekker om spiller er kommet i mål med alle brikkene
	 * 
     * @return boolean, true hvis alle spillernes pieces er i mål, ellers false
     */
    public boolean isAtGoal()
    {
        for(IPiece piece:pieces)
        {
            if (piece.isEnabled())
            {
                if(!piece.isAtGoal())
                {
                    return false;
                }
            }
        }
        return true;
    }

}
