package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class ArgumentCheck implements KnowledgeElementCheck {

	private KnowledgeElement argument;

	@Override
	public boolean execute(KnowledgeElement argument) {
		this.argument = argument;
		return isCompleteAccordingToDefault();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return hasDecisionOrAlternative();
	}

	@Override
	public boolean isCompleteAccordingToSettings(DefinitionOfDone definitionOfDone) {
		return true;
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(KnowledgeElement argument,
			DefinitionOfDone definitionOfDone) {
		this.argument = argument;

		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();

		if (!hasDecisionOrAlternative()) {
			qualityCheckResults.add(new QualityCriterionCheckResult(
					QualityCriterionType.ARGUMENT_LINKED_TO_DECISION_OR_ALTERNATIVE, true));
		} else {
			qualityCheckResults.add(new QualityCriterionCheckResult(
					QualityCriterionType.ARGUMENT_LINKED_TO_DECISION_OR_ALTERNATIVE, false));

		}

		return qualityCheckResults;
	}

	private boolean hasDecisionOrAlternative() {
		return argument.hasNeighborOfType(KnowledgeType.DECISION)
				|| argument.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
	}

}
