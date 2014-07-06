package com.aog.hcraid.save;

public enum Grunt {
	
	LIMBO("Limbo ", 0, 0, 0, 0, 0),
	ROOKIE("Rookie I", 0, 0, 0, 0, 0),
	MEMBER("Member II", 100, 10, 5, 1, 1),
	NOVICE_RAIDER("Novice Raider III", 200, 20, 15, 3, 5),
	RAIDER("Junoir Raider IV", 500, 75, 30, 6, 20),
	EXPERIENCED_RAIDER(" V ", 1200, 200, 75, 12, 50),
	MASTER_RAIDER("Master Raider VI", 3000, 500, 150, 20, 100);
	
	private String title;
	private int blocksToBreak;
	private int mobsToKill;
	private int playersToKill;
	private int hoursRequired;
	private int votesMade;
	
	Grunt(String title, int blocksToBreak, int mobsToKill, int playersToKill
			, int hoursRequired, int votesMade){
		this.title = title;
		this.blocksToBreak = blocksToBreak;
		this.mobsToKill = mobsToKill;
		this.playersToKill = playersToKill;
		this.hoursRequired = hoursRequired;
		this.votesMade = votesMade;
	}
	
	int blocksToBreak(){
		return blocksToBreak;
	}
	
	int mobsToKill(){
		return mobsToKill;
	}
	
	int playersToKill(){
		return playersToKill;
	}
	
	int hoursRequired(){
		return hoursRequired;
	}
	
	int votesRequired(){
		return votesMade;
	}
	
	String title(){
		return title;
	}
	
	Grunt nextRank(Grunt rank){
		
		boolean isNext = false;
		
		for(Grunt g : Grunt.values()){
			
			if(isNext){
				return g;
			}
			
			if(g == rank){
				isNext = true;
			}
			
		}
		
		return null;
		
	}
	

}
