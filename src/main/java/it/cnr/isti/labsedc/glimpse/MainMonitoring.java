package it.cnr.isti.labsedc.glimpse;
/*  GLIMPSE: A generic and flexible monitoring infrastructure.
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;

import javax.naming.InitialContext;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import it.cnr.isti.labsedc.glimpse.buffer.EventsBuffer;
import it.cnr.isti.labsedc.glimpse.cep.ComplexEventProcessor;
import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEvent;
import it.cnr.isti.labsedc.glimpse.impl.ComplexEventProcessorImpl;
import it.cnr.isti.labsedc.glimpse.impl.EventsBufferImpl;
import it.cnr.isti.labsedc.glimpse.impl.LearnerAssessmentManagerImpl;
import it.cnr.isti.labsedc.glimpse.impl.RoomManagerImpl;
import it.cnr.isti.labsedc.glimpse.impl.RuleTemplateManager;
import it.cnr.isti.labsedc.glimpse.manager.GlimpseManager;
import it.cnr.isti.labsedc.glimpse.manager.LearnerAssessmentManager;
import it.cnr.isti.labsedc.glimpse.manager.RestNotifier;
import it.cnr.isti.labsedc.glimpse.services.ServiceLocatorFactory;
import it.cnr.isti.labsedc.glimpse.smartbuilding.RoomManager;
import it.cnr.isti.labsedc.glimpse.storage.DBController;
import it.cnr.isti.labsedc.glimpse.storage.InfluxDBController;
import it.cnr.isti.labsedc.glimpse.telegram.GlimpseTelegramBot;
import it.cnr.isti.labsedc.glimpse.telegram.TelegramManualNotifier;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;
import it.cnr.isti.labsedc.glimpse.utils.JsonLogger;
import it.cnr.isti.labsedc.glimpse.utils.MailNotification;
import it.cnr.isti.labsedc.glimpse.utils.Manager;
import it.cnr.isti.labsedc.glimpse.utils.SplashScreen;

/**
 * @author Antonello Calabr&ograve;
 * 
 * This class is the launcher of the glimpse infrastructure
 * It setup the environment. It is possible to configure
 * different engine for complex event recognition.
 * 
 * It also provide the setup of all the connection to the ESB
 */

public class MainMonitoring {

	private static Properties systemProps = new Properties();

	// start settings
	protected static String ENVIRONMENTPARAMETERSFILE;
	protected static String ACTIVEMQSSLPARAMETERSFILE;
	protected static String SOAPREQUESTFILE;
	protected static String DROOLSRULEREQUESTTEMPLATE1;
	protected static String DROOLSRULEREQUESTTEMPLATE2;
	protected static String DROOLSRULEREQUESTTEMPLATE3_1;
	protected static String DROOLSRULEREQUESTTEMPLATE3_2;
	protected static String BSMWSDLURIFILEPATH;
	protected static String REGEXPATTERNFILEPATH;
	protected static String MAILNOTIFICATIONSETTINGSFILEPATH;
	protected static String DATABASECONNECTIONSTRINGH2;
	protected static String DATABASECONNECTIONSTRINGMYSQL;
	protected static String DATABASECONNECTIONSTRINGINFLUXDB;
	protected static String TELEGRAMTOKENURLSTRING;
	protected static String RESTNOTIFIERURLSTRING; 
	// end settings

	public static Calendar calendarConverter = Calendar.getInstance();
	private static Properties environmentParameters;
	private static Properties activeMqSSlParameters;
	private static InitialContext initConn;
	private static ActiveMQConnectionFactory connFact;
	public static String activeJsonLogger;

	/**
	 * This method reads parameters from text files
	 * Parameters are relative to environment, cep engine
	 * 
	 * @param systemSettings
	 * @return true if operations are completed correctly
	 */
	
	public static boolean initProps(String systemSettings) {
		try {
			systemProps = Manager.Read(systemSettings);

			ENVIRONMENTPARAMETERSFILE = 		systemProps.getProperty("ENVIRONMENTPARAMETERSFILE");
			ACTIVEMQSSLPARAMETERSFILE =			systemProps.getProperty("ACTIVEMQSSLPARAMETERSFILE");
			SOAPREQUESTFILE = 					systemProps.getProperty("SOAPREQUESTFILE");
			DROOLSRULEREQUESTTEMPLATE1 = 		systemProps.getProperty("DROOLSRULEREQUESTTEMPLATE1");	
			DROOLSRULEREQUESTTEMPLATE2 = 		systemProps.getProperty("DROOLSRULEREQUESTTEMPLATE2");	
			DROOLSRULEREQUESTTEMPLATE3_1 = 		systemProps.getProperty("DROOLSRULEREQUESTTEMPLATE3_1");
			DROOLSRULEREQUESTTEMPLATE3_2 = 		systemProps.getProperty("DROOLSRULEREQUESTTEMPLATE3_2");
			BSMWSDLURIFILEPATH = 				systemProps.getProperty("BSMWSDLURIFILEPATH");		
			REGEXPATTERNFILEPATH = 				systemProps.getProperty("REGEXPATTERNFILEPATH");
			MAILNOTIFICATIONSETTINGSFILEPATH = 	systemProps.getProperty("MAILNOTIFICATIONPATH");
			DATABASECONNECTIONSTRINGH2 = 		systemProps.getProperty("DATABASECONNECTIONSTRINGH2");
			DATABASECONNECTIONSTRINGMYSQL = 	systemProps.getProperty("DATABASECONNECTIONSTRINGMYSQL");
			DATABASECONNECTIONSTRINGINFLUXDB = 	systemProps.getProperty("DATABASECONNECTIONSTRINGINFLUXDB");
			RESTNOTIFIERURLSTRING = 			systemProps.getProperty("RESTNOTIFIERURLSTRING");
			TELEGRAMTOKENURLSTRING = 			systemProps.getProperty("TELEGRAMTOKENSTRING");
			return true;
		} catch (Exception asd) {
			System.out.println("USAGE: java -jar MainMonitoring.jar \"systemSettings\"");
			return false;
		}
	}
	

	/**
	 * Read the properties and init the connections to the enterprise service bus
	 * 
	 * @param is the systemSettings file
	 */
	public static void main(String[] args) {
		try{
			CreateLogger();
			
			if (MainMonitoring.initProps(args[0])) {
				environmentParameters = Manager.Read(ENVIRONMENTPARAMETERSFILE);
				activeMqSSlParameters = Manager.Read(ACTIVEMQSSLPARAMETERSFILE);
				initConn = new InitialContext(environmentParameters);

				DebugMessages.println(System.currentTimeMillis(), MainMonitoring.class.getSimpleName(), "Connection Parameters");
				DebugMessages.line();
				
				Enumeration<?> e = environmentParameters.propertyNames();
				while (e.hasMoreElements()) {
				      String key = (String) e.nextElement();
				      DebugMessages.println(System.currentTimeMillis(),MainMonitoring.class.getSimpleName(),
				    		  key + ": " + environmentParameters.getProperty(key));	
				}
				DebugMessages.line();
			    				
				if (Boolean.parseBoolean(environmentParameters.getProperty("activemq.execute.internal.instance"))) {
				
					System.out.println("Running ActiveMQ instance on " + environmentParameters.getProperty("java.naming.provider.url"));
					ActiveMQRunner anActiveMQInstance = new ActiveMQRunner(environmentParameters.getProperty("java.naming.provider.url"), 
							Long.parseLong(environmentParameters.getProperty("activemq.memory.usage")),
							Long.parseLong(environmentParameters.getProperty("activemq.temp.usage")),
							Boolean.parseBoolean(environmentParameters.getProperty("sslConnectionFactory.enabled")));
				    new Thread(anActiveMQInstance).start();
									
					while (!anActiveMQInstance.isBrokerStarted()) {
						Thread.sleep(1000);
					}
					System.out.println("ActiveMQ is running");					
				}

				System.out.println("Running GLIMPSE");
				SplashScreen.Show();
				System.out.println("Please wait until setup is done...");
				
				EventsBuffer<GlimpseBaseEvent<?>> buffer = new EventsBufferImpl<GlimpseBaseEvent<?>>();
				
				//The complex event engine that will be used (in this case drools)
				if (Boolean.parseBoolean(environmentParameters.getProperty("sslConnectionFactory.enabled"))) {
					connFact = createSSLConnection(activeMqSSlParameters, environmentParameters.getProperty("java.naming.provider.url"));
				} else {
					connFact = createConnection(environmentParameters.getProperty("java.naming.provider.url"));	
				}
				
				connFact.setTrustedPackages(new ArrayList<String>(Arrays.asList(
										environmentParameters.getProperty("activemq.trustable.serializable.class.list").split(","))));
				
				String topicOnWhichInferComplexEvents = Manager.Read(ENVIRONMENTPARAMETERSFILE).getProperty("topic.probeTopic").replaceFirst("jms.", "");
				
				ComplexEventProcessor engineOne = new ComplexEventProcessorImpl(buffer, connFact, initConn, topicOnWhichInferComplexEvents);
				engineOne.start();
				
				RestNotifier notifierEngine = new RestNotifier();
				notifierEngine.start();
				
//				//using INFLUXDB database
//				DBController databaseController = new H2Controller(Manager.Read(DATABASECONNECTIONSTRINGH2));
				DBController databaseController = new InfluxDBController(Manager.Read(DATABASECONNECTIONSTRINGINFLUXDB));
				
				LearnerAssessmentManager lam = new LearnerAssessmentManagerImpl(databaseController);
				lam.start(); 
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				
				//TODO: update according java 8 paradigm
				//RuleTemplateManager templateManager = new RuleTemplateManager(droolsRequestTemplates);
				
				RuleTemplateManager templateManager = new RuleTemplateManager(
										DROOLSRULEREQUESTTEMPLATE1,
										DROOLSRULEREQUESTTEMPLATE2, 
										DROOLSRULEREQUESTTEMPLATE3_1,
										DROOLSRULEREQUESTTEMPLATE3_2);
				//telegramBot
		        ApiContextInitializer.init();
		        TelegramBotsApi botsApi = new TelegramBotsApi();
		        GlimpseTelegramBot glimpseBot = new GlimpseTelegramBot(
		        		databaseController, Manager.Read(TELEGRAMTOKENURLSTRING).getProperty("telegramToken"));

		        try {
		            botsApi.registerBot(glimpseBot);
		        } catch (TelegramApiException eq) {
		            eq.printStackTrace();
		        }
				
				RoomManager theRoomManager4SmartBuilding = new RoomManagerImpl(databaseController);
				theRoomManager4SmartBuilding.start();
				
				//the component in charge to locate services and load specific rules.
				ServiceLocatorFactory.getServiceLocatorParseViolationReceivedFromBSM(
										engineOne,	templateManager, REGEXPATTERNFILEPATH).start();

				TelegramManualNotifier telegramNotifier = 
						new TelegramManualNotifier(glimpseBot,databaseController);
				telegramNotifier.start();
		
				//start MailNotifier component
				MailNotification mailer = new MailNotification(
						Manager.Read(MAILNOTIFICATIONSETTINGSFILEPATH));
				mailer.start();
				
				//the manager of all the architecture
				GlimpseManager manager = new GlimpseManager(Manager.Read(ENVIRONMENTPARAMETERSFILE).getProperty("topic.serviceTopic").replaceFirst("jms.", ""),
						connFact,initConn,engineOne.getRuleManager(),lam);
				manager.start();
			}
		} catch (Exception e) {
			System.out.println("USAGE: java -jar MainMonitoring.jar \"systemSettings\"");	
			System.out.println(e.getMessage());
		}
	}

	public static void CreateLogger() {	
		try {
			calendarConverter.setTimeInMillis(System.currentTimeMillis());
			int month = calendarConverter.get(Calendar.MONTH);
			int day = calendarConverter.get(Calendar.DAY_OF_MONTH);
			int year = calendarConverter.get(Calendar.YEAR);
			
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
		
	private static ActiveMQConnectionFactory createConnection(String namingProviderURL) {
		DebugMessages.println(System.currentTimeMillis(), MainMonitoring.class.getSimpleName(),"Setting up ActiveMQConnectionFactory");
		return new ActiveMQConnectionFactory(namingProviderURL); 
	}

	private static ActiveMQSslConnectionFactory createSSLConnection(Properties activeMqSSLParameters, String namingProviderURL) {		
		DebugMessages.println(System.currentTimeMillis(), MainMonitoring.class.getSimpleName(),"Setting up ActiveMQSslConnectionFactory");
	    
	    	System.setProperty("javax.net.ssl.keyStore", activeMqSSlParameters.getProperty("keyStore")); 
	    System.setProperty("javax.net.ssl.keyStorePassword", activeMqSSlParameters.getProperty("keyStorePassword")); 
	    System.setProperty("javax.net.debug", "handshake"); 
	    ActiveMQSslConnectionFactory connFact = new ActiveMQSslConnectionFactory(environmentParameters.getProperty("java.naming.provider.url"));
	    try {
			((ActiveMQSslConnectionFactory) connFact).setTrustStore(activeMqSSlParameters.getProperty("trustStore"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	    ((ActiveMQSslConnectionFactory) connFact).setTrustStorePassword(activeMqSSlParameters.getProperty("trustStorePassword"));    
	    return connFact;
	}
}
