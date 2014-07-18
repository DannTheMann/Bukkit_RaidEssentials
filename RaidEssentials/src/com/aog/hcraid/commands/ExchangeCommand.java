package com.aog.hcraid.commands;

import java.util.ArrayList;

import org.apache.commons.lang.WordUtils;
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
import com.aog.hcraid.save.ExchangeItem;
import com.aog.hcraid.save.GrandExchange;
import com.aog.hcraid.save.HCPlayer;
import com.aog.hcraid.save.WeaponRarity;

public class ExchangeCommand implements CommandExecutor{
	
	public static final String EXCHANGE_PREFIX = ChatColor.GOLD + "["
			+ ChatColor.AQUA + "Global Exchange" + ChatColor.GOLD + "] "
			+ ChatColor.GRAY;
	
	private static final String[] assholes = {"iphonetips1", "obaid_786", "AmunitionX"};
	private static final Material[] unsellable = {Material.GOLD_INGOT, Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.CLAY_BRICK, Material.IRON_INGOT};

	@Override
	public boolean onCommand(CommandSender s, Command c, String l,
			String[] args) {
		
		if(c.getName().equalsIgnoreCase("ge")){
			
			Player p = (Player)s;
			HCPlayer hp = Raid.UTIL.getPlayer(p);
			
			for (String name : assholes) {

				if (p.getName().equalsIgnoreCase(name)) {
					p.sendMessage(EXCHANGE_PREFIX
							+ ChatColor.RED
							+ "Sorry but we can't allow you to use the exchange, "
							+ "you see tax avoidance is a serious crime "
							+ "- for this reason we cannot trust you with money related services, have a nice day "
							+ "- Regards, Marketing Directory Demotah.");
					return true;
				}

			}
			
			String a1 = "";
			String a2 = "";
			String a3 = "";
			String a4 = "";
			
			GrandExchange e = Raid.UTIL.getRaidData().getExchange();
			
			if(args.length > 0){
				a1 = args[0];
				if(args.length > 1){
					a2 = args[1];
					if(args.length > 2){
						a3 = args[2];
						if(args.length > 3){
							a4 = args[3];
						}
					}
						
				}
			}
			
			//Raid.log("a1 == " + a1);
			//Raid.log("a2 == " + a2);
			//Raid.log("a3 == " + a3);
			//Raid.log("a4 == " + a4);
			
			if(a1.equalsIgnoreCase("add") || a1.equalsIgnoreCase("a")){
				
				// add an item to the exchange
				
				ItemStack itemToAdd = p.getItemInHand();
							
				if(itemToAdd == null || itemToAdd.getType() == Material.AIR){
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "Make sure you're holding the item you wish" +
							" to add to the Exchange.");
					return true;
				}
				
				for(Material m : unsellable){
					
					if(m == itemToAdd.getType()){
						p.sendMessage(EXCHANGE_PREFIX
								+ ChatColor.RED
								+ "The item '"
								+ WordUtils.capitalize(m.toString()
										.toLowerCase().replaceAll("_", " "))
								+ "' is not allowed to be sold"
								+ " on the market.");
						return true;
					}
					
				}
				
				if(a2 == ""){
					p.sendMessage(EXCHANGE_PREFIX + "How much do you want to sell this item for? For example '/ge g:1 s:3 b:3'" +
							" would sell this item for 1 Gold, 2 Silver and 3 Bronze! ");
					return true;
				}
				
				int totalGold = split("(?i)g:", a2, a3, a4);
				int totalSilver = split("(?i)s:", a2, a3, a4);
				int totalBronze = split("(?i)b:", a2, a3, a4);
				int total = Raid.UTIL.getTotalPointsForCurrency(totalGold, totalSilver, totalBronze);
				
				Raid.log("Total p = " + total);
				
				if(total <= 0){
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "You entered '" + a2 + "'. This doesn't seem to work, you must type something like..." +
							" '/ge g:4 s:2' - This will sell for 4 gold, and 2 silver.");
					return false;
				}
				
				e.addItemToExchange(p.getItemInHand(), total, p.getUniqueId().toString());
				
				p.sendMessage(EXCHANGE_PREFIX + ChatColor.GREEN + " Success! You've put '" + 
						WordUtils.capitalize(p.getItemInHand().getType().toString()
								.toLowerCase().replaceAll("_", " "))
								+ ":" + p.getItemInHand().getDurability()+
										" x" + p.getItemInHand().getAmount() + "' on the market.");
				p.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.GOLD + " Gold: " + totalGold);
				p.sendMessage(ChatColor.GRAY + " Silver: " + totalSilver);
				p.sendMessage(ChatColor.RED + " Bronze: " + totalBronze);
				
				p.setItemInHand(null);
				
			}else if(a1.equalsIgnoreCase("check") || a1.equalsIgnoreCase("buy") || a1.equalsIgnoreCase("get") 
					|| a1.equalsIgnoreCase("c")){
				
				if(a2 != ""){
					
					WeaponRarity wr = getWeaponRarity(a2, a3);
					Material m = getMaterialFromString(a2, a3);				
					
					ItemStack[] items;
					
					if(m != null && wr != null){
						
						Raid.log("Searching for " + wr + " " + m);
						
						items = e.searchFor(m, wr);
						
					}else if(m != null){
						
						items = e.searchFor(m);
						
					}else if(wr != null){
						
						items = e.searchFor(wr);
						
					}else{
						items = null;
					}
					
					
					if((items == null || items.length == 0) && (wr != null || m != null)){
						p.sendMessage(EXCHANGE_PREFIX + ChatColor.YELLOW + "There is no listing for this specific item on the Exchange.");
						return true;
					}else if(items == null){
						p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "You've got to specify what you're looking for. Here's a " +
								"few examples. ");
						p.sendMessage(ChatColor.GRAY + " > /ge check 276 - Checks for all Diamond Swords.");
						p.sendMessage(ChatColor.GRAY + " > /ge check stone - Checks for all stone blocks.");
						p.sendMessage(ChatColor.GRAY + " > /ge check 276 rare - Checks for all Diamond Swords that are rare.");
						p.sendMessage(ChatColor.GRAY + " > /ge check rare - Checks for all Rare items.");
						p.sendMessage(ChatColor.GRAY + " > /ge check 276 nothing - Checks for all Diamond Swords that are not of any Scale attribute.");
						return true;
					}
					
					Inventory inv = null;
					
					if(m != null && wr != null){
					 inv = Bukkit.createInventory(null, 54, 
							ChatColor.DARK_RED + 
					WordUtils.capitalize(m.toString().toLowerCase().replaceAll("_", " ")) + " - " + wr);
					}else if(m != null){
						 inv = Bukkit.createInventory(null, 54, 
									ChatColor.DARK_RED + 
							WordUtils.capitalize(m.toString().toLowerCase().replaceAll("_", " ")));
					}else if(wr != null){
						 inv = Bukkit.createInventory(null, 54, 
									ChatColor.DARK_RED + 
							WordUtils.capitalize(wr.toString().toLowerCase()));
					}
					 
					laceInventory(inv);
					
					for(ItemStack ei : items){
						
						inv.addItem(ei);
						
					}
					
					hp.setInventoryBalance(Raid.UTIL.getPlayersBalanceInInventory(p));
					Raid.UTIL.removePlayersInventoryBalance(p);
					p.openInventory(inv);
					hp.getManagement().setLookingAtExchangeItems(true);
					hp.setInventory(p.getInventory());
					
					
				}else{
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.YELLOW + "Search for an item you want.");
					p.sendMessage(ChatColor.AQUA + " Here are some examples of how to use this command.");
					p.sendMessage(ChatColor.GRAY + " - /ge check <itemID / item name> [Rarity]");
					p.sendMessage(ChatColor.GRAY + " > /ge check stone - Checks for all stone blocks.");
					p.sendMessage(ChatColor.GRAY + " - /ge check 276 - Searches for all diamond swords.");
					p.sendMessage(ChatColor.GRAY + " - /ge check 276 rare - Searches for all diamond swords " +
							"that are rare.");
				}
				
			}else if(a1.equalsIgnoreCase("list") || a1.equalsIgnoreCase("l")){
				
				// List their items on the exchange
				
				GrandExchange ge = Raid.UTIL.getRaidData().getExchange();
				
				ArrayList<ExchangeItem> is = Raid.UTIL.getPlayer(p).getItemsForSale();
				
				if(is.isEmpty()){
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "You have no items listed on the Grand Exchange.");
					return true;
				}
				
				Inventory inv = Bukkit.createInventory(null, 54, 
						ChatColor.DARK_RED + "What you're selling.");
				
				laceInventory(inv);
				
				for(ExchangeItem ei : is){
					
					inv.addItem(ei.toInformativeItemStack());
					
				}
				hp.getManagement().setLookingAtOwnListedItems(true);
				hp.setInventory(p.getInventory());
				p.openInventory(inv);
				
				
				
			}else if(a1.equalsIgnoreCase("remove") || (a1.equalsIgnoreCase("rem") || a1.equalsIgnoreCase("r"))){
				
				// Display an inventory, let them right-click to remove items from the GE, once they're
				// removed add those items to a list and wait until the inventory gets closed to return items.
				
				GrandExchange ge = Raid.UTIL.getRaidData().getExchange();
				
				ArrayList<ExchangeItem> is = Raid.UTIL.getPlayer(p).getItemsForSale(); 
				
				if(is.isEmpty()){
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "You have no items listed on the Grand Exchange to remove.");
					return true;
				}
				
				Inventory inv = Bukkit.createInventory(null, 54, 
						ChatColor.DARK_RED + "Right-click to remove.");
				
				laceInventory(inv);
				
				for(ExchangeItem ei : is){
					
					inv.addItem(ei.toInformativeItemStack());
					
				}

				hp.getManagement().setLookingAtRemovingItems(true);
				
				hp.setInventory(p.getInventory());
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
			}else if(a1.equalsIgnoreCase("i") || a1.equalsIgnoreCase("info")){
				
				ItemStack itemToCheck = p.getItemInHand();
				
				if(a2 != ""){
					
					Material m = Raid.UTIL.getItemAlias(a2);
					
					if(m == null){
						p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "Couldn't find an item called '" + a2 + "', try using a different name for this item or it's ID.");
						return true;
					}
					
					itemToCheck = new ItemStack(m);
				}
				
				if(itemToCheck == null || itemToCheck.getType() == Material.AIR){
					p.sendMessage(EXCHANGE_PREFIX + ChatColor.RED + "Make sure you're holding the item you wish" +
							" check on the Exchange.");
					return true;
				}
				
				Raid.log("Material m = " + itemToCheck.getType());
				
				ExchangeItem[] ei = e.getInformationOnItem(itemToCheck.getType());
				
				
				
				p.sendMessage(EXCHANGE_PREFIX + " ---- " + WordUtils.capitalize(itemToCheck
						.getType().toString().toLowerCase().replaceAll("_", " ")));
				if(ei.length == 0){
					p.sendMessage(ChatColor.GRAY + " > No " + WordUtils.capitalize(itemToCheck
						.getType().toString().toLowerCase().replaceAll("_", " ")) + " is for sale.");
					return true;
				}
				p.sendMessage(ChatColor.GRAY + " > Cheapest Selling Price: " + ei[0].getTradingTranslation());
				p.sendMessage(ChatColor.GRAY + " > Cheapest Quantity for sale: " + ei[0].getAmount());
				p.sendMessage(ChatColor.GRAY + " > Total being sold: " + ei.length);
				p.sendMessage(ChatColor.GRAY + " > Alias: " + Raid.UTIL.getItemAliases(itemToCheck.getType()));
				
			}else if(c.getName().equalsIgnoreCase("mod")){
				
				// Remove commands
				
				
			}else{
				
				showOptions(p, c.getName());
				
			}
			
		}
		
		return false;
	}
	
	private void showOptions(Player p, String cmd) {
		String r = ChatColor.GRAY + " > /" + cmd + " ";
		p.sendMessage(EXCHANGE_PREFIX + " --- Exchange Command --- ");
		p.sendMessage(r + "a - Add an item to the Exchange");
		p.sendMessage(r + "c - Check items on the Exchange.");
		p.sendMessage(r + "r - Remove an item from the Exchange.");
		p.sendMessage(r + "l - List your items on the Exchange.");
		p.sendMessage(r + "i - Display the lowest selling information about an item.");
	}

	private void laceInventory(Inventory inv) {
		ItemStack is = Raid.UTIL.nameItemStack(Raid.UTIL.loreItemStack(
				new ItemStack(Material.GOLD_INGOT, 1), "Left-Click to go back a page. " +
						"\n Right-Click to go forward a page."), "Page: 1");
		
		inv.setItem(53, is);
	}

	private WeaponRarity getWeaponRarity(String a2, String a3){
		
		for(WeaponRarity wr : WeaponRarity.values()){
			
			if(wr.toString().equalsIgnoreCase(a2)){
				return wr;
			}else if(wr.toString().equalsIgnoreCase(a3)){
				return wr;
			}
			
		}
		
		return null;
		
		
	}
	
	private Material getMaterialFromString(String a2, String a3) {
		
		try{
			return Raid.UTIL.getItemAlias(a2);
		}catch(NumberFormatException e){}
		
		try{
			return Raid.UTIL.getItemAlias(a3);
		}catch(NumberFormatException e){}
		
		return null;
	}

	@SuppressWarnings("unused")
	private int split(String string) {
		
		try{
			return Integer.parseInt("" + string.charAt(string.length()-1));
		}catch(Exception e){
			return 0;
		}
		
	}
	

	private int split(String lookFor, String a2, String a3, String a4) {
		
		try{
			Raid.log("Length = " + a2.split(lookFor).length) ;
			Raid.log("Searching for '" + lookFor + "'");
			Raid.log("Among String: " + a2);
			return Integer.parseInt(a2.split(lookFor)[1]);
		}catch(Exception e){}
		try{
			Raid.log(a3.split(lookFor)[0] + " - " + a3 + " - " + lookFor);
			return Integer.parseInt(a3.split(lookFor)[1]);
		}catch(Exception e){}
		try{
			Raid.log(a4.split(lookFor)[0] + " - " + a4 + " - " + lookFor);
			return Integer.parseInt(a4.split(lookFor)[1]);
		}catch(Exception e){}
		return 0;
	}

	


}
