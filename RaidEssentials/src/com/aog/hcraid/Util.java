package com.aog.hcraid;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import serial_file.SaveData;

import com.aog.hcraid.save.ExchangeItem;
import com.aog.hcraid.save.HCPlayer;
import com.aog.hcraid.save.RaidData;
import com.aog.hcraid.save.WeaponRarity;

public class Util {
	
	private JavaPlugin plugin;
	private RaidData raidData;
	public HashMap<String, HCPlayer> players;
	
	public void load(JavaPlugin plugin){
		this.plugin = plugin;
		players = new HashMap<>();
		raidData = SaveData.loadRaidDate();
	}
	
	public JavaPlugin getPlugin(){
		return plugin;
		
	}

	public static void nameItem(ItemStack itemstack, String generateName) {
		
		ItemMeta im = itemstack.getItemMeta();
		im.setDisplayName(generateName);
		itemstack.setItemMeta(im);
		
	}

	public static void loreItem(ItemStack itemstack, String string) {
		
		ItemMeta im = itemstack.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		lore.add(string);
		im.setLore(lore);
		itemstack.setItemMeta(im);
		
	}

	public static void returnItem(ItemStack bukkitItemStack, Player p) {
		
		if(bukkitItemStack == null || p == null){
			return;
		}
		
		for(ItemStack i : p.getInventory().getContents()){
			
			if(i == null){
				p.getInventory().addItem(bukkitItemStack);
				break;
			}
			
		}
		
		p.getWorld().dropItem(p.getLocation(), bukkitItemStack);
		
	}

	public static long getFutureDateInSeconds(int i) {		
		return 86400 * i;		
	}
	
	public static long getFutureDateInMilliseconds(int i) {		
		return (86400 * i) * 1000;		
	}

	public static WeaponRarity getWeaponRarity(ItemStack is) {
		
		String item = getItemStackName(is);
		
		if(item.startsWith(ChatColor.DARK_PURPLE + "*")){
			return WeaponRarity.LEGENDARY;
		}else if(item.startsWith(ChatColor.LIGHT_PURPLE + "*")){
			return WeaponRarity.HEROIC;
		}else if(item.startsWith(ChatColor.BLUE + "*")){
			return WeaponRarity.RARE;
		}else if(item.startsWith(ChatColor.DARK_AQUA + "*")){
			return WeaponRarity.UNCOMMON;
		}else if(item.startsWith(ChatColor.AQUA + "*")){
			return WeaponRarity.COMMON;
		}
		
		return WeaponRarity.NOTHING;
	}

	public static String getItemStackName(ItemStack is) {
	
		if(is != null && is.getItemMeta() != null){
			
			ItemMeta im = is.getItemMeta();
			
			return im.getDisplayName();
		}
		
		return "";
		
	}

	public static ItemStack nameItemStack(ItemStack is, String name) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack nameItemStack(ItemStack is, char a) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + a);
		is.setItemMeta(im);
		return is;		
	}

	public static ItemStack loreItemStack(ItemStack is, String line) {
		List<String> list = null;
		if(is.getItemMeta().getLore() == null)
			list = new ArrayList<String>();
		else
			list = is.getItemMeta().getLore();
		
		line = ChatColor.stripColor(line);
		
		if(line != null){
			
			for(int i = 0; i < line.split("\n").length; i++){
				
				list.add(ChatColor.RESET + " " + ChatColor.GRAY + line.split("\n")[i]);
				
			}

		}
		
		ItemMeta im = is.getItemMeta();
		im.setLore(list);
		is.setItemMeta(im);
		return is;
	}
	
	public static void returnItemToOfflinePlayer(ItemStack bukkitItemStack,
			String uuidSeller) {
		
		Raid.UTIL.players.get(uuidSeller).addItemToReturn(bukkitItemStack);
		
	}

	public static ItemStack createExchangeItem(ExchangeItem exchangeItem) {
		
		ItemStack is = exchangeItem.toBukkitItemStack();
		
		loreItem(is, "");
		loreItem(is, "Being Sold By: " + getPlayerName(exchangeItem.getSellerId()));
		loreItem(is, exchangeItem.getTradingTranslation());
		
		return null;
	}

	private static String getPlayerName(String sellerId) {
		
		Player p = Bukkit.getPlayer(UUID.fromString(sellerId));
		
		if(p != null){
			return p.getName();
		}else{
			return Bukkit.getOfflinePlayer(UUID.fromString(sellerId)).getName();
		}
		
	}

	public String getUUID(Player p) {
		return p.getUniqueId().toString();
	}
	
	@Deprecated
	public String getUUID(String playerName){
		
		Player p = Bukkit.getPlayer(playerName);
	
		if(p != null){
			return p.getUniqueId().toString();
		}else{
			OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
			
			if(op != null){
				return op.getUniqueId().toString();
			}
		}
		
		return null;
	}
	
	public Player getPlayer(String uuid){
		return Bukkit.getPlayer(UUID.fromString(uuid));
	}

	public int getTotalPointsForCurrency(int gold, int silver, int bronze){
		
		return gold + (silver *16) + (bronze * 256);
		
		
	}

	public RaidData getRaidData() {
		return raidData;
	}

	public String getDirectory() {		
		return plugin.getDataFolder().getAbsolutePath() + File.separator + "Data" + File.separator;
	}

	public String getPlayerDirectory() {
		return plugin.getDataFolder().getAbsolutePath() + File.separator + "Data" + File.separator + "Players" + File.separator;
	}

	public void save() {
		SaveData.saveRaidData(raidData);
		
		Raid.log("Trying to save all player files.");
		
		for(HCPlayer player : players.values()){
			try{
				SaveData.savePlayerFile(player);
			}catch(Exception e){
				Raid.log("Failed to save player file ('" + player.getUniqueId() + "')" + "! Printing stacktrace...");
				e.printStackTrace();
			}
		}	
		Raid.log("Saved all player files.");
	}

}
