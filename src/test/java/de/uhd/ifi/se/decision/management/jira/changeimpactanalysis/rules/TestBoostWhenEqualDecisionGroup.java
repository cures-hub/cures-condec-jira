package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostWhenEqualDecisionGroup extends TestSetUp {

	private KnowledgeElement currentElement;
	private KnowledgeElement rootElement;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		currentElement = KnowledgeElements.getDecision();
		rootElement = KnowledgeElements.getAlternative();
		filterSettings = new FilterSettings();
		filterSettings.setSelectedElementObject(rootElement);
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element is assigned the same decision group",
				ChangePropagationRuleType.BOOST_WHEN_EQUAL_DECISION_GROUP.getDescription());
	}

	@Test
	public void testPropagationRootNoDecisionGroups() {
		assertEquals(1.0, ChangePropagationRuleType.BOOST_WHEN_EQUAL_DECISION_GROUP.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}

	@Test
	public void testPropagationRootOnlyDecisionGroups() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup", rootElement);
		assertEquals(0.75, ChangePropagationRuleType.BOOST_WHEN_EQUAL_DECISION_GROUP.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}

	@Test
	public void testPropagationEqualDecisionGroups() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup", rootElement);
		DecisionGroupPersistenceManager.insertGroup("TestGroup", currentElement);

		assertEquals(1.0, ChangePropagationRuleType.BOOST_WHEN_EQUAL_DECISION_GROUP.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}
}