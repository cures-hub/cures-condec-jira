package de.uhd.ifi.se.decision.management.jira.decisionguidance.viewmodel;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestRecommendation extends TestSetUp {


	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testsetAndGetScore() {
		Recommendation recommendation = new Recommendation();
		assertEquals(0, recommendation.getScore());
	}

	@Test
	public void testSetAndGetKnowledgeSourceType() {
		Recommendation recommendation = new Recommendation();
		assertNull(recommendation.getKnowledgeSourceType());
		recommendation.setKnowledgeSourceType(KnowledgeSourceType.PROJECT);
		assertEquals(KnowledgeSourceType.PROJECT, recommendation.getKnowledgeSourceType());
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
