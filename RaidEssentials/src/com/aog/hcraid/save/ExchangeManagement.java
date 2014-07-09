package com.aog.hcraid.save;

import java.io.Serializable;

public class ExchangeManagement implements Serializable{

	private static final long serialVersionUID = -6046334022384534074L;
	
	private transient boolean lookingAtOwnListedItems;
	private transient boolean lookingAtExchangeItems;
	private transient boolean lookingAtRemovingItems;
	private transient int pageNumber;
	
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
	public boolean isLookingAtRemovingItems() {
		return lookingAtRemovingItems;
	}
	public void setLookingAtRemovingItems(boolean lookingAtRemovingItems) {
		this.lookingAtRemovingItems = lookingAtRemovingItems;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public void incrementPageNumber(){
		this.pageNumber++;
	}
	public void decrementPageNumber(){
		this.pageNumber--;
	}

}
