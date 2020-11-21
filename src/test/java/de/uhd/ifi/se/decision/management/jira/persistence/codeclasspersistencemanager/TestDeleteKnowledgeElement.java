package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteKnowledgeElement extends TestSetUp {

	private CodeClassPersistenceManager codeClassPersistenceManager;
	private ApplicationUser user;

	@Before
	@NonTransactional
	public void setUp() {
		init();
		codeClassPersistenceManager = new CodeClassPersistenceManager("TEST");
		KnowledgeElement classElement = TestInsertKnowledgeElement.createTestCodeClass();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		classElement = codeClassPersistenceManager.insertKnowledgeElement(classElement, user);
	}

	@Test
	@NonTransactional
	public void testDeleteDecisionKnowledgeElementWithIdZero() {
		assertFalse(new CodeClassPersistenceManager("Test").deleteKnowledgeElement(0, user));
	}

	@Test
	@NonTransactional
	public void testDeleteDecisionKnowledgeElementById() {
		assertTrue(codeClassPersistenceManager.deleteKnowledgeElement(1, user));
		assertEquals(0, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testDeleteDecisionKnowledgeElementNull() {
		assertFalse(codeClassPersistenceManager.deleteKnowledgeElement(null, user));
	}

	@Test
	@NonTransactional
	public void testDeleteDecisionKnowledgeElementNotInDatabase() {
		KnowledgeElement elementToBeDeleted = codeClassPersistenceManager.getKnowledgeElement(42);
		assertFalse(codeClassPersistenceManager.deleteKnowledgeElement(elementToBeDeleted, user));
	}

	@Test
	@NonTransactional
	public void testDeleteDecisionKnowledgeElement() {
		ChangedFile elementToBeDeleted = (ChangedFile) codeClassPersistenceManager.getKnowledgeElement(1);
		assertEquals("TestClass.java", elementToBeDeleted.getOldName());
		assertTrue(codeClassPersistenceManager.deleteKnowledgeElement(elementToBeDeleted, user));
		assertEquals(0, codeClassPersistenceManager.getKnowledgeElements().size());
	}
}
