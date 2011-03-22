package com.ronny.ludo.model;


public interface Brikke{
	
	void setOnWayHome();
	void setIsAtGoal();
	boolean isAtGoal();
	Player getOwner();
	void setBoardPosition(int boardPosition);
	int getBoardPosition();
	void setHousePosition(int housePosition);
	int getHousePosition();
	/**
	 * Returner current koordinat for brikke
	 * 
	 * @return
	 */
	Coordinate getCurrentPosition();
	void placeBrikkeOnBoard();
	// Get the string id til bitmap denne representerer
	String getId();
	// Flytte en brikke litt
	void moveForward(int theMove);
}
