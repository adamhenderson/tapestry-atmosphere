tapestry-atmosphere
===================

An Atmosphere integration for Tapestry 5.4.

By default Atmosphere handlers annotated with @ManagedService will be added if they are in the [com.yourco.appname].handlers package.

Tapestry need to know what paths will be explicity handled by Atmosphere

Example Atmosphere Handler using the ManagesService annotation:

@ManagedService(path = "/push/tapestryatmospherehandlerexample1")
public class TapestryManagedServiceHander1 {

	private final Logger logger = LoggerFactory.getLogger(TapestryManagedServiceHander1.class);

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

	@org.atmosphere.config.service.Message()
	public Object onMessage(Object message) throws IOException {
		logger.info("{} just send {}", message.toString());
		return message;
	}

}
 