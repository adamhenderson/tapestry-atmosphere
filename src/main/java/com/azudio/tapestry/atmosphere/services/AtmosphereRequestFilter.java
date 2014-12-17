package com.azudio.tapestry.atmosphere.services;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmosphereRequestFilter implements RequestFilter {

	private static final Logger log = LoggerFactory.getLogger(AtmosphereRequestFilter.class);

	private RequestGlobals requestGlobals;

	private AtmosphereFramework framework;

	public AtmosphereRequestFilter(RequestGlobals requestGlobals, AtmosphereFramework framework) {
		this.requestGlobals = requestGlobals;
		this.framework = framework;
	}

	public boolean service(Request request, Response response, RequestHandler handler) throws IOException {

		log.debug("Being Called");
		log.debug("Request Path:" + request.getPath());

		try {
			AtmosphereRequest atmosphereRequest = AtmosphereRequest.wrap(requestGlobals.getHTTPServletRequest());
			framework.doCometSupport(atmosphereRequest, AtmosphereResponse.wrap(requestGlobals.getHTTPServletResponse()));
			if (atmosphereRequest.resource().isSuspended()) {
				return true;
			}
		} catch (ServletException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		return handler.service(request, response);

	}
}
