/*
 * Author: Cyrus
 * Created: 6/29/2014
 * Purpose: A plugin to be used with bukkit 1.7.9 servers
 *          so that people can reserve usernames
 */

package x86asm.plugin.authenticator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import x86asm.plugin.commands.Commands;
import x86asm.plugin.events.Events;
import x86asm.plugin.io.FileDatabase;
import x86asm.plugin.player.LoginInformation;

public final class Authenticator extends JavaPlugin {

	public static final String dbName = "AuthenticatorDatabase.db";
		
	@Override
	public void onEnable() {
		// Bukkit events
		getServer().getPluginManager().registerEvents(new Events(), this);
		Commands commands = new Commands();
		getCommand("register").setExecutor(commands);
		getCommand("login").setExecutor(commands);	
		
		// Try loading our database
		FileDatabase db = FileDatabase.getInstance();
		if(!db.dbExists(dbName)) {
			try {
				db.createDatabase(dbName);
			} catch(IOException ex) {
				// We don't have permission to create files
				getLogger().log(Level.SEVERE, "[Authenticator] ERROR: COULD NOT CREATE CONFIG FILE.");
			}
		}
		
		try {
			Map<String, String> userConfig = db.getConfig(dbName);
			if(userConfig == null) {
				// No users are registered with the server
				getLogger().log(Level.INFO, "[Authenticator] INFO: There are no users registered to this server yet.");
				LoginInformation.getInstance().set(new HashMap<String, String>());
			}
			else {
				// Load the database, people are registered,
				LoginInformation.getInstance().set(userConfig);
			}
		} catch(RuntimeException ex) {
			// This occurs if:
			// a) We have an invalid database entry (bad format)
			// b) We have two entries for the same username
			// c) There was an IOException.  Do we have write permissions?
			getLogger().log(Level.SEVERE, "[Authenticator] " + ex.getMessage());
		}
	}
	
	@Override
	public void onDisable() {
		getLogger().log(Level.WARNING, "[Authenticator] This plugin has been disabled.");
	}
	
}
