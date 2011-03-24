package com.ronny.ludo.model;

import java.util.Vector;

public interface IPlayer
{
	public IBrikke[] getBrikker();
	public boolean isActive();
	public void setActive(boolean isActive);
	public void setColor(PlayerColor color);
	public PlayerColor getColor();
	public String getIconPrefix();
	public void setIconPrefix(String newPrefix);
	// Sette første posisjon ut fra home
	public void setFirstBoardPosition(int firsMovePosition);
	public int getFirstBoardPosition();
	// Siste posisjon før vi starter på vi inn i brettet.
	public void setStartWayHomePosition(int fieldToStartWayHome);
	public int getStartWayHomePosition();
	// Sette koordinater for posisjon i home
	public void setHomePositions(Vector<ICoordinate> baseHome);
	public Vector<ICoordinate> getHomePositions();
	// Setter koordinater for 'way home' til mål
	public void setWayHomePositions(Vector<ICoordinate> wayHome);
	public Vector<ICoordinate> getWayHomePositions();
	public ICoordinate getBoardPosition(int numberOfMovesFromhome);
	public void moveBrikke(int theBrikke, int theMove);
	
	public String toString();

}
