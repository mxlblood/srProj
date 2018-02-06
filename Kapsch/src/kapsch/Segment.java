package kapsch;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Segment {
	private int segmentID;
	private String segmentName;
	private Vector<Sensor> sensorVector= new Vector<Sensor>();
	
	public String toString() {
		String temp= "";
		temp+= "Segment ID: "+segmentID+"\n";
		temp+= "Segment Name: "+segmentName+"\n";
		temp+= "Number of Sensors: " +sensorVector.size()+"\n";
		return temp;
	}
	
	public Segment(int theSegmentID, String theSegmentName) {
		this.setSegmentID(theSegmentID);
		this.setSegmentName(theSegmentName);
	}
	
	public int getSegmentID()
	{
		return this.segmentID;
	}
	
	private void setSegmentID(int theSegmentID) {
		this.segmentID = theSegmentID;
	}
	
	public String getSegmentName()
	{
		return this.segmentName;
	}
	
	private void setSegmentName(String theSegmentName) {
		this.segmentName = theSegmentName;
	}
	
	public void addSensor(Sensor sensor) {
		sensorVector.add(sensor);
	}
	
	public void removeSensor(Sensor sensor) {
		sensorVector.remove(sensor);
	}
	
}

