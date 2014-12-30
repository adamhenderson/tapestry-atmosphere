package com.azudio.tapestry.atmosphere.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azudio.tapestry.atmosphere.TapestryAtmosphereSymbols;
import com.azudio.tapestry.atmosphere.services.AtmosphereRequestFilter;

public class AtmosphereModule {

	private static final Logger log = LoggerFactory.getLogger(AtmosphereModule.class);

	/**
	 * Contribute to the Application Defaults
	 * 
	 * @param configuration
	 */
	public static void contributeFactoryDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.add(TapestryAtmosphereSymbols.PUSH_PREFIX, "/push");
	}

	/**
	 * Contribute to the Application Defaults
	 * 
	 * @param configuration
	 */
	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.add(SymbolConstants.GZIP_COMPRESSION_ENABLED, false);
	}

	/**
	 * Binds Services
	 * 
	 * @param binder
	 */
	public static void bind(ServiceBinder binder) {
		binder.bind(RequestFilter.class, AtmosphereRequestFilter.class).withId("Atmosphere");
	}

	@EagerLoad
	public static AtmosphereFramework buildAtmosphereFramework(final ApplicationGlobals applicationGlobals) {

		String tapestryPackage = applicationGlobals.getServletContext().getInitParameter("tapestry.app-package");

		log.debug("Building AtmosphereFramework");

		try {
			AtmosphereFramework f;

			if ("xinitialiseUsingServletConfig".equals("initialiseUsingServletConfig")) {
				log.debug("Initialising Atmposhere using ServletConfig");
				f = initialiseUsingServletConfig(applicationGlobals);
			} else {
				log.debug("Initialising Atmposhere from scratch");

				f = new AtmosphereFramework(false, false);
				f.setBroadcasterCacheClassName("org.atmosphere.cache.DefaultBroadcasterCache");
				f.setUseNativeImplementation(true);
				f.setUseBlockingImplementation(false);
				f.addInitParameter("org.atmosphere.useWebSocketAndServlet3", "true");
				f.addInitParameter("org.atmosphere.cpr.packages", tapestryPackage + ".handlers");

				List<AtmosphereInterceptor> list = new ArrayList<AtmosphereInterceptor>();
				list.add(new AtmosphereResourceLifecycleInterceptor());
				list.add(new TrackMessageSizeInterceptor());
				list.add(new SuspendTrackerInterceptor());
				list.add(new BroadcastOnPostAtmosphereInterceptor());

				// This would come from a contribution and not be hard coded like this...
				// f.addAtmosphereHandler("/push/tapestryatmospherehandlerexample1", tapHandler1, list);
				// f.setHandlersPath("");
				// f.addAtmosphereHandler("/push/chat", new TapestryManagedServiceHander1(), list);
				// f.getAtmosphereConfig().handlers().keySet();
				// log.debug("Added Tap1Handler");

				f.init();

			}

			// This is a no-op handler for all tapestry page & component requests so a atmosphereframework can be attached to the request
			log.debug("Adding /* handler to allow requests down into Tapestry");
			f.addAtmosphereHandler("/*", new AtmosphereHandler() {

				public void onStateChange(AtmosphereResourceEvent event) throws IOException {
				}

				public void onRequest(AtmosphereResource resource) throws IOException {
				}

				public void destroy() {
				}
			});

			log.debug("Built AtmosphereFramework");

			return f;

		} catch (ServletException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	private static AtmosphereFramework initialiseUsingServletConfig(final ApplicationGlobals applicationGlobals) throws ServletException {

		AtmosphereFramework f = new AtmosphereFramework(new ServletConfig() {

			public String getServletName() {
				return "atmosphere";
			}

			public ServletContext getServletContext() {
				return applicationGlobals.getServletContext();
			}

			public Enumeration<String> getInitParameterNames() {
				return applicationGlobals.getServletContext().getInitParameterNames();
			}

			public String getInitParameter(String name) {
				return applicationGlobals.getServletContext().getInitParameter(name);
			}
		});

		return f;
	}

	/**
	 * @param configuration
	 */
	@Contribute(RequestHandler.class)
	public static void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration, @Inject @Service("Atmosphere") RequestFilter filter) {
		configuration.add("Atmosphere", filter, "after:StaticFiles");
	}

}