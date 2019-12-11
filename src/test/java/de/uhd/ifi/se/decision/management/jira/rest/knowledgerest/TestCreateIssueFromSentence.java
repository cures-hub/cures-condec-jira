package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.KnowledgeRestImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestCreateIssueFromSentence extends TestSetUp {

	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;
	private DecisionKnowledgeElement decisionKnowledgeElement;
	private final static String BAD_REQUEST_ERROR = "The documentation location could not be changed due to a bad request.";

	@Before
	public void setUp() {
		super.init();
		knowledgeRest = new KnowledgeRestImpl();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);

		decisionKnowledgeElement = new DecisionKnowledgeElementImpl();
		decisionKnowledgeElement.setProject("TEST");
		decisionKnowledgeElement.setSummary("Test summary");
		decisionKnowledgeElement.setType(KnowledgeType.ISSUE);
		decisionKnowledgeElement.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
	}

	@Test
	@NonTransactional
	public void testNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.createIssueFromSentence(null, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testNullFilled() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.createIssueFromSentence(null,
				decisionKnowledgeElement).getEntity());
	}

	@Test
	@NonTransactional
	public void testFilledNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				             .build().getEntity(), knowledgeRest.createIssueFromSentence(request, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testFilledFilledIntError() {
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), knowledgeRest.createIssueFromSentence(request,
				decisionKnowledgeElement).getStatus());
	}

	@Test
	@NonTransactional
	public void testFilledFilledOk() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("This is a test sentence.");
		DecisionKnowledgeElement decisionKnowledgeElementSentence = comment.get(0);
		decisionKnowledgeElementSentence.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(Response.Status.OK.getStatusCode(), knowledgeRest.createIssueFromSentence(request,
				decisionKnowledgeElementSentence).getStatus());
	}
}
