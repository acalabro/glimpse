package it.cnr.isti.labsedc.glimpse.smartbuilding.telegram;

import java.util.Vector;

import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.TelegramRequest;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import io.github.nixtabyte.telegram.jtelebot.response.json.Message;
import io.github.nixtabyte.telegram.jtelebot.server.impl.AbstractCommand;
import it.cnr.isti.labsedc.glimpse.storage.DBController;

public class MessageManagerCommand extends AbstractCommand {
	private DBController databaseController;

	public MessageManagerCommand(Message message, RequestHandler requestHandler, DBController databaseController) {
		super(message, requestHandler);
		this.databaseController = databaseController;
	}

	@Override
	public void execute() {
		TelegramRequest telegramRequest = null;
		try {
			if (message.getText().compareTo("/start") == 0) { 
			telegramRequest = TelegramRequestFactory.createSendMessageRequest(
					message.getChat().getId(),
					"Smart Building BOT - Benvenuto\n\n"+
					"I comandi disponibili sono:\n"+
					"status ID-STANZA\n"+
					"intrusion ID-STANZA [on/off]",
					true,
					message.getId(),
					null);
			}
			else {
				if (message.getText().toLowerCase().startsWith("status ")) {
					String roomID = "";
					Vector<Float> results = new Vector<Float>(5);
					try {
						roomID = message.getText().substring(7, message.getText().length()); 
						results = databaseController.getRoomStatus(roomID);
						
						if (results.size() > 0) {
						telegramRequest = TelegramRequestFactory.createSendMessageRequest(
								message.getChat().getId(),
								"Room: " + roomID + "\n"+
								"Temperature: " + results.get(0) + "\n"+
								"Occupancy: " + results.get(1) + "\n"+
								"Humidity: " + results.get(2) + "\n"+
								"Noise: "+ results.get(3) + "\n"+
								"Power consumption: " + results.get(4),
								true,
								message.getId(),
								null);
						}
						
					} catch (IndexOutOfBoundsException asd ){
						System.out.println();
					}
					
				}
			}
			
			if (telegramRequest!= null)
				requestHandler.sendRequest(telegramRequest);
		} catch (JsonParsingException | TelegramServerException e) {
			e.printStackTrace();
		}
	}
}