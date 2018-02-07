package kapsch;

import java.util.Date;

public class Sensor {
	private int sensorID;
	private int laneID;
	private int volume;
	private double speed;
	private float occupancy;
	private Date startTime;
	private Date endTime;
	private int smallCount;
	private int mediumCount;
	private int largeCount;
	private double density;
	private double flow;
	
	public String toString() {
		String temp= "";
		temp+= "Sensor ID: "+sensorID+"\n";
		temp+= "Lane ID: "+laneID+"\n";
		temp+= "Volume: "+volume+"\n";
		temp+= "Speed: "+speed+"\n";
		temp+= "Occupancy: "+occupancy+"\n";
		temp+= "Start Time: "+startTime+"\n";
		temp+= "End Time: "+endTime+"\n";
		temp+= "Small Count: "+smallCount+"\n";
		temp+= "Medium Count: "+mediumCount+"\n";
		temp+= "Large Count: "+largeCount+"\n";
		temp+= "Density: "+density+"\n";
		temp+= "Flow: "+flow+"\n";
		return temp;
	}
	
	public double getDensity()
	{
		return this.density;
	}
	
	public void setDensity(double theDensity) {
		this.density = theDensity;
	}
	
	public double getFlow()
	{
		return this.flow;
	}
	
	public void setFlow(double theFlow) {
		this.flow = theFlow;
	}
	
	public Sensor(int theSensorID, int theLaneID, int theVolume, double theSpeed, float theOccupancy, Date theStartTime, Date theEndTime, int theSmallCount, int theMediumCount, int theLargeCount) {
		this.setSensorID(theSensorID);
		this.setLane(theLaneID);
		this.setVolume(theVolume);
		this.setSpeed(theSpeed);
		this.setOccupancy(theOccupancy);
		this.setStartTime(theStartTime);
		this.setEndTime(theEndTime);
		this.setSmallCount(theSmallCount);
		this.setMediumCount(theMediumCount);
		this.setLargeCount(theLargeCount);
	}
	
	public int getLargeCount()
	{
		return this.largeCount;
	}
	
	private void setLargeCount(int theLargeCount) {
		this.largeCount = theLargeCount;
	}
	
	public int getMediumCount()
	{
		return this.mediumCount;
	}
	
	private void setMediumCount(int theMediumCount) {
		this.mediumCount = theMediumCount;
	}
	
	public int getSmallCount()
	{
		return this.smallCount;
	}
	
	private void setSmallCount(int theSmallCount) {
		this.smallCount = theSmallCount;
	}
	
	public Date getEndTime()
	{
		return this.endTime;
	}
	
	private void setEndTime(Date theEndTime) {
		this.endTime = theEndTime;
	}
	
	public Date getStartTime()
	{
		return this.startTime;
	}
	
	private void setStartTime(Date theStartTime) {
		this.startTime = theStartTime;
	}
	
	public float getOccupancy()
	{
		return this.occupancy;
	}
	
	private void setOccupancy(float theOccupancy) {
		this.occupancy = theOccupancy;
	}
	
	public double getSpeed()
	{
		return this.speed;
	}
	
	private void setSpeed(double theSpeed) {
		this.speed = theSpeed;
	}
	
	public int getVolume()
	{
		return this.volume;
	}
	
	private void setVolume(int theVolume) {
		this.volume = theVolume;
	}
	
	public int getLaneID()
	{
		return this.laneID;
	}
	
	private void setLane(int theLaneID) {
		this.laneID = theLaneID;
	}
	
	public int getSensorID()
	{
		return this.sensorID;
	}
	
	private void setSensorID(int theSensorID) {
		this.sensorID = theSensorID;
	}
	
	
}
