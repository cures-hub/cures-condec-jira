package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteLink extends TestSetUp {

	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		init();
		request = new MockHttpServletRequest();
	}

	@Test
	@NonTransactional
	public void testProjectExistentRequestFilledLinkFilled() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("This is a test sentence.");
		KnowledgeElement sentence = comment.get(0);

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-3");
		KnowledgeElement decisionKnowledgeElement = new KnowledgeElement(issue);
		decisionKnowledgeElement.setType(KnowledgeType.SOLUTION);

		Link link = new Link(sentence, decisionKnowledgeElement);
		GenericLinkManager.insertLink(link, null);

		// Test that element exists in database
		assertEquals(1, GenericLinkManager.getLinksForElement(decisionKnowledgeElement).size());
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.deleteLink(request, "TEST", link).getStatus());

		// Test that element does not exist in database
		assertEquals(0, GenericLinkManager.getLinksForElement(decisionKnowledgeElement).size());
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.deleteLink(request, "TEST", link).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectKeyFilledRequestFilledLinkNotExistentInDatabaseDocumentationLocationMixed() {
		Link link = new Link(1, 15, DocumentationLocation.JIRAISSUETEXT, DocumentationLocation.JIRAISSUE);
		link.setType("contain");
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.deleteLink(request, "TEST", link).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectKeyFilledRequestFilledLinkNotExistentInDatabaseButTransitivelyLinked() {
		KnowledgeElement source = KnowledgeElements.getTestKnowledgeElement();
		KnowledgeElement target = KnowledgeElements.getProArgument();
		Link link = new Link(source, target);
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.deleteLink(request, "TEST", link).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectKeyFilledRequestFilledLinkFilledDocumentationLocationJiraIssueComments() {
		Link link = new Link(14, 15, DocumentationLocation.JIRAISSUETEXT, DocumentationLocation.JIRAISSUETEXT);
		link.setType("contain");
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.deleteLink(request, "TEST", link).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectKeyNullRequestNullLinkNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.deleteLink(null, null, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectKeyExistentRequestNullLinkNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.deleteLink(null, "TEST", null).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectExistentRequestNullLinkFilled() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.deleteLink(null, "TEST", new Link(new KnowledgeElement(), null)).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectExistentRequestFilledElementNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.deleteLink(request, "TEST", null).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectKeyNullRequestFilledLinkNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.deleteLink(request, null, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testProjectKeyNullRequestNullLinkFilled() {
		Link link = new Link(14, 15, DocumentationLocation.JIRAISSUETEXT, DocumentationLocation.JIRAISSUETEXT);
		link.setType("contain");
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.deleteLink(null, null, link).getStatus());
	}
}
