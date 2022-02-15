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

public class TestBoostWhenEqualCreator extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element has the same creator as the selected element",
				ChangePropagationRuleType.BOOST_WHEN_EQUAL_CREATOR.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_EQUAL_CREATOR
			.getExplanation().contains("in the knowledge graph has the same creator"));
	}

	@Test
	public void testPropagation() {
		KnowledgeElement currentElement = KnowledgeElements.getProArgument();
		KnowledgeElement rootElement = KnowledgeElements.getDecision();
		FilterSettings filterSettings = new FilterSettings("TEST", "");
        filterSettings.setSelectedElementObject(rootElement);

		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_EQUAL_CREATOR.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}
}