package com.aog.hcraid.save;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.aog.hcraid.Util;

public class ExchangeItem extends SavedItem{
	
	private static final long serialVersionUID = 5286410047450203281L;
	private static final int SILVER_WORTH = 16;
	private static final int GOLD_WORTH = 256;
	private int id;
	private WeaponRarity rarity;
	private long addedDate;
	private long removalDate;
	
	public ExchangeItem(ItemStack is, String playerUUID, boolean adminSale, int sellingCost, int id) {
		super(is);
		
		if(playerUUID == null){
			throw new IllegalArgumentException("Player selling ID must not be null.");
		}else if(sellingCost < 0){
			throw new IllegalArgumentException("Selling cost must be greater than zero.");
		}
		
		this.uuidSeller = playerUUID;
		this.adminSale = adminSale;
		this.sellingCost = sellingCost;
		this.id = id;
		this.addedDate = System.currentTimeMillis() / 1000;
		this.removalDate = Util.getFutureDateInSeconds(7) + System.currentTimeMillis() / 1000;
		this.rarity = Util.getWeaponRarity(is);
	}
	
	private String uuidSeller;
	private boolean adminSale;
	private int sellingCost;
	
	public boolean valid(){
		return addedDate < removalDate;
	}
	
	public WeaponRarity getRarity(){
		return rarity;
	}
	
	public void retract(){
		Player p = Bukkit.getPlayer(UUID.fromString(uuidSeller));
		if(p != null){
			Util.returnItem(toBukkitItemStack(), p);
		}else{
			Util.returnItemToOfflinePlayer(toBukkitItemStack(), uuidSeller);
		}
	}
	
	public String getSellerId(){
		return uuidSeller;
	}
	
	public boolean isAdminSale(){
		return adminSale;
	}
	
	public int getSellingCost(){
		return sellingCost;
	}
	
	public String getTradingTranslation(){		
		int testPoints = sellingCost;
		
		int gold = testPoints / GOLD_WORTH;
		int goldPoints = gold * GOLD_WORTH;
		int silver = (testPoints - goldPoints) / SILVER_WORTH;
		int silverPoints = silver * SILVER_WORTH;
		int bronze = testPoints - (goldPoints + silverPoints);
		
		return ChatColor.GOLD + "Gold: " + gold + ChatColor.GRAY + ", Silver: " + silver + 
				 ChatColor.RED + ", Bronze: " + bronze;
	}
	
	@Override
	public String toString(){
		return super.toString() + " selling for " + getTradingTranslation();
	}

	public int getSellingId() {
		return id;
	}

}