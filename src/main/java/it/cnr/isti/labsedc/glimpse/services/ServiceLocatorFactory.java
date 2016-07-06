package it.cnr.isti.labsedc.glimpse.services;

import it.cnr.isti.labsedc.glimpse.cep.ComplexEventProcessor;
import it.cnr.isti.labsedc.glimpse.impl.RuleTemplateManager;
import it.cnr.isti.labsedc.glimpse.impl.ServiceLocatorImpl;
import it.cnr.isti.labsedc.glimpse.impl.ServiceLocatorParseViolationReceivedFromBSM;

public class ServiceLocatorFactory {
	
	public static ServiceLocator getServiceLocatorImpl(
			ComplexEventProcessor engine,
			String soapRequestFilePath,
			RuleTemplateManager ruleTemplateManager,
			String bsmWsdlUriFilePath,
			String regexPatternFilePath)
	{
		ServiceLocator locator = new ServiceLocatorImpl(
				engine,
				soapRequestFilePath,
				ruleTemplateManager,
				bsmWsdlUriFilePath,
				regexPatternFilePath);
		
		return locator;
	}
	
	public static ServiceLocator getServiceLocatorParseViolationReceivedFromBSM(ComplexEventProcessor engine,
			RuleTemplateManager ruleTemplateManager,
			String regexPatternFilePath)
	{
		ServiceLocator locator = new ServiceLocatorParseViolationReceivedFromBSM(engine,
				ruleTemplateManager,
				regexPatternFilePath);
		return locator;
	}
}
