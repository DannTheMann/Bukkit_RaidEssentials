package com.aog.hcraid.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.aog.hcraid.Raid;

public class GrandExchange implements Serializable{

	private static final long serialVersionUID = -3748001095630286155L;
	
	private HashMap<Material, GrandExchangeItem> itemsForSale = new HashMap<>();

	public GrandExchange(){
		
		for(Material m : Material.values()){
			if(!itemsForSale.containsKey(m)){
				itemsForSale.put(m, new GrandExchangeItem());
			}
		}
		
	}
	
	public ItemStack[] searchFor(Material m){
		
		GrandExchangeItem gei = itemsForSale.get(m);

		if(gei == null){
			return null;
		}
		
		ExchangeItem[] items = gei.getExchangeItems();
		
		ItemStack[] itemstack = new ItemStack[items.length];
		
		for(int i = 0; i < itemstack.length; i++){
			itemstack[i] = items[i].toInformativeItemStack();
		}
		
		return itemstack;
	}
	
	public ExchangeItem[] getInformationOnItem(Material m) {

		GrandExchangeItem gei = itemsForSale.get(m);

		if(gei == null){
			return null;
		}
		
		ExchangeItem[] items = gei.getExchangeItems();
		
		return items;
		
	}
	
	public ItemStack[] searchFor(WeaponRarity wr){
		
		ArrayList<ExchangeItem> itemOfRarity = new ArrayList<>();		

		if(itemsForSale == null){
			return null;
		}
		
		for(GrandExchangeItem gei : itemsForSale.values()){
			
			for(ArrayList<ExchangeItem> exchangeList : gei.getAllTrades()){
				
				for(ExchangeItem ei : exchangeList){
					if(ei.getRarity() == wr){
						itemOfRarity.add(ei);
					}
				}
				
			}
			
		}
		
		ExchangeItem[] list = new ExchangeItem[itemOfRarity.size()];
		
		for(int i = 0; i < itemOfRarity.size(); i++){
			list[i] = itemOfRarity.get(i);
		}
		
		int pos1 =0;
		int pos2 = pos1+1;
		
		
		
		for(int i = 0; i < list.length * list.length; i++){
			
			if(list.length <= pos2){
				// Checks to see if the list has only one item.
				break;
			}
			
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
		
		ItemStack[] itemstack = new ItemStack[list.length];
		
		for(int i = 0; i < itemstack.length; i++){
			itemstack[i] =  list[i].toInformativeItemStack();
		}
		
		return itemstack;
	}
	
	public ItemStack[] searchFor(Material m, WeaponRarity wr){
		
		GrandExchangeItem gei = itemsForSale.get(m);

		if(gei == null){
			return null;
		}
		
		ArrayList<ExchangeItem> itemOfRarity = new ArrayList<>();		
		
		ExchangeItem[] items = gei.getExchangeItems();

			for(ExchangeItem ei : items){
				
				if(ei.getRarity() == wr){
					itemOfRarity.add(ei);
				}
			}
		
		ExchangeItem[] list = new ExchangeItem[itemOfRarity.size()];
		
		for(int i = 0; i < itemOfRarity.size(); i++){
			list[i] = itemOfRarity.get(i);
		}
		
		ItemStack[] itemstack = new ItemStack[items.length];
		
		for(int i = 0; i < itemstack.length; i++){
			itemstack[i] =  items[i].toInformativeItemStack();
		}
		
		return itemstack;
	}

	public void addItemToExchange(ItemStack itemInHand,
			int totalPointsForCurrency, String uuid) {
		
		GrandExchangeItem gei = itemsForSale.get(itemInHand.getType());
		
		if(gei == null)
			gei = new GrandExchangeItem();
		
		gei.addItemToExchange(itemInHand, uuid, totalPointsForCurrency, false);
		
		itemsForSale.put(itemInHand.getType(), gei);
		
	}
	
	public boolean removeItemFromExchange(ExchangeItem ei){
		
		GrandExchangeItem gei =  itemsForSale.get(ei.getItemType());
		
		if(gei != null){
		
			return gei.withdrawItem(ei.getSellerId(), ei.getIndexValue());
		
		}
		
		return false;
	}
	
	public ItemStack purchaseItemFromExchange(ExchangeItem ei){
		
		GrandExchangeItem gei =  itemsForSale.get(ei.getItemType());
		
		gei.removeExchangeItem(ei);
		
		ei.setSold();
		
		HCPlayer hp = Raid.UTIL.getPlayer(Raid.UTIL.getPlayer(ei.getSellerId()));
		
		hp.addMoneyToWithdraw(ei.getSellingCost());
		
		return ei.toBukkitItemStack();
		
	}

	public ArrayList<ExchangeItem> getPlayerItems(Player p) {
		
		ArrayList<ExchangeItem> is = new ArrayList<ExchangeItem>();
		
		for(GrandExchangeItem gei : itemsForSale.values()){
			
			Collection<ArrayList<ExchangeItem>> i = gei.getAllTrades();
			
			for(ArrayList<ExchangeItem> ar : i){
				
				for(ExchangeItem ei : ar){
					
					if(ei.getSellerId().equalsIgnoreCase(Raid.UTIL.getUUID(p))){
						is.add(ei);
					}
					
				}
				
			}
			
		}
		return is;
		
	}

	public ExchangeItem getExchangeItem(String uuid, int indexPosition, Material material) {
		
		GrandExchangeItem gei = itemsForSale.get(material);
		
		return gei.getItemFromUUID(uuid, indexPosition);
		
	}
	
	public HashMap<Material, GrandExchangeItem> getItems(){
		return itemsForSale;
	}

}
