package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestSetGroupAssignment extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getDecision();
	}

	@Test
	@NonTransactional
	public void testGroupNamesNullElementValid() {
		assertFalse(DecisionGroupPersistenceManager.setGroupAssignment(null, element));
	}

	@Test
	@NonTransactional
	public void testGroupNamesNullElementNull() {
		assertFalse(DecisionGroupPersistenceManager.setGroupAssignment(Set.of("UI", "process"), null));
	}

	@Test
	@NonTransactional
	public void testGroupNamesBlankElementValid() {
		assertFalse(DecisionGroupPersistenceManager.setGroupAssignment(Set.of(" "), element));
	}

	@Test
	@NonTransactional
	public void testGroupNamesElementValid() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup", element);
		DecisionGroupPersistenceManager.setGroupAssignment(Set.of("UI", "process"), element);
		List<String> groups = DecisionGroupPersistenceManager.getGroupsForElement(element);
		assertFalse(groups.contains("TestGroup"));
		assertTrue(groups.contains("process"));
		assertTrue(groups.contains("UI"));
		assertEquals(2, groups.size());
	}

	@Test
	@NonTransactional
	public void testInheritGroupAssigment() {
		DecisionGroupPersistenceManager.setGroupAssignment(Set.of("TestGroup"), element);
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element.getLinkedDecisionProblems().get(0))
				.contains("TestGroup"));
	}

	@Test
	@NonTransactional
	public void testIsEqualCollectionTrue() {
		Set<String> setA = Set.of("UI");
		Set<String> setB = Set.of("UI");
		assertTrue(DecisionGroupPersistenceManager.isEqual(setA, setB));
		assertTrue(DecisionGroupPersistenceManager.isEqual(setB, setA));
	}

	@Test
	@NonTransactional
	public void testIsEqualCollectionFalse() {
		Set<String> setA = Set.of("UI");
		Set<String> setB = Set.of("process", "UI");
		assertFalse(DecisionGroupPersistenceManager.isEqual(setA, setB));
		assertFalse(DecisionGroupPersistenceManager.isEqual(setB, setA));
	}
}
