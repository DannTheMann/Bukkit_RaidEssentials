package com.aog.hcraid.serial_file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.aog.hcraid.Raid;
import com.aog.hcraid.save.HCPlayer;
import com.aog.hcraid.save.RaidData;

public class SaveData {
	
	public static boolean savePlayerFile(HCPlayer player){
	      try
	      	{
	         FileOutputStream fileOut =
	         new FileOutputStream(Raid.UTIL.getPlayerDirectory() + player.getUniqueId() + ".ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(player);
	         out.close();
	         fileOut.close();
	         Raid.log("Succesfully saved '" + player.getUniqueId() + "' file.");
	         return true;
	      }catch(IOException i){
	    	  Raid.log("An error occured while trying to serialize the object '" + player.getUniqueId() + "'.");
	          i.printStackTrace();
	          return false;
	      	}
	}
	
	public static HCPlayer loadPlayerFile(String uuid){
		
		HCPlayer data = null;
		
	      try
	      {
	    	  if(!new File(Raid.UTIL.getDirectory() + uuid + ".ser").exists())
	    		  return new HCPlayer(Bukkit.getPlayer(UUID.fromString(uuid)));
	    	  
	         FileInputStream fileIn = new FileInputStream(Raid.UTIL.getDirectory() + uuid + ".ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         data = (HCPlayer) in.readObject();
	         in.close();
	         fileIn.close();
	         Raid.log("Succesfully loaded '" + uuid + "' file.");
	         return data;
	      }catch(IOException i){
	         i.printStackTrace();
	         return null;
	      }catch(ClassNotFoundException c){
	    	 Raid.log("Failed to load '" + uuid + "'.");
	    	 c.printStackTrace();
	         return null;
	      }
		
	}
	
	public static boolean saveRaidData(RaidData data){
		
	      try
	      	{
	         FileOutputStream fileOut =
	         new FileOutputStream(Raid.UTIL.getDirectory() + "RaidData.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(data);
	         out.close();
	         fileOut.close();
	         Raid.log("Succesfully saved 'RaidData' file.");
	         return true;
	      }catch(IOException i){
	    	  Raid.log("An error occured while trying to serialize the object 'RaidData'.");
	          i.printStackTrace();
	          return false;
	      	}	
		
	}

	public static RaidData loadRaidDate() {

		RaidData data = null;
		
	      try
	      {
	    	  if(!new File(Raid.UTIL.getDirectory() + "RaidData.ser").exists()){
	    		  Raid.log("Creating new Raid data.");
	    		  return new RaidData();
	    	  }
	    	  
	         FileInputStream fileIn = new FileInputStream(Raid.UTIL.getDirectory() + "RaidData.ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         Raid.log("Loading file from directory - " + Raid.UTIL.getDirectory() + "RaidData.ser");
	         data = (RaidData) in.readObject();
	         in.close();
	         fileIn.close();
	         Raid.log("Succesfully loaded 'RaidData' file.");
	         
	         if(data == null){
	        	 Raid.log("Data was null! Creating a new RaidData object.");
	    		  return new RaidData();
	         }
	         
	         return data;
	      }catch(Exception i){
	    	  i.printStackTrace();
	         return null;
	      }
		
	}

}
