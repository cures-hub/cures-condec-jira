package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

import java.util.ArrayList;
import java.util.List;
public class DefinitionOfDoneCheck {

	private DefinitionOfDoneCheck() {
	}

	/**
	 * Returns a list of failed definition of done criteria.
	 */
	public static List<String> execute(KnowledgeElement knowledgeElement, FilterSettings filterSettings) {
		List<String> failedCompletenessCriteria = new ArrayList<>();
		if (CompletenessHandler.hasIncompleteKnowledgeLinked(knowledgeElement)) {
			failedCompletenessCriteria.add("hasIncompleteKnowledgeLinked");
		}
		if (CoverageHandler.doesNotHaveMinimumCoverage(knowledgeElement, KnowledgeType.DECISION, filterSettings)) {
			failedCompletenessCriteria.add("doesNotHaveMinimumCoverage");
		}
		return failedCompletenessCriteria;
	}
}
