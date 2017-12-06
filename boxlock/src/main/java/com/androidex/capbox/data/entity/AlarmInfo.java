package com.androidex.capbox.data.entity;

import java.io.Serializable;

public class AlarmInfo implements Serializable{
	private int id;
	private int isLostOpen;//防丢报警功能开启：0关闭，1 中等防丢，2高等防丢
	private int isKickOpen;//踢被子报警功能开启：0关闭，1开启
	private int isRealOpen;//实时温度开关：0关闭，1开启
	private int isFeverOpen;//发烧报警开关：0关闭，1开启
	private int isColdProof;//防寒穿衣报警开关：0关闭，1开启
	private int isTempIndicator;//温度指示灯开关：0关闭，1开启
	private int IsTempUnit;//温度单位：0 摄氏，1 华氏
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
	
	public int getIsRealOpen() {
		return isRealOpen;
	}

	public void setIsRealOpen(int isRealOpen) {
		this.isRealOpen = isRealOpen;
	}

	public int getIsFeverOpen() {
		return isFeverOpen;
	}

	public void setIsFeverOpen(int isFeverOpen) {
		this.isFeverOpen = isFeverOpen;
	}

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

	public int getIsLostOpen() {
		return isLostOpen;
	}

	public void setIsLostOpen(int isLostOpen) {
		this.isLostOpen = isLostOpen;
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
	
	public int getIsKickOpen() {
		return isKickOpen;
	}

	public void setIsKickOpen(int isKickOpen) {
		this.isKickOpen = isKickOpen;
	}

	public int getIsColdProof() {
		return isColdProof;
	}

	public void setIsColdProof(int isColdProof) {
		this.isColdProof = isColdProof;
	}
	
	public int getIsTempIndicator() {
		return isTempIndicator;
	}

	public void setIsTempIndicator(int isTempIndicator) {
		this.isTempIndicator = isTempIndicator;
	}

	public int getIsTempUnit() {
		return IsTempUnit;
	}

	public void setIsTempUnit(int isTempUnit) {
		IsTempUnit = isTempUnit;
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
				+ ", isLostOpen="+isLostOpen 
				+ ", isKickOpen="+isKickOpen
				+ ", isRealOpen="+isRealOpen
				+ ", isFeverOpen="+isFeverOpen
				+ ", isColdProof="+isColdProof
				+ ", isTempIndicator="+isTempIndicator
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
