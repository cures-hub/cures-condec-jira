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
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostWhenHighAmountOfDistinctAuthors extends TestSetUp {

	private KnowledgeElement currentElement;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		filterSettings = new FilterSettings();
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(0);
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element has a large number of distinct update authors",
				ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS
			.getExplanation().contains("has a large number of distinct update authors"));
	}

	@Test
	public void testPropagationNoAuthor() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<>();
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		assertEquals(1.0, ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.00);
	}

	@Test
	public void testPropagationOneAuthor() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<>();
		updateDateAndAuthor.put(new Date(), "FooBar");
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		assertEquals(0.9, ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.2);
	}

	@Test
	public void testPropagationOneAuthorMinRuleWeight() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<>();
		updateDateAndAuthor.put(new Date(), "FooBar");
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS", false, 0.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0,
				propagationRules);
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", config);
		filterSettings = new FilterSettings("TEST", "");

		assertEquals(0.0, ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.2);
	}

	@Test
	public void testPropagationFiveDifferentAuthorsMaxRuleWeight() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<>();
		for (int i = 0; i < 5; i++) {
			updateDateAndAuthor.put(new Date(i), "FooBar" + i);
		}
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS", false, 2.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0,
				propagationRules);
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", config);
		filterSettings = new FilterSettings("TEST", "");

		assertEquals(1.0, ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}

	@Test
	public void testPropagationNegativeRuleWeight() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<>();
		updateDateAndAuthor.put(new Date(), "FooBar");
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		List<ChangePropagationRule> propagationRules = new LinkedList<>();
		propagationRules.add(new ChangePropagationRule("BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS", false, -1.0f));
		ChangeImpactAnalysisConfiguration config = new ChangeImpactAnalysisConfiguration(0.25f, 0.25f, (long) 0,
				propagationRules);
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", config);
		filterSettings = new FilterSettings("TEST", "");

		assertEquals(0.95, ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}
}
