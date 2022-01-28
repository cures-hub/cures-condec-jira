package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.Set;
import java.util.stream.Collectors;

import com.atlassian.jira.bc.project.component.ProjectComponent;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations based on their assignment to specific components.
 * Elements that are assigned to the same component are stronger related
 * than elements that are not.
 */
public class ComponentContextInformationProvider implements ContextInformationProvider {

    @Override
    public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
        if (baseElement.getJiraIssue() != null && otherElement.getJiraIssue() != null
            && baseElement.getJiraIssue().getComponents() != null && !baseElement.getJiraIssue().getComponents().isEmpty()) {
            if (otherElement.getJiraIssue().getComponents() == null
                    || otherElement.getJiraIssue().getComponents().isEmpty()) {
                return new RecommendationScore(0.75f, getName());
            }

            Set<ProjectComponent> setOfMatchingComponents = baseElement.getJiraIssue()
                    .getComponents().stream().filter(item -> otherElement.getJiraIssue().getComponents().contains(item))
                    .collect(Collectors.toSet());
            double score = setOfMatchingComponents.isEmpty() ? 0.75  : 1.0;
            return new RecommendationScore((float) score, getName());
        }
		return new RecommendationScore(1.0f, getName());
    }
}
