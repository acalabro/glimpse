package it.cnr.isti.labsedc.glimpse.event;

import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventGeneric;

public class GlimpseBaseEventFaceRecognition<T> extends GlimpseBaseEventGeneric<Boolean> {

	private static final long serialVersionUID = 1L;
	private String personID;
	private String idScreenshot;
			    
	public GlimpseBaseEventFaceRecognition(
			Boolean accessGranted,
			String cameraName,
			Long timeStamp,
			String macAddress, 
			boolean isException,
			String roomID,
			String personID,
			String idScreenshot) {
		
		super(accessGranted, cameraName, timeStamp, macAddress, isException, roomID);
		this.personID = personID;
		this.idScreenshot = idScreenshot;
	}
	
	public String getPersonID() {
		return personID;
	}

	public void setPersonID(String personID) {
		this.personID = personID;
	}

	public String getIDScreenshot() {
		return idScreenshot;
	}

	public void setIDScreenshot(String idScreenshot) {
		this.idScreenshot = idScreenshot;
	}
}