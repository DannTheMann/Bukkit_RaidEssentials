package com.aog.hcraid.save;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class SavedItem implements Serializable{

	private static final long serialVersionUID = -1413651047381417937L;
	private int itemId;
	private boolean isAir;
	private boolean isColoured;
	private int amount;
	private short durability;
	private Material itemType;
	public String name;
	public List<String> lore;
	private Byte data;
	private HashMap<Integer, Integer> enchantments = new HashMap<Integer, Integer>(); // KEY = EnchantmentID, Value = LEVEL
	private ColourArmour colour;
	
	@Override
	public String toString(){
		return WordUtils.capitalize(itemType.toString().toLowerCase().replaceAll("_", " ")) + ":" +
				durability + " x" + amount;
	}
	
	@SuppressWarnings("deprecation")
	public SavedItem(ItemStack is){
		if((is == null) || (is.getType() == Material.AIR)
		|| (is.getTypeId() == 0)){
			throw new IllegalArgumentException("Cannot create a saved ItemStack of Air");
		}
		this.setItemId(is.getTypeId());
		this.setAmount(is.getAmount());
		this.setDurability(is.getDurability());
		this.setItemType(is.getType());
		this.setData(is.getData().getData());
		
		if(is.getItemMeta().getDisplayName() != null)
			this.setName(is.getItemMeta().getDisplayName());
		if(is.getItemMeta().getLore() != null)
			this.setLore(is.getItemMeta().getLore());	
		
		for(Enchantment e : is.getEnchantments().keySet())
			this.enchantments.put(e.getId(), is.getEnchantmentLevel(e));
		
		this.isColoured = false;
		
		if(isLeatherArmour(is)){
			
			LeatherArmorMeta ld = (LeatherArmorMeta) is.getItemMeta();
			
			this.setColour(new ColourArmour(ld));
			
		}
	}

	private boolean isLeatherArmour(ItemStack is) {
		
		if(is.getType() == Material.LEATHER_HELMET)
			return true;
		if(is.getType() == Material.LEATHER_CHESTPLATE)
			return true;
		if(is.getType() == Material.LEATHER_LEGGINGS)
			return true;
		if(is.getType() == Material.LEATHER_BOOTS)
			return true;
		
		return false;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public Material getItemType() {
		return itemType;
	}

	public void setItemType(Material itemType) {
		this.itemType = itemType;
	}

	public short getDurability() {
		return durability;
	}

	public void setDurability(short durability) {
		this.durability = durability;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public Byte getData() {
		return data;
	}

	public void setData(Byte data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("deprecation")
	public ItemStack toBukkitItemStack() {
		ItemStack is = new ItemStack(this.getItemId());
		
		if(isColoured())
		is = getColour().returnColour(is);
		
		is.setAmount(this.amount);
		is.setDurability(this.durability);
		is.getData().setData(this.data);
		
		ItemMeta im = is.getItemMeta();
		
		if(this.name != null){
			im.setDisplayName(this.name);
		}
		if(this.lore != null){
			im.setLore(this.lore);
		}
		
		is.setItemMeta(im);
	
		for(Integer i : this.enchantments.keySet()){
			is.addUnsafeEnchantment(Enchantment.getById(i), this.enchantments.get(i));
		}
		
		return is;
	}

	public boolean isAir() {
		return isAir;
	}

	public void setAir(boolean isAir) {
		this.isAir = isAir;
	}

	public boolean isColoured() {
		return isColoured;
	}

	public void setColoured(boolean isColoured) {
		this.isColoured = isColoured;
	}

	public ColourArmour getColour() {
		return colour;
	}

	public void setColour(ColourArmour colour) {
		this.colour = colour;
	}
	
	
}
