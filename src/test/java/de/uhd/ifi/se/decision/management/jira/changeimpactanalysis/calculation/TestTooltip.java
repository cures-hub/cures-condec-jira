package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import static org.junit.Assert.assertEquals;

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
    public void testCreateTooltipWithRootElement() {
        FilterSettings settings = new FilterSettings("TEST", "");
        settings.setSelectedElement("TEST-1");
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(settings.getSelectedElement());

        assertEquals("This is the source node from which the Change Impact Analysis was calculated.", Tooltip.createTooltip(element, settings));
    }

}
