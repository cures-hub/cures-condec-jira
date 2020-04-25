package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetTreeViewer extends TestSetUp {
	private ViewRest viewRest;

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		init();
	}

	@Test
	@NonTransactional
	public void testProjectKeyNullKnowledgeTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreeViewer(null, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectKeyNonExistentKnowledgeTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getTreeViewer("NotExistingProjectKey", null).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectKeyExistentKnowledgeTypeNull() {
		assertEquals(200, viewRest.getTreeViewer("TEST", null).getStatus());
	}

	@Test
	public void testProjectKeyExistentKnowledgeTypeEmpty() {
		assertEquals(200, viewRest.getTreeViewer("TEST", "").getStatus());
	}

	@Test
	public void testProjectKeyExistentKnowledgeTypeFilled() {
		assertEquals(200, viewRest.getTreeViewer("TEST", "Issue").getStatus());
	}
}
