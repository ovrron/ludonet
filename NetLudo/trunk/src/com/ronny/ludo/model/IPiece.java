/** 
* IPiece.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.model;

import java.util.List;


public interface IPiece{

	public void setIsAtGoal();
	public boolean isAtGoal();
	public IPlayer getOwner();
	public PlayerColor getColor();
	public void setBoardPosition(int boardPosition);
	public int getBoardPosition();
	public void setHousePosition(int housePosition);
	public int getHousePosition();
	public Coordinate getCurrentPosition();
	public Coordinate getPositionAtBoardPosition(int newPos);
	public boolean canPieceMove(int numPos);
	public void placePieceOnBoard();
	public String getId();
	public void moveForward(int theMove);
	public boolean isHome();
	public boolean isOnWayToGoal();
	public boolean isEnabled();
	public void setEnabled(boolean enabled);
	public List<IPiece> getInTowerWith();
	public void addInTowerWith(IPiece piece);
	public void clearInTowerWith();
	public void placePieceInHouse();
	public String toString();
	public boolean highLight();
	public void highLight(boolean highlight);
}
