/** 
* LudoBoard.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.model;

import java.util.LinkedHashMap;
import java.util.Vector;
import junit.framework.Assert;
import android.util.Log;
import com.ronny.ludo.helper.LudoConstants;

public class LudoBoard implements ILudoBoard {

	@SuppressWarnings("unused")
	private int xRes = 0, yRes = 0; // definitions resolution
	private int imX = 0, imY = 0; // Target image resolution

	private Vector<Coordinate> movingPath = new Vector<Coordinate>(); // Veien rundt bordet.
	private LinkedHashMap<PlayerColor, IPlayer> players = new LinkedHashMap<PlayerColor, IPlayer>();

	 /**
     * Konstruktør for LodoBoard  
     */ 
	public LudoBoard() {
		players.put(PlayerColor.RED, new Player(PlayerColor.RED,this));
		players.put(PlayerColor.GREEN, new Player(PlayerColor.GREEN,this));
		players.put(PlayerColor.YELLOW, new Player(PlayerColor.YELLOW,this));
		players.put(PlayerColor.BLUE, new Player(PlayerColor.BLUE,this));
	}
    
	/**
     * Legger til en ny spiller  
     * 
     * @param pc    Spillerens farge
     * @param pl    Spilleren
     */ 
	public void addPlayer(PlayerColor pc, IPlayer pl) {
		players.put(pc, pl);
	}

	/**
	 * En IPlayer flytter en brikke et visst antall ruter eller ut fra hus, eller
	 * hjem/goal
	 * 
	 * @param theColor     Spillerens farge
	 * @param theBrikke    Brikken som skal flyttes
	 * @param theMove      Antall flytt av brikke
	 */
	public IPiece playerMove(PlayerColor theColor, int theBrikke, int theMove) {
		IPlayer p = getPlayer(theColor);
		IPiece b = p.getBrikker()[theBrikke];
		// For test: De tre neste linjene tas ut hvis raskt flytt av tårn før homeway
		if (b.isHome()) {
	        theMove = LudoConstants.MOVE_FROM_HOUSE;
	    }
		if (theMove > 6) {
			// Command move
			switch(theMove) {
			case LudoConstants.MOVE_BACK_TO_HOUSE:
				b.setBoardPosition(-1);
				Log.d("IPiece(LB)","Col:"+theColor.toString()+" Back to House - brikke "+theBrikke);
				break;
			case LudoConstants.MOVE_FROM_HOUSE:
				Log.d("IPiece(LB)","Col:"+theColor.toString()+" From House - brikke "+theBrikke);
				b.setBoardPosition(0);
				break;
			case LudoConstants.MOVE_TO_GOAL:
				break;
			// For test: De to neste linjene tas inn hvis raskt flytt av tårn før homeway	
			// default: 
			// p.moveBrikke(theBrikke, theMove);
			}
		} else {
			// Move the brikke - delegate to player
			p.moveBrikke(theBrikke, theMove);
		}
		return b; 
	}

    /**
     * Finner spiller basert på farge  
     * 
     * @param color
     */ 	
	public IPlayer getPlayer(PlayerColor color) {
		IPlayer pl = players.get(color);
		return pl;
	}

     /**
     * Returnere en spiller basert på farge/streng  
     * 
     * @param theColor  Fargen til spilleren
     */ 
	public IPlayer getPlayer(String theColor) {
		IPlayer ret = null;
		if (theColor.compareToIgnoreCase("RED") == 0) {
			ret = getPlayer(PlayerColor.RED);
		} else if (theColor.compareToIgnoreCase("GREEN") == 0) {
			ret = getPlayer(PlayerColor.GREEN);
		} else if (theColor.compareToIgnoreCase("BLUE") == 0) {
			ret = getPlayer(PlayerColor.BLUE);
		} else if (theColor.compareToIgnoreCase("YELLOW") == 0) {
			ret = getPlayer(PlayerColor.YELLOW);
		}
		return ret;
	}

    /**
     * Legger til common felt - veien rundt brettet  
     * 
     * @param pos
     * @param x
     * @param y
     */ 
	public void addCommon(int pos, int x, int y) {
		movingPath.add(new Coordinate(pos, x, y));
	}

	/**
	 * Henter koordinat i path rundt bordet
	 * 
	 * @param position
	 * @return
	 */
	public Coordinate getBoardPosition(int position) {
		Coordinate co = movingPath.elementAt(position);
		return co;
	}

	/**
	 * Brikker beveger seg i økende lengde fra 'home'. Dette er mapping til index fra relativ posisjon
	 * 
	 * @param position
	 * @return ret
	 */
	public int getPathNumberFromRelativeMove(int currentPosition, int startingPosition) {
		int ret = 0;
		int pathLengde = movingPath.size();
		ret = (currentPosition + startingPosition) % pathLengde;
		return ret;
	}


    /**
     * Definerer første start i brettet ut fra home base  
     * 
     * @param playerColor           Spillerens farge
     * @param firsMovePosition      Første start i brettet
     */	
	public void addPlayerInfo(String playerColor, int firsMovePosition) {
		IPlayer pl = getPlayer(playerColor);
		if (pl == null) {
			return;
		}
		pl.setFirstBoardPosition(firsMovePosition);
	}

    /**
     * Setter posisjon før start mot eget målområde  
     * 
     * @param playerColor           Spillerens farge
     * @param fieldToStartWayHome   Felt før start hjem/mål
     */ 	
	public void setWayHomePosition(String playerColor, int fieldToStartWayHome) {
		IPlayer pl = getPlayer(playerColor);
		if (pl == null) {
			return;
		}
		pl.setStartWayHomePosition(fieldToStartWayHome);
	}
	
    /**
     * Legger inn home base definitions for en spiller  
     * 
     * @param col       Spillerens farge
     * @param baseHome  Felt før start hjem/mål
     */ 
	public void addBaseHomeDefs(String col, Vector<Coordinate> baseHome) {
		IPlayer pl = getPlayer(col);
		pl.setHomePositions(baseHome);
	}

    /**
     * Legger inn way home definitions for en spiller  
     * 
     * @param col       Spillerens farge
     * @param wayHome   Vektor som representerer way home området til en spiller. 
     */
	public void addWayHomeDefs(String col, Vector<Coordinate> wayHome) {
		IPlayer pl = getPlayer(col);
		if (pl == null) {
			return;
		}
		pl.setWayHomePositions(wayHome);
	}

	/**
	 * Set the initial resolution the board definitions where read from
	 * 
	 * @param x
	 * @param y
	 */
	public void setDefinitionResolution(int x, int y) {
		xRes = x;
		yRes = y;
	}

	/**
	 * Set graphics board size (may vary dependent on the DPI
	 * 
	 * @param x
	 * @param y
	 */
	public void setGraphicsResolution(int x, int y) {
		imX = x;
		imY = y;
	}

    /**
     * Rekalkulerer X  
     * 
     * @param x 
     */
	public int recalcX(int x) {
		if(x==0) {
			Assert.fail("Image width is 0 - this would say that scaling gives 0");
		}
		int ret = (int) (((((float) imX) / ((float) xRes))) * x);
		return ret;
	}
	
    /**
     * Rekalkulerer Y  
     * 
     * @param y 
     */
	public int recalcY(int y) {
		if(y==0) {
			Assert.fail("Image height is 0 - this would say that scaling gives 0");
		}
		int ret = (int) (((((float) imY) / ((float) xRes))) * y);
		return ret;
	}

	/**
	 * Rekalkulerer posisjonene basert på grafikk
	 */
	public void recalcPositions() {
		// WayHome
		for (IPlayer p : players.values()) {
			for (Coordinate c : p.getWayHomePositions()) {
				c.x = recalcX(c.x);
				c.y = recalcX(c.y);
			}
		}

		// IPlayer Homes
		for (IPlayer p : players.values()) {
			for (Coordinate c : p.getHomePositions()) {
				c.x = recalcX(c.x);
				c.y = recalcX(c.y);
			}
		}

		// Moving path
		for (Coordinate c : movingPath) {
			c.x = recalcX(c.x);
			c.y = recalcX(c.y);
		}

	}

	public void DumpGame() {
		Log.d("DUMP","Moving path : "+movingPath);
		for(IPlayer p : players.values()) {
			p.DumpGame();
		}
	}

	/**
	 * Reset the game
	 */
	public void resetGame() {
//
		
	}
}
