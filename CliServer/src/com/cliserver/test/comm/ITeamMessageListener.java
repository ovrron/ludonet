package com.cliserver.test.comm;

import java.io.Serializable;

/**
 * Listener interface for messages from the TeamMessageManager to a messagebroaker. 
 * 
 * @author ANDROD
 *
 */
public interface ITeamMessageListener {
	public void handleTeamMessage(Serializable message);
}
