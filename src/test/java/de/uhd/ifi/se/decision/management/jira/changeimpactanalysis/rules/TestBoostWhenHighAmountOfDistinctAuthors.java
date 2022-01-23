package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;

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
		filterSettings = new FilterSettings();
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element has a large number of distinct update authors",
				ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getDescription());
	}

	@Test
	public void testPropagationOneAuthor() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(0);
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<>();
		updateDateAndAuthor.put(new Date(), "FooBar");
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		assertEquals(0.9, ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.2);
	}

	@Test
	public void testPropagationFiveDifferentAuthors() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(0);
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		for (int i = 0; i < 5; i++) {
			updateDateAndAuthor.put(new Date(i), "FooBar" + i);
		}
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		assertEquals(0.98, ChangePropagationRuleType.BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(filterSettings, currentElement, null), 0.005);
	}
}
