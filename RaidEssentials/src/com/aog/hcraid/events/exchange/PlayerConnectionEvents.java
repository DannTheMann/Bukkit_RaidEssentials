package com.aog.hcraid.events.exchange;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.aog.hcraid.Raid;
import com.aog.hcraid.serial_file.SaveData;

public class PlayerConnectionEvents implements Listener{
	
	@EventHandler
	public void login(PlayerJoinEvent e){
		
		Raid.log(e.getPlayer().getName() + " joined the server.");
		
		Raid.UTIL.addPlayer(e.getPlayer());
		
	}
	
	@EventHandler
	public void logOff(PlayerQuitEvent e){
		
		save(e.getPlayer());
		
	}
	
	private void save(Player p) {
		
		SaveData.savePlayerFile(Raid.UTIL.getRaidData().getPlayers().get(p.getUniqueId().toString()));
		
	}

	@EventHandler
	public void kickOff(PlayerKickEvent e){
		save(e.getPlayer());
	}

}
