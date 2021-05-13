package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestEvaluator extends TestSetUp {

	private KnowledgeElement decisionProblem;

	@Before
	public void setUp() {
		init();
		decisionProblem = KnowledgeElements.getSolvedDecisionProblem();
	}

	@Test
	public void testTopKResults() {
		List<ElementRecommendation> recommendations = new ArrayList<>();
		recommendations.add(new ElementRecommendation());
		recommendations.add(new ElementRecommendation());

		assertEquals(1, Evaluator.getTopKRecommendations(recommendations, 1).size());
		assertEquals(2, Evaluator.getTopKRecommendations(recommendations, -42).size());
		assertEquals(2, Evaluator.getTopKRecommendations(recommendations, 42).size());
	}

	@Test
	public void testEvaluateProjectSourceSameProject() {
		ProjectSource projectSource = new ProjectSource("TEST", true);
		RecommendationEvaluation recommendationEvaluation = Evaluator.evaluate(decisionProblem, "", 5, projectSource);

		assertEquals("TEST", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(2, recommendationEvaluation.getRecommendations().size());
		assertEquals(6, recommendationEvaluation.getMetrics().size());
	}

	@Test
	public void testEvaluateRDFSource() {
		RDFSource rdfSource = new RDFSource();
		rdfSource.setName("DBPedia");
		RecommendationEvaluation recommendationEvaluation = Evaluator.evaluate(decisionProblem, "MySQL", 5, rdfSource);

		assertEquals("DBPedia", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(34, recommendationEvaluation.getRecommendations().size());
	}

	@Test
	public void testDefaultConstructor() {
		assertNotNull(new Evaluator());
	}
}