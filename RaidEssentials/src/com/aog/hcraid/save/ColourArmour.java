package com.aog.hcraid.save;

import java.io.Serializable;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ColourArmour implements Serializable{

	private static final long serialVersionUID = -7582495168470434850L;
	private int red;
	private int green;
	private int blue;
	
	public ColourArmour(LeatherArmorMeta lma){
		
		this.setRed(lma.getColor().getRed());
		this.setGreen(lma.getColor().getGreen());
		this.setBlue(lma.getColor().getBlue());
		
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}
	
	public ItemStack returnColour(ItemStack is){
		
		LeatherArmorMeta lad = (LeatherArmorMeta) is.getItemMeta();
		
		lad.getColor().setBlue(blue);
		lad.getColor().setGreen(green);
		lad.getColor().setRed(red);
		
		return is;
		
		
		
	}

}
