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
		
		if(hp != null && hp.getManagement().isLookingAtOwnListedItems()){
			e.setCancelled(true);
			
			if(e.getClick() == ClickType.RIGHT){
				
				if(e.getCurrentItem() != null){
					
					ItemStack is = e.getCurrentItem();
					
					int indexPosition = -99;
					
					List<String> lore = is.getItemMeta().getLore();
					
					for(String u : lore){
						if(u.startsWith("Index Value:")){
							
							indexPosition = Integer.parseInt(u.split("Index Value:")[1]);
							
						}
					}
					
					ArrayList<ExchangeItem> eil = hp.getItemsForSale();
					
					ExchangeItem exch = null;
					
					for(ExchangeItem ei : eil){
						
						if(ei.getIndexValue() == indexPosition){
							exch = ei;
							break;
						}
						
					}
					
					GrandExchange ge = Raid.UTIL.getRaidData().getExchange();
					
					if(ge.removeItemFromExchange(exch)){
						((Player) e.getWhoClicked()).sendMessage(ExchangeCommand.EXCHANGE_PREFIX + ChatColor.GREEN + 
								" Successfully removed " + exch.getItemType() + " x" + exch.getAmount() + " from the Exchange.");
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
		
		if(hp != null && hp.getManagement().isLookingAtOwnListedItems()){
			hp.getManagement().setLookingAtOwnListedItems(false);
		}
		
	}
	
	@EventHandler
	public void purchaseItemFromExchange(InventoryClickEvent e){
		
		HCPlayer hp = Raid.UTIL.getPlayer((Player) e.getWhoClicked());
		
		if(hp != null && hp.getManagement().isLookingAtOwnListedItems()){
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void purchasingItems(InventoryClickEvent e){
		
		HCPlayer hp = Raid.UTIL.getPlayer((Player) e.getWhoClicked());
		
		if(hp != null && hp.getManagement().isLookingAtExchangeItems()){
			e.setCancelled(true);
			
			if(e.getClick() == ClickType.RIGHT){
				
				if(e.getCurrentItem() != null){
					
					ItemStack is = e.getCurrentItem();
					
					int indexPosition = -99;
					String uuid = null;
					
					List<String> lore = is.getItemMeta().getLore();
					
					for(String u : lore){
						if(u.startsWith("Index Value:")){
							
							indexPosition = Integer.parseInt(u.split("Index Value:")[1]);
							
						}else if(u.startsWith("Seller ID: ")){
							
							uuid = u.split("Seller ID: ")[1];
							
						}
					}
					
					GrandExchange ge = Raid.UTIL.getRaidData().getExchange();
					
					ExchangeItem ei = ge.getExchangeItem(uuid, indexPosition, is.getType());
					
					// Charge them
					
					if(ge.removeItemFromExchange(ei)){
						((Player) e.getWhoClicked()).sendMessage(ExchangeCommand.EXCHANGE_PREFIX + ChatColor.GREEN + 
								" Successfully purchased " + ei.getItemType() + " x" + ei.getAmount() + " from the Exchange "
										+ ei.getTradingTranslation() + ".");
					}
					
					e.getInventory().remove(is);
					((Player) e.getWhoClicked()).updateInventory();
				
				}
				
			}
		}
		
	}
	
	
	

	
}
