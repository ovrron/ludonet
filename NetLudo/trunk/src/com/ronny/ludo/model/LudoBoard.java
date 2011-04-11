package com.ronny.ludo.model;

import java.util.Collection;
import java.util.Iterator;
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

	public LudoBoard() {
		players.put(PlayerColor.RED, new Player(PlayerColor.RED,this));
		players.put(PlayerColor.GREEN, new Player(PlayerColor.GREEN,this));
		players.put(PlayerColor.YELLOW, new Player(PlayerColor.YELLOW,this));
		players.put(PlayerColor.BLUE, new Player(PlayerColor.BLUE,this));
	}

	public void addPlayer(PlayerColor pc, IPlayer pl) {
		players.put(pc, pl);
	}

	/**
	 * En IPlayer flytter en brikke et visst antall ruter eller ut fra hus, eller
	 * hjem/goal
	 * 
	 * @param theColor
	 * @param theBrikke
	 * @param theMove
	 */
	public void playerMove(PlayerColor theColor, int theBrikke, int theMove) {
		IPlayer p = getPlayer(theColor);
		IPiece b = p.getBrikker()[theBrikke];
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
			}
		} else {
			// Move the brikke - delegate to player
			p.moveBrikke(theBrikke, theMove);
			
//			int currentPos = b.getBoardPosition();
//			// Sjekk om vi er på vei hjem
//			if(currentPos + theMove > p.getStartWayHomePosition()) {
//				int delta = p.getStartWayHomePosition() - currentPos;
//				b.setOnWayHome();
//				b.setBoardPosition(delta);				
//			} else if(currentPos + theMove > movingPath.size()) {
//				// Vi er kommet til en grense og må fortsette 'over' vektoren
//			
//			}else {
//				// Vi kan flytte normalt
//				b.addMove(theMove);;
//			}
		}
	}

	public IPlayer getPlayer(PlayerColor color) {
		IPlayer pl = players.get(color);
		return pl;
	}

	// Returnere en spiller basert på farge
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

	// Add commond fields - the path to move on
	public void addCommon(int pos, int x, int y) {
		movingPath.add(new Coordinate(pos, x, y));
	}

	/**
	 * Henter koordinat i path rundt bordet
	 * @param position
	 * @return
	 */
	public Coordinate getBoardPosition(int position) {
		Coordinate co = movingPath.elementAt(position);
		return co;
	}

	/**
	 * Brikker beveger seg i økende lengde fra 'home'. Dette er mapping til index fra relativ posisjon
	 * @param position
	 * @return
	 */
	public int getPathNumberFromRelativeMove(int currentPosition, int startingPosition) {
		int ret = 0;
		int pathLengde = movingPath.size();
		ret = (currentPosition + startingPosition) % pathLengde;
		return ret;
	}


	// Definerer første start i brettet ut fra home base
	public void addPlayerInfo(String playerColor, int firsMovePosition) {
		IPlayer pl = Game.getInstance().getPlayerInfo(playerColor);
		if (pl == null) {
			return;
		}
		pl.setFirstBoardPosition(firsMovePosition);
	}

	// Sets the position to when the
	public void setWayHomePosition(String playerColor, int fieldToStartWayHome) {
		IPlayer pl = getPlayer(playerColor);
		if (pl == null) {
			return;
		}
		pl.setStartWayHomePosition(fieldToStartWayHome);
	}

	// Add the home base definitions for a player
	public void addBaseHomeDefs(String col, Vector<Coordinate> baseHome) {
		IPlayer pl = getPlayer(col);
		pl.setHomePositions(baseHome);
	}

	// Add the way home definitions for a player
	public void addWayHomeDefs(String col, Vector<Coordinate> wayHome) {
		IPlayer pl = getPlayer(col);
		if (pl == null) {
			return;
		}
		pl.setWayHomePositions(wayHome);
	}

//	public void getHomePosition(PlayerColor color, int t) {
//		// TODO Auto-generated method stub
//
//	}

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

	public int recalcX(int x) {
		if(x==0) {
			Assert.fail("Image width is 0 - this would say that scaling gives 0");
		}
		int ret = (int) (((((float) imX) / ((float) xRes))) * x);
		return ret;
	}

	public int recalcY(int y) {
		if(y==0) {
			Assert.fail("Image height is 0 - this would say that scaling gives 0");
		}
		int ret = (int) (((((float) imY) / ((float) xRes))) * y);
		return ret;
	}

	/**
	 * Recalculating the positions based on the graphics attributes
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
	
	public PlayerColor getNextPlayerColor(PlayerColor playerColor)
	{
		IPlayer[] playersArray = (IPlayer[]) players.values().toArray();
		int i;
		for(i=0;i<playersArray.length;i++)
		{
			if(playersArray[i].getColor().compareTo(playerColor)==0)
			{
				if(i+1<playersArray.length)
				{
					return playersArray[i+1].getColor();
				}
				else
				{
					return playersArray[0].getColor();
				}
			}
		}
		return null;
	}
}
