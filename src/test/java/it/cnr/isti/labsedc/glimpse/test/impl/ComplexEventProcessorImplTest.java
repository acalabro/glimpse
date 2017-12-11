package it.cnr.isti.labsedc.glimpse.test.impl;

import static org.junit.Assert.*;

import javax.jms.TopicConnectionFactory;

import org.apache.poi.ss.formula.functions.T;
import org.junit.Before;
import org.junit.Test;

import it.cnr.isti.labsedc.glimpse.buffer.EventsBuffer;
import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEvent;
import it.cnr.isti.labsedc.glimpse.impl.EventsBufferImpl;

public class ComplexEventProcessorImplTest {

	@Before
	public void setUp() throws Exception {
		EventsBuffer<GlimpseBaseEvent<T>> buffer = new EventsBufferImpl<>();
		TopicConnectionFactory connectionFact = null;
		
		//ComplexEventProcessor cepToTest = new ComplexEventProcessorImpl(buffer, connectionFact, initConn, topicOnWhichInferComplexEvents)
	}

	@Test
	public void testGetRuleManager() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testInit() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnMessage() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSetMetric() {
		fail("Not yet implemented"); // TODO
	}

}
