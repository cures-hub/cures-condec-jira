package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.RecommendationEvaluation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestEvaluationRecommender extends TestSetUp {

	private ProjectSource projectSource;
	private RDFSource rdfSource;
	private List<KnowledgeSource> knowledgeSources;
	private List<KnowledgeElement> solutionOptions;
	private List<Recommendation> recommendations;
	private EvaluationRecommender recommender;

	@Before
	public void setUp() {
		init();


		recommender = new EvaluationRecommender(KnowledgeElements.getTestKnowledgeElement(), "", 5);

		projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true); // search for solutions
		// in the same
		// project
		rdfSource = new RDFSource(JiraProjects.getTestProject().getKey());


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

		recommendations = new ArrayList<>();
		Recommendation recommendation = new Recommendation();
		recommendation.setRecommendations("Test Alternative");
		Recommendation recommendation2 = new Recommendation();
		recommendation2.setRecommendations("Test Decision");
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
		assertEquals(KnowledgeElements.getTestKnowledgeElement().getId(), recommender.evaluate(KnowledgeElements.getTestKnowledgeElement()).getKnowledgeElement().getId());
		assertEquals("TEST", recommender.withKnowledgeSource(knowledgeSources, "TEST").getKnowledgeSources().get(0).getName());
	}


	@Test
	public void testEvaluationExecute() {

		ConfigPersistenceManager.setRecommendationInput("TEST", "KEYWORD");
		RecommendationEvaluation recommendationEvaluation = recommender.evaluate(KnowledgeElements.getTestKnowledgeElement()).withKnowledgeSource(knowledgeSources, "TEST").execute();

		assertNotNull(recommendationEvaluation);
		assertEquals("TEST", recommendationEvaluation.getKnowledgeSourceName());
		assertEquals(RecommenderType.ISSUE.toString(), recommendationEvaluation.getRecommenderType());
		assertEquals(2, recommendationEvaluation.getNumberOfResults());
		assertNotNull(recommendationEvaluation.getMetrics());


		EvaluationRecommender recommender2 = new EvaluationRecommender(KnowledgeElements.getTestKnowledgeElement(), "Not blank", 5);
		recommendationEvaluation = recommender2.evaluate(KnowledgeElements.getTestKnowledgeElement()).withKnowledgeSource(knowledgeSources, "TEST").execute();
		assertEquals("TEST", recommendationEvaluation.getKnowledgeSourceName());
		assertEquals(RecommenderType.KEYWORD.toString(), recommendationEvaluation.getRecommenderType());

	}

	@Test
	public void testGetElementWithStatus() {
		assertEquals(1, recommender.getElementsWithStatus(solutionOptions, KnowledgeStatus.IDEA).size());
		assertEquals(1, recommender.getElementsWithStatus(solutionOptions, KnowledgeStatus.DECIDED).size());
		assertEquals(0, recommender.getElementsWithStatus(solutionOptions, null).size());
		assertEquals(0, recommender.getElementsWithStatus(null, KnowledgeStatus.IDEA).size());
	}


	@Test
	public void testRecommendationEvaluation() {
		RecommendationEvaluation recommendationEvaluation = new RecommendationEvaluation(RecommenderType.ISSUE.toString(), "TEST", 6, null);

		recommendationEvaluation.setKnowledgeSourceName("TESTTEST");
		recommendationEvaluation.setNumberOfResults(2);
		recommendationEvaluation.setRecommenderType("KEYWORD");

		assertEquals(RecommenderType.KEYWORD.toString(), recommendationEvaluation.getRecommenderType());
		assertEquals("TESTTEST", recommendationEvaluation.getKnowledgeSourceName());
		assertEquals(2, recommendationEvaluation.getNumberOfResults());
	}

	@Test
	public void testgetResultsFromKnowledgeSource() {
		BaseRecommender recommender = new EvaluationRecommender(KnowledgeElements.getTestKnowledgeElement(), "Not blank", 5);
		KnowledgeSource knowledgeSource = new ProjectSource("TEST", "TEST", false);
		knowledgeSource.setRecommenderType(RecommenderType.KEYWORD);
		assertNotNull(recommender.getResultFromKnowledgeSource(knowledgeSource));
	}

}
