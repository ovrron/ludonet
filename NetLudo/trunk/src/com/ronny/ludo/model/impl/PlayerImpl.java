package com.ronny.ludo.model.impl;

import java.util.Vector;

import com.ronny.ludo.model.Brikke;
import com.ronny.ludo.model.Coordinate;
import com.ronny.ludo.model.LudoBoard;
import com.ronny.ludo.model.Player;
import com.ronny.ludo.model.PlayerColor;

public class PlayerImpl implements Player{
	private LudoBoard owner;
	private String TAG = "PLAYER:";
	private PlayerColor color;
	private boolean isActive = false;
	private int firstPositionOnBoard = 0; // Første posisjon i definisjonen av hovedsporet rundt bordet - se LudoBoard.movingPath
	private int lastPositionBeforeWayHome = 0; // Hvor mange steg skal gås før vi starter 'innover'
	private Vector<Coordinate> homePositions = null;
	private Vector<Coordinate> wayHomePositions = null;

	// Brikkene tilhører egentlig Game, men er fordelt på spiller
	private Brikke brikker[] = new BrikkeImpl[4];

	public PlayerImpl(PlayerColor color,LudoBoard owner) {
		this.owner = owner;
		this.setColor(color);
		for (int i = 0; i < 4; i++) {
			brikker[i] = new BrikkeImpl(this);
			brikker[i].setHousePosition(i);
		}
	}

	public Brikke[] getBrikker() {
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
	public void setHomePositions(Vector<Coordinate> baseHome) {
		homePositions = baseHome;
//		Log.d(TAG,"Player hom<"+color.toString()+" + base home + "+homePositions);
	}
	
	public Vector<Coordinate> getHomePositions() {
//		Log.d(TAG,"Player hom>"+color.toString()+" + base home + "+homePositions);
		return homePositions;
	}

	// Setter koordinater for 'way home' til mål
	public void setWayHomePositions(Vector<Coordinate> wayHome) {
		wayHomePositions = wayHome;		
//		Log.d(TAG,"Player way<"+color.toString()+" + way home + "+wayHomePositions);
	}
	
	public Vector<Coordinate> getWayHomePositions() {
//		Log.d(TAG,"Player way>"+color.toString()+" + way home + "+wayHomePositions);
		return wayHomePositions;		
	}
	
	public String toString() {
		String str = "Player: " +color.toString();
		return str;
	}

	//
	public Coordinate getBoardPosition(int numberOfMovesFromhome) {
		int boardIndex = owner.getPathNumberFromRelativeMove(numberOfMovesFromhome, firstPositionOnBoard);
		Coordinate co = owner.getBoardPosition(boardIndex);
		return co;
	}

	public void moveBrikke(int theBrikke, int theMove) {
		Brikke b = brikker[theBrikke];
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
