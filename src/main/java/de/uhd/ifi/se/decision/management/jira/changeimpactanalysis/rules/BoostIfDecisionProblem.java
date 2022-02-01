package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.DecisionProblemContextInformationProvider;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} is 
 * of {@link KnowledgeType} decision problem.
 */
public class BoostIfDecisionProblem implements ChangePropagationFunction {

    private static final DecisionProblemContextInformationProvider similarityProvider = new DecisionProblemContextInformationProvider();

    @Override
    public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
        float ruleWeight = ChangePropagationRule.getWeightForRule(filterSettings,
            ChangePropagationRuleType.BOOST_IF_DECISION_PROBLEM);

        float similarityScore = similarityProvider.assessRelation(filterSettings.getSelectedElement(), nextElement)
            .getValue();
        return similarityScore * (2 - ruleWeight) >= 1.0 ? 1.0 : similarityScore * (2 - ruleWeight);
    }
    
}
