package de.uhd.ifi.se.decision.management.jira.view;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDoneChecker;

import java.util.List;

public class ToolTip {

	public static String buildToolTip(KnowledgeElement knowledgeElement, String defaultText) {
		String text = "";

		if ((knowledgeElement.getProject() != null) && (knowledgeElement.getProject().getProjectKey() != null)) {
			FilterSettings filterSettings = new FilterSettings(knowledgeElement.getProject().getProjectKey(), "");
			DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(knowledgeElement.getProject().getProjectKey());
			filterSettings.setLinkDistance(definitionOfDone.getMaximumLinkDistanceToDecisions());
			filterSettings.setMinimumDecisionCoverage(definitionOfDone.getMinimumDecisionsWithinLinkDistance());

			List<String> failedDefinitionOfDoneCheckCriteriaCriteria =
				DefinitionOfDoneChecker.getFailedDefinitionOfDoneCheckCriteria(knowledgeElement, filterSettings);
			List<String> failedCompletenessCheckCriteria =
				DefinitionOfDoneChecker.getFailedCompletenessCheckCriteria(knowledgeElement);
			if (failedDefinitionOfDoneCheckCriteriaCriteria.contains("doesNotHaveMinimumCoverage")) {
				text = text.concat("Minimum decision coverage is not reached." + System.lineSeparator() + System.lineSeparator());
			}
			if (failedDefinitionOfDoneCheckCriteriaCriteria.contains("hasIncompleteKnowledgeLinked")) {
				text = text.concat("Linked decision knowledge is incomplete." + System.lineSeparator() + System.lineSeparator());
			}
			if (!failedCompletenessCheckCriteria.isEmpty()) {
				text = text.concat("Failed knowledge completeness criteria:" + System.lineSeparator());
				text = text.concat(String.join(System.lineSeparator(), failedCompletenessCheckCriteria));
			}
		}

		if (text.isBlank() && knowledgeElement.getDescription() != null
			&& !knowledgeElement.getDescription().isBlank() && !knowledgeElement.getDescription().equals("undefined")) {
			text = defaultText;
		}
		text = text.strip();
		return text;
	}
}
