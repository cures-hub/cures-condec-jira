package de.uhd.ifi.se.decision.management.jira.persistence;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;

public class TestAbstractPersistenceManager extends TestSetUpWithIssues {
	
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		initialization();
		element = new DecisionKnowledgeElementImpl();		
		element.setProject("TEST");
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testProjectKeyNull() {
		AbstractPersistenceManager.getPersistenceStrategy(null);
	}

	@Test
	public void testGetPersistenceStrategyProjectKeyNonExistent() {
		assertTrue(AbstractPersistenceManager.getPersistenceStrategy("TESTNOT") instanceof JiraIssuePersistenceManager);
	}

	@Test
	public void testGetPersistenceStrategyProjectKeyExistent() {
		assertTrue(AbstractPersistenceManager.getPersistenceStrategy("TEST") instanceof JiraIssuePersistenceManager);
	}
	
	@Test
	public void testGetPersistenceManagerElementExistentJiraIssue() {
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUE);
		assertTrue(AbstractPersistenceManager.getPersistence(element) instanceof JiraIssuePersistenceManager);
	}
	
	@Test
	public void testGetPersistenceManagerElementExistentActiveObject() {
		element.setDocumentationLocation(DocumentationLocation.ACTIVEOBJECT);
		assertTrue(AbstractPersistenceManager.getPersistence(element) instanceof ActiveObjectPersistenceManager);
	}
	
	@Test
	public void testGetPersistenceManagerElementExistentJiraIssueComment() {
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUECOMMENT);
		assertTrue(AbstractPersistenceManager.getPersistence(element) instanceof JiraIssueCommentPersistenceManager);
	}
}