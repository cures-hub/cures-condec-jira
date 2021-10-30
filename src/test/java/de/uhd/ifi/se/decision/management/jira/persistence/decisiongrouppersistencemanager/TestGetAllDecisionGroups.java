package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
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
		assertNotNull(new DecisionGroupPersistenceManager());
		assertTrue(DecisionGroupPersistenceManager.getAllDecisionGroups("TEST").contains("TestGroup"));
	}

	@Test
	@NonTransactional
	public void testLevelSortingWorksForSingleLevel() {
		DecisionGroupPersistenceManager.insertGroup("Realization_Level", KnowledgeElements.getDecision());

		List<String> allGroups = DecisionGroupPersistenceManager.getAllDecisionGroups("TEST");
		Iterator<String> iterator = allGroups.iterator();
		assertEquals("Realization_Level", iterator.next());
		assertEquals("TestGroup", iterator.next());
	}

	@Test
	@NonTransactional
	public void testLevelSortingWorksForAllLevels() {
		DecisionGroupPersistenceManager.insertGroup("Realization_Level", KnowledgeElements.getDecision());
		DecisionGroupPersistenceManager.insertGroup("High_Level", KnowledgeElements.getDecision());
		DecisionGroupPersistenceManager.insertGroup("UI", KnowledgeElements.getDecision());
		DecisionGroupPersistenceManager.insertGroup("Medium_Level", KnowledgeElements.getDecision());

		List<String> allGroups = DecisionGroupPersistenceManager.getAllDecisionGroups("TEST");
		Iterator<String> iterator = allGroups.iterator();
		assertEquals("High_Level", iterator.next());
		assertEquals("Medium_Level", iterator.next());
		assertEquals("Realization_Level", iterator.next());
		assertEquals("TestGroup", iterator.next());
		assertEquals("UI", iterator.next());
	}
}
