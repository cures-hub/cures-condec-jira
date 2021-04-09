package de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestProjectSourceRecommender extends TestSetUp {

	private ProjectSourceRecommender projectSourceRecommender;

	@Before
	public void setUp() {
		init();
		ProjectSource projectSource = new ProjectSource("TEST", "TEST", true);
		projectSourceRecommender = new ProjectSourceRecommender("TEST", projectSource);
	}

	@Test
	public void testKeywordOnly() {
		List<Recommendation> recommendations = projectSourceRecommender
				.getRecommendations("How can we implement the feature?");
		assertEquals(2, recommendations.size());
	}

	@Test
	public void testDecisionProblem() {
		KnowledgeElement decisionProblem = KnowledgeElements.getTestKnowledgeElements().get(4);
		List<Recommendation> recommendations = projectSourceRecommender.getRecommendations(decisionProblem);
		assertEquals(2, recommendations.size());
	}

	@Test
	public void testTextSimilarity() {
		assertEquals(0.96, projectSourceRecommender.calculateSimilarity("MySQL", "MySQL@en"), 0.1);
		assertEquals(1.0, projectSourceRecommender.calculateSimilarity("How can we implement the feature?",
				"How to implement the feature?"), 0.0);
	}
}
