package it.cnr.isti.labsedc.glimpse.smartbuilding;

import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventSB;

public abstract class RoomManager extends Thread {

	public abstract void updateParameter(GlimpseBaseEventSB<Float> theUpdatedParameter);
	public abstract void updateTemperature(String roomID, Float temperature);
	public abstract void updateHumidity(String roomID, Float humidity);
	public abstract void updateNoise(String roomID, Float Noise );
	public abstract void updateSocketPower(String roomID, Float powerConsumption);
	public abstract void updateOccupancy(String roomID, Float occupancy);
	public abstract void updateLightPower(String roomID, Float lightPower);
}
