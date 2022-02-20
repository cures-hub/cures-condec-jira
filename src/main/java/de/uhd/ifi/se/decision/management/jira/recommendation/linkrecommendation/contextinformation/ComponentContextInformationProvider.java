package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.Set;
import java.util.stream.Collectors;

import com.atlassian.jira.bc.project.component.ProjectComponent;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations based on the assignment to specific components. Elements that
 * are assigned to the same component are stronger related than elements that
 * are not.
 */
public class ComponentContextInformationProvider extends ContextInformationProvider {

	/**
	 * Per default, this context information provider is deactivated.
	 */
	public ComponentContextInformationProvider() {
		super();
		isActive = false;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
		float score = 1;
		if (baseElement.getJiraIssue() != null && otherElement.getJiraIssue() != null) {
			if (!baseElement.getJiraIssue().getComponents().isEmpty()) {
				Set<ProjectComponent> setOfMatchingComponents = baseElement.getJiraIssue().getComponents().stream()
						.filter(item -> otherElement.getJiraIssue().getComponents().contains(item))
						.collect(Collectors.toSet());
				score = setOfMatchingComponents.isEmpty() ? 0 : 1;
			}
		}
		return new RecommendationScore(score, getDescription());
	}

	@Override
	public String getExplanation() {
		return "Assumes that knowledge elements belonging the same component are related.";
	}

	@Override
	public String getDescription() {
		return "Recommend elements that are assigned to the same component as the source element";
	}
}
