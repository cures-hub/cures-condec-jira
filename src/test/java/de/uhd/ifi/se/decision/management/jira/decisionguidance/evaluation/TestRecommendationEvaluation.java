package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;

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
		RecommendationEvaluation recommendationEvaluation = new RecommendationEvaluation(RecommenderType.ISSUE,
				rdfSource, new ArrayList<>(), null, new ArrayList<>());

		assertEquals(RecommenderType.ISSUE, recommendationEvaluation.getRecommenderType());
		assertEquals("DBPedia", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(0, recommendationEvaluation.getRecommendations().size());
		assertEquals(0, recommendationEvaluation.getGroundTruthSolutionOptions().size());
	}

	@Test
	public void testRecommendationEvaluation() {
		RecommendationEvaluation recommendationEvaluation = new RecommendationEvaluation(RecommenderType.ISSUE,
				rdfSource, null, null, null);

		recommendationEvaluation.setKnowledgeSource(rdfSource);
		recommendationEvaluation.setRecommendations(new ArrayList<>());
		recommendationEvaluation.setRecommenderType(RecommenderType.KEYWORD);

		assertEquals(RecommenderType.KEYWORD, recommendationEvaluation.getRecommenderType());
		assertEquals("DBPedia", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(0, recommendationEvaluation.getRecommendations().size());
	}
}
