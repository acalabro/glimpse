package it.cnr.isti.labsedc.glimpse.smartbuilding.telegram;

import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.TelegramRequest;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;

public class TelegramManualNotifier extends Thread {

	private static RequestHandler requestHandler;


	public TelegramManualNotifier(RequestHandler reqHand) {
		TelegramManualNotifier.requestHandler = reqHand;
	}

	
	public static void sendSingleMessage(String receiverID, String textToSend) {
		TelegramRequest telegramRequest = null;
		try {
			DebugMessages.print(System.currentTimeMillis(), MessageManagerCommand.class.getClass().getSimpleName(), 
					"Sending message to: " + receiverID);
			telegramRequest = 
					TelegramRequestFactory.createSendMessageRequest(
							Long.parseLong(receiverID),textToSend, false, null, null);
			requestHandler.sendRequest(telegramRequest);
			DebugMessages.ok();
		} catch (NumberFormatException | JsonParsingException | TelegramServerException e) {
			DebugMessages.println(System.currentTimeMillis(), MessageManagerCommand.class.getClass().getSimpleName(), e.getMessage());
		}
	}
}
