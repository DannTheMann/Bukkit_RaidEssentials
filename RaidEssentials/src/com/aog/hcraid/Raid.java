package com.aog.hcraid;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.aog.hcraid.save.GrandExchange;

public class Raid extends JavaPlugin{

	public static final Util UTIL = new Util();
	
	public void onEnable(){
		UTIL.load(this);
	}
	
	public void onDisable(){
		UTIL.save();
		
		for(Player p : Bukkit.getOnlinePlayers()){
			p.closeInventory();
		}
		
	}

	public static void log(String string) {
		System.out.println("[Raid Essentials] " + string);
	}
	
	

}
