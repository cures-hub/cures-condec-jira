package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateGroupName extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getDecision();
		DecisionGroupPersistenceManager.insertGroup("TestGroup", element);
	}

	@Test
	@NonTransactional
	public void testGroupExistingInDatabase() {
		assertTrue(DecisionGroupPersistenceManager.updateGroupName("TestGroup", "ChangedGroup", "TEST"));
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("ChangedGroup"));
		assertFalse(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("TestGroup"));
	}

	@Test
	@NonTransactional
	public void testGroupNotExistingInDatabase() {
		assertFalse(DecisionGroupPersistenceManager.updateGroupName("TestUnknownGroup", "ChangedGroup", "TEST"));
	}
}
