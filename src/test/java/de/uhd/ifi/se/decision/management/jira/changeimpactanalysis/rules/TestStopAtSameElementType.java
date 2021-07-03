package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

public class TestStopAtSameElementType extends TestSetUp {

	private KnowledgeElement currentElement;
	private Link link;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		currentElement = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		currentElement.setType(KnowledgeType.ARGUMENT);
		target.setType(KnowledgeType.DECISION);
		link = new Link(currentElement, target, LinkType.RELATE);
		filterSettings = new FilterSettings();
		filterSettings.setSelectedElement(target);
	}

	@Test
	public void testDescription() {
		assertEquals("Stop at elements with the same type as the selected element",
				ChangePropagationRule.STOP_AT_SAME_ELEMENT_TYPE.getDescription());
	}

	@Test
	public void testPropagationFalseSameElementType() {
		assertFalse(ChangePropagationRule.STOP_AT_SAME_ELEMENT_TYPE.getPredicate().isChangePropagated(filterSettings,
				currentElement, link));
	}

	@Test
	public void testPropagationTrueDifferentElementType() {
		filterSettings.setSelectedElement(currentElement);
		assertTrue(ChangePropagationRule.STOP_AT_SAME_ELEMENT_TYPE.getPredicate().isChangePropagated(filterSettings,
				currentElement, link));
	}
}