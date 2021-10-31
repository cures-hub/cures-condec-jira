package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertGroup extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getDecision();
	}

	@Test
	@NonTransactional
	public void testGroupAlreadyExisting() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup", element);
		assertTrue(DecisionGroupPersistenceManager.insertGroup("TestGroup", element) != -1);
	}

	@Test
	@NonTransactional
	public void testGroupNameNullElementValid() {
		assertEquals(-1, DecisionGroupPersistenceManager.insertGroup(null, element));
	}

	@Test
	@NonTransactional
	public void testGroupNameBlankElementValid() {
		assertEquals(-1, DecisionGroupPersistenceManager.insertGroup("", element));
	}

	@Test
	@NonTransactional
	public void testGroupNameValidElementNull() {
		assertEquals(-1, DecisionGroupPersistenceManager.insertGroup("TestGroup", null));
	}

	@Test
	@NonTransactional
	public void testGroupNameValidElementValid() {
		assertTrue(DecisionGroupPersistenceManager.insertGroup("TestGroup", element) != -1);
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("TestGroup"));
	}
}