package com.aog.hcraid.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aog.hcraid.Raid;
import com.aog.hcraid.save.GrandExchange;
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
			
			int totalGold = split("(?i)g:", a2, a3, a4);
			int totalSilver = split("(?i)s:", a2, a3, a4);
			int totalBronze = split("(?i)b:", a2, a3, a4);
			int total = Raid.UTIL.getTotalPointsForCurrency(totalGold, totalSilver, totalBronze);
			
			if(a1.equalsIgnoreCase("all")){
				total = hp.getMoneyToWithdraw();
			}
			
			hp.withdrawMoney(total);
			
		}else if(c.getName().equalsIgnoreCase("deposit")){
			
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
			
			int totalGold = split("(?i)g:", a2, a3, a4);
			int totalSilver = split("(?i)s:", a2, a3, a4);
			int totalBronze = split("(?i)b:", a2, a3, a4);
			int total = Raid.UTIL.getTotalPointsForCurrency(totalGold, totalSilver, totalBronze);
			
		}
		
		return false;
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
