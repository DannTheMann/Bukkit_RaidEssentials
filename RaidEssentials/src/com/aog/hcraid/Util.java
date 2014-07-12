package com.aog.hcraid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


import com.aog.hcraid.commands.DebugCommand;
import com.aog.hcraid.commands.ExchangeCommand;
import com.aog.hcraid.events.exchange.InventoryManagement;
import com.aog.hcraid.events.exchange.PlayerConnectionEvents;
import com.aog.hcraid.save.ExchangeItem;
import com.aog.hcraid.save.HCPlayer;
import com.aog.hcraid.save.RaidData;
import com.aog.hcraid.save.WeaponRarity;
import com.aog.hcraid.serial_file.SaveData;

public class Util {
	
	private JavaPlugin plugin;
	private RaidData raidData;
	//public HashMap<String, HCPlayer> players;
	
	public void load(JavaPlugin plugin){
		this.plugin = plugin;
		log("Loading plugin...");
		//players = new HashMap<>();
		
		log("Checking directories...");
		checkDirectory();
		
		raidData = SaveData.loadRaidDate();
		
		if(raidData.getExchange() == null){
			raidData = new RaidData();
		}
		
		log("Loaded Raid Data...");
		registerEvents();
		log("Registered Events...");
		registerCommands();
		log("Registered Commands...");
		
		log("Checking online players, adding non existing members.");
		checkOnlinePlayers();
	}
	
	private void checkOnlinePlayers() {
		
		for(Player p : Bukkit.getOnlinePlayers()){
			
			addPlayer(p);
			
		}
		
	}

	private void checkDirectory() {
		File f = new File(getDirectory());
		if(!f.exists()){
			log("Making data directory... - " + getDirectory() );
			f.mkdirs();
		}
		f = new File(getPlayerDirectory());
		if(!f.exists()){
			log("Making player directory... - " + getPlayerDirectory());
			f.mkdirs();
		}
	}

	public void log(String string) {
		System.out.println("[" + plugin.getServer().getPluginManager().getPlugin(plugin.getName()).getName() 
				+ "] " + string);
	}

	private void registerCommands() {
		plugin.getServer().getPluginCommand("ge").setExecutor(new ExchangeCommand());
		plugin.getServer().getPluginCommand("rdebug").setExecutor(new DebugCommand());
	}

	private void registerEvents() {
		plugin.getServer().getPluginManager().registerEvents(new InventoryManagement(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new PlayerConnectionEvents(), plugin);
	}

	public JavaPlugin getPlugin(){
		return plugin;
		
	}

	public void nameItem(ItemStack itemstack, String generateName) {
		
		ItemMeta im = itemstack.getItemMeta();
		im.setDisplayName(generateName);
		itemstack.setItemMeta(im);
		
	}

	/**
	 * Overwrite existing lore using an Array.
	 * @param itemstack
	 * @param arrayList
	 */
	public void loreOverwriteItem(ItemStack itemstack, ArrayList<String> arrayList) {
		
		ItemMeta im = itemstack.getItemMeta();
		
		ArrayList<String> clone = new ArrayList<String>();
		
		for(String u : arrayList){
			clone.add(ChatColor.RESET + "" + ChatColor.GRAY + u);
		}
		
		im.setLore(clone);
		itemstack.setItemMeta(im);
		
	}

	public void returnItem(ItemStack bukkitItemStack, Player p) {
		
		if(bukkitItemStack == null || p == null){
			return;
		}
		
		for(ItemStack i : p.getInventory().getContents()){
			
			if(i == null){
				p.getInventory().addItem(bukkitItemStack);
				return;
			}
			
		}
		
		p.getWorld().dropItem(p.getLocation(), bukkitItemStack);
		
	}

	public long getFutureDateInSeconds(int i) {		
		return 86400 * i;		
	}
	
	public long getFutureDateInMilliseconds(int i) {		
		return (86400 * i) * 1000;		
	}

	public WeaponRarity getWeaponRarity(ItemStack is) {
		
		String item = getItemStackName(is);
		
		if(item == null){
			return WeaponRarity.NOTHING;
		}else if(item.startsWith(ChatColor.DARK_PURPLE + "*")){
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

	public String getItemStackName(ItemStack is) {
	
		if(is != null && is.getItemMeta() != null){
			
			ItemMeta im = is.getItemMeta();
			
			return im.getDisplayName();
		}
		
		return "";
		
	}

	public ItemStack nameItemStack(ItemStack is, String name) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return is;
	}
	
	public ItemStack nameItemStack(ItemStack is, char a) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + a);
		is.setItemMeta(im);
		return is;		
	}

	public ItemStack loreItemStack(ItemStack is, String line) {
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
	
	public void returnItemToOfflinePlayer(ItemStack bukkitItemStack,
			String uuidSeller) {
		
		Raid.UTIL.getRaidData().getPlayers().get(uuidSeller).addItemToReturn(bukkitItemStack);
		
	}

	@Deprecated
	public ItemStack createExchangeItemv(ExchangeItem exchangeItem) {
		
		@SuppressWarnings("unused")
		ItemStack is = exchangeItem.toBukkitItemStack();
		
		//loreItem(is, "");
		//loreItem(is, "Being Sold By: " + getPlayerName(exchangeItem.getSellerId()));
		//loreItem(is, exchangeItem.getTradingTranslation());
		
		return null;
	}

	@SuppressWarnings("unused")
	private String getPlayerName(String sellerId) {
		
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
		
		return (gold*256) + (silver *16) + bronze;
		
		
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
		
		if(getRaidData() == null)
			Raid.log("Raid Data is null.");
		
		if(getRaidData().getPlayers() == null){
			Raid.log("Players is null.");
		}
		
		for(HCPlayer player : getRaidData().getPlayers().values()){
			try{
				SaveData.savePlayerFile(player);
			}catch(Exception e){
				Raid.log("Failed to save player file ('" + player.getUniqueId() + "')" + "! Printing stacktrace...");
				e.printStackTrace();
			}
		}	
		Raid.log("Saved all player files.");
	}

	public HCPlayer getPlayer(Player p) {
		return getRaidData().getPlayers().get(getUUID(p));
	}

	public void addPlayer(Player p) {
		HCPlayer hp = raidData.getPlayers().get(p.getUniqueId().toString());
		
		if(hp == null){
			raidData.getPlayers().put(p.getUniqueId().toString(), new HCPlayer(p));
			log("Adding new player - '" + p.getUniqueId().toString() + "' (" + p.getName() + ") [PLAYER].");
		}else{
			log("Player '" + p.getName() + "' is already on the database.");
		}
	}

}
