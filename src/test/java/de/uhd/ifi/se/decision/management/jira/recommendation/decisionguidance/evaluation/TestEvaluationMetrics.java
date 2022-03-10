package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics.AveragePrecision;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics.EvaluationMetric;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics.FScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics.NumberOfTruePositives;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics.Precision;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics.Recall;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics.ReciprocalRank;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;

@SuppressWarnings({"PMD.AtLeastOneConstructor",  // For static code analysis: Rules that are not
		"PMD.BeanMembersShouldSerialize",  //       necessary for a test class
		"PMD.CommentRequired",
		"PMD.AvoidDuplicateLiterals"})
public class TestEvaluationMetrics extends TestSetUp {

	protected List<ElementRecommendation> recommendations;
	protected List<SolutionOption> groundTruthSolutionOptions;

	@Before
	public void setUp() {
		init();
		recommendations = new ArrayList<>();
		groundTruthSolutionOptions = new ArrayList<>();

		KnowledgeSource knowledgeSource = new ProjectSource("TEST");
		ElementRecommendation recommendation = new ElementRecommendation(knowledgeSource, "MySQL", "Test Url");
		ElementRecommendation recommendation2 = new ElementRecommendation(knowledgeSource, "PostgreSQL", "Test Url");
		recommendations.add(recommendation);
		recommendations.add(recommendation2);

		SolutionOption alternativeIdea = new SolutionOption();
		alternativeIdea.setType(KnowledgeType.ALTERNATIVE);
		alternativeIdea.setStatus(KnowledgeStatus.IDEA);
		alternativeIdea.setSummary("We could use MySQL"); // true positive

		SolutionOption alternativeDiscarded = new SolutionOption();
		alternativeDiscarded.setType(KnowledgeType.ALTERNATIVE);
		alternativeDiscarded.setStatus(KnowledgeStatus.DISCARDED);
		alternativeDiscarded.setSummary("We could use a NoSQL database such as Cassandra"); // false positive

		SolutionOption decision = new SolutionOption();
		decision.setType(KnowledgeType.ALTERNATIVE);
		decision.setSummary("We will use simple text files to store the data!"); // false negative
		decision.setStatus(KnowledgeStatus.DECIDED);

		groundTruthSolutionOptions.add(alternativeIdea);
		groundTruthSolutionOptions.add(alternativeDiscarded);
		groundTruthSolutionOptions.add(decision);

		recommendations = Evaluator.getTopKRecommendations(recommendations, 5);
	}

	@Test
	public void testIsMatching() {
		assertTrue(EvaluationMetric.isMatching(groundTruthSolutionOptions.get(0), recommendations.get(0)));
		assertTrue(EvaluationMetric.isMatching("MySQL", "MySQL@en"));
	}

	@Test
	public void testFScore() {
		FScore fScore = new FScore(recommendations, groundTruthSolutionOptions);
		assertEquals(0.4, fScore.calculateMetric(), 0.0);
		assertEquals("F-Score", fScore.getName());
		assertEquals(false, fScore.getDescription().isBlank());

		fScore = new FScore(0.5, 0.5);
		assertEquals(0.5, fScore.calculateMetric(), 0.0);

		fScore = new FScore(new ArrayList<>(), new ArrayList<>());
		assertEquals(0.0, fScore.calculateMetric(), 0.0);
	}

	@Test
	public void testNumberOfTruePositives() {
		NumberOfTruePositives numberOfTruePositives = new NumberOfTruePositives(recommendations,
				groundTruthSolutionOptions);
		assertEquals(1.0, numberOfTruePositives.calculateMetric(), 0.0);
		assertEquals("#True Positives", numberOfTruePositives.getName());
		assertEquals(false, numberOfTruePositives.getDescription().isBlank());
	}

	@Test
	public void testPrecision() {
		Precision precision = new Precision(recommendations, groundTruthSolutionOptions);
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
		Recall recall = new Recall(recommendations, groundTruthSolutionOptions);
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
	public void testAveragePrecision() {
		AveragePrecision averagePrecision = new AveragePrecision(recommendations, groundTruthSolutionOptions);
		assertEquals(0.33, averagePrecision.calculateMetric(), 0.1);
		assertEquals("Average Precision", averagePrecision.getName());
		assertEquals(false, averagePrecision.getDescription().isBlank());

		averagePrecision = new AveragePrecision(new ArrayList<>(), new ArrayList<>());
		assertEquals(0.0, averagePrecision.calculateMetric(), 0.0);
	}

	@Test
	public void testReciprocalRank() {
		ReciprocalRank reciprocalRank = new ReciprocalRank(recommendations, groundTruthSolutionOptions);
		assertEquals(1.0, reciprocalRank.calculateMetric(), 0.0);
		assertEquals("Reciprocal Rank", reciprocalRank.getName());
		assertEquals(false, reciprocalRank.getDescription().isBlank());

		reciprocalRank = new ReciprocalRank(new ArrayList<>(), new ArrayList<>());
		assertEquals(0.0, reciprocalRank.calculateMetric(), 0.0);

		reciprocalRank = new ReciprocalRank(recommendations, groundTruthSolutionOptions.subList(1, 2));
		assertEquals(0.0, reciprocalRank.calculateMetric(), 0.0);
	}
}