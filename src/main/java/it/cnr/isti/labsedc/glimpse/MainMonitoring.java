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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

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
import it.cnr.isti.labsedc.glimpse.storage.H2Controller;
import it.cnr.isti.labsedc.glimpse.telegram.GlimpseTelegramBot;
import it.cnr.isti.labsedc.glimpse.telegram.TelegramManualNotifier;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;
import it.cnr.isti.labsedc.glimpse.utils.Logger;
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
	private static Properties environmentParameters;
	private static Properties activeMqSSlParameters;
	private static InitialContext initConn;
	private static ActiveMQConnectionFactory connFact;

	/**
	 * Read the properties and init the connections to the enterprise service bus 
	 * @param is the systemSettings file
	 */
	public static void main(String[] args) {
		try{
			Logger.CreateLogger();
			systemProps = Manager.Read(args[0]);
			environmentParameters = Manager.Read(systemProps.getProperty("ENVIRONMENTPARAMETERSFILE"));
			activeMqSSlParameters = Manager.Read(systemProps.getProperty("ACTIVEMQSSLPARAMETERSFILE"));
			initConn = new InitialContext(environmentParameters);
			DebugMessages.PrintParams(environmentParameters);
			
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
			
			String topicOnWhichInferComplexEvents = Manager.Read(systemProps.getProperty("ENVIRONMENTPARAMETERSFILE")).getProperty("topic.probeTopic").replaceFirst("jms.", "");
			
 			ComplexEventProcessor engineOne = new ComplexEventProcessorImpl(buffer, connFact, initConn, topicOnWhichInferComplexEvents);
			engineOne.start();
			
			RestNotifier notifierEngine = new RestNotifier();
			notifierEngine.start();
			
			DBController databaseController = new H2Controller(Manager.Read(systemProps.getProperty("DATABASECONNECTIONSTRINGH2")));
			//DBController databaseController = new InfluxDBController(Manager.Read(systemProps.getProperty("DATABASECONNECTIONSTRINGINFLUXDB")));
			
			LearnerAssessmentManager lam = new LearnerAssessmentManagerImpl(databaseController);
			lam.start(); 
			
			Thread.sleep(1000);
			
			Stream<Path> paths = Files.walk(Paths.get(systemProps.getProperty("DROOLSRULETEMPLATESPATH")));
			RuleTemplateManager templateManager = new RuleTemplateManager(paths);
			
			//telegramBot
	        ApiContextInitializer.init();
	        TelegramBotsApi botsApi = new TelegramBotsApi();
	        GlimpseTelegramBot glimpseBot = new GlimpseTelegramBot(databaseController,
	        					Manager.Read(systemProps.getProperty("TELEGRAMTOKENSTRING")).getProperty("telegramToken"),
	        					Manager.Read(systemProps.getProperty("TELEGRAMTOKENSTRING")).getProperty("telegramBotUsername"));
	        
	        botsApi.registerBot(glimpseBot);
			RoomManager theRoomManager4SmartBuilding = new RoomManagerImpl(databaseController);
			theRoomManager4SmartBuilding.start();
			TelegramManualNotifier telegramNotifier = new TelegramManualNotifier(glimpseBot,databaseController);
			telegramNotifier.start();
	
			ServiceLocatorFactory.getServiceLocatorParseViolationReceivedFromBSM(
					engineOne,	templateManager, systemProps.getProperty("REGEXPATTERNFILEPATH")).start();

			MailNotification mailer = new MailNotification(Manager.Read(systemProps.getProperty("MAILNOTIFICATIONPATH")));
			mailer.start();
			
			//the manager of all the architecture
			GlimpseManager manager = new GlimpseManager(Manager.Read(systemProps.getProperty("ENVIRONMENTPARAMETERSFILE")).getProperty("topic.serviceTopic").replaceFirst("jms.", ""),
					connFact,initConn,engineOne.getRuleManager(),lam);
			manager.start();
		} catch (InterruptedException |  NamingException | IOException | TelegramApiRequestException e) {
			System.out.println("USAGE: java -jar MainMonitoring.jar \"systemSettings\"");	
			e.printStackTrace();
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
