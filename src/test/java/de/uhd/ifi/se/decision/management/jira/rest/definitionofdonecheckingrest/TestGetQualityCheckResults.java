package de.uhd.ifi.se.decision.management.jira.rest.definitionofdonecheckingrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.rest.DefinitionOfDoneCheckingRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetQualityCheckResults extends TestSetUp {

	protected HttpServletRequest request;
	protected DefinitionOfDoneCheckingRest dodCheckingRest;
	protected FilterSettings settings;

	@Before
	public void setUp() {
		init();
		dodCheckingRest = new DefinitionOfDoneCheckingRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		settings = new FilterSettings("TEST", "");
	}

	@Test
	@NonTransactional
	public void testRequestValidFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				dodCheckingRest.getQualityCheckResults(request, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestValidFilterSettingsWithNoSelectedElement() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				dodCheckingRest.getQualityCheckResults(request, settings).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestValidFilterSettingsWithProjectKeyNull() {
		settings.setProjectKey(null);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				dodCheckingRest.getQualityCheckResults(request, settings).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestValidFilterSettingsWithValidSelectedElement() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		settings.setSelectedElementObject(knowledgeElement);
		assertEquals(Status.OK.getStatusCode(), dodCheckingRest.getQualityCheckResults(request, settings).getStatus());
	}
}