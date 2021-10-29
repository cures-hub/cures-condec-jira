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

public class TestDeleteInvalidGroups extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testElementNullAndGroupNameValid() {
		KnowledgeElement element = new KnowledgeElement(42, "UNKNOWNPROJECT", "s");
		DecisionGroupPersistenceManager.insertGroup("TestGroup", element);
		assertTrue(DecisionGroupPersistenceManager.deleteInvalidGroups());
	}

	@Test
	@NonTransactional
	public void testElementValidAndGroupNameValid() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup", KnowledgeElements.getDecision());
		assertFalse(DecisionGroupPersistenceManager.deleteInvalidGroups());
	}
}