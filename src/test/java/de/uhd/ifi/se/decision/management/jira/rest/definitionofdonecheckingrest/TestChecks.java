package de.uhd.ifi.se.decision.management.jira.rest.definitionofdonecheckingrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

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

public class TestChecks extends TestSetUp {

	protected HttpServletRequest request;
	protected DefinitionOfDoneCheckingRest dodCheckingRest;

	@Before
	public void setUp() {
		init();
		dodCheckingRest = new DefinitionOfDoneCheckingRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testDoesIssueNeedCompletenessApproval() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement(knowledgeElement);
		Response response = dodCheckingRest.doesElementNeedCompletenessApproval(request, settings);
		assertEquals(200, response.getStatus());

		settings.setSelectedElement((KnowledgeElement) null);
		response = dodCheckingRest.doesElementNeedCompletenessApproval(request, settings);
		assertEquals(400, response.getStatus());

		response = dodCheckingRest.doesElementNeedCompletenessApproval(request, null);
		assertEquals(400, response.getStatus());
	}
}
