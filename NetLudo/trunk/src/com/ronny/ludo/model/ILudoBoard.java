package com.ronny.ludo.model;

import java.util.Vector;

public interface ILudoBoard{
	
	public void addPlayer(PlayerColor pc, IPlayer pl);
	/**
	 * En IPlayer flytter en brikke et visst antall ruter eller ut fra hus, eller
	 * hjem/goal
	 * 
	 * @param theColor
	 * @param theBrikke
	 * @param theMove
	 */
	public void playerMove(PlayerColor theColor, int theBrikke, int theMove);
	IPlayer getPlayer(PlayerColor color);
	// Returnere en spiller basert p� farge
	IPlayer getPlayer(String theColor);
	// Add commond fields - the path to move on
	public void addCommon(int pos, int x, int y);
	/**
	 * Henter koordinat i path rundt bordet
	 * @param position
	 * @return
	 */
	public ICoordinate getBoardPosition(int position);
	/**
	 * Brikker beveger seg i �kende lengde fra 'home'. Dette er mapping til index fra relativ posisjon
	 * @param position
	 * @return
	 */
	public int getPathNumberFromRelativeMove(int currentPosition, int startingPosition);
	// Definerer f�rste start i brettet ut fra home base
	public void addPlayerInfo(String playerColor, int firsMovePosition);
	// Sets the position to when the
	public void setWayHomePosition(String playerColor, int fieldToStartWayHome);
	// Add the home base definitions for a player
	public void addBaseHomeDefs(String col, Vector<ICoordinate> baseHome);
	// Add the way home definitions for a player
	public void addWayHomeDefs(String col, Vector<ICoordinate> wayHome);
//	public void getHomePosition(PlayerColor color, int t);
	/**
	 * Set the initial resolution the board definitions where read from
	 * 
	 * @param x
	 * @param y
	 */
	public void setDefinitionResolution(int x, int y);
	/**
	 * Set graphics board size (may vary dependent on the DPI
	 * 
	 * @param x
	 * @param y
	 */
	public void setGraphicsResolution(int x, int y);
	public int recalcX(int x);
	public int recalcY(int y);
	/**
	 * Recalculating the positions based on the graphics attributes
	 */
	void recalcPositions();
	public void DumpGame();
}
