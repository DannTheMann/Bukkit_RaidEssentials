package com.aog.hcraid.save;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.aog.hcraid.Raid;

public class HCPlayer implements Serializable {

	private static final long serialVersionUID = 1542553183065771755L;
	private String uniqueId;
	private ExchangeManagement management;
	//private boolean lookingAtOwnListedItems;
	//private boolean lookingToRemoveItems;
	//private boolean lookingAtExchangeItems;
	private GruntRank gruntPlus;
	private ArrayList<ExchangeItem> itemsBeingSold = new ArrayList<>();
	private ArrayList<SavedItem> itemsToReturn = new ArrayList<SavedItem>();

	public HCPlayer(Player p){
		Raid.UTIL.getUUID(p);
		gruntPlus = new GruntRank(p.getFirstPlayed());
		management = new ExchangeManagement();
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
		itemsToReturn.add(new SavedItem(bukkitItemStack));
	}
	
	public void returnItems(){
		
		for(SavedItem i : itemsToReturn){
			Raid.UTIL.returnItem(i.toBukkitItemStack(), Raid.UTIL.getPlayer(uniqueId));
		}
		
		itemsToReturn.clear();
		
	}

	public ExchangeManagement getManagement() {
		return management;
	}

	public void setManagement(ExchangeManagement management) {
		this.management = management;
	}

	/*
	public boolean isLookingAtOwnListedItems() {
		return lookingAtOwnListedItems;
	}

	public void setLookingAtOwnListedItems(boolean lookingAtOwnListedItems) {
		this.lookingAtOwnListedItems = lookingAtOwnListedItems;
	}

	public boolean isLookingAtExchangeItems() {
		return lookingAtExchangeItems;
	}

	public void setLookingAtExchangeItems(boolean lookingAtExchangeItems) {
		this.lookingAtExchangeItems = lookingAtExchangeItems;
	}

	public boolean isLookingToRemoveItems() {
		return lookingToRemoveItems;
	}

	public void setLookingToRemoveItems(boolean lookingToRemoveItems) {
		this.lookingToRemoveItems = lookingToRemoveItems;
	}
	*/
	
	
}
