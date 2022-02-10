package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

public class TestBoostIfDecisionProblem extends TestSetUp {
    
    private KnowledgeElement currentElement;
    private KnowledgeElement rootElement;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
        rootElement = KnowledgeElements.getProArgument();
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element is a decision problem",
				ChangePropagationRuleType.BOOST_IF_DECISION_PROBLEM.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_IF_DECISION_PROBLEM
			.getExplanation().contains("in the knowledge graph is a decision problem"));
	}

    @Test
	public void testPropagation() {
		currentElement = KnowledgeElements.getProArgument();
        List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_IF_DECISION_PROBLEM", false, 1.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0,
				propagationRules);
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", config);
		filterSettings = new FilterSettings("TEST", "");
        filterSettings.setSelectedElementObject(rootElement);

		assertNotNull(ChangePropagationRuleType.BOOST_IF_DECISION_PROBLEM.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}
}