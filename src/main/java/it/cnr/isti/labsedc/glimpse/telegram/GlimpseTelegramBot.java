package it.cnr.isti.labsedc.glimpse.telegram;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ntp.TimeStamp;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.vdurmont.emoji.EmojiParser;

import it.cnr.isti.labsedc.glimpse.smartbuilding.Room;
import it.cnr.isti.labsedc.glimpse.storage.DBController;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;

public class GlimpseTelegramBot extends TelegramLongPollingBot {

	private DBController databaseController;
	public String telegramToken;
	public String telegramBotUsername;
	
	public GlimpseTelegramBot(DBController databaseController, String telegramToken, String telegramBotUsername) {
		this.databaseController = databaseController;
		this.telegramToken = telegramToken;
		this.telegramBotUsername = telegramBotUsername;
	}

	public void onUpdateReceived(Update update) {

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            SendMessage message;
            String roomID;
            
            switch (message_text) {
			case "/start": {
				message = new SendMessage().setChatId(chat_id).setText("Benvenuto su Smart Building BOT, seleziona un comando");
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
                KeyboardRow row = new KeyboardRow();
                row.add("Ciao");
                row.add("Stato stanza");
                row.add("Ciaone");
                keyboard.add(row);
                row = new KeyboardRow();
                row.add("Attiva controllo intrusione");
                row.add("Disattiva controllo intrusione");
                row.add("Stato controllo intrusione");
                keyboard.add(row);
                keyboardMarkup.setKeyboard(keyboard);
                message.setReplyMarkup(keyboardMarkup);
    			break;
			}
			case "Attiva controllo intrusione": {
				roomID = databaseController.setIntrusionStatus(update.getMessage().getFrom().getId(), true, true);
				message = new SendMessage().setChatId(chat_id).setText("\u2705 Controllo intrusione attivo per la stanza: " + roomID);
				break;
			}
			case "Disattiva controllo intrusione": {
				roomID = databaseController.setIntrusionStatus(update.getMessage().getFrom().getId(), false, true);
				message = new SendMessage().setChatId(chat_id).setText("\u274E Controllo intrusione non attivo per la stanza: " + roomID);
				break;
			}
			
			case "Stato controllo intrusione": {
				boolean intrusionStatus = databaseController.getIntrusionStatus(update.getMessage().getFrom().getId());
				if (intrusionStatus) {
					message = new SendMessage().setChatId(chat_id).setText("Controllo intrusione attivo \u2705");
				} else 
					message = new SendMessage().setChatId(chat_id).setText("Controllo intrusione non attivo \u274E");
				break;
			}
			
			case "Stato stanza": {
				String localRoomID = databaseController.getRoomIDforTelegramUser(update.getMessage().getFrom().getId());
				Room result = databaseController.getRoomStatus(localRoomID);
				
				if (result != null)  {				
				message = new SendMessage().setChatId(chat_id).setText(
						EmojiParser.parseToUnicode(":office: ") + "Stanza: " + result.getRoomID() + "\n"
						+ EmojiParser.parseToUnicode(":rainbow: ") + "Temperatura: " + result.getTemperature() + " C°\n"
						+ EmojiParser.parseToUnicode(":eyes: ") + "Presenza: " + result.getOccupancy() +"\n"
						+ EmojiParser.parseToUnicode(":droplet: ") +  "Umidità: " + result.getHumidity() + " %\n"
						+ EmojiParser.parseToUnicode(":notes: ") + "Rumore: "+ result.getNoise() + " db\n"
						+ EmojiParser.parseToUnicode(":bulb: ") + "Consumo lampadine: " + result.getLightpower() + " watt\n"
						+ EmojiParser.parseToUnicode(":electric_plug: ") + "Consumo prese: " + result.getSocketpower() + " watt\n"
						+ EmojiParser.parseToUnicode(":watch: ") + "Aggiornato il: " + TimeStamp.getCurrentTime().toDateString());
				} else
					message = new SendMessage().setChatId(chat_id).setText("Per la stanza: " + localRoomID + 
							" al momento non sono presenti dati.\n" +
							"Riprova più tardi. " + EmojiParser.parseToUnicode(":see_no_evil: :hear_no_evil: :speak_no_evil:"));
				
				break;
			}
			
			
			case "Ciao": {
				message = new SendMessage().setChatId(chat_id).setText(EmojiParser.parseToUnicode(":wave:") + " Faccio cose, vedo gente");
				break;
			}
			case "Ciaone": {
				message = new SendMessage().setChatId(chat_id).setText(EmojiParser.parseToUnicode(":open_hands:") + " Faccio cosone, vedo gentone");
				break;
			}
			default: {
				message = new SendMessage().setChatId(chat_id).setText("Comando sconosciuto");
				break;
			}
		}
        try {
        		DebugMessages.println(System.currentTimeMillis(), this.getClass().getSimpleName(), "Ricevuta richiesta telegram " + message.getChatId());
        		execute(message); // Sending our message object to user
        		} catch (TelegramApiException e) {
        			e.printStackTrace(); }
        }
	}
	
    @Override
    public String getBotUsername() {
        // Return bot username
        return telegramBotUsername;
    }

	@Override
	public String getBotToken() {
		return telegramToken;
	}
	
	public void sendMessageToID(String telegramID, String textToSend) {
		try {
			SendMessage message = new SendMessage();
			message.setChatId(telegramID).setText(textToSend);
					
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Sending message to: " + telegramID.toString());
			execute(message);
			DebugMessages.ok();
			} catch (TelegramApiException e) {
			DebugMessages.fail();
		}
	}
}
