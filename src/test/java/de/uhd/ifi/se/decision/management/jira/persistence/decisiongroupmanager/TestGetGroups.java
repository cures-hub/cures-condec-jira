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
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

/**
 * Test class for the persistence of the assigned decision groups.
 */
public class TestGetGroups extends TestSetUp {

	private long id;
	private KnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		init();
		this.id = 100;
		String summary = "Test";
		String description = "Test";
		KnowledgeType type = KnowledgeType.SOLUTION;
		String projectKey = "TEST";
		String key = "Test-100";

		this.decisionKnowledgeElement = new KnowledgeElement(id, summary, description, type, projectKey, key,
				DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);

		DecisionGroupPersistenceManager.insertGroup("TestGroup1", decisionKnowledgeElement);
	}

	@Test
	public void testGetGroupsForElementNull() {
		assertNull(DecisionGroupPersistenceManager.getGroupsForElement(null));
	}

	@Test
	public void testGetGroupsForElementNotNull() {
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElement).contains("TestGroup1"));
	}

	@Test
	public void testGetGroupsForElementIdZero() {
		assertNull(DecisionGroupPersistenceManager.getGroupsForElement(0, DocumentationLocation.JIRAISSUE));
	}

	@Test
	public void testGetGroupsForElementIdNotNull() {
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(this.id, DocumentationLocation.JIRAISSUE)
				.contains("TestGroup1"));
	}

	@Test
	public void testGetGroupsForElementDocLocWrong() {
		assertNull(DecisionGroupPersistenceManager.getGroupsForElement(0, DocumentationLocation.CODE));
	}

	@Test
	public void testGetGroupsForElementDocLocNull() {
		assertNull(DecisionGroupPersistenceManager.getGroupsForElement(0, null));
	}

	@Test
	public void testGetGroupsInDatabaseGroupNull() {
		assertNull(DecisionGroupPersistenceManager.getGroupInDatabase(null, decisionKnowledgeElement));
	}

	@Test
	public void testGetGroupsInDatabaseElementNull() {
		assertNull(DecisionGroupPersistenceManager.getGroupInDatabase("TestGroup1", null));
	}

	@Test
	public void testGetGroupsInDatabaseArgsNotNull() {
		assertNotNull(DecisionGroupPersistenceManager.getGroupInDatabase("TestGroup1", decisionKnowledgeElement));
	}

	@Test
	public void testGetAllDecisionGroups() {
		assertTrue(DecisionGroupPersistenceManager.getAllDecisionGroups("TEST").contains("TestGroup1"));
	}

	@Test
	public void testGetAllDecisionElementsWithCertainGroup() {
		assertEquals(0, DecisionGroupPersistenceManager.getAllDecisionElementsWithCertainGroup("TestGroup1", "Test").size());
	}

	@Test
	public void testGetAllClassElementsWithCertainGroup() {
		KnowledgeElement element = new ChangedFile();
		element.setSummary("AbstractTestHandler.java");
		element.setDescription("TEST-3;");
		element.setProject("TEST");
		CodeClassPersistenceManager ccManager = new CodeClassPersistenceManager("TEST");
		KnowledgeElement newElement = ccManager.insertKnowledgeElement(element,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		DecisionGroupPersistenceManager.insertGroup("TestGroup2", newElement);
		assertEquals(1, DecisionGroupPersistenceManager.getAllClassElementsWithCertainGroup("TestGroup2", "TEST").size());
	}

}
