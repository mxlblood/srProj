package kapsch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;

public class TestNeuralNetwork {
	private static Facility north;
	private static Segment longSegment;
	private static String fullStartTime;
	private static String fullEndTime;
	private static String facilityID;
	private static String facilityDescription;
	private static String segmentID;
	private static String segmentDescription;
	private static int interval;
	private static Calendar interval4;
	private static Calendar interval8;
	private static Calendar interval12;
	private static Calendar interval16;
	private static Vector<String> sensorIDList= new Vector<String>();

	public static void main(String[] args) {
		
		System.out.println("---------------Highway-----------------");
		System.out.println("---121---122---123---124---125---126---\n\n");
		System.out.println("---------------Highway-----------------");
		
		Scanner sc = new Scanner(System.in);

		int idCounter = 0;
		while(idCounter<3){
			System.out.println("Enter sensor ID to create segment of highway for predictions or enter 'x' to escape: ");
			String sensorInputID = sc.next();
			if(sensorInputID.equals("x"))
				break;
			else
				sensorIDList.add(sensorInputID);
				idCounter++;	
		}
		
		System.out.println("Enter a start date in format 'yyyy-MM-dd': ");
		String startDateInput = sc.next();
		while(HistoricData.isThisDateValid(startDateInput, "yyyy-MM-dd")==false) {
			System.out.println("Error in date formatting.");
			System.out.println("Enter a date in format 'yyyy-MM-dd': ");
			startDateInput = sc.next();
		}
		
		System.out.println("Enter a start time in format 'HH:mm:ss': ");
		String startTimeResult = sc.next();
		while(HistoricData.isThisDateValid(startTimeResult, "HH:mm:ss")==false) {
			System.out.println("Error in start time formatting.");
			System.out.println("Enter a start time in format 'HH:mm:ss': ");
			startTimeResult = sc.next();
		}
		
		do {
			System.out.println("Enter how many minutes in the future you would like to predict:\nEnter 4, 8 or 12 minutes.");
            while (!sc.hasNextInt()) {
                String input = sc.next();
                System.out.println("Please enter '4', '8' or '12'");
            }
            interval = sc.nextInt();
        } while(interval!=4 && interval!=8 && interval!=12); 
		
		sc.close();
		
		File configFile = new File("config.properties");
		try {
		    FileReader reader = new FileReader(configFile);
		    Properties props = new Properties();
		    props.load(reader);

		    facilityID = props.getProperty("facilityID");
		    facilityDescription = props.getProperty("facilityDescription");
		    segmentID = props.getProperty("segmentID");
		    segmentDescription = props.getProperty("segmentDescription");
			
		    Calendar theEndTime = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			theEndTime.setTime(sdf.parse(startTimeResult));

		    if(interval == 4){
		    		theEndTime.add(Calendar.MINUTE, 4);
		    }
		    else if(interval == 8) {
		    		theEndTime.add(Calendar.MINUTE, 8);
		    }
		    else if(interval == 12) {
		    		theEndTime.add(Calendar.MINUTE, 12);
		    }
		    else if(interval == 16) {
		    		theEndTime.add(Calendar.MINUTE, 16);
		    }
		    else {
		    		System.out.println("Error in interval length");
		    }
		    
		    fullStartTime=(startDateInput+"T"+startTimeResult);
		    String endFormattedTime = sdf.format(theEndTime.getTime());
		    fullEndTime=(startDateInput+"T"+endFormattedTime);
		    
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
		
		while(sensorCount>0) {
			try {
			Date currentDateTime = testSet.get(sensorCount-1).getStartTime();
			Calendar currentEndTime = Calendar.getInstance();
			currentEndTime.setTime(currentDateTime);
			
			Calendar startDateTime = Calendar.getInstance();
			startDateTime.setTime(sdf.parse(fullStartTime));
			
			int currentSensorID = testSet.get(sensorCount-1).getSensorID();
			int sensorIDFromList = Integer.parseInt(sensorIDList.get(sensorIDListCount-sensorIDListCount));
			double currentFlow = testSet.get(sensorCount-1).getFlow();

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
		System.out.println("___|" + sensorIDList.get(sensorIDListCount-sensorIDListCount) + " | " +  sensorIDList.get(sensorIDListCount-(sensorIDListCount-1)) + " | " + sensorIDList.get(sensorIDListCount-(sensorIDListCount-2)));
		System.out.println("T1 |" + T1.get(0) + "|" + T1.get(1) + "|" + T1.get(2));
		System.out.println("T2 |" + T2.get(0) + "|" + T2.get(1) + "|" + T2.get(2));
		System.out.println("T3 |" + T3.get(0) + "|" + T3.get(1) + "|" + T3.get(2));
		System.out.println("T4 |" + T4.get(0) + "|" + T4.get(1) + "|" + T4.get(2));
		
		ArrayList<Double> T1normal = normalizeData(T1);
		ArrayList<Double> T2normal = normalizeData(T2);
		ArrayList<Double> T3normal = normalizeData(T3);
		ArrayList<Double> T4normal = normalizeData(T4);

		if(interval == 4) {
			System.out.println("writing to file");
			BufferedWriter bw = null;
			FileWriter fw = null;
			try {
				File file = new File("inputTestArray.txt");
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
		else if (interval == 8) {
			//change
			System.out.println("writing to file");
			BufferedWriter bw = null;
			FileWriter fw = null;
			try {
				File file = new File("inputTestArray.txt");
				fw = new FileWriter(file);
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
		else if (interval == 12) {
			//change
			System.out.println("writing to file");
			BufferedWriter bw = null;
			FileWriter fw = null;
			try {
				File file = new File("inputTestArray.txt");
				fw = new FileWriter(file);
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
		else if (interval == 16) {
			//change
			System.out.println("writing to file");
			BufferedWriter bw = null;
			FileWriter fw = null;
			try {
				File file = new File("inputTestArray.txt");
				fw = new FileWriter(file);
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

		DataSet dSet = DataSet.createFromFile("inputTestArray.txt", 6, 1, ",");
		NeuralNetwork nNet = NeuralNetwork.createFromFile("mpKapschNet.nnet");
		//NeuralNetworkTrain.testSinglePrediction(nNet, dSet);
		//System.out.println(longSegment);
		
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
		Sensor sensor;
		Vector<Sensor> testSet= new Vector<Sensor>();
		for(int i=0;i<longSegment.getSensorsSize(); i++) {
			sensor = longSegment.getSensor(i);
			String currentSensor = Integer.toString(sensor.getSensorID());
			if (sensorIDList.contains(currentSensor)) {
				testSet.add(sensor);
			}	
		}
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
			Date date = sdf.parse(dateToValidate);
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
            facilityElement.addTextNode(facilityID);
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
