package kapsch;

import java.util.ArrayList;
import java.util.List;

public class Facility {
	private int facilityID;
	private String facilityName;
	private List<Segment> segmentList= new ArrayList<Segment>();
	
	public String toString() {
		String temp= "";
		temp+= "Facility ID: "+facilityID+"\n";
		temp+= "Facility Name: "+facilityName+"\n";
		return temp;
	}
	
	public Facility(int theFacilityID, String theFacilityName) {
		this.setFacilityID(theFacilityID);
		this.setFacilityName(theFacilityName);
	}
	
	private int getFacilityID() {
		return this.facilityID;
	}

	private void setFacilityID(int theFacilityID) {
		this.facilityID = theFacilityID;
	}
	
	private String getFacilityName() {
		return this.facilityName;
	}

	private void setFacilityName(String theFacilityName) {
		this.facilityName = theFacilityName;
	}
	
	public void addSegment(Segment segment) {
		segmentList.add(segment);
	}
	
	public void removeSegment(Segment segment) {
		segmentList.remove(segment);
	}
	
}
