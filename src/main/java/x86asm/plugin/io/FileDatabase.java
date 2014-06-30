/*
 * Author: Cyrus
 * Created: 6/29/2014
 * Purpose: This singleton class acts as our database.
 *          it allows us to load our database file into
 *          a map object, and to add entries into our 
 *          database.
 *          
 *          It is NOT responsible for adding newly 
 *          logged in users to the LoginInformation class,
 *          NOR is it responsible for hashing passwords.
 *          
 *          Since more than one thread might call
 *          any of our database methods, a singleton
 *          design has been implemented.  Users
 *          must first get an instance of this class
 *          using getInstance() before calling any methods.
 *          
 *          This class is thread safe.
 */

package x86asm.plugin.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class FileDatabase {
	
	private static class FileDatabaseLoader {
		private static FileDatabase INSTANCE = new FileDatabase();
	}
	
	/**
	 * Gets the instance of this singleton class.
	 * @return instance to this class
	 */
	public static FileDatabase getInstance() {
		return FileDatabaseLoader.INSTANCE;
	}
	
	/**
	 * Check if the db file already exists
	 * @param config
	 * @return true if exists, false if it doesnt
	 */
	public synchronized boolean dbExists(String config) {
		File file = new File(config);
		return file.exists();
	}
	
	/**
	 * Create the db file. Throws an IOException if there
	 * are problems with permissions.
	 * @param configName
	 * @return true if it was created, false if it already exists
	 * @throws IOException
	 */
	public synchronized boolean createDatabase(String configName) throws IOException {
		File file = new File(configName);		
		return file.createNewFile();
	}
	
	/**
	 * Add a username and password to the db file
	 * @param dbName
	 * @param username
	 * @param password
	 * @return true on success, false on error i.e. no permission
	 */
	public synchronized boolean append(String dbName, String username, String password) {
		boolean succeeded = true;
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(dbName, true)))) {
			out.println(username + ":" + password);
		} catch(IOException ex) {
			succeeded = false;
		}
		return succeeded;
	}
	
	/**
	 * Loads the db file into memory.  The LoginInformation.java class
	 * will contain all of the usernames and passwords inside of the db file.
	 * The register/login event is responsible for hashing the password.
	 * @param dbName
	 * @return a map object that is either empty, filled with entries, or null if the db doesn't exist
	 * @throws RuntimeException
	 */
	public synchronized Map<String, String> getConfig(String dbName) throws RuntimeException {
		Map<String, String> userInfo = null;
		File db = new File(dbName);
		int lineNumber = 0;
		
		if(db.exists()) {
			userInfo = new HashMap<String, String>();
			try(BufferedReader in = new BufferedReader(new FileReader(dbName))) {
				// There is one db entry per line
				for(String line; (line = in.readLine()) != null;) {
					String[] split = line.split(":");
					// The format of the database is username:password
					if(split.length != 2) {		
						// Bad format
						throw new RuntimeException("Invalid database entry on line: " + Integer.toString(lineNumber));
					}
					else if(userInfo.containsKey(split[0])) {
						// Multiple entries for the same username
						throw new RuntimeException("Database has two entries for the same username");
					}
					else {
						// Everything is fine.
						userInfo.put(split[0], split[1]);
					}
					lineNumber++;
				}
			} catch(IOException ex) {
				// Throw exception, so our Authenticator class
				// can log the error and tell the user to check permissions
				throw new RuntimeException("IOException: " + ex.getMessage());
			}
		}
		
		return userInfo;
	}
	
}
