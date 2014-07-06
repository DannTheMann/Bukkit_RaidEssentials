package com.aog.hcraid.save;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.aog.hcraid.Raid;

public class HCBoss extends HCLivingEntity{

	private static final long serialVersionUID = -6224169256740809193L;
	
	private static final double WITHER_SPAWN_CHANCE = 0.1;
	private static final double GIANT_SPAWN_CHANCE = 0.2;
	private static final double WITHER_SKELETON_SPAWN_CHANCE = 0.5;
	static final float MAX_HEALTH = 100.0f;

	private int health;
	private String name;
	
	private transient BukkitTask task;
	
	public HCBoss(){
		
	}
	
	@Override
	public void respawn(){
		
		EntityType type = EntityType.fromId(getEntityId());
		
		setEntity((LivingEntity) getLocation().getWorld().spawnEntity(getLocation(), type));
		
		getEntity().setCustomName(name);
		
		
		task = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				for(Entity e : getEntity().getNearbyEntities(30, 30, 30)){
					if( e instanceof Player){
						BarAPI.displayBar((Player)e, name, health, 1, false);
					}
				}
				
			}
		}.runTaskTimer(Raid.UTIL.getPlugin(), 20, 20);
	}
	
	
	
}
