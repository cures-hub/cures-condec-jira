package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;

public class TestRecommendationEvaluation extends TestSetUp {

	private RDFSource rdfSource;

	@Before
	public void setUp() {
		init();

		rdfSource = new RDFSource();
		rdfSource.setName("DBPedia");
	}

	@Test
	public void testConstructor() {
		RecommendationEvaluation recommendationEvaluation = new RecommendationEvaluation(rdfSource, new ArrayList<>(),
				null, new ArrayList<>());

		assertEquals("DBPedia", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(0, recommendationEvaluation.getRecommendations().size());
		assertEquals(0, recommendationEvaluation.getGroundTruthSolutionOptions().size());
	}

	@Test
	public void testRecommendationEvaluation() {
		RecommendationEvaluation recommendationEvaluation = new RecommendationEvaluation(rdfSource, null, null, null);

		recommendationEvaluation.setKnowledgeSource(rdfSource);
		recommendationEvaluation.setRecommendations(new ArrayList<>());
		recommendationEvaluation.setMetrics(new ArrayList<>());
		recommendationEvaluation.setGroundTruthSolutionOptions(new ArrayList<>());

		assertEquals("DBPedia", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(0, recommendationEvaluation.getRecommendations().size());
		assertEquals(0, recommendationEvaluation.getMetrics().size());
		assertEquals(0, recommendationEvaluation.getGroundTruthSolutionOptions().size());
	}
}
