package com.azudio.tapestry.atmosphere.handlers;

import java.io.IOException;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedService(path = "/pubsub")
public class PubSubHandler {
	private final Logger logger = LoggerFactory.getLogger(PubSubHandler.class);

	@Ready
	public void onReady(final AtmosphereResource r) {
		logger.info("Browser {} connected.", r.uuid());
	}

	@Disconnect
	public void onDisconnect(AtmosphereResourceEvent event) {
		if (event.isCancelled()) {
			logger.info("Browser {} unexpectedly disconnected", event.getResource().uuid());
		} else if (event.isClosedByClient()) {
			logger.info("Browser {} closed the connection", event.getResource().uuid());
		}
	}

	@Message
	public Object onMessage(Object message) throws IOException {
		logger.info("{} just send {}", message.toString(), message.toString());
		return message;
	}

}