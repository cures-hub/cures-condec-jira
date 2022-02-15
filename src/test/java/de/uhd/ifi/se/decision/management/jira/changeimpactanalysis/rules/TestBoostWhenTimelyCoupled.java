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

public class TestBoostWhenTimelyCoupled extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element is timely coupled to the selected element",
				ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getExplanation()
				.contains("is timely coupled to the source element"));
	}

	@Test
	public void testPropagation() {
		KnowledgeElement rootElement = KnowledgeElements.getTestKnowledgeElements().get(0);
		KnowledgeElement nextElement = KnowledgeElements.getTestKnowledgeElements().get(1);
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElementObject(rootElement);

		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null));
	}
}
