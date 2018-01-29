package com.androidex.capbox.data.entity;

import java.io.Serializable;

public class AlarmInfo implements Serializable{
	private int id;
	private float temp;//发烧报警温度
	private float coldTemp;//防寒穿衣报警温度
	private int isLost;//防丢报警方式：0震动，1铃声，2震动+铃声
	private String mLostRingtoneName;//防丢报警铃声音乐名
	private String mLostRingtoneUri;//防丢报警铃声Uri
	private int isFever;//发烧报警方式：0震动，1铃声，2震动+铃声
	private String mFeverRingtoneName;//发烧报警铃声音乐名
	private String mFeverRingtoneUri;//发烧报警铃声Uri
	private int isKickAQuilt;//踢被子报警方式：0震动，1铃声，2震动+铃声
	private String mKickAQuiltRingtoneName;//踢被子报警铃声音乐名
	private String mKickAQuiltRingtoneUri;//踢被子报警铃声Uri

	public String getmLostRingtoneName() {
		return mLostRingtoneName;
	}

	public void setmLostRingtoneName(String mLostRingtoneName) {
		this.mLostRingtoneName = mLostRingtoneName;
	}

	public String getmFeverRingtoneName() {
		return mFeverRingtoneName;
	}

	public void setmFeverRingtoneName(String mFeverRingtoneName) {
		this.mFeverRingtoneName = mFeverRingtoneName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getTemp() {
		return temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}

	public int getIsLost() {
		return isLost;
	}

	public void setIsLost(int isLost) {
		this.isLost = isLost;
	}

	public String getmLostRingtoneUri() {
		return mLostRingtoneUri;
	}

	public void setmLostRingtoneUri(String mLostRingtoneUri) {
		this.mLostRingtoneUri = mLostRingtoneUri;
	}

	public int getIsFever() {
		return isFever;
	}

	public void setIsFever(int isFever) {
		this.isFever = isFever;
	}

	public String getmFeverRingtoneUri() {
		return mFeverRingtoneUri;
	}

	public void setmFeverRingtoneUri(String mFeverRingtoneUri) {
		this.mFeverRingtoneUri = mFeverRingtoneUri;
	}

	public int getIsKickAQuilt() {
		return isKickAQuilt;
	}

	public void setIsKickAQuilt(int isKickAQuilt) {
		this.isKickAQuilt = isKickAQuilt;
	}

	public String getmKickAQuiltRingtoneName() {
		return mKickAQuiltRingtoneName;
	}

	public void setmKickAQuiltRingtoneName(String mKickAQuiltRingtoneName) {
		this.mKickAQuiltRingtoneName = mKickAQuiltRingtoneName;
	}

	public String getmKickAQuiltRingtoneUri() {
		return mKickAQuiltRingtoneUri;
	}

	public void setmKickAQuiltRingtoneUri(String mKickAQuiltRingtoneUri) {
		this.mKickAQuiltRingtoneUri = mKickAQuiltRingtoneUri;
	}

	public float getColdTemp() {
		return coldTemp;
	}

	public void setColdTemp(float coldTemp) {
		this.coldTemp = coldTemp;
	}

	@Override
	public String toString() {
		return "UserInfo [id=" + id
				+ ", temp=" + temp 
				+ ", coldTemp=" + coldTemp 
				+ ", isLost=" + isLost 
				+ ", mLostRingtoneName="+mLostRingtoneName 
				+ ", mLostRingtoneUri="+mLostRingtoneUri 
				+ ", isFever="+isFever 
				+ ", mFeverRingtoneName="+mFeverRingtoneName 
				+ ", mFeverRingtoneUri="+mFeverRingtoneUri 
				+ ", isKickAQuilt="+isKickAQuilt 
				+ ", mKickAQuiltRingtoneName="+mKickAQuiltRingtoneName 
				+ ", mKickAQuiltRingtoneUri="+mKickAQuiltRingtoneUri 
				+ "]";
	}
	
}
