package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class ArgumentCheck extends KnowledgeElementCheck {

	private KnowledgeElement argument;

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(KnowledgeElement argument,
			DefinitionOfDone definitionOfDone) {
		this.argument = argument;

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
		return argument.hasNeighborOfType(KnowledgeType.DECISION)
				|| argument.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
	}

}
