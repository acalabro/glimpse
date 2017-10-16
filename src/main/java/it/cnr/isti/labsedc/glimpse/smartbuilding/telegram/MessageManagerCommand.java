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
		
		if (message.getText().toLowerCase().startsWith("intrusione ")) {
			
			String roomID = "";
			boolean intrusion = false;
			boolean matched = false;
			try {
				
				if (message.getText().toLowerCase().endsWith("on")) {
					roomID = message.getText().toLowerCase().substring(10, message.getText().length()-2).trim();
					intrusion = true;
					matched = true;
				}
				if (message.getText().toLowerCase().endsWith("off")) {
					roomID = message.getText().toLowerCase().substring(10, message.getText().length()-3).trim();
					intrusion = false;
					matched = true;
				}	
					
				boolean allowed = databaseController.checkIfIamAllowedToUpdateRoomIntrusionStatus(message.getFromUser().getId(), roomID);
							
				if (matched) {
					if (allowed && (roomID.length() > 2)) {
						databaseController.setIntrusionStatus(message.getFromUser().getId(), intrusion, true);
						telegramRequest = TelegramRequestFactory.createSendMessageRequest(message.getChat().getId(),
								"Stato controllo intrusione stanza " + roomID + ": " + Boolean.toString(intrusion).toUpperCase(),true,message.getId(),null);
						}
					else {
						telegramRequest = TelegramRequestFactory.createSendMessageRequest(message.getChat().getId(),
								"Utente non abilitato",true,message.getId(),null);
					}
				}
				else {
					telegramRequest = TelegramRequestFactory.createSendMessageRequest(message.getChat().getId(),
							"USO: intrusione [STANZA] [ON/OFF]",true,message.getId(),null);
					}	
				} catch (IndexOutOfBoundsException | NullPointerException | JsonParsingException asd ){
			}
		} else {
			if (message.getText().toLowerCase().startsWith("stato ")) {
		
				String roomID = "";
				Room result = null;
				try {
					roomID = message.getText().toLowerCase().substring(7, message.getText().length()); 
					result = databaseController.getRoomStatus(roomID);
					
					if (result != null) {
					telegramRequest = TelegramRequestFactory.createSendMessageRequest(message.getChat().getId(),"Stanza: " + roomID + "\n"+
							"Temperatura: " + result.getTemperature() + " C°\n"+"Presenza: " + result.getOccupancy() + "\n"+
							"Umidità: " + result.getHumidity() + " %\n"+"Rumore: "+ result.getNoise() + " db\n"+
							"Consumo lampadine: " + result.getLightpower() + " watt\n"+"Consumo prese: " 
							+ result.getSocketpower() + " watt\n"+"Aggiornato alle: " + TimeStamp.getCurrentTime().toDateString(),
							true,message.getId(),null);
						}
					} catch (IndexOutOfBoundsException | NullPointerException | JsonParsingException asd ){
					}
			} else {

				switch(message.getText().toLowerCase()) {
				case "/start":
					try {
						telegramRequest = TelegramRequestFactory.createSendMessageRequest(
								message.getChat().getId(),
								"Smart Building BOT - Benvenuto\n\nLista dei comandi disponibili:\n"
								+ "stato [STANZA]\n" 
								+"intrusione [STANZA] [ON/OFF]",true,message.getId(),null);
						//telegramRequest = TelegramRequestFactory.createSendMessageRequest(message.getChat().getId(),"Smart Building BOT - Welcome\n\n"+
						//		"List of available commands:\n"+ "status [ROOM-ID]\n"+"intrusion [ROOM-ID] [ON/OFF]",true,message.getId(),null);
					} catch (JsonParsingException | NullPointerException e1) {}
					break;
				case "ciao":
					try {
						telegramRequest = TelegramRequestFactory.createSendMessageRequest(message.getChat().getId(),
								"Faccio cose, vedo gente",true,message.getId(),null);
					} catch (JsonParsingException | NullPointerException e1) {}
					break;
				
				case "ciaone":
					try {
						telegramRequest = TelegramRequestFactory.createSendMessageRequest(message.getChat().getId(),
								"Faccio cosone, vedo gentone",true,message.getId(),null);
					} catch (JsonParsingException | NullPointerException e1) {}
					break;
				}
			}
		}
			
		if (telegramRequest!= null) {
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), 
					"Send response to telegram request: id: " + 
							message.getChat().getId() + ", username: " + message.getFromUser().getUsername());
			try {
				requestHandler.sendRequest(telegramRequest);
			} catch (JsonParsingException | TelegramServerException | NullPointerException e) {
				System.out.println();
				e.printStackTrace();
				System.out.println();
				System.out.println();
				System.out.println(e.getCause().getMessage());
				System.out.println();
				System.out.println();
				
			}
			DebugMessages.ok();
			}
		}
	
	public void sendMessageToID(Long telegramID, String textToSend) {
		try {
			TelegramRequest telegramRequest = null;
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Sending intrusion detection message ");
			telegramRequest = TelegramRequestFactory.createSendMessageRequest(telegramID,
					textToSend,true,telegramID,null);
			
			if (telegramRequest!= null)
					requestHandler.sendRequest(telegramRequest);			
			DebugMessages.ok();
		} catch (JsonParsingException| TelegramServerException | NullPointerException e) {
			DebugMessages.fail();
		}
	}
}