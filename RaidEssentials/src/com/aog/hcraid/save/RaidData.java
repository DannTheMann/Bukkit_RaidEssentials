package com.aog.hcraid.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.LivingEntity;

import com.aog.hcraid.serial_file.SaveData;


public class RaidData implements Serializable{
	
	private static final long serialVersionUID = 3102517037751751723L;
	
	private HashMap<String, HCPlayer> players;
	private ArrayList<HCBoss> bosses;
	private GrandExchange exchange;
	
	public static final int MAX_BOSSES_ALLOWED = 30;
	
	private transient ArrayList<LivingEntity> enemies = new ArrayList<>();
	
	public RaidData() {
		players = new HashMap<String, HCPlayer>();
		bosses = new ArrayList<HCBoss>();
		exchange = new GrandExchange();
	}
	
	public void reload(){
		enemies = new ArrayList<>();
	}
	
	public GrandExchange getExchange(){
		return exchange;
	}
	
	public void save(){
		SaveData.saveRaidData(this);
	}

	public HashMap<String, HCPlayer> getPlayers() {
		return players;
	}

	public ArrayList<HCBoss> getBosses() {
		return bosses;
	}

	

}
