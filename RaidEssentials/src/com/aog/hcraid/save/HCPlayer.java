package com.aog.hcraid.save;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.aog.hcraid.Raid;

public class HCPlayer implements Serializable {

	private String uniqueId;
	private GruntRank gruntPlus;
	private ArrayList<ExchangeItem> itemsBeingSold = new ArrayList<>();
	private transient ArrayList<ItemStack> volatileItemsToReturn = new ArrayList<ItemStack>();
	private ArrayList<SavedItem> nonVolatileItemsToReturn = new ArrayList<SavedItem>();

	public HCPlayer(Player p){
		Raid.UTIL.getUUID(p);
		gruntPlus = new GruntRank(p.getFirstPlayed());
		
	}
	
	public String getUniqueId(){
		return uniqueId;
	}
	
	public GruntRank getGrunt(){
		return gruntPlus;
	}
	
	public void addItemForSale(ExchangeItem ei){
		itemsBeingSold.add(ei);
	}
	
	public void removeItemForSale(ExchangeItem ei){
		itemsBeingSold.remove(ei);
	}
	
	public ArrayList<ExchangeItem> getItemsForSale(){
		return itemsBeingSold;
	}

	public void addItemToReturn(ItemStack bukkitItemStack) {
		
		
		
	}
	
	
}
