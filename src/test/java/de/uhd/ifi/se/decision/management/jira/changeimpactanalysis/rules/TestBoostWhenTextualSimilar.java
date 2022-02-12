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
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestBoostWhenTextualSimilar extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element is textual similar to the selected element",
				ChangePropagationRuleType.BOOST_WHEN_TEXTUAL_SIMILAR.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_TEXTUAL_SIMILAR
			.getExplanation().contains("is textual similar to the selected element"));
	}

	@Test
	public void testPropagation() {
		KnowledgeElement currentElement = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		currentElement.setType(KnowledgeType.ARGUMENT);
		currentElement.setSummary("Commonly known");
		target.setType(KnowledgeType.DECISION);
		target.setSummary("MySQL");
		
		FilterSettings filterSettings = new FilterSettings();
		filterSettings.setSelectedElementObject(target);

		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_TEXTUAL_SIMILAR.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}
}