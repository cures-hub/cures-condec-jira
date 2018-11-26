package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestInsertDecisionKnowledgeElement extends TestJiraIssuePersistenceManagerSetUp {

	@Test(expected = NullPointerException.class)
	public void testElementNullUserNull() {
		issueStrategy.insertDecisionKnowledgeElement(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testElementEmptyUserNull() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		issueStrategy.insertDecisionKnowledgeElement(element, null);
	}

	@Test(expected = NullPointerException.class)
	public void testElementEmptyUserExistent() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertNotNull(issueStrategy.insertDecisionKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserExistent() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertNotNull(issueStrategy.insertDecisionKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserNotAuthorized() {
		DecisionKnowledgeElementImpl element = new DecisionKnowledgeElementImpl();
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = new MockApplicationUser("WithFails");
		assertNull(issueStrategy.insertDecisionKnowledgeElement(element, user));
	}
}
