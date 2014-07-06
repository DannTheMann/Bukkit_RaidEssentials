package com.aog.hcraid.save;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class WeaponDrop {
	
	private Material type;
	private int rarity;
	private ArrayList<Enchantment> validEnchantments;
	
	@SuppressWarnings("serial")
	public WeaponDrop(Material type, int rare){
		this.type = type;
		this.rarity = rare;
		
		Material[][] items = {{Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE
			, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
			Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS
			, Material.IRON_BOOTS},{Material.DIAMOND_SWORD, Material.DIAMOND_AXE
			, Material.IRON_AXE, Material.IRON_SWORD},{Material.BOW}};
		
		int validation = 0;
		
		for(Material[] item : items){
			
			for(int i = 0; i < item.length; i++){
				if(item[i] == type){
					break;
				}
			}
			
			validation++;
			
		}
		
		switch(validation){
		
		case 0:
			validEnchantments = new ArrayList<Enchantment>(){{
				add(Enchantment.PROTECTION_ENVIRONMENTAL);
				add(Enchantment.PROTECTION_EXPLOSIONS);
				add(Enchantment.PROTECTION_FALL);
				add(Enchantment.PROTECTION_FIRE);
				add(Enchantment.THORNS);
				add(Enchantment.WATER_WORKER);
				add(Enchantment.OXYGEN);
			}};
			break;
		case 1:
			validEnchantments = new ArrayList<Enchantment>(){{
				add(Enchantment.DAMAGE_ALL);
				add(Enchantment.DAMAGE_ARTHROPODS);
				add(Enchantment.DAMAGE_UNDEAD);
				add(Enchantment.FIRE_ASPECT);
				add(Enchantment.KNOCKBACK);
			}};
			break;
		case 2:
			validEnchantments = new ArrayList<Enchantment>(){{
				add(Enchantment.ARROW_DAMAGE);
				add(Enchantment.ARROW_FIRE);
				add(Enchantment.ARROW_INFINITE);
				add(Enchantment.ARROW_KNOCKBACK);
			}};
			break;
		
		}
		
		validEnchantments.add(Enchantment.DURABILITY);
		
	}
	
	public ItemStack getItemStack(){
		return new ItemStack(type);
	}
	
	public int getRarity(){
		return rarity;
	}

	public ArrayList<Enchantment> validDefaultEnchantments(){
		return validEnchantments;
	}
	
}
