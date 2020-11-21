package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateKnowledgeElement extends TestSetUp {

	private KnowledgeElement classElement;
	private CodeClassPersistenceManager codeClassPersistenceManager;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		codeClassPersistenceManager = new CodeClassPersistenceManager("TEST");
		classElement = TestInsertKnowledgeElement.createTestCodeClass();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		classElement = codeClassPersistenceManager.insertKnowledgeElement(classElement, user);
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeElementWithElementNull() {
		assertFalse(codeClassPersistenceManager.updateKnowledgeElement(null, user));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeElementWithElementNoProject() {
		classElement.setProject((DecisionKnowledgeProject) null);
		assertFalse(codeClassPersistenceManager.updateKnowledgeElement(classElement, user));
		classElement.setProject("TEST");
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeElementWithElementNotInDatabase() {
		KnowledgeElement newClassElement = new ChangedFile();
		newClassElement.setProject("TEST");
		assertFalse(codeClassPersistenceManager.updateKnowledgeElement(newClassElement, user));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeElement() {
		classElement.setSummary("ChangedTestClass.java");
		assertTrue(codeClassPersistenceManager.updateKnowledgeElement(classElement, user));
	}
}
