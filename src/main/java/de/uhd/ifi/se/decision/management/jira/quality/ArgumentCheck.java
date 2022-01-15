package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class ArgumentCheck extends KnowledgeElementCheck {

	public ArgumentCheck(KnowledgeElement elementToBeChecked) {
		super(elementToBeChecked);
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(DefinitionOfDone definitionOfDone) {

		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();

		if (!hasDecisionOrAlternative()) {
			qualityCheckResults.add(
					new QualityCriterionCheckResult(QualityCriterionType.ARGUMENT_LINKED_TO_SOLUTION_OPTION, true));
		} else {
			qualityCheckResults.add(
					new QualityCriterionCheckResult(QualityCriterionType.ARGUMENT_LINKED_TO_SOLUTION_OPTION, false));

		}

		return qualityCheckResults;
	}

	private boolean hasDecisionOrAlternative() {
		return element.hasNeighborOfType(KnowledgeType.DECISION)
				|| element.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
	}

}
