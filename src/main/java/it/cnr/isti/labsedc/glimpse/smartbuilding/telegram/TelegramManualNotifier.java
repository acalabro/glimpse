package it.cnr.isti.labsedc.glimpse.smartbuilding.telegram;

import java.util.List;

import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.TelegramRequest;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import it.cnr.isti.labsedc.glimpse.smartbuilding.SmartCampusUser;
import it.cnr.isti.labsedc.glimpse.storage.DBController;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;

public class TelegramManualNotifier extends Thread {

	private static RequestHandler requestHandler;
	private static DBController dbController;


	public TelegramManualNotifier(RequestHandler reqHand, DBController databaseController) {
		TelegramManualNotifier.requestHandler = reqHand;
		TelegramManualNotifier.dbController = databaseController;
	}

	
	public static void sendSingleMessage(String receiverID, String textToSend) {
		TelegramRequest telegramRequest = null;
		try {
			DebugMessages.print(System.currentTimeMillis(), MessageManagerCommand.class.getName(), 
					"Sending message to: " + receiverID);
			telegramRequest = 
					TelegramRequestFactory.createSendMessageRequest(
							Long.parseLong(receiverID),textToSend, false, null, null);
			requestHandler.sendRequest(telegramRequest);
			DebugMessages.ok();
		} catch (NumberFormatException | JsonParsingException | TelegramServerException e) {
			DebugMessages.println(System.currentTimeMillis(), MessageManagerCommand.class.getName(), e.getMessage());
		}
	}
	
	public static void notifyUnknownPersonToRoomOwnerIfIntrusionActive(String roomID) {
		List<SmartCampusUser> users = dbController.getUsersForTheRoom(roomID);
		DebugMessages.println(System.currentTimeMillis(), 
				MessageManagerCommand.class.getClass().getSimpleName(),
				"Users available for roomID: " + roomID + "  - " + users.size());
		for (int i = 0; i<users.size(); i++) {
			DebugMessages.println(System.currentTimeMillis(), MessageManagerCommand.class.getName(),"Room ID: " + roomID);
			
			boolean intrusionStatus = dbController.getIntrusionStatus(Long.parseLong(users.get(i).getTelegram_id()));
			if (intrusionStatus) {
				
			DebugMessages.println(System.currentTimeMillis(), 
					MessageManagerCommand.class.getClass().getName(),
					"Sending notification message to user: " + users.get(i).getName() + 
					" for room id: " + roomID + " due to unknown person detected into the room.");	
				sendSingleMessage(
						users.get(i).getTelegram_id(), 
						"Unknown person detected in room: " + roomID);
			}
		}
		
	}
}
