package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteGroup extends TestSetUp {

	@Before
	public void setUp() {
		init();
		DecisionGroupPersistenceManager.insertGroup("TestGroup", KnowledgeElements.getDecision());
	}

	@Test
	@NonTransactional
	public void testGroupNullProjectKeyValid() {
		assertFalse(DecisionGroupPersistenceManager.deleteGroup(null, "TEST"));
	}

	@Test
	@NonTransactional
	public void testGroupValidProjectKeyValid() {
		DecisionGroupPersistenceManager.deleteGroup("TestGroup", "TEST");
		assertFalse(DecisionGroupPersistenceManager.getAllDecisionGroups("TEST").contains("TestGroup"));
	}
}
