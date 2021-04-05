package de.uhd.ifi.se.decision.management.jira.decisionguidance.viewmodel;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.score.RecommendationScore;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TestRecommendationScore extends TestSetUp {

	private RecommendationScore score;

	@Before
	public void setUp() {
		init();
		score = new RecommendationScore(0.0f, "TEST DESCRIPTION");
	}

	@Test
	public void testScore() {
		assertEquals(0, score.getSubScores().size());
		score.addSubScore(new RecommendationScore(0.5f, "First Score"));
		score.addSubScore(new RecommendationScore(0.5f, "Second Score"));
		assertEquals(2, score.getSubScores().size());

		score.setValue(1.5f);
		assertEquals(1.5, score.getValue(), 0.0);

		score.setSubScore(new ArrayList<>());
		assertEquals(2, score.getSubScores().size());

		score.setExplanation("TEST EXPLANATION");
		assertEquals("TEST EXPLANATION", score.getExplanation());


	}
}
