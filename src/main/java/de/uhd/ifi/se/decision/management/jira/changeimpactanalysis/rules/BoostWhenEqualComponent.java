package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import java.util.Set;
import java.util.stream.Collectors;

import com.atlassian.jira.bc.project.component.ProjectComponent;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} is of
 * equal component.
 */
public class BoostWhenEqualComponent implements ChangePropagationFunction {

    @Override
    public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
        if (filterSettings.getSelectedElement().getJiraIssue() != null
            && nextElement.getJiraIssue() != null
            && filterSettings.getSelectedElement().getJiraIssue().getComponents() != null
            && !filterSettings.getSelectedElement().getJiraIssue().getComponents().isEmpty()) {
                if (nextElement.getJiraIssue().getComponents() != null
                    && nextElement.getJiraIssue().getComponents().isEmpty()) {
                    return 0.75;
                }

                Set<ProjectComponent> setOfMatchingComponents = filterSettings.getSelectedElement()
                    .getJiraIssue().getComponents().stream()
                    .filter(item -> nextElement.getJiraIssue().getComponents().contains(item))
                    .collect(Collectors.toSet());
                return setOfMatchingComponents.isEmpty() ? 0.75 : 1.0;
        }
        return 1.0;
    }
}
