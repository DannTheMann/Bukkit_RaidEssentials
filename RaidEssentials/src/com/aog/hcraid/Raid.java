package com.aog.hcraid;

import org.bukkit.plugin.java.JavaPlugin;

import com.aog.hcraid.save.GrandExchange;

public class Raid extends JavaPlugin{

	public static final Util UTIL = new Util();
	public static GrandExchange ge;
	
	public void onEnable(){
		UTIL.load(this);
	}
	
	public void onDisable(){
		UTIL.save();
	}

	public static void log(String string) {
	
		System.out.println("[Raid Essentials] " + string);
	}
	
	

}
