package com.aog.hcraid.save;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BukkitLocation implements Serializable{
	
	private static final long serialVersionUID = 5706465049309789523L;
	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;
	private String world;
	
	public BukkitLocation(Location location){
		x = location.getX();
		y = location.getY();
		z = location.getZ();
		pitch = location.getPitch();
		yaw = location.getYaw();
		world = location.getWorld().getName();
	}
	
	public Location getBukkitLocation(){
		
		Location location = new Location(Bukkit.getWorld(world), x, y, z);
		location.setPitch(pitch);
		location.setYaw(yaw);
		
		return location;
	}
	
}
