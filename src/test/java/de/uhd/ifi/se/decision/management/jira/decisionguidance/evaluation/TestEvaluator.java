package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestEvaluator extends TestSetUp {

	private List<KnowledgeSource> knowledgeSources;
	private List<KnowledgeElement> groundTruthSolutionOptions;
	private KnowledgeElement decisionProblem;

	@Before
	public void setUp() {
		init();
		decisionProblem = KnowledgeElements.getSolvedDecisionProblem();

		// search for solution options in the same project
		ProjectSource projectSource = new ProjectSource("TEST", "TEST", true);

		RDFSource rdfSource = new RDFSource();
		rdfSource.setName("DBPedia");

		knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		groundTruthSolutionOptions = new ArrayList<>();
		KnowledgeElement alternative = new KnowledgeElement();
		alternative.setSummary("Test Alternative");
		alternative.setStatus(KnowledgeStatus.IDEA);
		KnowledgeElement decision = new KnowledgeElement();
		decision.setSummary("Test Decision");
		decision.setStatus(KnowledgeStatus.DECIDED);
		groundTruthSolutionOptions.add(alternative);
		groundTruthSolutionOptions.add(decision);

		List<Recommendation> recommendations = new ArrayList<>();
		Recommendation recommendation = new Recommendation();
		recommendation.setSummary("Test Alternative");
		Recommendation recommendation2 = new Recommendation();
		recommendation2.setSummary("Test Decision");
		recommendations.add(recommendation);
		recommendations.add(recommendation2);
	}

	@Test
	public void testTopKResults() {
		List<Recommendation> recommendations = new ArrayList<>();
		Recommendation recommendation = new Recommendation();
		recommendation.setSummary("Test Alternative");
		Recommendation recommendation2 = new Recommendation();
		recommendation2.setSummary("Test Decision");
		recommendations.add(recommendation);
		recommendations.add(recommendation2);
		assertEquals(1, Evaluator.getTopKRecommendations(recommendations, 1).size());
	}

	@Test
	public void testEvaluationExecute() {
		ProjectSource projectSource = new ProjectSource("TEST", "TEST", true);
		RecommendationEvaluation recommendationEvaluation = Evaluator.evaluate(decisionProblem, "", 5, projectSource);

		assertNotNull(recommendationEvaluation);
		assertEquals("TEST", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(2, recommendationEvaluation.getRecommendations().size());
		assertNotNull(recommendationEvaluation.getMetrics());
	}
}