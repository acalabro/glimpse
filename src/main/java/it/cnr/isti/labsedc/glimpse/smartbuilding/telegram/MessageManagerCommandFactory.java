package it.cnr.isti.labsedc.glimpse.smartbuilding.telegram;

import org.apache.log4j.Logger;

import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.response.json.Message;
import io.github.nixtabyte.telegram.jtelebot.server.Command;
import io.github.nixtabyte.telegram.jtelebot.server.CommandFactory;
import it.cnr.isti.labsedc.glimpse.storage.DBController;

public class MessageManagerCommandFactory implements CommandFactory {
	
	private static final Logger LOG = Logger.getLogger(MessageManagerCommandFactory.class);
	private DBController databaseController;

	
	public MessageManagerCommandFactory(DBController databaseController) {
		this.databaseController = databaseController;
	}
	
	@Override
	public Command createCommand(Message message, RequestHandler requestHandler) {
		LOG.info("MESSAGGIO: " + message.getText());
		return new MessageManagerCommand(message, requestHandler, databaseController); 
	}
}