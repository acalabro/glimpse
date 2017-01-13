/**
 * 
 * Copyright (C) 2015 Roberto Dominguez Estrada and Juan Carlos Sedano Salas
 *
 * This material is provided "as is", with absolutely no warranty expressed
 * or implied. Any use is at your own risk.
 *
 */
package io.github.nixtabyte.telegram.jtelebot.server.impl;

import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.client.impl.DefaultRequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import io.github.nixtabyte.telegram.jtelebot.response.json.Message;
import io.github.nixtabyte.telegram.jtelebot.response.json.TelegramResponse;
import io.github.nixtabyte.telegram.jtelebot.response.json.Update;
import io.github.nixtabyte.telegram.jtelebot.server.Command;
import io.github.nixtabyte.telegram.jtelebot.server.CommandDispatcher;
import io.github.nixtabyte.telegram.jtelebot.server.CommandFactory;
import io.github.nixtabyte.telegram.jtelebot.server.CommandWatcher;

import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

/**
 * Main implementation of {@link CommandWatcher} that extends the
 * {@link AbstractCommandWatcher} behavior in order to implement a polling
 * component service for the Telegram Bot API service. This is a watcher that is
 * continuously observing when there are commands available, which are
 * originated by a Telegram User.</br></br>It has a reference to a
 * {@link CommandDispatcher} implementation so that it can bypass via its queue,
 * the group of {@link Command} instances collected on every bunch of polling
 * updates.</br></br>For the polling purpose, it uses a
 * {@link DefaultRequestHandler} implementation internally in order to send
 * requests to the Telegram Bot API service and get {@link Updates} as the
 * result.
 * 
 * @see Command
 * @see CommandWatcher
 * @see AbstractCommandWatcher
 * @see CommandDispatcher
 * @see DefaultRequestHandler
 * @see Update
 * @since 0.0.1
 * */
public class DefaultCommandWatcher extends AbstractCommandWatcher {

	private static final Logger LOG = Logger
			.getLogger(DefaultCommandWatcher.class);

	private static final long MAX_CACHE_CAPACITY = 1000;

	private CommandDispatcher commandDispatcher;

	private CommandFactory commandFactory;
	private RequestHandler requestHandler;


	private long offset;
	private long limit;
	private long timeout;

	private ConcurrentMap<String, Message> cache;

	public DefaultCommandWatcher() {
		this(0, MAX_CACHE_CAPACITY, null, null, null);
	}

	public DefaultCommandWatcher(final String telegramToken,
			CommandDispatcher commandDispatcher,
			final CommandFactory commandFactory) {
		this(0, MAX_CACHE_CAPACITY, telegramToken, commandDispatcher,
				commandFactory);
	}


	public DefaultCommandWatcher(final long delayInMillis,
			final long cacheCapacity, final String telegramToken,
			final CommandDispatcher commandDispatcher,
			final CommandFactory commandFactory) {

		super(delayInMillis);
		this.commandDispatcher = commandDispatcher;
		this.commandFactory = commandFactory;
		this.requestHandler = new DefaultRequestHandler(telegramToken);

		// TODO These parameters must be persisted (i.e. DB,
		// configuration file, etc.)
		this.offset = 0;
		this.limit = 100;
		this.timeout = 0;

		cache = new ConcurrentLinkedHashMap.Builder<String, Message>()
				.maximumWeightedCapacity(cacheCapacity).build();
	}

	@Override
	public void retrieveCommands() {
		LOG.debug("\tPolling Telegram updates (offset:" + offset + ", limit:"
				+ limit + ", timeout=" + timeout + ")...");
		TelegramResponse<?> response;
		try {
			response = requestHandler.sendRequest(
					TelegramRequestFactory.createGetUpdatesRequest(offset, limit,
							timeout));
			if (response.isSuccessful()) {
				handleUpdates(response);
			} else {
				// TODO decide what to do in case of unsuccessful response
				LOG.error("Telegram response was unsuccessful: ["
						+ response.getErrorCode() + "] "
						+ response.getDescription());
			}
		} catch (JsonParsingException e) {
			LOG.error("JSON parsing failed");
			LOG.error(e);
		} catch (TelegramServerException e) {
			LOG.error("Fail at retrieving response from telegram");
			LOG.error(e);

		}

	}

	private void handleUpdates(final TelegramResponse<?> response) {
		int newUpdatesCounter = 0;
		for (final Object updateObj : response.getResult()) {
			final Update update = (Update) updateObj;
			
			if (update.getMessage() != null) {
				LOG.trace("Watching... UpdateId:" + update.getUpdateId()
						+ " - MessageID:" + update.getMessage().getId() + " - "
						+ update.getMessage().getFromUser().getId() + ":"
						+ update.getMessage().getFromUser().getUsername());
	
				// Assert that UpdateId has not been dispatched before by reviewing
				// in cache..
				// LOG.debug(cache.keySet());
				if (!cache.containsKey(update.getUpdateId().toString())) {
					cache.put(update.getUpdateId().toString(), update.getMessage());
					newUpdatesCounter++;
					// Instantiate a new Command, attach the Message object, enqueue
					// Command via the Dispatcher
					try {
						final Command command = commandFactory.createCommand(
								update.getMessage(), requestHandler);
						commandDispatcher.enqueueCommand(command);
					} catch (Exception e) {
						// gotta catch 'em all
						LOG.error(e);
					}
					// Update offset in order to fetch a new slot the next time
					offset = update.getUpdateId().longValue() + 1L;
				}

			if (LOG.isInfoEnabled() && response.getResult().size() > 0) {
				LOG.info("\tFound " + response.getResult().size() + " updates, "
						+ newUpdatesCounter
						+ " new updates added - History cache size: "
						+ cache.size());
	
			} else {
				LOG.trace("\tFound " + response.getResult().size() + " updates, "
						+ newUpdatesCounter
						+ " new updates added - History cache size: "
						+ cache.size());
			}
		}
	}
}


	public CommandDispatcher getCommandDispatcher() {
		return commandDispatcher;
	}

	public void setCommandDispatcher(CommandDispatcher commandDispatcher) {
		this.commandDispatcher = commandDispatcher;
	}

	public RequestHandler getRequestHandler() {
		return requestHandler;
	}

	public void setRequestHandler(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

}