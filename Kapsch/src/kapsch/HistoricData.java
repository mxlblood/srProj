package kapsch;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;

import javax.xml.soap.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class HistoricData {
	private static Facility north;
	private static Segment longSegment;
	private static String date;
	private static String startTime;
	private static String endTime;
	private static String fullStartTime;
	private static String fullEndTime;
	private static String facilityID;
	private static String segmentID;
	
	private static int interval;
	
	//added
	private static String sensorID;
	private static Vector<String> sensorIDList= new Vector<String>();
	
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter a start date in format 'yyyy-MM-dd': ");
		date = sc.next();
		while(isThisDateValid(date, "yyyy-MM-dd")==false) {
			System.out.println("Error in date formatting.");
			System.out.println("Enter a date in format 'yyyy-MM-dd': ");
			date = sc.next();
		}
		
		System.out.println("Enter a start time in format 'HH:mm:ss': ");
		startTime = sc.next();
		while(isThisDateValid(startTime, "HH:mm:ss")==false) {
			System.out.println("Error in start time formatting.");
			System.out.println("Enter a start time in format 'HH:mm:ss': ");
			startTime = sc.next();
		}
		
		System.out.println("Enter an end time in format 'HH:mm:ss': ");
		endTime = sc.next();
		while(isThisDateValid(endTime, "HH:mm:ss")==false) {
			System.out.println("Error in end time formatting.");
			System.out.println("Enter an end time in format 'HH:mm:ss': ");
			endTime = sc.next();
		}
		
		//facilityID= 80
		System.out.println("Enter a facility ID: ");
		facilityID = sc.next();
		
		//segmentID=6
		System.out.println("Enter a segment ID: ");
		segmentID = sc.next();
		
		//added
		while(true){
			System.out.println("Enter a sensor ID or enter 'x' to escape: ");
			sensorID = sc.next();
			if(sensorID.equals("x"))
				break;
			else
				sensorIDList.add(sensorID);
		}
		
		sc.close();
		fullStartTime=(date+"T"+startTime);
		fullEndTime=(date+"T"+endTime);
		
		north = new Facility(80,"Northbound");
		longSegment = new Segment(6,"LongSegment");
		north.addSegment(longSegment);
		
		System.out.println(longSegment);
		
		GetHistoricSensorData(facilityID, segmentID, fullStartTime, fullEndTime);
		Vector<Sensor> testSet = getTestSet();
		
		for(int i=0; i<testSet.size(); i++) {
			Sensor current = testSet.get(i);
			double flow = calculateFlow(current.getVolume());
			double density = calculateDensity(flow, current.getSpeed());
			current.setFlow(flow);
			current.setDensity(density);
		}
		
		//System.out.println("\n"+longSegment);
		System.out.println("\n"+testSet);
	}
	
	public static Vector<Sensor> getTestSet() {
		Sensor sensor;
		Vector<Sensor> testSet= new Vector<Sensor>();
		for(int i=0;i<longSegment.getSensorsSize(); i++) {
			sensor = longSegment.getSensor(i);
			String currentSensor = Integer.toString(sensor.getSensorID());
			if (sensorIDList.contains(currentSensor)) {
				testSet.add(sensor);
			}	
		}
		//System.out.println(testSet);
		return testSet;
	}

	public static double calculateFlow(int volume) {
		double flow= (double) (3600*volume)/240;
		return flow;
	}
	
	public static double calculateDensity(double flow, double speed) {
		double density = flow/speed;
		return density;
	}

	public static boolean isThisDateValid(String dateToValidate, String dateFormat){
		if(dateToValidate == null){
			return false;
		}
		DateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setLenient(false);
		try {
			//if not valid, it will throw ParseException
			Date date = sdf.parse(dateToValidate);
			//System.out.println(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	public static void GetHistoricSensorData(String facilityID, String segmentID, String fullStartTime, String fullEndTime)
    {
        try {
            String soapEndpointUrl = "http://192.168.10.173:8080/dpe-services/DynamicPricingDataInterface";
            String soapAction = "http://services.dpe.its.telvent.com/getIntervalData";

            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            
            // SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("ser", "http://services.dpe.its.telvent.com");

            // SOAP Body
            SOAPBody soapBody = envelope.getBody();
            SOAPElement getIntervalDataElement = soapBody.addChildElement("getIntervalData", "ser");
            SOAPElement tollingSegmentDataElement = getIntervalDataElement.addChildElement("TollingSegmentIntervalDataRequest");
            SOAPElement tollingSegmentElement = tollingSegmentDataElement.addChildElement("TollingSegmentID");
            SOAPElement facilityElement = tollingSegmentElement.addChildElement("FacilityID");
            SOAPElement segmentElement = tollingSegmentElement.addChildElement("SegmentID");
            SOAPElement startTimeElement = tollingSegmentDataElement.addChildElement("StartTime");
            SOAPElement endTimeElement = tollingSegmentDataElement.addChildElement("EndTime");
            //facilityElement.addTextNode("80");
            //segmentElement.addTextNode("6");
            //startTimeElement.addTextNode("2017-10-17T08:00:00");
            //endTimeElement.addTextNode("2017-10-17T08:03:58");
            //String stringFacilityID= String.valueOf(facilityID);
            facilityElement.addTextNode(facilityID);
            //String stringSegmentID= String.valueOf(segmentID);
            segmentElement.addTextNode(segmentID);
            startTimeElement.addTextNode(fullStartTime);
            endTimeElement.addTextNode(fullEndTime);


            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", soapAction);
            soapMessage.saveChanges();

            // Call Service and return token
            SOAPMessage soapResponse = callSoapWebService(soapEndpointUrl, soapAction, soapMessage);
            FileOutputStream fileName=new FileOutputStream("inputData.xml");
            soapResponse.writeTo(fileName);
            readXML();
            //soapResponse.writeTo(System.out);
            //System.out.println(soapResponse.getSOAPBody().getFirstChild().getFirstChild().getLastChild().getTextContent());
        } catch (Exception e)
        {
            System.out.println("An error occured: " + e.toString());
        }
    }

    private static SOAPMessage callSoapWebService(String soapEndpointUrl, String soapAction, SOAPMessage soapMessage) throws Exception {
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        SOAPMessage soapResponse = soapConnection.call(soapMessage, soapEndpointUrl);
        soapConnection.close();
        return soapResponse;
    }
	
	public void parseData(int facilityListID, int segmentID) {
		//when parsing, search for facility and segmentID to create sensor data and put in segment
	}
	
	public static void readXML() {
		try {
			
//			Scanner scan = new Scanner(System.in);
//			String fileInput;
//			System.out.println("Enter a file name: ");
//			fileInput = scan.next();
//			scan.close();
			
			//File fXmlFile = new File("HistoricData.xml");
			//File fXmlFile = new File(fileInput);
			File fXmlFile = new File("inputData.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("LaneIntervalData");
			System.out.println("----------------------------");
	
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				
				System.out.println("\nCurrent Element : " + nNode.getNodeName());
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					System.out.println("Lane ID : " + eElement.getElementsByTagName("LaneID").item(0).getTextContent());
					String laneResult= eElement.getElementsByTagName("LaneID").item(0).getTextContent();
					int laneID = Integer.parseInt(laneResult);
					
					System.out.println("Sensor ID : " + eElement.getElementsByTagName("SensorID").item(0).getTextContent());
					String sensorResult= eElement.getElementsByTagName("SensorID").item(0).getTextContent();
					int sensorID = Integer.parseInt(sensorResult);
					
					System.out.println("Start Time : " + eElement.getElementsByTagName("LaneIntervalStart").item(0).getTextContent());
					String startResult= eElement.getElementsByTagName("LaneIntervalStart").item(0).getTextContent();
					SimpleDateFormat startSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX");
					SimpleDateFormat startOutput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX");
					Date startTime = startSDF.parse(startResult);
					String startFormattedTime = startOutput.format(startTime);
					//System.out.println(startFormattedTime);
					
					System.out.println("End Time : " + eElement.getElementsByTagName("LaneIntervalEnd").item(0).getTextContent());
					String endResult= eElement.getElementsByTagName("LaneIntervalEnd").item(0).getTextContent();		
					SimpleDateFormat endSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX");
					SimpleDateFormat endOutput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX");
					Date endTime = endSDF.parse(endResult);
					String endFormattedTime = endOutput.format(endTime);
					//System.out.println(endFormattedTime);
					
					System.out.println("Speed : " + eElement.getElementsByTagName("AverageSpeed").item(0).getTextContent());
					String speedResult= eElement.getElementsByTagName("AverageSpeed").item(0).getTextContent();
					double speed = Double.parseDouble(speedResult);
					
					System.out.println("Occupancy : " + eElement.getElementsByTagName("Occupancy").item(0).getTextContent());
					String occupancyResult= eElement.getElementsByTagName("Occupancy").item(0).getTextContent();
					Float occupancy = Float.parseFloat(occupancyResult);
					
					System.out.println("Volume : " + eElement.getElementsByTagName("Volume").item(0).getTextContent());
					String volumeResult= eElement.getElementsByTagName("Volume").item(0).getTextContent();
					int volume = Integer.parseInt(volumeResult);
					
					System.out.println("Small Count : " + eElement.getElementsByTagName("SmallCount").item(0).getTextContent());
					String smallResult= eElement.getElementsByTagName("SmallCount").item(0).getTextContent();
					int smallCount = Integer.parseInt(smallResult);
					
					System.out.println("Medium Count : " + eElement.getElementsByTagName("MediumCount").item(0).getTextContent());
					String mediumResult= eElement.getElementsByTagName("MediumCount").item(0).getTextContent();
					int mediumCount = Integer.parseInt(mediumResult);
					
					System.out.println("Large Count : " + eElement.getElementsByTagName("LargeCount").item(0).getTextContent());
					String largeResult= eElement.getElementsByTagName("LargeCount").item(0).getTextContent();
					int largeCount = Integer.parseInt(largeResult);
					
					Sensor sensor = new Sensor(sensorID, laneID, volume, speed, occupancy, startTime, endTime, smallCount, mediumCount, largeCount);
					longSegment.addSensor(sensor);	
					//search through list for sensorIDs
					
				}
				
			}
			
		    } catch (Exception e) {
			e.printStackTrace();
		    }	
	}
	
}
