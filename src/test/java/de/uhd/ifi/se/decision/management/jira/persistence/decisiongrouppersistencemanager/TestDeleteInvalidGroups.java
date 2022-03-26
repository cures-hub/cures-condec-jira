package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
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
	public void testElementInvalidAndGroupNameValid() {
		KnowledgeElement elementWithInvalidId = new KnowledgeElement(-1, "TEST", "s");
		DecisionGroupPersistenceManager.insertGroup("TestGroup", elementWithInvalidId);

		KnowledgeElement elementWithOtherProject = new KnowledgeElement(42, "CONDEC", "s");
		DecisionGroupPersistenceManager.insertGroup("TestGroup2", elementWithOtherProject);
		assertTrue(DecisionGroupPersistenceManager.deleteInvalidGroups("TEST"));
	}

	@Test
	@NonTransactional
	public void testElementValidAndGroupNameValid() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup", KnowledgeElements.getDecision());
		assertFalse(DecisionGroupPersistenceManager.deleteInvalidGroups("TEST"));
	}

	@Test
	@NonTransactional
	public void testElementOnlyInGraphAndGroupNameValid() {
		KnowledgeElement element = new KnowledgeElement(-42, "TEST", "s");
		KnowledgeGraph.getInstance("TEST").addVertexNotBeingInDatabase(element);
		DecisionGroupPersistenceManager.insertGroup("TestGroup", element);
		assertFalse(DecisionGroupPersistenceManager.deleteInvalidGroups("TEST"));
	}
}