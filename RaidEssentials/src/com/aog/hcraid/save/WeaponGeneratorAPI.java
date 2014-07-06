package com.aog.hcraid.save;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.aog.hcraid.Util;

public class WeaponGeneratorAPI {
	
	private static Random random = new Random();
	private static ArrayList<WeaponDrop> items = new ArrayList<WeaponDrop>(){{
		add(new WeaponDrop(Material.DIAMOND_SWORD, 4));
		add(new WeaponDrop(Material.DIAMOND_AXE, 4));
		add(new WeaponDrop(Material.DIAMOND_HOE, 4));
		add(new WeaponDrop(Material.IRON_SWORD, 2));
		add(new WeaponDrop(Material.IRON_AXE, 2));
		add(new WeaponDrop(Material.IRON_HOE, 2));
		add(new WeaponDrop(Material.BOW, 1));
		add(new WeaponDrop(Material.DIAMOND_CHESTPLATE, 3));
		add(new WeaponDrop(Material.DIAMOND_HELMET, 3));
		add(new WeaponDrop(Material.DIAMOND_LEGGINGS, 3));
		add(new WeaponDrop(Material.DIAMOND_BOOTS, 3));
		add(new WeaponDrop(Material.IRON_HELMET, 2));
		add(new WeaponDrop(Material.IRON_CHESTPLATE, 2));
		add(new WeaponDrop(Material.IRON_LEGGINGS, 2));
		add(new WeaponDrop(Material.IRON_BOOTS, 2));
	}};
	
	public static final double COMMON_CHANCE = 0.2;
	
	public static ItemStack generateRandomWeapon(){
		
		WeaponRarity wr = getRarity();
		
		if(wr == null){
			return null;
		}
		
		if(items == null){
			return null;
		}
		
		WeaponDrop drop = items.get(random.nextInt(items.size()));
		
		ItemStack itemstack = new ItemStack(drop.getItemStack());
		
		int maxEnchantments = wr.getMaxEnchantments();
		int rand = random.nextInt(drop.validDefaultEnchantments().size());
		int randCount = 0;
		int scape = 0;
		
		while(itemstack.getEnchantments().size() < maxEnchantments && scape < 100){
			
			randCount = 0;
			
			for(Enchantment enc : drop.validDefaultEnchantments()){
				
				if( ! itemstack.containsEnchantment(enc) && rand == randCount){
					
					itemstack.addEnchantment(enc, random.nextInt(wr.defaultMaxLevel())+1);
					
				}
				
				randCount++;
				
			}
			
			rand = random.nextInt(drop.validDefaultEnchantments().size());
			
			scape++;
			
		}
		
		Util.nameItem(itemstack, wr.name() + " " + generateName(drop));
		Util.loreItem(itemstack, ChatColor.GRAY + "A " + wr + " weapon.");
		
		return itemstack;
		
	}
	
	private static WeaponRarity getRarity(){
		
		double chance = random.nextDouble();
		
		for(WeaponRarity wr : WeaponRarity.values()){
			
			if(wr.chance(chance)){
				return wr;
			}
			
		}
		return null;
	}
	
	private static String generateName(WeaponDrop wd){
		
		if(wd.validDefaultEnchantments().contains(Enchantment.ARROW_DAMAGE)){
			
			String[] header = {"Powerful", "Aweful", "Blighting", "Kickass", "Devastating", "Accurate",
							"Aphrodite's", "Ivory", "Putrid", "Feathered"};
			String[] middle = {"Bow", "Long Bow", "Torque Bow", "Olden Bow", "Magic Bow", "Elven Bow",
					"Compound Bow", "Short Bow", "Mystic Bow", "Arch Bow", "Compact Bow", "Orcish Bow",
					"Recurve Bow"};
			String[] end = {"of The Universe", "of the World", "of Ages", "", "of the Legends", "of Time"};
			
			return header[random.nextInt(header.length)] + " " + middle[random.nextInt(middle.length)]
					+ " " + end[random.nextInt(end.length)];
			
		}else if(wd.validDefaultEnchantments().contains(Enchantment.DAMAGE_ALL)){
			
			String[] header = {"Incredible", "Amazing", "Destroying", "Kickass", "Legendary", "Fortold"
					, "Royal", "Magical", "Feared", "Reaped", "Forbidden", "Insane", "eZ", "Randi Slaying"
					, "Loyal", "Mystic", "Mythical", "Emerald", "Ruby", "Onyx", "Mithril", "Ancient", "War"
					, "Deadly", "Rustic", "Enlightened"};
			String[] middle = {"Longsword", "Cleaver", "Katana", "Cutlass", "Scimitar","Brand"
					,"Edge","Bayonet","Cutter","Lance"};
			String[] end = {"of Punishment","of Light","of Darkness","of Goodness","of Anger","of Revenge","of Happiness",
					"of Sorrow","of Melancholy","of Destruction","of Insanity","of Retribution","of Kindness","of Love",
					"of Sin","of Gluttony","of Wrath","of Envy","of Pride"};
			
			return header[random.nextInt(header.length)] + " " + middle[random.nextInt(middle.length)]
					+ " " + end[random.nextInt(end.length)];
			
		}else if(wd.validDefaultEnchantments().contains(Enchantment.PROTECTION_ENVIRONMENTAL)){
			
			String[] header = {"Citrine","Pearl","Topaz","King's","Legendary","Prince's",
					"Scrubs","Bishop's","Rook's","Turquoise","Randi's","Mercury's","Aphrodite's","Mars's","Jupiter's",
					"Saturn's","Neptune's","Skeleton","Bone","Poison","Unknown","Battle","Mythril","Earth","Gale","Rune",
					"Regal","Infamous","danslayerx's","Alucard's"};
			String middle = WordUtils.capitalize(wd.getItemStack().getType().toString().replaceAll("_", "_").toLowerCase());
			String[] end = {"of Punishment","of Light","of Darkness","of Goodness","of Anger","of Revenge","of Happiness",
					"of Sorrow","of Melancholy","of Destruction","of Insanity","of Hellfire","of Iron","of HcRaid","of Beowulf",
					"of Death","of Truth","of Silence","of the Untold","of the Abyss","of Swiftness","of Harmony"};
			
			return header[random.nextInt(header.length)] + " " + middle + " " + end[random.nextInt(end.length)];
			
		}else{
			return "Some Magical Weapon!";
		}
	
	}

}
