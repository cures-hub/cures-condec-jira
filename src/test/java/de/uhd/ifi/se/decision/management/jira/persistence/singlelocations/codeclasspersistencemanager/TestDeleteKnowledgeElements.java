package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.codeclasspersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteKnowledgeElements extends TestSetUp {

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
	public void testDeleteDecisionKnowledgeElementsProjectKeyNull() {
		CodeClassPersistenceManager codeClassPersistenceManager = new CodeClassPersistenceManager(null);
		assertFalse(codeClassPersistenceManager.deleteKnowledgeElements());
	}

	@Test
	@NonTransactional
	public void testDeleteDecisionKnowledgeElementsProjectKeyEmpty() {
		CodeClassPersistenceManager codeClassPersistenceManager = new CodeClassPersistenceManager("");
		assertFalse(codeClassPersistenceManager.deleteKnowledgeElements());
	}

	@Test
	@NonTransactional
	public void testDeleteDecisionKnowledgeElementsProjectKeyValid() {
		assertTrue(codeClassPersistenceManager.deleteKnowledgeElements());
	}

}
