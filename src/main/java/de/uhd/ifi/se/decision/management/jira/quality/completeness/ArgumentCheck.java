package de.uhd.ifi.se.decision.management.jira.quality.completeness;

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
	public List<QualityProblem> getQualityProblems(KnowledgeElement argument, DefinitionOfDone definitionOfDone) {
		List<QualityProblem> qualityProblems = new ArrayList<>();

		if (!hasDecisionOrAlternative()) {
			qualityProblems.add(QualityProblem.ARGUMENT_DOESNT_HAVE_DECISION_OR_ALTERNATIVE);
		}

		return qualityProblems;
	}

	private boolean hasDecisionOrAlternative() {
		return argument.hasNeighborOfType(KnowledgeType.DECISION)
			|| argument.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
	}

}
