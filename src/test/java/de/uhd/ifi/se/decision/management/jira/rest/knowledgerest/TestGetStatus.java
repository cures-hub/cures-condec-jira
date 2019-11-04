package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestGetStatus extends TestSetUp {

	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;
	private DecisionKnowledgeElement decisionKnowledgeElement;
	private final static String BAD_REQUEST_ERROR = "Setting element status failed due to a bad request.";

	@Before
	public void setUp() {
		super.init();
		knowledgeRest = new KnowledgeRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-3");
		decisionKnowledgeElement = new DecisionKnowledgeElementImpl(issue);
	}

	@Test
	@NonTransactional
	public void testNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.getStatus(null, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testNullFilled() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.getStatus(null, decisionKnowledgeElement).getEntity());
	}

	@Test
	@NonTransactional
	public void testFilledNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.getStatus(request, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testFilledFilled() {
		assertEquals(Response.Status.OK.getStatusCode(), knowledgeRest.getStatus(request, decisionKnowledgeElement).getStatus());
	}
}
