package com.ronny.ludo.model;


public interface IBrikke{
	
//	void setOnWayHome();
	public void setIsAtGoal();
	public boolean isAtGoal();
	public IPlayer getOwner();
	public void setBoardPosition(int boardPosition);
	public int getBoardPosition();
	public void setHousePosition(int housePosition);
	public int getHousePosition();
	/**
	 * Returner current koordinat for brikke
	 * 
	 * @return
	 */
	public ICoordinate getCurrentPosition();
	public void placeBrikkeOnBoard();
	// Get the string id til bitmap denne representerer
	public String getId();
	// Flytte en brikke litt
	public void moveForward(int theMove);
}
