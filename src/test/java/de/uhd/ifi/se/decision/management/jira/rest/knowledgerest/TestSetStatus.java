package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.KnowledgeRestImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestSetStatus extends TestSetUp {

	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;
	private DecisionKnowledgeElement decisionKnowledgeElement;
	private final static String BAD_REQUEST_ERROR = "Setting element status failed due to a bad request.";

	@Before
	public void setUp() {
		super.init();
		knowledgeRest = new KnowledgeRestImpl();
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
				             .build().getEntity(), knowledgeRest.setStatus(null, null, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testNullNullFilled() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.setStatus(null, null, decisionKnowledgeElement).getEntity());
	}

	@Test
	@NonTransactional
	public void testNullFilledNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.setStatus(null, KnowledgeStatus.IDEA.toString(),
				null).getEntity());
	}

	@Test
	@NonTransactional
	public void testFilledNullNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.setStatus(request, null, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testNullFilledFilled() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.setStatus(null, KnowledgeStatus.IDEA.toString(),
				decisionKnowledgeElement).getEntity());
	}

	@Test
	@NonTransactional
	public void testFilledNullFilled() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.setStatus(request, null,
				decisionKnowledgeElement).getEntity());
	}

	@Test
	@NonTransactional
	public void testFilledFilledNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.setStatus(request, KnowledgeStatus.IDEA.toString(),
				null).getEntity());
	}

	@Test
	@NonTransactional
	public void testFilledFilledFilled() {
		assertEquals(Response.Status.OK.getStatusCode(), knowledgeRest.setStatus(request, KnowledgeStatus.IDEA.toString(),
				decisionKnowledgeElement).getStatus());
	}
}
