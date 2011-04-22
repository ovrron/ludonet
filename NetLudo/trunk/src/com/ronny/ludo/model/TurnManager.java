package com.ronny.ludo.model;

import java.util.Vector;

import com.ronny.ludo.model.TurnManager.PlayerLocation;

import android.util.Log;

public class TurnManager {

	/**
	 * Definition of a location - local, remote or not allocated
	 */
	public static enum PlayerLocation {
		FREE, LOCAL, REMOTE;
	};

	private PlayerColor currentTurnColor;
	private int currentTurnPos = -1;
	private int numPlayers = 0;

	/**
	 * Define a player, color and location
	 * 
	 * @author ANDROD
	 */
	private class APlayer {
		private PlayerColor color;
		private PlayerLocation location;

		public APlayer() {
		}

		public APlayer(PlayerColor color) {
			this.color = color;
			this.location = PlayerLocation.FREE;
		}

		public void setLocation(PlayerLocation newLocation) {
			this.location = newLocation;
		}

		public PlayerLocation getLocation() {
			return location;
		}

		public void setColor(PlayerColor newColor) {
			this.color = newColor;
		}

		public PlayerColor getColor() {
			return color;
		}
	}

	// Vector with all players, allocated colors and location
	private Vector<APlayer> players = new Vector<APlayer>(); // Clients' colors
																// and location

	// Current players' color

	// Default constructor
	public TurnManager() {
//		players.add(new APlayer(PlayerColor.RED));
//		players.add(new APlayer(PlayerColor.GREEN));
//		players.add(new APlayer(PlayerColor.YELLOW));
//		players.add(new APlayer(PlayerColor.BLUE));
	}
	
	
	/**
	 * Legger til en spillerfarge
	 * @param playerColor
	 */
	public void addPlayer(PlayerColor playerColor)
	{
		if(!players.contains(playerColor))
		{
			players.add(new APlayer(playerColor));
		}
	}
	
	/**
	 * Henter spillfargen til alle spillerne som er med
	 * @return vector med PlayerColor
	 */
	public Vector<PlayerColor> getPlayers()
	{
		Vector<PlayerColor> playerColors = null;
		for(APlayer ap:players)
		{
			if(playerColors==null)
			{
				playerColors = new Vector<PlayerColor>();
			}
			playerColors.add(ap.getColor());
		}
		return playerColors;
	}
	
	/**
	 * Release a previously allocated color.
	 * This is my silent tribute to 'Free Willie'.
	 * 
	 * @param theCol
	 */
	public void freeColor(PlayerColor theCol) {
		for (APlayer ap : players) {
			if (ap.getColor() == theCol) {
				ap.setLocation(PlayerLocation.FREE);
				Log.d("TurnMGR",
						"Color Freed " + theCol.toString());
				return;
			}
		}
	}

	/**
	 * Get a free player color.
	 * 
	 * @param remote
	 *            where is the player asking for the color from ?
	 * @param wantedColor
	 *            try to allocate the color if it can be done.
	 * @param shouldSelectAnother
	 *            true if the routine should give you another free if the wanted
	 *            color is not free
	 * @return allocated color
	 */
	public PlayerColor getFreeColor(PlayerLocation remoteOrLocal,
			PlayerColor wantedColor, boolean shouldSelectAnother) {

		PlayerColor wColor = (wantedColor == null ? PlayerColor.NONE
				: wantedColor);

		// See if the color is free
		if (wColor != PlayerColor.NONE) {
			for (APlayer ap : players) {
				if (ap.getColor() == wColor) {
					if (ap.getLocation() == PlayerLocation.FREE) {
						ap.setLocation(remoteOrLocal);
						Log.d("TurnMGR",
								"Color allocated (wanted) " + wColor.toString());
						numPlayers++;
						return ap.getColor();
					}
				}
			}
		}

		if (shouldSelectAnother) {
			// Otherwise - check all free colors for first match
			for (APlayer ap : players) {
				if (ap.getLocation() == PlayerLocation.FREE) {
					ap.setLocation(remoteOrLocal);
					Log.d("TurnMGR", "Color allocated (free) "
							+ ap.getColor().toString());
					numPlayers++;
					return ap.getColor();
				}
			}
		}

		// Default return 'no color'
		Log.d("TurnMGR", "NO Color allocated...");
		return PlayerColor.NONE;
	}

	/**
	 * Get the first players' color and set positions.
	 * 
	 * @return
	 */
	private PlayerColor getFirstPlayerColor() {
		for (int i = 0; i < players.size(); i++) {
			APlayer p = players.get(i);
			if (p.getLocation() != PlayerLocation.FREE) {
				currentTurnColor = p.getColor();
				currentTurnPos = i;
				return p.getColor();
			}
		}
		return PlayerColor.NONE;
	}

	/**
	 * Advance to the next players color. The rules decides if there is a change
	 * in players, this class just do the transision.
	 * 
	 * @return the players color
	 */
	public PlayerColor advanceToNextPlayer() {
//		Log.d("TurnMGR", "Current color : " + currentTurnColor.toString());
		// If first time called - init to the first player.
		if (currentTurnPos < 0) {
			return getFirstPlayerColor();
		}
		
		if (numPlayers == 0) {
			return PlayerColor.NONE;
		}
		if (numPlayers == 1) {
			return currentTurnColor;
		}
		for (int i = 1; i < players.size(); i++) {
			Log.d("TurnMGR", "Advance index : "
					+ ((i + currentTurnPos) % players.size()));
			APlayer p = players.get(((i + currentTurnPos) % players.size()));
			if (p.getLocation() != PlayerLocation.FREE) {
				currentTurnPos = ((i + currentTurnPos) % players.size());
				currentTurnColor = p.getColor();
				return currentTurnColor;
			}
		}

		return PlayerColor.NONE;
	}

	/**
	 * Get the current player color.
	 * 
	 * @return
	 */
	public PlayerColor getCurrentPlayerColor() {
		// If first time called - init to the first player.
		if (currentTurnPos < 0) {
			return getFirstPlayerColor();
		}
		return currentTurnColor;
	}
	

	/**
	 * Check if this color is currently available
	 * @param theCol
	 * @return
	 */
	public boolean isFree(PlayerColor theCol) {
		for (APlayer pl : players) {
			if (pl.getColor() == theCol) {
				return (pl.getLocation()==PlayerLocation.FREE);
			}
		}
		return false;
	}

	/**
	 * Check if this color is currently in use locally
	 * @param theCol
	 * @return
	 */
	public boolean isLocal(PlayerColor theCol) {
		for (APlayer pl : players) {
			if (pl.getColor() == theCol) {
				return (pl.getLocation()==PlayerLocation.LOCAL);
			}
		}
		return false;
	}
	/**
	 * Check if this color is currently in use remotely
	 * @param theCol
	 * @return
	 */
	public boolean isRemote(PlayerColor theCol) {
		for (APlayer pl : players) {
			if (pl.getColor() == theCol) {
				return (pl.getLocation()==PlayerLocation.REMOTE);
			}
		}
		return false;
	}

	/**
	 * Get the location for a color
	 * @param color
	 * @return
	 */
	public PlayerLocation getLocation(PlayerColor color) {
		for (APlayer pl : players) {
			if (pl.getColor() == color) {
				return pl.getLocation();
			}
		}
		return null;
	}
	
	public void setLoaction(PlayerColor color, PlayerLocation location)
	{
		for (APlayer pl : players) {
			if (pl.getColor() == color) {
				pl.setLocation(location);
			}
		}
	}

}
