package de.uhd.ifi.se.decision.management.jira.rest.definitionofdonecheckingrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.rest.DefinitionOfDoneCheckingRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestHasIncompleteKnowledgeLinked  extends TestSetUp {

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
	public void testHasIncompleteKnowledgeLinked() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		String issueKey = knowledgeElement.getKey();
		Response response = dodCheckingRest.hasIncompleteKnowledgeLinked(request, issueKey);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		response = dodCheckingRest.hasIncompleteKnowledgeLinked(request, null);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
}
