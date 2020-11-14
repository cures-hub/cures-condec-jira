package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationframework.evaluationmethods;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public interface EvaluationMethod {
	double calculateMetric(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions, int topKResults);
}
