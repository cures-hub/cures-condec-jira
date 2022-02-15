package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestTimeContextInformationProvider extends TestSetUp {

	private TimeContextInformationProvider timeContextInformationProvider;
	protected KnowledgeElement rootElement;
	protected KnowledgeElement currentElement;

	@Before
	public void setUp() {
		init();
		timeContextInformationProvider = new TimeContextInformationProvider();
		rootElement = KnowledgeElements.getTestKnowledgeElements().get(0);
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(1);
	}

	@Test
	public void testPropagationNoCoupling() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(0), "FooBar");
		rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(800000), "FooBar");
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);
        RecommendationScore score = timeContextInformationProvider.assessRelation(rootElement, currentElement);

        assertEquals(0.0, score.getValue(), 0.00);
        assertEquals("TimeContextInformationProvider (ms)", score.getExplanation());
	}

	@Test
	public void testPropagationDoubleCoupling() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(1000), "FooBar");
		rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);

		updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(2000), "FooBar");
		updateDateAndAuthor.put(new Date(0), "FooBar");

		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);
		RecommendationScore score = timeContextInformationProvider.assessRelation(rootElement, currentElement);

        assertEquals(0.9375, score.getValue(), 0.005);
	}

	@Test
	public void testPropagationNoUpdates() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		rootElement.setUpdateDateAndAuthor(updateDateAndAuthor);
		currentElement.setUpdateDateAndAuthor(updateDateAndAuthor);
		RecommendationScore score = timeContextInformationProvider.assessRelation(rootElement, currentElement);

        assertEquals(0.0, score.getValue(), 0.00);
	}

	@Test
	public void testExplanation() {
		assertNotNull(timeContextInformationProvider.getExplanation());
	}

	@Test
	public void testDescription() {
		assertNotNull(timeContextInformationProvider.getDescription());
	}
}