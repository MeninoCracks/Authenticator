/*
 * Author: Cyrus
 * Created: 6/29/2014
 * Purpose: A singleton, thread-safe class which is responsible for
 *          holding a list of all players who are currently logged into the game
 *          and are thus allowed to chat, move, interact, etc.
 */

package x86asm.plugin.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class PlayerList {
	
	private static class PlayerListLoader {
		private static PlayerList INSTANCE = new PlayerList();
	}
	
	private List<Player> playerList = new ArrayList<Player>();	

	/**
	 * Users must call this to get an instance to
	 * the class, allowing them to use the
	 * thread-safe methods existing within 
	 * this class.
	 * @return a class instance of this class
	 */
	public static PlayerList getInstance() {
		return PlayerListLoader.INSTANCE;
	}
	
	/**
	 * Add the player to the "logged in" list.
	 * @param player
	 */
	public void add(Player player) {
		synchronized(playerList) {
			playerList.add(player);
		}
	}
	
	/**
	 * Check if the player is "logged in"
	 * @param player
	 * @return
	 */
	public boolean contains(Player player) {
		boolean exists = false;
		synchronized(playerList) {
			exists = playerList.contains(player);
		}
		return exists;
	}
	
	/**
	 * Remove the player from the "logged in" list, i.e.,
	 * when they disconnect.
	 * @param player
	 */
	public void remove(Player player) {
		synchronized(playerList) {
			playerList.remove(player);
		}
	}
	
}
