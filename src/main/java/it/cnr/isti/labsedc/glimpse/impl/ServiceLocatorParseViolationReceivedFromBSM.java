package it.cnr.isti.labsedc.glimpse.impl;

import java.util.Properties;

import it.cnr.isti.labsedc.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;
import it.cnr.isti.labsedc.glimpse.alerts.SLAAlertParser;
import it.cnr.isti.labsedc.glimpse.cep.ComplexEventProcessor;
import it.cnr.isti.labsedc.glimpse.services.HashMapManager;
import it.cnr.isti.labsedc.glimpse.services.ServiceLocator;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;
import it.cnr.isti.labsedc.glimpse.utils.Manager;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPMessage;

public class ServiceLocatorParseViolationReceivedFromBSM extends ServiceLocator {
	
	public static ServiceRegistryImpl dataSetForCollectedInformation;
	public static RuleTemplateManager localRuleTemplateManager;
	public static String localRegexPatternFilePath;
	public static ComplexEventProcessor localEngine;
	
	public static HashMapManager theHashMapManager;
	
	
	public Properties regexPatternProperties;

	public ServiceLocatorParseViolationReceivedFromBSM(
			ComplexEventProcessor engine,
			RuleTemplateManager ruleTemplateManager, String regexPatternFilePath) {
		
		DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Starting ServiceLocator component ");
		
		ServiceLocatorParseViolationReceivedFromBSM.localEngine = engine;
		ServiceLocatorParseViolationReceivedFromBSM.localRuleTemplateManager = ruleTemplateManager;
		ServiceLocatorParseViolationReceivedFromBSM.localRegexPatternFilePath = regexPatternFilePath;
	
		theHashMapManager = new HashMapManager();
		regexPatternProperties = Manager.Read(regexPatternFilePath);		
		DebugMessages.ok();

	}
	
	public void run() {
		
		while (this.getState() == State.RUNNABLE) {
			try {
				Thread.sleep(90000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void GetMachineIP(String senderName, String filterService, String serviceRole, RuleTemplateEnum ruleTemplateType, String payload, Long timeStamp) {
		
		DebugMessages.println(System.currentTimeMillis(),ServiceLocatorImpl.class.getCanonicalName(), "getMachineIP method called");
		
		try{
			SLAAlertParser slaParser = new SLAAlertParser(payload);
			slaParser.process();
			
			String alertServiceName = slaParser.getProcessedServiceName();
			String machineIP = slaParser.getProcessedMachineIP();
						
			DebugMessages.println(System.currentTimeMillis(),ServiceLocatorImpl.class.getCanonicalName(), "IP retrieved: " + machineIP);

						
			//generate the new rule to monitor
			if (alertServiceName != null && machineIP != null) {
			ComplexEventRuleActionListDocument newRule = localRuleTemplateManager
					.generateNewRuleToInjectInKnowledgeBase(
							machineIP, alertServiceName, ruleTemplateType, timeStamp, filterService);
			
			DebugMessages.println(System.currentTimeMillis(),
					ServiceLocatorImpl.class.getName(),
					newRule.getComplexEventRuleActionList().xmlText());
			
			//insert new rule into the knowledgeBase
			localRuleTemplateManager.insertRule(newRule, localEngine.getRuleManager());
			}
			else {
				DebugMessages.println(System.currentTimeMillis(),ServiceLocatorImpl.class.getCanonicalName(),
						"the payload (field DATA) is not compliant to the defined capnote standard." + 
						"\nGlimpse is not able to instanciate correctly a new rule from the meta_rule already " + 
						"loaded using the information provided through the GlimpseBaseEventChoreos message occurred");
			}	
		}
		catch(IndexOutOfBoundsException e) {
			DebugMessages.println(System.currentTimeMillis(),ServiceLocatorImpl.class.getName(),"Not an SLA Alert");
		}	 
	}

	@Override
	protected SOAPConnection createConnectionToService() {
		return null;
	}

	@Override
	protected String readSoapRequest(String SoapRequestXMLFilePath) {
		return null;
	}

	@Override
	protected SOAPMessage messageCreation(String soapMessageStringFile) {
		return null;
	}

	@Override
	protected SOAPMessage messageSendingAndGetResponse(
			SOAPConnection soapConnection, SOAPMessage soapMessage,
			String serviceWsdl) {
		return null;
	}

	@Override
	public String getMachineIPLocally(String serviceName, String serviceType,
			String serviceRole) {
		return null;
	}

	@Override
	public String getMachineIPQueryingDSB(String serviceName,
			String serviceType, String serviceRole) {
		return null;
	}

}
