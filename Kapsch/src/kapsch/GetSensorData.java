package kapsch;

import javax.xml.soap.*;

public class GetSensorData {
    public static void GetHistoricSensorData()
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
            facilityElement.addTextNode("80");
            segmentElement.addTextNode("6");
            startTimeElement.addTextNode("2017-10-17T08:00:00");
            endTimeElement.addTextNode("2017-10-17T08:03:58");


            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", soapAction);
            soapMessage.saveChanges();

            // Call Service and return token
            SOAPMessage soapResponse = callSoapWebService(soapEndpointUrl, soapAction, soapMessage);
            soapResponse.writeTo(System.out);
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

    public static void main(String []args)
    {
    	GetHistoricSensorData();
    }
}
