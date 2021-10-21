package de.uhd.ifi.se.decision.management.jira.persistence.decisiongroupmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetGroups extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getDecision();
		DecisionGroupPersistenceManager.insertGroup("TestGroup1", element);
	}

	@Test
	@NonTransactional
	public void testGetGroupsForElementNull() {
		assertNull(DecisionGroupPersistenceManager.getGroupsForElement(null));
	}

	@Test
	@NonTransactional
	public void testGetGroupsForElementNotNull() {
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("TestGroup1"));
	}

	@Test
	@NonTransactional
	public void testGetGroupsForElementIdZero() {
		assertNull(DecisionGroupPersistenceManager.getGroupsForElement(0, DocumentationLocation.JIRAISSUE));
	}

	@Test
	@NonTransactional
	public void testGetGroupsForElementIdNotNull() {
		assertTrue(DecisionGroupPersistenceManager
				.getGroupsForElement(element.getId(), element.getDocumentationLocation()).contains("TestGroup1"));
	}

	@Test
	@NonTransactional
	public void testGetGroupsForElementDocLocWrong() {
		assertNull(DecisionGroupPersistenceManager.getGroupsForElement(0, DocumentationLocation.CODE));
	}

	@Test
	@NonTransactional
	public void testGetGroupsForElementDocLocNull() {
		assertNull(DecisionGroupPersistenceManager.getGroupsForElement(0, null));
	}

	@Test
	@NonTransactional
	public void testGetGroupsInDatabaseGroupNull() {
		assertNull(DecisionGroupPersistenceManager.getGroupInDatabase(null, element));
	}

	@Test
	@NonTransactional
	public void testGetGroupsInDatabaseElementNull() {
		assertNull(DecisionGroupPersistenceManager.getGroupInDatabase("TestGroup1", null));
	}

	@Test
	@NonTransactional
	public void testGetGroupsInDatabaseArgsNotNull() {
		assertNotNull(DecisionGroupPersistenceManager.getGroupInDatabase("TestGroup1", element));
	}

	@Test
	@NonTransactional
	public void testGetAllDecisionGroups() {
		assertTrue(DecisionGroupPersistenceManager.getAllDecisionGroups("TEST").contains("TestGroup1"));
	}

	@Test
	@NonTransactional
	public void testGetAllDecisionElementsWithCertainGroup() {
		assertEquals(0,
				DecisionGroupPersistenceManager.getAllDecisionElementsWithCertainGroup("TestGroup1", "Test").size());
	}

	@Test
	@NonTransactional
	public void testGetAllClassElementsWithCertainGroup() {
		KnowledgeElement element = new ChangedFile();
		element.setSummary("AbstractTestHandler.java");
		element.setDescription("TEST-3;");
		element.setProject("TEST");
		CodeClassPersistenceManager ccManager = new CodeClassPersistenceManager("TEST");
		KnowledgeElement newElement = ccManager.insertKnowledgeElement(element,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		DecisionGroupPersistenceManager.insertGroup("TestGroup2", newElement);
		assertEquals(1,
				DecisionGroupPersistenceManager.getAllClassElementsWithCertainGroup("TestGroup2", "TEST").size());
	}

}
