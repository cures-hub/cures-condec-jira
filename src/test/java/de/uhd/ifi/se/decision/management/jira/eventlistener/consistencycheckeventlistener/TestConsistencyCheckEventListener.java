package de.uhd.ifi.se.decision.management.jira.eventlistener.consistencycheckeventlistener;

import de.uhd.ifi.se.decision.management.jira.eventlistener.Subscriber;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.ConsistencyCheckEventListenerSingleton;
import de.uhd.ifi.se.decision.management.jira.rest.EventEmitter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestConsistencyCheckEventListener {

	private ConsistencyCheckEventListenerSingleton eventListener;

	@Before
	public void setUp() {
		eventListener = ConsistencyCheckEventListenerSingleton.getInstance();
	}

	@Test
	public void testRegisterAndUnregister() {
		Subscriber subscriber1 = new EventEmitter();
		eventListener.register(subscriber1);
		assertEquals("One subscriber should be registered.", 1, eventListener.getSubscribers().size());

		eventListener.register(null);
		assertEquals("After registering 'null' as a subscriber still only one subscriber should be registered.", 1, eventListener.getSubscribers().size());

		eventListener.unregister(null);
		assertEquals("After unregistering 'null' as a subscriber still only one subscriber should be registered.", 1, eventListener.getSubscribers().size());

		eventListener.unregister(new EventEmitter());
		assertEquals("After unregistering a not yet subscribed subscriber, still only one subscriber should be registered.", 1, eventListener.getSubscribers().size());

		eventListener.unregister(subscriber1);
		assertEquals("After unregistering a not yet subscribed subscriber, still only one subscriber should be registered.", 0, eventListener.getSubscribers().size());
	}

	@Test
	public void testGetter() {
		assertEquals("One subscriber should be registered.", 2, eventListener.getAllConsistencyCheckEventTriggerNames().size());

		assertTrue("Name 'done' should exist.", eventListener.doesConsistencyCheckEventTriggerNameExist("done"));

		assertFalse("Name 'none' should NOT exist.", eventListener.doesConsistencyCheckEventTriggerNameExist("none"));

		assertFalse("Name being null should NOT exist.", eventListener.doesConsistencyCheckEventTriggerNameExist(null));
	}

	@After
	public void cleanUp() {
		eventListener.resetSubscribers();

	}

}
