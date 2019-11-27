package de.uhd.ifi.se.decision.management.jira.classification;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import smile.validation.ClassificationMeasure;

import java.util.List;
import java.util.Map;

public interface EvaluableClassifier {

	/**
	 * Evaluates classifier using predefined metrics.
	 *
	 * @return Map of evaluation results
	 * @throws Exception
	 */
	Map<String, Double> evaluateClassifier() throws Exception;

	/**
	 * Evaluates classifier using metrics in parameters.
	 *
	 * @param measurements         List of metrics to be evaluated
	 * @param partOfJiraIssueTexts
	 * @return Map of evaluation results
	 * @throws Exception
	 */
	Map<String, Double> evaluateClassifier(List<ClassificationMeasure> measurements,
										   List<DecisionKnowledgeElement> partOfJiraIssueTexts) throws Exception;
}
