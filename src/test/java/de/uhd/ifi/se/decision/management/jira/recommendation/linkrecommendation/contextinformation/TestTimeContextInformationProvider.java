package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestTimeContextInformationProvider extends TestSetUp {

	private TimeContextInformationProvider timeContextInformationProvider;

	@Before
	public void setUp() {
		init();
		timeContextInformationProvider = new TimeContextInformationProvider();
	}

	@Test
	public void testAssessRelation() {
		RecommendationScore score = timeContextInformationProvider.assessRelation(KnowledgeElements.getAlternative(),
				KnowledgeElements.getProArgument());
		assertEquals(1.0, score.getValue(), 0);
		assertEquals("TimeContextInformationProvider (ms)", score.getExplanation());
	}
}