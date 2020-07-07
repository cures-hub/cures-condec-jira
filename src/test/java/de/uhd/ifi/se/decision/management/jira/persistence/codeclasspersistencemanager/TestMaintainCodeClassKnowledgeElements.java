package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestMaintainCodeClassKnowledgeElements extends TestSetUp {

	private CodeClassPersistenceManager ccManager;

	@Before
	public void setUp() {
		init();
		ccManager = new CodeClassPersistenceManager("Test");
	}

	@Test
	@NonTransactional
	public void testMaintainCodeClassKnowledgeElementsWithoutClasses() {
		ccManager.maintainCodeClassKnowledgeElements(TestSetUpGit.GIT_URI, null, null);
		assertEquals(0, ccManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainCodeClassKnowledgeElementsWithClasses() {
		KnowledgeElement classElement;
		classElement = new KnowledgeElement();
		classElement.setProject("TEST");
		classElement.setType("Other");
		classElement.setDescription("TEST-1;");
		classElement.setSummary("TestClass.java");
		ccManager.insertKnowledgeElement(classElement, JiraUsers.SYS_ADMIN.getApplicationUser());
		ccManager.maintainCodeClassKnowledgeElements(TestSetUpGit.GIT_URI, null, null);
		assertEquals(0, ccManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testGetIssueListAsString() {
		List<String> list = new ArrayList<String>();
		list.add("123");
		list.add("456");
		assertEquals(ccManager.getIssueListAsString(list), "123;456;");
	}
}
