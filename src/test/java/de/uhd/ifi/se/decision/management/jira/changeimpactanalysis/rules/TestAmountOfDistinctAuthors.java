package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestAmountOfDistinctAuthors extends TestSetUp {
    
    private KnowledgeElement currentElement;

    @Before
	public void setUp() {
		init();
	}

    @Test
	public void testDescription() {
		assertEquals("Boost when element has a large number of distinct update authors",
				ChangePropagationRule.AMOUNT_OF_DISTINCT_AUTHORS.getDescription());
	}

	@Test
	public void testPropagationOneAuthor() {
        currentElement = KnowledgeElements.getTestKnowledgeElements().get(0);
        TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
        updateDateAndAuthor.put(new Date(), "FooBar");
        currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		assertEquals(0.9, ChangePropagationRule.AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(null, currentElement, null), 0.2);
	}

    @Test
	public void testPropagationFiveDifferentAuthors() {
        currentElement = KnowledgeElements.getTestKnowledgeElements().get(0);
        TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
        for (int i = 0; i < 5; i++) {
            updateDateAndAuthor.put(new Date(i), "FooBar" + i);
        }
        currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		assertEquals(0.98, ChangePropagationRule.AMOUNT_OF_DISTINCT_AUTHORS.getFunction()
				.isChangePropagated(null, currentElement, null), 0.005);
	}
}
