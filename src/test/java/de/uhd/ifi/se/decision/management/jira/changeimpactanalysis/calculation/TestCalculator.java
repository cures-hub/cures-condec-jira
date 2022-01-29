package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

public class TestCalculator extends TestSetUp {

	protected FilterSettings settings;
	protected KnowledgeElementWithImpact rootElement;

	@Before
	public void setUp() {
		init();
		settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-1");
		rootElement = new KnowledgeElementWithImpact(settings.getSelectedElement());
	}

	@Test
	public void testCalculateChangeImpact() {
		List<KnowledgeElementWithImpact> impactedElements = new ArrayList<>();
		impactedElements.add(rootElement);

		impactedElements = Calculator.calculateChangeImpact(settings.getSelectedElement(), 1.0, settings,
				impactedElements, (long) settings.getLinkDistance());

		assertEquals(1.0, impactedElements.get(0).getImpactValue(), 0.05);
		assertEquals(1.0, impactedElements.get(0).getParentImpact(), 0.05);
		assertEquals(1.0, impactedElements.get(0).getLinkTypeWeight(), 0.05);
		assertEquals(1.0, impactedElements.get(0).getRuleBasedValue(), 0.05);
		assertEquals(0, impactedElements.get(0).getPropagationRules().size());
	}

	@Test
	public void testCalculateChangeImpactLinkRecommendations() {
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration();
		config.setAreLinkRecommendationsIncludedInCalculation(true);
	 	List<ChangePropagationRule> propagationRules = new LinkedList<ChangePropagationRule>();
		ChangePropagationRule rule = new ChangePropagationRule(ChangePropagationRuleType.BOOST_WHEN_TEXTUAL_SIMILAR);
		propagationRules.add(rule);
		config.setPropagationRules(propagationRules);
		settings.setChangeImpactAnalysisConfig(config);

		List<KnowledgeElementWithImpact> impactedElements = new ArrayList<>();
		impactedElements.add(rootElement);

		impactedElements = Calculator.calculateChangeImpact(settings.getSelectedElement(), 1.0, settings,
				impactedElements, (long) settings.getLinkDistance());

		assertTrue(impactedElements.size() > 0);
	}

	@Test
	public void testCalculateChangeImpactContext() {
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration();
		config.setContext(1);
		List<ChangePropagationRule> propagationRules = new LinkedList<ChangePropagationRule>();
		ChangePropagationRule rule = new ChangePropagationRule(ChangePropagationRuleType.BOOST_WHEN_TEXTUAL_SIMILAR);
		propagationRules.add(rule);
		config.setPropagationRules(propagationRules);
		settings.setChangeImpactAnalysisConfig(config);

		List<KnowledgeElementWithImpact> impactedElements = new ArrayList<>();
		impactedElements.add(rootElement);

		impactedElements = Calculator.calculateChangeImpact(settings.getSelectedElement(), 1.0, settings,
				impactedElements, (long) settings.getLinkDistance());

		assertEquals(10, impactedElements.size());
		assertTrue(impactedElements.get(9).getImpactExplanation().contains("context"));
	}

	@Test
	public void testCalculatePropagationRuleImpact() {
		KnowledgeElement element = settings.getSelectedElement();
		Link link = element.getLinks().iterator().next();

		assertEquals(0.0, Calculator.calculatePropagationRuleImpact(settings, element, link), 0.05);
	}

	@Test
	public void testGenerateImpactExplanationPropagationRule() {
		double impactValue = 0.75;
		double parentImpact = 1.0;
		double ruleBasedValue = 0.5;
		double decayValue = 0.25;

		String explanation = Calculator.generateImpactExplanation(parentImpact, ruleBasedValue, decayValue,
				impactValue, "");

		assertTrue(explanation.contains("mainly due to a used propagation rule."));
	}

	@Test
	public void testGenerateImpactExplanationParentImpact() {
		double impactValue = 0.25;
		double parentImpact = 0.5;
		double ruleBasedValue = 1.0;
		double decayValue = 0.25;

		String explanation = Calculator.generateImpactExplanation(parentImpact, ruleBasedValue, decayValue,
				impactValue, "");

		assertTrue(explanation.contains("mainly due to its parent having a lowered impact score."));
	}

	@Test
	public void testGenerateImpactExplanationDecayValue() {
		double impactValue = 0.25;
		double parentImpact = 1.0;
		double ruleBasedValue = 1.0;
		double decayValue = 0.75;

		String explanation = Calculator.generateImpactExplanation(parentImpact, ruleBasedValue, decayValue,
				impactValue, "");

		assertTrue(explanation.contains("mainly due to the decay value."));
	}

	@Test
	public void testGenerateImpactExplanationLinkRecommendation() {
		double impactValue = 0.25;
		double parentImpact = 1.0;
		double ruleBasedValue = 1.0;
		double decayValue = 1.0;

		String explanation = Calculator.generateImpactExplanation(parentImpact, ruleBasedValue, decayValue,
				impactValue, LinkType.RECOMMENDED.getName());

		assertTrue(explanation.contains("link recommendation"));
	}
}
