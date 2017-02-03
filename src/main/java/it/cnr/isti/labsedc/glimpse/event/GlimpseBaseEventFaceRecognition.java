package it.cnr.isti.labsedc.glimpse.event;

import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventGeneric;

public class GlimpseBaseEventFaceRecognition<T> extends GlimpseBaseEventGeneric<Boolean> {

	private static final long serialVersionUID = 1L;

	public GlimpseBaseEventFaceRecognition(
			Boolean accessGranted,
			String cameraName,
			Long timeStamp,
			String parameterName, //unused
			boolean isException,
			String roomID) {
		
		super(accessGranted, cameraName, timeStamp, parameterName, isException, roomID);
	}
	
}