package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import static junit.framework.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestMaintainCodeClassKnowledgeElements extends TestSetUp {

	private CodeClassPersistenceManager codeClassPersistenceManager;

	@Before
	public void setUp() {
		init();
		codeClassPersistenceManager = new CodeClassPersistenceManager("Test");
	}

	@Test
	@NonTransactional
	public void testMaintainCodeClassKnowledgeElementsWithoutClasses() {
		codeClassPersistenceManager.maintainCodeClassKnowledgeElements(null);
		assertEquals(0, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainCodeClassKnowledgeElementsWithClasses() {
		KnowledgeElement classElement = TestInsertKnowledgeElement.createTestCodeClass();
		codeClassPersistenceManager.insertKnowledgeElement(classElement, JiraUsers.SYS_ADMIN.getApplicationUser());
		codeClassPersistenceManager.maintainCodeClassKnowledgeElements(null);
		assertEquals(0, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testGetIssueListAsString() {
		Set<String> list = new HashSet<String>();
		list.add("123");
		list.add("456");
		assertEquals(codeClassPersistenceManager.getIssueListAsString(list), "123;456;");
	}
}
