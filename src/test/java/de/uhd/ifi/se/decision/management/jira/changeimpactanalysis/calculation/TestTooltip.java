package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestTooltip extends TestSetUp {
       
    protected KnowledgeElement rootElement;
    protected KnowledgeElementWithImpact nextElementWithImpact;
    protected FilterSettings settings;

    @Before
	public void setUp() {
        init();
        @SuppressWarnings("unused")
        Tooltip tooltip = new Tooltip();
        rootElement = KnowledgeElements.getTestKnowledgeElements().get(0);
        settings = new FilterSettings("TEST", "");
        settings.setSelectedElementObject(rootElement);
        nextElementWithImpact = new KnowledgeElementWithImpact(KnowledgeElements.getTestKnowledgeElements().get(1));
	}

    @Test
    public void testCreateTooltipSourceElement() {
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(rootElement);

        assertEquals("This is the source element from which the Change Impact Analysis was calculated.",
            Tooltip.createTooltip(element, settings));
    }

    @Test
    public void testCreateTooltipNonSourceElement() {
        Map<String, Double> propagationRuleMap = new HashMap<>();
        propagationRuleMap.put("Exclude elements which are not part of at least one equal component", 1.0);
        nextElementWithImpact.setPropagationRules(propagationRuleMap);
        String tooltip = Tooltip.createTooltip(nextElementWithImpact, settings);

        assertTrue(tooltip.contains("Overall CIA Impact Factor"));
        assertTrue(tooltip.contains("Propagation Rule Value"));
    }

    @Test
    public void testCreateTooltipNonSourceElementNoPropagationRules() {
        Map<String, Double> propagationRules = new HashMap<>();
        nextElementWithImpact.setPropagationRules(propagationRules);

        String tooltip = Tooltip.createTooltip(nextElementWithImpact, settings);
        assertFalse(tooltip.contains("Propagation Rule Value"));
    }

    @Test
    public void testCreateTooltipLinkRecommendationElement() {
        assertEquals("This element is not implicitly linked to the source element but has been included as a result of the link recommendation.",
            Tooltip.createLinkRecommendationTooltip());
    }
}