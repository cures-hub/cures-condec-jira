package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCreateLinksForNonLinkedElementsForProject extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testLinkAllUnlikedSentence() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front.  {pro} testobject {pro} some sentence in the back.");
		long id = JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);
		assertEquals(1, GenericLinkManager.getLinksForElement(id, DocumentationLocation.JIRAISSUETEXT).size());
		GenericLinkManager.deleteLinksForElement(id, DocumentationLocation.JIRAISSUETEXT);
		assertEquals(0, GenericLinkManager.getLinksForElement(id, DocumentationLocation.JIRAISSUETEXT).size());
		JiraIssueTextPersistenceManager.createLinksForNonLinkedElementsForProject("TEST");
		assertEquals(1, GenericLinkManager.getLinksForElement(id, DocumentationLocation.JIRAISSUETEXT).size());
	}

	@Test
	@NonTransactional
	public void testProjectKeyNull() {
		assertFalse(JiraIssueTextPersistenceManager.createLinksForNonLinkedElementsForProject(null));
	}

	@Test
	@NonTransactional
	public void testProjectKeyEmpty() {
		assertFalse(JiraIssueTextPersistenceManager.createLinksForNonLinkedElementsForProject(""));
	}
}
