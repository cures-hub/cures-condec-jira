package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCreateLink extends TestSetUp {
	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		init();

		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementFilledLinkTypeNull() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", 4, "i", 1, "i", null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledProjectKeyFilledChildKnowledgeTypeNullParentElementFilledLinkTypeNull() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", 4, "i", 1, "i", null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementFilledDocumentationLocationUnknownLinkTypeNull() {
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", 4, "", 1, "", null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementFilledDocumentationLocationJiraIssueCommentsLinkTypeNull() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{issue} testobject {issue} {decision} testobject {decision}");
		PartOfJiraIssueText sentenceIssue = comment.get(0);
		KnowledgePersistenceManager.getInstance("TEST").insertKnowledgeElement(sentenceIssue,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		PartOfJiraIssueText sentenceDecision = comment.get(1);
		KnowledgePersistenceManager.getInstance("TEST").insertKnowledgeElement(sentenceDecision,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest
				.createLink(request, "TEST", sentenceIssue.getId(), "s", sentenceDecision.getId(), "s", null)
				.getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementFilledDocumentationLocationDifferLinkTypeNull() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("{issue} testobject {issue}");
		PartOfJiraIssueText sentence = comment.get(0);
		KnowledgePersistenceManager.getInstance("TEST").insertKnowledgeElement(sentence,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", 4, "i", sentence.getId(), "s", null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullChildElementFilledParentElementFilledLinkTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.createLink(null, null, 4, "i", 1, "i", null).getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyNullChildElementFilledParentElementFilledLinkTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.createLink(request, null, 4, "i", 1, "i", null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyFilledChildElementFilledParentElementFilledLinkTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.createLink(null, "TEST", 4, "i", 1, "i", null).getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyFilledChildElementIdZeroParentElementFilledLinkTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", 0, "i", 1, "i", null).getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementIdZeroLinkTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", 4, "i", 0, "i", null).getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementFilledLinkTypeFilled() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", 4, "i", 1, "i", "relates").getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementFilledLinkTypeInvalid() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", 4, "i", 1, "i", "null").getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementEqualsChildelement() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", 4, "i", 4, "i", null).getStatus());
	}
}
