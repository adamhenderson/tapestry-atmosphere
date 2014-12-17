package com.azudio.tapestry.atmosphere.handlers;

import java.io.IOException;

import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.handler.OnMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapHandler1 extends OnMessage<Object> {

	Logger log = LoggerFactory.getLogger(TapHandler1.class);

	@Override
	public void onMessage(AtmosphereResponse response, Object message) throws IOException {
		log.debug("Got: {}", message.toString());
		response.write(message.toString());
	}

}
