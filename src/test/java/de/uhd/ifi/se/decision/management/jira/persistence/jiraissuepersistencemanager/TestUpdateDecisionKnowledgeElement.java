package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;

public class TestUpdateDecisionKnowledgeElement extends TestJiraIssuePersistenceManagerSetUp {

	@Test(expected = NullPointerException.class)
	public void testElementNullUserNull() {
		issueStrategy.updateDecisionKnowledgeElement((DecisionKnowledgeElement) null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testElementNonExistentUserNull() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		issueStrategy.updateDecisionKnowledgeElement(element, null);
	}

	public void testElementNonExistentUserExistent() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertNotNull(issueStrategy.updateDecisionKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserExistent() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertNotNull(issueStrategy.updateDecisionKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserNotAuthorized() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = new MockApplicationUser("WithFails");
		assertFalse(issueStrategy.updateDecisionKnowledgeElement(element, user));
	}
}
