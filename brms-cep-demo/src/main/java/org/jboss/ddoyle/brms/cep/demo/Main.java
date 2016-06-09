package org.jboss.ddoyle.brms.cep.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.impl.PseudoClockScheduler;
import org.jboss.ddoyle.brms.cep.demo.commons.events.FactsLoader;
import org.jboss.ddoyle.brms.cep.demo.model.Event;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of the demo.
 * <p>
 * Loads events via the given {@link FactsLoader}, inserts them into the {@link KieSession} and fires the rules.
 * <p>
 * The {@link KieContainer} is build from the classpath. The {@link KieSession} configuration is configured in the <code>kmodule.xml</code>
 * of the KJAR on the classpath.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public class Main {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static final String EVENTS_FILE_NAME = "events.csv";

	public static void main(String[] args) {
		KieServices kieServices = KieServices.Factory.get();

		// Load the KJARs from the classpath.
		KieContainer kieContainer = kieServices.getKieClasspathContainer();

		KieSession kieSession = kieContainer.newKieSession();

		LOGGER.info("Start processing events.");

		try {
			List<Event> events;
			// Load the events.
			try (InputStream eventFileInputStream = Main.class.getClassLoader().getResourceAsStream(EVENTS_FILE_NAME)) {
				events = FactsLoader.loadEvents(eventFileInputStream);
			} catch (IOException ioe) {
				throw new RuntimeException("I/O problem loading event file. Not much we can do in this lab.", ioe);

			}
			events.stream().forEach(event -> {
				insertAndFire(kieSession, event);
			});
		} finally {
			kieSession.dispose();
		}

		LOGGER.info("Finished processing events.");
	}

	private static void insertAndFire(KieSession kieSession, Event event) {
		PseudoClockScheduler clock = kieSession.getSessionClock();
		EntryPoint ep = kieSession.getEntryPoint("BagEvents");
		ep.insert(event);
		long deltaTime = event.getTimestamp().getTime() - clock.getCurrentTime();
		if (deltaTime > 0) {
			LOGGER.debug("Advancing clock with: " + deltaTime);
			clock.advanceTime(deltaTime, TimeUnit.MILLISECONDS);
		}
		kieSession.fireAllRules();
	}

}
