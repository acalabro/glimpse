package it.cnr.isti.labsedc.glimpse.telegram;

import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.TelegramRequest;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import io.github.nixtabyte.telegram.jtelebot.response.json.Message;
import io.github.nixtabyte.telegram.jtelebot.server.impl.AbstractCommand;

public class MessageManagerCommand extends AbstractCommand {
	public MessageManagerCommand(Message message, RequestHandler requestHandler) {
		super(message, requestHandler);
	}

	@Override
	public void execute() {
		try {
			if (message.getText().compareTo("Ciao") == 0) { 
			TelegramRequest telegramRequest = TelegramRequestFactory.createSendMessageRequest(
					message.getChat().getId(),
					"Faccio cose, vedo gente",
					true,
					message.getId(),
					null);
			
			requestHandler.sendRequest(telegramRequest);
			}
		} catch (JsonParsingException | TelegramServerException e) {
			e.printStackTrace();
		}
	}
}