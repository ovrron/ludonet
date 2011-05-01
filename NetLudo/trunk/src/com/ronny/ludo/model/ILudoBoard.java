/** 
* ILudoBoard.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.model;

import java.util.Vector;

public interface ILudoBoard{
	
	public void addPlayer(PlayerColor pc, IPlayer pl);
	public IPiece playerMove(PlayerColor theColor, int theBrikke, int theMove);
	IPlayer getPlayer(PlayerColor color);
	IPlayer getPlayer(String theColor);
	public void addCommon(int pos, int x, int y);
	public Coordinate getBoardPosition(int position);
	public int getPathNumberFromRelativeMove(int currentPosition, int startingPosition);
	public void addPlayerInfo(String playerColor, int firsMovePosition);
	public void setWayHomePosition(String playerColor, int fieldToStartWayHome);
	public void addBaseHomeDefs(String col, Vector<Coordinate> baseHome);
	public void addWayHomeDefs(String col, Vector<Coordinate> wayHome);
	public void setDefinitionResolution(int x, int y);
	public void setGraphicsResolution(int x, int y);
	public int recalcX(int x);
	public int recalcY(int y);
	void recalcPositions();
	public void DumpGame();
	public void resetGame();
}
