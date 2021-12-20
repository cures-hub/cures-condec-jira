package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

public class TestIncludeOnlySameComponent extends TestSetUp {
    
    private KnowledgeElement currentElement;
	private Link link;
	private FilterSettings filterSettings;

    @Before
	public void setUp() {
		init();
		currentElement = new KnowledgeElement();
        KnowledgeElement rootElement = new KnowledgeElement();
		filterSettings = new FilterSettings();
		filterSettings.setSelectedElementObject(rootElement);
	}

    @Test
	public void testDescription() {
		assertEquals("Only include elements which are part of the same component",
				ChangePropagationRule.SAME_COMPONENT_ONLY.getDescription());
	}

    @Test
	public void testPropagationTrueNoComponents() {
		assertEquals(1.0, ChangePropagationRule.SAME_COMPONENT_ONLY.getFunction()
				.isChangePropagated(filterSettings, currentElement, link), 0.0);
	}
}
