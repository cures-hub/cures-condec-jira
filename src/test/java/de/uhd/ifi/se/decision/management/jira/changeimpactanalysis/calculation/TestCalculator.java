package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.rest.LinkRecommendationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestCalculator extends TestSetUp {

	protected FilterSettings settings;
	protected KnowledgeElementWithImpact rootElement;

	@Before
	public void setUp() {
		init();
		@SuppressWarnings("unused")
		Calculator calculator = new Calculator();
		settings = new FilterSettings();
		rootElement = new KnowledgeElementWithImpact(KnowledgeElements.getTestKnowledgeElements().get(0));
		settings.setSelectedElementObject(rootElement);
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
	public void testCalculateChangeImpactLinkRecommendationsWithDiscardedRecommendations() {
		List<ChangePropagationRule> propagationRules = new LinkedList<ChangePropagationRule>();
		ChangePropagationRule rule = new ChangePropagationRule(ChangePropagationRuleType.BOOST_WHEN_TEXTUAL_SIMILAR);
		propagationRules.add(rule);
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.2f, 0.2f, (long) 1, propagationRules);
		config.setAreLinkRecommendationsIncludedInCalculation(true);
		settings.setChangeImpactAnalysisConfig(config);
		settings.recommendLinks(true);
		
		// Discard a few recommendations
		LinkRecommendationRest linkRecommendationRest = new LinkRecommendationRest();
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		linkRecommendationRest.discardRecommendation(request, new LinkRecommendation(rootElement, KnowledgeElements.getTestKnowledgeElements().get(4)));
		linkRecommendationRest.discardRecommendation(request, new LinkRecommendation(rootElement, KnowledgeElements.getTestKnowledgeElements().get(5)));

		List<KnowledgeElementWithImpact> impactedElements = new ArrayList<>();
		impactedElements.add(rootElement);

		impactedElements = Calculator.calculateChangeImpact(settings.getSelectedElement(), 1.0, settings,
				impactedElements, (long) settings.getLinkDistance());

		assertTrue(impactedElements.size() > 5);
	}

	@Test
	public void testCalculateChangeImpactContextNegativeRuleWeight() {
		List<ChangePropagationRule> propagationRules = new LinkedList<ChangePropagationRule>();
		ChangePropagationRule rule = new ChangePropagationRule(ChangePropagationRuleType.BOOST_WHEN_TEXTUAL_SIMILAR);
		rule.setWeightValue(-1.0f);
		propagationRules.add(rule);
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.2f, 0.2f, (long) 1, propagationRules);
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", config);
		settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-1");

		List<KnowledgeElementWithImpact> impactedElements = new ArrayList<>();
		impactedElements.add(rootElement);

		impactedElements = Calculator.calculateChangeImpact(settings.getSelectedElement(), 1.0, settings,
				impactedElements, (long) settings.getLinkDistance());

		assertTrue(impactedElements.size() > 8);
	}

	@Test
	public void testGenerateImpactExplanationPropagationRule() {
		double impactValue = 0.75;
		double parentImpact = 1.0;
		double ruleBasedValue = 0.5;
		double decayValue = 0.25;
		String explanation = Calculator.generateImpactExplanation(
			parentImpact, ruleBasedValue, decayValue, impactValue, "", 0);

		assertTrue(explanation.contains("mainly due to a used propagation rule."));

		decayValue = 1.0;
		explanation = Calculator.generateImpactExplanation(
			parentImpact, ruleBasedValue, decayValue, impactValue, "", 0);

		assertTrue(explanation.contains("mainly due to the decay value."));

	}

	@Test
	public void testGenerateImpactExplanationParentImpact() {
		double impactValue = 0.25;
		double parentImpact = 0.5;
		double ruleBasedValue = 1.0;
		double decayValue = 0.25;
		String explanation = Calculator.generateImpactExplanation(
			parentImpact, ruleBasedValue, decayValue, impactValue, "", 0);

		assertTrue(explanation.contains("mainly due to its parent having a lowered impact score."));
	}

	@Test
	public void testGenerateImpactExplanationDecayValue() {
		double impactValue = 0.25;
		double parentImpact = 1.0;
		double ruleBasedValue = 1.0;
		double decayValue = 0.75;
		String explanation = Calculator.generateImpactExplanation(
			parentImpact, ruleBasedValue, decayValue, impactValue, "", 0);

		assertTrue(explanation.contains("mainly due to the decay value."));
	}

	@Test
	public void testGenerateImpactExplanationLinkRecommendation() {
		double impactValue = 0.25;
		double parentImpact = 1.0;
		double ruleBasedValue = 1.0;
		double decayValue = 1.0;
		String explanation = Calculator.generateImpactExplanation(
			parentImpact, ruleBasedValue, decayValue, impactValue, LinkType.RECOMMENDED.getName(), 0.8);

		assertTrue(explanation.contains("Link Recommendation Score"));
	}

	@After
	public void tearDown() {
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", new ChangeImpactAnalysisConfiguration());
	}
}
