package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestRegisterFunctions extends TestSetUpEventListener {

	@Test
	@NonTransactional
	public void testCreation() {
		assertNotNull(listener);
	}

	@Test
	public void testAfterPropertiesSet() throws Exception {
		listener.afterPropertiesSet();
	}

	@Test
	public void testDestroy() throws Exception {
		listener.destroy();
	}

	@Test
	public void testIssueEventNull() {
		listener.onIssueEvent(null);
	}
}