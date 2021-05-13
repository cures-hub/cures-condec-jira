package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;

/**
 * Counts the number of true positives under the top-k results, i.e. the number
 * of {@link ElementRecommendation}s from a {@link KnowledgeSource} that were already
 * documented in the {@link KnowledgeGraph}.
 * 
 * @issue Should the recommendation of rejected decisions and discarded
 *        alternatives be counted as true positives?
 * @alternative Do not count recommendations that recommend rejected decisions
 *              or discarded alternatives as true positives.
 * @con Harder to implement.
 * @decision Count recommendations that recommend rejected decisions or
 *           discarded alternatives as true positives.
 * @pro Makes the collection of solution options that might solve a decision
 *      problem more complete.
 */
public class NumberOfTruePositives extends EvaluationMetric {

	public NumberOfTruePositives(List<ElementRecommendation> recommendations, List<SolutionOption> solutionOptions) {
		super(recommendations, solutionOptions);
	}

	@Override
	public String getName() {
		return "#True Positives";
	}

	@Override
	public String getDescription() {
		return "Provides the number of correctly recommended results within the top-k results.";
	}

	@Override
	public double calculateMetric() {
		int numberOfTruePositives = 0;
		for (ElementRecommendation recommendation : recommendations) {
			if (countMatches(groundTruthSolutionOptions, recommendation.getSummary()) > 0) {
				numberOfTruePositives++;
			}
		}
		return numberOfTruePositives;
	}
}
