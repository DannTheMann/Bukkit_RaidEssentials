package com.aog.hcraid;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
	private MaterialAlias alias;
	//public HashMap<String, HCPlayer> players;
	
	public void load(JavaPlugin plugin){
		this.plugin = plugin;
		log("Loading plugin...");
		//players = new HashMap<>();
		
		log("Checking directories...");
		checkDirectory();
		
		raidData = SaveData.loadRaidDate();
		
		if(raidData == null){
			raidData = new RaidData();
		}
		
		alias = new MaterialAlias();
		
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

	public int getPlayersBalanceInInventory(Player p) {
		
		int points = 0;
		
		for(ItemStack i : p.getInventory()){
			if (i != null) {
				if (i.getType() == Material.GOLD_INGOT) {
					points += 256 * i.getAmount();
				} else if (i.getType() == Material.IRON_INGOT) {
					points += 16 * i.getAmount();;
				} else if (i.getType() == Material.CLAY_BRICK) {
					points+= i.getAmount();;
				}
			}
		}
		return points;
	}

	@SuppressWarnings("deprecation")
	public void deductFromPlayersInventory(Player p, int sellingCost) {
		
		Raid.log("Deducting...");
		Raid.log("Cost = " + sellingCost);
		
		for(ItemStack i : p.getInventory()){
			if ( i == null) { continue; }
			if(i.getType() == Material.GOLD_INGOT){
				if(sellingCost >= 256){
					
					if(i.getAmount() > 1){
						i.setAmount(i.getAmount()-1);
					}else{
						p.getInventory().remove(i);
					}
					
					sellingCost =- 256;
					
				}
				
			}else if(i.getType() == Material.IRON_INGOT){
				
				if(sellingCost >= 16){
					
					if(i.getAmount() > 1){
						i.setAmount(i.getAmount()-1);
					}else{
						p.getInventory().remove(i);
					}
					
					sellingCost =- 16;
					
				}
				
			}else if(i.getType() == Material.CLAY_BRICK){
				
				if(sellingCost >= 1){
					
					if(i.getAmount() > 1){
						i.setAmount(i.getAmount()-1);
					}else{
						p.getInventory().remove(i);
					}
					
					sellingCost--;
					
				}
			}
			
			if(sellingCost == 0){
				break;
			}
			
		}
		
		Raid.log("After Cost = " + sellingCost);
		
		if(sellingCost > 0){
			
			
			
			int goldBars = 0;
			int silverBars = 0;
			
			for(ItemStack i : p.getInventory()){
				if(i != null){
					if(i.getType() == Material.GOLD_INGOT){
						goldBars++;
					}else if(i.getType() == Material.IRON_INGOT){
						silverBars++;
					}
				}
			}
			
			if(silverBars > 0){
				
				for(int i = 0; i < silverBars && sellingCost >= 16; i++){
					sellingCost =- 16;
					
				}
			}
			
			if(goldBars > 0){
				
				for(int i = 0; i < goldBars && sellingCost >= 256; i++){
					sellingCost =- 256;
				}
				
			}
			/*
			
			for (ItemStack i : p.getInventory()) {
				if(i == null) { continue; }
				if (i.getType() == Material.GOLD_INGOT) {
					if (sellingCost >= 256) {

						if (i.getAmount() > 1) {
							i.setAmount(i.getAmount() - 1);
						} else {
							p.getInventory().remove(i);
						}

						returnPoints = 256 - sellingCost;

						break;
					}

				} else if (i.getType() == Material.IRON_INGOT) {

					if (sellingCost >= 16) {

						if (i.getAmount() > 1) {
							i.setAmount(i.getAmount() - 1);
						} else {
							p.getInventory().remove(i);
						}
						returnPoints = 16 - sellingCost;
						break;
					}

				}

			}
			
			*/
			
			Raid.log("Return Points = " + sellingCost);
			
			
			int sil = sellingCost / 16;
			
			int br = sellingCost - (16 * sil);
			
			Raid.log("Silver 2R = " + sil);
			Raid.log("Bronze 2R = " + br);
			
			if(sil > 0){
				p.getInventory().addItem(getSilver(sil));
			}
			if(br > 0){
				p.getInventory().addItem(getBronze(br));
			}
		}

		p.updateInventory();
		
	}
	
	public ItemStack getBronze(int amount){
		return nameItemStack(new ItemStack(Material.CLAY_BRICK, amount), "Bronze Bar");
	}
	
	public ItemStack getSilver(int amount){
		return nameItemStack(new ItemStack(Material.IRON_INGOT, amount), "Silver Bar");
	}
	
	public ItemStack getGold(int amount){
		return nameItemStack(new ItemStack(Material.GOLD_INGOT, amount), "Gold Bar");
	}

	@SuppressWarnings("deprecation")
	public int removePlayersInventoryBalance(Player p) {
		int points = 0;
		
		for(ItemStack i : p.getInventory()){
			if (i != null) {
				if (i.getType() == Material.GOLD_INGOT) {
					points += 256 * i.getAmount();
					p.getInventory().remove(i);
				} else if (i.getType() == Material.IRON_INGOT) {
					points += 16 * i.getAmount();;
					p.getInventory().remove(i);
				} else if (i.getType() == Material.CLAY_BRICK) {
					points+= i.getAmount();;
					p.getInventory().remove(i);
				}
			}
		}
		p.updateInventory();
		return points;
	}

	public void payOutPlayer(Player p, int bal) {
		
		int testPoints = bal;
		
		int gold = testPoints / ExchangeItem.GOLD_WORTH;
		int goldPoints = gold * ExchangeItem.GOLD_WORTH;
		int silver = (testPoints - goldPoints) / ExchangeItem.SILVER_WORTH;
		int silverPoints = silver * ExchangeItem.SILVER_WORTH;
		int bronze = testPoints - (goldPoints + silverPoints);
		
		while(gold > 64 || silver > 64 || bronze > 64){
			
			if(gold > 64){
				gold =- 64;
				givePlayerItem(p, getGold(64));
			}
			if(silver > 64){
				silver =- 64;
				givePlayerItem(p, getSilver(64));
			}
			if(bronze > 64){
				bronze =- 64;
				givePlayerItem(p, getBronze(64));
			}
			
		}
		
		givePlayerItem(p, getGold(gold));
		givePlayerItem(p, getSilver(silver));
		givePlayerItem(p, getBronze(bronze));
		
	}

	public void givePlayerItem(Player p, ItemStack item) {
		
		if(item.getAmount() == 0){
			return;
		}
		
		for(ItemStack i : p.getInventory()){
			if(i == null){
				p.getInventory().addItem(item);
				return;
			}
		}
		
		p.getWorld().dropItem(p.getLocation(), item);
		
	}

	public Material getItemAlias(String a2) {
		
		HashMap<Material, ArrayList<String>> map = alias.getAlias();
		
		for(Material m : alias.getAlias().keySet()){
			
			for(String u : alias.getAlias().get(m)){
				if(u.equalsIgnoreCase(a2)){
					return m;
				}
			}
			
		}
		
		try{
			return Material.getMaterial(Integer.parseInt(a2));
		}catch(NumberFormatException e){
			
		}
		
		return null;
		
	}

	public String getItemAliases(Material type) {
		
		for(Material m : alias.getAlias().keySet()){
			
			if(m == type){
			
				String line = "";

				for (String u : alias.getAlias().get(m)) {
					line += u + ",";
				}

				line.substring(0, line.length() - 2);

				return line;
			}

		}
		return "N/A";
		
	}

	public String getTranslationForCurrency(int tax) {
		int testPoints = tax;
		
		int gold = testPoints / ExchangeItem.GOLD_WORTH;
		int goldPoints = gold * ExchangeItem.GOLD_WORTH;
		int silver = (testPoints - goldPoints) / ExchangeItem.SILVER_WORTH;
		int silverPoints = silver * ExchangeItem.SILVER_WORTH;
		int bronze = testPoints - (goldPoints + silverPoints);
		
		return ChatColor.GOLD + "Gold: " + gold + ChatColor.AQUA + " Silver: " + silver + 
				 ChatColor.RED + " Bronze: " + bronze;
	}

	public Player getPlayer(HCPlayer hcPlayer) {
		
		return Bukkit.getPlayer(UUID.fromString(hcPlayer.getUniqueId()));
		
	}

}
