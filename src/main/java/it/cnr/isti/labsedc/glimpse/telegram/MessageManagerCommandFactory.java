package it.cnr.isti.labsedc.glimpse.telegram;

import org.apache.log4j.Logger;

import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.response.json.Message;
import io.github.nixtabyte.telegram.jtelebot.server.Command;
import io.github.nixtabyte.telegram.jtelebot.server.CommandFactory;

public class MessageManagerCommandFactory implements CommandFactory {
	
	private static final Logger LOG = Logger.getLogger(MessageManagerCommandFactory.class);

	@Override
	public Command createCommand(Message message, RequestHandler requestHandler) {
		LOG.info("MESSAGGIO: " + message.getText());
		return new MessageManagerCommand(message, requestHandler);
	}
}