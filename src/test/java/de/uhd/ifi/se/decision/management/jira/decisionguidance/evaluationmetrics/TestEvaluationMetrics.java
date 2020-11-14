package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationmetrics;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationframework.evaluationmethods.AveragePrecision;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationframework.evaluationmethods.FScore;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationframework.evaluationmethods.ReciprocalRank;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestEvaluationMetrics extends TestSetUp {

	protected FScore fScore;
	protected AveragePrecision averagePrecision;
	protected ReciprocalRank reciprocalRank;
	protected List<Recommendation> recommendations;
	protected List<KnowledgeElement> solutionOptions;

	@Before
	public void setUp() {
		init();

		fScore = new FScore();
		averagePrecision = new AveragePrecision();
		reciprocalRank = new ReciprocalRank();

		recommendations = new ArrayList<>();
		solutionOptions = new ArrayList<>();

		Recommendation recommendation = new Recommendation("TEST", "Test Recommendation", "Test Url");
		Recommendation recommendation2 = new Recommendation("TEST", "Recommendation Test", "Test Url");

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

	}

	@Test
	public void testCalculations() {
		assertEquals(0.5, fScore.calculateMetric(recommendations, solutionOptions, 5), 0.0);
		assertEquals(1.0, averagePrecision.calculateMetric(recommendations, solutionOptions, 5), 0.0);
		assertEquals(1.0, reciprocalRank.calculateMetric(recommendations, solutionOptions, 5), 0.0);
	}

	@Test
	public void testDefault() {
		assertEquals(0.0, fScore.calculateMetric(new ArrayList<>(), new ArrayList<>(), 5), 0.0);
		assertEquals(0.0, averagePrecision.calculateMetric(new ArrayList<>(), new ArrayList<>(), 5), 0.0);
		assertEquals(0.0, reciprocalRank.calculateMetric(new ArrayList<>(), new ArrayList<>(), 5), 0.0);
	}

}
