package de.uhd.ifi.se.decision.management.jira.recommendation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestRecommendation extends TestSetUp {

	private Recommendation recommendation;

	@Before
	public void setUp() {
		init();
		recommendation = new LinkRecommendation(KnowledgeElements.getOtherWorkItem(),
				KnowledgeElements.getSolvedDecisionProblem());
	}

	@Test
	public void testAddToScore() {
		recommendation.addToScore(0.5, "explanation for score");
		assertEquals(0.5, recommendation.getScore().getValue(), 0);
	}

	@Test
	public void testCompareTo() {
		recommendation.addToScore(0.5, "explanation for score");
		assertEquals(-1, recommendation.compareTo(null));

		Recommendation otherRecommendation = new LinkRecommendation(KnowledgeElements.getOtherWorkItem(),
				KnowledgeElements.getAlternative());
		otherRecommendation.addToScore(0.5, "explanation for score");
		assertEquals(1, recommendation.compareTo(otherRecommendation));

		otherRecommendation.addToScore(0.5, "explanation for score");
		assertEquals(1, recommendation.compareTo(otherRecommendation));

		recommendation.addToScore(1., "explanation for score");
		assertEquals(-1, recommendation.compareTo(otherRecommendation));
	}

	@Test
	public void testNormalizeScores() {
		Recommendation otherRecommendation = new LinkRecommendation(KnowledgeElements.getOtherWorkItem(),
				KnowledgeElements.getAlternative());
		otherRecommendation.addToScore(0.5, "explanation for score");

		List<Recommendation> recommendations = new ArrayList<>();
		recommendations.add(recommendation);
		recommendations.add(otherRecommendation);

		assertEquals(0.5, Recommendation.getMaxScoreValue(recommendations), 0);
		Recommendation.normalizeRecommendationScore(recommendations);
		assertEquals(1, Recommendation.getMaxScoreValue(recommendations), 0);
	}

	@Test
	public void testSetDiscarded() {
		recommendation.setDiscarded(true);
		assertTrue(recommendation.isDiscarded());
	}
}