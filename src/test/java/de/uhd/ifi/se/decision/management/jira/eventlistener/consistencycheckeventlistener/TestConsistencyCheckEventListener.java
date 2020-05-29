package de.uhd.ifi.se.decision.management.jira.eventlistener.consistencycheckeventlistener;

import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.ConsistencyCheckEventListenerSingleton;
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
	public void testGetter() {
		assertEquals("Two consistency check event trigger names should be registered.", 2, eventListener.getAllConsistencyCheckEventTriggerNames().size());

		assertTrue("Name 'done' should exist.", eventListener.doesConsistencyCheckEventTriggerNameExist("done"));

		assertFalse("Name 'none' should NOT exist.", eventListener.doesConsistencyCheckEventTriggerNameExist("none"));

		assertFalse("Name being null should NOT exist.", eventListener.doesConsistencyCheckEventTriggerNameExist(null));
	}



}
