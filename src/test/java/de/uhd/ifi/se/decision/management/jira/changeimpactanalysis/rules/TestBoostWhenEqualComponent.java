package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostWhenEqualComponent extends TestSetUp {
    
    private KnowledgeElement currentElement;
	private FilterSettings filterSettings;

    @Before
	public void setUp() {
		init();
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(1);
        KnowledgeElement rootElement = KnowledgeElements.getTestKnowledgeElements().get(2);
		filterSettings = new FilterSettings();
		filterSettings.setSelectedElementObject(rootElement);
	}

    @Test
	public void testDescription() {
		assertEquals("Boost when element is assigned the same component",
				ChangePropagationRule.BOOST_WHEN_EQUAL_COMPONENT.getDescription());
	}

    @Test
	public void testPropagationRootNoComponents() {
		KnowledgeElement rootElement = KnowledgeElements.getTestKnowledgeElements().get(1);
		filterSettings.setSelectedElementObject(rootElement);

		assertEquals(1.0, ChangePropagationRule.BOOST_WHEN_EQUAL_COMPONENT.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}

	@Test
	public void testPropagationEqualComponents() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(2);
		assertEquals(1.0, ChangePropagationRule.BOOST_WHEN_EQUAL_COMPONENT.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}
}
