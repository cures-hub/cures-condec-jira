package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Checks whether a code file fulfills the {@link DefinitionOfDone}.
 */
public class OtherCheck extends KnowledgeElementCheck {

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(KnowledgeElement issue,
			DefinitionOfDone definitionOfDone) {

		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();

		return qualityCheckResults;
	}

}
