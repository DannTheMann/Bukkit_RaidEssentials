package com.aog.hcraid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;

public class MaterialAlias {

	private File file;
	private HashMap<Material, ArrayList<String>> alias = new HashMap<>();

	public MaterialAlias(){
		file = new File(Raid.UTIL.getDirectory() + "Material Alias.txt");
		
		if(!file.exists()){
			populateFile();
		}else{
			load();
		}
		
	}

	private void load() {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			String line = br.readLine();
			
				while (line != null) {
					
					Material m = Material.valueOf(line.split(";")[0]);
					
					ArrayList<String> list = split(line.split(";")[1]);
					
					alias.put(m, list);
					
					line = br.readLine();
					
				}
				
			Raid.log("Finished loading the alias file.");
				
			br.close();
				
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

	public HashMap<Material, ArrayList<String>> getAlias(){
		return alias;
	}
	
	private void populateFile() {
		
		ArrayList<Material> list = new ArrayList<Material>();
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			
			for(Material m : Material.values()){
				
				if(list.contains(m)){
					continue;
				}
				
				String line = m + ";" + m.toString().toLowerCase().replaceAll("_", " ") + "";
				
				if(m.toString().split("_").length > 1){
					line += "," + m.toString().toLowerCase().replaceAll("_", "");
				}
				
				bw.write(line);
				bw.newLine();
				
				alias.put(m, split(line));
				
				list.add(m);
				
			}
			
			bw.close();
			
		} catch (IOException e) {
			Raid.log("Failed to populate Material Alias file!");
			e.printStackTrace();
		}
		
	}

	private ArrayList<String> split(String line) {
		
		String[] list = line.split(",");
		ArrayList<String> alist = new ArrayList<String>();
		
		for(int i = 0; i < list.length; i++){
			alist.add(list[i]);
		}
		
		return alist;
	}
	
}
