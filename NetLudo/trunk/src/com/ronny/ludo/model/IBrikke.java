package com.ronny.ludo.model;


public interface IBrikke{
	
	void setOnWayHome();
	void setIsAtGoal();
	boolean isAtGoal();
	IPlayer getOwner();
	void setBoardPosition(int boardPosition);
	int getBoardPosition();
	void setHousePosition(int housePosition);
	int getHousePosition();
	/**
	 * Returner current koordinat for brikke
	 * 
	 * @return
	 */
	ICoordinate getCurrentPosition();
	void placeBrikkeOnBoard();
	// Get the string id til bitmap denne representerer
	String getId();
	// Flytte en brikke litt
	void moveForward(int theMove);
}
