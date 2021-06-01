package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.codeclasspersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertKnowledgeElement extends TestSetUp {

	private KnowledgeElement classElement;
	private CodeClassPersistenceManager codeClassPersistenceManager;
	private ApplicationUser user;

	public static KnowledgeElement createTestCodeClass() {
		KnowledgeElement classElement = new ChangedFile();
		classElement.setProject("TEST");
		classElement.setSummary("TestClass.java");
		return classElement;
	}

	@Before
	public void setUp() {
		init();
		codeClassPersistenceManager = new CodeClassPersistenceManager("Test");
		classElement = createTestCodeClass();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	@NonTransactional
	public void testInsertKnowledgeElement() {
		classElement.setDescription("TEST-1;");
		KnowledgeElement newElement = codeClassPersistenceManager.insertKnowledgeElement(classElement, user);
		assertEquals(classElement.getSummary(), newElement.getSummary());
	}

	@Test
	@NonTransactional
	public void testInsertKnowledgeElementDescLength() {
		classElement.setDescription("TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;");
		KnowledgeElement newElement = codeClassPersistenceManager.insertKnowledgeElement(classElement, user);
		assertEquals(classElement.getSummary(), newElement.getSummary());
	}
}
