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

public class TestBoostWhenEqualKnowledgeType extends TestSetUp{
    
	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element has the same knowledge type",
				ChangePropagationRuleType.BOOST_WHEN_EQUAL_KNOWLEDGE_TYPE.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_EQUAL_KNOWLEDGE_TYPE
			.getExplanation().contains("has the same knowledge type"));
	}

    @Test
	public void testPropagation() {
		KnowledgeElement currentElement = KnowledgeElements.getDecision();
        KnowledgeElement rootElement = KnowledgeElements.getProArgument();

        List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_WHEN_EQUAL_KNOWLEDGE_TYPE", false, 1.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0,
				propagationRules);
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", config);

		FilterSettings filterSettings = new FilterSettings("TEST", "");
        filterSettings.setSelectedElementObject(rootElement);

		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_EQUAL_KNOWLEDGE_TYPE.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}
}
