package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestDeleteDecisionKnowledgeElement extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testElementNullUserNull() {
		assertFalse(issueStrategy.deleteKnowledgeElement(null, null));
	}

	@Test
	public void testElementNonExistentUserNull() {
		KnowledgeElement element = new KnowledgeElement();
		assertFalse(issueStrategy.deleteKnowledgeElement(element, null));
	}

	@Test
	public void testElementExistentUserExistent() {
		KnowledgeElement element = new KnowledgeElement();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		assertTrue(issueStrategy.deleteKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserNotAuthorized() {
		KnowledgeElement element = new KnowledgeElement();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		assertFalse(issueStrategy.deleteKnowledgeElement(element, user));
	}
}
