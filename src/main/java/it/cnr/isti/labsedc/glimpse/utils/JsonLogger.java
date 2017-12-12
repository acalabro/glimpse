package it.cnr.isti.labsedc.glimpse.utils;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventFaceRecognition;
import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventMachineInformation;
import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventSB;

public class JsonLogger extends Thread {
	
	public static Calendar calendarConverter = Calendar.getInstance();
	public static Long lastMessageTime = 0L;
	public static PrintStream localPs;
	
	public JsonLogger(PrintStream ps) {
		localPs = ps;
	}

	private static void checkLog(Long now, Long lastMessageTime) {

		calendarConverter.setTimeInMillis(lastMessageTime);
		int previousMessageDay = calendarConverter.get(Calendar.DAY_OF_YEAR);
	
		calendarConverter.setTimeInMillis(now);
		int lastMessageDay = calendarConverter.get(Calendar.DAY_OF_YEAR);
		
		if (lastMessageDay > previousMessageDay ) {
			Logger.CloseJsonLogger();
			Logger.CreateJsonLogger();
		}
	}
	
	public void run() {
		
	}

	public static void printlnMachineInformationInJSONformat(GlimpseBaseEventMachineInformation<?> theObjectToPrint, Long now) {

		checkLog(now, DebugMessages.lastMessageTime);

		ObjectMapper TEMPjsonObjectToPrint = new ObjectMapper();
		TEMPjsonObjectToPrint.setVisibility(JsonMethod.FIELD, Visibility.ANY);

		try {			
			localPs.println(TEMPjsonObjectToPrint.writeValueAsString(theObjectToPrint)+",");
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//end-JsonTest		
	}

	public static void printlnFaceRecognitionEventInJSONformat(GlimpseBaseEventFaceRecognition<?> theObjectToPrint, Long now) {
		
		checkLog(now, DebugMessages.lastMessageTime);
		
		ObjectMapper TEMPjsonObjectToPrint = new ObjectMapper();
		TEMPjsonObjectToPrint.setVisibility(JsonMethod.FIELD, Visibility.ANY);

		try {
			localPs.println(TEMPjsonObjectToPrint.writeValueAsString(theObjectToPrint)+",");
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//end-JsonTest		
	}

	public static void printlnSmartBuildingEventInJSONformat(GlimpseBaseEventSB<?> theObjectToPrint, Long now) {
		
		checkLog(now, DebugMessages.lastMessageTime);
		
		ObjectMapper TEMPjsonObjectToPrint = new ObjectMapper();
		TEMPjsonObjectToPrint.setVisibility(JsonMethod.FIELD, Visibility.ANY);

		try {
			localPs.println(TEMPjsonObjectToPrint.writeValueAsString(theObjectToPrint)+",");
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//end-JsonTest		
	}
}
