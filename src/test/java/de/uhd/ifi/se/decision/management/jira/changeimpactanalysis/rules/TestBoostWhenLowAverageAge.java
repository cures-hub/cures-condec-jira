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

public class TestBoostWhenLowAverageAge extends TestSetUp {
    
    private KnowledgeElement currentElement;

    @Before
	public void setUp() {
		init();
	}

    @Test
	public void testDescription() {
		assertEquals("Boost when element has a low average age",
				ChangePropagationRule.BOOST_WHEN_LOW_AVERAGE_AGE.getDescription());
	}

    @Test
	public void testPropagationTwoWeekAvgAge() {
        currentElement = KnowledgeElements.getTestKnowledgeElements().get(0);

        TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
        updateDateAndAuthor.put(new Date(999999999), "FooBar");
        currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);
        currentElement.setCreationDate(new Date(0));

		assertEquals(0.92, ChangePropagationRule.BOOST_WHEN_LOW_AVERAGE_AGE.getFunction()
				.isChangePropagated(null, currentElement, null), 0.05);
    }

    @Test
	public void testPropagationNoUpdates() {
        currentElement = KnowledgeElements.getTestKnowledgeElements().get(0);

        TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
        currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);
        currentElement.setCreationDate(new Date(0));

		assertEquals(1.0, ChangePropagationRule.BOOST_WHEN_LOW_AVERAGE_AGE.getFunction()
				.isChangePropagated(null, currentElement, null), 0.005);
    }
}
