package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;

public class TestAbstractPersistenceManagerForSingleLocation extends TestSetUp {

	private static DecisionKnowledgeElement element;

	@BeforeClass
	public static void setUp() {
		init();
		element = new DecisionKnowledgeElementImpl();
		element.setProject("TEST");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProjectKeyNull() {
		KnowledgePersistenceManager.getOrCreate((String) null).getDefaultManagerForSingleLocation();
	}

	@Test
	public void testGetPersistenceStrategyProjectKeyNonExistent() {
		assertTrue(KnowledgePersistenceManager.getOrCreate("TESTNOT")
				.getDefaultManagerForSingleLocation() instanceof JiraIssuePersistenceManager);
	}

	@Test
	public void testGetPersistenceStrategyProjectKeyExistent() {
		assertTrue(KnowledgePersistenceManager.getOrCreate("TEST")
				.getDefaultManagerForSingleLocation() instanceof JiraIssuePersistenceManager);
	}

	@Test
	public void testGetPersistenceManagerElementExistentJiraIssue() {
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUE);
		assertTrue(KnowledgePersistenceManager
				.getManagerForSingleLocation(element) instanceof JiraIssuePersistenceManager);
	}

	@Test
	public void testGetPersistenceManagerElementExistentJiraIssueComment() {
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		assertTrue(KnowledgePersistenceManager
				.getManagerForSingleLocation(element) instanceof JiraIssueTextPersistenceManager);
	}
}