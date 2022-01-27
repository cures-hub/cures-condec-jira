package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

public class TestStopAtSameElementType extends TestSetUp {

	private KnowledgeElement currentElement;
	private KnowledgeElement target;
	private Link link;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		currentElement = new KnowledgeElement();
		target = new KnowledgeElement();
		currentElement.setType(KnowledgeType.ARGUMENT);
		target.setType(KnowledgeType.DECISION);
		link = new Link(currentElement, target, LinkType.RELATE);
		filterSettings = new FilterSettings();
		filterSettings.setSelectedElementObject(target);
	}

	@Test
	public void testDescription() {
		assertEquals("Stop at elements with the same type as the selected element",
				ChangePropagationRuleType.STOP_AT_SAME_ELEMENT_TYPE.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.STOP_AT_SAME_ELEMENT_TYPE
			.getExplanation().contains("is not propagated after a element with the same knowledge type was reached"));
	}

	@Test
	public void testPropagationFalseSameElementType() {
		assertEquals(0.0, ChangePropagationRuleType.STOP_AT_SAME_ELEMENT_TYPE.getFunction()
				.isChangePropagated(filterSettings, target, link), 0.0);
	}

	@Test
	public void testPropagationTrueDifferentElementType() {
		filterSettings.setSelectedElementObject(currentElement);
		assertEquals(1.0, ChangePropagationRuleType.STOP_AT_SAME_ELEMENT_TYPE.getFunction()
				.isChangePropagated(filterSettings, target, link), 0.0);
	}
}