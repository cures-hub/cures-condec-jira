package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

import java.util.ArrayList;
import java.util.List;

public class ArgumentCheck implements KnowledgeElementCheck<KnowledgeElement> {

	private KnowledgeElement argument;

	@Override
	public boolean execute(KnowledgeElement argument) {
		this.argument = argument;
		return isCompleteAccordingToDefault();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return argument.hasNeighborOfType(KnowledgeType.DECISION)
				|| argument.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		return true;
	}

	@Override
	public List<QualityProblem> getFailedCriteria(KnowledgeElement argument) {
		List<QualityProblem> failedCriteria = new ArrayList<>();

		if (!(argument.hasNeighborOfType(KnowledgeType.DECISION)
			|| argument.hasNeighborOfType(KnowledgeType.ALTERNATIVE))) {
			failedCriteria.add(QualityProblem.ARGUMENTDOESNTHAVEDECISIONORALTERNATIVE);
		}

		return failedCriteria;
	}

}
