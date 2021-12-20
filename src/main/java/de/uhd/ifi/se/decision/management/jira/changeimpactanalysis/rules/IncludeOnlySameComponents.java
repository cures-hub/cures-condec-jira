package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import java.util.Set;
import java.util.stream.Collectors;

import com.atlassian.jira.bc.project.component.ProjectComponent;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

public class IncludeOnlySameComponents implements ChangePropagationFunction {

    @Override
    public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement currentElement, Link link) {
        if (filterSettings.getSelectedElement().getJiraIssue() != null
            && currentElement.getJiraIssue() != null
            && !filterSettings.getSelectedElement().getJiraIssue().getComponents().isEmpty()) {
                if (currentElement.getJiraIssue().getComponents().isEmpty()) {
                    return 0.0;
                }
                Set<ProjectComponent> setOfMatchingComponents = filterSettings.getSelectedElement()
                    .getJiraIssue().getComponents().stream()
                    .filter(item -> currentElement.getJiraIssue().getComponents().contains(item))
                    .collect(Collectors.toSet());
                return setOfMatchingComponents.isEmpty() ? 0.0 : 1.0;
        }
        return 1.0;
    }
}
