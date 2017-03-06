 /*
  * GLIMPSE: A generic and flexible monitoring infrastructure.
  * For further information: http://labsewiki.isti.cnr.it/labse/tools/glimpse/public/main
  * 
  * Copyright (C) 2011  Software Engineering Laboratory - ISTI CNR - Pisa - Italy
  * 
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  * 
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  * 
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  * 
*/
package it.cnr.isti.labsedc.glimpse.utils;

import java.util.Calendar;

import it.cnr.isti.labsedc.glimpse.MainMonitoring;

/**
 * This class provides print function for debug
 * 
 * @author Antonello Calabr&ograve;
 *
 */
public class DebugMessages {

	public static int lastMessageLength = 0;
	public static Calendar calendarConverter = Calendar.getInstance();
	public static Long lastMessageTime = System.currentTimeMillis();
	
	/**
	 * Print the string "className : message " without break line.
	 * Can be used with method {@link #ok()}
	 * 
	 * @param callerClass the name of the class that is calling method
	 * @param messageToPrint the message to print
	 */
	
	public static void print(Long now, String callerClass, String messageToPrint)
	{
		checkLog(now, lastMessageTime);
		calendarConverter.setTimeInMillis(now);
		String message =  calendarConverter.getTime().toString() + " - " +  callerClass + ": " + messageToPrint;
		System.err.print(message);
		lastMessageLength = message.length();
		lastMessageTime = now;
	}
	private static void checkLog(Long now, Long lastMessageTime) {

		calendarConverter.setTimeInMillis(lastMessageTime);
		int previousMessageDay = calendarConverter.get(Calendar.DAY_OF_MONTH);
	
		calendarConverter.setTimeInMillis(now);
		int lastMessageDay = calendarConverter.get(Calendar.DAY_OF_MONTH);
		
		if (lastMessageDay > previousMessageDay) {
			MainMonitoring.CreateLogger();
		}
	}
	
	public static void main (String[] args) {
		
		System.out.println(System.currentTimeMillis());
		checkLog(System.currentTimeMillis(), Long.parseLong(args[0]));
		
	}
	/**
	 * Print the string "className : message " with break line.
	 * Can be used with method {@link #ok()}
	 * 
	 * @param callerClass the name of the class that is calling method
	 * @param messageToPrint the message to print
	 */
	public static void println(Long now, String callerClass, String messageToPrint)
	{		
		checkLog(now, lastMessageTime);
		calendarConverter.setTimeInMillis(now);
		String message =  calendarConverter.getTime().toString() + " - " +  callerClass + ": " + messageToPrint;
		System.err.println(message);
		lastMessageTime = now;
	}
	
	public static void fail()
	{
		int tab = 10 - (lastMessageLength / 8);
		String add="";
		for(int i = 0; i< tab;i++) {
			add +="\t"; 
		}
		System.out.println(add + "[FAIL]");
	}
	
	/**
	 * Print the OK text
	 */
	public static void ok()
	{
		int tab = 15 - (lastMessageLength / 8);
		String add="";
		for(int i = 0; i< tab;i++) {
			add +="\t"; 
		}
		System.err.println(add + "[ OK ]");
	}
	/**
	 * 
	 * Print a line <br />
	 */
	public static void line() {
		System.err.println("------------------------------------------------------------------------------------------------------------------------------");
	}
	
	/**
	 * Print asterisks
	 */
	public static void asterisks() {
		System.err.println("******************************************************************************************************************************");
	}
}
