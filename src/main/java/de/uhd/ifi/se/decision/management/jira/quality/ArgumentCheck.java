package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Checks whether an argument ({@link KnowledgeType#ARGUMENT},
 * {@link KnowledgeType#PRO}, or {@link KnowledgeType#CON}) fulfills the
 * {@link DefinitionOfDone}.
 */
public class ArgumentCheck extends KnowledgeElementCheck {

	public ArgumentCheck(KnowledgeElement elementToBeChecked) {
		super(elementToBeChecked);
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(DefinitionOfDone definitionOfDone) {
		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();
		qualityCheckResults.add(checkArgumentLinkedToSolutionOption(element));
		return qualityCheckResults;
	}

	private QualityCriterionCheckResult checkArgumentLinkedToSolutionOption(KnowledgeElement argument) {
		if (hasDecisionOrAlternative(argument)) {
			return new QualityCriterionCheckResult(QualityCriterionType.ARGUMENT_LINKED_TO_SOLUTION_OPTION, false);
		}
		return new QualityCriterionCheckResult(QualityCriterionType.ARGUMENT_LINKED_TO_SOLUTION_OPTION, true);
	}

	private boolean hasDecisionOrAlternative(KnowledgeElement argument) {
		return argument.hasNeighborOfType(KnowledgeType.DECISION)
				|| argument.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
	}
}