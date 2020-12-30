package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestDeleteDecisionKnowledgeElement extends TestSetUp {

	private JiraIssuePersistenceManager persistenceManager;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueManager();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	public void testElementNullUserNull() {
		assertFalse(persistenceManager.deleteKnowledgeElement(null, null));
	}

	@Test
	public void testElementNonExistentUserNull() {
		KnowledgeElement element = new KnowledgeElement();
		assertFalse(persistenceManager.deleteKnowledgeElement(element, null));
	}

	@Test
	public void testElementExistentUserExistent() {
		KnowledgeElement element = new KnowledgeElement();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		assertTrue(persistenceManager.deleteKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserNotAuthorized() {
		KnowledgeElement element = new KnowledgeElement();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		assertFalse(persistenceManager.deleteKnowledgeElement(element, user));
	}
}
