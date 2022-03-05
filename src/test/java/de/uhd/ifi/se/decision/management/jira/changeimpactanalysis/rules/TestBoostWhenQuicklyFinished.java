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

public class TestBoostWhenQuicklyFinished extends TestSetUp {

	private KnowledgeElement currentElement;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(0);
		filterSettings = new FilterSettings("TEST", "");
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element was quickly finished",
				ChangePropagationRuleType.BOOST_WHEN_QUICKLY_FINISHED.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_WHEN_QUICKLY_FINISHED
			.getExplanation().contains("was quickly finished"));
	}

	@Test
	public void testPropagationElementQuicklyFinished() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(0), "FooBar");
		updateDateAndAuthor.put(new Date(1000), "FooBar");
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_QUICKLY_FINISHED.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}

	@Test
	public void testPropagationElementNotQuicklyFinished() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(0), "FooBar");
		updateDateAndAuthor.put(new Date(), "FooBar");
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		assertNotNull(ChangePropagationRuleType.BOOST_WHEN_QUICKLY_FINISHED.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}
}
