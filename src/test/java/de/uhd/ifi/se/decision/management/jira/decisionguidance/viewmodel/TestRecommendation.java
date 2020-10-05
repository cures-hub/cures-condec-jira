package de.uhd.ifi.se.decision.management.jira.decisionguidance.viewmodel;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

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
	public void testConstructor() {
		Recommendation recommendation = new Recommendation("TEST", "TEST", "TESTURL");
		assertEquals("TEST", recommendation.getKnowledgeSourceName());
		assertEquals("TEST", recommendation.getRecommendations());
		assertEquals("TESTURL", recommendation.getUrl());
	}


	@Test
	public void testHashCode() {
		Recommendation recommendation = new Recommendation("TEST", "TEST", "TESTURL");
		assertEquals(Objects.hash("TEST", "TEST"), recommendation.hashCode());

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
