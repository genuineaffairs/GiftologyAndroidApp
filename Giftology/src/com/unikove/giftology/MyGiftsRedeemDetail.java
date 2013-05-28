package com.unikove.giftology;

import android.graphics.Bitmap;

public class MyGiftsRedeemDetail {
	
	public String COUPONCODE; 
	public String getCOUPONCODE() {
		return COUPONCODE;
	}
	public void setCOUPONCODE(String couponcode) {
		COUPONCODE = couponcode;
	}
	
	public String TANDC; 
	public String getTANDC() {
		return TANDC;
	}
	public void setTANDC(String tandc) {
		TANDC = tandc;
	}

	public String REDEEMINST; 
	public String getREDEEMINST() {
		return REDEEMINST;
	}
	public void setREDEEMINST(String redeeminst) {
		REDEEMINST = redeeminst;
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
	
	public String MESSAGE; 
	public String getMESSAGE() {
		return MESSAGE;
	}
	public void setMESSAGE(String msg) {
		MESSAGE=msg;
	}
	
}
