package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestUpdateDecisionKnowledgeElement extends TestJiraIssuePersistenceManagerSetUp {

	@Test(expected = NullPointerException.class)
	public void testElementNullUserNull() {
		issueStrategy.updateDecisionKnowledgeElement((KnowledgeElement) null, null);
	}

	@Test
	public void testElementNonExistentUserNull() {
		KnowledgeElement element = new KnowledgeElement();
		assertFalse(issueStrategy.updateDecisionKnowledgeElement(element, null));
	}

	@Test
	public void testElementNonExistentUserExistent() {
		KnowledgeElement element = new KnowledgeElement();
		assertNotNull(issueStrategy.updateDecisionKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserExistent() {
		KnowledgeElement element = new KnowledgeElement();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		assertNotNull(issueStrategy.updateDecisionKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserNotAuthorized() {
		KnowledgeElement element = new KnowledgeElement();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		assertFalse(issueStrategy.updateDecisionKnowledgeElement(element, JiraUsers.BLACK_HEAD.getApplicationUser()));
	}
}
