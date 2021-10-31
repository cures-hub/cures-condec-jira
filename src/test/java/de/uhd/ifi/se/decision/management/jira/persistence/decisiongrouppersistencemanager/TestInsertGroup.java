package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertGroup extends TestSetUpGit {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		super.setUp();
		element = KnowledgeElements.getDecision();
		DecisionGroupPersistenceManager.insertGroup("TestGroup1", element);
	}

	@Test
	@NonTransactional
	public void testInsertGroupAlreadyExisting() {
		assertTrue(DecisionGroupPersistenceManager.insertGroup("TestGroup1", element) != -1);
	}

	@Test
	@NonTransactional
	public void testInsertGroupNull() {
		assertTrue(DecisionGroupPersistenceManager.insertGroup(null, element) == -1);
	}

	@Test
	@NonTransactional
	public void testInsertGroupElementNull() {
		assertTrue(DecisionGroupPersistenceManager.insertGroup("TestGroup2", null) == -1);
	}

	@Test

	public void testInsertGroupArgsNotNull() {
		assertTrue(DecisionGroupPersistenceManager.insertGroup("TestGroup2", element) != -1);
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("TestGroup2"));
	}

}
