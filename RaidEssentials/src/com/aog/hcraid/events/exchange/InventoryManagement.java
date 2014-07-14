package com.aog.hcraid.events.exchange;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.aog.hcraid.Raid;
import com.aog.hcraid.commands.ExchangeCommand;
import com.aog.hcraid.save.ExchangeItem;
import com.aog.hcraid.save.GrandExchange;
import com.aog.hcraid.save.HCPlayer;

public class InventoryManagement implements Listener{
	
	
	public void multiplyExplosion(EntityExplodeEvent e) {
		
		e.setYield(0);
		
		final ArrayList<Block> locations = new ArrayList<Block>();
		
		for (Block b : e.blockList()) {

			if(b.getType() != Material.AIR){
				locations.add(b);
			}
		}

		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(Block l : locations)
					l.setType(Material.LAVA);
			}
		}.runTaskLater(Raid.UTIL.getPlugin(), 3);

		
		Bukkit.broadcastMessage("Size of blocks changed: " + e.blockList().size());

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void inspectingOwnItems(InventoryClickEvent e){
		
		HCPlayer hp = Raid.UTIL.getPlayer((Player) e.getWhoClicked());
		
		if(hp != null && hp.getManagement().isLookingAtRemovingItems()){
			e.setCancelled(true);
			
			if(e.getClick() == ClickType.RIGHT){
				
				if(e.getCurrentItem() != null){
					
					ItemStack is = e.getCurrentItem();
					
					if(e.getSlot() == 53){
						return;
					}
					
					int indexPosition = -99;
					
					List<String> lore = is.getItemMeta().getLore();
					
					for(String u : lore){
						
						u = ChatColor.stripColor(u);
						
						if(u.startsWith("Index Value:")){
							
							indexPosition = Integer.parseInt(u.split("Index Value:")[1].replaceAll(" ", ""));
							
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
					
					if(exch == null){
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
				|| hp.getManagement().isLookingAtRemovingItems()
				|| hp.getManagement().isLookingAtExchangeItems())){
			hp.getManagement().setLookingAtOwnListedItems(false);
			hp.getManagement().setLookingAtRemovingItems(false);
			hp.getManagement().setLookingAtExchangeItems(false);
			
			hp.restoreInventory((Player) e.getPlayer());
			
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
					
					if(e.getSlot() == 53){
						return;
					}
					
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
					
					// Get the balance.
					// Remove their existing currency.
					// Add the new amount.
					
					if(Raid.UTIL.getPlayersBalanceInInventory((Player)e.getWhoClicked()) >= ei.getSellingCost()){
					
					int balance = Raid.UTIL.removePlayersInventoryBalance((Player)e.getWhoClicked());		
						
					ItemStack item = ge.purchaseItemFromExchange(ei);
					
					((Player) e.getWhoClicked()).sendMessage(ExchangeCommand.EXCHANGE_PREFIX + ChatColor.GREEN + 
							" Successfully bought " + ei.getItemType() + " x" + ei.getAmount() + " from the Exchange "
									+ ei.getTradingTranslation() + ".");
					
					Raid.log("Balance: " + balance + ", Payout: " + (balance - ei.getSellingCost()));
					
					Raid.UTIL.givePlayerItem((Player)e.getWhoClicked(), item);
					
					Raid.UTIL.payOutPlayer((Player)e.getWhoClicked(), balance - ei.getSellingCost());
					
					e.getInventory().remove(is);
					((Player) e.getWhoClicked()).updateInventory();
					
					}else{
						
						((Player) e.getWhoClicked()).sendMessage(ExchangeCommand.EXCHANGE_PREFIX + 
								ChatColor.RED + "You don't have enough gold, silver or bronze in your inventory to buy this.");
					}
				
				}
				
			}
		}
		
	}
	
	
	

	
}
