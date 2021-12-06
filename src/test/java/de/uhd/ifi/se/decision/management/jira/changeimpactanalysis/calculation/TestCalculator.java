package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;

public class TestCalculator extends TestSetUp {
    
    @Before
	public void setUp() {
		init();
	}

    @Test
    public void testCalculateChangeImpact() {
        FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-1");
		KnowledgeElementWithImpact rootElement = new KnowledgeElementWithImpact(settings.getSelectedElement());
        List<KnowledgeElementWithImpact> impactedElements = new ArrayList<>();
        impactedElements.add(rootElement);

        impactedElements = Calculator.calculateChangeImpact(
            settings.getSelectedElement(), 1.0, settings, impactedElements, (long) settings.getLinkDistance());
        
        assertEquals(7, impactedElements.size());
        assertEquals(1.0, impactedElements.get(0).getImpactValue(), 0.05);
        assertEquals(1.0, impactedElements.get(0).getParentImpact(), 0.05);
        assertEquals(1.0, impactedElements.get(0).getLinkTypeWeight(), 0.05);
        assertEquals(1.0, impactedElements.get(0).getRuleBasedValue(), 0.05);
        assertEquals(0, impactedElements.get(0).getPropagationRules().size());
        assertEquals(0.75, impactedElements.get(1).getImpactValue(), 0.05);
        assertEquals(3, impactedElements.get(1).getPropagationRules().size());
    }
}
