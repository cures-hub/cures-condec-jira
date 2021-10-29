package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetGroupsForElement extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getDecision();
		DecisionGroupPersistenceManager.insertGroup("TestGroup", element);
		DecisionGroupPersistenceManager.insertGroup("High_Level", element);
	}

	@Test
	@NonTransactional
	public void testElementNull() {
		assertEquals(0, DecisionGroupPersistenceManager.getGroupsForElement(null).size());
	}

	@Test
	@NonTransactional
	public void testElementNotNull() {
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("TestGroup"));
	}

	@Test
	@NonTransactional
	public void testElementIdZero() {
		KnowledgeElement invalidElement = new KnowledgeElement();
		invalidElement.setDocumentationLocation(DocumentationLocation.JIRAISSUE);
		assertEquals(0, DecisionGroupPersistenceManager.getGroupsForElement(invalidElement).size());
	}

	@Test
	@NonTransactional
	public void testElementIdValid() {
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("TestGroup"));
	}

	@Test
	@NonTransactional
	public void testElementDocumentationLocationNull() {
		KnowledgeElement invalidElement = new KnowledgeElement();
		invalidElement.setId(1);
		assertEquals(0, DecisionGroupPersistenceManager.getGroupsForElement(invalidElement).size());
	}
}
