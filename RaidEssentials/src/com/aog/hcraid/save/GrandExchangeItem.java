package com.aog.hcraid.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.aog.hcraid.Raid;

public class GrandExchangeItem implements Serializable{

	private static final long serialVersionUID = -2440239323457903581L;

	private HashMap<String, ArrayList<ExchangeItem>> playerTrade = new HashMap<>();
	private static final Material[] unsellable = {Material.GOLD_INGOT, Material.BRICK, Material.IRON_INGOT};
	
	/**
	 * Withdraws an item from the Exchange by ID.
	 * @param uuid The sellers ID
	 * @param itemId The items ID
	 * @return Whether or not the item was removed.
	 */
	public boolean withdrawItem(String uuid, int itemId){
		
		ArrayList<ExchangeItem> items = playerTrade.get(uuid);
		
		if(items == null){
			return false;
		}
		
		ExchangeItem item = getExchangeItemById(itemId, items);
		
		if(item == null){
			return false;
		}
		
		 Raid.UTIL.players.get(uuid).removeItemForSale(item);
		 
		 items.remove(item);
		
		 Raid.UTIL.returnItem(item.toBukkitItemStack(), Bukkit.getPlayer(UUID.fromString(item.getSellerId())));
		
		return true;
		
	}
	
	/**
	 * Gets the all the ExchangeItems.
	 * @return ExchangeItems ArrayList.
	 */
	public Collection<ArrayList<ExchangeItem>> getAllTrades(){
		return playerTrade.values();
	}
	
	/**
	 * Retrieves all Items under this ID, regardless of who is selling them
	 * organises them from cheapest to most expensive.
	 * @return A list of items from cheapest to expensive.
	 */
	public ExchangeItem[] getExchangeItems(){			
		
		ArrayList<ExchangeItem> allItems = new ArrayList<>();
		
		for(ArrayList<ExchangeItem> eil : playerTrade.values()){
			for(ExchangeItem ei : eil){
				allItems.add(ei);
			}
		}
		
		ExchangeItem[] list = new ExchangeItem[allItems.size()];
		
		for(int i = 0; i < allItems.size(); i++){
			list[i] = allItems.get(i);
		}
		
		int pos1 =0;
		int pos2 = pos1+1;
		
		for(int i = 0; i < list.length * list.length; i++){
			
			if(list[pos2].getSellingCost() < list[pos1].getSellingCost()){
				ExchangeItem ei = list[pos1];
				list[pos1] = list[pos2];
				list[pos2] = ei;
			}
			
			pos1++;
			pos2++;
			
			if(list.length <= pos2){
				pos1 = 0;
				pos2 = pos1+1;
			}
			
		}
		
		return list;
		
	}
	
	/**
	 * Get an exchange Item by int ID.
	 * @param itemId The integer ID for which you're searching
	 * @param items The list of items to look through
	 * @return The ExchangeItem for which this player is selling under this ID
	 */
	public ExchangeItem getExchangeItemById(int itemId,
			ArrayList<ExchangeItem> items) {
		
		for(ExchangeItem ei : items){
			if(ei.getSellingId() == itemId){
				return ei;
			}
		}
		
		return null;
	}

	/**
	 * Add an item to the exchange under a players name, the price, whether it's a admin selling item.
	 * @param item The ItemStack being added to the exchange
	 * @param uuid The UUID for the player selling the item
	 * @param askingPrice The Asking Price for this item
	 * @param admin Whether or not this item is an Admin Item, should it be unlimited?
	 * @return A String to alert the player to what happened.
	 */
	public String addItemToExchange(ItemStack item, String uuid, int askingPrice, boolean admin){
		
		ArrayList<ExchangeItem> items = playerTrade.get(uuid);
		
		if(item == null){
			items = new ArrayList<>();
		}
		
		if(cantSell(item.getType())){
			return ChatColor.DARK_RED + "You can't sell " + item.getType().toString().toLowerCase().replaceAll("_", " ");
		}
		
		ExchangeItem exch = new ExchangeItem(item, uuid, admin, askingPrice, getID(items));
		
		items.add(exch);
		
		Raid.UTIL.players.get(uuid).addItemForSale(exch);
		
		return ChatColor.DARK_BLUE + "Added the item " + exch.toString();
	}

	/**
	 * A list of items that are not allowed to be sold.
	 * @param type The Material type
	 * @return
	 */
	private boolean cantSell(Material type) {
		
		for(Material m : unsellable){
			if(m == type){
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Returns the next ID to use when adding a new item to the Exchange.
	 * @param items The current list of items in the Exchange for this player under the item.
	 * @return The next free number to be used an ID for storing this value.
	 */
	private int getID(ArrayList<ExchangeItem> items) {
		
		int response = items.size();
		
		for(int i = 0; i < items.size(); i++){
			
			if(items.get(i).getItemId() != response){
				return i;
			}
			
		}
		
		return response;
	}

	public ExchangeItem getItemFromUUID(String uuid, int indexPosition) {
		
		ArrayList<ExchangeItem> items = playerTrade.get(uuid);
		
		if(items == null){
			return null;
		}
		
		ExchangeItem item = getExchangeItemById(indexPosition, items);
		
		if(item == null){
			return null;
		}
		
		return item;
	}

	public void removeExchangeItem(ExchangeItem ei) {
		
		playerTrade.get(ei.getSellerId()).remove(ei);
		
	}

}
