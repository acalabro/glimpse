package it.cnr.isti.labsedc.glimpse.smartbuilding.telegram;

import org.apache.commons.net.ntp.TimeStamp;

import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.TelegramRequest;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import io.github.nixtabyte.telegram.jtelebot.response.json.Message;
import io.github.nixtabyte.telegram.jtelebot.server.impl.AbstractCommand;
import it.cnr.isti.labsedc.glimpse.smartbuilding.Room;
import it.cnr.isti.labsedc.glimpse.storage.DBController;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;

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
					"Smart Building BOT - Welcome\n\n"+
					"List of available commands:\n"+
					"status ROOM-ID\n"+
					"intrusion ROOM-ID [on/off]",
					true,
					message.getId(),
					null);
			}
			else {
				if (message.getText().toLowerCase().startsWith("status ")) {
					String roomID = "";
					Room result = null;
					try {
						roomID = message.getText().toLowerCase().substring(7, message.getText().length()); 
						result = databaseController.getRoomStatus(roomID);
						
						if (result != null) {
						telegramRequest = TelegramRequestFactory.createSendMessageRequest(
								message.getChat().getId(),
								"Room: " + roomID + "\n"+
								"Temperature: " + result.getTemperature() + " C°\n"+
								"Occupancy: " + result.getOccupancy() + "\n"+
								"Humidity: " + result.getHumidity() + " %\n"+
								"Noise: "+ result.getNoise() + " db\n"+
								"Power consumption: " + result.getPower() + " watt\n"+
								"Updated at: " + result.getUpdateDateTime().getTime(),
								true,
								message.getId(),
								null);
						}
						
					} catch (IndexOutOfBoundsException asd ){
					}
				}
			}
			
			if (telegramRequest!= null) {
				DebugMessages.print(TimeStamp.getCurrentTime(), this.getClass().getSimpleName(), "Send response to a well-formed telegram request ");
				requestHandler.sendRequest(telegramRequest);
				DebugMessages.ok();
				}
		} catch (JsonParsingException | TelegramServerException e) {
			
		}
	}
}