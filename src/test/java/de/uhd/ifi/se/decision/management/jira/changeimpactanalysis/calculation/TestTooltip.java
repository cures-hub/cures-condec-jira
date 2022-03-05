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
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
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

    @Test
	public void testGenerateImpactExplanationPropagationRule() {
		double impactValue = 0.75;
		double parentImpact = 1.0;
		double ruleBasedValue = 0.5;
		double decayValue = 0.25;
		String explanation = Tooltip.generateImpactExplanation(
			parentImpact, ruleBasedValue, decayValue, impactValue, "", 0);

		assertTrue(explanation.contains("mainly due to a used propagation rule."));

		decayValue = 1.0;
		explanation = Tooltip.generateImpactExplanation(
			parentImpact, ruleBasedValue, decayValue, impactValue, "", 0);

		assertTrue(explanation.contains("mainly due to the decay value."));

	}

	@Test
	public void testGenerateImpactExplanationParentImpact() {
		double impactValue = 0.25;
		double parentImpact = 0.5;
		double ruleBasedValue = 1.0;
		double decayValue = 0.25;
		String explanation = Tooltip.generateImpactExplanation(
			parentImpact, ruleBasedValue, decayValue, impactValue, "", 0);

		assertTrue(explanation.contains("mainly due to its parent having a lowered impact score."));
	}

	@Test
	public void testGenerateImpactExplanationDecayValue() {
		double impactValue = 0.25;
		double parentImpact = 1.0;
		double ruleBasedValue = 1.0;
		double decayValue = 0.75;
		String explanation = Tooltip.generateImpactExplanation(
			parentImpact, ruleBasedValue, decayValue, impactValue, "", 0);

		assertTrue(explanation.contains("mainly due to the decay value."));
	}

	@Test
	public void testGenerateImpactExplanationLinkRecommendation() {
		double impactValue = 0.25;
		double parentImpact = 1.0;
		double ruleBasedValue = 1.0;
		double decayValue = 1.0;
		String explanation = Tooltip.generateImpactExplanation(
			parentImpact, ruleBasedValue, decayValue, impactValue, LinkType.RECOMMENDED.getName(), 0.8);

		assertTrue(explanation.contains("Link Recommendation Score"));
	}

}