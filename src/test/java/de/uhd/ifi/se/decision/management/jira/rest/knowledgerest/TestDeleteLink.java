package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestDeleteLink extends TestSetUpWithIssues {

	private final static String DELETION_ERROR = "Deletion of link failed.";

	private EntityManager entityManager;
	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
	}

	@Test
	@NonTransactional
	public void testProjectExistentRequestFilledLinkFilled() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("This is a test sentence.");
		DecisionKnowledgeElement sentence = comment.get(0);

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("3");
		DecisionKnowledgeElement decisionKnowledgeElement = new DecisionKnowledgeElementImpl(issue);
		decisionKnowledgeElement.setType(KnowledgeType.SOLUTION);

		Link link = new LinkImpl(sentence, decisionKnowledgeElement);
		GenericLinkManager.insertLink(link, null);

		// Test that element exists in database
		assertEquals(1, GenericLinkManager.getLinksForElement(decisionKnowledgeElement).size());
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.deleteLink("TEST", request, link).getStatus());

		// Test that element does not exist in database
		assertEquals(0, GenericLinkManager.getLinksForElement(decisionKnowledgeElement).size());
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.deleteLink("TEST", request, link).getStatus());
	}

	@Test
	public void testProjectKeyFilledRequestFilledLinkNotExistentInDatabaseDocumentationLocationMixed() {
		Link link = new LinkImpl();
		link.setSourceElement(1, "s");
		link.setDestinationElement(15, "i");
		link.setType("contain");
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.deleteLink("TEST", request, link).getStatus());
	}

	@Test
	public void testProjectKeyFilledRequestFilledLinkFilledDocumentationLocationJiraIssueComments() {
		Link link = new LinkImpl();
		link.setSourceElement(14, "s");
		link.setDestinationElement(15, "s");
		link.setType("contain");
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.deleteLink("TEST", request, link).getStatus());
	}

	@Test
	public void testProjectKeyNullRequestNullLinkNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
				.getEntity(), knowledgeRest.deleteLink(null, null, null).getEntity());
	}

	@Test
	public void testProjectKeyExistentRequestNullLinkNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteLink("TEST", null, null).getEntity());
	}

	@Test
	public void testProjectExistentRequestNullLinkFilled() {
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
						.getEntity(),
				knowledgeRest.deleteLink("TEST", null, new LinkImpl(new DecisionKnowledgeElementImpl(), null))
						.getEntity());
	}

	@Test
	public void testProjectExistentRequestFilledElementNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
				.getEntity(), knowledgeRest.deleteLink("TEST", request, null).getEntity());
	}

	@Test
	public void testProjectKeyNullRequestFilledLinkNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteLink(null, request, null).getEntity());
	}

	@Test
	public void testProjectKeyNullRequestNullLinkFilled() {
		Link link = new LinkImpl();
		link.setSourceElement(14, "s");
		link.setDestinationElement(15, "s");
		link.setType("contain");
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteLink(null, null, link).getEntity());
	}
}
