package com.ronny.ludo.model;

import java.util.Vector;

import android.util.Log;


public class Player implements IPlayer{
	@SuppressWarnings("unused")
	private String TAG = "PLAYER:";
	
	private ILudoBoard owner;
	private PlayerColor color;
	private String iconPrefix = null;
	
	private boolean isActive = false;
	private int firstPositionOnBoard = 0; // F�rste posisjon i definisjonen av hovedsporet rundt bordet - se ILudoBoard.movingPath
	private int lastPositionBeforeWayHome = 0; // Hvor mange steg skal g�s f�r vi starter 'innover'
	private Vector<Coordinate> homePositions = new Vector<Coordinate>();
	private Vector<Coordinate> wayHomePositions = new Vector<Coordinate>();

	// Brikkene tilh�rer egentlig Game, men er fordelt p� spiller
	private IPiece brikker[] = new Piece[4];

	public Player(PlayerColor color,ILudoBoard owner) {
		this.owner = owner;
		this.setColor(color);
		for (int i = 0; i < 4; i++) {
			brikker[i] = new Piece(this);
			brikker[i].setHousePosition(i);
		}
		// default prefix er farge hvis ingen ting er gitt...
		iconPrefix = ""+color.toString().toLowerCase().charAt(0);
	}

	public IPiece[] getBrikker() {
		return brikker; 
	}
	

	/**
	 * @return the iconPrefix
	 */
	public String getIconPrefix() {
		return iconPrefix;
	}

	/**
	 * @param iconPrefix the iconPrefix to set
	 */
	public void setIconPrefix(String iconPrefix) {
		this.iconPrefix = iconPrefix;
		// Default if err in settings
		if(this.iconPrefix==null) {
			this.iconPrefix = ""+color.toString().toLowerCase().charAt(0);
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setColor(PlayerColor color) {
		this.color = color;
	}

	public PlayerColor getColor() {
		return color;
	}

	// Sette f�rste posisjon ut fra home
	public void setFirstBoardPosition(int firsMovePosition) {
		firstPositionOnBoard = firsMovePosition;
	}
	public int getFirstBoardPosition() {
		return firstPositionOnBoard;
	}

	// Siste posisjon f�r vi starter p� vi inn i brettet.
	public void setStartWayHomePosition(int fieldToStartWayHome) {
		lastPositionBeforeWayHome = fieldToStartWayHome;
	}
	public int getStartWayHomePosition() {
		return lastPositionBeforeWayHome;
	}

	// Sette koordinater for posisjon i home
	public void setHomePositions(Vector<Coordinate> baseHome) {
		homePositions = baseHome;
//		Log.d(TAG,"IPlayer hom<"+color.toString()+" + base home + "+homePositions);
	}
	
	public Vector<Coordinate> getHomePositions() {
//		Log.d(TAG,"IPlayer hom>"+color.toString()+" + base home + "+homePositions);
		return homePositions;
	}

	// Setter koordinater for 'way home' til m�l
	public void setWayHomePositions(Vector<Coordinate> wayHome) {
		wayHomePositions = wayHome;		
//		Log.d(TAG,"IPlayer way<"+color.toString()+" + way home + "+wayHomePositions);
	}
	
	public Vector<Coordinate> getWayHomePositions() {
//		Log.d(TAG,"IPlayer way>"+color.toString()+" + way home + "+wayHomePositions);
		return wayHomePositions;		
	}
	
	public String toString() {
		String str = "IPlayer: " +color.toString();
		return str;
	}

	//
	public Coordinate getBoardPosition(int numberOfMovesFromhome) {
		int boardIndex = owner.getPathNumberFromRelativeMove(numberOfMovesFromhome, firstPositionOnBoard);
		Coordinate co = owner.getBoardPosition(boardIndex);
		return co;
	}

	public void moveBrikke(int theBrikke, int theMove) {
		IPiece b = brikker[theBrikke];
//		int brikkeMoves = b.getBoardPosition();
		b.moveForward(theMove);
		
		// Sjekk om brikken 
//		if(brikkeMoves + theMove > getStartWayHomePosition()) {
//			int delta = etStartWayHomePosition() - brikkeMoves;
//			b.setOnWayHome(); // Sett at vi er p� vei hjem
//			b.setBoardPosition(delta); // ... og at vi er kommet s� langt inn				
//		} else if(currentPos + theMove > movingPath.size()) {
//			// Vi er kommet til en grense og m� fortsette 'over' vektoren
//		
//		}else {
//			// Vi kan flytte normalt
//			b.addMove(theMove);;
//		}
//		
//		int currentPos = b.getBoardPosition();
//		// Sjekk om vi er p� vei hjem
//		if(currentPos + theMove > p.getStartWayHomePosition()) {
//			int delta = p.getStartWayHomePosition() - currentPos;
//			b.setOnWayHome();
//			b.setBoardPosition(delta);				
//		} else if(currentPos + theMove > movingPath.size()) {
//			// Vi er kommet til en grense og m� fortsette 'over' vektoren
//		
//		}else {
//			// Vi kan flytte normalt
//			b.addMove(theMove);;
//		}

		
	}

	public void DumpGame() {
		Log.d("DUMP","  Player : "+color.toString());
		for(Coordinate c : homePositions) {
			Log.d("DUMP","    Homes : "+c);
		}
	}

}
