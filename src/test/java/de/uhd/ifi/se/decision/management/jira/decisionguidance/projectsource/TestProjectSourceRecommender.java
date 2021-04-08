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

	private ProjectSource projectSource;
	private ProjectSourceRecommender projectSourceRecommender;

	@Before
	public void setUp() {
		init();
		projectSource = new ProjectSource("TEST", "TEST", true);
		projectSourceRecommender = new ProjectSourceRecommender("TEST", projectSource);
	}

	@Test
	public void testKeywordOnly() {
		List<Recommendation> recommendations = projectSourceRecommender.getRecommendations("MySQL");

		assertEquals(2, recommendations.size());
	}

	@Test
	public void testDecisionProblem() {
		KnowledgeElement decisionProblem = KnowledgeElements.getTestKnowledgeElements().get(5);
		List<Recommendation> recommendations = projectSourceRecommender.getRecommendations(decisionProblem);
		assertEquals(2, recommendations.size());
	}
}
