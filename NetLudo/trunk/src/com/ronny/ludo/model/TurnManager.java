/** 
* TurnManager.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.model;

import java.util.Vector;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class TurnManager {

	/**
	 * Definition of a location - local, remote or not allocated
	 */
	public static enum PlayerLocation {
		FREE, LOCAL, REMOTE;

		public static PlayerLocation getLocationFromString(String theLocation) {
			if (theLocation == null) {
				return FREE;
			}
			for (PlayerLocation pll : PlayerLocation.values()) {
				if (pll.toString().compareTo(theLocation) == 0) {
					return pll;
				}
			}
			return FREE;
		}
	};

	private PlayerColor currentTurnColor;
	private int currentTurnPos = -1;

	// private int numPlayers = 0;

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

		/** 
		* Create a player 
		* 
		* @param color
		*/
		public APlayer(PlayerColor color) {
			this.color = color;
			this.location = PlayerLocation.FREE;
		}

      /** 
        * Set location based on new location 
        * 
        * @param newLocation
        */
		public void setLocation(PlayerLocation newLocation) {
			this.location = newLocation;
		}

      /** 
        * Returns current location 
        * 
        * @return location
        */
		public PlayerLocation getLocation() {
			return location;
		}

      /** 
        * Returns current colcor 
        * 
        * @return color
        */
		public PlayerColor getColor() {
			return color;
		}
	}

	//Vector with all players, allocated colors and location
	private Vector<APlayer> players = new Vector<APlayer>();

    /** 
     * Default constructor 
    **/
	public TurnManager() {
	}

	/**
	 * Add a new player based on playercolor
	 * 
	 * @param playerColor
	 */
	public void addPlayer(PlayerColor playerColor) {
		if (!players.contains(playerColor)) {
			players.add(new APlayer(playerColor));
		}
	}

	/**
	 * Returns a vector with the colors for all players
	 * 
	 * @return vector      with all PlayerColor
	 */
	public Vector<PlayerColor> getPlayers() {
		Vector<PlayerColor> playerColors = null;
		if (players != null) {
			for (APlayer ap : players) {
				if (playerColors == null) {
					playerColors = new Vector<PlayerColor>();
				}
				playerColors.add(ap.getColor());
			}
		}
		return playerColors;
	}

    /**
     * Returns a String with all players
     * 
     * @return vector      with all PlayerColor
     */	
	public String getPlayersJSON() {
		JSONObject retVal = new JSONObject();
		try {
			int i = 0;
			for (APlayer ap : players) {
				if (ap.getLocation() != PlayerLocation.FREE) {
					JSONObject player = new JSONObject();
					player.put("PlayerColor", ap.getColor().toString());
					player.put("PlayerLocation", ap.getLocation().toString());
					retVal.put("Player" + i++, player);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retVal.toString();
	}

    /**
     * Set all players
     * 
     * @param players
     */ 
	public void setPlayersJSON(String players) {
		try {
			JSONObject jSonObject = new JSONObject(players);
			this.players = new Vector<APlayer>();
			int i = 0;
			while (jSonObject.has("Player" + i)) {
				JSONObject jSonPlayer = jSonObject.getJSONObject("Player" + i++);
				APlayer player = new APlayer(PlayerColor.getColorFromString(jSonPlayer.getString("PlayerColor")));
				player.setLocation(PlayerLocation.getLocationFromString(jSonPlayer.getString("PlayerLocation")));
				this.players.add(player);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Release a previously allocated color. This is my silent tribute to 'Free
	 * Willie'.
	 * 
	 * @param theCol
	 */
	public void freeColor(PlayerColor theCol) {
		for (APlayer ap : players) {
			if (ap.getColor() == theCol) {
				ap.setLocation(PlayerLocation.FREE);
				Log.d("TurnMGR", "Color Freed " + theCol.toString());
				return;
			}
		}
	}

	/**
	 * Get a free player color.
	 * 
	 * @param remote               where is the player asking for the color from ?
	 * @param wantedColor          try to allocate the color if it can be done.
	 * @param shouldSelectAnother  true if the routine should give you another free if the wanted
	 *                             color is not free
	 * @return allocated           color
	 */
	public PlayerColor getFreeColor(PlayerLocation remoteOrLocal, PlayerColor wantedColor, boolean shouldSelectAnother) {

		PlayerColor wColor = (wantedColor == null ? PlayerColor.NONE : wantedColor);

		// See if the color is free
		if (wColor != PlayerColor.NONE) {
			for (APlayer ap : players) {
				if (ap.getColor() == wColor) {
					if (ap.getLocation() == PlayerLocation.FREE) {
						ap.setLocation(remoteOrLocal);
						Log.d("TurnMGR", "Color allocated (wanted) " + wColor.toString());
						// numPlayers++;
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
					Log.d("TurnMGR", "Color allocated (free) " + ap.getColor().toString());
					// numPlayers++;
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
		// Log.d("TurnMGR", "Current color : " + currentTurnColor.toString());
		// If first time called - init to the first player.

		PlayerColor retVal = PlayerColor.NONE;

		if (currentTurnPos < 0) {
			// return getFirstPlayerColor();
			retVal = getFirstPlayerColor();
		} else if (getNumPlayers() == 0) {
			// return PlayerColor.NONE;
			retVal = PlayerColor.NONE;
		} else if (getNumPlayers() == 1) {
			// return currentTurnColor;
			retVal = currentTurnColor;
		} else {
			for (int i = 1; i < players.size(); i++) {
				Log.d("TurnMGR", "Advance index : " + ((i + currentTurnPos) % players.size()));
				APlayer p = players.get(((i + currentTurnPos) % players.size()));
				if (p.getLocation() != PlayerLocation.FREE) {
					currentTurnPos = ((i + currentTurnPos) % players.size());
					currentTurnColor = p.getColor();
					// return currentTurnColor;
					retVal = currentTurnColor;
					break;
				}
			}

		}
		// GameHolder.getInstance().getMessageBroker().sendCurrentPlayer(retVal);
		return retVal;
	}

    /**
     * Returns the number of players
     * 
     * @return numberofplayers
     */ 
	public int getNumPlayers() {
		int retVal = 0;
		for (int i = 0; i < players.size(); i++) {
			APlayer p = players.get(i);
			if (p.getLocation() != PlayerLocation.FREE) {
				retVal++;
			}
		}
		return retVal;
	}

	/**
	 * Get the current player color.
	 * 
	 * @return currentTurnColor
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
	 * 
	 * @param theCol
	 * @return true    if color is free, else false
	 */
	public boolean isFree(PlayerColor theCol) {
		for (APlayer pl : players) {
			if (pl.getColor() == theCol) {
				return (pl.getLocation() == PlayerLocation.FREE);
			}
		}
		return false;
	}

	/**
	 * Check if this color is currently in use locally
	 * 
	 * @param theCol
	 * @return true    if player is local, else false
	 */
	public boolean isLocal(PlayerColor theCol) {
		for (APlayer pl : players) {
			if (pl.getColor() == theCol) {
				return (pl.getLocation() == PlayerLocation.LOCAL);
			}
		}
		return false;
	}

	/**
	 * Check if this color is currently in use remotely
	 * 
	 * @param theCol
	 * @return true    if player is remote, else false
	 */
	public boolean isRemote(PlayerColor theCol) {
		for (APlayer pl : players) {
			if (pl.getColor() == theCol) {
				return (pl.getLocation() == PlayerLocation.REMOTE);
			}
		}
		return false;
	}

	/**
	 * Get the location for a color
	 * 
	 * @param color
	 * @return currentlocation
	 */
	public PlayerLocation getLocation(PlayerColor color) {
		for (APlayer pl : players) {
			if (pl.getColor() == color) {
				return pl.getLocation();
			}
		}
		return null;
	}
	
    /**
     * Set the location for playercolor
     * 
     * @param color
     * @return location
     */	
	public void setLoaction(PlayerColor color, PlayerLocation location) {
		for (APlayer pl : players) {
			if (pl.getColor() == color) {
				pl.setLocation(location);
			}
		}
	}

	/**
	 * Resetting the player colors to 'free'
	 */
	public void resetGame() {
		if (players != null) {
			for (APlayer ap : players) {
				ap.setLocation(PlayerLocation.FREE);
			}
		}
	}

}
