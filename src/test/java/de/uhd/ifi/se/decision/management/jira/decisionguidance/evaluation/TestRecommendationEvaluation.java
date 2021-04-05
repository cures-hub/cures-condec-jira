package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import static org.junit.Assert.assertEquals;

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
				rdfSource, 6, null);

		assertEquals(RecommenderType.ISSUE, recommendationEvaluation.getRecommenderType());
		assertEquals("DBPedia", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(6, recommendationEvaluation.getNumberOfResults());
	}

	@Test
	public void testRecommendationEvaluation() {
		RecommendationEvaluation recommendationEvaluation = new RecommendationEvaluation(RecommenderType.ISSUE,
				rdfSource, 6, null);

		recommendationEvaluation.setKnowledgeSource(rdfSource);
		recommendationEvaluation.setNumberOfResults(2);
		recommendationEvaluation.setRecommenderType(RecommenderType.KEYWORD);

		assertEquals(RecommenderType.KEYWORD, recommendationEvaluation.getRecommenderType());
		assertEquals("DBPedia", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(2, recommendationEvaluation.getNumberOfResults());
	}
}
