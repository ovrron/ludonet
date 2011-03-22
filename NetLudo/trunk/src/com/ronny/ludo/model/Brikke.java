package com.ronny.ludo.model;

import android.util.Log;

public class Brikke {
	private Player owner;

	/**
	 * holder for board position - managed by Game. Board position is [-1
	 * (home), 0..n]
	 */
	private int boardPosition = -1;

	private int numberOfElements = 1; // Antall brikker i høyden 1..4
	private int housePosition = -1; // position in the 'house'
	private boolean isAtGoal = false; // true hvis brikke er i mål
	private boolean isOnWayHome = false; // true hvis brikke er i mål

	public Brikke(Player owner) {
		this.owner = owner;
	}

	public void setOnWayHome() {
		isOnWayHome = true;
	}

	public void setIsAtGoal() {
		isAtGoal = true;
	}

	public boolean isAtGoal() {
		return isAtGoal;
	}

	public Player getOwner() {
		return owner;
	}

	public void setBoardPosition(int boardPosition) {
		this.boardPosition = boardPosition;
	}

	public int getBoardPosition() {
		return boardPosition;
	}

	public void setHousePosition(int housePosition) {
		this.housePosition = housePosition;
	}

	public int getHousePosition() {
		return housePosition;
	}

	/**
	 * Returner current koordinat for brikke
	 * 
	 * @return
	 */
	public Coordinate getCurrentPosition() {
		Coordinate co = null;
		if (boardPosition < 0) {
			// Still in house - position housePosition
			co = owner.getHomePositions().elementAt(housePosition);
		} else {
			
			
			if( boardPosition > owner.getStartWayHomePosition()) {
				// Vi er på vei inn
				int wayHpos = boardPosition - owner.getStartWayHomePosition() - 1;
				co = owner.getWayHomePositions().elementAt(wayHpos);
			} else {
				co = owner.getBoardPosition(boardPosition);
			}
//			
//			
//			
//			// Check goal - or last point
//			if (isOnWayHome) {
//				if (boardPosition < owner.getWayHomePositions().size()) {
//					co = owner.getWayHomePositions().elementAt(boardPosition);
//				} else {
//					co = owner.getWayHomePositions().elementAt(
//							owner.getWayHomePositions().size());
//				}
//			} else {
//				if (isAtGoal) {
//					// Siste element i wayHome-listen
//					co = owner.getWayHomePositions().elementAt(
//							owner.getWayHomePositions().size());
//				} else {
//					// and we are still on the board
//					// Positions are -1..n - relative from 'home'
//					co = owner.getBoardPosition(boardPosition);
//				}
//			}
		}
		return co;
	}

	public void placeBrikkeOnBoard() {
		boardPosition = owner.getFirstBoardPosition();
	}

	// Get the string id til bitmap denne representerer
	public String getId() {
		String ret = owner.getColor().toString().toLowerCase().charAt(0)
				+ Integer.toString(numberOfElements);
		return ret;
	}

	// Flytte en brikke litt
	public void moveForward(int theMove) {
		// boardPosition += theMove;
		if (isAtGoal) {
			return;
		}
		
		Log.d("Brikke"," Col: "+owner.getColor().toString()+"Brikke: "+housePosition+" Current pos: "+boardPosition+" move: "+theMove);
		boardPosition += theMove;
		// Sjekk om vi har gått for langt...
		if (boardPosition >= (owner.getWayHomePositions().size() + owner.getStartWayHomePosition())) {
			boardPosition = owner.getWayHomePositions().size() + owner.getStartWayHomePosition();
			isAtGoal = true;
		}
	}

}
