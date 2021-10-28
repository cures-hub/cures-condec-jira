package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestUpdateKnowledgeElement extends TestSetUp {

	private JiraIssuePersistenceManager persistenceManager;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		persistenceManager = KnowledgePersistenceManager.getInstance("TEST").getJiraIssueManager();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test(expected = NullPointerException.class)
	public void testElementNullUserNull() {
		persistenceManager.updateKnowledgeElement((KnowledgeElement) null, null);
	}

	@Test
	public void testElementNonExistentUserNull() {
		KnowledgeElement element = new KnowledgeElement();
		assertFalse(persistenceManager.updateKnowledgeElement(element, null));
	}

	@Test
	public void testElementNonExistentUserExistent() {
		KnowledgeElement element = new KnowledgeElement();
		assertNotNull(persistenceManager.updateKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserExistent() {
		KnowledgeElement element = new KnowledgeElement();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		element.setStatus("unresolved");
		assertNotNull(persistenceManager.updateKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserNotAuthorized() {
		KnowledgeElement element = new KnowledgeElement();
		element.setId(1);
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		assertFalse(persistenceManager.updateKnowledgeElement(element, JiraUsers.BLACK_HEAD.getApplicationUser()));
	}
}
