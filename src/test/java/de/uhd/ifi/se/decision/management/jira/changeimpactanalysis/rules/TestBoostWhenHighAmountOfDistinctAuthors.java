package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostWhenHighAmountOfDistinctAuthors extends TestSetUp {

	private KnowledgeElement currentElement;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
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
		filterSettings = new FilterSettings("TEST", "");

		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}

	@Test
	public void testPropagationOneAuthor() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<>();
		updateDateAndAuthor.put(new Date(), "FooBar");
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);
		filterSettings = new FilterSettings("TEST", "");

		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}

	@Test
	public void testPropagationManyAuthors() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<>();
		for (int i = 0; i < 10; i++) {
			updateDateAndAuthor.put(new Date(i * 1000), "FooBar" + i);
		}
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);
		filterSettings = new FilterSettings("TEST", "");

		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}
}
