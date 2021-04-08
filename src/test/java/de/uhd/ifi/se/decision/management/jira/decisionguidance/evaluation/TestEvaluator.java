package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestEvaluator extends TestSetUp {

	private List<KnowledgeSource> knowledgeSources;
	private List<KnowledgeElement> solutionOptions;
	private Evaluator recommender;
	private KnowledgeElement decisionProblem;

	@Before
	public void setUp() {
		init();

		decisionProblem = KnowledgeElements.getTestKnowledgeElements().get(4);

		// search for solutions in the same project
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
		recommender = new Evaluator(KnowledgeElements.getTestKnowledgeElement(), "", 5, projectSource);

		RDFSource rdfSource = new RDFSource();
		rdfSource.setName("DBPedia");

		knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		solutionOptions = new ArrayList<>();
		KnowledgeElement alternative = new KnowledgeElement();
		alternative.setSummary("Test Alternative");
		alternative.setStatus(KnowledgeStatus.IDEA);
		KnowledgeElement decision = new KnowledgeElement();
		decision.setSummary("Test Decision");
		decision.setStatus(KnowledgeStatus.DECIDED);
		solutionOptions.add(alternative);
		solutionOptions.add(decision);

		List<Recommendation> recommendations = new ArrayList<>();
		Recommendation recommendation = new Recommendation();
		recommendation.setSummary("Test Alternative");
		Recommendation recommendation2 = new Recommendation();
		recommendation2.setSummary("Test Decision");
		recommendations.add(recommendation);
		recommendations.add(recommendation2);
	}

	@After
	public void tearDown() throws Exception {
		recommender = null;
	}

	@Test
	public void testEvaluation() {
		recommender.setKnowledgeElement(KnowledgeElements.getTestKnowledgeElement());
		recommender.getKnowledgeElement();
		assertEquals(KnowledgeElements.getTestKnowledgeElement().getId(),
				recommender.evaluate(KnowledgeElements.getTestKnowledgeElement()).getKnowledgeElement().getId());
	}

	@Test
	public void testEvaluationExecute() {
		RecommendationEvaluation recommendationEvaluation = recommender.evaluate(decisionProblem).execute();

		assertNotNull(recommendationEvaluation);
		assertEquals("TEST", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(2, recommendationEvaluation.getRecommendations().size());
		assertNotNull(recommendationEvaluation.getMetrics());
	}
}