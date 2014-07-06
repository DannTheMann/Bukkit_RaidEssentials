package com.aog.hcraid.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.aog.hcraid.Util;

public class GrandExchange implements Serializable{

	private static final long serialVersionUID = -3748001095630286155L;
	
	private HashMap<Material, GrandExchangeItem> itemsForSale = new HashMap<>();

	public ItemStack[] searchFor(Material m){
		
		GrandExchangeItem gei = itemsForSale.get(m);

		if(gei == null){
			return null;
		}
		
		ExchangeItem[] items = gei.getExchangeItems();
		
		ItemStack[] itemstack = new ItemStack[items.length];
		
		for(int i = 0; i < itemstack.length; i++){
			itemstack[i] = Util.createExchangeItem(items[i]);
		}
		
		return itemstack;
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
			itemstack[i] = Util.createExchangeItem(list[i]);
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
			itemstack[i] = Util.createExchangeItem(items[i]);
		}
		
		return itemstack;
	}

	public void addItemToExchange(ItemStack itemInHand,
			int totalPointsForCurrency, String uuid) {
		
		GrandExchangeItem gei = itemsForSale.get(itemInHand.getType());
		
		if(gei == null)
			gei = new GrandExchangeItem();
		
		gei.addItemToExchange(itemInHand, uuid, totalPointsForCurrency, false);
		
	}
}
