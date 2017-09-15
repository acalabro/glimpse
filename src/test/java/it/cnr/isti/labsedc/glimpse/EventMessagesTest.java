package it.cnr.isti.labsedc.glimpse;

import org.junit.Before;

import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEvent;
import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventSB;
import it.cnr.isti.labsedc.glimpse.probe.GlimpseAbstractProbe;

import junit.framework.Assert;

public class EventMessagesTest {

	//@Parameters
	//public GlimpseAbstractProbe theProbe = new 
	
	@Before
	public void CreateProbe() {
		
	}
	
	public void SendMessageContainingMalformedSmartBuildingEvent(GlimpseBaseEventSB<String> smartBuildingEvent) {
		
		//Assert.assertEquals(true, theProbe.sendMessage(event, true););
	}
}