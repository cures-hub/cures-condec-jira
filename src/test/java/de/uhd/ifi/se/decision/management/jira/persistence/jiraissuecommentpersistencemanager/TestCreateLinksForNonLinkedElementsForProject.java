package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCreateLinksForNonLinkedElementsForProject extends TestJiraIssueCommentPersistenceManagerSetUp {
	@Test
	@NonTransactional
	public void testLinkAllUnlikedSentence() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc.getSentencesForCommentText(
				"some sentence in front.  {pro} testobject {pro} some sentence in the back.");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);
		assertEquals(1, GenericLinkManager.getLinksForElement(id, DocumentationLocation.JIRAISSUECOMMENT).size());
		GenericLinkManager.deleteLinksForElement(id, DocumentationLocation.JIRAISSUECOMMENT);
		assertEquals(0, GenericLinkManager.getLinksForElement(id, DocumentationLocation.JIRAISSUECOMMENT).size());
		JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForProject("TEST");
		assertEquals(1, GenericLinkManager.getLinksForElement(id, DocumentationLocation.JIRAISSUECOMMENT).size());
	}

	@Test
	@NonTransactional
	public void testProjectKeyNull() {
		JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForProject(null);
	}

	@Test
	@NonTransactional
	public void testProjectKeyEmpty() {
		JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForProject("");
	}
}
