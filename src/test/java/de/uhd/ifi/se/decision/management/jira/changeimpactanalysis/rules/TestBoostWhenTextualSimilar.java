package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestBoostWhenTextualSimilar extends TestSetUp {

	private KnowledgeElement currentElement;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		currentElement = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		currentElement.setType(KnowledgeType.ARGUMENT);
		currentElement.setSummary("Commonly known");
		target.setType(KnowledgeType.DECISION);
		target.setSummary("MySQL");
		filterSettings = new FilterSettings();
		filterSettings.setSelectedElement(target);
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element is textual similar to the selected element",
				ChangePropagationRule.BOOST_WHEN_TEXTUAL_SIMILAR.getDescription());
	}

	@Test
	public void testPropagationFalseSameElementType() {
		assertEquals(0.47, ChangePropagationRule.BOOST_WHEN_TEXTUAL_SIMILAR.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.2);
	}

	@Test
	public void testPropagationTrueDifferentElementType() {
		filterSettings.setSelectedElement(currentElement);
		assertEquals(1.0, ChangePropagationRule.BOOST_WHEN_TEXTUAL_SIMILAR.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.0);
	}
}