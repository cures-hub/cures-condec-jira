package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestUpdateDecisionKnowledgeElement extends TestJiraIssuePersistenceManagerSetUp {

	@Test(expected = NullPointerException.class)
	public void testElementNullUserNull() {
		issueStrategy.updateDecisionKnowledgeElement((DecisionKnowledgeElement) null, null);
	}

	@Test
	public void testElementNonExistentUserNull() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertFalse(issueStrategy.updateDecisionKnowledgeElement(element, null));
	}

	@Test
	public void testElementNonExistentUserExistent() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertNotNull(issueStrategy.updateDecisionKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserExistent() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		assertNotNull(issueStrategy.updateDecisionKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserNotAuthorized() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		assertFalse(issueStrategy.updateDecisionKnowledgeElement(element, JiraUsers.BLACK_HEAD.getApplicationUser()));
	}
}
