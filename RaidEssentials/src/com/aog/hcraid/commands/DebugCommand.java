package com.aog.hcraid.commands;

import java.util.HashMap;

import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.PathEntity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.aog.hcraid.Raid;
import com.aog.hcraid.save.GrandExchange;
import com.aog.hcraid.save.GrandExchangeItem;
import com.aog.hcraid.save.WeaponGeneratorAPI;
import com.aog.hcraid.save.WeaponRarity;

public class DebugCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender s, Command c, String l,
			String[] args) {
		
			if(c.getName().equalsIgnoreCase("rdebug")){
				
				if(!s.hasPermission("HcRaid.ADMIN")){
					s.sendMessage(ChatColor.RED + "This command is for Administrative use only.");
					return true;
				}
				
				Player p = (Player)s;
				
				if(args.length == 0){
					
					p.sendMessage( " General Debug Commands for Raid Essentials. " );
					p.sendMessage( " - /rdebug weapon - Generate random weapon. ");
					p.sendMessage(" - /rdebug exchange - Exchange debug info.");
					p.sendMessage( " - /redebug nullcheck - checks null data.");
					
				}else if(args[0].equalsIgnoreCase("weapon")){
				
					if(args.length == 1){
						
						p.sendMessage(" /rdebug weapon <l|h|r|u|c|random> - Drop a generated weapon of said standard.");
						
					}else if(args[1].equalsIgnoreCase("legendary") || args[1].equalsIgnoreCase("l")){
						
						p.getInventory().addItem(WeaponGeneratorAPI.generateRandomWeapon(WeaponRarity.LEGENDARY));
						
					}else if(args[1].equalsIgnoreCase("heroic") || args[1].equalsIgnoreCase("h")){
						
						p.getInventory().addItem(WeaponGeneratorAPI.generateRandomWeapon(WeaponRarity.HEROIC));
						
					}else if(args[1].equalsIgnoreCase("rare") || args[1].equalsIgnoreCase("r")){
						
						p.getInventory().addItem(WeaponGeneratorAPI.generateRandomWeapon(WeaponRarity.RARE));
						
					}else if(args[1].equalsIgnoreCase("uncommon") || args[1].equalsIgnoreCase("u")){
						
						p.getInventory().addItem(WeaponGeneratorAPI.generateRandomWeapon(WeaponRarity.UNCOMMON));
						
					}else if(args[1].equalsIgnoreCase("common") || args[1].equalsIgnoreCase("c")){
						
						p.getInventory().addItem(WeaponGeneratorAPI.generateRandomWeapon(WeaponRarity.COMMON));
						
					}else if(args[1].equalsIgnoreCase("random") || args[1].equalsIgnoreCase("ra")){
						
						p.getInventory().addItem(WeaponGeneratorAPI.generateRandomWeapon());
						
					}else{
						p.sendMessage(" /rdebug weapon <l|h|r|u|c|random> - Drop a generated weapon of said standard.");
					}
					
				}else if(args[0].equalsIgnoreCase("exchange")){
								
					int amount = 10;
					
					if(args.length >= 3){
						amount = Integer.parseInt(args[2]);
					}
					
					if(args.length == 1){
						
						p.sendMessage("/rdebug exchange list - List all items in exchange.");
						p.sendMessage("/rdebug exchange gold - Gives 999 gold.");
						p.sendMessage("/rdebug exchange silver - Gives 999 silver.");
						p.sendMessage("/rdebug exchange bronze - Gives 999 bronze.");						
						
					}else if(args[1].equalsIgnoreCase("list")){
					
						GrandExchange ge = Raid.UTIL.getRaidData().getExchange();
					
						HashMap<Material, GrandExchangeItem> i = ge.getItems();
						
						for(Material m : i.keySet()){
							p.sendMessage("Material ID: " + m);
							p.sendMessage("Total amount of different client selling items. - " + i.get(m).getAllTrades().size());
							
						}
						
						
						p.sendMessage("'HashMap<Material, GrandExchangeItem> itemsForSale' size - " + i.keySet().size());
						
					}else if(args[1].equalsIgnoreCase("gold")){
						
						p.getInventory().addItem(Raid.UTIL.getGold(amount));
						
					}else if(args[1].equalsIgnoreCase("silver")){
						
						p.getInventory().addItem(Raid.UTIL.getSilver(amount));
						
					}else if(args[1].equalsIgnoreCase("bronze")){
						
						p.getInventory().addItem(Raid.UTIL.getBronze(amount));
						
					}
					
				}else if(args[0].equalsIgnoreCase("nullcheck")){
					
					p.sendMessage("RaidData: " + isNull(Raid.UTIL.getRaidData()));
					p.sendMessage("Exchange: " + isNull(Raid.UTIL.getRaidData().getExchange()));
					p.sendMessage("Bosses: " + isNull(Raid.UTIL.getRaidData().getBosses()));
					p.sendMessage("Players: " + isNull(Raid.UTIL.getRaidData().getPlayers()));
					GrandExchange e = Raid.UTIL.getRaidData().getExchange();
					p.sendMessage("Exchange HashMap Items: " + isNull(e.getItems()));
					
				}else if(args[0].equalsIgnoreCase("path")){
					
					for(Entity e : p.getNearbyEntities(50, 50, 50)){
					
						if(e instanceof LivingEntity && (!(e instanceof Player || e instanceof Bat))){
						Location cs = p.getLocation();

						EntityCreature ec = ((CraftCreature) e).getHandle();

						float speed = 0.9f;
						
						PathEntity pf = ((CraftWorld) cs.getWorld())
								.getHandle().a(ec, cs.getBlockX(),
										cs.getBlockY(), cs.getBlockZ(), speed,
										true, false, false, true);

						ec.setPathEntity(pf);
						ec.getNavigation().a(cs.getBlockX(), cs.getBlockY(),
								cs.getBlockZ(), speed);

						//Creature creature = (Creature) e;
							//creature.setTarget(e);   
						
							p.sendMessage("Moving " + e.getType() + " towards you.");
						}
			        
					}
					
				}				
			}
		
		return false;
	}

	private boolean isNull(Object o) {
		return o == null;
	}
	
	

}
