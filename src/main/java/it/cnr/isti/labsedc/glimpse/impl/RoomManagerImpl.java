package it.cnr.isti.labsedc.glimpse.impl;

import org.apache.commons.net.ntp.TimeStamp;

import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventSB;
import it.cnr.isti.labsedc.glimpse.smartbuilding.RoomManager;
import it.cnr.isti.labsedc.glimpse.storage.DBController;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;
import it.cnr.isti.labsedc.glimpse.utils.UpdateRoom;
public class RoomManagerImpl extends RoomManager {

	private DBController dbController;
	UpdateRoom roomUpdater;
	
	public RoomManagerImpl(DBController theControllerForTheDB) {
		this.dbController = theControllerForTheDB;
	}
	
	@Override
	public void updateParameter(GlimpseBaseEventSB<Float> theUpdatedParameter) {
		switch (theUpdatedParameter.getSensorType()) {
		case HUMIDITY:
			updateHumidity(theUpdatedParameter.getExtraDataField(),theUpdatedParameter.getEventData());
			break;
		case NOISE:
			updateNoise(theUpdatedParameter.getExtraDataField(),theUpdatedParameter.getEventData());
			break;
		case OCCUPANCY:
			updateOccupancy(theUpdatedParameter.getExtraDataField(),theUpdatedParameter.getEventData());
			break;
		case POWER:
			updatePowerConsumption(theUpdatedParameter.getExtraDataField(),theUpdatedParameter.getEventData());
			break;
		case TEMPERATURE:
			updateTemperature(theUpdatedParameter.getExtraDataField(),theUpdatedParameter.getEventData());
			break;
		default:
			break;
		}
	}

	public void run() {
		DebugMessages.print(TimeStamp.getCurrentTime(), this.getClass().getSimpleName(), "Starting RoomManager and roomUpdater ");
		roomUpdater = new UpdateRoom(this);
		DebugMessages.ok();
	}

	@Override
	public void updateTemperature(String roomID, Float temperature) {
		dbController.updateTemperature(roomID, temperature);		
	}

	@Override
	public void updateHumidity(String roomID, Float humidity) {
		dbController.updateHumidity(roomID, humidity);
	}

	@Override
	public void updateNoise(String roomID, Float Noise) {
		dbController.updateNoise(roomID, Noise);
	}

	@Override
	public void updatePowerConsumption(String roomID, Float powerConsumption) {
		dbController.updatePowerConsumption(roomID, powerConsumption);
	}

	@Override
	public void updateOccupancy(String roomID, Float occupancy) {
		dbController.updateOccupancy(roomID, occupancy);
	}
}
