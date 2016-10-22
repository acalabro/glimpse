package it.cnr.isti.labsedc.glimpse.event;

import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventGeneric;

public class GlimpseBaseEventSB<T> extends GlimpseBaseEventGeneric<Float> {

	private static final long serialVersionUID = 1L;
	
	public GlimpseBaseEventSB(
			Float parameterValue,
			String sensorName,
			Long timeStamp,
			String parameterName,
			boolean isException,
			String roomID) {
		
		super(parameterValue, sensorName, timeStamp, parameterName, isException, roomID);
		
	}
	
}