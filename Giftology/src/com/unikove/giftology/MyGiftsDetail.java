package com.unikove.giftology;

import android.graphics.Bitmap;

public class MyGiftsDetail {
	
	public String ID; 
	public String getID() {
		return ID;
	}
	public void setID(String id) {
		ID = id;
	}
	
	public String Name; 
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}

	public String PRICE; 
	public String getPRICE() {
		return PRICE;
	}
	public void setPRICE(String price) {
		PRICE = price;
	}

	public String Small_Pic_URL; 
	public String getSmall_Pic_URL() {
		return Small_Pic_URL;
	}
	public void setSmall_Pic_URL(String url) {
		Small_Pic_URL=url;
	}
	
	public String Large_Pic_URL; 
	public String getLarge_Pic_URL() {
		return Large_Pic_URL;
	}
	public void setLarge_Pic_URL(String url) {
		Large_Pic_URL=url;
	}
	
	public Bitmap BITMAP; 
	public Bitmap getBITMAP() {
		return BITMAP;
	}
	public void setBITMAP(Bitmap bmp ) {
		BITMAP=bmp;
	}
	
	public String FROM; 
	public String getFROM() {
		return FROM;
	}
	public void setFROM(String from) {
		FROM=from;
	}
	
	public String EXPIRY; 
	public String getEXPIRY() {
		return EXPIRY;
	}
	public void setEXPIRY(String expiry) {
		EXPIRY=expiry;
	}
	
}
