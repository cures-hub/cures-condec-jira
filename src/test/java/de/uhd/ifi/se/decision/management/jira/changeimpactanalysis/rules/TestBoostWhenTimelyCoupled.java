package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostWhenTimelyCoupled extends TestSetUp {

	protected KnowledgeElement rootElement;
	protected KnowledgeElement nextElement;
	protected FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		rootElement = KnowledgeElements.getTestKnowledgeElements().get(0);
		nextElement = KnowledgeElements.getTestKnowledgeElements().get(1);
		filterSettings = new FilterSettings();
		filterSettings.setSelectedElementObject(rootElement);
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element is timely coupled to the selected element",
				ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getExplanation()
				.contains("is timely coupled to the source element"));
	}

	@Test
	public void testPropagationRuleWeightMaximum() {
		List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_WHEN_TIMELY_COUPLED", false, 0.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0,
				propagationRules);
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", config);
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElementObject(rootElement);

		assertEquals(1.0, ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}

	@Test
	public void testPropagationRuleWeightMinimum() {
		List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_WHEN_TIMELY_COUPLED", false, 2.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0,
				propagationRules);
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", config);
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElementObject(rootElement);

		assertEquals(0.0, ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}

	@Test
	public void testPropagationRuleWeightNegative() {
		List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_WHEN_TIMELY_COUPLED", false, -1.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0,
				propagationRules);
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", config);
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElementObject(rootElement);

		assertEquals(1.0, ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}
}
