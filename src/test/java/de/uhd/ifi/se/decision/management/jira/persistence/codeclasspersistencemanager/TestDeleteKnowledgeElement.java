package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestDeleteKnowledgeElement extends TestSetUp {

	private CodeClassPersistenceManager codeClassPersistenceManager;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		codeClassPersistenceManager = new CodeClassPersistenceManager("Test");
		KnowledgeElement classElement = TestInsertKnowledgeElement.createTestCodeClass();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		classElement = codeClassPersistenceManager.insertKnowledgeElement(classElement, user);
	}

	@Test
	public void testDeleteDecisionKnowledgeElementWithIdZero() {
		assertFalse(codeClassPersistenceManager.deleteKnowledgeElement(0, user));
	}

	@Test
	public void testDeleteDecisionKnowledgeElement() {
		assertTrue(codeClassPersistenceManager.deleteKnowledgeElement(1, user));
		assertTrue(codeClassPersistenceManager.getKnowledgeElements().size() == 0);
	}
}
