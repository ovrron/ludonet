package com.cliserver.test.comm;

import java.io.Serializable;

import android.os.Handler;

/**
 * Team message manager interface
 * 
 * @author ANDROD
 *
 */
public interface ITeamMessageManager {
	public void addListener(Handler messageBroakerClient);
	public void removeListener(Handler messageBroakerClient);
	public void sendMessage(Serializable outgoingMessage);
	
	public boolean isRegistrationOpen();
	public void openRegistration();
	public void closeRegistration();
	
	/**
	 * Handler for debug messages
	 * @param handle
	 */
	public void setHandler(Handler handle);
}
