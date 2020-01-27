package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.ViewRestImpl;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetTreeViewerForSingleElement extends TestSetUp {
	private ViewRest viewRest;

	@Before
	public void setUp() {
		viewRest = new ViewRestImpl();
		init();
	}

	@Test
	@NonTransactional
	public void testJiraIssueKeyNullFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getTreeViewerForSingleElement(null, null, null).getStatus());
	}
}
