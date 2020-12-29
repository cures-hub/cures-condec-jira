package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteKnowledgeElement extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;
	protected static KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	@NonTransactional
	public void testLessNull() {
		assertFalse(manager.deleteKnowledgeElement(-1, null));
	}

	@Test
	@NonTransactional
	public void testZeroNull() {
		assertFalse(manager.deleteKnowledgeElement(0, null));
	}

	@Test
	@NonTransactional
	public void testMoreNull() {
		assertFalse(manager.deleteKnowledgeElement(12, null));
	}

	@Test
	@NonTransactional
	public void testLessFilled() {
		assertFalse(manager.deleteKnowledgeElement(-1, user));
	}

	@Test
	@NonTransactional
	public void testZeroFilled() {
		assertFalse(manager.deleteKnowledgeElement(0, user));
	}

	@Test
	@NonTransactional
	public void testMoreFilled() {
		assertTrue(manager.deleteKnowledgeElement(1, user));
	}

}
