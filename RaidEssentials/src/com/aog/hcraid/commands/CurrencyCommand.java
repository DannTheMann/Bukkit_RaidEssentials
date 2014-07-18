package com.aog.hcraid.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.aog.hcraid.Raid;
import com.aog.hcraid.save.HCPlayer;

public class CurrencyCommand implements CommandExecutor{

	
	public static final String CURRENCY_PREFIX = ChatColor.GOLD + "["
			+ ChatColor.AQUA + "Bank" + ChatColor.GOLD + "] "
			+ ChatColor.GRAY;
	
	@Override
	public boolean onCommand(CommandSender s, Command c, String arg2,
			String[] args) {
	
		if(c.getName().equalsIgnoreCase("withdraw")){
			
			Player p = (Player)s;
			HCPlayer hp = Raid.UTIL.getPlayer(p);
			
			if(args.length == 0){
				p.sendMessage(CURRENCY_PREFIX + ChatColor.YELLOW + " ---- Bank Withdraw Commands");
				p.sendMessage(ChatColor.GRAY + " - /wd [g:amount s:amount b:amount] - Withdraws however much you specify from" +
				" the bank.");
				p.sendMessage(ChatColor.GRAY + " - /wd all - Withdraw everything in your bank. " + ChatColor.RED + "WARNING: Any excess items will be dropped " +
						" on the floor!");
				p.sendMessage(ChatColor.YELLOW + " --- To deposit money into your bank, use /deposit");
				return true;
			}	
			
			String a1 = "";
			String a2 = "";
			String a3 = "";
			String a4 = "";
			
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
			
			int totalGold = split("(?i)g:", a1, a2, a3);
			int totalSilver = split("(?i)s:", a1, a2, a3);
			int totalBronze = split("(?i)b:", a1, a2, a3);
			int total = Raid.UTIL.getTotalPointsForCurrency(totalGold, totalSilver, totalBronze);
			
			if(hp.getMoneyToWithdraw() == 0){
				p.sendMessage(CURRENCY_PREFIX + ChatColor.RED + "You don't have any money in your bank.");
				return true;
			}
			
			if(a1.equalsIgnoreCase("all")){
				total = hp.getMoneyToWithdraw();
			}
			
			if(total > hp.getMoneyToWithdraw()){
				p.sendMessage(CURRENCY_PREFIX + ChatColor.RED + "You don't have this much money, do /bal for your balance.");
				return true;
			}
			
			p.sendMessage("Total = " + total);
			
			hp.withdrawMoney(total);
			
		}else if(c.getName().equalsIgnoreCase("deposit")){
			
			Player p = (Player)s;
			HCPlayer hp = Raid.UTIL.getPlayer(p);
			
			int invGold = 0;
			int invSilver = 0;
			int invBronze = 0;
			
			for(ItemStack i : p.getInventory()){
				if (i != null) {
					if (i.getType() == Material.GOLD_INGOT) {
						invGold += i.getAmount();
					} else if (i.getType() == Material.IRON_INGOT) {
						invSilver += i.getAmount();
					} else if (i.getType() == Material.CLAY_BRICK) {
						invBronze += i.getAmount();
					}
				}
			}
			
			String a1 = "";
			String a2 = "";
			String a3 = "";
			String a4 = "";
			
			int inventoryTotal = Raid.UTIL.getPlayersBalanceInInventory(p);
			
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
			
			if(a1 == ""){
				p.sendMessage(CURRENCY_PREFIX + ChatColor.YELLOW + " ---- Bank Deposit Commands");
				p.sendMessage(ChatColor.GRAY + " - /d g:amount s:amount b:amount - Deposit an amount of gold, silver or bronze.");
				p.sendMessage(ChatColor.GRAY + " - /d all - Deposit everything you have into your bank.");
				p.sendMessage(ChatColor.YELLOW + " --- To withdraw money into your bank, use /withdraw");
				return true;
			}			

			if(hp.getMoneyToWithdraw() >= 51200){
				p.sendMessage(CURRENCY_PREFIX + ChatColor.RED + "You've reached your bank limit! You can only store up to 200 gold in value in the bank!");
				return true;
			}
			
			int totalGold = split("(?i)g:", a1, a2 ,a3);
			int totalSilver = split("(?i)s:", a1, a2, a3);
			int totalBronze = split("(?i)b:", a1, a2, a3);
			int total = Raid.UTIL.getTotalPointsForCurrency(totalGold, totalSilver, totalBronze);
			
			if(a1.equalsIgnoreCase("all")){
				total = inventoryTotal;
			}
			
			if(total <= 0){
				p.sendMessage(CURRENCY_PREFIX + ChatColor.RED + "Oops! This doesn't seem to work, you must type something like..." +
						" ' /d <all | g:2 s:10 b:4> ' - This would deposit 2 gold, 10 silver and 4 bronze.");
				return true;
			}else if(total > inventoryTotal){
				p.sendMessage(CURRENCY_PREFIX + ChatColor.RED +
						"You don't have that much! You have " + Raid.UTIL.getTranslationForCurrency(inventoryTotal));
				return true;
			}
			
			int returnValue = inventoryTotal - total;
			
			int newBalance = hp.getMoneyToWithdraw() + total;
			
			boolean flag = false;
			
			if(newBalance > 51200){
				returnValue = returnValue + (total - (newBalance - 51200));
				total = total - (newBalance - 51200);
				flag = true;
			}
			
			Raid.UTIL.removePlayersInventoryBalance(p);
			
			hp.addMoneyToWithdraw(total);
			
			Raid.UTIL.payOutPlayer(p, returnValue);
			
			p.sendMessage(ChatColor.YELLOW + "╔═════════════════════════════════╗");
			p.sendMessage(buildString(ChatColor.AQUA + " --- Depositing money into account --- "));
			p.sendMessage(buildString(Raid.UTIL.getTranslationForCurrency(total)));
			if(flag) { p.sendMessage(buildString(ChatColor.RED + "You've maxed out your bank!")); }
			p.sendMessage(ChatColor.YELLOW + "╚═════════════════════════════════╝");
			
			
			
			
			/*
			 * 
			 * String a1 = "";
			String a2 = "";
			String a3 = "";
			String a4 = "";
			
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
			
			if(args.length == 0){
				p.sendMessage(CURRENCY_PREFIX + ChatColor.YELLOW + "/deposit g:amount s:amount b:amount - Deposit money into the bank.");
				p.sendMessage(ChatColor.YELLOW + "/deposit all - Deposit all money in your inventory into the bank.");
				return true;
			}
			
			int totalGold = split("(?i)g:", a1, a2, a3);
			int totalSilver = split("(?i)s:", a1, a2, a3);
			int totalBronze = split("(?i)b:", a1, a2, a3);
			
			int actualGold = 0;
			int actualSilver = 0;
			int actualBronze = 0;
			
			for(ItemStack i : p.getInventory()){
				if (i != null) {
					if (i.getType() == Material.GOLD_INGOT) {
						actualGold += i.getAmount();
					} else if (i.getType() == Material.IRON_INGOT) {
						actualSilver += i.getAmount();
					} else if (i.getType() == Material.CLAY_BRICK) {
						actualBronze += i.getAmount();
					}
				}
			}
			
			if(a1.equalsIgnoreCase("all")){
				totalGold = actualGold;
				totalSilver = actualSilver;
				totalBronze = actualBronze;
			}
			
			p.sendMessage("Actual money in inventory: " + 
			Raid.UTIL.getTranslationForCurrency(Raid.UTIL.getTotalPointsForCurrency
					(actualGold, actualSilver, actualBronze)));
			
			if(actualGold < totalGold
					|| actualSilver < totalSilver
					|| actualBronze < totalBronze){
				
				p.sendMessage(CURRENCY_PREFIX + ChatColor.RED + "You don't have " + Raid.UTIL.getTranslationForCurrency(
						totalBronze + (totalSilver * 16) + (totalGold * 256)) + ".");
				p.sendMessage(ChatColor.YELLOW + " You have - " + Raid.UTIL.getTranslationForCurrency(
						actualBronze + (actualSilver * 16) + (actualGold * 256)) + ".");
				return true;
				
			}			
			
			p.sendMessage("Payout: " + ((actualGold - totalGold * 256) + (actualSilver - totalSilver * 16) + actualBronze - totalBronze));
			p.sendMessage("Money being requested: " + 
			Raid.UTIL.getTranslationForCurrency(Raid.UTIL.getTotalPointsForCurrency
					(totalGold, totalSilver, totalBronze)));
			
			Raid.UTIL.removePlayersInventoryBalance(p);
			
			hp.addMoneyToWithdraw(Raid.UTIL.getTotalPointsForCurrency(totalGold, totalSilver, totalBronze));
			
			p.sendMessage(CURRENCY_PREFIX + ChatColor.GREEN + "Deposited " + Raid.UTIL.getTranslationForCurrency(hp.getMoneyToWithdraw()));
			
			Raid.UTIL.payOutPlayer(p, (actualGold - totalGold * 256) + (actualSilver - totalSilver * 16) + actualBronze - totalBronze);
			p.updateInventory();
			
			 * 
			 */
		}else if(c.getName().equalsIgnoreCase("balance")){
			
			Player p = (Player)s;
			HCPlayer hp = Raid.UTIL.getPlayer(p);
			
			String a1 = "";
			String a2 = "";
			String a3 = "";
			String a4 = "";
			
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
			
			String id = p.getUniqueId().toString();
			
			if(args.length > 0){
				
				String id2 = null;
				
				id2 = Raid.UTIL.getPlayerFromName(a1);
				
				if(id2 == null){
					p.sendMessage(CURRENCY_PREFIX + ChatColor.RED + "Couldn't find a player called '" + 
								a1 + "'.");
					return true;
				}
				
				id = id2;
				
			}
			
			Player lookFor = Raid.UTIL.getPlayer(id);
			HCPlayer hpLookFor = Raid.UTIL.getPlayer(Raid.UTIL.getPlayer(id));
			
			p.sendMessage(ChatColor.YELLOW + "╔═════════════════════════════════╗");
			p.sendMessage(buildString(ChatColor.AQUA + " --- " + lookFor.getName() + " Balance --- "));
			p.sendMessage(buildString( Raid.UTIL.getTranslationForCurrency(hpLookFor.getMoneyToWithdraw())));
			p.sendMessage(ChatColor.YELLOW + "╚═════════════════════════════════╝");
		}
		
		return false;
	}
	
	private String buildString(String string) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(ChatColor.YELLOW + "║ " + string + " " + ChatColor.YELLOW);
		
		return sb.toString();
		
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
