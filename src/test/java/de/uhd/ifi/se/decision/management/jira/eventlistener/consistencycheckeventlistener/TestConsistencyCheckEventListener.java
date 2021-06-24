package de.uhd.ifi.se.decision.management.jira.eventlistener.consistencycheckeventlistener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.QualityCheckEventListenerSingleton;

public class TestConsistencyCheckEventListener extends TestSetUp {

	private QualityCheckEventListenerSingleton eventListener;

	@Before
	public void setUp() {
		TestSetUp.init();
		eventListener = (QualityCheckEventListenerSingleton) QualityCheckEventListenerSingleton.getInstance();
	}

	@Test
	public void testGetter() {
		assertEquals("Two quality check event trigger names should be registered.", 2,
				eventListener.getAllQualityCheckEventTriggerNames().size());

		assertTrue("Name 'done' should exist.", eventListener.doesQualityCheckEventTriggerNameExist("done"));

		assertFalse("Name 'none' should NOT exist.", eventListener.doesQualityCheckEventTriggerNameExist("none"));

		assertFalse("Name being null should NOT exist.", eventListener.doesQualityCheckEventTriggerNameExist(null));
	}

}
