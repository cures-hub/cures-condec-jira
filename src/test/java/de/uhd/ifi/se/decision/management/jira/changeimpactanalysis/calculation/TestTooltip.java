package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;

public class TestTooltip extends TestSetUp {
       
    @Before
	public void setUp() {
        init();
	}

    @Test
    public void testCreateTooltipSourceElement() {
        FilterSettings settings = new FilterSettings("TEST", "");
        settings.setSelectedElement("TEST-1");
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(settings.getSelectedElement());

        assertEquals("This is the source node from which the Change Impact Analysis was calculated.",
            Tooltip.createTooltip(element, settings));
    }

    @Test
    public void testCreateTooltipNonSourceElement() {
        FilterSettings settings = new FilterSettings("TEST", "");
        settings.setSelectedElement("TEST-1");
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(settings.getSelectedElement());
        settings.setSelectedElement("TEST-2");

        String tooltip = Tooltip.createTooltip(element, settings);
        assertTrue(tooltip.contains("Overall CIA Impact Factor"));
    }
}