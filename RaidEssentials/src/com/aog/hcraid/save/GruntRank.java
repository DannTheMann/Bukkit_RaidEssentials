package com.aog.hcraid.save;

import java.io.Serializable;
import java.util.ArrayList;

public class GruntRank implements Serializable{

	private Grunt rank;
	private int blocksBroken;
	private int mobsKilled;
	private int playersKilled;
	private int hoursSpent;
	private int votes;
	private long joinTime;
	
	public GruntRank(long joinTime){
		rank = Grunt.LIMBO;
		this.joinTime = joinTime;
	}
	
	public void rankUp(){
		rank = rank.nextRank(rank);
	}
	
	public String getTitle(){
		return rank.title();
	}
	
	public boolean canRankUp(){
		
		updateHours();
		
		if(blocksBroken >= rank.blocksToBreak()
				&& mobsKilled >= rank.mobsToKill()
				&& playersKilled >= rank.playersToKill()
				&& hoursSpent >= rank.hoursRequired()
				&& votes >= rank.votesRequired()){
			return true;
		}else{
			return false;
		}
		
	}
	
	public ArrayList<String> getRequirementsForNextRank(){
		ArrayList<String> list = new ArrayList<>();
		
		list.add("Blocks To Break: " + (rank.blocksToBreak() - blocksBroken));
		list.add("Mobs To Kill: " + (rank.mobsToKill() - mobsKilled));
		list.add("Players To Kill: " + (rank.playersToKill() - playersKilled));
		list.add("Hours to Spend: " + (rank.hoursRequired() - hoursSpent));
		
		return list;
	}

	public void incremementVotes(){
		votes++;
	}
	
	public void updateHours() {
		
		int hours = (int) ((((System.currentTimeMillis() / 1000) / 60) / 60) - (((joinTime / 1000) / 60) / 60));
		
		this.hoursSpent = hours;
		
	}

}
