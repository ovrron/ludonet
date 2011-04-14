package com.ronny.ludo.model;

import java.util.List;


public interface IPiece{
	
//	void setOnWayHome();
	public void setIsAtGoal();
	public boolean isAtGoal();
	public IPlayer getOwner();
	public PlayerColor getColor();
	public void setBoardPosition(int boardPosition);
	public int getBoardPosition();
	public void setHousePosition(int housePosition);
	public int getHousePosition();
	/**
	 * Returner current koordinat for brikke
	 * 
	 * @return
	 */
	public Coordinate getCurrentPosition();
	/**
	 * Finn koordinater til brikke på et visst antall flytt. 
	 * @param newPos posisjon (boardPosition)
	 * @return Coordinate eller null hvis antallet er lengre enn Goal (?)
	 */
	public Coordinate getPositionAtBoardPosition(int newPos);
	
	/**
	 * Kan en brikke flytte så mange plasser fram 
	 * @param numPos antall posisjoner fram
	 * @return true hvis den kan flytte, false hvis den ikke kan det. (Regler?)
	 */
	public boolean canPieceMove(int numPos);
	
	public void placePieceOnBoard();
	// Get the string id til bitmap denne representerer
	public String getId();
	// Flytte en brikke litt
	public void moveForward(int theMove);
	public boolean isHome();
	public boolean isOnWayToGoal();
	public boolean isEnabled();
	public void setEnabled(boolean enabled);
	public List<IPiece> getInTowerWith();
	public void addInTowerWith(IPiece piece);
	public void clearInTowerWith();
	public void placePieceInHouse();
}
