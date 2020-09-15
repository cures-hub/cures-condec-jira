package de.uhd.ifi.se.decision.management.jira.decisionguidance.viewmodel;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.ScoreCalculator;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.ScoreCalculatorFactory;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
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
	public void testsetAndGetScore() {
		Recommendation recommendation = new Recommendation();
		assertEquals(0, recommendation.getScore());
	}

	@Test
	public void testSetAndGetKnowledgeSourceType() {
		Recommendation recommendation = new Recommendation();
		assertEquals(null, recommendation.getKnowledgeSourceType());
		recommendation.setKnowledgeSourceType(KnowledgeSourceType.PROJECT);
		assertEquals(KnowledgeSourceType.PROJECT, recommendation.getKnowledgeSourceType());
	}

}
