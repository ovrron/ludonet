package com.ronny.ludo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;

import com.ronny.ludo.communication.LudoMessageBroker;
import com.ronny.ludo.communication.TeamMessageMgr;
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
	 * @param turnManager the turnManager to set
	 */
	public void setTurnManager(TurnManager turnManager) {
		this.turnManager = turnManager;
	}
	
//	// TurnManager slot holder
//	public TurnManager getTurnManager() {
//		return turnManager;
//	}
//	
//	public void setTurnManager(TurnManager tm) {
//		this.turnManager = tm;
//	}


}
