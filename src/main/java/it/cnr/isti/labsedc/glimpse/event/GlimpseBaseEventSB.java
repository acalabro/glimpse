package it.cnr.isti.labsedc.glimpse.event;

import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventGeneric;
import it.cnr.isti.labsedc.glimpse.utils.SensorType;

public class GlimpseBaseEventSB<T> extends GlimpseBaseEventGeneric<Float> {

	private static final long serialVersionUID = 1L;
	private SensorType sensorType;
	
	public SensorType getSensorType() {
		return sensorType;
	}

	public void setSensorType(SensorType sensorType) {
		this.sensorType = sensorType;
	}

	public GlimpseBaseEventSB(
			Float parameterValue,
			String sensorName,
			Long timeStamp,
			String parameterName,
			boolean isException,
			String roomID, SensorType sensorType) {
		
		super(parameterValue, sensorName, timeStamp, parameterName, isException, roomID);
		this.sensorType = sensorType;
	}
	
}