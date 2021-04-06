package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.AveragePrecision;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.EvaluationMetric;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.FScore;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.NumberOfTruePositives;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.Precision;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.Recall;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.ReciprocalRank;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestEvaluationMetrics extends TestSetUp {

	protected FScore fScore;
	protected AveragePrecision averagePrecision;
	protected NumberOfTruePositives numberOfTruePositives;
	protected List<Recommendation> recommendations;
	protected List<KnowledgeElement> solutionOptions;

	@Before
	public void setUp() {
		init();

		recommendations = new ArrayList<>();
		solutionOptions = new ArrayList<>();

		KnowledgeSource knowledgeSource = new ProjectSource("TEST", "TEST", true);

		Recommendation recommendation = new Recommendation(knowledgeSource, "MySQL", "Test Url");
		Recommendation recommendation2 = new Recommendation(knowledgeSource, "PostgreSQL", "Test Url");

		recommendations.add(recommendation);
		recommendations.add(recommendation2);

		KnowledgeElement alternativeIdea = new KnowledgeElement();
		alternativeIdea.setType(KnowledgeType.ALTERNATIVE);
		alternativeIdea.setStatus(KnowledgeStatus.IDEA);
		alternativeIdea.setSummary("We could use MySQL"); // true positive

		KnowledgeElement alternativeDiscarded = new KnowledgeElement();
		alternativeDiscarded.setType(KnowledgeType.ALTERNATIVE);
		alternativeDiscarded.setStatus(KnowledgeStatus.DISCARDED);
		alternativeDiscarded.setSummary("We could use a NoSQL database such as Cassandra"); // false positive

		KnowledgeElement decision = new KnowledgeElement();
		decision.setType(KnowledgeType.ALTERNATIVE);
		decision.setSummary("We will use simple text files to store the data!"); // false Negative
		decision.setStatus(KnowledgeStatus.DECIDED);

		solutionOptions.add(alternativeIdea);
		solutionOptions.add(alternativeDiscarded);
		solutionOptions.add(decision);

		recommendations = EvaluationMetric.getTopKRecommendations(recommendations, 5);

		fScore = new FScore(recommendations, solutionOptions);
		numberOfTruePositives = new NumberOfTruePositives(recommendations, solutionOptions);
		averagePrecision = new AveragePrecision(recommendations, solutionOptions);
	}

	@Test
	public void testAllMetricsCalculations() {
		assertEquals(0.5, fScore.calculateMetric(), 0.0);
		assertEquals(0.33, averagePrecision.calculateMetric(), 0.1);
		assertEquals(1.0, numberOfTruePositives.calculateMetric(), 0.0);
	}

	@Test
	public void testLabels() {
		assertEquals("F-Score", fScore.getName());
		assertEquals("Average Precision", averagePrecision.getName());
		assertEquals("#True Positives", numberOfTruePositives.getName());
	}

	@Test
	public void testDescriptions() {
		assertEquals(false, fScore.getDescription().isBlank());
		assertEquals(false, averagePrecision.getDescription().isBlank());
		assertEquals(false, numberOfTruePositives.getDescription().isBlank());
	}

	@Test
	public void testPrecision() {
		Precision precision = new Precision(recommendations, solutionOptions);
		assertEquals(0.5, precision.calculateMetric(), 0.0);
		assertEquals("Precision(@k)", precision.getName());
		assertEquals(false, precision.getDescription().isBlank());

		precision = new Precision(recommendations, 2);
		assertEquals(2, recommendations.size());
		assertEquals(1.0, precision.calculateMetric(), 0.0);

		precision = new Precision(new ArrayList<>(), 0);
		assertEquals(0.0, precision.calculateMetric(), 0.0);
	}

	@Test
	public void testRecall() {
		Recall recall = new Recall(recommendations, solutionOptions);
		assertEquals("Recall(@k)", recall.getName());
		assertEquals(0.33, recall.calculateMetric(), 0.1);
		assertEquals(false, recall.getDescription().isBlank());

		recall = new Recall(1, 1);
		assertEquals(0.5, recall.calculateMetric(), 0.0);

		recall = new Recall(0, 0);
		assertEquals(0.0, recall.calculateMetric(), 0.0);

		recall = new Recall(5, -1);
		assertEquals(1.0, recall.calculateMetric(), 0.0);
	}

	@Test
	public void testReciprocalRank() {
		ReciprocalRank reciprocalRank = new ReciprocalRank(recommendations, solutionOptions);
		assertEquals(1.0, reciprocalRank.calculateMetric(), 0.0);
		assertEquals("Reciprocal Rank", reciprocalRank.getName());
		assertEquals(false, reciprocalRank.getDescription().isBlank());

		reciprocalRank = new ReciprocalRank(new ArrayList<>(), new ArrayList<>());
		assertEquals(0.0, reciprocalRank.calculateMetric(), 0.0);

		reciprocalRank = new ReciprocalRank(recommendations, new ArrayList<>());
		assertEquals(0.0, reciprocalRank.calculateMetric(), 0.0);
	}
}