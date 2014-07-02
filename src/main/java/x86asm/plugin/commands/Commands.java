/*
 * Author: Cyrus
 * Created: 6/29/2014
 * Purpose: Methods which react to the /login and
 *          /register commands.
 *          
 *          This is the code responsible for
 *          adding users to the database,
 *          hashing passwords (TODO),
 *          and logging users in (adding them to PlayerList)       
 */

package x86asm.plugin.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import x86asm.plugin.authenticator.Authenticator;
import x86asm.plugin.io.FileDatabase;
import x86asm.plugin.player.LoginInformation;
import x86asm.plugin.player.PlayerList;
import x86asm.plugin.security.BCrypt;

public class Commands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			if(cmd.getName().equalsIgnoreCase("register")) {
				return onRegister((Player)sender, args);
			} else if(cmd.getName().equalsIgnoreCase("login")) {
				return onLogin((Player)sender, args);
			}
		}
		
		return false;
	}
	
	/**
	 * Called when the user types /register.
	 * Passwords must be alphanumeric and
	 * the user must not already be registered
	 * @param player
	 * @param args
	 * @return true on success, false on error / bad input
	 */
	private boolean onRegister(Player player, String[] args) {
		
		if(args.length != 1) {
			// We only accept one arg, the password
			player.sendMessage(ChatColor.RED + "Format: /register <password>");
			return false;
		}
		
		// Info received from the user
		String username = player.getName().toLowerCase();
		String password = args[0];
		
		// Passwords must be alphanumeric
		if(!StringUtils.isAlphanumeric(password)) {
			player.sendMessage(ChatColor.RED + "Passwords must be alphanumeric. (a-z, A-Z, 0-9).");
			return false;
		}
		
		// This class is essentially our "database" loaded into memory
		LoginInformation loginInformation = LoginInformation.getInstance();
		
		if(loginInformation.get(username) != null) {
			// You can't register a username which someone else already has.
			player.sendMessage(ChatColor.RED + "That username is already registered.  Use /login <password> to login to it.");
			return false;
		}
		
		// Hash our password
		password = BCrypt.hashpw(password, BCrypt.gensalt());
		
		// If we reach here, that means everything is OK and we can successfully register the user
		loginInformation.add(username, password);
		boolean success = FileDatabase.getInstance().append(Authenticator.dbName, username, password);
		
		if(!success) {
			// Oh no, not everything was okay! We couldn't write to the database for some reason.
			player.sendMessage(ChatColor.RED + "Fatal error: Unable to add information to database.  Contact the server admin.");
			return false;
		} else {
			// Everything is okay!
			player.sendMessage(ChatColor.GREEN + "Success! You are now registered and can login to the game with /login <password>");
		}
		
		return true;
	}
	
	/**
	 * Called when the user types /login
	 * @param player
	 * @param args
	 * @return true on success, false on error / bad input
	 */
	private boolean onLogin(Player player, String[] args) {
		
		// One arg only, the password
		if(args.length != 1) {
			player.sendMessage(ChatColor.RED + "Format: /login <password>");
			return false;
		}
		
		String inputPassword = args[0];
		if(!StringUtils.isAlphanumeric(inputPassword)) {
			player.sendMessage(ChatColor.RED + "Passwords must be alphanumeric. (a-z, A-Z, 0-9).");
			return false;
		}
		
		// Is the password correct for the persons username?
		String registeredPassword = LoginInformation.getInstance().get(player.getName().toLowerCase());
		if(registeredPassword == null) {
			player.sendMessage(ChatColor.RED + "This account has not yet been registered.");
			return false;
		}		
		
		if(BCrypt.checkpw(inputPassword, registeredPassword)) {
			loginPlayer(player);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Logs the player into the game and adds them
	 * to the login list so that our hooks in Events.java
	 * do not effect them.  
	 * @param player
	 */
	private void loginPlayer(Player player) {		
		player.sendMessage(ChatColor.GREEN + "Logged in!");
		PlayerList.getInstance().add(player);
	}
	
}
