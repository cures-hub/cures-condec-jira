package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
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
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element is timely coupled to the selected element",
				ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED
			.getExplanation().contains("is timely coupled to the source element"));
	}

	@Test
	public void testPropagationNoCoupling() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(0), "FooBar");
		rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);
		filterSettings.setSelectedElementObject(rootElement);

		TreeMap<Date, String> updateDateAndAuthorNext = new TreeMap<Date, String>();
		updateDateAndAuthorNext.put(new Date(600001), "FooBar");
		nextElement.setUpdateDateAndAuthor(updateDateAndAuthorNext);

		assertEquals(0.5, ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}

	@Test
	public void testPropagationNoCouplingMinRuleWeight() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(0), "FooBar");
		rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);
		filterSettings.setSelectedElementObject(rootElement);

		TreeMap<Date, String> updateDateAndAuthorNext = new TreeMap<Date, String>();
		updateDateAndAuthorNext.put(new Date(600001), "FooBar");
		nextElement.setUpdateDateAndAuthor(updateDateAndAuthorNext);

		List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_WHEN_TIMELY_COUPLED", false, 0.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0, propagationRules);
		filterSettings.setChangeImpactAnalysisConfig(config);

		assertEquals(1.0, ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}

	@Test
	public void testPropagationSingleCoupling() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(0), "FooBar");
		rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);
		filterSettings.setSelectedElementObject(rootElement);

		TreeMap<Date, String> updateDateAndAuthorNext = new TreeMap<Date, String>();
		updateDateAndAuthorNext.put(new Date(599999), "FooBar");
		nextElement.setUpdateDateAndAuthor(updateDateAndAuthorNext);

		assertEquals(0.666, ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}

	@Test
	public void testPropagationMaxScoreCoupling() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(1), "FooBar");
		rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);
		filterSettings.setSelectedElementObject(rootElement);

		TreeMap<Date, String> updateDateAndAuthorNext = new TreeMap<Date, String>();
		updateDateAndAuthorNext.put(new Date(0), "FooBar");
		updateDateAndAuthorNext.put(new Date(10), "FooBar");
		nextElement.setUpdateDateAndAuthor(updateDateAndAuthorNext);

		List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_WHEN_TIMELY_COUPLED", false, 2.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0, propagationRules);
		filterSettings.setChangeImpactAnalysisConfig(config);

		assertEquals(1.0, ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}

	@Test
	public void testPropagationNoUpdates() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);
		filterSettings.setSelectedElementObject(rootElement);
		nextElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		assertEquals(0.5, ChangePropagationRuleType.BOOST_WHEN_TIMELY_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}
}
