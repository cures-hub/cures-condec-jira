package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import java.util.Set;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} is of
 * equal decision group.
 */
public class BoostWhenEqualDecisionGroup implements ChangePropagationFunction {

    @Override
    public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
        if (!filterSettings.getSelectedElement().getDecisionGroups().isEmpty()) {
            if (nextElement.getDecisionGroups().isEmpty()) {
                return 0.75;
            }
            Set<String> setOfMatchingComponents = filterSettings.getSelectedElement()
                .getDecisionGroups().stream()
                .filter(item -> nextElement.getDecisionGroups().contains(item))
                .collect(Collectors.toSet());
            return setOfMatchingComponents.isEmpty() ? 0.75 : 1.0;
        } else {
            return 1.0;
        }
    }    
}
