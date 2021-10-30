package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertTrue;

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
		assertTrue(DecisionGroupPersistenceManager.getAllDecisionGroups("TEST").contains("TestGroup"));
	}
}
