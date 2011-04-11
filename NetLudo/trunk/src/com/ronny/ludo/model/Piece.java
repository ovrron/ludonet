package com.ronny.ludo.model;

import android.util.Log;

public class Piece implements IPiece{
	private IPlayer owner;

	/**
	 * holder for board position - managed by Game. Board position is [-1
	 * (home), 0..n]
	 */
	private int boardPosition = -1;

	private int numberOfElements = 1; // Antall brikker i høyden 1..4
	private int housePosition = -1; // position in the 'house'
	private boolean isAtGoal = false; // true hvis brikke er i mål
	
	//TODO denne må beregnes etter hvert flytt
	private boolean isOnWayToGoal = false; // true hvis brikke er på tur i mål
	
//	@SuppressWarnings("unused")
//	private boolean isOnWayHome = false; // true hvis brikke er i mål

	public Piece(IPlayer owner) {
		this.owner = owner;
	}

//	public void setOnWayHome() {
//		isOnWayHome = true;
//	}

	public void setIsAtGoal() {
		isAtGoal = true;
	}

	public boolean isAtGoal() {
		return isAtGoal;
	}
	
	public boolean isHome() {
		return boardPosition==housePosition;
	}

	public IPlayer getOwner() {
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

	public void placePieceOnBoard() {
		boardPosition = owner.getFirstBoardPosition();
	}
	

	// Get the string id til bitmap denne representerer
	public String getId() {
		String ret = owner.getIconPrefix() + Integer.toString(numberOfElements);
		return ret;
	}

	// Flytte en brikke litt
	public void moveForward(int theMove) {
		// boardPosition += theMove;
		if (isAtGoal) {
			return;
		}
		
		Log.d("IPiece"," Col: "+owner.getColor().toString()+"IPiece: "+housePosition+" Current pos: "+boardPosition+" move: "+theMove);
		boardPosition += theMove;
		// Sjekk om vi har gått for langt...
		if (boardPosition >= (owner.getWayHomePositions().size() + owner.getStartWayHomePosition())) {
			boardPosition = owner.getWayHomePositions().size() + owner.getStartWayHomePosition();
			isAtGoal = true;
		}
	}

	public boolean isOnWayToGoal()
	{
		return isOnWayToGoal;
	}

	@Override
	public void placeBrikkeOnBoard()
	{
		// TODO Auto-generated method stub
		
	}

}
