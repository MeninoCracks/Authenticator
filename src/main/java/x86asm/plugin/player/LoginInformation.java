/*
 * Author: Cyrus
 * Created: 6/29/2014
 * Purpose: This class is essentially our "database" loaded into memory.
 *          It is thread safe and follows the singleton design.
 *          
 *          Users must first get an instance via getInstance()
 *          before they call any methods in this class.
 */

package x86asm.plugin.player;

import java.util.Map;

public class LoginInformation {
	
	private Map<String, String> userInfo;
	private boolean isSet = false;
	
	private static class LoginInformationLoader {
		private static LoginInformation INSTANCE = new LoginInformation();
	}
	
	/**
	 * Returns a class instance.  All methods must be called from
	 * the returned instance.
	 * @return instance to this class
	 */
	public static LoginInformation getInstance() {
		return LoginInformationLoader.INSTANCE;
	}
	
	/**
	 * Set the database to a Map existing in memory
	 * @param userInformation
	 */
	public synchronized void set(Map<String, String> userInformation) {
		this.userInfo = userInformation;
		this.isSet = true;
	}
	
	/**
	 * Add an entry to our Map.  The caller is responsible
	 * for adding it to the database as well.
	 * @param username
	 * @param password
	 * @return true on success, false on error
	 */
	public synchronized boolean add(String username, String password) {
		if(isSet) {
			userInfo.put(username,  password);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets a persons password from their username
	 * @param username
	 * @return the usernames password, null if the database hasn't been set, and null if the user is not in the database
	 */
	public synchronized String get(String username) {
		if(isSet) {
			return userInfo.get(username);
		}
		return null;
	}
	
}
