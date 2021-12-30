package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostWhenCoupled extends TestSetUp {
    
    protected KnowledgeElement rootElement;
    protected KnowledgeElement nextElement;
    protected FilterSettings filterSettings;

    @Before
	public void setUp() {
		init();
	}

    @Test
	public void testDescription() {
		assertEquals("Boost when element is coupled to the selected element",
				ChangePropagationRule.BOOST_WHEN_COUPLED.getDescription());
	}

    @Test
	public void testPropagationNoCoupling() {
        rootElement = KnowledgeElements.getTestKnowledgeElements().get(0);
        TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
        updateDateAndAuthor.put(new Date(0), "FooBar");
        rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);
        filterSettings = new FilterSettings();
		filterSettings.setSelectedElementObject(rootElement);

        nextElement = KnowledgeElements.getTestKnowledgeElements().get(1);
        TreeMap<Date, String> updateDateAndAuthorNext = new TreeMap<Date, String>();
        updateDateAndAuthorNext.put(new Date(600001), "FooBar");
        nextElement.setUpdateDateAndAuthor(updateDateAndAuthorNext);

		assertEquals(0.5, ChangePropagationRule.BOOST_WHEN_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}

    @Test
	public void testPropagationSingleCoupling() {
        rootElement = KnowledgeElements.getTestKnowledgeElements().get(0);
        TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
        updateDateAndAuthor.put(new Date(0), "FooBar");
        rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);
        filterSettings = new FilterSettings();
		filterSettings.setSelectedElementObject(rootElement);

        nextElement = KnowledgeElements.getTestKnowledgeElements().get(1);
        TreeMap<Date, String> updateDateAndAuthorNext = new TreeMap<Date, String>();
        updateDateAndAuthorNext.put(new Date(599999), "FooBar");
        nextElement.setUpdateDateAndAuthor(updateDateAndAuthorNext);

		assertEquals(0.833, ChangePropagationRule.BOOST_WHEN_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}

    @Test
	public void testPropagationDoubleCoupling() {
        rootElement = KnowledgeElements.getTestKnowledgeElements().get(0);
        TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
        updateDateAndAuthor.put(new Date(0), "FooBar");
        rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);
        filterSettings = new FilterSettings();
		filterSettings.setSelectedElementObject(rootElement);

        nextElement = KnowledgeElements.getTestKnowledgeElements().get(1);
        TreeMap<Date, String> updateDateAndAuthorNext = new TreeMap<Date, String>();
        updateDateAndAuthorNext.put(new Date(599999), "FooBar");
        updateDateAndAuthorNext.put(new Date(0), "FooBar");
        nextElement.setUpdateDateAndAuthor(updateDateAndAuthorNext);

		assertEquals(1.0, ChangePropagationRule.BOOST_WHEN_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}

    @Test
	public void testPropagationNoUpdates() {
        rootElement = KnowledgeElements.getTestKnowledgeElements().get(0);
        TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
        rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);
        filterSettings = new FilterSettings();
		filterSettings.setSelectedElementObject(rootElement);

        nextElement = KnowledgeElements.getTestKnowledgeElements().get(1);
        TreeMap<Date, String> updateDateAndAuthorNext = new TreeMap<Date, String>();
        nextElement.setUpdateDateAndAuthor(updateDateAndAuthorNext);

		assertEquals(0.5, ChangePropagationRule.BOOST_WHEN_COUPLED.getFunction()
				.isChangePropagated(filterSettings, nextElement, null), 0.005);
	}
}
