package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteDecisionKnowledgeElement extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;
	protected static DecisionKnowledgeElement element;

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
		assertFalse(manager.deleteDecisionKnowledgeElement(-1, null));
	}

	@Test
	@NonTransactional
	public void testZeroNull() {
		assertFalse(manager.deleteDecisionKnowledgeElement(0, null));
	}

	@Test
	@NonTransactional
	public void testMoreNull() {
		assertFalse(manager.deleteDecisionKnowledgeElement(12, null));
	}

	@Test
	@NonTransactional
	public void testLessFilled() {
		assertFalse(manager.deleteDecisionKnowledgeElement(-1, user));
	}

	@Test
	@NonTransactional
	public void testZeroFilled() {
		assertFalse(manager.deleteDecisionKnowledgeElement(0, user));
	}

	@Test
	@NonTransactional
	public void testMoreFilled() {
		assertTrue(manager.deleteDecisionKnowledgeElement(1, user));
	}

}
