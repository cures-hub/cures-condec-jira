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

public class TestBoostWhenLowAverageAge extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element has a low average age",
				ChangePropagationRuleType.BOOST_WHEN_LOW_AVERAGE_AGE.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_LOW_AVERAGE_AGE
			.getExplanation().contains("average age is determined by"));
	}

	@Test
	public void testPropagation() {
		KnowledgeElement currentElement = KnowledgeElements.getTestKnowledgeElements().get(0);
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(0), "FooBar");
		updateDateAndAuthor.put(new Date(), "FooBar");
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);
		FilterSettings filterSettings = new FilterSettings("TEST", "");

		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_LOW_AVERAGE_AGE.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}
}
