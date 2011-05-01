/** 
* IPlayer.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.model;

import java.util.Vector;

public interface IPlayer
{
	public IPiece[] getBrikker();
	public boolean isActive();
	public void setActive(boolean isActive);
	public void setColor(PlayerColor color);
	public PlayerColor getColor();
	public String getIconPrefix();
	public void setIconPrefix(String newPrefix);
	public void setFirstBoardPosition(int firsMovePosition);
	public int getFirstBoardPosition();
	public void setStartWayHomePosition(int fieldToStartWayHome);
	public int getStartWayHomePosition();
	public void setHomePositions(Vector<Coordinate> baseHome);
	public Vector<Coordinate> getHomePositions();
	public void setWayHomePositions(Vector<Coordinate> wayHome);
	public Vector<Coordinate> getWayHomePositions();
	public Coordinate getBoardPosition(int numberOfMovesFromhome);
	public void moveBrikke(int theBrikke, int theMove);
	public String toString();
	public void DumpGame();
	public boolean hasPiecesInPlay();
	public boolean isAtGoal();
}
