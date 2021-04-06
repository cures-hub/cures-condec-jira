package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.DecisionGuidanceConfiguration;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestEvaluator extends TestSetUp {

	private List<KnowledgeSource> knowledgeSources;
	private List<KnowledgeElement> solutionOptions;
	private Evaluator recommender;
	private KnowledgeElement testElement;

	@Before
	public void setUp() {
		init();

		testElement = new KnowledgeElement();
		testElement.setId(123);
		testElement.setSummary("How can we implement the feature");

		recommender = new Evaluator(KnowledgeElements.getTestKnowledgeElement(), "", 5);

		// search for solutions in the same project
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
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
		assertEquals("TEST",
				recommender.withKnowledgeSource(knowledgeSources, "TEST").getKnowledgeSources().get(0).getName());
	}

	@Test
	public void testEvaluationExecute() {
		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration("TEST");
		decisionGuidanceConfiguration.setRecommendationInput("KEYWORD", true);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration("TEST", decisionGuidanceConfiguration);

		RecommendationEvaluation recommendationEvaluation = recommender.evaluate(testElement)
				.withKnowledgeSource(knowledgeSources, "TEST").execute();

		assertNotNull(recommendationEvaluation);
		assertEquals("TEST", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(RecommenderType.ISSUE, recommendationEvaluation.getRecommenderType());
		assertEquals(2, recommendationEvaluation.getRecommendations().size());
		assertNotNull(recommendationEvaluation.getMetrics());

		Evaluator recommender2 = new Evaluator(KnowledgeElements.getTestKnowledgeElement(),
				"Not blank", 5);
		recommendationEvaluation = recommender2.evaluate(KnowledgeElements.getTestKnowledgeElement())
				.withKnowledgeSource(knowledgeSources, "TEST").execute();
		assertEquals("TEST", recommendationEvaluation.getKnowledgeSource().getName());
		assertEquals(RecommenderType.KEYWORD, recommendationEvaluation.getRecommenderType());
	}

	@Test
	public void testGetElementWithStatus() {
		assertEquals(1, recommender.getElementsWithStatus(solutionOptions, KnowledgeStatus.IDEA).size());
		assertEquals(1, recommender.getElementsWithStatus(solutionOptions, KnowledgeStatus.DECIDED).size());
		assertEquals(0, recommender.getElementsWithStatus(solutionOptions, null).size());
		assertEquals(0, recommender.getElementsWithStatus(null, KnowledgeStatus.IDEA).size());
	}

	@Test
	public void testGetResultsFromKnowledgeSource() {
		Evaluator recommender = new Evaluator(KnowledgeElements.getTestKnowledgeElement(),
				"Not blank", 5);
		KnowledgeSource knowledgeSource = new ProjectSource("TEST", "TEST", false);
		assertNotNull(recommender.getRecommendations(knowledgeSource));
	}
}