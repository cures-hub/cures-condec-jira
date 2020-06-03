package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;

public class TestMaintainCodeClassKnowledgeElements extends TestSetUp {

	private CodeClassPersistenceManager ccManager;
	@Mock
	private JiraAuthenticationContext jiraAuthenticationContext;
	@Mock
	private ComponentAccessor componentAccessor;

	@Before
	public void setUp() {
		init();
		TestSetUpGit.init();
		ccManager = new CodeClassPersistenceManager("Test");
	}

	@Test
	public void testMaintainCodeClassKnowledgeElementsWithoutClasses() {
		ccManager.maintainCodeClassKnowledgeElements(TestSetUpGit.GIT_URI, null, null);
		assertEquals(ccManager.getKnowledgeElements().size(), 0);
	}

	@Test
	public void testMaintainCodeClassKnowledgeElementsWithClasses() {
		KnowledgeElement classElement;
		classElement = new KnowledgeElement();
		classElement.setProject("TEST");
		classElement.setType("Other");
		classElement.setDescription("TEST-1;");
		classElement.setSummary("TestClass.java");
		KnowledgeElement newElement = ccManager.insertKnowledgeElement(classElement, JiraUsers.SYS_ADMIN.getApplicationUser());
		ccManager.maintainCodeClassKnowledgeElements(TestSetUpGit.GIT_URI, null, null);
		assertEquals(ccManager.getKnowledgeElements().size(), 0);
	}

	@Test
	public void testGetIssueListAsString() {
		List<String> list = new ArrayList<String>();
		list.add("123");
		list.add("456");
		assertEquals(ccManager.getIssueListAsString(list), "123;456;");
	}
}
