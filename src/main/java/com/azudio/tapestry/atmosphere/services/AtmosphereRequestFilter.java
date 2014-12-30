package com.azudio.tapestry.atmosphere.services;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azudio.tapestry.atmosphere.TapestryAtmosphereSymbols;

public class AtmosphereRequestFilter implements RequestFilter {

	private static final Logger log = LoggerFactory.getLogger(AtmosphereRequestFilter.class);

	private RequestGlobals requestGlobals;

	private AtmosphereFramework framework;

	private String pushPrefix;

	public AtmosphereRequestFilter(RequestGlobals requestGlobals, AtmosphereFramework framework, @Symbol(TapestryAtmosphereSymbols.PUSH_PREFIX) String pushPrefix) {
		this.requestGlobals = requestGlobals;
		this.framework = framework;
		this.pushPrefix = pushPrefix;
	}

	public boolean service(Request request, Response response, RequestHandler handler) throws IOException {

		AtmosphereRequest atmosphereRequest = AtmosphereRequest.wrap(requestGlobals.getHTTPServletRequest());

		Action action;
		try {
			log.debug(atmosphereRequest.getRequestURL().toString());

			action = framework.doCometSupport(atmosphereRequest, AtmosphereResponse.wrap(requestGlobals.getHTTPServletResponse()));

			log.debug(action.toString());

			// If path is not / and is a Atmosphere handled request return true
			if (request.getPath().startsWith(pushPrefix)) {
				log.debug("As atmosphere handled this will return true now.");
				return true;
			}

		} catch (ServletException e) {
			e.printStackTrace();
			return true;
		}

		// If the request was not handled by atmosphere, let Tapestry continue to process the request
		return handler.service(request, response);

	}
}
