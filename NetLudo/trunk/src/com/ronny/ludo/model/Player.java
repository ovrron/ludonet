package com.ronny.ludo.model;

import java.util.Vector;

public interface Player
{
	Brikke[] getBrikker();
	boolean isActive();
	void setActive(boolean isActive);
	void setColor(PlayerColor color);
	PlayerColor getColor();
	// Sette f�rste posisjon ut fra home
	void setFirstBoardPosition(int firsMovePosition);
	int getFirstBoardPosition();
	// Siste posisjon f�r vi starter p� vi inn i brettet.
	void setStartWayHomePosition(int fieldToStartWayHome);
	int getStartWayHomePosition();
	// Sette koordinater for posisjon i home
	void setHomePositions(Vector<Coordinate> baseHome);
	Vector<Coordinate> getHomePositions();
	// Setter koordinater for 'way home' til m�l
	void setWayHomePositions(Vector<Coordinate> wayHome);
	Vector<Coordinate> getWayHomePositions();
	String toString();
	Coordinate getBoardPosition(int numberOfMovesFromhome);
	void moveBrikke(int theBrikke, int theMove);
}
