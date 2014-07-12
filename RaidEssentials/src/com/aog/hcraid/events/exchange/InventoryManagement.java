package com.aog.hcraid.events.exchange;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import com.aog.hcraid.Raid;
import com.aog.hcraid.commands.ExchangeCommand;
import com.aog.hcraid.save.ExchangeItem;
import com.aog.hcraid.save.GrandExchange;
import com.aog.hcraid.save.HCPlayer;

public class InventoryManagement implements Listener{
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void inspectingOwnItems(InventoryClickEvent e){
		
		HCPlayer hp = Raid.UTIL.getPlayer((Player) e.getWhoClicked());
		
		if(hp != null && hp.getManagement().isLookingAtRemovingItems()){
			e.setCancelled(true);
			
			if(e.getClick() == ClickType.RIGHT){
				
				if(e.getCurrentItem() != null){
					
					Raid.log("Ready to remove.");
					
					ItemStack is = e.getCurrentItem();
					
					int indexPosition = -99;
					
					List<String> lore = is.getItemMeta().getLore();
					
					for(String u : lore){
						
						u = ChatColor.stripColor(u);
						
						if(u.startsWith("Index Value:")){
							
							indexPosition = Integer.parseInt(u.split("Index Value:")[1].replaceAll(" ", ""));
							
						}
					}
					
					Raid.log("Index pos = " + indexPosition);
					
					ArrayList<ExchangeItem> eil = hp.getItemsForSale();
					
					ExchangeItem exch = null;
					
					for(ExchangeItem ei : eil){
						
						if(ei.getIndexValue() == indexPosition){
							exch = ei;
							break;
						}
						
					}
					
					if(exch == null){
						Raid.log("Couldn't find item to Index Value: " + indexPosition);
						return;
					}
					
					GrandExchange ge = Raid.UTIL.getRaidData().getExchange();
					
					if(ge.removeItemFromExchange(exch)){
						((Player) e.getWhoClicked()).sendMessage(ExchangeCommand.EXCHANGE_PREFIX + ChatColor.GREEN + 
								" Successfully removed " + exch.getItemType() + ":" + exch.getDurability() + " x" + exch.getAmount() + " from the Exchange.");
					}
					
					e.getInventory().remove(is);
					((Player) e.getWhoClicked()).updateInventory();
					
				}
				
			}
			
		}
		
	}
	
	@EventHandler
	public void closingInspectionOfOwnItems(InventoryCloseEvent e){
		
		HCPlayer hp = Raid.UTIL.getPlayer((Player) e.getPlayer());
		
		if(hp != null && (hp.getManagement().isLookingAtOwnListedItems()
				|| hp.getManagement().isLookingAtRemovingItems())){
			hp.getManagement().setLookingAtOwnListedItems(false);
			hp.getManagement().setLookingAtRemovingItems(false);
		}
		
	}
	
	@EventHandler
	public void purchaseItemFromExchange(InventoryClickEvent e){
		
		HCPlayer hp = Raid.UTIL.getPlayer((Player) e.getWhoClicked());
		
		if(hp != null && hp.getManagement().isLookingAtOwnListedItems()){
			e.setCancelled(true);
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void purchasingItems(InventoryClickEvent e){
		
		HCPlayer hp = Raid.UTIL.getPlayer((Player) e.getWhoClicked());
		
		if(hp != null && hp.getManagement().isLookingAtExchangeItems()){
			e.setCancelled(true);
			
			if(e.getClick() == ClickType.RIGHT){
				
				if(e.getCurrentItem() != null){
					
					ItemStack is = e.getCurrentItem();
					
					Raid.log("Looking to buy...");
					
					int indexPosition = -99;
					String uuid = null;
					
					List<String> lore = is.getItemMeta().getLore();
					
					for(String u : lore){
						
						u = ChatColor.stripColor(u);
						
						if(u.startsWith("Index Value:")){
							
							indexPosition = Integer.parseInt(u.split("Index Value:")[1].replaceAll(" ", ""));
							
						}else if(u.startsWith("Seller ID: ")){
							
							uuid = u.split("Seller ID: ")[1].replaceAll(" ", "");
							
						}
					}
					
					GrandExchange ge = Raid.UTIL.getRaidData().getExchange();
					
					ExchangeItem ei = ge.getExchangeItem(uuid, indexPosition, is.getType());
					
					if(ei == null || ei.isSold()){
						((Player) e.getWhoClicked()).sendMessage(ExchangeCommand.EXCHANGE_PREFIX + ChatColor.RED
								+ "Oops! Looks like this item has been sold or removed, sorry about that :(");
						e.getInventory().remove(is);
						((Player) e.getWhoClicked()).updateInventory();
						return;
					}
					
					// Charge them
					
					ItemStack item = ge.purchaseItemFromExchange(ei);
					
					((Player) e.getWhoClicked()).sendMessage(ExchangeCommand.EXCHANGE_PREFIX + ChatColor.GREEN + 
							" Successfully purchased " + ei.getItemType() + " x" + ei.getAmount() + " from the Exchange "
									+ ei.getTradingTranslation() + ".");
					
					e.getWhoClicked().getInventory().addItem(item);
					
					e.getInventory().remove(is);
					((Player) e.getWhoClicked()).updateInventory();
				
				}
				
			}
		}
		
	}
	
	
	

	
}
