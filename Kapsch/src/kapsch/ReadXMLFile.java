package kapsch;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadXMLFile {

  public static void main(String argv[]) {

    try {

	File fXmlFile = new File("HistoricData.xml");
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
			//Segment.addSensor(sensor);	
		}
		
	}
    } catch (Exception e) {
	e.printStackTrace();
    }
  }

}

