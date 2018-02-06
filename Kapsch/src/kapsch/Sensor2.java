package kapsch;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Sensor2 {
//	String start = "2017-10-10T10:00:00.000000000-05:00";
//    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX");
//    String dNow= ft.format(start);
//    Date s = ft.parse(start);
    
	private int sensorID;
	private int laneID;
	private int volume;
	private double speed;
	private float occupancy;
	private Date startTime;
	private Date endTime;

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
		temp+= "Density: "+density+"\n";
		temp+= "Flow: "+flow+"\n";
		return temp;
	}
	
	public double calculateDensity(double flow, double Speed) {
		density = flow/Speed;
		return density;
	}
	
	public double getDensity()
	{
		return this.density;
	}
	
	private void setDensity(double theDensity) {
		this.density = theDensity;
	}
	
	public double calculateFlow(int Volume) {
		flow= Volume/60;
		return flow;
	}
	
	public double getFlow()
	{
		return this.flow;
	}
	
	private void setFlow(double theFlow) {
		this.flow = theFlow;
	}
	
	public Sensor2(int theSensorID, int theLaneID, int theVolume, double theSpeed, float theOccupancy, Date theStartTime, Date theEndTime, double theFlow, double theDensity) {
		this.setSensorID(theSensorID);
		this.setLane(theLaneID);
		this.setVolume(theVolume);
		this.setSpeed(theSpeed);
		this.setOccupancy(theOccupancy);
		this.setStartTime(theStartTime);
		this.setEndTime(theEndTime);
		this.setFlow(theFlow);
		this.setDensity(theDensity);
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
