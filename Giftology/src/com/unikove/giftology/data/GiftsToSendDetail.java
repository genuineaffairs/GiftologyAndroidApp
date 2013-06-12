package com.unikove.giftology.data;

import android.graphics.Bitmap;

public class GiftsToSendDetail {

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

	public String MAXPRICE;

	public String getMAXPRICE() {
		return MAXPRICE;
	}

	public void setMAXPRICE(String price) {
		MAXPRICE = price;
	}

	public String MINPRICE;

	public String getMINPRICE() {
		return MINPRICE;
	}

	public void setMINPRICE(String minprice) {
		MINPRICE = minprice;
	}

	public String MINVALUE;

	public String getMINVALUE() {
		return MINVALUE;
	}

	public void setMINVALUE(String minvalue) {
		MINVALUE = minvalue;
	}

	public String Small_Pic_URL;

	public String getSmall_Pic_URL() {
		return Small_Pic_URL;
	}

	public void setSmall_Pic_URL(String url) {
		Small_Pic_URL = url;
	}

	public String Large_Pic_URL;

	public String getLarge_Pic_URL() {
		return Large_Pic_URL;
	}

	public void setLarge_Pic_URL(String url) {
		Large_Pic_URL = url;
	}

	public Bitmap BITMAP;

	public Bitmap getBITMAP() {
		return BITMAP;
	}

	public void setBITMAP(Bitmap bmp) {
		BITMAP = bmp;
	}

	public String TERMSCONDITION;

	public String getTERMSCONDITION() {
		return TERMSCONDITION;
	}

	public void setTERMSCONDITION(String termscondition) {
		TERMSCONDITION = termscondition;
	}

	public String VALIDITY;

	public String getVALIDITY() {
		return VALIDITY;
	}

	public void setVALIDITY(String validity) {
		VALIDITY = validity;
	}

	public String ABOUTPRODUCT;

	public String getABOUTPRODUCT() {
		return ABOUTPRODUCT;
	}

	public void setABOUTPRODUCT(String aBOUTPRODUCT) {
		ABOUTPRODUCT = aBOUTPRODUCT;
	}

	@Override
	public String toString() {
		return "GiftsToSendDetail [ID=" + ID + ", Name=" + Name + ", MAXPRICE="
				+ MAXPRICE + ", MINPRICE=" + MINPRICE + ", MINVALUE="
				+ MINVALUE + ", Small_Pic_URL=" + Small_Pic_URL
				+ ", Large_Pic_URL=" + Large_Pic_URL + ", BITMAP=" + BITMAP
				+ ", TERMSCONDITION=" + TERMSCONDITION + ", VALIDITY="
				+ VALIDITY + ", ABOUTPRODUCT=" + ABOUTPRODUCT + "]";
	}

}
