package com.ronny.ludo.model;

import java.util.Vector;

public interface ILudoBoard{
	
	void addPlayer(PlayerColor pc, IPlayer pl);
	/**
	 * En IPlayer flytter en brikke et visst antall ruter eller ut fra hus, eller
	 * hjem/goal
	 * 
	 * @param theColor
	 * @param theBrikke
	 * @param theMove
	 */
	void playerMove(PlayerColor theColor, int theBrikke, int theMove);
	IPlayer getPlayer(PlayerColor color);
	// Returnere en spiller basert på farge
	IPlayer getPlayer(String theColor);
	// Add commond fields - the path to move on
	void addCommon(int pos, int x, int y);
	/**
	 * Henter koordinat i path rundt bordet
	 * @param position
	 * @return
	 */
	ICoordinate getBoardPosition(int position);
	/**
	 * Brikker beveger seg i økende lengde fra 'home'. Dette er mapping til index fra relativ posisjon
	 * @param position
	 * @return
	 */
	int getPathNumberFromRelativeMove(int currentPosition, int startingPosition);
	// Definerer første start i brettet ut fra home base
	void addPlayerInfo(String playerColor, int firsMovePosition);
	// Sets the position to when the
	void setWayHomePosition(String playerColor, int fieldToStartWayHome);
	// Add the home base definitions for a player
	void addBaseHomeDefs(String col, Vector<ICoordinate> baseHome);
	// Add the way home definitions for a player
	void addWayHomeDefs(String col, Vector<ICoordinate> wayHome);
//	public void getHomePosition(PlayerColor color, int t);
	/**
	 * Set the initial resolution the board definitions where read from
	 * 
	 * @param x
	 * @param y
	 */
	void setDefinitionResolution(int x, int y);
	/**
	 * Set graphics board size (may vary dependent on the DPI
	 * 
	 * @param x
	 * @param y
	 */
	void setGraphicsResolution(int x, int y);
	int recalcX(int x);
	int recalcY(int y);
	/**
	 * Recalculating the positions based on the graphics attributes
	 */
	void recalcPositions();
}
