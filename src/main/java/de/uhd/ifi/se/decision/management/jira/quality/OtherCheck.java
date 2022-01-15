package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Checks whether a {@link KnowledgeElement} of {@link KnowledgeType#OTHER}
 * (e.g. Jira issue) fulfills the {@link DefinitionOfDone}.
 */
public class OtherCheck extends KnowledgeElementCheck {

	public OtherCheck(KnowledgeElement elementToBeChecked) {
		super(elementToBeChecked);
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(DefinitionOfDone definitionOfDone) {
		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();
		return qualityCheckResults;
	}
}