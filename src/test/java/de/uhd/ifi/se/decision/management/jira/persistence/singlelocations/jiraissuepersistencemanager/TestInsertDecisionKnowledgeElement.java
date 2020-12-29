package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestInsertDecisionKnowledgeElement extends TestJiraIssuePersistenceManagerSetUp {

	@Test(expected = NullPointerException.class)
	public void testElementNullUserNull() {
		issueStrategy.insertKnowledgeElement(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testElementEmptyUserNull() {
		KnowledgeElement element = new KnowledgeElement();
		issueStrategy.insertKnowledgeElement(element, null);
	}

	@Test(expected = NullPointerException.class)
	public void testElementEmptyUserExistent() {
		KnowledgeElement element = new KnowledgeElement();
		assertNotNull(issueStrategy.insertKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserExistent() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		assertNotNull(issueStrategy.insertKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserNotAuthorized() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		assertNull(issueStrategy.insertKnowledgeElement(element, JiraUsers.BLACK_HEAD.getApplicationUser()));
	}
}
