package com.ronny.ludo.model;

import java.util.Vector;


public class Player implements IPlayer{
	private ILudoBoard owner;
	private String TAG = "PLAYER:";
	private PlayerColor color;
	private boolean isActive = false;
	private int firstPositionOnBoard = 0; // Første posisjon i definisjonen av hovedsporet rundt bordet - se ILudoBoard.movingPath
	private int lastPositionBeforeWayHome = 0; // Hvor mange steg skal gås før vi starter 'innover'
	private Vector<ICoordinate> homePositions = null;
	private Vector<ICoordinate> wayHomePositions = null;

	// Brikkene tilhører egentlig Game, men er fordelt på spiller
	private IBrikke brikker[] = new Brikke[4];

	public Player(PlayerColor color,ILudoBoard owner) {
		this.owner = owner;
		this.setColor(color);
		for (int i = 0; i < 4; i++) {
			brikker[i] = new Brikke(this);
			brikker[i].setHousePosition(i);
		}
	}

	public IBrikke[] getBrikker() {
		return brikker; 
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

	// Sette første posisjon ut fra home
	public void setFirstBoardPosition(int firsMovePosition) {
		firstPositionOnBoard = firsMovePosition;
	}
	public int getFirstBoardPosition() {
		return firstPositionOnBoard;
	}

	// Siste posisjon før vi starter på vi inn i brettet.
	public void setStartWayHomePosition(int fieldToStartWayHome) {
		lastPositionBeforeWayHome = fieldToStartWayHome;
	}
	public int getStartWayHomePosition() {
		return lastPositionBeforeWayHome;
	}

	// Sette koordinater for posisjon i home
	public void setHomePositions(Vector<ICoordinate> baseHome) {
		homePositions = baseHome;
//		Log.d(TAG,"IPlayer hom<"+color.toString()+" + base home + "+homePositions);
	}
	
	public Vector<ICoordinate> getHomePositions() {
//		Log.d(TAG,"IPlayer hom>"+color.toString()+" + base home + "+homePositions);
		return homePositions;
	}

	// Setter koordinater for 'way home' til mål
	public void setWayHomePositions(Vector<ICoordinate> wayHome) {
		wayHomePositions = wayHome;		
//		Log.d(TAG,"IPlayer way<"+color.toString()+" + way home + "+wayHomePositions);
	}
	
	public Vector<ICoordinate> getWayHomePositions() {
//		Log.d(TAG,"IPlayer way>"+color.toString()+" + way home + "+wayHomePositions);
		return wayHomePositions;		
	}
	
	public String toString() {
		String str = "IPlayer: " +color.toString();
		return str;
	}

	//
	public ICoordinate getBoardPosition(int numberOfMovesFromhome) {
		int boardIndex = owner.getPathNumberFromRelativeMove(numberOfMovesFromhome, firstPositionOnBoard);
		ICoordinate co = owner.getBoardPosition(boardIndex);
		return co;
	}

	public void moveBrikke(int theBrikke, int theMove) {
		IBrikke b = brikker[theBrikke];
		int brikkeMoves = b.getBoardPosition();
		b.moveForward(theMove);
		
		// Sjekk om brikken 
//		if(brikkeMoves + theMove > getStartWayHomePosition()) {
//			int delta = etStartWayHomePosition() - brikkeMoves;
//			b.setOnWayHome(); // Sett at vi er på vei hjem
//			b.setBoardPosition(delta); // ... og at vi er kommet så langt inn				
//		} else if(currentPos + theMove > movingPath.size()) {
//			// Vi er kommet til en grense og må fortsette 'over' vektoren
//		
//		}else {
//			// Vi kan flytte normalt
//			b.addMove(theMove);;
//		}
//		
//		int currentPos = b.getBoardPosition();
//		// Sjekk om vi er på vei hjem
//		if(currentPos + theMove > p.getStartWayHomePosition()) {
//			int delta = p.getStartWayHomePosition() - currentPos;
//			b.setOnWayHome();
//			b.setBoardPosition(delta);				
//		} else if(currentPos + theMove > movingPath.size()) {
//			// Vi er kommet til en grense og må fortsette 'over' vektoren
//		
//		}else {
//			// Vi kan flytte normalt
//			b.addMove(theMove);;
//		}

		
	}

}
