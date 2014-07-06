package com.aog.hcraid.save;

import org.bukkit.ChatColor;

public enum WeaponRarity {
	
	LEGENDARY(0.002, ChatColor.DARK_PURPLE + "*", 7, true, 7),
	HEROIC(0.002, ChatColor.LIGHT_PURPLE + "*", 6, true, 6),
	RARE(0.005, ChatColor.BLUE + "*", 5, false, 5),
	UNCOMMON(0.01, ChatColor.DARK_AQUA + "*", 5, false, 3),
	COMMON(0.02, ChatColor.AQUA + "*", 5, false, 2),
	NOTHING(0.0, "", 0, false, 0);
	
	private final double chance;
	private final String symbol;
	private final int number_of_enchantments;
	private final int default_max_enchant;
	private final boolean overEnchant;
	
	WeaponRarity(double chance, String symbol, int number_of_enchantments, boolean overStack, int maxLevel){
		this.chance = chance;
		this.default_max_enchant = maxLevel;
		this.symbol = symbol;
		this.number_of_enchantments = number_of_enchantments;
		this.overEnchant = overStack;
	}
	
	boolean chance(double canBeat){
		return chance <= canBeat;
	}
	
	String symbol(){
		return symbol;
	}
	
	int getMaxEnchantments(){
		return number_of_enchantments;
	}
	
	boolean overEnchantItem(){
		return overEnchant;
	}
	
	int defaultMaxLevel(){
		return default_max_enchant;
	}

}
