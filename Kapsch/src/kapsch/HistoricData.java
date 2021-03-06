package kapsch;

import java.io.BufferedReader;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
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

import java.io.*;
import org.json.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TrainingSetImport;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.core.learning.SupervisedLearning;

public class HistoricData {
	private static Facility north;
	private static Segment longSegment;
	private static String date;
	private static String startTime;
	private static String endTime;
	private static String fullStartTime;
	private static String fullEndTime;
	private static String facilityID;
	private static String facilityDescription;
	private static String segmentID;
	private static String segmentDescription;
	private static int interval;
	private static int dataCounter;
	
	private static Calendar interval4;
	private static Calendar interval8;
	private static Calendar interval12;
	private static Calendar interval16;
	
	//added
	private static String sensorID;
	private static Vector<String> sensorIDList= new Vector<String>();
	
	private static ArrayList<Integer> sensorIDInputList= new ArrayList<Integer>();
	
	public static void main(String[] args) {
		File configFile = new File("config.properties");
		try {
		    FileReader reader = new FileReader(configFile);
		    Properties props = new Properties();
		    props.load(reader);
		    
		    dataCounter = Integer.parseInt(props.getProperty("dataCounter"));

		    facilityID = props.getProperty("facilityID");
		    facilityDescription = props.getProperty("facilityDescription");
		    segmentID = props.getProperty("segmentID");
		    segmentDescription = props.getProperty("segmentDescription");
		    String sensorIDs = props.getProperty("sensorIDs");
		    String[] convertedSensorIDList = sensorIDs.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
		    for (int i = 0; i < convertedSensorIDList.length; i++) {
		        try {
		        		sensorID = convertedSensorIDList[i];
		        		sensorIDList.add(sensorID);
//		        		int tempIntSensor = Integer.parseInt(sensorID);
//		        		added to be able to get previous sensors
//		        		tempIntSensor = tempIntSensor-convertedSensorIDList.length;
//		        		String tempStringSensor = String.valueOf(tempIntSensor);
//		        		sensorIDList.add(tempStringSensor);
		        } catch (NumberFormatException nfe) {
		        };
		    }
		    
		    String startDate = props.getProperty("startDate");
		    if(isThisDateValid(startDate, "yyyy-MM-dd")==false) {
	    			System.out.println("Error in date formatting");
		    }
		    
		    String startTimeResult = props.getProperty("startTime");
		    if(isThisDateValid(startTimeResult, "HH:mm:ss")==false) {
		    		System.out.println("Error in start time formatting");
		    }
		    
		    Calendar theEndTime = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			theEndTime.setTime(sdf.parse(startTimeResult));
		    
			//interval is duration
			//number of intervals 
		    interval = Integer.parseInt(props.getProperty("interval"));
		    if(interval == 1){
		    		theEndTime.add(Calendar.MINUTE, 4);
		    }
		    else if(interval == 2) {
		    		theEndTime.add(Calendar.MINUTE, 8);
		    }
		    else if(interval == 3) {
		    		theEndTime.add(Calendar.MINUTE, 12);
		    }
		    else if(interval == 4) {
		    		theEndTime.add(Calendar.MINUTE, 16);
		    }
		    else {
		    		System.out.println("Error in interval length");
		    }
		    
		    fullStartTime=(startDate+"T"+startTimeResult);
		    String endFormattedTime = sdf.format(theEndTime.getTime());
		    fullEndTime=(startDate+"T"+endFormattedTime);
		    
		    System.out.print("Facility ID: " + facilityID+"\n");
		    System.out.print("Segment ID: " + segmentID+"\n");
		    System.out.print("Sensors: " + sensorIDList+"\n");
		    System.out.print("Start Time: " + fullStartTime +"\n");
		    System.out.print("End Time: " + fullEndTime +"\n");
		    System.out.print("Interval: " + interval+"\n");
		    reader.close();
		} catch (FileNotFoundException ex) {
		    // file does not exist
		} catch (IOException ex) {
		    // I/O error
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//converting strings to ints to use in constructors
		int facilityIDint = Integer.parseInt(facilityID);
		int segmentIDint = Integer.parseInt(segmentID);
		
		north = new Facility(facilityIDint, facilityDescription);
		longSegment = new Segment(segmentIDint, segmentDescription);
		north.addSegment(longSegment);
		
		//System.out.println("\n"+longSegment);
		
		for(int testSetCounter = 0; testSetCounter<dataCounter; testSetCounter++) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Calendar startTimeIncrement = Calendar.getInstance();
			Calendar endTimeIncrement = Calendar.getInstance();
			try {
				startTimeIncrement.setTime(sdf.parse(fullStartTime));
				endTimeIncrement.setTime(sdf.parse(fullEndTime));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(testSetCounter!=0) {
				startTimeIncrement.add(Calendar.MINUTE, 20);
				endTimeIncrement.add(Calendar.MINUTE, 20);
				}
			Date startDate = startTimeIncrement.getTime();
			fullStartTime = sdf.format(startDate);
			Date endDate = endTimeIncrement.getTime();
			fullEndTime = sdf.format(endDate);
			System.out.println(fullStartTime);
			System.out.println(fullEndTime);
			GetHistoricSensorData(facilityID, segmentID, fullStartTime, fullEndTime);
			System.out.println("----------------Test Set-------------------");
			Vector<Sensor> testSet = getTestSet();
			System.out.println("Finished getting test set");
		
		
		for(int i=0; i<testSet.size(); i++) {
			Sensor current = testSet.get(i);
			double flow = calculateFlow(current.getVolume());
			double density = calculateDensity(flow, current.getSpeed());
			current.setFlow(flow);
			current.setDensity(density);
		}
		
		//System.out.println("\n"+longSegment);
//		for (int k = 0; k<testSet.size(); k++) {
//			System.out.println(testSet.elementAt(k).toString());
//		}
		
		for (int j = 0; j<longSegment.getSensorsSize(); j++) {
			String temp = Integer.toString(longSegment.getSensor(j).getSensorID());
			if(sensorIDList.contains(temp)) {
				longSegment.getSensor(j).toString();
			}
		}

		//Begin Hash Map Code
		int sensorCount = testSet.size();
		int sensorIDListCount = sensorIDList.size();
		ArrayList<Double> T1 = new ArrayList<Double>();
		T1.add(0.0);
		T1.add(0.0);
		T1.add(0.0);
		ArrayList<Double> T2 = new ArrayList<Double>();
		T2.add(0.0);
		T2.add(0.0);
		T2.add(0.0);
		ArrayList<Double> T3 = new ArrayList<Double>();
		T3.add(0.0);
		T3.add(0.0);
		T3.add(0.0);
		ArrayList<Double> T4 = new ArrayList<Double>();
		T4.add(0.0);
		T4.add(0.0);
		T4.add(0.0);
		ArrayList<Double> T5 = new ArrayList<Double>();
		T5.add(0.0);
		T5.add(0.0);
		T5.add(0.0);
		
		//Calendar startDateTimePlusInterval = Calendar.getInstance();
		//startDateTimePlusInterval.setTime(sdf.parse(fullStartTime));
		//startDateTimePlusInterval.add(Calendar.MINUTE, 4);
		try {
		interval4 = Calendar.getInstance();
		interval4.setTime(sdf.parse(fullStartTime));
		interval4.add(Calendar.MINUTE, 4);
		
		interval8 = Calendar.getInstance();
		interval8.setTime(sdf.parse(fullStartTime));
		interval8.add(Calendar.MINUTE, 8);
		
		interval12 = Calendar.getInstance();
		interval12.setTime(sdf.parse(fullStartTime));
		interval12.add(Calendar.MINUTE, 12);
		
		interval16 = Calendar.getInstance();
		interval16.setTime(sdf.parse(fullStartTime));
		interval16.add(Calendar.MINUTE, 16);
		}
		catch (Exception e) {
			e.printStackTrace();
		    }
		
		System.out.println(sensorCount);
		//System.out.println(testSet);
		while(sensorCount>0) {
			try {
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			
			//change back to getEndTime()
			Date currentDateTime = testSet.get(sensorCount-1).getStartTime();
			Calendar currentEndTime = Calendar.getInstance();
			currentEndTime.setTime(currentDateTime);
			
			Calendar startDateTime = Calendar.getInstance();
			startDateTime.setTime(sdf.parse(fullStartTime));
			
			int currentSensorID = testSet.get(sensorCount-1).getSensorID();
			int sensorIDFromList = Integer.parseInt(sensorIDList.get(sensorIDListCount-sensorIDListCount));
			double currentFlow = testSet.get(sensorCount-1).getFlow();

			//System.out.println(startDateTime.getTime());
			//System.out.println(currentEndTime.getTime());
			//System.out.println(interval4.getTime());
			
			if(currentEndTime.before(interval4) && currentEndTime.after(startDateTime) || currentEndTime.equals(startDateTime) ) {
				if(currentSensorID == sensorIDFromList){
					T1.set(0, currentFlow);
					sensorCount--;
				}
				else if(currentSensorID == (sensorIDFromList+1)){
					T1.set(1, currentFlow);
					sensorCount--;
				}
				else if(currentSensorID == (sensorIDFromList+2)){
					T1.set(2, currentFlow);
					sensorCount--;
				}
			}
			else if(currentEndTime.before(interval8) && currentEndTime.after(interval4) || currentEndTime.equals(interval4) ) {
				if(currentSensorID == sensorIDFromList){
					T2.set(0, currentFlow);
					sensorCount--;
				}
				else if(currentSensorID == (sensorIDFromList+1)){
					T2.set(1, currentFlow);
					sensorCount--;
				}
				else if(currentSensorID == (sensorIDFromList+2)){
					T2.set(2, currentFlow);
					sensorCount--;
				}
			}
			else if(currentEndTime.before(interval12) && currentEndTime.after(interval8) || currentEndTime.equals(interval8) ) {
				if(currentSensorID == sensorIDFromList){
					T3.set(0, currentFlow);
					sensorCount--;
				}
				else if(currentSensorID == (sensorIDFromList+1)){
					T3.set(1, currentFlow);
					sensorCount--;
				}
				else if(currentSensorID == (sensorIDFromList+2)){
					T3.set(2, currentFlow);
					sensorCount--;
				}
			}
			else if(currentEndTime.before(interval16) && currentEndTime.after(interval12) || currentEndTime.equals(interval12) ) {
				if(currentSensorID == sensorIDFromList){
					T4.set(0, currentFlow);
					sensorCount--;
				}
				else if(currentSensorID == (sensorIDFromList+1)){
					T4.set(1, currentFlow);
					sensorCount--;
				}
				else if(currentSensorID == (sensorIDFromList+2)){
					T4.set(2, currentFlow);
					sensorCount--;
				}
			}
			else {
				sensorCount--;
				//testSet gets bigger each time because it is based off of long segment which is added to each time the for loop iterates
			}
			}
			catch (Exception e) {
				e.printStackTrace();
			    }	
		}//end Hash Map code
		System.out.println("___| " + sensorIDList.get(sensorIDListCount-sensorIDListCount) + " | " +  sensorIDList.get(sensorIDListCount-(sensorIDListCount-1)) + " | " + sensorIDList.get(sensorIDListCount-(sensorIDListCount-2)));
		System.out.println("T1 |" + T1.get(0) + "|" + T1.get(1) + "|" + T1.get(2));
		System.out.println("T2 |     |" + T2.get(1) + "|" + T2.get(2));
		System.out.println("T3 |     |     |" + T3.get(2));
		//System.out.println("T4 |" + T4.get(0) + "|" + T4.get(1) + "|" + T4.get(2));
		System.out.println("T4 |     |     |" + T4.get(2));
		
		ArrayList<Double> T1normal = normalizeData(T1);
		ArrayList<Double> T2normal = normalizeData(T2);
		ArrayList<Double> T3normal = normalizeData(T3);
		ArrayList<Double> T4normal = normalizeData(T4);
		System.out.println(T1normal);
		
		if(interval == 1) {
			System.out.println("writing to file");
			BufferedWriter bw = null;
			FileWriter fw = null;
			try {
				File file = new File("normalizedData.txt");
				// true = append file
				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.write(Double.toString(T1normal.get(0)));
			    bw.write(",");
			    bw.write(Double.toString(T1normal.get(1)));
			    bw.write(",");
			    bw.write(Double.toString(T1normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T2normal.get(1)));
			    bw.write(",");
			    bw.write(Double.toString(T2normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T3normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T4normal.get(2)));//output
			    bw.write("\n");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null)
						bw.close();
					if (fw != null)
						fw.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		else if(interval == 2) {
			//change
			System.out.println("writing to file");
			BufferedWriter bw = null;
			FileWriter fw = null;
			try {
				File file = new File("normalizedData.txt");
				// true = append file
				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.write(Double.toString(T1normal.get(0)));
			    bw.write(",");
			    bw.write(Double.toString(T1normal.get(1)));
			    bw.write(",");
			    bw.write(Double.toString(T1normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T2normal.get(1)));
			    bw.write(",");
			    bw.write(Double.toString(T2normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T3normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T4normal.get(2)));//output
			    bw.write("\n");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null)
						bw.close();
					if (fw != null)
						fw.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		else if(interval == 3) {
			//change
			System.out.println("writing to file");
			BufferedWriter bw = null;
			FileWriter fw = null;
			try {
				File file = new File("normalizedData.txt");
				// true = append file
				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.write(Double.toString(T1normal.get(0)));
			    bw.write(",");
			    bw.write(Double.toString(T1normal.get(1)));
			    bw.write(",");
			    bw.write(Double.toString(T1normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T2normal.get(1)));
			    bw.write(",");
			    bw.write(Double.toString(T2normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T3normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T4normal.get(2)));//output
			    bw.write("\n");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null)
						bw.close();
					if (fw != null)
						fw.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		//change
		else if(interval == 4) {
			System.out.println("writing to file");
			BufferedWriter bw = null;
			FileWriter fw = null;
			try {
				File file = new File("normalizedData.txt");
				// true = append file
				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.write(Double.toString(T1normal.get(0)));
			    bw.write(",");
			    bw.write(Double.toString(T1normal.get(1)));
			    bw.write(",");
			    bw.write(Double.toString(T1normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T2normal.get(1)));
			    bw.write(",");
			    bw.write(Double.toString(T2normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T3normal.get(2)));
			    bw.write(",");
			    bw.write(Double.toString(T4normal.get(2)));//output
			    bw.write("\n");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null)
						bw.close();
					if (fw != null)
						fw.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		}//end for loop
	}//end main
	
	public static ArrayList<Double> normalizeData(ArrayList<Double> list) {
		double normalized;
		double min = 0;
		double max = 3200;
		double x;
		ArrayList<Double> normalizedArray = new ArrayList<Double>();
		for (int i=0; i<list.size(); i++) {
			x = list.get(i);
			normalized = (x-min)/(max-min);
			normalizedArray.add(normalized);
		}
		return normalizedArray;
	}
	
	public static Vector<Sensor> getTestSet() {
		//System.out.println(longSegment.getSensorsSize());
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
	
	//speed = mph
	//distance = miles
	//.5miles/60mph (3600)
	//free flow speed = 70mph
	//max flow = 1300
	public static double calculateTime(double speed) {
		double time = (.5)/speed;
		return time;
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
	
	//need to adjust this to account for specific sensors
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
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("LaneIntervalData");
			System.out.println("----------------------------");
	
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				
				//System.out.println("\nCurrent Element : " + nNode.getNodeName());
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					//System.out.println("Lane ID : " + eElement.getElementsByTagName("LaneID").item(0).getTextContent());
					String laneResult= eElement.getElementsByTagName("LaneID").item(0).getTextContent();
					int laneID = Integer.parseInt(laneResult);
					
					//System.out.println("Sensor ID : " + eElement.getElementsByTagName("SensorID").item(0).getTextContent());
					String sensorResult= eElement.getElementsByTagName("SensorID").item(0).getTextContent();
					int sensorID = Integer.parseInt(sensorResult);
					
					//System.out.println("Start Time : " + eElement.getElementsByTagName("LaneIntervalStart").item(0).getTextContent());
					String startResult= eElement.getElementsByTagName("LaneIntervalStart").item(0).getTextContent();
					SimpleDateFormat startSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX");
					SimpleDateFormat startOutput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX");
					Date startTime = startSDF.parse(startResult);
					String startFormattedTime = startOutput.format(startTime);
					//System.out.println(startFormattedTime);
					
					//System.out.println("End Time : " + eElement.getElementsByTagName("LaneIntervalEnd").item(0).getTextContent());
					String endResult= eElement.getElementsByTagName("LaneIntervalEnd").item(0).getTextContent();		
					SimpleDateFormat endSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX");
					SimpleDateFormat endOutput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX");
					Date endTime = endSDF.parse(endResult);
					String endFormattedTime = endOutput.format(endTime);
					//System.out.println(endFormattedTime);
					
					//System.out.println("Speed : " + eElement.getElementsByTagName("AverageSpeed").item(0).getTextContent());
					String speedResult= eElement.getElementsByTagName("AverageSpeed").item(0).getTextContent();
					double speed = Double.parseDouble(speedResult);
					
					//System.out.println("Occupancy : " + eElement.getElementsByTagName("Occupancy").item(0).getTextContent());
					String occupancyResult= eElement.getElementsByTagName("Occupancy").item(0).getTextContent();
					Float occupancy = Float.parseFloat(occupancyResult);
					
					//System.out.println("Volume : " + eElement.getElementsByTagName("Volume").item(0).getTextContent());
					String volumeResult= eElement.getElementsByTagName("Volume").item(0).getTextContent();
					int volume = Integer.parseInt(volumeResult);
					
					//System.out.println("Small Count : " + eElement.getElementsByTagName("SmallCount").item(0).getTextContent());
					String smallResult= eElement.getElementsByTagName("SmallCount").item(0).getTextContent();
					int smallCount = Integer.parseInt(smallResult);
					
					//System.out.println("Medium Count : " + eElement.getElementsByTagName("MediumCount").item(0).getTextContent());
					String mediumResult= eElement.getElementsByTagName("MediumCount").item(0).getTextContent();
					int mediumCount = Integer.parseInt(mediumResult);
					
					//System.out.println("Large Count : " + eElement.getElementsByTagName("LargeCount").item(0).getTextContent());
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
