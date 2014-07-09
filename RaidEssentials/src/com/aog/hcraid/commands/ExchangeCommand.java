package com.aog.hcraid.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.aog.hcraid.Raid;
import com.aog.hcraid.Util;
import com.aog.hcraid.save.ExchangeItem;
import com.aog.hcraid.save.GrandExchange;
import com.aog.hcraid.save.HCPlayer;
import com.aog.hcraid.save.WeaponRarity;

public class ExchangeCommand implements CommandExecutor{
	
	public static final String EXCHANGE_PREFIX = ChatColor.GOLD + "["
			+ ChatColor.AQUA + "Global Exchange" + ChatColor.GOLD + "] "
			+ ChatColor.GRAY;

	@Override
	public boolean onCommand(CommandSender s, Command c, String l,
			String[] args) {
		
		if(c.getName().equalsIgnoreCase("exchange")){
			
			Player p = (Player)s;
			
			String a1 = "";
			String a2 = "";
			String a3 = "";
			
			GrandExchange e = Raid.UTIL.getRaidData().getExchange();
			
			if(args.length > 0){
				a1 = args[0];
				if(args.length > 1){
					a2 = args[1];
					if(args.length > 2){
						a3 = args[2];
					}
				}
			}
			
			if(a1.equalsIgnoreCase("add")){
				
				// add an item to the exchange
				
				ItemStack itemToAdd = p.getItemInHand();
				
				if(a2 == ""){
					p.sendMessage(EXCHANGE_PREFIX + "How much do you want to sell this item for? For example '/ge 1g2s3b'" +
							" would sell this item for 1 Gold, 2 Silver and 3 Bronze! ");
					return true;
				}
				
				if(itemToAdd == null){
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "Make sure you're holding the item you wish" +
							"to add to the Exchange, you're currently holding nothing :[ .");
					return true;
				}
				
				int totalGold = split(a2.split("(i?)g")[0]);
				int totalSilver = split(a2.split("(i?)s")[0]);
				int totalBronze = split(a2.split("(i?)b")[0]);

				if(Raid.UTIL.getTotalPointsForCurrency(totalGold, totalSilver, totalBronze) <= 0){
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "You entered '" + a2 + "'. This doesn't seem to work, you must type something like..." +
							" '/ge 4g2s' - This will sell for 4 gold, and 2 silver.");
					return false;
				}
				
				e.addItemToExchange(p.getItemInHand(), Raid.UTIL.getTotalPointsForCurrency(totalGold, totalSilver, totalBronze), p.getUniqueId().toString());
				
			}else if(a1.equalsIgnoreCase("check") || a1.equalsIgnoreCase("buy") || a1.equalsIgnoreCase("get")){
				
				if(a2 != ""){
					
					WeaponRarity wr = getWeaponRarity(a2, a3);
					Material m = getMaterialFromString(a2, a3);
					ItemStack[] items;
					
					if(m != null && wr != null){
						
						items = e.searchFor(m, wr);
						
					}else if(m != null){
						
						items = e.searchFor(m);
						
					}else if(wr != null){
						
						items = e.searchFor(wr);
						
					}else{
						items = null;
					}
					
					if(items == null){
						p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "You've got to specify what you're looking for. Here's a " +
								"few examples. ");
						p.sendMessage(ChatColor.GRAY + " > /ge check 276 - Checks for all Diamond Swords");
						p.sendMessage(ChatColor.GRAY + " > /ge check 276 rare - Checks for all Diamond Swords that are rare.");
						p.sendMessage(ChatColor.GRAY + " > /ge check rare - Checks for all Rare items.");
						p.sendMessage(ChatColor.GRAY + " > /ge check 276 nothing - Checks for all Diamond Swords that are not of any Scale attribute.");
						return true;
					}
					
				}else{
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.YELLOW + "Search for an item you want.");
					p.sendMessage(ChatColor.GRAY + " - /ge check <itemID> [Rarity]");
					p.sendMessage(ChatColor.GRAY + " - /ge check 276 - Searches for all diamond swords.");
					p.sendMessage(ChatColor.GRAY + " - /ge check 276 rare - Searches for all diamond swords " +
							"that are rare.");
				}
				
			}else if(a1.equalsIgnoreCase("list")){
				
				// List their items on the exchange
				
				GrandExchange ge = Raid.UTIL.getRaidData().getExchange();
				
				ArrayList<ExchangeItem> is = ge.getPlayerItems(p);
				
				if(is.isEmpty()){
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "You have no items listed on the Grand Exchange.");
					return true;
				}
				
				Inventory inv = Bukkit.createInventory(null, 53, 
						ChatColor.DARK_RED + "Your Items on the GE, [Total " + is.size() + "]");
				
				laceInventory(inv);
				
				for(ExchangeItem ei : is){
					
					inv.addItem(ei.toInformativeItemStack());
					
				}
				
				HCPlayer hp = Raid.UTIL.getPlayer(p);
				hp.getManagement().setLookingAtOwnListedItems(true);
				p.openInventory(inv);
				
				
				
			}else if(a1.equalsIgnoreCase("remove") || (a1.equalsIgnoreCase("rem"))){
				
				// Display an inventory, let them right-click to remove items from the GE, once they're
				// removed add those items to a list and wait until the inventory gets closed to return items.
				
				GrandExchange ge = Raid.UTIL.getRaidData().getExchange();
				
				ArrayList<ExchangeItem> is = ge.getPlayerItems(p);
				
				if(is.isEmpty()){
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "You have no items listed on the Grand Exchange to remove.");
					return true;
				}
				
				Inventory inv = Bukkit.createInventory(null, 53, 
						ChatColor.DARK_RED + "Your Items on the GE, [Total " + is.size() + "]");
				
				laceInventory(inv);
				
				for(ExchangeItem ei : is){
					
					inv.addItem(ei.toInformativeItemStack());
					
				}
				
				HCPlayer hp = Raid.UTIL.getPlayer(p);
				hp.getManagement().setLookingAtRemovingItems(true);
				p.openInventory(inv);
				
				/*
				 
				if(a2 != ""){
				
					int id = 0;
					int indexValue = -99;
					
					try{
						id = Integer.parseInt(a2);
					}catch(NumberFormatException nfe){
						p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "You must specify the Item Id to remove.");
						p.sendMessage(ChatColor.GRAY + " - /ge rem <itemId> - Removes all items of this ID.");
						p.sendMessage(ChatColor.GRAY + " - /ge rem <itemId> <index value> - Removes the " +
								"item of this ID under this index value.");
						return true;
					}
					
					if(a3 != ""){
						try{
							indexValue = Integer.parseInt(a3);
						}catch(NumberFormatException nfe){
							p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "You must specify the Index Value to remove this item.");
							p.sendMessage(ChatColor.GRAY + " - /ge rem <itemId> - Removes all items of this ID.");
							p.sendMessage(ChatColor.GRAY + " - /ge rem <itemId> <index value> - Removes the " +
									"item of this ID under this index value.");
							p.sendMessage(ChatColor.GRAY + " To see Index Values, do /ge list");
							return true;
						}
					}
					
					GrandExchange ge = Raid.UTIL.getRaidData().getExchange();
				
					ArrayList<ExchangeItem> is = ge.getPlayerItems(p);
					
					for(ExchangeItem ei : is){
						
						
						
					}
				
				}else{
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.YELLOW + "Remove an item listed on the Grand Exchange.");
					p.sendMessage(ChatColor.GRAY + " - /ge rem <itemId> - Removes all items of this ID.");
					p.sendMessage(ChatColor.GRAY + " - /ge rem <itemId> <index value> - Removes the " +
							"item of this ID under this index value.");
				}
				
				
				 */
			}
			
		}
		
		return false;
	}

	private void laceInventory(Inventory inv) {
		ItemStack is = Raid.UTIL.nameItemStack(Raid.UTIL.loreItemStack(
				new ItemStack(Material.GOLD_INGOT, 1), "Left-Click to go back a page. " +
						"\n Right-Click to go forward a page."), "Page: 1");
		
		inv.setItem(53, is);
	}

	private WeaponRarity getWeaponRarity(String a2, String a3){
		
		
		WeaponRarity wr = WeaponRarity.valueOf(a2);
		
		if(wr == null){
			return WeaponRarity.valueOf(a3);
		}
		
		return wr;
		
		
	}
	
	private Material getMaterialFromString(String a2, String a3) {
		
		try{
			return Material.getMaterial(Integer.parseInt(a2));
		}catch(NumberFormatException e){}
		
		try{
			return Material.getMaterial(Integer.parseInt(a3));
		}catch(NumberFormatException e){}
		
		return null;
	}

	private int split(String string) {
		
		try{
			return Integer.parseInt("" + string.charAt(string.length()-1));
		}catch(NumberFormatException e){
			return 0;
		}
		
	}

	


}
