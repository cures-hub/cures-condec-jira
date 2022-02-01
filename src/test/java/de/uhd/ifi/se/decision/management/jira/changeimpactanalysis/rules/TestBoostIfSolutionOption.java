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

public class TestBoostIfSolutionOption extends TestSetUp {
    
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
		assertEquals("Boost when element is a solution option",
				ChangePropagationRuleType.BOOST_IF_SOLUTION_OPTION.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_IF_SOLUTION_OPTION
			.getExplanation().contains("in the knowledge graph is a solution option"));
	}

    @Test
	public void testPropagationNonSolutionOptionMinRuleWeight() {
		currentElement = KnowledgeElements.getOtherWorkItem();
        List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_IF_SOLUTION_OPTION", false, 0.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0,
				propagationRules);
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", config);
		filterSettings = new FilterSettings("TEST", "");
        filterSettings.setSelectedElementObject(rootElement);
        System.out.println(currentElement.getTypeAsString());
		assertEquals(1.0, ChangePropagationRuleType.BOOST_IF_SOLUTION_OPTION.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}

    @Test
	public void testPropagationSolutionOptionMaxRuleWeight() {
		currentElement = KnowledgeElements.getAlternative();
        List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_IF_SOLUTION_OPTION", false, 2.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0,
				propagationRules);
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", config);
		filterSettings = new FilterSettings("TEST", "");
        filterSettings.setSelectedElementObject(rootElement);
        System.out.println(currentElement.getTypeAsString());
		assertEquals(0.0, ChangePropagationRuleType.BOOST_IF_SOLUTION_OPTION.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}
}