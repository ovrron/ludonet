package com.ronny.ludo.model;

import java.util.Vector;

public interface IPlayer
{
	IBrikke[] getBrikker();
	boolean isActive();
	void setActive(boolean isActive);
	void setColor(PlayerColor color);
	PlayerColor getColor();
	// Sette første posisjon ut fra home
	void setFirstBoardPosition(int firsMovePosition);
	int getFirstBoardPosition();
	// Siste posisjon før vi starter på vi inn i brettet.
	void setStartWayHomePosition(int fieldToStartWayHome);
	int getStartWayHomePosition();
	// Sette koordinater for posisjon i home
	void setHomePositions(Vector<ICoordinate> baseHome);
	Vector<ICoordinate> getHomePositions();
	// Setter koordinater for 'way home' til mål
	void setWayHomePositions(Vector<ICoordinate> wayHome);
	Vector<ICoordinate> getWayHomePositions();
	String toString();
	ICoordinate getBoardPosition(int numberOfMovesFromhome);
	void moveBrikke(int theBrikke, int theMove);
}
