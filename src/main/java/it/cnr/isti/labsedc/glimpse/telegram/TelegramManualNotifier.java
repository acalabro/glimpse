package it.cnr.isti.labsedc.glimpse.telegram;

import java.util.List;

import it.cnr.isti.labsedc.glimpse.smartbuilding.SmartCampusUser;
import it.cnr.isti.labsedc.glimpse.storage.DBController;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;

public class TelegramManualNotifier extends Thread {

	private static DBController dbController;
	private static GlimpseTelegramBot glimpseBot;


	public TelegramManualNotifier(GlimpseTelegramBot glimpseBot, DBController databaseController) {
		TelegramManualNotifier.dbController = databaseController;
		TelegramManualNotifier.glimpseBot = glimpseBot;
	}

//	
//	public static void sendSingleMessage(String receiverID, String textToSend) {
//		TelegramRequest telegramRequest = null;
//		try {
//			DebugMessages.print(System.currentTimeMillis(), MessageManagerCommand.class.getName(), 
//					"Sending message to: " + receiverID);
//			telegramRequest = 
//					TelegramRequestFactory.createSendMessageRequest(
//							Long.parseLong(receiverID),textToSend, false, null, null);
//			requestHandler.sendRequest(telegramRequest);
//			DebugMessages.ok();
//		} catch (NumberFormatException | JsonParsingException | TelegramServerException e) {
//			DebugMessages.println(System.currentTimeMillis(), MessageManagerCommand.class.getName(), e.getMessage());
//		}
//	}
//	
	public static void notifyUnknownPersonToRoomOwnerIfIntrusionActive(String roomID) {
		List<SmartCampusUser> users = dbController.getUsersForTheRoom(roomID);
		DebugMessages.println(System.currentTimeMillis(), 
				TelegramManualNotifier.class.getSimpleName(),
				"Users available for roomID: " + roomID + "  - " + users.size());
		for (int i = 0; i<users.size(); i++) {
			DebugMessages.println(System.currentTimeMillis(), TelegramManualNotifier.class.getSimpleName(),"Room ID: " + roomID);
			
			boolean intrusionStatus = dbController.getIntrusionStatus(Integer.parseInt(users.get(i).getTelegram_id()));
			if (intrusionStatus) {
				
			DebugMessages.println(System.currentTimeMillis(), 
					TelegramManualNotifier.class.getClass().getSimpleName(),
					"Sending notification message to user: " + users.get(i).getName() + 
					" for room id: " + roomID + " due to unknown person detected into the room.");	
				glimpseBot.sendMessageToID(users.get(i).getTelegram_id(), 
						"Unknown person detected in room: " + roomID);
			}
		}
		
	}
}
