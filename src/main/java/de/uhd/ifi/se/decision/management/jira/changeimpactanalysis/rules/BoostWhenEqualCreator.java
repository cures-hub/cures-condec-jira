package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.UserContextInformationProvider;

public class BoostWhenEqualCreator implements ChangePropagationFunction {

    private static final UserContextInformationProvider similarityProvider = new UserContextInformationProvider();

    @Override
    public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
        float ruleWeight = ChangePropagationRule.getWeightForRule(filterSettings,
                ChangePropagationRuleType.BOOST_WHEN_EQUAL_CREATOR);
        float similarityScore = similarityProvider.assessRelation(filterSettings.getSelectedElement(), nextElement)
                .getValue();
        return ChangePropagationRule.addWeightValue(ruleWeight, similarityScore);
    }
}