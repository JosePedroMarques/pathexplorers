package utils;

import pathfinder.ArmyPathFinderModel.MapName;

public class Config {

	private float EMPTYWEIGHT;
	private float UNKOWNWEIGHT;
	private float DISPERSIONWEIGHT;
	private boolean VERBOSE;
	private int TIMEOUT;
	private int live_robot;
	private int communication_Range;
	private int sight_Range;
	private MapName mapName;
	private int radioBattery;

	public Config(int live_robot, float EMPTYWEIGHT, float UNKOWNWEIGHT,
			float DISPERSIONWEIGHT, int communication_Range, int sight_Range,
			int TIMEOUT,boolean VERBOSE, MapName mapName, int radioBattery) {
		
		this.live_robot = live_robot;
		this.EMPTYWEIGHT = EMPTYWEIGHT;
		this.UNKOWNWEIGHT = UNKOWNWEIGHT;
		this.DISPERSIONWEIGHT = DISPERSIONWEIGHT;
		this.communication_Range = communication_Range;
		this.sight_Range = sight_Range;
		this.TIMEOUT = TIMEOUT;
		this.mapName = mapName;
		this.radioBattery = radioBattery;

	}

	/**
	 * @return the eMPTYWEIGHT
	 */
	public float getEMPTYWEIGHT() {
		return EMPTYWEIGHT;
	}

	/**
	 * @param eMPTYWEIGHT the eMPTYWEIGHT to set
	 */
	public void setEMPTYWEIGHT(float eMPTYWEIGHT) {
		EMPTYWEIGHT = eMPTYWEIGHT;
	}

	/**
	 * @return the uNKOWNWEIGHT
	 */
	public float getUNKOWNWEIGHT() {
		return UNKOWNWEIGHT;
	}

	/**
	 * @param uNKOWNWEIGHT the uNKOWNWEIGHT to set
	 */
	public void setUNKOWNWEIGHT(float uNKOWNWEIGHT) {
		UNKOWNWEIGHT = uNKOWNWEIGHT;
	}

	/**
	 * @return the dISPERSIONWEIGHT
	 */
	public float getDISPERSIONWEIGHT() {
		return DISPERSIONWEIGHT;
	}

	/**
	 * @param dISPERSIONWEIGHT the dISPERSIONWEIGHT to set
	 */
	public void setDISPERSIONWEIGHT(float dISPERSIONWEIGHT) {
		DISPERSIONWEIGHT = dISPERSIONWEIGHT;
	}

	/**
	 * @return the vERBOSE
	 */
	public boolean isVERBOSE() {
		return VERBOSE;
	}

	/**
	 * @param vERBOSE the vERBOSE to set
	 */
	public void setVERBOSE(boolean vERBOSE) {
		VERBOSE = vERBOSE;
	}

	/**
	 * @return the tIMEOUT
	 */
	public int getTIMEOUT() {
		return TIMEOUT;
	}

	/**
	 * @param tIMEOUT the tIMEOUT to set
	 */
	public void setTIMEOUT(int tIMEOUT) {
		TIMEOUT = tIMEOUT;
	}

	/**
	 * @return the live_robot
	 */
	public int getLive_robot() {
		return live_robot;
	}

	/**
	 * @param live_robot the live_robot to set
	 */
	public void setLive_robot(int live_robot) {
		this.live_robot = live_robot;
	}

	/**
	 * @return the communication_Range
	 */
	public int getCommunication_Range() {
		return communication_Range;
	}

	/**
	 * @param communication_Range the communication_Range to set
	 */
	public void setCommunication_Range(int communication_Range) {
		this.communication_Range = communication_Range;
	}

	/**
	 * @return the sight_Range
	 */
	public int getSight_Range() {
		return sight_Range;
	}

	/**
	 * @param sight_Range the sight_Range to set
	 */
	public void setSight_Range(int sight_Range) {
		this.sight_Range = sight_Range;
	}

	/**
	 * @return the filename
	 */
	public MapName getMapName() {
		return mapName;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setMapName(MapName mapName) {
		this.mapName = mapName;
	}

	public int getRadioBattery() {
		// TODO Auto-generated method stub
		return radioBattery;
	}
	
	public void setRadioBattery(int rb){
		this.radioBattery = rb;
	}

}
