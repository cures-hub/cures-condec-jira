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
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostWhenEqualComponent extends TestSetUp {

	private KnowledgeElement currentElement;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(1);
		KnowledgeElement rootElement = KnowledgeElements.getTestKnowledgeElements().get(2);
		filterSettings = new FilterSettings();
		filterSettings.setSelectedElementObject(rootElement);
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element is assigned the same component",
				ChangePropagationRuleType.BOOST_WHEN_EQUAL_COMPONENT.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_EQUAL_COMPONENT
			.getExplanation().contains("is assigned to the same component"));
	}

	@Test
	public void testPropagationRootNoComponentsMinWeightValue() {
		KnowledgeElement rootElement = KnowledgeElements.getTestKnowledgeElements().get(1);
		filterSettings.setSelectedElementObject(rootElement);

		List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_WHEN_EQUAL_COMPONENT", false, 0.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0, propagationRules);
		filterSettings.setChangeImpactAnalysisConfig(config);

		assertEquals(1.0, ChangePropagationRuleType.BOOST_WHEN_EQUAL_COMPONENT.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}

	@Test
	public void testPropagationEqualComponents() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(2);
		
		assertEquals(1.0, ChangePropagationRuleType.BOOST_WHEN_EQUAL_COMPONENT.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}

	@Test
	public void testPropagationRootOnlyComponent() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(4);

		assertEquals(0.75, ChangePropagationRuleType.BOOST_WHEN_EQUAL_COMPONENT.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}

	@Test
	public void testPropagationNoMatchingComponents() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(3);
		
		assertEquals(0.75, ChangePropagationRuleType.BOOST_WHEN_EQUAL_COMPONENT.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}
}
