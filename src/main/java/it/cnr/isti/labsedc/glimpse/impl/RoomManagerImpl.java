package it.cnr.isti.labsedc.glimpse.impl;

import java.util.List;

import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.TelegramRequest;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandWatcher;
import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventSB;
import it.cnr.isti.labsedc.glimpse.smartbuilding.RoomManager;
import it.cnr.isti.labsedc.glimpse.smartbuilding.SmartCampusUser;
import it.cnr.isti.labsedc.glimpse.storage.DBController;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;
import it.cnr.isti.labsedc.glimpse.utils.UpdateRoom;
public class RoomManagerImpl extends RoomManager {

	private DBController dbController;
	UpdateRoom roomUpdater;
	private DefaultCommandWatcher commandWatcher;
	
	public RoomManagerImpl(DBController theControllerForTheDB, DefaultCommandWatcher commandWatcher) {
		this.dbController = theControllerForTheDB;
		this.commandWatcher = commandWatcher;
	}
	
	@Override
	public void updateParameter(GlimpseBaseEventSB<Float> theUpdatedParameter) {
		switch (theUpdatedParameter.getSensorType()) {
		case HUMIDITY:
			updateHumidity(theUpdatedParameter.getExtraDataField().toLowerCase(),theUpdatedParameter.getEventData());
			break;
		case NOISE:
			updateNoise(theUpdatedParameter.getExtraDataField().toLowerCase(),theUpdatedParameter.getEventData());
			break;
		case OCCUPANCY:
			updateOccupancy(theUpdatedParameter.getExtraDataField().toLowerCase(),theUpdatedParameter.getEventData());
			break;
		case SOCKETPOWER:
			updateSocketPower(theUpdatedParameter.getExtraDataField().toLowerCase(),theUpdatedParameter.getEventData());
			break;
		case LIGHTPOWER:
			updateLightPower(theUpdatedParameter.getExtraDataField().toLowerCase(),theUpdatedParameter.getEventData());
			break;
		case TEMPERATURE:
			updateTemperature(theUpdatedParameter.getExtraDataField().toLowerCase(),theUpdatedParameter.getEventData());
			break;
		default:
			break;
		}
	}

	public void run() {
		DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Starting RoomManager and roomUpdater ");
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
	public void updateSocketPower(String roomID, Float socketPower) {
		dbController.updateSocketPower(roomID, socketPower);
	}
	
	@Override
	public void updateLightPower(String roomID, Float lightPower) {
		dbController.updateLightPower(roomID, lightPower);	
	}

	@Override
	public void updateOccupancy(String roomID, Float occupancy) {
		dbController.updateOccupancy(roomID, occupancy);
	}

	@Override
	public void notifyAccessToRoom(String roomID) {
		List<SmartCampusUser> users = dbController.getUsersForTheRoom(roomID);
		if (users.size() > 0) {
			for (int i = 0; i<users.size(); i++) {  
				
				if (users.get(i).isIntrusion_mode()) {
					TelegramRequest telegramRequest;
					try {
						DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(),
								"Intrusion detected, sending message ");

						telegramRequest = TelegramRequestFactory.createSendMessageRequest(Long.parseLong(users.get(i).getTelegram_id()),
								"Intrusione rilevata in " + roomID + "\nPassiamo a defcon 3 e chiamatemi il S.A.C." ,true,null,null);
								commandWatcher.getRequestHandler().sendRequest(telegramRequest);
								DebugMessages.ok();
					} catch (NumberFormatException | JsonParsingException | TelegramServerException e) {
					
						System.out.println(e.getMessage());
						DebugMessages.fail();
					}
					
				}
			}
		}
	}
}
