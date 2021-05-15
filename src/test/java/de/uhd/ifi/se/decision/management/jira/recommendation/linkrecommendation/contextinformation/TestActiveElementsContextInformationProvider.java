package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestActiveElementsContextInformationProvider extends TestSetUp {

	private ActiveElementsContextInformationProvider activeElementsContextInformationProvider;

	@Before
	public void setUp() {
		TestSetUp.init();
		activeElementsContextInformationProvider = new ActiveElementsContextInformationProvider();
	}

	@Test
	public void testAssessRelationWithoutSprint() {
		RecommendationScore score = activeElementsContextInformationProvider
				.assessRelation(KnowledgeElements.getAlternative(), KnowledgeElements.getProArgument());
		assertEquals(0.0, score.getValue(), 0);
		assertEquals("ActiveElementsContextInformationProvider (same Sprint)", score.getExplanation());
	}
}