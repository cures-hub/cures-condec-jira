package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestRecommendationScore extends TestSetUp {

	private RecommendationScore score;

	@Before
	public void setUp() {
		init();
		score = new RecommendationScore(0.0f, "TEST DESCRIPTION");
	}

	@Test
	public void testGetValueWithoutSubScores() {
		score.setValue(0.5f);
		assertTrue(score.getSubScores().isEmpty());
		assertEquals(0.5, score.getValue(), 0);
		assertEquals("TEST DESCRIPTION", score.getExplanation());
	}

	@Test
	public void testGetValueWithSubScores() {
		score.addSubScore(new RecommendationScore(0.5f, "First Score"));
		score.addSubScore(new RecommendationScore(0.5f, "Second Score"));
		assertEquals(2, score.getSubScores().size());
		assertEquals(1.0, score.getValue(), 0.0);

		score.setValue(0.5f);
		assertEquals(0.5, score.getValue(), 0.0);
	}

	@Test
	public void testSetAndGetSubScores() {
		score.setSubScores(new ArrayList<>());
		assertTrue(score.getSubScores().isEmpty());
	}

	@Test
	public void testSetAndGetExplanation() {
		score.setExplanation("TEST EXPLANATION");
		assertEquals("TEST EXPLANATION", score.getExplanation());
	}
}