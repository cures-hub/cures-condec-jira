package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} is coupled with the source
 * element, i.e. if both have received updates in the same timeframe.
 */
public class BoostWhenTimelyCoupled implements ChangePropagationFunction{

    @Override
    public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
        float ruleWeight = filterSettings.getChangeImpactAnalysisConfig().getPropagationRules()
            .get(filterSettings.getChangeImpactAnalysisConfig().getPropagationRules()
                .indexOf(ChangePropagationRule.BOOST_WHEN_TIMELY_COUPLED))
            .getWeightValue();
        
        Set<Date> coupledUpdates = null;
        for (Date rootElementUpdate : filterSettings.getSelectedElement().getUpdateDateAndAuthor().keySet()) {
            // 600000ms equals 10 minutes, as such when an element was updated either 10 minutes before or
            // after the source node coupling will be assumed
            coupledUpdates = nextElement.getUpdateDateAndAuthor().keySet().stream().filter(updateDate ->
                updateDate.getTime() > (rootElementUpdate.getTime() - 600000)
                && updateDate.getTime() < (rootElementUpdate.getTime() + 600000)).collect(Collectors.toSet());
        } 
        if (coupledUpdates != null && !coupledUpdates.isEmpty()) {
            return (1 - Math.pow(3, (-1 * coupledUpdates.size()))) * ruleWeight >= 1.0
                ? 1.0
                : (1 - Math.pow(3, (-1 * coupledUpdates.size()))) * ruleWeight;
        } else {
            return 0.5 * (2 - ruleWeight) >= 1.0
                ? 1.0
                : 0.5 * (2 - ruleWeight);
        }
    }
}
