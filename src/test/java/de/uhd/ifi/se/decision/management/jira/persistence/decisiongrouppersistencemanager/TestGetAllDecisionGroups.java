package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetAllDecisionGroups extends TestSetUp {

	@Before
	public void setUp() {
		init();
		DecisionGroupPersistenceManager.insertGroup("TestGroup", KnowledgeElements.getDecision());
	}

	@Test
	@NonTransactional
	public void testGetAllDecisionGroups() {
		assertTrue(DecisionGroupPersistenceManager.getAllDecisionGroups("TEST").contains("TestGroup"));
	}

	@Test
	@NonTransactional
	public void testGetAllDecisionElementsWithCertainGroup() {
		assertEquals(0,
				DecisionGroupPersistenceManager.getAllDecisionElementsWithCertainGroup("TestGroup", "Test").size());
	}

	@Test
	@NonTransactional
	@Ignore
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
