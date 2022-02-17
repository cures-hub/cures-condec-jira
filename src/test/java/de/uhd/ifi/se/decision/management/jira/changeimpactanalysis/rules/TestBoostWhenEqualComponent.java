package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostWhenEqualComponent extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element is assigned the same component",
				ChangePropagationRuleType.BOOST_WHEN_EQUAL_COMPONENT.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_EQUAL_COMPONENT.getExplanation()
				.contains("is assigned to the same component"));
	}

	@Test
	public void testPropagation() {
		KnowledgeElement rootElement = KnowledgeElements.getTestKnowledgeElements().get(2);
		KnowledgeElement currentElement = KnowledgeElements.getTestKnowledgeElements().get(1);
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElementObject(rootElement);

		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_EQUAL_COMPONENT.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}
}
