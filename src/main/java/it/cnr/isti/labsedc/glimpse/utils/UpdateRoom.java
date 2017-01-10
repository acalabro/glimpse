package it.cnr.isti.labsedc.glimpse.utils;

import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventSB;
import it.cnr.isti.labsedc.glimpse.smartbuilding.RoomManager;

public class UpdateRoom {
	private static RoomManager roomManager;

	public UpdateRoom(RoomManager roomManager) {
		UpdateRoom.roomManager = roomManager;
	}

	public static void SetValue(GlimpseBaseEventSB<Float> theEvent) {
		roomManager.updateParameter(theEvent);
	}
}
