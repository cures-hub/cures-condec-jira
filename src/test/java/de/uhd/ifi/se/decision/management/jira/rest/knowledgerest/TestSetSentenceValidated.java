package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSetSentenceValidated extends TestSetUp {

	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		knowledgeRest = new KnowledgeRest();
		request = new MockHttpServletRequest();
	}

	@Test
	@NonTransactional
	public void testRequestNullElementNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), knowledgeRest.setSentenceValidated(null, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestNullElementFilled() {
		KnowledgeElement decisionKnowledgeElement = JiraIssues.getIrrelevantSentence();
		decisionKnowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			knowledgeRest.setSentenceValidated(null, decisionKnowledgeElement).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			knowledgeRest.setSentenceValidated(request, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilled() {
		PartOfJiraIssueText sentence = JiraIssues.getIrrelevantSentence();
		sentence.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(Response.Status.OK.getStatusCode(),
			knowledgeRest.setSentenceValidated(request, sentence).getStatus());

		sentence = (PartOfJiraIssueText) KnowledgePersistenceManager.getOrCreate("TEST")
			.getJiraIssueTextManager()
			.getKnowledgeElement(sentence);
		assertFalse(sentence.isRelevant());
		assertTrue(sentence.getLinks().size() > 0);
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledButNotDocumentedInJiraIssueComment() {
		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-3");
		KnowledgeElement decisionKnowledgeElement = new KnowledgeElement(issue);
		assertEquals(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(),
			knowledgeRest.setSentenceValidated(request, decisionKnowledgeElement).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementNotExisting() {
		KnowledgeElement decisionKnowledgeElement = JiraIssues.getIrrelevantSentence();
		decisionKnowledgeElement.setId(4200);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			knowledgeRest.setSentenceValidated(request, decisionKnowledgeElement).getStatus());
	}
}
