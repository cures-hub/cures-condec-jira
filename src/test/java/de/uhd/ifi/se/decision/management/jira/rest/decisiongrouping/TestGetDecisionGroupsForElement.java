package de.uhd.ifi.se.decision.management.jira.rest.decisiongrouping;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGroupingRest;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDecisionGroupsForElement extends TestSetUp {

	private DecisionGroupingRest decisionGroupingRest;
	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		decisionGroupingRest = new DecisionGroupingRest();
		element = KnowledgeElements.getDecision();
		DecisionGroupPersistenceManager.insertGroup("TestGroup", element);
	}

	@Test
	@NonTransactional
	public void testKnowledgeElementNull() {
		Response response = decisionGroupingRest.getDecisionGroupsForElement(null);
		assertEquals("[]", response.getEntity().toString());
	}

	@Test
	@NonTransactional
	public void testKnowledgeElementWithInvalidAttributes() {
		KnowledgeElement element = new KnowledgeElement();
		Response response = decisionGroupingRest.getDecisionGroupsForElement(element);
		assertEquals("[]", response.getEntity().toString());
	}

	@Test
	@NonTransactional
	public void testKnowledgeElementValidOneGroup() {
		Response response = decisionGroupingRest.getDecisionGroupsForElement(element);
		assertEquals("[TestGroup]", response.getEntity().toString());
	}

	@Test
	@NonTransactional
	public void testKnowledgeElementValidTwoGroups() {
		DecisionGroupPersistenceManager.insertGroup("High_Level", element);
		Response response = decisionGroupingRest.getDecisionGroupsForElement(element);
		assertEquals("[High_Level, TestGroup]", response.getEntity().toString());
	}
}