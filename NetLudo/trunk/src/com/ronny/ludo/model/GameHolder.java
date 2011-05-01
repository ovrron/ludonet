/** 
* GameHolder.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.model;

import java.util.Vector;

import com.ronny.ludo.board.LudoSurfaceView;
import com.ronny.ludo.communication.LudoMessageBroker;
import com.ronny.ludo.communication.TeamMessageMgr;
import com.ronny.ludo.rules.IRules;
import com.ronny.ludo.rules.StandardRules;

/**
 * Game holder/controller
 * 
 * This class is a provider/holder of all the elements in the game - the Game class and other
 * remedies which may change if we decide to use another game type. 
 * 
 * @author ANDROD
 *
 */
public class GameHolder  {

	private Game game = null;  // The game class
	private LudoMessageBroker messageBroker = null;  // Message broker
	private TeamMessageMgr messageManager = null;  // MessageManager is always needed
	private TurnManager turnManager = null;
	private IRules rules = new StandardRules();
	private LudoSurfaceView surfaceView = null;
	private Vector<PlayerColor> localClientColors = null;
	private boolean soundOn = true;
	
	// Singleton type class
	private static GameHolder INSTANCE = null; 

	public static GameHolder getInstance() {
		if(INSTANCE==null) {
			INSTANCE = new GameHolder();
		}
		return INSTANCE;
	}

	// Instantiation prevention
	private GameHolder() {
		localClientColors = new Vector<PlayerColor>();
//		localClientColors.add(PlayerColor.NONE);
	}

	/**
	 * @return the LudoSurfaceView
	 */
	public LudoSurfaceView getSurfaceView() {
		return surfaceView;
	}

	/**
	 * @param LudoSurfaceView the surfaceView to set
	 */
	public void setSurfaceView(LudoSurfaceView surfaceView) {
		this.surfaceView = surfaceView;
	}
	
	public void resetPlayers(){
		localClientColors.removeAllElements();
	}
	
	
	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * @param game the game to set
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * @return the messageBroker
	 */
	public LudoMessageBroker getMessageBroker() {
		return messageBroker;
	}

	/**
	 * @param messageBroker the messageBroker to set
	 */
	public void setMessageBroker(LudoMessageBroker messageBroker) {
		this.messageBroker = messageBroker;
	}

	/**
	 * @return the messageManager
	 */
	public TeamMessageMgr getMessageManager() {
		return messageManager;
	}

	/**
	 * @param messageManager the messageManager to set
	 */
	public void setMessageManager(TeamMessageMgr messageManager) {
		this.messageManager = messageManager;
	}

	/**
	 * @return the turnManager
	 */
	public TurnManager getTurnManager() {
		return turnManager;
	}
	
	/**
     * @return the rules
     */
    public IRules getRules()
      {
        return rules;
      }

	/**
	 * @param turnManager the turnManager to set
	 */
	public void setTurnManager(TurnManager turnManager) {
		this.turnManager = turnManager;
	}

	/**
	 * @return the localClientColor
	 */
	public Vector<PlayerColor> getLocalClientColor() {
		return localClientColors;
	}

	/**
	 * @param localClientColor the localClientColor to set
	 */
	public void addLocalClientColor(PlayerColor localClientColor) {
		
		if(localClientColors.contains(PlayerColor.NONE))
		{
			localClientColors.remove(PlayerColor.NONE);
		}
		if(!localClientColors.contains(localClientColor))
		{
			localClientColors.add(localClientColor);	
		}
	}

	/**
	 * @return the soundOn
	 */
	public boolean isSoundOn()
	{
		return soundOn;
	}

	/**
	 * @param soundOn the soundOn to set
	 */
	public void setSoundOn(boolean soundOn)
	{
		this.soundOn = soundOn;
	}
	


}
