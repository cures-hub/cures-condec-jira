package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.AveragePrecision;
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
	protected Precision precision;
	protected Recall recall;
	protected AveragePrecision averagePrecision;
	protected ReciprocalRank reciprocalRank;
	protected NumberOfTruePositives numberOfTruePositives;
	protected List<Recommendation> recommendations;
	protected List<KnowledgeElement> solutionOptions;

	@Before
	public void setUp() {
		init();

		recommendations = new ArrayList<>();
		solutionOptions = new ArrayList<>();

		KnowledgeSource knowledgeSource = new ProjectSource("TEST", "TEST", true);

		Recommendation recommendation = new Recommendation(knowledgeSource, "Test Recommendation", "Test Url");
		Recommendation recommendation2 = new Recommendation(knowledgeSource, "Recommendation Test", "Test Url");

		recommendations.add(recommendation);
		recommendations.add(recommendation2);

		KnowledgeElement alternativeIdea = new KnowledgeElement();
		alternativeIdea.setType(KnowledgeType.ALTERNATIVE);
		alternativeIdea.setStatus(KnowledgeStatus.IDEA);
		alternativeIdea.setSummary("Test Recommendation"); // true positive

		KnowledgeElement alternativeDiscarded = new KnowledgeElement();
		alternativeDiscarded.setType(KnowledgeType.ALTERNATIVE);
		alternativeDiscarded.setStatus(KnowledgeStatus.DISCARDED);
		alternativeDiscarded.setSummary("Test Discarded"); // false positive

		KnowledgeElement decision = new KnowledgeElement();
		decision.setType(KnowledgeType.ALTERNATIVE);
		decision.setSummary("Test DECISION"); // false Negative
		decision.setStatus(KnowledgeStatus.DECIDED);

		solutionOptions.add(alternativeIdea);
		solutionOptions.add(alternativeDiscarded);
		solutionOptions.add(decision);

		fScore = new FScore(recommendations, solutionOptions, 5);
		precision = new Precision(recommendations, solutionOptions, 5);
		recall = new Recall(recommendations, solutionOptions, 5);
		averagePrecision = new AveragePrecision(recommendations, solutionOptions, 5);
		reciprocalRank = new ReciprocalRank(recommendations, solutionOptions, 5);
		numberOfTruePositives = new NumberOfTruePositives(recommendations, solutionOptions, 5);
	}

	@Test
	public void testCalculations() {
		assertEquals(0.5, fScore.calculateMetric(), 0.0);
		assertEquals(0.5, precision.calculateMetric(), 0.0);
		assertEquals(0.33, recall.calculateMetric(), 0.1);
		assertEquals(0.33, averagePrecision.calculateMetric(), 0.1);
		assertEquals(1.0, reciprocalRank.calculateMetric(), 0.0);
		assertEquals(1.0, numberOfTruePositives.calculateMetric(), 0.0);
	}

	@Test
	public void testLabels() {
		assertEquals("F-Score", fScore.getName());
		assertEquals("Precision(@k)", precision.getName());
		assertEquals("Recall(@k)", recall.getName());
		assertEquals("Average Precision", averagePrecision.getName());
		assertEquals("Reciprocal Rank", reciprocalRank.getName());
		assertEquals("#True Positives", numberOfTruePositives.getName());
	}

	@Test
	public void testDescriptions() {
		assertEquals(false, fScore.getDescription().isBlank());
		assertEquals(false, precision.getDescription().isBlank());
		assertEquals(false, recall.getDescription().isBlank());
		assertEquals(false, averagePrecision.getDescription().isBlank());
		assertEquals(false, reciprocalRank.getDescription().isBlank());
		assertEquals(false, numberOfTruePositives.getDescription().isBlank());
	}
}