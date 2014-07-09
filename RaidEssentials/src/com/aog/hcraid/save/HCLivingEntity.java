package com.aog.hcraid.save;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class HCLivingEntity implements Serializable{

	
	private static final long serialVersionUID = -3587651797105935770L;
	
	private int entityId;
	private static final double DROP_CHANCE = 0.6;
	private transient LivingEntity entity;
	private BukkitLocation location;
	
	public HCLivingEntity(){
		
	}
	
	@SuppressWarnings("deprecation")
	public void respawn(){
		
		location.getBukkitLocation().getWorld().spawnEntity(location.getBukkitLocation(), 
				EntityType.fromId(entityId));
		
	}
	
	public int getEntityId(){
		return entityId;
	}
	
	public Location getLocation(){
		return location.getBukkitLocation();
	}
	
	public LivingEntity getEntity(){
		return entity;
	}
	
	public void setEntity(LivingEntity le){
		entity = le;
	}
	
	public void dropLoot(){
		
		if(Math.random() <= DROP_CHANCE){
		
			ItemStack possibleWeapon = WeaponGeneratorAPI
					.generateRandomWeapon();

			if (possibleWeapon != null && notFromSpawner()) {

				entity.getWorld()
						.dropItem(entity.getLocation(), possibleWeapon);

			}

		}
		
	}

	private boolean notFromSpawner() {
	
		int total = 0;
		
		for(Entity e : getEntity().getNearbyEntities(10, 10, 10)){
			
			if(e instanceof LivingEntity){
				
				if(e.getType() == entity.getType()){
					total++;
				}
				
				if(total > 20){
					return false;
				}
				
			}
			
		}
		
		return true;
	}
	
}
