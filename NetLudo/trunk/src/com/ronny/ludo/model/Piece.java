package com.ronny.ludo.model;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Piece implements IPiece{
	private IPlayer owner;

	/**
	 * holder for board position - managed by Game. Board position is [-1
	 * (home), 0..n]
	 */
	private int boardPosition = -1;
//	private int numberOfElements = 1; // Antall brikker i høyden 1..4
	private int housePosition = -1; // position in the 'house'
	private boolean isAtGoal = false; // true hvis brikke er i mål
	private boolean enabled = true; //brikken er med i spillet (ikke som en del av et tårn)
	private List<IPiece> inTowerWith = null;
	
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

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public List<IPiece> getInTowerWith()
	{
		return inTowerWith;
	}

	public void addInTowerWith(IPiece piece)
	{
		if(inTowerWith==null)
		{
			inTowerWith = new ArrayList<IPiece>();
		}
		inTowerWith.add(piece);
	}
	
	public void clearInTowerWith()
	{
		inTowerWith=null;
	}
	
	public void setIsAtGoal() {
		isAtGoal = true;
	}

	public boolean isAtGoal() {
		return isAtGoal;
	}
	
	public boolean isHome() {
		return boardPosition==-1;
	}

	public IPlayer getOwner() {
		return owner;
	}

	public PlayerColor getColor() {
		return owner.getColor();
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
		}
		return co;
	}

	public void placePieceOnBoard() {
		boardPosition = owner.getFirstBoardPosition();
	}
	
	//TODO Denne må implementeres
	public void placePieceInHouse()
	{
		boardPosition = -1; // Settes til 'i huset'
	}

	// Get the string id til bitmap denne representerer
	public String getId() {
		String ret = null;
		if(enabled)
		{
			if(inTowerWith==null || inTowerWith.size()==0)
			{
				ret = owner.getIconPrefix() + "1";
			}
			else
			{
				ret = owner.getIconPrefix() + Integer.toString(inTowerWith.size()+1);
			}
		}
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

	public Coordinate getPositionAtBoardPosition(int newPos) {
		// 
		if(newPos >= (owner.getWayHomePositions().size() + owner.getStartWayHomePosition())) {
			return null;
		}
		
		Coordinate co = null;
		if (newPos < 0) {
			// Still in house - position housePosition
			co = owner.getHomePositions().elementAt(housePosition);
		} else {
			
			if( newPos > owner.getStartWayHomePosition()) {
				// Vi er p� vei inn
				int wayHpos = newPos - owner.getStartWayHomePosition() - 1;
				co = owner.getWayHomePositions().elementAt(wayHpos);
			} else {
				co = owner.getBoardPosition(newPos);
			}
		}
		
		return co;
	}

	public boolean canPieceMove(int numPos) {
		// TODO Her burde vi konferere med regler ?
		if ( (boardPosition + numPos) > (owner.getWayHomePositions().size() + owner.getStartWayHomePosition())) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		return owner.getBoardPosition(boardPosition).toString();
	}

}
