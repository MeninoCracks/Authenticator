/*
 * Author: Cyrus
 * Created: 6/29/2014
 * Purpose: Deny users who are not logged in various permissions such as
 *          moving, chatting, interacting with blocks, etc. *          
 */

package x86asm.plugin.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import x86asm.plugin.player.PlayerList;

public class Events implements Listener {	
	
	/**
	 * Called when a player joins the server.
	 * We set the player to creative mode and
	 * show the login prompt message.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();			
		showLoginPrompt(player);		
	}
	
	/**
	 * Called when a player uses a command.
	 * We will disallow them from using commands 
	 * (except login or register) until they are
	 * logged in
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST) 
	private void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if(!PlayerList.getInstance().contains(player)) {			
			String msg = event.getMessage().toLowerCase();
			if((!msg.startsWith("/register")) && (!msg.startsWith("/login"))) {			
				showLoginPrompt(player);
				event.setCancelled(true);
			}
		}
	}
	
	/**
	 * Called when the player tries to move.
	 * We will not allow them to move until
	 * they have logged in.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(!PlayerList.getInstance().contains(player)) {
			Location from = event.getFrom();
			Location to = event.getTo();
			if( (from.getBlockX() != to.getBlockX()) ||
				(from.getBlockY() != to.getBlockY()) ||
				(from.getBlockZ() != to.getBlockZ())) {
				
				event.setTo(from);
				showLoginPrompt(player);
			}
		}
	}
	
	/**
	 * Called when a player tries to drop an item.
	 * We will not allow them to do this,
	 * unless they are logged in.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if(!PlayerList.getInstance().contains(player)) {
			event.setCancelled(true);
			showLoginPrompt(player);
		}
	}
	
	/**
	 * Called when a player tries to send a chat message.
	 * We will not allow them to do this,
	 * unless they are logged in.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if(!PlayerList.getInstance().contains(player)) {
			event.setCancelled(true);
			showLoginPrompt(player);
		}
	}
	
	/**
	 * Called when a player tries to interact with an object.
	 * We won't allow them to do this, until they log in.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(!PlayerList.getInstance().contains(player)) {
			event.setCancelled(true);
			showLoginPrompt(player);
		}
	}
	
	/**
	 * Called when a player logs out.
	 * We will remove them from the login list
	 * once they log out.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST) 
	private void onPlayerLogout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerList.getInstance().remove(player);
	}	
			
	/**
	 * This shows the player a message telling them
	 * that they must register or login to the game.
	 * @param player
	 */
	private void showLoginPrompt(Player player) {
		player.sendMessage(ChatColor.RED + "Please register or login.");
		player.sendMessage(ChatColor.RED + "/register <password>");
		player.sendMessage(ChatColor.RED + "/login <password>");	
	}
	
}
