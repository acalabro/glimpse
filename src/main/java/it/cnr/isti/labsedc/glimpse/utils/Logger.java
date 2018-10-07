package it.cnr.isti.labsedc.glimpse.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;

public class Logger {
	public static Calendar calendarConverter = Calendar.getInstance();
	public static String activeJsonLogger;

	public static void CreateLogger() {	
		try {
			calendarConverter.setTimeInMillis(System.currentTimeMillis());
			int month = calendarConverter.get(Calendar.MONTH);
			int day = calendarConverter.get(Calendar.DAY_OF_MONTH);
			int year = calendarConverter.get(Calendar.YEAR);
			
			new File("logs").mkdir();
			FileOutputStream fos;
			fos = new FileOutputStream("logs//glimpseLog_" + year + "-" + (month +1)+ "-" + day + ".log");
			PrintStream ps = new PrintStream(fos);
			System.setErr(ps);
			CreateJsonLogger();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	public static void CreateJsonLogger() {
		try {
			calendarConverter.setTimeInMillis(System.currentTimeMillis());
			int month = calendarConverter.get(Calendar.MONTH);
			int day = calendarConverter.get(Calendar.DAY_OF_MONTH);
			int year = calendarConverter.get(Calendar.YEAR);
			
			FileOutputStream fos;
			activeJsonLogger = "logs//glimpseLog_" + year + "-" + (month +1)+ "-" + day + ".json";	
			fos = new FileOutputStream(activeJsonLogger);
			PrintStream ps = new PrintStream(fos);
			JsonLogger jLog = new JsonLogger(ps);
			ps.println("[");
			jLog.start();
			System.out.println("[");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	public static void CloseJsonLogger() {
		
		try {
		    File file = new File(activeJsonLogger);
		    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			FileChannel fileChannel = fileOutputStream.getChannel();
			if (fileChannel.size()>0) {
				fileChannel.truncate(fileChannel.size() - 1); //Removes last character	
			};
		    fileChannel.close();
		    
		    DataOutputStream dos = new DataOutputStream(fileOutputStream);
		    char c = ']';
		    dos.writeChar(c);
		    dos.close();
		    fileOutputStream.close();
		}
		catch (IOException ex) {
		}
	}
}
