package de.uhd.ifi.se.decision.management.jira.decisionguidance.viewmodel;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestRecommendation extends TestSetUp {


	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testSetAndGetScore() {
		Recommendation recommendation = new Recommendation();
		assertEquals(0, recommendation.getScore());
	}


	@Test
	public void testEquals() {
		Recommendation recommendationA = new Recommendation();
		recommendationA.setKnowledgeSourceName("SourceA");
		recommendationA.setRecommendations("Recommendation");

		Recommendation recommendationB = new Recommendation();
		recommendationB.setKnowledgeSourceName("SourceA");
		recommendationB.setRecommendations("Recommendation");

		assertEquals(recommendationA, recommendationB);

		recommendationB.setKnowledgeSourceName("SourceB");

		assertNotEquals(recommendationA, recommendationB);

	}

}
