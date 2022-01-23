package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import java.util.Set;
import java.util.stream.Collectors;

import com.atlassian.jira.bc.project.component.ProjectComponent;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} is of equal
 * component.
 */
public class BoostWhenEqualComponent implements ChangePropagationFunction {

	@Override
	public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
		float ruleWeight = ChangePropagationRule.getWeightForRule(filterSettings,
				ChangePropagationRuleType.BOOST_WHEN_EQUAL_COMPONENT);
		double weightFactor = (0.75 * (2 - ruleWeight)) >= 1.0 ? 1.0 : (0.75 * (2 - ruleWeight));

		if (filterSettings.getSelectedElement().getJiraIssue() != null && nextElement.getJiraIssue() != null
				&& filterSettings.getSelectedElement().getJiraIssue().getComponents() != null
				&& !filterSettings.getSelectedElement().getJiraIssue().getComponents().isEmpty()) {
			if (nextElement.getJiraIssue().getComponents() != null
					&& nextElement.getJiraIssue().getComponents().isEmpty()) {
				return weightFactor;
			}

			Set<ProjectComponent> setOfMatchingComponents = filterSettings.getSelectedElement().getJiraIssue()
					.getComponents().stream().filter(item -> nextElement.getJiraIssue().getComponents().contains(item))
					.collect(Collectors.toSet());
			return setOfMatchingComponents.isEmpty() ? weightFactor : 1.0;
		}
		return 1.0;
	}
}
