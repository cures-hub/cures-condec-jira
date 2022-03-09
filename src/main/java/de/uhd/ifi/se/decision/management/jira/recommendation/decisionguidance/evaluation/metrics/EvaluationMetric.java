package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics;

import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.Evaluator;

/**
 * Abstract superclass for evaluation metrics, such as
 * {@link NumberOfTruePositives} and {@link ReciprocalRank}.
 * 
 * Use {@link Evaluator#getTopKRecommendations(List, int)} to trim the list of
 * {@link ElementRecommendation}s to the top-k results.
 */
public abstract class EvaluationMetric {

	/**
	 * Ground truth recommendations to which the given ones are compared
	 */
	protected List<SolutionOption> groundTruthSolutionOptions;

	/**
	 * Recommendations to be evaluated
	 */
	protected List<ElementRecommendation> recommendations;

	/**
	 * @param recommendations {@link EvaluationMetric#recommendations}
	 */
	public EvaluationMetric(List<ElementRecommendation> recommendations) {
		this.recommendations = recommendations;
	}

	/**
	 * @param recommendations
	 *            {@link ElementRecommendation}s from a {@link KnowledgeSource},
	 *            such as DBPedia or a Jira project.
	 * @param groundTruthSolutionOptions
	 *            gold standard/ground truth that was already documented.
	 */
	public EvaluationMetric(List<ElementRecommendation> recommendations, List<SolutionOption> groundTruthSolutionOptions) {
		this(recommendations);
		this.groundTruthSolutionOptions = groundTruthSolutionOptions;
	}

	/**
	 * @return metric value, e.g. for {@link Precision} or {@link ReciprocalRank}.
	 */
	@XmlElement(name = "value")
	public abstract double calculateMetric();

	/**
	 * @return name of the metric that is shown in the user interface (settings
	 *         page).
	 */
	@XmlElement
	public abstract String getName();

	/**
	 * @return description of the metric that is shown in the user interface
	 *         (settings page).
	 */
	@XmlElement
	public abstract String getDescription();

	/**
	 * @param knowledgeElements
	 *            list of already documented solution options whose summary is
	 *            matched against the recommendation.
	 * @param matchingString
	 *            summary of one single recommendation.
	 * @return number of matches.
	 */
	protected static int countMatches(List<SolutionOption> knowledgeElements, String matchingString) {
		int counter = 0;
		for (KnowledgeElement knowledgeElement : knowledgeElements) {
			if (isMatching(knowledgeElement.getSummary(), matchingString)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Check whether the given knowledge element matches the given recommendation.
	 * @see EvaluationMetric#isMatching(String, String) How matching of summaries is defined
	 *
	 * @param knowledgeElement Knowledge element, e.g. decision or alternative, to be compared to the given
	 *                         recommendation.
	 * @param recommendation Recommendation to be compared to the given knowledge element.
	 * @return true, if the summaries of the given knowledge element and recommendation match, otherwise false.
	 */
	public static boolean isMatching(KnowledgeElement knowledgeElement, Recommendation recommendation) {
		return isMatching(knowledgeElement.getSummary(), ((ElementRecommendation) recommendation).getSummary());
	}

	/**
	 * Check whether the given Strings match each other.
	 *
	 * @param summaryA First string to be compared to the second one.
	 * @param summaryB Second string to be compared to the first one.
	 * @return true, if one of the Strings {@link String#contains(CharSequence)} the other one after normalization.
	 */
	public static boolean isMatching(String summaryA, String summaryB) {
		return summaryA.toLowerCase(Locale.ENGLISH).contains(summaryB.toLowerCase(Locale.ENGLISH).trim())
				|| summaryB.toLowerCase(Locale.ENGLISH).contains(summaryA.toLowerCase(Locale.ENGLISH).trim());
	}

	/**
	 * @return {@link EvaluationMetric#groundTruthSolutionOptions}
	 */
	public List<SolutionOption> getGroundTruthSolutionOptions() {
		return groundTruthSolutionOptions;
	}

	/**
	 * @param groundTruthSolutionOptions {@link EvaluationMetric#groundTruthSolutionOptions}
	 */
	public void setGroundTruthSolutionOptions(List<SolutionOption> groundTruthSolutionOptions) {
		this.groundTruthSolutionOptions = groundTruthSolutionOptions;
	}

	/**
	 * @return {@link EvaluationMetric#recommendations}
	 */
	public List<ElementRecommendation> getRecommendations() {
		return recommendations;
	}

	/**
	 * @param recommendations {@link EvaluationMetric#recommendations}
	 */
	public void setRecommendations(List<ElementRecommendation> recommendations) {
		this.recommendations = recommendations;
	}
}