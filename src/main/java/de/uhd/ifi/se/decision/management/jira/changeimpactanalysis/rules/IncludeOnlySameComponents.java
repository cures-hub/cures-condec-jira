package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import java.util.Set;
import java.util.stream.Collectors;

import com.atlassian.jira.bc.project.component.ProjectComponent;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is not propagated after a
 * {@link KnowledgeElement} without the same {@link ProjectComponent} was reached.
 * 
 * For example, if a change is made in a work item with the component "Server", the 
 * change is not propagated for other elements which do not have the same component.
 */
public class IncludeOnlySameComponents implements ChangePropagationFunction {

    @Override
    public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
        if (filterSettings.getSelectedElement().getJiraIssue() != null
            && nextElement.getJiraIssue() != null
            && !filterSettings.getSelectedElement().getJiraIssue().getComponents().isEmpty()) {
                if (nextElement.getJiraIssue().getComponents().isEmpty()) {
                    return 0.0;
                }
                Set<ProjectComponent> setOfMatchingComponents = filterSettings.getSelectedElement()
                    .getJiraIssue().getComponents().stream()
                    .filter(item -> nextElement.getJiraIssue().getComponents().contains(item))
                    .collect(Collectors.toSet());
                return setOfMatchingComponents.isEmpty() ? 0.0 : 1.0;
        }
        return 1.0;
    }
}
