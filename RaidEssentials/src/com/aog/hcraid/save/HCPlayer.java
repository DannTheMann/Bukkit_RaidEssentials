package com.aog.hcraid.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.aog.hcraid.Raid;
import com.aog.hcraid.commands.CurrencyCommand;

public class HCPlayer implements Serializable {

	private static final long serialVersionUID = 1542553183065771755L;
	private String uniqueId;
	private ExchangeManagement management;
	//private boolean lookingAtOwnListedItems;
	//private boolean lookingToRemoveItems;
	//private boolean lookingAtExchangeItems;
	private GruntRank gruntPlus;
	private ArrayList<ExchangeItem> itemsBeingSold = new ArrayList<>();
	private ArrayList<String> messagesForReturn = new ArrayList<>();
	private ArrayList<SavedItem> itemsToReturn = new ArrayList<SavedItem>();
	private HashMap<Integer, SavedItem> inventorySave = new HashMap<Integer, SavedItem>();
	//private Inventory exchangeInventory;
	private int moneyToWithdraw;
	private int inventoryBalance;
	private int tradingAccount;

	public HCPlayer(Player p){
		Raid.UTIL.getUUID(p);
		uniqueId = p.getUniqueId().toString();
		gruntPlus = new GruntRank(p.getFirstPlayed());
		management = new ExchangeManagement();
	}
	
	public String getUniqueId(){
		return uniqueId;
	}
	
	public GruntRank getGrunt(){
		return gruntPlus;
	}
	
	public void addItemForSale(ExchangeItem ei){
		itemsBeingSold.add(ei);
		Raid.log("Added item for '" + Bukkit.getPlayer(UUID.fromString(uniqueId)).getName() + "'. Total Items being sold = " + itemsBeingSold.size());
	}
	
	public void removeItemForSale(ExchangeItem ei){
		itemsBeingSold.remove(ei);
	}
	
	public ArrayList<ExchangeItem> getItemsForSale(){
		return itemsBeingSold;
	}

	public void addItemToReturn(ItemStack bukkitItemStack) {
		
		if(bukkitItemStack == null ||bukkitItemStack.getAmount() <= 0 || bukkitItemStack.getType() == Material.AIR){
			return;
		}
		
		itemsToReturn.add(new SavedItem(bukkitItemStack));
	}
	
	public void returnItems(){
		
		for(SavedItem i : itemsToReturn){
			Raid.UTIL.returnItem(i.toBukkitItemStack(), Raid.UTIL.getPlayer(uniqueId));
		}
		
		itemsToReturn.clear();
		
	}

	public ExchangeManagement getManagement() {
		return management;
	}

	public void setManagement(ExchangeManagement management) {
		this.management = management;
	}

	public void addMoneyToWithdraw(int money) {
		moneyToWithdraw += money;
	}
	
	public int getMoneyToWithdraw(){
		return moneyToWithdraw;
	}

	public void setInventory(PlayerInventory inventory) {
		
		inventorySave = new HashMap<>();
		
		for(int i = 0; i < inventory.getSize(); i++){
			if((inventory.getItem(i) != null) && (inventory.getItem(i).getType() != Material.AIR)){
				inventorySave.put(i, new SavedItem(inventory.getItem(i)));
			}
		}
		
		
		inventory.clear();
		
		ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE);
		is.setDurability((short) 8);
		
		for(int i = 0; i < inventory.getSize(); i++){
			inventory.setItem(i, is);
		}
		
		//exchangeInventory = inventory;
	}
	
	@SuppressWarnings("deprecation")
	public void restoreInventory(final Player p){
		
		p.getInventory().clear();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if(p == null || !p.isOnline()){
					for(SavedItem is : inventorySave.values()){
						itemsToReturn.add(is);
					}
					return;
				}
				
				for(int i = 0; i < p.getInventory().getSize(); i++){
					p.getInventory().setItem(i, null);
				}
				
				for(int i : inventorySave.keySet()){
					p.getInventory().setItem(i, inventorySave.get(i).toBukkitItemStack());
				}
				
				for(SavedItem is : itemsToReturn){
					Raid.UTIL.givePlayerItem(p, is.toBukkitItemStack());
				}
				
				itemsToReturn.clear();
				
				Raid.log("size: " + inventorySave.size());
				
				inventorySave.clear();
				
				Raid.UTIL.payOutPlayer(p, getInventoryBalance());
				setInventoryBalance(0);
				p.updateInventory();
			}
		}.runTaskLater(Raid.UTIL.getPlugin(), 2);	
		
	}

	public void tax() {
		if(moneyToWithdraw > 2560){
			int tax = (moneyToWithdraw / 100) * 5;
			moneyToWithdraw = moneyToWithdraw - tax;
			addMessage(CurrencyCommand.CURRENCY_PREFIX + ChatColor.YELLOW + "2 days have passed, you've been taxed from" +
					Raid.UTIL.getTranslationForCurrency(tax) + ChatColor.YELLOW + 
					" your bank account.");
		}
	}

	public void addMessage(String string) {
		
		if(Raid.UTIL.getPlayer(this) != null && Raid.UTIL.getPlayer(this).isOnline()){
			message(string);
			return;
		}
		
		messagesForReturn.add(string);
	}
	
	public void printOfflineMessages(){
		for(String u : messagesForReturn){
			Raid.UTIL.getPlayer(this).sendMessage(u);
		}
	}

	public void withdrawMoney(int total) {
		
		moneyToWithdraw -= total;
		
		if(moneyToWithdraw < 0){
			moneyToWithdraw = 0;
		}
		
		Raid.UTIL.payOutPlayer(Raid.UTIL.getPlayer(uniqueId), total);
		
		message(CurrencyCommand.CURRENCY_PREFIX + ChatColor.GREEN + " You've withdrawed " +
				Raid.UTIL.getTranslationForCurrency(total));
		
	}

	public void message(String string) {
		Player p = Bukkit.getPlayer(UUID.fromString(uniqueId)) ;
		if(p != null){
			p.sendMessage(string);
		}
	}

	public ArrayList<ItemStack> getTemporaryInventory() {
		
		ArrayList<ItemStack> is = new ArrayList<ItemStack>();
		
		for(SavedItem si : inventorySave.values())
			is.add(si.toBukkitItemStack());
		
		return is;
		
	}

	public int getInventoryBalance() {
		return inventoryBalance;
	}

	public void setInventoryBalance(int inventoryBalance) {
		this.inventoryBalance = inventoryBalance;
	}

	public void setWithdrawMoney(int returnValue) {
		this.moneyToWithdraw = returnValue;
	}

	public void addMoneyToTraderAccount(int sellingCost) {
		tradingAccount += sellingCost;
	}
	
	public int getTraderAccount(){
		return tradingAccount;
	}
	
	public void clearTraderAccount(){
		tradingAccount = 0;
	}

	public void payOutTradeAccount() {
		
		final Player p = Raid.UTIL.getPlayer(this);
	
		if(tradingAccount <= 0){
			return;
		}
		
		p.sendMessage(CurrencyCommand.CURRENCY_PREFIX + ChatColor.RED + " WARNING.");
		p.sendMessage(ChatColor.YELLOW + " --- You will be paid VIA you're trading account in 30 seconds, if you're inventory is full move now to a safe location" +
				" or log off. You've been warned!");
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if(p == null || !p.isOnline()){
					return;
				}
				
				Raid.UTIL.payOutPlayer(p, tradingAccount);
				p.sendMessage(CurrencyCommand.CURRENCY_PREFIX + ChatColor.YELLOW + "Paid out VIA Trading Account - " + 
				Raid.UTIL.getTranslationForCurrency(tradingAccount));
				
				tradingAccount = 0;	
			}
		}.runTaskLater(Raid.UTIL.getPlugin(), 600);
		
	}
	
	/*
	public boolean isLookingAtOwnListedItems() {
		return lookingAtOwnListedItems;
	}

	public void setLookingAtOwnListedItems(boolean lookingAtOwnListedItems) {
		this.lookingAtOwnListedItems = lookingAtOwnListedItems;
	}

	public boolean isLookingAtExchangeItems() {
		return lookingAtExchangeItems;
	}

	public void setLookingAtExchangeItems(boolean lookingAtExchangeItems) {
		this.lookingAtExchangeItems = lookingAtExchangeItems;
	}

	public boolean isLookingToRemoveItems() {
		return lookingToRemoveItems;
	}

	public void setLookingToRemoveItems(boolean lookingToRemoveItems) {
		this.lookingToRemoveItems = lookingToRemoveItems;
	}
	*/
	
	
}
