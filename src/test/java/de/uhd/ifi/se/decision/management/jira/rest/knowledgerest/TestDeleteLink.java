package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteLink extends TestSetUp {

	private final static String DELETION_ERROR = "Deletion of link failed.";

	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		init();

		request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
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
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.deleteLink(request, "TEST", link).getStatus());
	}

	@Test
	public void testProjectKeyFilledRequestFilledLinkNotExistentInDatabaseDocumentationLocationMixed() {
		Link link = new Link(1, 15, DocumentationLocation.JIRAISSUETEXT, DocumentationLocation.JIRAISSUE);
		link.setType("contain");
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.deleteLink(request, "TEST", link).getStatus());
	}

	@Test
	public void testProjectKeyFilledRequestFilledLinkFilledDocumentationLocationJiraIssueComments() {
		Link link = new Link(14, 15, DocumentationLocation.JIRAISSUETEXT, DocumentationLocation.JIRAISSUETEXT);
		link.setType("contain");
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.deleteLink(request, "TEST", link).getStatus());
	}

	@Test
	public void testProjectKeyNullRequestNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
				.getEntity(), knowledgeRest.deleteLink(null, null, null).getEntity());
	}

	@Test
	public void testProjectKeyExistentRequestNullLinkNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteLink(null, "TEST", null).getEntity());
	}

	@Test
	public void testProjectExistentRequestNullLinkFilled() {
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
						.getEntity(),
				knowledgeRest.deleteLink(null, "TEST", new Link(new KnowledgeElement(), null))
						.getEntity());
	}

	@Test
	public void testProjectExistentRequestFilledElementNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
				.getEntity(), knowledgeRest.deleteLink(request, "TEST", null).getEntity());
	}

	@Test
	public void testProjectKeyNullRequestFilledLinkNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteLink(request, null, null).getEntity());
	}

	@Test
	public void testProjectKeyNullRequestNullLinkFilled() {
		Link link = new Link(14, 15, DocumentationLocation.JIRAISSUETEXT, DocumentationLocation.JIRAISSUETEXT);
		link.setType("contain");
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteLink(null, null, link).getEntity());
	}
}
